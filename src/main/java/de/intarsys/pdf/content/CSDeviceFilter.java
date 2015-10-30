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
package de.intarsys.pdf.content;

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.pdf.pd.PDForm;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDPostScript;
import de.intarsys.pdf.pd.PDShading;
import de.intarsys.pdf.pd.PDXObject;

/**
 * Abstract superclass for implementing a filter on {@link ICSDevice}.
 * <p>
 * Just create a subclass and switch of / extend the methods of interest.
 * 
 */
abstract public class CSDeviceFilter implements ICSDevice, ICSDeviceFeatures {

	final private ICSDevice device;

	private boolean supportsColorSpace = true;

	private boolean supportsExtendedState = true;

	private boolean supportsFont = true;

	private boolean supportsInlineImage = true;

	private boolean supportsPattern = true;

	private boolean supportsProperties = true;

	private boolean supportsShading = true;

	private boolean supportsXObject = true;

	public CSDeviceFilter(ICSDevice device) {
		this.device = device;
		if (device instanceof ICSDeviceFeatures) {
			supportsColorSpace = ((ICSDeviceFeatures) device)
					.supportsColorSpace();
			supportsExtendedState = ((ICSDeviceFeatures) device)
					.supportsExtendedState();
			supportsFont = ((ICSDeviceFeatures) device).supportsFont();
			supportsInlineImage = ((ICSDeviceFeatures) device)
					.supportsInlineImage();
			supportsPattern = ((ICSDeviceFeatures) device).supportsPattern();
			supportsProperties = ((ICSDeviceFeatures) device)
					.supportsProperties();
			supportsShading = ((ICSDeviceFeatures) device).supportsShading();
			supportsXObject = ((ICSDeviceFeatures) device).supportsXObject();
		}
	}

	public void close() {
		getDevice().close();
	}

	public void compatibilityBegin() {
		getDevice().compatibilityBegin();
	}

	public void compatibilityEnd() {
		getDevice().compatibilityEnd();
	}

	protected void doForm(COSName name, PDForm form) throws CSException {
		saveState();
		try {
			CDSMatrix m = form.getMatrix();
			if (m != null) {
				transform(m.getA(), m.getB(), m.getC(), m.getD(), m.getE(), m
						.getF());
			}
			CDSRectangle r = form.getBoundingBox();
			if (r != null) {
				// todo 1 clip
			}
			if (getInterpreter() != null) {
				getInterpreter().process(form.getContentStream(),
						form.getResources());
			}
		} finally {
			restoreState();
		}
	}

	protected void doImage(COSName name, PDImage image) throws CSException {
		getDevice().doXObject(name, image);
	}

	protected void doPostScript(COSName name, PDPostScript postscript)
			throws CSException {
		// ignore
	}

	public void doShading(COSName resourceName, PDShading shading) {
		getDevice().doShading(resourceName, shading);
	}

	public void doXObject(COSName name, PDXObject xobject) {
		if (xobject == null) {
			return;
		}
		if (xobject.isForm()) {
			try {
				doForm(name, (PDForm) xobject);
			} catch (CSException e) {
				throw e;
			} catch (Exception e) {
				throw new CSError("Unexpected error rendering form", e); //$NON-NLS-1$
			}
		} else if (xobject.isImage()) {
			try {
				doImage(name, (PDImage) xobject);
			} catch (CSException e) {
				throw e;
			} catch (Exception e) {
				throw new CSError("Unexpected error rendering image", e); //$NON-NLS-1$
			}
		} else if (xobject.isPostscript()) {
			try {
				doPostScript(name, (PDPostScript) xobject);
			} catch (CSException e) {
				throw e;
			} catch (Exception e) {
				throw new CSError("Unexpected error rendering postscript", e); //$NON-NLS-1$
			}
		} else {
			throw new CSNotSupported("unknown XObject type"); //$NON-NLS-1$
		}
	}

	public ICSDevice getDevice() {
		return device;
	}

	public GraphicsState getGraphicsState() {
		return getDevice().getGraphicsState();
	}

	public ICSInterpreter getInterpreter() {
		return getDevice().getInterpreter();
	}

	public void inlineImage(PDImage img) {
		getDevice().inlineImage(img);
	}

	public void markedContentBegin(COSName tag) {
		getDevice().markedContentBegin(tag);
	}

	public void markedContentBeginProperties(COSName tag, COSName resourceName,
			COSDictionary properties) {
		getDevice().markedContentBeginProperties(tag, resourceName, properties);
	}

	public void markedContentEnd() {
		getDevice().markedContentEnd();
	}

	public void markedContentPoint(COSName tag) {
		getDevice().markedContentPoint(tag);
	}

	public void markedContentPointProperties(COSName tag, COSName resourceName,
			COSDictionary properties) {
		getDevice().markedContentPointProperties(tag, resourceName, properties);
	}

	public void open(ICSInterpreter interpreter) {
		getDevice().open(interpreter);
	}

	public void pathClipEvenOdd() {
		getDevice().pathClipEvenOdd();
	}

	public void pathClipNonZero() {
		getDevice().pathClipNonZero();
	}

	public void pathClose() {
		getDevice().pathClose();
	}

	public void pathCloseFillStrokeEvenOdd() {
		getDevice().pathCloseFillStrokeEvenOdd();
	}

	public void pathCloseFillStrokeNonZero() {
		getDevice().pathCloseFillStrokeNonZero();
	}

	public void pathCloseStroke() {
		getDevice().pathCloseStroke();
	}

	public void pathEnd() {
		getDevice().pathEnd();
	}

	public void pathFillEvenOdd() {
		getDevice().pathFillEvenOdd();
	}

	public void pathFillNonZero() {
		getDevice().pathFillNonZero();
	}

	public void pathFillStrokeEvenOdd() {
		getDevice().pathFillStrokeEvenOdd();
	}

	public void pathFillStrokeNonZero() {
		getDevice().pathFillStrokeNonZero();
	}

	public void pathStroke() {
		getDevice().pathStroke();
	}

	public void penCurveToC(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		getDevice().penCurveToC(x1, y1, x2, y2, x3, y3);
	}

	public void penCurveToV(float x2, float y2, float x3, float y3) {
		getDevice().penCurveToV(x2, y2, x3, y3);
	}

	public void penCurveToY(float x1, float y1, float x3, float y3) {
		getDevice().penCurveToY(x1, y1, x3, y3);
	}

	public void penLineTo(float x, float y) {
		getDevice().penLineTo(x, y);
	}

	public void penMoveTo(float x, float y) {
		getDevice().penMoveTo(x, y);
	}

	public void penRectangle(float x, float y, float w, float h) {
		getDevice().penRectangle(x, y, w, h);
	}

	public void restoreState() {
		getDevice().restoreState();
	}

	public void saveState() {
		getDevice().saveState();
	}

	public void setExtendedState(COSName resourceName, PDExtGState gstate) {
		getDevice().setExtendedState(resourceName, gstate);
	}

	public void setFlatnessTolerance(float flatness) {
		getDevice().setFlatnessTolerance(flatness);
	}

	public void setLineCap(int capStyle) {
		getDevice().setLineCap(capStyle);
	}

	public void setLineDash(float[] pattern, float phase) {
		getDevice().setLineDash(pattern, phase);
	}

	public void setLineJoin(int joinStyle) {
		getDevice().setLineJoin(joinStyle);
	}

	public void setLineWidth(float lineWidth) {
		getDevice().setLineWidth(lineWidth);
	}

	public void setMiterLimit(float miterLimit) {
		getDevice().setMiterLimit(miterLimit);
	}

	public void setNonStrokeColorCMYK(float c, float m, float y, float k) {
		getDevice().setNonStrokeColorCMYK(c, m, y, k);
	}

	public void setNonStrokeColorGray(float gray) {
		getDevice().setNonStrokeColorGray(gray);
	}

	public void setNonStrokeColorRGB(float r, float g, float b) {
		getDevice().setNonStrokeColorRGB(r, g, b);
	}

	public void setNonStrokeColorSpace(COSName resourceName,
			PDColorSpace colorSpace) {
		getDevice().setNonStrokeColorSpace(resourceName, colorSpace);
	}

	public void setNonStrokeColorValues(float[] values) {
		getDevice().setNonStrokeColorValues(values);
	}

	public void setNonStrokeColorValues(float[] values, COSName resourceName,
			PDPattern pattern) {
		getDevice().setNonStrokeColorValues(values, resourceName, pattern);
	}

	public void setRenderingIntent(COSName intent) {
		getDevice().setRenderingIntent(intent);
	}

	public void setStrokeColorCMYK(float c, float m, float y, float k) {
		getDevice().setStrokeColorCMYK(c, m, y, k);
	}

	public void setStrokeColorGray(float gray) {
		getDevice().setStrokeColorGray(gray);
	}

	public void setStrokeColorRGB(float r, float g, float b) {
		getDevice().setStrokeColorRGB(r, g, b);
	}

	public void setStrokeColorSpace(COSName resourceName,
			PDColorSpace colorSpace) {
		getDevice().setStrokeColorSpace(resourceName, colorSpace);
	}

	public void setStrokeColorValues(float[] values) {
		getDevice().setStrokeColorValues(values);
	}

	public void setStrokeColorValues(float[] values, COSName resourceName,
			PDPattern pattern) {
		getDevice().setStrokeColorValues(values, resourceName, pattern);
	}

	public boolean supportsColorSpace() {
		return supportsColorSpace;
	}

	public boolean supportsExtendedState() {
		return supportsExtendedState;
	}

	public boolean supportsFont() {
		return supportsFont;
	}

	public boolean supportsInlineImage() {
		return supportsInlineImage;
	}

	public boolean supportsPattern() {
		return supportsPattern;
	}

	public boolean supportsProperties() {
		return supportsProperties;
	}

	public boolean supportsShading() {
		return supportsShading;
	}

	public boolean supportsXObject() {
		return supportsXObject;
	}

	public void textBegin() {
		getDevice().textBegin();
	}

	public void textEnd() {
		getDevice().textEnd();
	}

	public void textLineMove(float dx, float dy) {
		getDevice().textLineMove(dx, dy);
	}

	public void textLineNew() {
		getDevice().textLineNew();
	}

	public void textMove(float dx, float dy) {
		getDevice().textMove(dx, dy);
	}

	public void textMoveTo(float x, float y) {
		getDevice().textMoveTo(x, y);
	}

	public void textSetCharSpacing(float charSpacing) {
		getDevice().textSetCharSpacing(charSpacing);
	}

	public void textSetFont(COSName resourceName, PDFont font, float size) {
		getDevice().textSetFont(resourceName, font, size);
	}

	public void textSetHorizontalScaling(float scale) {
		getDevice().textSetHorizontalScaling(scale);
	}

	public void textSetLeading(float leading) {
		getDevice().textSetLeading(leading);
	}

	public void textSetRenderingMode(int renderingMode) {
		getDevice().textSetRenderingMode(renderingMode);
	}

	public void textSetRise(float rise) {
		getDevice().textSetRise(rise);
	}

	public void textSetTransform(float a, float b, float c, float d, float e,
			float f) {
		getDevice().textSetTransform(a, b, c, d, e, f);
	}

	public void textSetWordSpacing(float wordSpacing) {
		getDevice().textSetWordSpacing(wordSpacing);
	}

	public void textShow(byte[] text, int offset, int length) {
		getDevice().textShow(text, offset, length);
	}

	public void textShow(char[] chars, int offset, int length) {
		getDevice().textShow(chars, offset, length);
	}

	public void textShow(String text) {
		getDevice().textShow(text);
	}

	public void textT3SetGlyphWidth(float x, float y) {
		getDevice().textT3SetGlyphWidth(x, y);
	}

	public void textT3SetGlyphWidthBB(float x, float y, float llx, float lly,
			float urx, float ury) {
		getDevice().textT3SetGlyphWidthBB(x, y, llx, lly, urx, ury);
	}

	public void transform(float a, float b, float c, float d, float e, float f) {
		getDevice().transform(a, b, c, d, e, f);
	}

}
