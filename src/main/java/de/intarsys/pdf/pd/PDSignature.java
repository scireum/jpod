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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectProxy;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.PDFParser;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.stream.StreamTools;

/**
 * This class represents the signature object referenced for example in an
 * AcroForm signature field.
 */
public class PDSignature extends PDObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDSignature(object);
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName CN_Type_Sig = COSName.constant("Sig"); //$NON-NLS-1$

	public static final COSName DK_Filter = COSName.constant("Filter"); //$NON-NLS-1$

	public static final COSName DK_SubFilter = COSName.constant("SubFilter"); //$NON-NLS-1$

	public static final COSName DK_Contents = COSName.constant("Contents"); //$NON-NLS-1$
	public static final COSName DK_Cert = COSName.constant("Cert"); //$NON-NLS-1$

	public static final COSName DK_ByteRange = COSName.constant("ByteRange"); //$NON-NLS-1$

	public static final COSName DK_Reference = COSName.constant("Reference"); //$NON-NLS-1$

	public static final COSName DK_Changes = COSName.constant("Changes"); //$NON-NLS-1$

	public static final COSName DK_Name = COSName.constant("Name"); //$NON-NLS-1$

	public static final COSName DK_M = COSName.constant("M"); //$NON-NLS-1$

	public static final COSName DK_Location = COSName.constant("Location"); //$NON-NLS-1$

	public static final COSName DK_Reason = COSName.constant("Reason"); //$NON-NLS-1$

	public static final COSName DK_ContactInfo = COSName
			.constant("ContactInfo"); //$NON-NLS-1$

	public static final COSName DK_R = COSName.constant("R"); //$NON-NLS-1$

	public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$

	public static final COSName DK_Prop_Build = COSName.constant("Prop_Build"); //$NON-NLS-1$

	public static final COSName DK_Prop_AuthTime = COSName
			.constant("Prop_Auth_Time"); //$NON-NLS-1$

	public static final COSName DK_Prop_AuthType = COSName
			.constant("Prop_AuthType"); //$NON-NLS-1$

	private PDAFSignatureField acroFormField;

	private List cachedReferences;

	protected PDSignature(COSObject object) {
		super(object);
	}

	/**
	 * @return the byte range for the signature or null
	 */
	public COSArray cosGetByteRange() {
		return cosGetField(DK_ByteRange).asArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Sig;
	}

	public PDAFSignatureField getAcroFormField() {
		return acroFormField;
	}

	public PDBuildProperties getBuildProperties() {
		return (PDBuildProperties) PDBuildProperties.META
				.createFromCos(cosGetField(DK_Prop_Build));
	}

	/**
	 * <p>
	 * Get a List of certificates, the first one is the certificate of the
	 * signer himself. Followed by certificates of certificate authoritys.
	 * </p>
	 * <p>
	 * A certificate is stored as DER encoded byte[]
	 * </p>
	 * 
	 * @return a List of certificates or null
	 */
	public List getCert() {
		List result = null;
		COSObject cert = cosGetField(DK_Cert);
		if (!cert.isNull()) {
			result = new ArrayList();
			if (cert instanceof COSString) {
				result.add(((COSString) cert).byteValue());
				return result;
			}
			if (cert instanceof COSArray) {
				COSArray certArray = (COSArray) cert;
				for (Iterator i = certArray.iterator(); i.hasNext();) {
					COSString value = ((COSObject) i.next()).asString();
					if (value != null) {
						result.add(value.byteValue());
					}
				}
			}
		}
		return result;
	}

	/**
	 * @return how to contact the signer or null
	 */
	public String getContactInfo() {
		return getFieldString(DK_ContactInfo, null);
	}

	/**
	 * @return content of '/Contents' field or an empty byte array if the
	 *         content could not be read
	 */
	public byte[] getContentBytes() {
		/*
		 * Upon writing, /Contents are handled by a COSObjectProxy and inserted
		 * after serializing the document. The data is not processed by the
		 * standard COSWriter, especially it is not encrypted even if the
		 * document has a security handler. <br> <p> Now, reading the /Content
		 * is done the same way to circumvent any context specific processing
		 * (especially decrypting).
		 */
		byte[] contentBytes = null;
		COSDocumentElement content = cosGetDict().basicGet(DK_Contents);
		if (content instanceof COSString) {
			if (getDoc().isEncrypted()) {
				// read from plain input
				contentBytes = getContentBytesPlain();
			} else {
				contentBytes = ((COSString) content).byteValue();
			}
		} else if (content instanceof COSObjectProxy) {
			COSString string = content.dereference().asString();
			if (string != null) {
				contentBytes = string.byteValue();
			}
		}
		if (contentBytes == null) {
			contentBytes = new byte[0];
		}
		return contentBytes;
	}

	protected byte[] getContentBytesPlain() {
		COSArray cosByteRange = cosGetByteRange();
		if (cosByteRange.size() != 4) {
			return null;
		}
		IRandomAccess ra = null;
		try {
			int startSigValue = cosByteRange.get(1).asInteger().intValue();
			ra = getDoc().getLocator().getRandomAccess();
			ra.seek(startSigValue);
			PDFParser parser = new COSDocumentParser(null);
			Object object = parser.parseElement(ra);
			if (object instanceof COSString) {
				return ((COSString) object).byteValue();
			}
		} catch (Exception e) {
			//
		} finally {
			StreamTools.close(ra);
		}
		return null;
	}

	/**
	 * @return the date the signature took place
	 */
	public CDSDate getDate() {
		return CDSDate.createFromCOS(cosGetField(DK_M).asString());
	}

	/**
	 * Filter is a name for the original signature creator, for example:
	 * Adobe.PPKLite
	 * 
	 * @return name of the signature creator
	 */
	public COSName getFilter() {
		return cosGetField(DK_Filter).asName();
	}

	/**
	 * @return where the document was signed or null
	 */
	public String getLocation() {
		return getFieldString(DK_Location, null);
	}

	/**
	 * @return name of the signer or null
	 */
	public String getName() {
		return getFieldString(DK_Name, null);
	}

	/**
	 * @return reason for signing this document or null
	 */
	public String getReason() {
		return getFieldString(DK_Reason, null);
	}

	public List getSignatureReferences() {
		if (cachedReferences == null) {
			cachedReferences = getPDObjects(DK_Reference,
					PDSignatureReference.META, true);
		}
		return cachedReferences;
	}

	/**
	 * SubFilter is the name of a encoding and storage algorithm.
	 * 
	 * @return the name of the encoding algorithm
	 */
	public COSName getSubFilter() {
		return cosGetField(DK_SubFilter).asName();
	}

	@Override
	public void invalidateCaches() {
		cachedReferences = null;
		super.invalidateCaches();
	}

	public void setAcroFormField(PDAFSignatureField acroFormField) {
		this.acroFormField = acroFormField;
	}

	public void setBuildProperties(PDBuildProperties buildProperties) {
		setFieldObject(DK_Prop_Build, buildProperties);
	}

	/**
	 * Sets certificates in the /Cert field.
	 * 
	 * @param certificate
	 *            a DER encoded byte[]
	 */
	public void setCert(byte[] certificate) {
		if (certificate == null) {
			return;
		}
		COSString certString = COSString.createHex(certificate);
		cosSetField(DK_Cert, certString);
	}

	/**
	 * Sets certificates in the /Cert field.
	 * 
	 * @param certificates
	 *            a list of DER encoded byte[]
	 */
	public void setCert(List certificates) {
		if ((certificates == null) || certificates.isEmpty()) {
			return;
		}
		if (certificates.size() == 1) {
			setCert((byte[]) certificates.get(0));
			return;
		}
		COSArray certList = COSArray.create(certificates.size());
		for (Iterator i = certificates.iterator(); i.hasNext();) {
			COSString certString = COSString.createHex((byte[]) i.next());
			certList.add(certString);
		}
		cosSetField(DK_Cert, certList);
	}

	/**
	 * @param contactInfo
	 *            how to contact the signer, may be null
	 */
	public void setContactInfo(String contactInfo) {
		setFieldString(DK_ContactInfo, contactInfo);
	}

	public void setDate(CDSDate date) {
		setFieldObject(DK_M, date);
	}

	/**
	 * Set the name of the signature creator, for example: Adobe.PPKLite
	 * 
	 * @param filter
	 *            name of the signature creator
	 */
	public void setFilter(COSName filter) {
		cosSetField(DK_Filter, filter);
	}

	/**
	 * @param location
	 *            location the signer signed the document, may be null
	 */
	public void setLocation(String location) {
		setFieldString(DK_Location, location);
	}

	/**
	 * @param name
	 *            name of the signer, may be null
	 */
	public void setName(String name) {
		setFieldString(DK_Name, name);
	}

	/**
	 * @param reason
	 *            reason why is document was signed, may be null
	 */
	public void setReason(String reason) {
		setFieldString(DK_Reason, reason);
	}

	public void setSignatureReferences(List signatureReferences) {
		setPDObjects(DK_Reference, signatureReferences);
		cachedReferences = signatureReferences;
	}

	/**
	 * Set the name of the encoding algorithm
	 * 
	 * @param subfilter
	 *            name of the encoding algorithm
	 */
	public void setSubFilter(COSName subfilter) {
		cosSetField(DK_SubFilter, subfilter);
	}
}
