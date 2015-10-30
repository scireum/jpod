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

/**
 * A generic markup annotation implementation.
 */
public class PDMarkupAnnotation extends PDAnnotation {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDMarkupAnnotation(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName DK_CreationDate = COSName
			.constant("CreationDate");

	public static final COSName DK_Subj = COSName.constant("Subj");

	public static final COSName DK_T = COSName.constant("T");

	public static final COSName DK_Popup = COSName.constant("Popup");

	public static final COSName DK_CA = COSName.constant("CA");

	public static final COSName DK_L = COSName.constant("L");

	public static final COSName DK_RC = COSName.constant("RC");

	public static final COSName DK_Vertices = COSName.constant("Vertices");

	public static final COSName CN_Subtype_Ink = COSName.constant("Ink");

	public static final COSName CN_Subtype_Square = COSName.constant("Square");

	public static final COSName CN_Subtype_Circle = COSName.constant("Circle");

	public static final COSName CN_Subtype_Line = COSName.constant("Line");

	public static final COSName CN_Subtype_Polygon = COSName
			.constant("Polygon");

	public static final COSName CN_Subtype_PolyLine = COSName
			.constant("PolyLine");

	public static final COSName DK_InkList = COSName.constant("InkList");

	public static final COSName DK_IC = COSName.constant("IC");

	public static final COSName DK_IRT = COSName.constant("IRT");

	protected PDMarkupAnnotation(COSObject object) {
		super(object);
	}

	@Override
	public COSObject cosSetField(COSName name, COSObject cosObj) {
		if (!DK_M.equals(name)) {
			touch();
		}
		return super.cosSetField(name, cosObj);
	}

	public CDSDate getCreationDate() {
		return CDSDate.createFromCOS(cosGetField(DK_CreationDate).asString());
	}

	public float[] getInnerColor() {
		return getFieldFixedArray(DK_IC, null);
	}

	public PDAnnotation getInReplyTo() {
		return (PDAnnotation) PDAnnotation.META
				.createFromCos(cosGetField(DK_IRT));
	}

	public double getOpacity() {
		return getFieldFixed(DK_CA, 1);
	}

	public PDAnnotation getPopup() {
		return (PDAnnotation) PDAnnotation.META
				.createFromCos(cosGetField(DK_Popup));
	}

	public String getRichContent() {
		return getFieldString(DK_RC, "");
	}

	public String getSubject() {
		return getFieldString(DK_Subj, "");
	}

	@Override
	public String getSubtypeLabel() {
		if (CN_Subtype_Circle.equals(cosGetSubtype())) {
			return "Circle";
		} else if (CN_Subtype_Ink.equals(cosGetSubtype())) {
			return "Freehand";
		} else if (CN_Subtype_Line.equals(cosGetSubtype())) {
			return "Line";
		} else if (CN_Subtype_Polygon.equals(cosGetSubtype())) {
			return "Polygon";
		} else if (CN_Subtype_PolyLine.equals(cosGetSubtype())) {
			return "Polyline";
		} else if (CN_Subtype_Square.equals(cosGetSubtype())) {
			return "Square";
		} else {
			return "Annotation";
		}
	}

	public String getText() {
		return getFieldString(DK_T, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAnnotation#isMarkupAnnotation()
	 */
	@Override
	public boolean isMarkupAnnotation() {
		return true;
	}

	public void setInnerColor(float[] color) {
		setFieldFixedArray(DK_IC, color);
	}

	public void setOpacity(float value) {
		setFieldFixed(DK_CA, value);
	}

	public void setPopup(PDPopupAnnotation popup) {
		setFieldObject(DK_Popup, popup);
	}

	public void setSubject(String value) {
		setFieldString(DK_Subj, value);
	}

	public void setText(String text) {
		setFieldString(DK_T, text);
	}
}
