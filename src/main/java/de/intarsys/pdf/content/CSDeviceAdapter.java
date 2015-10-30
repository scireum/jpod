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
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.pd.PDCSDeviceCMYK;
import de.intarsys.pdf.pd.PDCSDeviceGray;
import de.intarsys.pdf.pd.PDCSDeviceRGB;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.pdf.pd.PDForm;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDPostScript;
import de.intarsys.pdf.pd.PDShading;
import de.intarsys.pdf.pd.PDXObject;
import de.intarsys.tools.geometry.ApplySpaceChangeShape;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

/**
 * An adapter implementation for ICSDevice. This can be used as a simple
 * superclass for implementing PDF content stream aware devices.
 * <p>
 */
abstract public class CSDeviceAdapter implements ICSDevice, ICSDeviceFeatures {
    /**
     * the maximum number of nested states
     */
    public static int MAX_STACK_SIZE = 500;

    private AffineTransform deviceTransform = new AffineTransform();

    /**
     * The currently active {@link GraphicsState}.
     */
    protected GraphicsState graphicsState;

    private ICSInterpreter interpreter;

    /**
     * the {@link GraphicsState} stack.
     */
    private GraphicsState[] stack = new GraphicsState[MAX_STACK_SIZE];

    /**
     * The current stack pointer
     */
    private int stackPtr = 0;

    /**
     * A shortcut to the current text state
     */
    protected TextState textState;

    /**
     * Create a new {@link CSDeviceAdapter}
     */
    public CSDeviceAdapter() {
        //
    }

    protected void basicSetNonStrokeColorSpace(PDColorSpace colorSpace) {
        graphicsState.nonStrokeColorSpace = colorSpace;
        graphicsState.nonStrokeColorValues = null;
    }

    protected void basicSetNonStrokeColorValues(float[] values) {
        graphicsState.nonStrokeColorValues = values;
    }

    protected void basicSetStrokeColorSpace(PDColorSpace colorSpace) {
        graphicsState.strokeColorSpace = colorSpace;
        graphicsState.strokeColorValues = null;
    }

    protected void basicSetStrokeColorValues(float[] values) {
        graphicsState.strokeColorValues = values;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#close()
     */
    public void close() {
        this.interpreter = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#compatibilityBegin()
     */
    public void compatibilityBegin() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#compatibilityEnd()
     */
    public void compatibilityEnd() {
        // redefine
    }

    protected void doForm(COSName name, PDForm form) throws CSException {
        saveState();
        try {
            CDSMatrix m = form.getMatrix();
            if (m != null) {
                transform(m.getA(), m.getB(), m.getC(), m.getD(), m.getE(), m.getF());
            }
            CDSRectangle r = form.getBoundingBox();
            if (r != null) {
                Rectangle2D tempRect = r.toNormalizedRectangle();
                /*
				 * using the "operations" interface is not exactly right but
				 * don't have a "private" interface right now
				 */
                penRectangle((float) tempRect.getMinX(),
                             (float) tempRect.getMinY(),
                             (float) tempRect.getWidth(),
                             (float) tempRect.getHeight());
                pathClipNonZero();
                pathEnd();
            }
            if (interpreter != null) {
                interpreter.process(form.getContentStream(), form.getResources());
            }
        } finally {
            restoreState();
        }
    }

    protected void doImage(COSName name, PDImage image) throws CSException {
        // override in subclass
    }

    protected void doPostScript(COSName name, PDPostScript postscript) throws CSException {
        // override in subclass
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#doShading(de.intarsys.pdf.cos.COSName,
     * de.intarsys.pdf.pd.PDShading)
     */
    public void doShading(COSName name, PDShading shading) {
        // override in subclass
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#doXObject(de.intarsys.pdf.cos.COSName,
     * de.intarsys.pdf.pd.PDXObject)
     */
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

    public AffineTransform getDeviceTransform() {
        return deviceTransform;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#getGraphicsState()
     */
    public GraphicsState getGraphicsState() {
        return graphicsState;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#getInterpreter()
     */
    public ICSInterpreter getInterpreter() {
        return interpreter;
    }

    protected GraphicsState graphicsStateCopy(GraphicsState oldState) {
        return oldState.copy();
    }

    protected GraphicsState graphicsStateCreate() {
        return new GraphicsState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#inlineImage(de.intarsys.pdf.pd.PDImage)
     */
    public void inlineImage(PDImage img) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#markedContentBegin(de.intarsys.pdf.
     * cos.COSName)
     */
    public void markedContentBegin(COSName tag) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#markedContentBeginProperties(de.intarsys
     * .pdf.cos.COSName, de.intarsys.pdf.cos.COSName,
     * de.intarsys.pdf.cos.COSDictionary)
     */
    public void markedContentBeginProperties(COSName tag, COSName resourceName, COSDictionary properties) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#markedContentEnd()
     */
    public void markedContentEnd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#markedContentPoint(de.intarsys.pdf.
     * cos.COSName)
     */
    public void markedContentPoint(COSName tag) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#markedContentPointProperties(de.intarsys
     * .pdf.cos.COSName, de.intarsys.pdf.cos.COSName,
     * de.intarsys.pdf.cos.COSDictionary)
     */
    public void markedContentPointProperties(COSName tag, COSName resourceName, COSDictionary properties) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#open(de.intarsys.pdf.content.ICSInterpreter
     * )
     */
    public void open(ICSInterpreter pInterpreter) {
        this.interpreter = pInterpreter;
        this.graphicsState = graphicsStateCreate();
        this.textState = this.graphicsState.textState;
        // initial state
        basicSetNonStrokeColorSpace(PDCSDeviceGray.SINGLETON);
        basicSetStrokeColorSpace(PDCSDeviceGray.SINGLETON);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathClipEvenOdd()
     */
    public void pathClipEvenOdd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathClipNonZero()
     */
    public void pathClipNonZero() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathClose()
     */
    public void pathClose() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathCloseFillStrokeEvenOdd()
     */
    public void pathCloseFillStrokeEvenOdd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathCloseFillStrokeNonZero()
     */
    public void pathCloseFillStrokeNonZero() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathCloseStroke()
     */
    public void pathCloseStroke() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathEnd()
     */
    public void pathEnd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathFillEvenOdd()
     */
    public void pathFillEvenOdd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathFillNonZero()
     */
    public void pathFillNonZero() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathFillStrokeEvenOdd()
     */
    public void pathFillStrokeEvenOdd() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathFillStrokeNonZero()
     */
    public void pathFillStrokeNonZero() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#pathStroke()
     */
    public void pathStroke() {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penCurveToC(float, float, float,
     * float, float, float)
     */
    public void penCurveToC(float x1, float y1, float x2, float y2, float x3, float y3) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penCurveToV(float, float, float,
     * float)
     */
    public void penCurveToV(float x2, float y2, float x3, float y3) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penCurveToY(float, float, float,
     * float)
     */
    public void penCurveToY(float x1, float y1, float x3, float y3) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penLineTo(float, float)
     */
    public void penLineTo(float x, float y) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penMoveTo(float, float)
     */
    public void penMoveTo(float x, float y) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#penRectangle(float, float, float,
     * float)
     */
    public void penRectangle(float x, float y, float w, float h) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#restoreState()
     */
    public void restoreState() {
        if (stackPtr == 0) {
            throw new RuntimeException("stack underflow"); //$NON-NLS-1$
        }
        stackPtr--;
        graphicsState = stack[stackPtr];
        textState = graphicsState.textState;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#saveState()
     */
    public void saveState() {
        if (stackPtr == MAX_STACK_SIZE) {
            throw new RuntimeException("stack overflow"); //$NON-NLS-1$
        }
        stack[stackPtr++] = graphicsState;
        graphicsState = graphicsStateCopy(graphicsState);
        textState = graphicsState.textState;
    }

    public void setDeviceTransform(AffineTransform deviceTransform) {
        this.deviceTransform = deviceTransform;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#setExtendedState(de.intarsys.pdf.cos
     * .COSName, de.intarsys.pdf.pd.PDExtGState)
     */
    public void setExtendedState(COSName name, PDExtGState gstate) {
        if (gstate == null) {
            return;
        }
        if (graphicsState.extState == null) {
            graphicsState.extState = (PDExtGState) PDExtGState.META.createFromCos(gstate.cosGetObject().copyShallow());
        } else {
            graphicsState.extState.cosGetDict().addAll(gstate.cosGetDict());
        }
        graphicsState.capStyle = gstate.getFieldInt(PDExtGState.DK_LC, graphicsState.capStyle);
        COSArray cosDash = gstate.cosGetField(PDExtGState.DK_D).asArray();
        if (cosDash != null && cosDash.size() == 2) {
            COSArray cosPattern;
            COSNumber cosPhase;

            cosPattern = cosDash.get(0).asArray();
            cosPhase = cosDash.get(1).asNumber();
            if (cosPattern != null && cosPhase != null) {
                float[] pattern;

                pattern = new float[cosPattern.size()];
                for (int index = 0; index < cosPattern.size(); index++) {
                    COSNumber number = cosPattern.get(index).asNumber();
                    pattern[index] = number == null ? 0 : number.floatValue();
                }
                graphicsState.dashPattern = pattern;
                graphicsState.dashPhase = cosPhase.intValue();
            }
        }
        graphicsState.joinStyle = gstate.getFieldInt(PDExtGState.DK_LJ, graphicsState.joinStyle);
        setMiterLimit(gstate.getFieldFixed(PDExtGState.DK_ML, graphicsState.miterLimit));
        setLineWidth(gstate.getFieldFixed(PDExtGState.DK_LW, graphicsState.lineWidth));
        graphicsState.strokeAlphaValue = gstate.getFieldFixed(PDExtGState.DK_CA, graphicsState.strokeAlphaValue);
        graphicsState.nonStrokeAlphaValue = gstate.getFieldFixed(PDExtGState.DK_ca, graphicsState.nonStrokeAlphaValue);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setFlatnessTolerance(float)
     */
    public void setFlatnessTolerance(float flatness) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setLineCap(int)
     */
    public void setLineCap(int capStyle) {
        graphicsState.capStyle = capStyle;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setDash(float[], float)
     */
    public void setLineDash(float[] pattern, float phase) {
        graphicsState.dashPattern = pattern;
        graphicsState.dashPhase = phase;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setLineJoin(int)
     */
    public void setLineJoin(int joinStyle) {
        graphicsState.joinStyle = joinStyle;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setLineWidth(float)
     */
    public void setLineWidth(float lineWidth) {
        graphicsState.lineWidth = lineWidth;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setMiterLimit(float)
     */
    public void setMiterLimit(float miterLimit) {
        graphicsState.miterLimit = miterLimit;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setNonStrokeColorCMYK(float,
     * float, float, float)
     */
    public void setNonStrokeColorCMYK(float c, float m, float y, float k) {
        PDColorSpace colorSpace = PDCSDeviceCMYK.SINGLETON;
        if (graphicsState.nonStrokeColorSpace != colorSpace) {
            basicSetNonStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{c, m, y, k};
        if (!Arrays.equals(graphicsState.nonStrokeColorValues, values)) {
            basicSetNonStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setNonStrokeColorGray(float)
     */
    public void setNonStrokeColorGray(float gray) {
        PDColorSpace colorSpace = PDCSDeviceGray.SINGLETON;
        if (graphicsState.nonStrokeColorSpace != colorSpace) {
            basicSetNonStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{gray};
        if (!Arrays.equals(graphicsState.nonStrokeColorValues, values)) {
            basicSetNonStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setNonStrokeColorRGB(float, float,
     * float)
     */
    public void setNonStrokeColorRGB(float r, float g, float b) {
        PDColorSpace colorSpace = PDCSDeviceRGB.SINGLETON;
        if (graphicsState.nonStrokeColorSpace != colorSpace) {
            basicSetNonStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{r, g, b};
        if (!Arrays.equals(graphicsState.nonStrokeColorValues, values)) {
            basicSetNonStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#setNonStrokeColorSpace(de.intarsys.
     * pdf.cos.COSName, de.intarsys.pdf.pd.PDColorSpace)
     */
    public void setNonStrokeColorSpace(COSName name, PDColorSpace colorSpace) {
        // we are not allowed to optimize color space setting when not changed
        // side effect of setting is resetting the color to black
        basicSetNonStrokeColorSpace(colorSpace);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setNonStrokeColorValues(float[])
     */
    public void setNonStrokeColorValues(float[] values) {
        if (!Arrays.equals(graphicsState.nonStrokeColorValues, values)) {
            basicSetNonStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setNonStrokePatternValues(float[],
     * de.intarsys.pdf.pd.PDPattern)
     */
    public void setNonStrokeColorValues(float[] values, COSName name, PDPattern pattern) {
        // override in subclass
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#setRenderingIntent(de.intarsys.pdf.
     * cos.COSName)
     */
    public void setRenderingIntent(COSName intent) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setStrokeColorCMYK(float, float,
     * float, float)
     */
    public void setStrokeColorCMYK(float c, float m, float y, float k) {
        PDColorSpace colorSpace = PDCSDeviceCMYK.SINGLETON;
        if (graphicsState.strokeColorSpace != colorSpace) {
            basicSetStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{c, m, y, k};
        if (!Arrays.equals(graphicsState.strokeColorValues, values)) {
            basicSetStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setStrokeColorGray(float)
     */
    public void setStrokeColorGray(float gray) {
        PDColorSpace colorSpace = PDCSDeviceGray.SINGLETON;
        if (graphicsState.strokeColorSpace != colorSpace) {
            basicSetStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{gray};
        if (!Arrays.equals(graphicsState.strokeColorValues, values)) {
            basicSetStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setStrokeColorRGB(float, float,
     * float)
     */
    public void setStrokeColorRGB(float r, float g, float b) {
        PDColorSpace colorSpace = PDCSDeviceRGB.SINGLETON;
        if (graphicsState.strokeColorSpace != colorSpace) {
            basicSetStrokeColorSpace(colorSpace);
        }
        float[] values = new float[]{r, g, b};
        if (!Arrays.equals(graphicsState.strokeColorValues, values)) {
            basicSetStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#setStrokeColorSpace(de.intarsys.pdf
     * .cos.COSName, de.intarsys.pdf.pd.PDColorSpace)
     */
    public void setStrokeColorSpace(COSName name, PDColorSpace colorSpace) {
        // we are not allowed to optimize color space setting when not changed
        // side effect of setting is resetting the color to black
        basicSetStrokeColorSpace(colorSpace);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setStrokeColorValues(float[])
     */
    public void setStrokeColorValues(float[] values) {
        if (!Arrays.equals(graphicsState.strokeColorValues, values)) {
            basicSetStrokeColorValues(values);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#setStrokeColorValues(float[],
     * de.intarsys.pdf.cos.COSName, de.intarsys.pdf.pd.PDPattern)
     */
    public void setStrokeColorValues(float[] values, COSName name, PDPattern pattern) {
        // override in subclass
    }

    public boolean supportsColorSpace() {
        return true;
    }

    public boolean supportsExtendedState() {
        return true;
    }

    public boolean supportsFont() {
        return true;
    }

    public boolean supportsInlineImage() {
        return true;
    }

    public boolean supportsPattern() {
        return true;
    }

    public boolean supportsProperties() {
        return true;
    }

    public boolean supportsShading() {
        return true;
    }

    public boolean supportsXObject() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textBegin()
     */
    public void textBegin() {
        // reset text line matrix
        textState.lineTransform.setTransform(1, 0, 0, 1, 0, 0);
        // reset text matrix
        textState.transform.setTransform(1, 0, 0, 1, 0, 0);
        // reset global matrix
        textState.globalTransform.setTransform(graphicsState.transform);
        textState.active = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textEnd()
     */
    public void textEnd() {
        textState.active = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textLineMove(float, float)
     */
    public void textLineMove(float dx, float dy) {
        // move text line
        textState.lineTransform.translate(dx, dy);
        // restart text matrix
        textState.transform.setTransform(textState.lineTransform);
        // restart global transformation
        textState.globalTransform.setTransform(graphicsState.transform);
        textState.globalTransform.concatenate(textState.transform);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textLineNew()
     */
    public void textLineNew() {
        // move text line
        textState.lineTransform.translate(0, textState.leading);
        // restart text matrix
        textState.transform.setTransform(textState.lineTransform);
        // restart global transformation
        textState.globalTransform.setTransform(graphicsState.transform);
        textState.globalTransform.concatenate(textState.transform);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textMove(float, float)
     */
    public void textMove(float dx, float dy) {
        // move
        textState.transform.translate(dx, dy);
        textState.globalTransform.translate(dx, dy);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textMoveTo(float, float)
     */
    public void textMoveTo(float x, float y) {
        // move to
        float dx = x - (float) textState.transform.getTranslateX();
        float dy = y - (float) textState.transform.getTranslateY();
        textMove(dx, dy);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetCharSpacing(float)
     */
    public void textSetCharSpacing(float charSpacing) {
        textState.charSpacing = charSpacing;
        // calculate derived values
        textState.derivedCharSpacingScaled = textState.charSpacing * textState.derivedHorizontalScalingFactor;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.ICSDevice#textSetFont(de.intarsys.pdf.cos.COSName
     * , de.intarsys.pdf.font.PDFont, float)
     */
    public void textSetFont(COSName name, PDFont font, float size) {
        textState.font = font;
        textState.fontSize = size;
        // calculate derived values
        textState.derivedGlyphAdvanceFactor = textState.fontSize / 1000f * textState.derivedHorizontalScalingFactor;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetHorizontalScaling(float)
     */
    public void textSetHorizontalScaling(float scaling) {
        textState.horizontalScaling = scaling;
        // calculate derived values
        textState.derivedHorizontalScalingFactor = textState.horizontalScaling / 100f;
        textState.derivedGlyphAdvanceFactor = textState.fontSize / 1000f * textState.derivedHorizontalScalingFactor;
        textState.derivedCharSpacingScaled = textState.charSpacing * textState.derivedHorizontalScalingFactor;
        textState.derivedWordSpacingScaled = textState.wordSpacing * textState.derivedHorizontalScalingFactor;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetLeading(float)
     */
    public void textSetLeading(float leading) {
        textState.leading = leading;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetRenderingMode(int)
     */
    public void textSetRenderingMode(int renderingMode) {
        textState.renderingMode = renderingMode;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetRise(float)
     */
    public void textSetRise(float rise) {
        textState.rise = rise;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetTransform(float, float,
     * float, float, float, float)
     */
    public void textSetTransform(float a, float b, float c, float d, float e, float f) {
        // reset text line matrix
        textState.lineTransform.setTransform(a, b, c, d, e, f);
        // reset text matrix
        textState.transform.setTransform(a, b, c, d, e, f);
        // restart global transformation
        textState.globalTransform.setTransform(graphicsState.transform);
        textState.globalTransform.concatenate(textState.transform);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textSetWordSpacing(float)
     */
    public void textSetWordSpacing(float wordSpacing) {
        textState.wordSpacing = wordSpacing;
        // calculate derived values
        textState.derivedWordSpacingScaled = textState.wordSpacing * textState.derivedHorizontalScalingFactor;
    }

    public void textShow(byte[] text, int offset, int length) {
        // override in subclass
    }

    public void textShow(char[] chars, int offset, int length) {
        // todo encoding may be not correct with symbolic true type
        byte[] bytes = textState.font.getEncoding().encode(chars, offset, length);
        textShow(bytes, 0, bytes.length);
    }

    public void textShow(String text) {
        // todo encoding may be not correct with symbolic true type
        byte[] bytes = textState.font.getEncoding().encode(text.toCharArray(), 0, text.length());
        textShow(bytes, 0, bytes.length);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textT3SetGlyphWidth(float, float)
     */
    public void textT3SetGlyphWidth(float x, float y) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#textT3SetGlyphWidthBB(float,
     * float, float, float, float, float)
     */
    public void textT3SetGlyphWidthBB(float x, float y, float llx, float lly, float urx, float ury) {
        // redefine
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.content.ICSDevice#transform(float, float, float,
     * float, float, float)
     */
    public void transform(float a, float b, float c, float d, float e, float f) {
        AffineTransform transform = new AffineTransform(a, b, c, d, e, f);
        transform.preConcatenate(graphicsState.transform);
        graphicsState.transform = transform;
        // update clip shape
        if (graphicsState.clip != null) {
            graphicsState.clip = ApplySpaceChangeShape.setTransform(graphicsState.clip, graphicsState.transform);
        }
    }
}
