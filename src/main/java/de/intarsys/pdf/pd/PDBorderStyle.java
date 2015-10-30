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

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * The definition of a border style for an annotation.
 */
public class PDBorderStyle extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDBorderStyle(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * The width type.
     */
    public static final COSName DK_W = COSName.constant("W");

    /**
     * The Style type.
     */
    public static final COSName DK_S = COSName.constant("S");

    /**
     * Style S: solid
     */
    public static final COSName CN_S_S = COSName.constant("S");

    /**
     * Style D: dashed
     */
    public static final COSName CN_S_D = COSName.constant("D");

    /**
     * Style B: beveled
     */
    public static final COSName CN_S_B = COSName.constant("B");

    /**
     * Style I: Inset
     */
    public static final COSName CN_S_I = COSName.constant("I");

    /**
     * Style U: underlined
     */
    public static final COSName CN_S_U = COSName.constant("U");

    /**
     * The DashArray type.
     */
    public static final COSName DK_D = COSName.constant("D"); //

    /**
     * The border type name
     */
    public static final COSName CN_Type_Border = COSName.constant("Border"); //

    protected PDBorderStyle(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
     */
    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_Border;
    }

    public int[] getDashArray() {
        COSArray array = cosGetField(DK_D).asArray();
        if (array != null) {
            int[] result = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                COSInteger value = array.get(i).asInteger();
                if (value != null) {
                    result[i] = value.intValue();
                } else {
                    // TODO 3 wrong default, maybe restrict
                    result[i] = 0;
                }
            }
            return result;
        }
        return new int[]{3}; // default
    }

    public COSName getStyle() {
        COSName style = cosGetField(DK_S).asName();
        if (style != null) {
            return style;
        }
        return CN_S_S;
    }

    public float getWidth() {
        return getFieldFixed(DK_W, 1);
    }

    public void setDashArray(int[] newDashArray) {
        if ((newDashArray == null) || ((newDashArray.length == 1) && (newDashArray[0] == 3))) {
            cosRemoveField(DK_D);
            return;
        }

        COSArray a = COSArray.create(newDashArray.length);
        cosSetField(DK_D, a); // overwrite existing array

        for (int i = 0; i < newDashArray.length; i++) {
            a.add(COSInteger.create(newDashArray[i]));
        }
    }

    public void setStyle(COSName newStyle) {
        cosSetField(DK_S, newStyle);
    }

    /**
     * Set the style. If newWidth = 0, no border is drawn.
     *
     * @param newWidth The new width.
     */
    public void setWidth(float newWidth) {
        if (newWidth != 1) {
            setFieldFixed(DK_W, newWidth);
        } else {
            cosRemoveField(DK_W);
        }
    }
}
