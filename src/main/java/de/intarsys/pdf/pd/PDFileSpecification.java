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
import de.intarsys.pdf.cos.COSString;

/**
 * Repesents the PDF's FileSpecification-type
 * 
 */
public class PDFileSpecification extends PDObject {
	// todo 2 review file system attribute

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDFileSpecification(object);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * de.intarsys.pdf.cos.COSBasedObject.MetaClass#doDetermineClass(de.
		 * intarsys.pdf.cos.COSObject)
		 */
		@Override
		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			// Type entry is optional!
			// COSName type = ((COSDictionary) object).get(DK_Type).asName();
			// if (!CN_Type_Filespec.equals(type)) {
			// throw new IllegalArgumentException(
			// "not a File Specification Dictionary");
			// }
			COSName fileSystem = ((COSDictionary) object).get(DK_FS).asName();
			if (CN_FS_URL.equals(fileSystem)) {
				return PDFileSpecificationURL.META;
			}
			return PDFileSpecification.META;
		}

		@Override
		public Class getRootClass() {
			return PDFileSpecification.class;
		}
	}

	static public final COSName CN_Type_Filespec = COSName.constant("Filespec");

	static public final COSName CN_Type_Alt_Filespec = COSName.constant("F");

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(
			MetaClass.class.getDeclaringClass());

	/** Name of the file system. */
	static public final COSName DK_FS = COSName.constant("FS");

	/** File */
	static public final COSName DK_F = COSName.constant("F");

	/** Unicode File */
	static public final COSName DK_UF = COSName.constant("UF");

	/** File specification string for DOS files */
	static public final COSName CN_F_DOS = COSName.constant("DOS");

	/** File specification string for Macintosh files */
	static public final COSName CN_F_Mac = COSName.constant("Mac");

	/** File specification string for UNIX files */
	static public final COSName CN_F_Unix = COSName.constant("Unix");

	/** File URL */
	static public final COSName CN_FS_URL = COSName.constant("URL");

	/** Embedded file dict */
	static public final COSName DK_EF = COSName.constant("EF");

	/** Embedded file array dict. */
	static public final COSName DK_RF = COSName.constant("RF");

	/** Description */
	static public final COSName DK_Desc = COSName.constant("Desc");

	private COSName fileSystem = null;

	protected PDFileSpecification(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Filespec;
	}

	public String getDescription() {
		COSString cosDesc = cosGetField(DK_Desc).asString();
		if (cosDesc == null) {
			return null;
		}
		return cosDesc.stringValue();
	}

	/**
	 * The embedded file of a specific flavor (one of F, DOS, Mac, Unix),
	 * 
	 * @param flavor
	 *            One of F, DOC, Mac, Unix
	 * @return the embedded file of this flavor, if any, or null.
	 */
	public PDEmbeddedFile getEmbeddedFile(COSName flavor) {
		COSDictionary embeddedFileDict = getEmbeddedFiles();
		if (embeddedFileDict == null) {
			return null;
		}
		return (PDEmbeddedFile) PDEmbeddedFile.META
				.createFromCos(embeddedFileDict.get(flavor));
	}

	/**
	 * The dictionary mapping the F/DOC/Mac/Unix entries of the file spec to
	 * embedded file dictionaries.
	 */
	public COSDictionary getEmbeddedFiles() {
		return cosGetField(DK_EF).asDictionary();
	}

	public String getFile() {
		COSString cosFile = cosGetField(DK_F).asString();
		if (cosFile == null) {
			return null;
		}
		return cosFile.stringValue();
	}

	public String getFileSpecificationString(COSName flavor) {
		return cosGetField(flavor).stringValue();
	}

	public COSName getFileSystem() {
		return fileSystem;
	}

	/**
	 * provide some hook for initialization after creation based on a cos object
	 */
	@Override
	protected void initializeFromCos() {
		super.initializeFromCos();
	}

	/**
	 * provide some hook for initialization after creation from scratch
	 */
	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		cosSetField(DK_FS, getFileSystem());
	}

	public void setDescription(String description) {
		setFieldString(DK_Desc, description);
	}

	public void setEmbeddedFile(COSName flavor, PDEmbeddedFile embeddedFile) {
		COSDictionary embeddedFileDict = getEmbeddedFiles();
		if (embeddedFileDict == null) {
			embeddedFileDict = COSDictionary.create();
			cosSetField(DK_EF, embeddedFileDict);
		}
		embeddedFileDict.put(flavor, embeddedFile.cosGetObject());
	}

	public void setFile(String file) {
		setFieldString(DK_F, file);
	}

	public void setFileSpecificationString(COSName flavor, String spec) {
		setFieldString(flavor, spec);
	}

	public void setFileSystem(COSName bs) {
		fileSystem = bs;
	}
}
