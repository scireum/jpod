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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSStream;

/**
 * An object defining the shading to be used when filling a shape.
 */
public abstract class PDShading extends PDObject {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class<?> paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        public Class<?> getRootClass() {
            return PDShading.class;
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            COSDictionary cosDictionary;

            if (object instanceof COSStream) {
                // shading types 4-7
                cosDictionary = object.asStream().getDict();
            } else {
                // shading types 1-3
                cosDictionary = object.asDictionary();
            }
            int type = ((COSInteger) cosDictionary.get(DK_ShadingType)).intValue();
            switch (type) {
                case SHADING_TYPE_FUNCTIONBASED:
                    return PDFunctionBasedShading.META;
                case SHADING_TYPE_AXIAL:
                    return PDAxialShading.META;
                case SHADING_TYPE_RADIAL:
                    return PDRadialShading.META;
                case SHADING_TYPE_FREEFORM:
                    return PDFreeFormShading.META;
                case SHADING_TYPE_LATTICEFORM:
                    return PDLatticeFormShading.META;
                case SHADING_TYPE_COONS:
                    return PDCoonsShading.META;
                case SHADING_TYPE_TENSORPRODUCT:
                    return PDTensorProductShading.META;
                default:
                    object.handleException(new COSRuntimeException("unsupported shading type " + type));
                    return null;
            }
        }
    }

    private static final COSName DK_AntiAlias = COSName.constant("AntiAlias"); //$NON-NLS-1$

    private static final COSName DK_BBox = COSName.constant("BBox"); //$NON-NLS-1$

    private static final COSName DK_ColorSpace = COSName.constant("ColorSpace"); //$NON-NLS-1$

    protected static final COSName DK_ShadingType = COSName.constant("ShadingType"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final int SHADING_TYPE_AXIAL = 2;

    public static final int SHADING_TYPE_COONS = 6;

    public static final int SHADING_TYPE_FREEFORM = 4;

    public static final int SHADING_TYPE_FUNCTIONBASED = 1;

    public static final int SHADING_TYPE_LATTICEFORM = 5;

    public static final int SHADING_TYPE_RADIAL = 3;

    public static final int SHADING_TYPE_TENSORPRODUCT = 7;

    private boolean antiAlias;

    private CDSRectangle boundingBox;

    private PDColorSpace colorSpace;

    protected PDShading(COSObject object) {
        super(object);

        COSDictionary cosDictionary;
        COSObject cosAntiAlias;
        COSObject cosBBox;

        if (object instanceof COSStream) {
            // shading types 4-7
            cosDictionary = object.asStream().getDict();
        } else {
            // shading types 1-3
            cosDictionary = object.asDictionary();
        }
        cosAntiAlias = cosDictionary.get(DK_AntiAlias);
        if (cosAntiAlias.isNull()) {
            antiAlias = false;
        } else {
            antiAlias = cosAntiAlias.asBoolean().booleanValue();
        }

        cosBBox = cosDictionary.get(DK_BBox);
        if (cosBBox.isNull()) {
            boundingBox = null;
        } else {
            boundingBox = CDSRectangle.createFromCOS(cosBBox.asArray());
        }

        // color space will be resolved lazily
    }

    public CDSRectangle getBoundingBox() {
        return boundingBox;
    }

    public PDColorSpace getColorSpace() {
        if (colorSpace == null) {
            COSDictionary cosDictionary;

            if (cosGetObject() instanceof COSStream) {
                // shading types 4-7
                cosDictionary = cosGetObject().asStream().getDict();
            } else {
                // shading types 1-3
                cosDictionary = cosGetObject().asDictionary();
            }
            colorSpace = (PDColorSpace) PDColorSpace.META.createFromCos(cosDictionary.get(DK_ColorSpace));
        }
        return colorSpace;
    }

    public abstract int getShadingType();

    public boolean isAntiAlias() {
        return antiAlias;
    }
}
