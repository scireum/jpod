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

import java.net.MalformedURLException;
import java.net.URL;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSString;

public class PDFileSpecificationURL extends PDFileSpecification {
	// todo 2 review url attribute

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDFileSpecification.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDFileSpecificationURL(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	static public PDFileSpecificationURL createNew(URL url) {
		PDFileSpecificationURL result = (PDFileSpecificationURL) META
				.createNew();
		result.setURL(url);
		return result;
	}

	private URL url = null;

	protected PDFileSpecificationURL(COSObject object) {
		super(object);
	}

	public URL getURL() {
		return url;
	}

	/**
	 * provide some hook for initialization after creation based on a cos object
	 */
	@Override
	protected void initializeFromCos() {
		super.initializeFromCos();
		setFileSystem(CN_FS_URL);
		COSString fileString = cosGetField(DK_F).asString();
		if (fileString != null) {
			try {
				setURL(new URL(fileString.stringValue()));
			} catch (MalformedURLException e) {
				cosGetObject().handleException(
						new COSRuntimeException("error parsing URL", e));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDFileSpecification#initializeFromScratch()
	 */
	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		setFileSystem((COSName) CN_FS_URL.copyShallow());
	}

	public void setURL(URL newURL) {
		this.url = newURL;
		if (newURL == null) {
			setFieldString(DK_F, null);
		} else {
			setFieldString(DK_F, url.toString());
		}
	}
}
