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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * Function implementation supporting interpolation.
 */
public class PDInterpolationFunction extends PDFunction {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDFunction.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDInterpolationFunction(object);
        }
    }

    public static final COSName DK_C0 = COSName.constant("C0"); //$NON-NLS-1$

    public static final COSName DK_C1 = COSName.constant("C1"); //$NON-NLS-1$

    public static final COSName DK_N = COSName.constant("N"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private float[] c0;

    private float[] c1;

    private float n;

    protected PDInterpolationFunction(COSObject object) {
        super(object);

        COSArray cosC0;
        COSArray cosC1;

        cosC0 = cosGetDict().get(DK_C0).asArray();
        if (cosC0 == null) {
            c0 = new float[]{0.0f};
        } else {
            c0 = new float[cosC0.size()];
            for (int i = 0; i < c0.length; i++) {
                c0[i] = cosC0.get(i).asNumber().floatValue();
            }
        }

        cosC1 = cosGetDict().get(DK_C1).asArray();
        if (cosC1 == null) {
            c1 = new float[]{1.0f};
        } else {
            c1 = new float[cosC1.size()];
            for (int i = 0; i < c1.length; i++) {
                c1[i] = cosC1.get(i).asNumber().floatValue();
            }
        }

        // TODO 2 @ehk appropriate exception if null (required value according
        // to spec)
        n = cosGetDict().get(DK_N).asNumber().floatValue();
    }

    protected float[] evaluate(float value) {
        float[] result;

        if (value == 0.0) {
            return getC0();
        }
        if (value == 1.0) {
            return getC1();
        }

        // try to make faster by skipping accessor methods
        result = new float[getOutputSize()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (float) (c0[i] + (Math.pow(value, n) * (c1[i] - c0[i])));
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDFunction#evaluate(float[])
     */
    @Override
    public float[] evaluate(float[] values) {
        int outputSize;
        float[] result;

        outputSize = getOutputSize();
        result = new float[values.length * outputSize];

        for (int i = 0; i < values.length; i++) {
            System.arraycopy(evaluate(values[i]), i, result, i * outputSize, outputSize);
        }
        return result;
    }

    public float[] getC0() {
        return c0;
    }

    public float[] getC1() {
        return c1;
    }

    public float getN() {
        return n;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDFunction#getOutputSize()
     */
    @Override
    public int getOutputSize() {
        return getC0().length;
    }
}
