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
package de.intarsys.pdf.crypt;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * The definition of the document encryption.
 * 
 * <p>
 * This object contains the state information defining the context for the
 * document encryption.
 * </p>
 */
public class COSEncryption extends COSBasedObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends COSBasedObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new COSEncryption(object);
		}
	}

	public static final COSName DK_EncryptMetadata = COSName
			.constant("EncryptMetadata"); //$NON-NLS-1$

	public static final COSName DK_Filter = COSName.constant("Filter"); //$NON-NLS-1$

	public static final COSName DK_SubFilter = COSName.constant("SubFilter"); //$NON-NLS-1$

	public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$

	public static final COSName DK_Length = COSName.constant("Length"); //$NON-NLS-1$

	public static final COSName DK_CF = COSName.constant("CF"); //$NON-NLS-1$

	public static final COSName CN_IDENTITY = COSName.constant("Identity"); //$NON-NLS-1$

	public static final COSName DK_StmF = COSName.constant("StmF"); //$NON-NLS-1$

	public static final COSName DK_EFF = COSName.constant("EEF"); //$NON-NLS-1$

	public static final COSName DK_StrF = COSName.constant("StrF"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected COSEncryption(COSObject object) {
		super(object);
	}

	public COSDictionary getCryptFilterDict(COSName name) {
		if (CN_IDENTITY.equals(name)) {
			// can't redefine...
			return null;
		}
		COSDictionary cryptFilters = cosGetField(COSEncryption.DK_CF)
				.asDictionary();
		if (cryptFilters == null) {
			return null;
		}
		return cryptFilters.get(name).asDictionary();
	}

	public COSName getCryptFilterNameFile() {
		COSName name = cosGetField(DK_EFF).asName();
		if (name == null) {
			return CN_IDENTITY;
		}
		return name;
	}

	public COSName getCryptFilterNameStream() {
		COSName name = cosGetField(DK_StmF).asName();
		if (name == null) {
			return CN_IDENTITY;
		}
		return name;
	}

	public COSName getCryptFilterNameString() {
		COSName name = cosGetField(DK_StrF).asName();
		if (name == null) {
			return CN_IDENTITY;
		}
		return name;
	}

	public COSName getFilter() {
		return cosGetField(DK_Filter).asName();
	}

	/**
	 * The length of the encryption key, in bits. The value must be a multiple
	 * of 8, in the range 40 to 128. Default value: 40.
	 * 
	 * @return length of the encryption key, in bits
	 */
	public int getLength() {
		return getFieldInt(DK_Length, 40);
	}

	public int getVersion() {
		return getFieldInt(DK_V, 0);
	}

}
