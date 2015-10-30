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
import de.intarsys.pdf.cos.COSString;

/**
 * This object specifies details on the signature.
 * 
 */
public class PDSignatureReference extends PDObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDSignatureReference(object);
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName CN_Type_SigRef = COSName.constant("SigRef"); //$NON-NLS-1$

	public static final COSName DK_TransformMethod = COSName
			.constant("TransformMethod"); //$NON-NLS-1$

	public static final COSName DK_TransformParams = COSName
			.constant("TransformParams"); //$NON-NLS-1$

	public static final COSName DK_Data = COSName.constant("Data"); //$NON-NLS-1$

	public static final COSName DK_DigestMethod = COSName
			.constant("DigestMethod"); //$NON-NLS-1$

	public static final COSName DK_DigestValue = COSName
			.constant("DigestValue"); //$NON-NLS-1$

	public static final COSName DK_DigestLocation = COSName
			.constant("DigestLocation"); //$NON-NLS-1$

	public static final String DIGESTMETHOD_MD5 = "MD5"; //$NON-NLS-1$

	public static final String DIGESTMETHOD_SHA1 = "SHA1"; //$NON-NLS-1$

	private static final String DEFAULT_DIGESTMETHOD = DIGESTMETHOD_MD5; //$NON-NLS-1$

	protected PDSignatureReference(COSObject object) {
		super(object);
	}

	public COSObject cosGetData() {
		COSObject data = cosGetField(DK_Data);
		return data.isNull() ? null : data;
	}

	public COSArray cosGetDigestLocation() {
		return cosGetField(DK_DigestLocation).asArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_SigRef;
	}

	public void cosSetData(COSObject data) {
		cosSetField(DK_Data, data);
	}

	public String getDigestMethod() {
		COSName cosDigestMethod = cosGetField(DK_DigestMethod).asName();
		if (cosDigestMethod == null) {
			return DEFAULT_DIGESTMETHOD;
		}
		return cosDigestMethod.stringValue();
	}

	public byte[] getDigestValue() {
		COSString cosDigestValue = cosGetField(DK_DigestValue).asString();
		if (cosDigestValue == null) {
			return null;
		}
		return cosDigestValue.byteValue();
	}

	public PDTransformMethod getTransformMethod() {
		return (PDTransformMethod) PDTransformMethod.META
				.createFromCos(cosGetField(DK_TransformMethod));
	}

	public PDTransformParams getTransformParams() {
		return (PDTransformParams) PDTransformParams.META
				.createFromCos(cosGetField(DK_TransformParams));
	}

	public void setDigestMethod(String digestMethod) {
		COSName cosDigestMethod = null;
		if (digestMethod != null) {
			cosDigestMethod = COSName.create(digestMethod);
		}
		cosSetField(DK_DigestMethod, cosDigestMethod);
	}

	public void setDigestValue(byte[] digest) {
		COSString cosDigestValue = null;
		if (digest != null) {
			cosDigestValue = COSString.create(digest);
		}
		cosSetField(DK_DigestValue, cosDigestValue);
	}

	public void setTransformMethod(PDTransformMethod method) {
		COSObject cosMethod = null;
		if (method != null) {
			cosMethod = method.cosGetObject();
		}
		cosSetField(DK_TransformMethod, cosMethod);
	}

	public void setTransformParams(PDTransformParams params) {
		COSObject cosParams = null;
		if (params != null) {
			cosParams = params.cosGetObject();
		}
		cosSetField(DK_TransformParams, cosParams);
	}
}
