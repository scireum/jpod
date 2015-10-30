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

import java.util.Arrays;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSBoolean;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSTrue;

/**
 * An object describing the appearance of an icon within a button.
 * 
 */
public class PDIconFit extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDIconFit(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	static private final float ALIGNMENT_CENTER = 0.5f;

	private static final float[] DEFAULT_ALIGNMENT = new float[] {
			ALIGNMENT_CENTER, ALIGNMENT_CENTER };

	static public final COSName DK_SW = COSName.constant("SW");

	static public final COSName DK_S = COSName.constant("S");

	static public final COSName DK_A = COSName.constant("A");

	static public final COSName DK_FB = COSName.constant("FB");

	/** always scale, the default */
	static public final COSName CN_SW_A = COSName.constant("A");

	/** scale only when the icon is bigger than the annotation rectangle */
	static public final COSName CN_SW_B = COSName.constant("B");

	/** scale only when the icon is smaller than the annotation rectangle */
	static public final COSName CN_SW_S = COSName.constant("S");

	/** never scale */
	static public final COSName CN_SW_N = COSName.constant("N");

	/** Anamorphic scaling */
	static public final COSName CN_S_A = COSName.constant("A");

	/** Proportional scaling, default */
	static public final COSName CN_S_P = COSName.constant("P");

	protected PDIconFit(COSObject object) {
		super(object);
	}

	public void setAlignment(float[] align) {
		if ((align == null) || Arrays.equals(align, DEFAULT_ALIGNMENT)) {
			cosRemoveField(DK_A);
		} else {
			setFieldFixedArray(DK_A, align);
		}
	}

	public float[] getAlignment() {
		return getFieldFixedArray(DK_A, DEFAULT_ALIGNMENT);
	}

	public void setScalingCircumstances(COSName flag) {
		if ((flag != null) && !flag.equals(CN_SW_A)) {
			cosSetField(DK_SW, flag);
		} else {
			cosRemoveField(DK_SW);
		}
	}

	public COSName getScalingCircumstances() {
		COSName cosObject = cosGetField(DK_SW).asName();
		if (cosObject != null) {
			return cosObject;
		}
		return CN_SW_A;
	}

	public void setScalingMode(COSName flag) {
		if ((flag != null) && !flag.equals(CN_S_P)) {
			cosSetField(DK_S, flag);
		} else {
			cosRemoveField(DK_S);
		}
	}

	public COSName getScalingMode() {
		COSName cosObject = cosGetField(DK_S).asName();
		if (cosObject != null) {
			return cosObject;
		}
		return CN_S_P;
	}

	public void setIgnoreBorderWidth(boolean ignore) {
		if (ignore) {
			cosSetField(DK_FB, COSTrue.create());
		} else {
			cosRemoveField(DK_FB); // false is default
		}
	}

	public boolean isIgnoreBorderWidth() {
		COSBoolean ignore = cosGetField(DK_FB).asBoolean();
		if (ignore == null) {
			return false;
		}
		return ignore.booleanValue();
	}
}
