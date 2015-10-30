/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */
package de.intarsys.pdf.content.text;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.intarsys.pdf.content.ICSInterpreter;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDGlyphs;

/**
 * A still very simple text extraction utility for PDF documents.
 */
public class CSTextExtractor extends CSCharacterParser {

	private StringBuilder content;

	private double maxDX = 5;

	private double maxDY = 5;

	public CSTextExtractor() {
		super();
	}

	private void append(char c) {
		if (c > 0) {
			content.append(c);
		} else {
			content.append(' ');
		}
	}

	private void append(char[] chars) {
		content.append(chars);
	}

	private void append(String s) {
		content.append(s);
	}

	public String getContent() {
		return content.toString();
	}

	@Override
	protected void onCharacterFound(PDGlyphs glyphs, Rectangle2D rect) {
		char[] chars = glyphs.getChars();
		if (chars == null) {
			chars = new char[] { ' ' };
		}

		double dX = lastStopX - lastStartX;
		double dY = lastStopY - lastStartY;
		if (Math.abs(dX) < maxDX) {
			if (Math.abs(dY) > maxDY && content.length() > 0) {
				append(System.getProperty("line.separator"));
			}
		} else {
			if (content.length() > 0) {
				if (Math.abs(dY) < maxDY) {
					append(" ");
				} else {
					append(System.getProperty("line.separator"));
				}
			}
		}
		append(chars);
	}

	@Override
	public void open(ICSInterpreter pInterpreter) {
		super.open(pInterpreter);
		content = new StringBuilder();
	}

	@Override
	public void textSetFont(COSName name, PDFont font, float size) {
		super.textSetFont(name, font, size);
		AffineTransform tx;
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		maxDX = textState.fontSize * 0.2 * tx.getScaleX();
		maxDY = textState.fontSize * 0.6 * tx.getScaleY();
	}

	@Override
	public void textSetTransform(float a, float b, float c, float d, float e,
			float f) {
		super.textSetTransform(a, b, c, d, e, f);
		AffineTransform tx;
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		maxDX = textState.fontSize * 0.2 * tx.getScaleX();
		maxDY = textState.fontSize * 0.6 * tx.getScaleY();
	}

}
