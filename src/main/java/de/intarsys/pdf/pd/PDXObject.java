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
import de.intarsys.pdf.cos.COSStream;

/**
 * The representation of a XObject. A XObject defines a strokable object. The
 * content is defined in the underlying content stream.
 */
abstract public class PDXObject extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSObject doCreateCOSObject() {
			return COSStream.create(null);
		}

		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			COSDictionary dict;
			if (object instanceof COSStream) {
				dict = ((COSStream) object).getDict();
			} else {
				dict = (COSDictionary) object;
			}
			COSName subtype = (COSName) dict.get(DK_Subtype).asName();
			if (PDForm.CN_Subtype_Form.equals(subtype)) {
				return PDForm.META;
			} else if (PDImage.CN_Subtype_Image.equals(subtype)) {
				return PDImage.META;
			} else if (PDPostScript.CN_Subtype_PS.equals(subtype)) {
				return PDPostScript.META;
			} else {
				return super.doDetermineClass(object);
			}
		}

		public Class getRootClass() {
			return PDXObject.class;
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	//
	static public final COSName DK_Resources = COSName.constant("Resources");

	static public final COSName CN_Type_XObject = COSName.constant("XObject");

	/**
	 * Create the receiver class from an already defined {@link COSStream}.
	 * NEVER use the constructor directly.
	 * 
	 * @param object
	 *            the PDDocument containing the new object
	 */
	protected PDXObject(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSBasedObject#cosGetDict()
	 */
	public COSDictionary cosGetDict() {
		return cosGetStream().getDict();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	protected COSName cosGetExpectedType() {
		return CN_Type_XObject;
	}

	/**
	 * The data representing the XObject
	 * 
	 * @return The data representing the XObject
	 */
	public byte[] getBytes() {
		return cosGetStream().getDecodedBytes();
	}

	/**
	 * <code>true</code> if this is a form.
	 * 
	 * @return <code>true</code> if this is a form.
	 */
	public boolean isForm() {
		return false;
	}

	/**
	 * <code>true</code> if this is an image.
	 * 
	 * @return <code>true</code> if this is an image.
	 */
	public boolean isImage() {
		return false;
	}

	/**
	 * <code>true</code> if this is a postscript object.
	 * 
	 * @return <code>true</code> if this is a postscript object.
	 */
	public boolean isPostscript() {
		return false;
	}

	/**
	 * Set the data representing the XObject
	 * 
	 * @param bytes
	 *            The data representing the XObject
	 */
	public void setBytes(byte[] bytes) {
		cosGetStream().setDecodedBytes(bytes);
	}
}
