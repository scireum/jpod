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
package de.intarsys.pdf.cos;

/**
 * Represents floating point numbers in pdf.
 */
public class COSFixed extends COSNumber {
    static public final int DEFAULT_PRECISION = 5;

    static public COSFixed create(byte[] bytes, int start, int length) {
        long result = 0;
        long decimal = 1;
        int end = start + length;
        boolean negative = false;
        boolean point = false;
        int precision = 0;
        int i = start;
        byte prefix = bytes[i];
        if (prefix == '+') {
            i++;
        } else if (prefix == '-') {
            negative = true;
            i++;
        }
        for (; i < end; i++) {
            byte digit = bytes[i];
            if (digit == '.') {
                point = true;
            } else {
                result = ((result * 10) + digit) - '0';
                if (point) {
                    decimal = decimal * 10;
                    precision++;
                }
            }
        }
        if (negative) {
            return new COSFixed(-(float) ((double) result / (double) decimal), precision);
        }
        return new COSFixed((float) ((double) result / (double) decimal), precision);
    }

    static public COSFixed create(double value) {
        return new COSFixed((float) value, DEFAULT_PRECISION);
    }

    static public COSFixed create(double value, int precision) {
        return new COSFixed((float) value, precision);
    }

    static public COSFixed create(float value) {
        return new COSFixed(value, DEFAULT_PRECISION);
    }

    static public COSFixed create(float value, int precision) {
        return new COSFixed(value, precision);
    }

    private final float floatValue;

    private byte precision;

    protected COSFixed(float value, int precision) {
        this.floatValue = value;
        this.precision = (byte) precision;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor)
     */
    @Override
    public java.lang.Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        return visitor.visitFromFixed(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#asFixed()
     */
    @Override
    public COSFixed asFixed() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        return String.valueOf(floatValue);
    }

    @Override
    protected COSObject copyBasic() {
        return new COSFixed(floatValue, precision);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof COSFixed)) {
            return false;
        }
        return floatValue == ((COSFixed) o).floatValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSNumber#floatValue()
     */
    @Override
    public float floatValue() {
        return floatValue;
    }

    /**
     * The precision (digits after period) for this.
     *
     * @return The precision (digits after period) for this.
     */
    public int getPrecision() {
        return precision;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(floatValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSNumber#intValue()
     */
    @Override
    public int intValue() {
        return (int) floatValue;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#restoreState(java.lang.Object)
     */
    @Override
    public void restoreState(Object object) {
        super.restoreState(object);
        COSFixed fixed = (COSFixed) object;
        this.precision = fixed.precision;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    public Object saveState() {
        COSFixed result = new COSFixed(floatValue, precision);
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * Assign the precision for this.
     *
     * @param precision The new precision.
     */
    public void setPrecision(int precision) {
        this.precision = (byte) precision;
    }
}
