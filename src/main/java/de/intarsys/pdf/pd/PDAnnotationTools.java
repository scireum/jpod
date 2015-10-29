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

import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.st.STDocument;

/**
 * Tool class for common tasks with {@link PDAnnotation} objects.
 * 
 */
public class PDAnnotationTools {
	public static final String CAPTION_CHECK = "4";

	public static final String CAPTION_CIRCLE = "l";

	public static final String CAPTION_CROSS = "8";

	public static final String CAPTION_DIAMOND = "u";

	public static final String CAPTION_SQUARE = "n";

	public static final String CAPTION_STAR = "H";

	public static void adjustRectangleToAppearance(PDAnnotation annotation) {
		PDAppearance appearance = annotation.getAppearance();
		if (appearance == null) {
			return;
		}
		adjustRectangleToAppearance(annotation, appearance);
	}

	public static void adjustRectangleToAppearance(PDAnnotation annotation,
			PDAppearance appearance) {
		List<PDForm> forms = appearance.getForms();
		Float maxWidth = null;
		Float maxHeight = null;
		for (PDForm form : forms) {
			CDSRectangle formRect = form.getBoundingBox();
			float width = formRect.getWidth();
			float height = formRect.getHeight();
			if (maxWidth == null || width > maxWidth) {
				maxWidth = width;
			}
			if (maxHeight == null || height > maxHeight) {
				maxHeight = height;
			}
		}
		// adjust annotation rect
		boolean changedRect = false;
		CDSRectangle rect = annotation.getNormalizedRectangle();
		if (maxWidth != null && maxWidth != rect.getWidth()) {
			rect.setWidth(maxWidth);
			changedRect = true;
		}
		if (maxHeight != null && maxHeight != rect.getHeight()) {
			rect.setHeight(maxHeight);
			changedRect = true;
		}
		// adjust annotation
		if (changedRect) {
			STDocument doc = annotation.getDoc().cosGetDoc().stGetDoc();
			boolean wasDirty = doc.isDirty();
			annotation.setRectangle(rect);
			if (!wasDirty) {
				doc.setDirty(false);
			}
		}

	}

	/**
	 * The {@link PDAppearance} for annotation. If no {@link PDAppearance} is
	 * yet available, a new one is created but NOT associated with the
	 * annotation. This behavior is intended to allow for dynamic appearance
	 * creation when rendering without changing the document itself.
	 * 
	 * @return The {@link PDAppearance} for <code>annotation</code>.
	 */
	public static PDAppearance getAppearance(PDAnnotation annotation) {
		PDAppearance appearance = annotation.getAppearance();
		if (appearance == null) {
			appearance = (PDAppearance) PDAppearance.META.createNew();
		}
		return appearance;
	}

	public static float[] getBorderColor(PDWidgetAnnotation annotation) {
		PDAppearanceCharacteristics appearanceCharacteristics = annotation
				.getAppearanceCharacteristics();
		if (appearanceCharacteristics == null) {
			return null;
		}
		return appearanceCharacteristics.getBorderColor();
	}

	public static float[] getBorderColorNegative(PDWidgetAnnotation annotation) {
		float[] borderColor = getBorderColor(annotation);
		if (borderColor == null) {
			return null;
		}
		float[] borderColorNegative = new float[borderColor.length];
		for (int i = 0; i < borderColor.length; i++) {
			borderColorNegative[i] = Math.max(
					Math.min(borderColorNegative[i] + 0.25f, 1), 0);
		}
		return borderColorNegative;
	}

	public static float[] getFillColor(PDWidgetAnnotation annotation) {
		PDAppearanceCharacteristics appearanceCharacteristics = annotation
				.getAppearanceCharacteristics();
		if (appearanceCharacteristics == null) {
			return null;
		}
		return appearanceCharacteristics.getBackgroundColor();
	}

	public static float[] getFillColorNegative(PDWidgetAnnotation annotation) {
		float[] fillColor = getFillColor(annotation);
		if (fillColor == null) {
			return null;
		}
		float[] fillColorNegative = new float[fillColor.length];
		for (int i = 0; i < fillColor.length; i++) {
			fillColorNegative[i] = Math.max(Math.min(fillColor[i] - 0.25f, 1),
					0);
		}
		return fillColorNegative;
	}

	/**
	 * Returns the next annotation following <code>annotation</code> on the same
	 * page. Returns <code>null</code> if no annotation following
	 * <code>annotation</code> could be found or <code>annotation</code> is the
	 * last one on the page.
	 * 
	 * @param annotation
	 * 
	 * @return the next annotation or <code>null</code>
	 */
	static public PDAnnotation getNextAnnotation(PDAnnotation annotation) {
		PDPage page = getPage(annotation);
		if (page == null) {
			return null;
		}
		return page.getNextAnnotation(annotation);
	}

	/**
	 * Returns the next annotation following <code>annotation</code> in the
	 * whole document. Returns <code>null</code> if no annotation following
	 * <code>annotation</code> could be found in the document.
	 * 
	 * @param annotation
	 * 
	 * @return the next annotation or <code>null</code>
	 */
	static public PDAnnotation getNextAnnotationAllPages(PDAnnotation annotation) {
		PDPage page = getPage(annotation);
		if (page == null) {
			return null;
		}
		PDAnnotation result = getNextAnnotation(annotation);
		while (result == null) {
			page = page.getNextPage();
			if (page == null) {
				return null;
			}
			result = page.getFirstAnnotation();
		}
		return result;
	}

	static public PDPage getPage(PDAnnotation annotation) {
		PDPage page = annotation.getPage();
		if (page != null) {
			return page;
		}
		PDDocument doc = annotation.getDoc();
		if (doc == null) {
			return null;
		}
		for (page = doc.getPageTree().getFirstPage(); page != null; page = page
				.getNextPage()) {
			List pageAnnots = page.getAnnotations();
			if (pageAnnots == null) {
				continue;
			}
			for (Iterator i = pageAnnots.iterator(); i.hasNext();) {
				PDAnnotation current = (PDAnnotation) i.next();
				if (current.cosGetObject() == annotation.cosGetObject()) {
					return page;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the annotation preceding <code>annotation</code> on the same
	 * page. Returns <code>null</code> if no annotation preceding
	 * <code>annotation</code> could be found or <code>annotation</code> is the
	 * first one on the page.
	 * 
	 * @param annotation
	 * 
	 * @return the preceding annotation or <code>null</code>
	 */
	static public PDAnnotation getPreviousAnnotation(PDAnnotation annotation) {
		PDPage page = getPage(annotation);
		if (page == null) {
			return null;
		}
		return page.getPreviousAnnotation(annotation);
	}

	/**
	 * Returns the annotation preceding <code>annotation</code> in the whole
	 * document. Returns <code>null</code> if no annotation preceding
	 * <code>annotation</code> could be found in the document.
	 * 
	 * @param annotation
	 * 
	 * @return the previous annotation or <code>null</code>
	 */
	static public PDAnnotation getPreviousAnnotationAllPages(
			PDAnnotation annotation) {
		PDPage page = getPage(annotation);
		if (page == null) {
			return null;
		}
		PDAnnotation result = getPreviousAnnotation(annotation);
		while (result == null) {
			page = page.getPreviousPage();
			if (page == null) {
				return null;
			}
			result = page.getLastAnnotation();
		}
		return result;
	}

	/**
	 * Lookup the state that is used to represent "not off" in
	 * <code>annotation</code>.
	 * 
	 * @param annotation
	 *            The annotation to inspect for its "not off" state.
	 * @return Lookup the state that is used to represent "not off" in
	 *         <code>annotation</code>.
	 */
	public static COSName getStateChecked(PDWidgetAnnotation annotation) {
		for (Iterator i = annotation.getAppearanceStates().iterator(); i
				.hasNext();) {
			COSName state = (COSName) i.next();
			if (isStateChecked(state)) {
				return state;
			}
		}
		return null;
	}

	/**
	 * Checks a COSDictionary for being a subtyped known annotation as of Spec
	 * PDF 1.4
	 * 
	 * @param dict
	 * @return <code>true</code> if known as of Spec PDF 1.4
	 */
	public static boolean isAnnotationSpec14(COSDictionary dict) {
		COSName subtype = dict.get(PDObject.DK_Subtype).asName();
		if (subtype == null) {
			return false;
		}
		if (subtype.equals(PDWidgetAnnotation.CN_Subtype_Widget)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_Ink)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_Square)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_Circle)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_Line)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_Polygon)) {
			return true;
		}
		if (subtype.equals(PDMarkupAnnotation.CN_Subtype_PolyLine)) {
			return true;
		}
		if (subtype.equals(PDPopupAnnotation.CN_Subtype_Popup)) {
			return true;
		}
		if (subtype.equals(COSName.constant("Link"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("FreeText"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Highlight"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Underline"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Squiggly"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("StrikeOut"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Stamp"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("FileAttachment"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Sound"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("Movie"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("PrinterMark"))) {
			return true;
		}
		if (subtype.equals(COSName.constant("TrapNet"))) {
			return true;
		}
		return false;
	}

	/**
	 * <code>true</code> if <code>state</code> represents a state that is not
	 * "/Off". "/Off" is the only legal way to switch of a toggle button, so
	 * anything else is "on".
	 * 
	 * @param state
	 *            The state to inspect if it is not "/Off".
	 * @return <code>true</code> if <code>state</code> represents a state that
	 *         is not "/Off".
	 */
	public static boolean isStateChecked(COSName state) {
		return !PDWidgetAnnotation.CN_State_Off.equals(state);
	}

	public static void transform(CDSRectangle rect, CDSMatrix matrix,
			int rotation) {
		if (rotation == 90) {
			matrix.setTransformation(CDSMatrix.MATRIX_90);
		} else if (rotation == 180) {
			matrix.setTransformation(CDSMatrix.MATRIX_180);
		} else if (rotation == 270) {
			matrix.setTransformation(CDSMatrix.MATRIX_270);
		}
		float[] vec = new float[] { rect.getWidth(), rect.getHeight() };
		float[] tVec = matrix.transform(vec);
		if (rotation == 90) {
			matrix.setE(rect.getWidth());
		} else if (rotation == 180) {
			matrix.setE(rect.getWidth());
			matrix.setF(rect.getHeight());
		} else if (rotation == 270) {
			matrix.setF(rect.getHeight());
		}
		rect.setCorners(0, 0, tVec[0], tVec[1]);
		rect.normalize();
	}

	private PDAnnotationTools() {
		// tool class
	}
}
