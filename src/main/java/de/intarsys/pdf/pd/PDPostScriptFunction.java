/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.encoding.PDFDocEncoding;
import de.intarsys.pdf.postscript.Handler;
import de.intarsys.pdf.postscript.ParseException;
import de.intarsys.pdf.postscript.Parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A function implementation based on a "mini" postscript interpreter.
 */
public class PDPostScriptFunction extends PDFunction {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDFunction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDPostScriptFunction(object);
        }

        @Override
        protected COSObject doCreateCOSObject() {
            return COSStream.create(null);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private String code;

    protected PDPostScriptFunction(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#cosGetDict()
     */
    @Override
    public COSDictionary cosGetDict() {
        return cosGetStream().getDict();
    }

    @Override
    public float[] evaluate(float[] values) {
        Handler handler;
        List argList;
        List resultList;
        float[] result;

        handler = new Handler();
        argList = new ArrayList(values.length);
        for (int index = 0; index < values.length; index++) {
            argList.add(new Double(values[index]));
        }
        handler.pushArgs(argList);
        try {
            new Parser(new StringReader(getCode())).parse(handler);
        } catch (ParseException ex) {
            // TODO warning?
            return dummyResult();
        } catch (UnsupportedOperationException ex) {
            // postscript is only partially implemented; do the same as when it
            // wasn't implemented at all
            return dummyResult();
        }
        resultList = handler.popResult();

        result = new float[getOutputSize()];
        for (int index = 0; index < result.length; index++) {
            result[index] = ((Number) resultList.get(index)).floatValue();
        }
        return result;
    }

    public String getCode() {
        if (code == null) {
            byte[] bytes;
            String string;

            bytes = cosGetStream().getDecodedBytes();
            string = PDFDocEncoding.UNIQUE.decode(bytes);
            int start = string.indexOf('{');
            int stop = string.lastIndexOf('}');
            if (start != -1 && stop != -1) {
                code = string.substring(start + 1, stop);
            } else {
                code = string;
            }
        }
        return code;
    }

    @Override
    public int getOutputSize() {
        return getRange().size() / 2;
    }
}
