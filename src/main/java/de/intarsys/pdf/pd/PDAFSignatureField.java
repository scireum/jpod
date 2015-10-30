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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.util.HashSet;
import java.util.Iterator;

/**
 * A logical signature field within an AcroForm.
 */
public class PDAFSignatureField extends PDAcroFormField {
    /*
     * protected PDSignatureLock cosGetLock() { COSObject cosObject =
     * cosGetField(DK_Lock); if (cosObject != null) { return (PDSignatureLock)
     * PDSignatureLock.createFromCos( this, (COSDictionary) cosObject ); }
     * return null; }
     *
     * protected PDSeedValue cosGetSeedValue() { COSObject cosObject =
     * cosGetField(DK_SV); if (cosObject != null) { return (PDSeedValue)
     * PDSeedValue.createFromCos( this, (COSDictionary) cosObject ); } return
     * null; }
     *
     * protected void cosSetLock(PDSignatureLock newLock) { if (newLock != null) {
     * cosSetField(DK_Lock, newLock.cosSerialize()); } else {
     * cosSetField(DK_Lock, null); } }
     *
     * protected void cosSetSeedValue(PDSeedValue newSeedValue) { if
     * (newSeedValue != null) { cosSetField(DK_SV, newSeedValue.cosSerialize()); }
     * else { cosSetField(DK_SV, null); } }
     */
    public static class MetaClass extends PDAcroFormField.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDAFSignatureField(object);
        }
    }

    public static final COSName DK_Lock = COSName.constant("Lock"); //$NON-NLS-1$

    public static final COSName DK_SV = COSName.constant("SV"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private PDSignature cachedSignature;

    protected PDAFSignatureField(COSObject object) {
        super(object);
    }

    public void clearSignature() {
        COSDictionary perms = getDoc().cosGetPermissionsDict();
        if (perms != null) {
            for (Iterator i = new HashSet(perms.keySet()).iterator(); i.hasNext(); ) {
                COSName key = (COSName) i.next();
                COSObject value = perms.get(key);
                if (value == cosGetValue()) {
                    perms.remove(key);
                }
            }
        }
        //
        setSignature(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAcroFormField#cosGetExpectedFieldType()
     */
    @Override
    public COSName cosGetExpectedFieldType() {
        return CN_FT_Sig;
    }

    @Override
    public COSObject cosSetValue(COSObject newValue) {
        COSObject result = super.cosSetValue(newValue);
        AcroFormSigFlags sigFlags = getAcroForm().getSigFlags();
        sigFlags.setAppendOnly(getAcroForm().isSigned());
        return result;
    }

    public PDSignatureLock getLock() {
        return (PDSignatureLock) PDSignatureLock.META.createFromCos(cosGetField(DK_Lock));
    }

    public PDSignatureSeedValue getSeedValue() {
        return (PDSignatureSeedValue) PDSignatureSeedValue.META.createFromCos(cosGetField(DK_SV));
    }

    /**
     * The associated {@link PDSignature} if available.
     *
     * @return The associated {@link PDSignature} if available.
     */
    public PDSignature getSignature() {
        if (cachedSignature == null) {
            cachedSignature = (PDSignature) PDSignature.META.createFromCos(cosGetValue());
            if (cachedSignature != null) {
                cachedSignature.setAcroFormField(this);
            }
        }
        return cachedSignature;
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedSignature = null;
    }

    /**
     * {@code true} if this field is already signed.
     *
     * @return {@code true} if this field is already signed.
     */
    public boolean isSigned() {
        return !cosGetValue().isNull();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAcroFormField#isTypeSig()
     */
    @Override
    public boolean isTypeSig() {
        return true;
    }

    public void setLock(PDSignatureLock lock) {
        setFieldObject(DK_Lock, lock);
    }

    public void setSeedValue(PDSignatureSeedValue value) {
        setFieldObject(DK_SV, value);
    }

    /**
     * Assign a new signature value.
     *
     * @param newSignature The new signature value.
     */
    public void setSignature(PDSignature newSignature) {
        if (cachedSignature != null) {
            cachedSignature.setAcroFormField(null);
        }
        cachedSignature = newSignature;

        if (newSignature != null) {
            newSignature.setAcroFormField(this);
            cosSetValue(newSignature.cosGetObject());
        } else {
            cosSetValue(null);
        }
    }
}
