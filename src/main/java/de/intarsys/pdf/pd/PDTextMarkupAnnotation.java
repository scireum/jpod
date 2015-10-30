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

/**
 * An abstract text markup (highlight, underline, squiggly, strikeout)
 * annotation.
 * <p>
 * This class is not abstract for historical reasons.
 */
public class PDTextMarkupAnnotation extends PDMarkupAnnotation {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDMarkupAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDTextMarkupAnnotation(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName DK_QuadPoints = COSName.constant("QuadPoints");

	public static final COSName CN_Subtype_Highlight = COSName
			.constant("Highlight");

	public static final COSName CN_Subtype_Underline = COSName
			.constant("Underline");

	public static final COSName CN_Subtype_Squiggly = COSName
			.constant("Squiggly");

	public static final COSName CN_Subtype_StrikeOut = COSName
			.constant("StrikeOut");

	protected PDTextMarkupAnnotation(COSObject object) {
		super(object);
	}

	public float[] getQuadPoints() {
		return getFieldFixedArray(DK_QuadPoints, null);
	}

	@Override
	public String getSubtypeLabel() {
		if (CN_Subtype_Highlight.equals(cosGetSubtype())) {
			return "Highlight";
		} else if (CN_Subtype_Underline.equals(cosGetSubtype())) {
			return "Underline";
		} else if (CN_Subtype_Squiggly.equals(cosGetSubtype())) {
			return "Squiggly";
		} else if (CN_Subtype_StrikeOut.equals(cosGetSubtype())) {
			return "StrikeOut";
		} else {
			return super.getSubtypeLabel();
		}
	}

	public void setQuadPoints(float[] color) {
		setFieldFixedArray(DK_QuadPoints, color);
	}
}
