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

import java.util.Date;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDEmbeddedFileParams extends PDObject {

	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDEmbeddedFileParams(object);
		}

		@Override
		protected boolean isIndirect() {
			return false;
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(
			MetaClass.class.getDeclaringClass());

	public static final COSName DK_Size = COSName.constant("Size"); //$NON-NLS-1$
	public static final COSName DK_CreationDate = COSName
			.constant("CreationDate"); //$NON-NLS-1$
	public static final COSName DK_ModDate = COSName.constant("ModDate"); //$NON-NLS-1$
	public static final COSName DK_Mac = COSName.constant("Mac"); //$NON-NLS-1$
	public static final COSName DK_CheckSum = COSName.constant("CheckSum"); //$NON-NLS-1$

	protected PDEmbeddedFileParams(COSObject object) {
		super(object);
	}

	public CDSDate getCreationDate() {
		return getFieldDate(DK_CreationDate, null);
	}

	public CDSDate getModDate() {
		return getFieldDate(DK_ModDate, null);
	}

	public Integer getSize() {
		int size = getFieldInt(DK_Size, -1);
		if (size == -1) {
			return null;
		}
		return size;
	}

	public void setCreationDate(CDSDate creationDate) {
		setFieldObject(DK_CreationDate, creationDate);
	}

	public void setCreationDate(Date date) {
		String stringValue = null;
		if (date != null) {
			stringValue = CDSDate.toStringWithZone(date);
		}
		setCreationDate(stringValue);
	}

	public void setCreationDate(String creationDate) {
		setFieldString(DK_CreationDate, creationDate);
	}

	public void setModDate(CDSDate modDate) {
		setFieldObject(DK_ModDate, modDate);
	}

	public void setModDate(Date date) {
		String stringValue = null;
		if (date != null) {
			stringValue = CDSDate.toStringWithZone(date);
		}
		setModDate(stringValue);
	}

	public void setModDate(String modDate) {
		setFieldString(DK_ModDate, modDate);
	}

	public void setSize(Integer size) {
		if (size == null) {
			cosRemoveField(DK_Size);
		} else {
			setFieldInt(DK_Size, size);
		}
	}

}
