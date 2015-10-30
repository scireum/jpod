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

/**
 * Separation color spaces.
 */
public class PDCSSeparation extends PDCSSpecial {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDCSSpecial.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        public COSBasedObject doCreateCOSBasedObjectBasic(COSObject object) {
            return new PDCSSeparation(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private PDColorSpace alternate;

    private String name;

    private PDFunction tintTransform;

    protected PDCSSeparation(COSObject object) {
        super(object);

        name = cosGetArray().get(1).asName().stringValue();

        // alternateSpace and tintTransform will be resolved lazily
    }

    public PDColorSpace getAlternate() {
        if (alternate == null) {
            alternate = (PDColorSpace) PDColorSpace.META.createFromCos(cosGetArray().get(2));
        }
        return alternate;
    }

    public String getName() {
        return name;
    }

    public PDFunction getTintTransform() {
        if (tintTransform == null) {
            tintTransform = (PDFunction) PDFunction.META.createFromCos(cosGetArray().get(3));
        }
        return tintTransform;
    }
}
