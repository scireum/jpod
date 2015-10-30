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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * More details on the appearance of an annotation.
 * 
 */
public class PDAppearanceCharacteristics extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDAppearanceCharacteristics(object);
		}

		protected boolean isIndirect() {
			return false;
		}
	}

	static public final COSName DK_AC = COSName.constant("AC"); //$NON-NLS-1$ 

	static public final COSName DK_BC = COSName.constant("BC"); //$NON-NLS-1$ 

	static public final COSName DK_BG = COSName.constant("BG"); //$NON-NLS-1$ 

	static public final COSName DK_CA = COSName.constant("CA"); //$NON-NLS-1$ 

	static public final COSName DK_I = COSName.constant("I"); //$NON-NLS-1$ 

	static public final COSName DK_IF = COSName.constant("IF"); //$NON-NLS-1$

	static public final COSName DK_IX = COSName.constant("IX"); //$NON-NLS-1$

	static public final COSName DK_R = COSName.constant("R"); //$NON-NLS-1$

	static public final COSName DK_RC = COSName.constant("RC"); //$NON-NLS-1$

	static public final COSName DK_RI = COSName.constant("RI"); //$NON-NLS-1$

	static public final int TP_CAPTION_ONLY = 0;

	static public final int TP_ICON_ONLY = 1;

	static public final int TP_CAPTION_BELOW_ICON = 2;

	static public final int TP_CAPTION_ABOVE_ICON = 3;

	static public final int TP_CAPTION_RIGHT_OF_ICON = 4;

	static public final int TP_CAPTION_LEFT_OF_ICON = 5;

	static public final int TP_CAPTION_OVERLAIS_ICON = 6;

	/**
	 * The name for the caption entry.
	 * <p>
	 * The name could have following values (not complete):
	 * </p>
	 * <ul>
	 * <li>0: No icon, caption only
	 * <li>1: No caption; icon only
	 * <li>2: Caption below the icon
	 * <li>3: Caption above the icon
	 * <li>4: Caption to the right of the icon
	 * <li>5: Caption to the left of the icon
	 * <li>6: Caption overlaid directly on the icon
	 * </ul>
	 */
	static public final COSName DK_TP = COSName.constant("TP"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected PDAppearanceCharacteristics(COSObject object) {
		super(object);
	}

	public float[] getBackgroundColor() {
		return getFieldFixedArray(DK_BG, null);
	}

	public float[] getBorderColor() {
		return getFieldFixedArray(DK_BC, null);
	}

	public String getDownCaption() {
		return getFieldString(DK_AC, null);
	}

	public COSStream cosGetDownIcon() {
		return cosGetField(DK_IX).asStream();
	}

	public PDIconFit getIconFit() {
		COSObject cosObject = cosGetField(DK_IF);
		if (cosObject.isNull()) {
			return null;
		}
		return (PDIconFit) PDIconFit.META.createFromCos(cosObject);
	}

	public String getNormalCaption() {
		return getFieldString(DK_CA, null);
	}

	public COSStream cosGetNormalIcon() {
		return cosGetField(DK_I).asStream();
	}

	public int getTextPosition() {
		return getFieldInt(DK_TP, TP_CAPTION_ONLY);
	}

	public String getRolloverCaption() {
		return getFieldString(DK_RC, null);
	}

	public COSStream cosGetRolloverIcon() {
		return cosGetField(DK_RI).asStream();
	}

	public int getRotation() {
		return getFieldInt(DK_R, 0);
	}

	public void setBackgroundColor(float[] newBackgroundColor) {
		setFieldFixedArray(DK_BG, newBackgroundColor);
	}

	public void setBorderColor(float[] newBorderColor) {
		setFieldFixedArray(DK_BC, newBorderColor);
	}

	public void setDownCaption(String newDownCaption) {
		setFieldString(DK_AC, newDownCaption);
	}

	public COSStream cosSetDownIcon(COSStream newDownIcon) {
		return cosSetField(DK_IX, newDownIcon).asStream();
	}

	public void setIconFit(PDIconFit newIconFit) {
		setFieldObject(DK_IF, newIconFit);
	}

	public void setNormalCaption(String newNormalCaption) {
		setFieldString(DK_CA, newNormalCaption);
	}

	public COSStream cosSetNormalIcon(COSStream newNormalIcon) {
		return cosSetField(DK_I, newNormalIcon).asStream();
	}

	public void setTextPosition(int newPositionTextIcon) {
		if (newPositionTextIcon != TP_CAPTION_ONLY) { // default
			setFieldInt(DK_TP, newPositionTextIcon);
		} else {
			cosRemoveField(DK_TP);
		}
	}

	public void setRolloverCaption(String newRolloverCaption) {
		setFieldString(DK_RC, newRolloverCaption);
	}

	public COSStream cosSetRolloverIcon(COSStream newRolloverIcon) {
		return cosSetField(DK_RI, newRolloverIcon).asStream();
	}

	public void setRotation(int newRotation) {
		if (newRotation != 0) {
			setFieldInt(DK_R, newRotation);
		} else {
			cosRemoveField(DK_R);
		}
	}
}
