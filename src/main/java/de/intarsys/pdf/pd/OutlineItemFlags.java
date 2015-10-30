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

/**
 * The flags of an outline item.
 * <p>
 * The flags are bits of an integer.<br>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: show in italic
 * <li>1: show in bold
 * </ul>
 */
public class OutlineItemFlags extends AbstractBitFlags {
    public static final int Bit_Italic = 1;

    public static final int Bit_Bold = 1 << 1;

    private PDOutlineItem outlineItem;

    public OutlineItemFlags(int value) {
        super(value);
    }

    public OutlineItemFlags(PDOutlineItem outlineItem) {
        super(outlineItem, null);
        this.outlineItem = outlineItem;
    }

    protected PDOutlineItem getOutlineItem() {
        return outlineItem;
    }

    @Override
    protected int getValueInObject() {
        return getOutlineItem().basicGetFlags();
    }

    public boolean isBold() {
        return isSetAnd(Bit_Bold);
    }

    public boolean isItalic() {
        return isSetAnd(Bit_Italic);
    }

    public void setBold(boolean flag) {
        set(Bit_Bold, flag);
    }

    public void setItalic(boolean flag) {
        set(Bit_Italic, flag);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.AbstractBitFlags#setValue(int)
     */
    @Override
    protected void setValueInObject(int newValue) {
        getOutlineItem().basicSetFlags(newValue);
    }
}
