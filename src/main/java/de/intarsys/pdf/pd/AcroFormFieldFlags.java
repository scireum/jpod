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

import java.util.Iterator;

/**
 * The flags of a field in an acrobat form.
 * <p>
 * The flags are bits of an integer.<br>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: default
 * <li>1: Readonly
 * <li>2: Required
 * <li>3: NoExport
 * <li>13: Multiline
 * <li>14: Password
 * <li>15: NoToggleToOff
 * <li>16: Radio
 * <li>17: Pushbutton
 * <li>18: Combo
 * <li>19: Edit
 * <li>20: Sort
 * <li>21: FileSelect
 * <li>22: MulitSelect
 * <li>23: DoNotSpellChec
 * <li>24: DoNotScroll
 * <li>25: Comb
 * <li>26: RadiosInUnison
 * <li>27: CommitOnSelChange
 * <li>28: RichText
 * </ul>
 */
public class AcroFormFieldFlags extends AbstractBitFlags {
    final public static int Bit_ReadOnly = 1; // Bit position 1

    final public static int Bit_Required = 1 << 1; // Bit position 2

    final public static int Bit_NoExport = 1 << 2; // Bit position 3

    final public static int Bit_Multiline = 1 << 12;

    final public static int Bit_Password = 1 << 13;

    final public static int Bit_NoToggleToOff = 1 << 14;

    final public static int Bit_Radio = 1 << 15;

    final public static int Bit_Pushbutton = 1 << 16;

    final public static int Bit_Combo = 1 << 17;

    final public static int Bit_Edit = 1 << 18;

    final public static int Bit_Sort = 1 << 19;

    final public static int Bit_FileSelect = 1 << 20;

    final public static int Bit_MultiSelect = 1 << 21;

    final public static int Bit_DoNotSpellCheck = 1 << 22;

    final public static int Bit_DoNotScroll = 1 << 23;

    final public static int Bit_Comb = 1 << 24;

    final public static int Bit_RadiosInUnison = 1 << 25;

    final public static int Bit_CommitOnSelChange = 1 << 26;

    final public static int Bit_RichText = 1 << 27;

    private PDAcroFormField field;

    public AcroFormFieldFlags(int value) {
        super(value);
    }

    public AcroFormFieldFlags(PDAcroFormField field) {
        super(field, null);
        this.field = field;
    }

    protected PDAcroFormField getField() {
        return field;
    }

    @Override
    protected int getValueInObject() {
        return getField().basicGetFieldFlags();
    }

    public boolean isComb() {
        return isSetAnd(Bit_Comb);
    }

    public boolean isCombo() {
        return isSetAnd(Bit_Combo);
    }

    public boolean isCommitOnSelChange() {
        return isSetAnd(Bit_CommitOnSelChange);
    }

    public boolean isDoNotScroll() {
        return isSetAnd(Bit_DoNotScroll);
    }

    public boolean isEdit() {
        return isSetAnd(Bit_Edit);
    }

    public boolean isFileSelect() {
        return isSetAnd(Bit_FileSelect);
    }

    public boolean isMultiline() {
        return isSetAnd(Bit_Multiline);
    }

    public boolean isMultiSelect() {
        return isSetAnd(Bit_MultiSelect);
    }

    public boolean isNoExport() {
        return isSetAnd(Bit_NoExport);
    }

    public boolean isNoToggleOff() {
        return isSetAnd(Bit_NoToggleToOff);
    }

    public boolean isPassword() {
        return isSetAnd(Bit_Password);
    }

    public boolean isPushbutton() {
        return isSetAnd(Bit_Pushbutton);
    }

    public boolean isRadio() {
        return isSetAnd(Bit_Radio);
    }

    public boolean isRadiosInUnison() {
        return isSetAnd(Bit_RadiosInUnison);
    }

    public boolean isReadOnly() {
        return isSetAnd(Bit_ReadOnly);
    }

    public boolean isRequired() {
        return isSetAnd(Bit_Required);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.AbstractBitFlags#set(int, boolean)
     */
    @Override
    public void set(int bitMask, boolean flag) {
        super.set(bitMask, flag);
        if (getField() != null) {
            if (getField().getKids() != null) {
                for (Iterator i = getField().getKids().iterator(); i.hasNext(); ) {
                    PDAcroFormField kid = (PDAcroFormField) i.next();
                    kid.getFieldFlags().set(bitMask, flag);
                }
            }
        }
    }

    public void setComb(boolean f) {
        set(Bit_Comb, f);
    }

    public void setCombo(boolean f) {
        set(Bit_Combo, f);
    }

    public void setCommitOnSelChange(boolean f) {
        set(Bit_CommitOnSelChange, f);
    }

    public void setDoNotScroll(boolean f) {
        set(Bit_DoNotScroll, f);
    }

    public void setEdit(boolean edit) {
        set(Bit_Edit, edit);
    }

    public void setFileSelect(boolean f) {
        set(Bit_FileSelect, f);
    }

    public void setMultiline(boolean f) {
        set(Bit_Multiline, f);
    }

    public void setMultiSelect(boolean multiSelect) {
        set(Bit_MultiSelect, multiSelect);
    }

    public void setNoExport(boolean f) {
        set(Bit_NoExport, f);
    }

    public void setNoToggleToOff(boolean noToggleToOff) {
        set(Bit_NoToggleToOff, noToggleToOff);
    }

    public void setPassword(boolean f) {
        set(Bit_Password, f);
    }

    public void setPushbutton(boolean f) {
        set(Bit_Pushbutton, f);
    }

    public void setRadio(boolean f) {
        set(Bit_Radio, f);
    }

    public void setReadOnly(boolean f) {
        set(Bit_ReadOnly, f);
    }

    public void setRequired(boolean f) {
        set(Bit_Required, f);
    }

    @Override
    protected void setValueInObject(int newValue) {
        getField().basicSetFieldFlags(newValue);
    }
}
