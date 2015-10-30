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

import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDGlyphs;
import de.intarsys.tools.geometry.ApplySpaceChangeShape;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * A {@link ICSDevice} handling the complex semantics of the path and text
 * drawing operators.
 * <p>
 * As this are expensive operations, devices that do not rely on path and text
 * handling may override the respective methods to save performance.
 */
abstract public class CSBasicDevice extends CSDeviceAdapter {
    public static final float THOUSAND = 1000f;

    /**
     * Flag if clipping is requested by the current path creating operations
     */
    private boolean clip;

    /**
     * Flag which clipping mode is requested when clip is true;
     */
    private boolean clipEvenOdd;

    /**
     * The shape currently in construction by graphics operations.
     */
    private GeneralPath currentShape;

    private boolean currentShapeDegenerated;

    private boolean currentShapeEmpty;

    /**
     * The x coordinate of the current point in the active shape.
     * <p>
     * <p>
     * This is "cached" to ease life with bezier curves
     * </p>
     */
    private float currentX;

    /**
     * The y coordinate of the current point in the active shape.
     * <p>
     * <p>
     * This is "cached" to ease life with bezier curves
     * </p>
     */
    private float currentY;

    /**
     * The x coordinate of the initial point in the active subpath of the shape.
     * <p>
     * <p>
     * This is "cached" to ease life with bezier curves
     * </p>
     */
    private float initialX;

    /**
     * The y coordinate of the initial point in the active subpath of the shape.
     * <p>
     * <p>
     * This is "cached" to ease life with bezier curves
     * </p>
     */
    private float initialY;

    protected void basicClip(Shape shape) throws CSException {
        // override in subclass
    }

    protected void basicDraw(Shape shape) throws CSException {
        // override in subclass
    }

    protected void basicFill(Shape shape) throws CSException {
        // override in subclass
    }

    /**
     * This special addition to standard fill is called whenever we have a
     * "simple" shape (such as a line or a rectangle with a width < 1) and only
     * a fill operation is performed. Acrobat will "fill" the non existing area
     * and create a solid line - mimic this behavior.
     *
     * @param shape
     */
    protected void basicFillDegenerated(Shape shape) {
        //
    }

    protected void basicTextShowBegin() {
    }

    protected void basicTextShowEnd() {
    }

    protected void basicTextShowGlyphs(PDGlyphs glyphs, float advance) throws CSException {
        textMove(advance, 0f);
    }

    protected GeneralPath getCurrentShape() {
        return currentShape;
    }

    @Override
    public void open(ICSInterpreter interpreter) {
        super.open(interpreter);
        // provide an initial empty path - we found documents stroking
        // uninitialized paths ("... S S")
        currentShape = new GeneralPath();
        currentShapeDegenerated = true;
        currentShapeEmpty = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathClipEvenOdd()
     */
    @Override
    public void pathClipEvenOdd() {
        clip = true;
        clipEvenOdd = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathClipNonZero()
     */
    @Override
    public void pathClipNonZero() {
        clip = true;
        clipEvenOdd = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathClose()
     */
    @Override
    public void pathClose() {
        privateClosePath();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathCloseFillStrokeEvenOdd()
     */
    @Override
    public void pathCloseFillStrokeEvenOdd() {
        privateClosePath();
        currentShape.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        basicFill(currentShape);
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathCloseFillStrokeNonZero()
     */
    @Override
    public void pathCloseFillStrokeNonZero() {
        privateClosePath();
        currentShape.setWindingRule(GeneralPath.WIND_NON_ZERO);
        basicFill(currentShape);
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathCloseStroke()
     */
    @Override
    public void pathCloseStroke() {
        privateClosePath();
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathEnd()
     */
    @Override
    public void pathEnd() {
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathFillEvenOdd()
     */
    @Override
    public void pathFillEvenOdd() {
        currentShape.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        basicFill(currentShape);
        if (currentShapeDegenerated) {
            basicFillDegenerated(currentShape);
        }
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathFillNonZero()
     */
    @Override
    public void pathFillNonZero() {
        currentShape.setWindingRule(GeneralPath.WIND_NON_ZERO);
        basicFill(currentShape);
        if (currentShapeDegenerated) {
            basicFillDegenerated(currentShape);
        }
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathFillStrokeEvenOdd()
     */
    @Override
    public void pathFillStrokeEvenOdd() {
        currentShape.setWindingRule(GeneralPath.WIND_EVEN_ODD);
        basicFill(currentShape);
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathFillStrokeNonZero()
     */
    @Override
    public void pathFillStrokeNonZero() {
        currentShape.setWindingRule(GeneralPath.WIND_NON_ZERO);
        basicFill(currentShape);
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#pathStroke()
     */
    @Override
    public void pathStroke() {
        basicDraw(currentShape);
        privateClip();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penCurveToC(float, float,
     * float, float, float, float)
     */
    @Override
    public void penCurveToC(float x1, float y1, float x2, float y2, float x3, float y3) {
        if (currentShapeEmpty) {
            currentShapeEmpty = false;
        } else {
            currentShapeDegenerated = false;
        }
        currentShape.curveTo(x1, y1, x2, y2, x3, y3);
        currentX = x3;
        currentY = y3;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penCurveToV(float, float,
     * float, float)
     */
    @Override
    public void penCurveToV(float x2, float y2, float x3, float y3) {
        if (currentShapeEmpty) {
            currentShapeEmpty = false;
        } else {
            currentShapeDegenerated = false;
        }
        currentShape.curveTo(currentX, currentY, x2, y2, x3, y3);
        currentX = x3;
        currentY = y3;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penCurveToY(float, float,
     * float, float)
     */
    @Override
    public void penCurveToY(float x1, float y1, float x3, float y3) {
        if (currentShapeEmpty) {
            currentShapeEmpty = false;
        } else {
            currentShapeDegenerated = false;
        }
        currentShape.curveTo(x1, y1, x3, y3, x3, y3);
        currentX = x3;
        currentY = y3;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penLineTo(float, float)
     */
    @Override
    public void penLineTo(float x, float y) {
        if (currentShapeEmpty) {
            currentShapeEmpty = false;
        } else {
            currentShapeDegenerated = false;
        }
        currentShape.lineTo(x, y);
        currentX = x;
        currentY = y;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penMoveTo(float, float)
     */
    @Override
    public void penMoveTo(float x, float y) {
        clip = false;
        clipEvenOdd = false;
        currentShape.moveTo(x, y);
        initialX = x;
        initialY = y;
        currentX = x;
        currentY = y;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.CSDeviceAdapter#penRectangle(float, float,
     * float, float)
     */
    @Override
    public void penRectangle(float x, float y, float w, float h) {
        if (currentShapeEmpty) {
            currentShapeEmpty = false;
            currentShapeDegenerated = (w <= 1 && w >= -1) || (h <= 1 && h >= -1);
        } else {
            currentShapeDegenerated = false;
        }
        clip = false;
        clipEvenOdd = false;
        currentShape.moveTo(x, y);
        currentShape.lineTo(x + w, y);
        currentShape.lineTo(x + w, y + h);
        currentShape.lineTo(x, y + h);
        currentShape.closePath();
        initialX = x;
        initialY = y;
        currentX = x;
        currentY = y;
    }

    /**
     * Finalize the rendering of a path.
     */
    protected void privateClip() {
        if (clip) {
            if (clipEvenOdd) {
                currentShape.setWindingRule(GeneralPath.WIND_EVEN_ODD);
            } else {
                currentShape.setWindingRule(GeneralPath.WIND_NON_ZERO);
            }
            Shape shape;
            if (graphicsState.clip != null) {
                Area newShape = new Area(currentShape);
                Area intersection = new Area(graphicsState.clip);
                intersection.intersect(newShape);
                shape = intersection;
            } else {
                shape = currentShape;
            }
            graphicsState.clip = ApplySpaceChangeShape.create(shape, graphicsState.transform);
            basicClip(currentShape);
        }
        clip = false;
        currentShape = new GeneralPath();
        currentShapeDegenerated = true;
        currentShapeEmpty = true;
    }

    protected void privateClosePath() {
        currentShape.closePath();
        currentX = initialX;
        currentY = initialY;
    }

    @Override
    public void textShow(byte[] text, int offset, int length) {
        TextState ts = textState;
        if (ts.rise != 0) {
            // todo 1 text rise handling not correct
            textMove(0f, ts.rise);
        }
        PDFont font = ts.font;
        if (font == null) {
            // content stream error
            return;
        }
        float advanceFactor = ts.derivedGlyphAdvanceFactor;
        float charSpacing = ts.derivedCharSpacingScaled;
        float wordSpacing = ts.derivedWordSpacingScaled;
        ByteArrayInputStream is = new ByteArrayInputStream(text);
        basicTextShowBegin();
        while (true) {
            try {
                PDGlyphs glyphs = font.getNextGlyphsEncoded(is);
                if (glyphs == null) {
                    // all glyphs referenced by "text" consumed
                    break;
                }
                float advance = advanceFactor * glyphs.getWidth() + (wordSpacing != 0 && glyphs.isWhitespace() ?
                                                                     wordSpacing :
                                                                     0) + charSpacing;
                basicTextShowGlyphs(glyphs, advance);
            } catch (IOException e) {
                // ignore, no io exception on byte array
            }
        }
        basicTextShowEnd();
    }
}
