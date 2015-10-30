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
 * The flags of a signature field.
 * <p>
 * The flags are bits of an integer.<br>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: default
 * <li>1: SignatureExists
 * <li>2: AppendOnly
 * </ul>
 */
public class AcroFormSigFlags extends AbstractBitFlags {
    public static int Bit_SignatureExists = 1; // Bit position 1

    public static int Bit_AppendOnly = 1 << 1; // Bit position 2

    private PDAcroForm acroForm;

    public AcroFormSigFlags(int value) {
        super(value);
    }

    public AcroFormSigFlags(PDAcroForm form) {
        super(form, null);
        acroForm = form;
    }

    protected PDAcroForm getAcroForm() {
        return acroForm;
    }

    @Override
    protected int getValueInObject() {
        return getAcroForm().getFieldInt(PDAcroForm.DK_SigFlags, 0);
    }

    /**
     * @return appendOnly flag
     * @see de.intarsys.pdf.pd.AcroFormSigFlags#setAppendOnly(boolean)
     */
    public boolean isAppendOnly() {
        return isSetAnd(Bit_AppendOnly);
    }

    /**
     * @return signatureExists flag
     * @see de.intarsys.pdf.pd.AcroFormSigFlags#setSignatureExists(boolean)
     */
    public boolean isSignatureExists() {
        return isSetAnd(Bit_SignatureExists);
    }

    /**
     * excerpt from PDF 1.7 spec (p. 674):
     * <p>
     * If set, the document contains signatures that may be invalidated if the
     * file is saved (written) in a way that alters its previous contents, as
     * opposed to an incremental update. Merely updating the file by appending
     * new information to the end of the previous version is safe (see Section
     * G.6, Updating Example). Viewer applications can use this flag to
     * present a user requesting a full save with an additional alert box
     * warning that signatures will be invalidated and requiring explicit
     * confirmation before continuing with the operation.
     * <p>
     *
     * @param appendOnly
     */
    public void setAppendOnly(boolean appendOnly) {
        set(Bit_AppendOnly, appendOnly);
    }

    /**
     * excerpt from PDF 1.7 spec (p. 674):
     * <p>
     * If set, the document contains at least one signature field. This flag
     * allows a viewer application to enable user interface items (such as menu
     * items or pushbuttons) related to signature processing without having to
     * scan the entire document for the presence of signature fields.
     * </p>
     *
     * @param signatureExists
     */
    public void setSignatureExists(boolean signatureExists) {
        set(Bit_SignatureExists, signatureExists);
    }

    @Override
    protected void setValueInObject(int newValue) {
        if (newValue != 0) { // default
            getAcroForm().setFieldInt(PDAcroForm.DK_SigFlags, newValue);
        } else {
            getAcroForm().cosRemoveField(PDAcroForm.DK_SigFlags);
        }
    }
}
