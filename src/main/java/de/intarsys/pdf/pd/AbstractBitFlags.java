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

/**
 * AbstractBitFlags provides access to an integer containing bit wise flags.
 * <p>
 * The concrete value is either provided by an associated PDF object along with
 * the access method to the value within the object or by an independent integer
 * value itself.
 */
public abstract class AbstractBitFlags {

    private int value;

    private COSBasedObject object;

    private COSName field;

    protected AbstractBitFlags(COSBasedObject object, COSName field) {
        super();
        this.object = object;
        this.field = field;
    }

    protected AbstractBitFlags(int value) {
        super();
        this.value = value;
    }

    /**
     * By implementing this method the subclass provides the integer which
     * contains the bit flags.
     *
     * @return the integer containing the bit flags.
     */
    public final int getValue() {
        if (object == null) {
            return value;
        } else {
            return getValueInObject();
        }
    }

    protected int getValueInObject() {
        return object.getFieldInt(field, 0);
    }

    /**
     * Checks if all the bits set in the bit mask are also set in the underlying
     * integer. A clear bit == 0 and a set bit == 1.
     *
     * @param bitMask a integer containing the bit mask to test
     * @return true if all bits in the mask are set in the underlying integer
     */
    public boolean isSetAnd(int bitMask) {
        return (getValue() & bitMask) == bitMask;
    }

    /**
     * Checks if one of the bits set in the bit mask are also set in the
     * underlying integer. A clear bit == 0 and a set bit == 1.
     *
     * @param bitMask a integer containing the bit mask
     * @return true if one of the bits in the mask are set in the underlying
     * integer
     */
    public boolean isSetOr(int bitMask) {
        return (getValue() & bitMask) != 0;
    }

    /**
     * All bits in the underlying integer masked by the bit mask are set
     * acording to the flag. If the flag is true, the bits are set to 1
     * otherwise the bits are set to 0.
     *
     * @param bitMask
     * @param flag
     */
    public void set(int bitMask, boolean flag) {
        if (flag) {
            setValue(getValue() | bitMask);
        } else {
            setValue(getValue() & (~bitMask));
        }
    }

    /**
     * This method is used to write back the changes made to the bit flags.
     *
     * @param newValue the whole integer containing all bit flags
     */
    public final void setValue(int newValue) {
        if (object == null) {
            value = newValue;
        } else {
            setValueInObject(newValue);
        }
    }

    protected void setValueInObject(int newValue) {
        object.setFieldInt(field, newValue);
    }
}
