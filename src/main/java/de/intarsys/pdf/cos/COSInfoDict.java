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

import de.intarsys.pdf.cds.CDSDate;

/**
 * The document information dictionary..
 */
public class COSInfoDict extends COSBasedObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends COSBasedObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new COSInfoDict(object);
		}
	}

	/** Well known attributes */
	public static final COSName DK_CreationDate = COSName
			.constant("CreationDate"); //$NON-NLS-1$

	public static final COSName DK_Creator = COSName.constant("Creator"); //$NON-NLS-1$

	public static final COSName DK_ModDate = COSName.constant("ModDate"); //$NON-NLS-1$

	public static final COSName DK_Producer = COSName.constant("Producer"); //$NON-NLS-1$

	public static final COSName DK_Title = COSName.constant("Title"); //$NON-NLS-1$

	public static final COSName DK_Author = COSName.constant("Author"); //$NON-NLS-1$

	public static final COSName DK_Subject = COSName.constant("Subject"); //$NON-NLS-1$

	public static final COSName DK_Keywords = COSName.constant("Keywords"); //$NON-NLS-1$

	public static final COSName DK_Trapped = COSName.constant("Trapped"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected COSInfoDict(COSObject object) {
		super(object);
	}

	/**
	 * The /Author field in the info dictionary.
	 * 
	 */
	public String getAuthor() {
		return getFieldString(DK_Author, null);
	}

	/**
	 * The /CreationDate field in the info dictionary.
	 * 
	 */
	public CDSDate getCreationDate() {
		return getFieldDate(DK_CreationDate, null);
	}

	/**
	 * The /CreationDate field in the info dictionary as a {@link String}.
	 * 
	 */
	public String getCreationDateString() {
		return getFieldString(DK_CreationDate, null);
	}

	/**
	 * The /Creator field in the info dictionary.
	 * 
	 */
	public String getCreator() {
		return getFieldString(DK_Creator, null);
	}

	/**
	 * The /Keywords field in the info dictionary.
	 * 
	 */
	public String getKeywords() {
		return getFieldString(DK_Keywords, null);
	}

	/**
	 * The /ModDate field in the info dictionary.
	 * 
	 */
	public CDSDate getModDate() {
		return getFieldDate(DK_ModDate, null);
	}

	/**
	 * The /ModDate field in the info dictionary as a {@link String}.
	 * 
	 */
	public String getModDateString() {
		return getFieldString(DK_ModDate, null);
	}

	/**
	 * The /Producer field in the info dictionary.
	 * 
	 */
	public String getProducer() {
		return getFieldString(DK_Producer, null);
	}

	/**
	 * The /Subject field in the info dictionary.
	 * 
	 */
	public String getSubject() {
		return getFieldString(DK_Subject, null);
	}

	/**
	 * The /Title field in the info dictionary.
	 * 
	 */
	public String getTitle() {
		return getFieldString(DK_Title, null);
	}

	/**
	 * The /Trapped field in the info dictionary.
	 * 
	 */
	public String getTrapped() {
		return getFieldString(DK_Trapped, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSBasedObject#initializeFromScratch()
	 */
	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		cosSetField(DK_Creator, COSString
				.create("jPod intarsys consulting pdf library") //$NON-NLS-1$
		);
		cosSetField(DK_CreationDate, new CDSDate().cosGetObject());
		cosSetField(DK_Producer, COSString
				.create("jPod intarsys consulting pdf library") //$NON-NLS-1$
		);
		cosSetField(DK_ModDate, new CDSDate().cosGetObject());
	}

	/**
	 * Set the /Author field in the info dictionary.
	 * 
	 */
	public void setAuthor(String value) {
		setFieldString(DK_Author, value);
	}

	/**
	 * Set the /CreationDate field in the info dictionary.
	 * 
	 */
	public void setCreationDate(String value) {
		setFieldString(DK_CreationDate, value);
	}

	/**
	 * Set the /Creator field in the info dictionary.
	 * 
	 */
	public void setCreator(String value) {
		setFieldString(DK_Creator, value);
	}

	/**
	 * Set the /Keywords field in the info dictionary.
	 * 
	 */
	public void setKeywords(String value) {
		setFieldString(DK_Keywords, value);
	}

	/**
	 * Set the /ModDate field in the info dictionary.
	 * 
	 */
	public void setModDate(String value) {
		setFieldString(DK_ModDate, value);
	}

	/**
	 * Set the /Producer field in the info dictionary.
	 * 
	 */
	public void setProducer(String value) {
		setFieldString(DK_Producer, value);
	}

	/**
	 * Set the /Subject field in the info dictionary.
	 * 
	 */
	public void setSubject(String value) {
		setFieldString(DK_Subject, value);
	}

	/**
	 * Set the /Title field in the info dictionary.
	 * 
	 */
	public void setTitle(String value) {
		setFieldString(DK_Title, value);
	}

	/**
	 * Set the /Trapped field in the info dictionary.
	 * 
	 */
	public void setTrapped(String value) {
		setFieldName(DK_Trapped, value);
	}
}
