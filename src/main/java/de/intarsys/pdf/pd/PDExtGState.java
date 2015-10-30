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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * The extended graphic state object. This is used in a resource dictionary to
 * additionally define graphics context information.
 */
public class PDExtGState extends PDObject {
	public static final COSName DK_LW = COSName.constant("LW");

	public static final COSName DK_LC = COSName.constant("LC");

	public static final COSName DK_LJ = COSName.constant("LJ");

	public static final COSName DK_ML = COSName.constant("ML");

	public static final COSName DK_D = COSName.constant("D");

	public static final COSName DK_RI = COSName.constant("RI");

	public static final COSName DK_OP = COSName.constant("OP");

	public static final COSName DK_op = COSName.constant("op");

	public static final COSName DK_OPM = COSName.constant("OPM");

	public static final COSName DK_Font = COSName.constant("Font");

	public static final COSName DK_BG = COSName.constant("BG");

	public static final COSName DK_BG2 = COSName.constant("BG2");

	public static final COSName DK_UCR = COSName.constant("UCR");

	public static final COSName DK_UCR2 = COSName.constant("UCR2");

	public static final COSName DK_TR = COSName.constant("TR");

	public static final COSName DK_TR2 = COSName.constant("TR2");

	public static final COSName DK_HT = COSName.constant("HT");

	public static final COSName DK_FL = COSName.constant("FL");

	public static final COSName DK_SM = COSName.constant("SM");

	public static final COSName DK_SA = COSName.constant("SA");

	public static final COSName DK_BM = COSName.constant("BM");

	public static final COSName DK_SMask = COSName.constant("SMask");

	public static final COSName DK_CA = COSName.constant("CA");

	public static final COSName DK_ca = COSName.constant("ca");

	public static final COSName DK_AIS = COSName.constant("AIS");

	public static final COSName DK_TK = COSName.constant("TK");

	public static final COSName CN_BM_Multiply = COSName.constant("Multiply");

	public static final COSName CN_BM_Normal = COSName.constant("Normal");

	/** supported additional action triggers */
	static public final Set BLEND_MODES;

	static {
		BLEND_MODES = new HashSet(5);
		BLEND_MODES.add(CN_BM_Normal);
		BLEND_MODES.add(CN_BM_Multiply);
	}

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDExtGState(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	/** The type name */
	static public final COSName CN_Type_ExtGState = COSName
			.constant("ExtGState"); // 

	protected PDExtGState(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	protected COSName cosGetExpectedType() {
		return CN_Type_ExtGState;
	}

	public void setStrokingAlphaConstant(float alpha) {
		setFieldFixed(DK_CA, alpha);
	}

	public void setNonStrokingAlphaConstant(float alpha) {
		setFieldFixed(DK_ca, alpha);
	}

	public float getStrokingAlphaConstant() {
		return getFieldFixed(DK_CA, 1.0f);
	}

	public float getNonStrokingAlphaConstant() {
		return getFieldFixed(DK_ca, 1.0f);
	}

	public COSName getBlendMode() {
		COSObject blendMode = cosGetField(DK_BM);
		if (blendMode.isNull()) {
			return CN_BM_Normal;
		}
		if (blendMode instanceof COSName) {
			if (BLEND_MODES.contains(blendMode)) {
				return (COSName) blendMode;
			}
		} else if (blendMode instanceof COSArray) {
			COSArray blendModes = (COSArray) blendMode;
			for (Iterator i = blendModes.iterator(); i.hasNext();) {
				COSName current = ((COSObject) i.next()).asName();
				if (BLEND_MODES.contains(current)) {
					return (COSName) current;
				}
			}
		}
		return CN_BM_Normal;
	}

	public void setBlendMode(COSName blendMode) {
		if (CN_BM_Normal.equals(blendMode)) {
			blendMode = null;
		}
		cosSetField(DK_BM, blendMode);
	}

	public boolean isBlendModeNormal() {
		return CN_BM_Normal.equals(getBlendMode());
	}

	public void setBlendModeNormal() {
		setBlendMode(CN_BM_Normal);
	}

	public boolean isBlendModeMultiply() {
		return CN_BM_Multiply.equals(getBlendMode());
	}

	public void setBlendModeMultiply() {
		setBlendMode(CN_BM_Multiply);
	}
}
