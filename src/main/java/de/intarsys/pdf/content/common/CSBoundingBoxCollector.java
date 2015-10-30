/*
 * intarsys consulting gmbh
 * all rights reserved
 *
 */

package de.intarsys.pdf.content.common;

import de.intarsys.pdf.content.CSBasicDevice;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDGlyphs;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.tools.geometry.GeometryTools;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * Determine the bounding box of the content streams graphic primitives.
 * <p>
 * Usage <br>
 * {@code
 * CSBoundingBoxCollector bbCollector = new CSBoundingBoxCollector();
 * CSDeviceBasedInterpreter interpreter = new CSDeviceBasedInterpreter(null, bbCollector);
 * interpreter.process(content, getResources());
 * if (bbCollector.getBoundingBox() != null) {
 * ...
 * }
 * }
 * </p>
 * ATTENTION: The {@link CSBoundingBoxCollector} does not take care of text yet !!
 */
public class CSBoundingBoxCollector extends CSBasicDevice {

    private Rectangle2D boundingBox;

    protected void addBoundingBox(Rectangle2D rect, boolean addLineWidth) {
        GeometryTools.normalizeRect(rect);
        if (addLineWidth) {
            double factor;
            factor = graphicsState.transform.getScaleX();
            factor = Math.max(graphicsState.transform.getScaleY(), factor);
            double border = graphicsState.lineWidth * factor + 5;
            rect.add(rect.getMinX() - border, rect.getMinY() - border);
            rect.add(rect.getMaxX() + border, rect.getMaxY() + border);
        }
        if (graphicsState.clip != null) {
            Area rectArea = new Area(rect);
            Area tempArea = new Area(graphicsState.clip);
            rectArea.intersect(tempArea);
            if (rectArea.isEmpty()) {
                return;
            }
            rect = rectArea.getBounds2D();
        }
        if (boundingBox == null) {
            boundingBox = (Rectangle2D) rect.clone();
        } else {
            boundingBox.add(rect);
        }
    }

    @Override
    protected void basicDraw(Shape shape) {
        Area area = new Area(shape.getBounds2D());
        area.transform(graphicsState.transform);
        addBoundingBox(area.getBounds2D(), true);
    }

    @Override
    protected void basicFill(Shape shape) {
        Area area = new Area(shape.getBounds2D());
        area.transform(graphicsState.transform);
        addBoundingBox(area.getBounds2D(), true);
    }

    @Override
    protected void basicTextShowGlyphs(PDGlyphs glyphs, float advance) {
        double factor = textState.fontSize / 1000f;
        float x = 0;
        float y = (float) (glyphs.getDescent() * factor);
        float width = (float) (glyphs.getWidth() * factor);
        float height = (float) (glyphs.getAscent() * factor);
        Rectangle2D rect = new Rectangle2D.Float(x, y, width, height);
        Area area = new Area(rect);
        area.transform(textState.globalTransform);
        addBoundingBox(area.getBounds2D(), true);
        super.basicTextShowGlyphs(glyphs, advance);
    }

    @Override
    protected void doImage(COSName name, PDImage image) {
        Area area = new Area(new Rectangle2D.Float(0, 0, 1, 1));
        area.transform(graphicsState.transform);
        addBoundingBox(area.getBounds2D(), false);
    }

    /**
     * The bounding box containing all graphics artifacts stemming from
     * operations in the content stream processed.
     * <p>
     * The result may be {@code null}.
     *
     * @return The bounding box containing all graphics artifacts stemming from
     * operations in the content stream processed.
     */
    public Rectangle2D getBoundingBox() {
        return boundingBox;
    }
}
