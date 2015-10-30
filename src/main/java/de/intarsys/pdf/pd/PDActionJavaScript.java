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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;

/**
 * The JavaScript action.
 * <p>
 * When executed the action executes a user defined JavaScript.
 */
public class PDActionJavaScript extends PDAction {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDAction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDActionJavaScript(object);
        }
    }

    static public final COSName CN_ActionType_JavaScript = COSName.constant("JavaScript"); //$NON-NLS-1$

    static public final COSName DK_JS = COSName.constant("JS"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    static public PDActionJavaScript createNew(String script) {
        PDActionJavaScript result = (PDActionJavaScript) PDActionJavaScript.META.createNew();
        result.setJavaScript(script);
        return result;
    }

    public static String decodeSource(COSObject cosSource) {
        if (cosSource.isNull()) {
            return null;
        }
        if (cosSource instanceof COSString) {
            return (cosSource.stringValue());
        }
        if (cosSource instanceof COSStream) {
            byte[] bytes = ((COSStream) cosSource).getDecodedBytes();
            return COSString.create(bytes).stringValue();
        }
        return null;
    }

    protected PDActionJavaScript(COSObject object) {
        super(object);
    }

    @Override
    public COSName cosGetExpectedActionType() {
        return CN_ActionType_JavaScript;
    }

    public String getJavaScript() {
        COSObject cosObject = cosGetField(DK_JS);
        return decodeSource(cosObject);
    }

    public void setJavaScript(String newJavaScript) {
        setFieldString(DK_JS, newJavaScript);
    }
}
