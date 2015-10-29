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
 * An annotation implementing a hyperlink behavior within a document.
 */
public class PDLinkAnnotation extends PDAnnotation {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDLinkAnnotation(object);
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName CN_Subtype_Link = COSName.constant("Link");

	public static final COSName DK_Dest = COSName.constant("Dest");

	public static final COSName DK_H = COSName.constant("H");

	public static final COSName DK_PA = COSName.constant("PA");

	public static final COSName DK_QuadPoints = COSName.constant("QuadPoints");

	public static final COSName CN_H_I = COSName.constant("I");

	public static final COSName CN_H_N = COSName.constant("N");

	public static final COSName CN_H_O = COSName.constant("O");

	public static final COSName CN_H_P = COSName.constant("P");

	protected PDLinkAnnotation(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
	 */
	@Override
	protected COSName cosGetExpectedSubtype() {
		return CN_Subtype_Link;
	}

	@Override
	public float getDefaultHeight() {
		return 30;
	}

	@Override
	public float getDefaultWidth() {
		return 120;
	}

	public PDDestination getDestination() {
		COSObject destObj = cosGetField(DK_Dest);
		if (destObj.isNull()) {
			return null;
		}
		return (PDDestination) PDDestination.META.createFromCos(destObj);
	}

	public COSName getHighlightingMode() {
		COSName mode = cosGetField(DK_H).asName();
		if (mode != null) {
			return mode;
		}
		return CN_H_I; // default
	}

	@Override
	public String getSubtypeLabel() {
		return "Link";
	}

	public void setDestination(PDDestination destination) {
		setFieldObject(DK_Dest, destination);
	}

	public void setHighlightingMode(COSName newHighlightingMode) {
		if ((newHighlightingMode == null) || CN_H_I.equals(newHighlightingMode)) {
			cosRemoveField(DK_H);
		} else {
			cosSetField(DK_H, newHighlightingMode);
		}
	}
}
