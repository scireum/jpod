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
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * Support for indexed color spaces.
 */
public class PDCSIndexed extends PDCSSpecial {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDCSSpecial.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        public COSBasedObject doCreateCOSBasedObjectBasic(COSObject object) {
            return new PDCSIndexed(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private PDColorSpace baseColorSpace;

    private byte[] colorBytes;

    private int colorCount;

    protected PDCSIndexed(COSObject object) {
        super(object);

        COSObject byteObject;
        baseColorSpace = (PDColorSpace) PDColorSpace.META.createFromCos(object.asArray().get(1));
        colorCount = object.asArray().get(2).asInteger().intValue() + 1;
        byteObject = object.asArray().get(3);
        if (byteObject instanceof COSStream) {
            colorBytes = ((COSStream) byteObject).getDecodedBytes();
        } else {
            colorBytes = byteObject.asString().byteValue();
        }
    }

    public PDColorSpace getBaseColorSpace() {
        return baseColorSpace;
    }

    public byte[] getColorBytes() {
        return colorBytes;
    }

    public int getColorCount() {
        return colorCount;
    }
}
