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

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.tools.string.StringTools;

/**
 * 
 * 
 */
public class PDBuildData extends PDObject {

	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDBuildData(object);
		}

		protected boolean isIndirect() {
			return false;
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName DK_Name = COSName.constant("Name"); //$NON-NLS-1$
	public static final COSName DK_Date = COSName.constant("Date"); //$NON-NLS-1$
	public static final COSName DK_R = COSName.constant("R"); //$NON-NLS-1$
	public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$
	public static final COSName DK_PreRelease = COSName.constant("PreRelease"); //$NON-NLS-1$
	public static final COSName DK_OS = COSName.constant("OS"); //$NON-NLS-1$
	public static final COSName DK_NonEFontNoWarn = COSName
			.constant("NonEFontNoWarn"); //$NON-NLS-1$
	public static final COSName DK_TrustedMode = COSName
			.constant("TrustedMode"); //$NON-NLS-1$

	protected PDBuildData(COSObject object) {
		super(object);
	}

	public CDSDate getDate() {
		return getFieldDate(DK_Date, null);
	}

	public String getName() {
		return getFieldString(DK_Name, StringTools.EMPTY);
	}

	public String getOS() {
		return getFieldString(DK_OS, StringTools.EMPTY);
	}

	public int getR() {
		return getFieldInt(DK_R, 0);
	}

	public int getV() {
		return getFieldInt(DK_V, 0);
	}

	public boolean isNonEFontNoWarn() {
		return getFieldBoolean(DK_NonEFontNoWarn, true);
	}

	public boolean isPreRelease() {
		return getFieldBoolean(DK_PreRelease, false);
	}

	public boolean isTrustedMode() {
		return getFieldBoolean(DK_TrustedMode, false);
	}

	public void setDate(CDSDate date) {
		setFieldString(DK_Date, date == null ? null : date.stringValue());
	}

	public void setName(String name) {
		setFieldString(DK_Name, name);
	}

	public void setNonEFontNoWarn(boolean nonEFontNoWarn) {
		setFieldBoolean(DK_NonEFontNoWarn, nonEFontNoWarn);
	}

	public void setOS(String osString) {
		setFieldString(DK_OS, osString);
	}

	public void setPreRelease(boolean preRelease) {
		setFieldBoolean(DK_PreRelease, preRelease);
	}

	public void setR(int moduleRevision) {
		setFieldInt(DK_R, moduleRevision);
	}

	public void setTrustedMode(boolean trustedMode) {
		setFieldBoolean(DK_TrustedMode, trustedMode);
	}

	public void setV(int minimumSoftwareVersion) {
		setFieldInt(DK_V, minimumSoftwareVersion);
	}
}
