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
 * The flags for subimtting a form
 * <p>
 * The flags are bits of an integer.<br>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: include
 * <li>1: include no value fields
 * <li>2: export format
 * <li>3: get method
 * <li>4: submit coordinates
 * <li>5: XFDF
 * <li>6: include append saves
 * <li>7: include annotations
 * <li>8: submit PDF
 * <li>9: canonical format
 * <li>10: exclude non user annotations
 * <li>11: exclude F key
 * <li>12: not defined
 * <li>13: embed form
 * </ul>
 */
public class SubmitFormFlags extends AbstractBitFlags {
    static public final int Bit_Include = 1; // Bit position 1

    static public final int Bit_IncludeNoValueFields = 1 << 1; // Bit pos 2

    static public final int Bit_ExportFormat = 1 << 2; // Bit pos 3

    static public final int Bit_GetMethod = 1 << 3;

    static public final int Bit_SubmitCoordinates = 1 << 4;

    static public final int Bit_XFDF = 1 << 5;

    static public final int Bit_IncludeAppendSaves = 1 << 6;

    static public final int Bit_IncludeAnnotations = 1 << 7;

    static public final int Bit_SubmitPDF = 1 << 8;

    static public final int Bit_CanonicalFormat = 1 << 9;

    static public final int Bit_ExclNonUserAnnots = 1 << 10;

    static public final int Bit_ExclFKey = 1 << 11;

    static public final int Bit_NotDEFINED = 1 << 12;

    static public final int Bit_EmbedForm = 1 << 13;

    private PDActionSubmitForm submitForm;

    public SubmitFormFlags(int value) {
        super(value);
    }

    public SubmitFormFlags(PDActionSubmitForm submitForm) {
        super(submitForm, null);
        this.submitForm = submitForm;
    }

    protected PDActionSubmitForm getSubmitForm() {
        return submitForm;
    }

    @Override
    protected int getValueInObject() {
        return getSubmitForm().basicGetFlags();
    }

    public boolean isCanonicalFormat() {
        return isSetAnd(Bit_CanonicalFormat);
    }

    public boolean isEmbedForm() {
        return isSetAnd(Bit_EmbedForm);
    }

    public boolean isExclFKey() {
        return isSetAnd(Bit_ExclFKey);
    }

    public boolean isExclNonUserAnnots() {
        return isSetAnd(Bit_ExclNonUserAnnots);
    }

    public boolean isExportFormat() {
        return isSetAnd(Bit_ExportFormat);
    }

    public boolean isGetMethod() {
        return isSetAnd(Bit_GetMethod);
    }

    public boolean isInclude() {
        return isSetAnd(Bit_Include);
    }

    public boolean isIncludeAnnotations() {
        return isSetAnd(Bit_IncludeAnnotations);
    }

    public boolean isIncludeAppendSaves() {
        return isSetAnd(Bit_IncludeAppendSaves);
    }

    public boolean isIncludeNoValueFields() {
        return isSetAnd(Bit_IncludeNoValueFields);
    }

    public boolean isNotDEFINED() {
        return isSetAnd(Bit_NotDEFINED);
    }

    public boolean isSubmitCoordinates() {
        return isSetAnd(Bit_SubmitCoordinates);
    }

    public boolean isSubmitPDF() {
        return isSetAnd(Bit_SubmitPDF);
    }

    public boolean isXFDF() {
        return isSetAnd(Bit_XFDF);
    }

    public void setCanonicalFormat(boolean flag) {
        set(Bit_CanonicalFormat, flag);
    }

    public void setEmbedForm(boolean flag) {
        set(Bit_EmbedForm, flag);
    }

    public void setExclFKey(boolean flag) {
        set(Bit_ExclFKey, flag);
    }

    public void setExclNonUserAnnots(boolean flag) {
        set(Bit_ExclNonUserAnnots, flag);
    }

    public void setExportFormat(boolean flag) {
        set(Bit_ExportFormat, flag);
    }

    public void setGetMethod(boolean flag) {
        set(Bit_GetMethod, flag);
    }

    public void setInclude(boolean flag) {
        set(Bit_Include, flag);
    }

    public void setIncludeAnnotations(boolean flag) {
        set(Bit_IncludeAnnotations, flag);
    }

    public void setIncludeAppendSaves(boolean flag) {
        set(Bit_IncludeAppendSaves, flag);
    }

    public void setIncludeNoValueFields(boolean flag) {
        set(Bit_IncludeNoValueFields, flag);
    }

    public void setNotDEFINED(boolean flag) {
        set(Bit_NotDEFINED, flag);
    }

    public void setSubmitCoordinates(boolean flag) {
        set(Bit_SubmitCoordinates, flag);
    }

    public void setSubmitPDF(boolean flag) {
        set(Bit_SubmitPDF, flag);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.AbstractBitFlags#setValue(int)
     */
    @Override
    protected void setValueInObject(int newValue) {
        getSubmitForm().basicSetFlags(newValue);
    }

    public void setXFDF(boolean flag) {
        set(Bit_XFDF, flag);
    }
}
