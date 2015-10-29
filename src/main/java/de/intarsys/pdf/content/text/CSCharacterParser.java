/*
 * intarsys consulting GmbH
 * all rights reserved
 *
 */
package de.intarsys.pdf.content.text;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.CSTextDevice;
import de.intarsys.pdf.font.PDGlyphs;

/**
 * Collect the character objects in a content stream.
 */
abstract public class CSCharacterParser extends CSTextDevice {

	protected double lastStopX;

	protected double lastStopY;

	protected double lastStartX;

	protected double lastStartY;

	private Shape bounds;

	protected CSCharacterParser() {
		super();
	}

	@Override
	protected void basicTextShowGlyphs(PDGlyphs glyphs, float advance)
			throws CSException {
		AffineTransform tx;
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		lastStartX = tx.getTranslateX();
		lastStartY = tx.getTranslateY();
		// get the transformed character bounding box
		double glyphAscent = glyphs.getAscent();
		double glyphDescent = glyphs.getDescent();
		double ascent = (textState.fontSize * glyphAscent) / THOUSAND;
		double descent = (textState.fontSize * glyphDescent) / THOUSAND;
		if (descent > 0) {
			descent = -descent;
		}
		double[] pts = new double[] { 0, descent, advance, ascent };
		tx.deltaTransform(pts, 0, pts, 0, 2);
		//
		float x = (float) lastStartX;
		float y = (float) (lastStartY + pts[1]);
		float width = (float) pts[2];
		float height = (float) (pts[3] - pts[1]);
		if (width < 0) {
			x += width;
			width = -width;
		}
		if (height < 0) {
			y += height;
			height = -height;
		}
		Rectangle2D charRect = new Rectangle2D.Float(x, y, width, height);
		if (getBounds() == null || getBounds().intersects(charRect)) {
			onCharacterFound(glyphs, charRect);
		}
		// advance text matrix and store position for reference
		super.basicTextShowGlyphs(glyphs, advance);
		tx = (AffineTransform) getDeviceTransform().clone();
		tx.concatenate(textState.globalTransform);
		lastStopX = tx.getTranslateX();
		lastStopY = tx.getTranslateY();
	}

	protected void onCharacterFound(PDGlyphs glyphs, Rectangle2D rect) {
		// redefine
	}

	public Shape getBounds() {
		return bounds;
	}

	public void setBounds(Shape bounds) {
		this.bounds = bounds;
	}

}
