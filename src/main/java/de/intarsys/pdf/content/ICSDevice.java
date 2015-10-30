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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDResources;
import de.intarsys.pdf.pd.PDShading;
import de.intarsys.pdf.pd.PDXObject;

/**
 * The abstraction of a device that is manipulated by a PDF content stream
 * interpreter ({@link ICSInterpreter}) processing a content stream.
 * <p>
 * The device lifecycle is framed by open/close, issued by the
 * {@link ICSInterpreter}. The behavior of an {@link ICSDevice} that is not
 * open is undefined.
 * <p>
 * <p>
 * Any of the {@link ICSDevice} methods may throw a {@link CSException}. When
 * the {@link ICSDevice} is used in the context of an {@link ICSInterpreter},
 * the exception may be handled by the associated {@link ICSExceptionHandler},
 * otherwise you have to be prepared yourself.
 * <p>
 * Following a list of operator/method relationship. Not all operators may have
 * a corresponding method.
 * <p>
 * <code>
 * b                | pathCloseFillStrokeNonZero
 * B                | pathFillStrokeNonZero
 * b*                | patcCloseFillStrokeEvenOdd
 * B*                | pathFillStrokeEvenOdd
 * BDC                | markedContentBeginProperties
 * BI                | inlineImageBegin
 * BMC                | markedContentBegin
 * BT                | textBegin
 * BX                | compatibilityBegin
 * c                | penCurveToC
 * cm                | transform
 * CS                | setStrokeColorSpace
 * cs                | setNonStrokeColorSpace
 * d                | setLineDash
 * d0                | textT3SetGlyphWidth
 * d1                | textT3SetGlyphWidthBB
 * Do                | doXObject
 * DP                | markedContentPointProperties
 * EI                | inlineImageEnd
 * EMC                | markedContentEnd
 * ET                | textEnd
 * EX                | compatibilityEnd
 * f                | pathFillNonZero
 * f*                | pathFillEvenOdd
 * G                | setStrokeColorGray
 * g                | setNonStrokeColorGray
 * gs                | setExtendedState
 * h                | pathClose
 * i                | setFlatnessTolerance
 * ID                | inlineImageBeginData
 * j                | setLineJoin
 * J                | setLineCap
 * K                | setStrokeColorCMYK
 * k                | setNonStrokeColorCMYK
 * l                | penLineTo
 * m                | penMoveTo
 * M                | setMiterLimit
 * MP                | markedContentPoint
 * n                | pathEnd
 * q                | saveState
 * Q                | restoreState
 * re                | penRectangle
 * RG                | setStrokeColorRGB
 * rg                | setNonStrokeColorRGB
 * ri                | setRenderingIntent
 * s                | pathCloseStroke
 * S                | pathStroke
 * SC                | setStrokeColorValues
 * sc                | setNonStrokeColorValues
 * SCN                | setStrokeColorValuesSpecial
 * scn                | setNonStrokeColorValuesSpecial
 * sh                | paintFill
 * T*                | textLineNew
 * Tc                | textSetCharSpacing
 * Td                | textLineMove
 * TD                | use primitive methods
 * Tf                | textSetFont
 * Tj                | textShow
 * TJ                | textShow & textMove
 * TL                | textSetLeading
 * Tm                | textSetTransform
 * Tr                | textSetRenderingMode
 * Ts                | textSetRise
 * Tw                | textSetWordSpacing
 * Tz                | textSetHorizontalScaling
 * v                | penCurveToV
 * w                | setLineWidth
 * W                | pathClipNonZero
 * W*                | pathClipEvenOdd
 * y                | penCurveToY
 * '                | use primitive methods
 * "                | use primitive methods
 * </code>
 */
public interface ICSDevice {
    /**
     * Close the device after use.
     */
    public void close();

    /**
     * Begin a compatibility section.
     * <p>
     * PDF graphics operator "BX"
     */
    public void compatibilityBegin();

    /**
     * End a compatibility section.
     * <p>
     * PDF graphics operator "EX"
     */
    public void compatibilityEnd();

    /**
     * Paint shape and color shading according to shading dictionary.
     * <p>
     * PDF graphics operator "sh"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param shading      The {@link PDShading} to be painted.
     */
    public void doShading(COSName resourceName, PDShading shading);

    /**
     * Stroke a PDXObject. A PDXObject is a self contained graphical
     * description, either a form, an image or a postscript program.
     * <p>
     * <p>
     * PDF graphics operator "Do"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param xObject      The {@link PDXObject} to be stroked.
     */
    public void doXObject(COSName resourceName, PDXObject xObject);

    /**
     * The current {@link GraphicsState} active.
     * <p>
     * This is a read only representation of the graphics state used when
     * performing rendering operations in the device.
     *
     * @return The current {@link GraphicsState} active.
     */
    public GraphicsState getGraphicsState();

    /**
     * The {@link ICSInterpreter} associated with this {@link ICSDevice}. The
     * interpreter is associated with "open" and should be no longer used after
     * "close".
     *
     * @return The {@link ICSInterpreter} associated with this {@link ICSDevice}.
     */
    public ICSInterpreter getInterpreter();

    /**
     * Stroke an inlined image.
     * <p>
     * PDF graphics operators "BI", "ID", "EI"
     *
     * @param img The inlined image. The image may use some special keys instead
     *            of the standard {@link PDImage} dictionary keys.
     */
    public void inlineImage(PDImage img);

    /**
     * Begin a marked content sequence.
     * <p>
     * PDF graphics operator "BMC"
     *
     * @param tag The tag indicating the role or significance.
     */
    public void markedContentBegin(COSName tag);

    /**
     * Begin a marked content sequence with a property list.
     * <p>
     * PDF graphics operator "BDC"
     *
     * @param tag          The tag indicating the role or significance.
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param properties   The properties for the marked content sequence.
     */
    public void markedContentBeginProperties(COSName tag, COSName resourceName, COSDictionary properties);

    /**
     * End marked content sequence started with "BMC" or "BDC".
     * <p>
     * PDF graphics operator "EMC"
     */
    public void markedContentEnd();

    /**
     * Set a marked point.
     * <p>
     * PDF graphics operator "MP"
     *
     * @param tag The tag indicating the role or significance.
     */
    public void markedContentPoint(COSName tag);

    /**
     * Define a marked content point with a property list.
     * <p>
     * PDF graphics operator "DP"
     *
     * @param tag          The tag indicating the role or significance.
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param properties   The properties for the marked content point.
     */
    public void markedContentPointProperties(COSName tag, COSName resourceName, COSDictionary properties);

    /**
     * Open the device for use by <code>interpreter</code>.
     *
     * @param interpreter
     */
    public void open(ICSInterpreter interpreter);

    /**
     * Intersect the current clipping path with the current path using the
     * even/odd rule.
     * <p>
     * PDF graphics operator "W*"
     */
    public void pathClipEvenOdd();

    /**
     * Intersect the current clipping path with the current path using the
     * nonzero winding rule.
     * <p>
     * PDF graphics operator "W"
     */
    public void pathClipNonZero();

    /**
     * Close the path and append a line segment from the current coordinate to
     * the starting point of the path.
     * <p>
     * <p>
     * PDF graphics operator "h"
     * </p>
     */
    public void pathClose();

    /**
     * Close, Fill and then stroke the path using the even/odd rule.
     * <p>
     * PDF graphics operator "b*"
     */
    public void pathCloseFillStrokeEvenOdd();

    /**
     * Close, Fill and then stroke the path using the non zero winding rule.
     * <p>
     * PDF graphics operator "b"
     */
    public void pathCloseFillStrokeNonZero();

    /**
     * Close and then stroke the path.
     * <p>
     * PDF graphics operator "s"
     */
    public void pathCloseStroke();

    /**
     * End the path without filling or stroking.
     * <p>
     * <p>
     * This may for example be used to manipulate the clipping path without a
     * painting operation.
     * </p>
     * <p>
     * PDF graphics operator "n"
     */
    public void pathEnd();

    /**
     * Fill the path using the even/odd rule.
     * <p>
     * PDF graphics operator "f*"
     */
    public void pathFillEvenOdd();

    /**
     * Fill the path using the non-zero winding rule.
     * <p>
     * <p>
     * An open subpath is closed before filling.
     * </p>
     * <p>
     * PDF graphics operator "f"
     */
    public void pathFillNonZero();

    /**
     * Fill and then stroke the path using the even/odd rule.
     * <p>
     * PDF graphics operator "B*"
     */
    public void pathFillStrokeEvenOdd();

    /**
     * Fill and then stroke the path using the non-zero winding rule.
     * <p>
     * PDF graphics operator "B"
     */
    public void pathFillStrokeNonZero();

    /**
     * Stroke the current path.
     * <p>
     * PDF graphics operator "S"
     */
    public void pathStroke();

    /**
     * Append a cubic bezier curve to the path.<br>
     * The curve extends from the current point to x3, y3, where x1,y1 and x2,y2
     * are the bezier control points.
     * <p>
     * <code>
     * <p>
     * current
     * +---------* x1/y1
     * . _
     * -.
     * .       *x2/y2
     * .
     * .
     * .
     * + x3/y3
     * </code>
     * <p>
     * <p>
     * PDF graphics operator "c"
     * </p>
     *
     * @param x1 x coordinate of first control point
     * @param y1 y coordinate of first control point
     * @param x2 x coordinate of second control point
     * @param y2 y coordinate of second control point
     * @param x3 x coordinate of endpoint
     * @param y3 y coordinate of endpoint
     */
    public void penCurveToC(float x1, float y1, float x2, float y2, float x3, float y3);

    /**
     * Append a cubic bezier curve to the path. The curve extends from the
     * current point to x3, y3, where the first control point coincides with the
     * current point and x2,y2 is the second bezier control point.
     * <p>
     * <code>
     * <p>
     * current
     * +
     * . _
     * -.
     * .       *x2/y2
     * .
     * .
     * .
     * + x3/y3
     * </code>
     * <p>
     * <p>
     * PDF graphics operator "v"
     * </p>
     *
     * @param x2 x coordinate of second control point
     * @param y2 y coordinate of second control point
     * @param x3 x coordinate of endpoint
     * @param y3 y coordinate of endpoint
     */
    public void penCurveToV(float x2, float y2, float x3, float y3);

    /**
     * Append a cubic bezier curve to the path.
     * <p>
     * The curve extends from the current point to x3, y3, where x1,y1 and x3,y3
     * are the bezier control points.
     * <p>
     * <code>
     * <p>
     * current
     * +---------* x1/y1
     * . _
     * -.
     * .
     * .
     * .
     * .
     * + x3/y3
     * </code>
     * <p>
     * <p>
     * <p>
     * PDF graphics operator "y"
     * </p>
     *
     * @param x1 x coordinate of first control point
     * @param y1 y coordinate of first control point
     * @param x3 x coordinate of endpoint
     * @param y3 y coordinate of endpoint
     */
    public void penCurveToY(float x1, float y1, float x3, float y3);

    /**
     * Add a line from the current point to <code>x</code>, <code>y</code>.
     * The new current point is <code>x</code>, <code>y</code>.
     * <p>
     * <p>
     * PDF graphics operator "l"
     * </p>
     *
     * @param x The new current x coordinate
     * @param y The new current y coordinate
     */
    public void penLineTo(float x, float y);

    /**
     * Move the current point to <code>x</code>, <code>y</code>. No line
     * is added to the path, a new subpath is started.
     * <p>
     * <p>
     * PDF graphics operator "m"
     * </p>
     *
     * @param x The new current x coordinate
     * @param y The new current y coordinate
     */
    public void penMoveTo(float x, float y);

    /**
     * Append a complete rectangle to as a subpath.
     * <p>
     * <p>
     * The lower left corner is at <code>x</code>, <code>y</code>, the
     * dimensions are <code>width</code> and <code>height</code>. The
     * numbers are defined in user space.
     * </p>
     * <p>
     * <p>
     * PDF graphics operator "re"
     * </p>
     *
     * @param x The x coordinate of the lower left corner in user space
     * @param y The y coordinate of the lower left corner in user space
     * @param w The width in user space
     * @param h The height in user space
     */
    public void penRectangle(float x, float y, float w, float h);

    /**
     * Restore the graphics state from the stack.
     * <p>
     * PDF graphics operator "Q"
     */
    public void restoreState();

    /**
     * Save the current graphics state on a stack for later use.
     * <p>
     * PDF graphics operator "q"
     */
    public void saveState();

    /**
     * Set the dictionary as the new graphic state, creating a new
     * {@link PDResources} entry if needed.
     * <p>
     * PDF graphics operator "gs"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param gstate       The new {@link PDExtGState}
     */
    public void setExtendedState(COSName resourceName, PDExtGState gstate);

    /**
     * Set the flatness tolerance. <code>flatness</code> is a value between 0
     * and 100, with 0 defining the device's default flatness tolerance.
     * <p>
     * PDF graphics operator "i"
     *
     * @param flatness The flatness tolerance between 0 and 100.
     */
    public void setFlatnessTolerance(float flatness);

    /**
     * The line cap specifies the shape to be used at the ends of open subpaths.
     * <p>
     * <code>
     * 0: Butt. The stroke is cut at the endpoint.
     * 1: Round. A circle is drawn with the diamter of the line width at the endpoint
     * 2: Square. A square is drawn with its center at the endpoint.
     * </code>
     * <p>
     * PDF graphics operator "J"
     *
     * @param capStyle The line cap style to use (0,1 or 2)
     */
    public void setLineCap(int capStyle);

    /**
     * Define the pattern used to stroke paths. <code>unitsOn</code> defines a
     * length in user space where the line is drawn, <code>unitsOff</code>
     * defines a length in user space wher the line is not drawn.
     * <code>phase</code> defines a "offset" in the pattern definition.
     * <p>
     * <p>
     * This is a simplified version that only allows for a two phase pattern.
     * </p>
     * <p>
     * PDF graphics operator "d"
     *
     * @param pattern The pattern array for the dash
     * @param phase   Offset in pattern
     */
    public void setLineDash(float[] pattern, float phase);

    /**
     * The line join specifies the shape to be used at the connection points of
     * two adjacent lines in a path.
     * <p>
     * <code>
     * 0: Miter Join, the outer line boreders are extended until they meet.
     * 1: Round join. A circle is drawn at the meeting point with its
     * diameter the same as the line width.
     * 2: Bevel join. The segments are cut at the endpoints as in the line cap
     * style &quot;Butt&quot;. The empty triangle is filled.
     * </code>
     * <p>
     * PDF graphics operator "j"
     *
     * @param joinStyle The line join style to use (one of 0,1,2)
     */
    public void setLineJoin(int joinStyle);

    /**
     * Set the thickness of the line used to stroke a path. This is a number in
     * user space units.
     * <p>
     * <p>
     * A width of zero denotes the thinest line that can be rendered.
     * </p>
     * <p>
     * PDF graphics operator "w"
     *
     * @param lineWidth The line width in user space.
     */
    public void setLineWidth(float lineWidth);

    /**
     * The maximum ratio of MiterLength/LineWidth when connecting two lines with
     * miter style.
     * <p>
     * PDF graphics operator "M"
     *
     * @param miterLimit The maximum ratio of MiterLength/LineWidth when connecting two
     *                   lines with miter style.
     */
    public void setMiterLimit(float miterLimit);

    /**
     * Set the non stroking color space to /DeviceCMYK and set the color values
     * <code>c</code>, <code>m</code>, <code>y</code>, <code>K</code>.
     *
     * @param c A number between 0 (minimum) and 1 (maximum)
     * @param m A number between 0 (minimum) and 1 (maximum)
     * @param y A number between 0 (minimum) and 1 (maximum)
     * @param k A number between 0 (minimum) and 1 (maximum)
     */
    public void setNonStrokeColorCMYK(float c, float m, float y, float k);

    /**
     * Set the non stroking color space to /DeviceGray and set the gray level to
     * <code>gray</code>.
     *
     * @param gray A number between 0 (black) and 1 (white)
     */
    public void setNonStrokeColorGray(float gray);

    /**
     * Set the non stroking color space to /DeviceRGB and set the color values
     * <code>r</code>, <code>g</code>, <code>b</code>.
     *
     * @param r A number between 0 (minimum) and 1 (maximum)
     * @param g A number between 0 (minimum) and 1 (maximum)
     * @param b A number between 0 (minimum) and 1 (maximum)
     */
    public void setNonStrokeColorRGB(float r, float g, float b);

    /**
     * Set color space for non-stroking.
     * <p>
     * PDF graphics operator "cs"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param colorSpace   The new {@link PDColorSpace}
     */
    public void setNonStrokeColorSpace(COSName resourceName, PDColorSpace colorSpace);

    /**
     * Set the color used for non stroking operations, dependent on the
     * currently selected color spaces.
     * <p>
     * PDF graphics operator "sc" or "scn", dependen on the active color space.
     *
     * @param values The color values
     */
    public void setNonStrokeColorValues(float[] values);

    /**
     * Set the color used for non stroking operations, dependent on the
     * currently selected special color spaces.
     * <p>
     * PDF graphics operator "scn"
     *
     * @param values       The color values.
     * @param resourceName An optional logical name of the resource in the
     *                     {@link PDResources}
     * @param pattern      An optional {@link PDPattern}
     */
    public void setNonStrokeColorValues(float[] values, COSName resourceName, PDPattern pattern);

    /**
     * Set the color rendering intent.
     * <p>
     * PDF graphics operator "ri"
     *
     * @param intent The name of the rendering intent.
     */
    public void setRenderingIntent(COSName intent);

    /**
     * Set the stroking color space to /DeviceCMYK and set the color values
     * <code>c</code>, <code>m</code>, <code>y</code>, <code>K</code>.
     *
     * @param c A number between 0 (minimum) and 1 (maximum)
     * @param m A number between 0 (minimum) and 1 (maximum)
     * @param y A number between 0 (minimum) and 1 (maximum)
     * @param k A number between 0 (minimum) and 1 (maximum)
     */
    public void setStrokeColorCMYK(float c, float m, float y, float k);

    /**
     * Set the stroking color space to /DeviceGray and set the gray level to
     * <code>gray</code>.
     *
     * @param gray A number between 0 (black) and 1 (white)
     */
    public void setStrokeColorGray(float gray);

    /**
     * Set the stroking color space to /DeviceRGB and set the color values
     * <code>r</code>, <code>g</code>, <code>b</code>.
     *
     * @param r A number between 0 (minimum) and 1 (maximum)
     * @param g A number between 0 (minimum) and 1 (maximum)
     * @param b A number between 0 (minimum) and 1 (maximum)
     */
    public void setStrokeColorRGB(float r, float g, float b);

    /**
     * Set color space for stroking.
     * <p>
     * PDF graphics operator "CS"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param colorSpace   The new {@link PDColorSpace}
     */
    public void setStrokeColorSpace(COSName resourceName, PDColorSpace colorSpace);

    /**
     * Set the color used for stroking operations, dependent on the currently
     * selected color spaces.
     * <p>
     * PDF graphics operator "SC" or "SCN", dependen on the active color space.
     *
     * @param values The color values.
     */
    public void setStrokeColorValues(float[] values);

    /**
     * Set the color used for stroking operations, dependent on the currently
     * selected special color spaces.
     * <p>
     * PDF graphics operator "SCN"
     *
     * @param values       The color values.
     * @param resourceName An optional logical name of the resource in the
     *                     {@link PDResources}
     * @param pattern      An optional {@link PDPattern}
     */
    public void setStrokeColorValues(float[] values, COSName resourceName, PDPattern pattern);

    /**
     * Begin text mode. User space and text space are initialized to be equal.
     * <p>
     * PDF graphics operator "BT"
     */
    public void textBegin();

    /**
     * End text mode. User space is reestablished.
     * <p>
     * PDF graphics operator "ET"
     */
    public void textEnd();

    /**
     * Move the current text line by <code>dx</code>, <code>dy</code>.
     * <p>
     * PDF graphics operator "Td"
     *
     * @param dx The x offset for the new glyph starting point from the last
     *           text line starting point.
     * @param dy The y offset for the new glyph starting point from the last
     *           text line starting point.
     */
    public void textLineMove(float dx, float dy);

    /**
     * Move the current position to a new line. <code>y</code>.
     * <p>
     * PDF graphics operator "T*"
     */
    public void textLineNew();

    /**
     * Move the current text cursor represented by the current text state
     * transform by <code>dx</code>, <code>dy</code>.
     * <p>
     * There is no graphics operator for this. It is implemented as a tool for
     * the ease of creating a content stream.
     *
     * @param dx The x offset for the new glyph starting point from the current
     *           text cursor position.
     * @param dy The x offset for the new glyph starting point from the current
     *           text cursor position.
     */
    public void textMove(float dx, float dy);

    /**
     * Move the current text cursor represented by the current text state
     * transform to <code>x</code>, <code>y</code>.
     * <p>
     * There is no graphics operator for this. It is implemented as a tool for
     * the ease of creating a content stream.
     *
     * @param x The x coordinate for the next glyph starting point .
     * @param y The y coordinate for the next glyph starting point .
     */
    public void textMoveTo(float x, float y);

    /**
     * Set the character spacing.
     * <p>
     * PDF graphics operator "Tc"
     *
     * @param charSpacing The character spacing
     */
    public void textSetCharSpacing(float charSpacing);

    /**
     * Set the current font and size.
     * <p>
     * PDF graphics operator "Tf"
     *
     * @param resourceName The logical name of the resource in the {@link PDResources}
     * @param font         The new {@link PDFont}
     * @param size         The new font size (scaling)
     */
    public void textSetFont(COSName resourceName, PDFont font, float size);

    /**
     * Set the horizontal scling factor.
     * <p>
     * PDF graphics operator "Tz"
     *
     * @param scale The new horizontal scaling factor.
     */
    public void textSetHorizontalScaling(float scale);

    /**
     * Set the text leading.
     * <p>
     * PDF graphics operator "TL"
     *
     * @param leading The new leading
     */
    public void textSetLeading(float leading);

    /**
     * Set the text rendering mode.
     * <p>
     * PDF graphics operator "Tr"
     *
     * @param renderingMode The new rendering mode.
     */
    public void textSetRenderingMode(int renderingMode);

    /**
     * Set the text rise.
     * <p>
     * PDF graphics operator "Ts"
     *
     * @param rise The new text rise (super/subscript) amount
     */
    public void textSetRise(float rise);

    /**
     * Set the text transformation matrix. Both the text matrix and the text
     * line matrix are set to the new values.
     * <p>
     * PDF graphics operator "Tm"
     *
     * @param a operand 1,1 in the matrix
     * @param b operand 1,2 in the matrix
     * @param c operand 2,1 in the matrix
     * @param d operand 2,2 in the matrix
     * @param e operand 3,1 in the matrix
     * @param f operand 3,2 in the matrix
     */
    public void textSetTransform(float a, float b, float c, float d, float e, float f);

    /**
     * Set the word spacing.
     * <p>
     * PDF graphics operator "Tw"
     *
     * @param wordSpacing The new word spacing.
     */
    public void textSetWordSpacing(float wordSpacing);

    /**
     * Show a sequence of bytes as text. The bytes are assumed to be encoded and
     * copied directly to the device.
     * <p>
     * PDF graphics operator "Tj"
     *
     * @param text   The bytes to be shown.
     * @param offset
     * @param length
     */
    public void textShow(byte[] text, int offset, int length);

    /**
     * Show a sequence of characters as text, using the current font encoding.
     * <p>
     * This is an optional operation from the viewpoint of an
     * {@link ICSInterpreter}. It is called only "manually" in content creation
     * devices. This method may throw an {@link UnsupportedOperationException}.
     * <p>
     * PDF graphics operator "Tj"
     *
     * @param chars  The chars to be shown.
     * @param offset
     * @param length
     */
    public void textShow(char[] chars, int offset, int length);

    /**
     * Show a string value as text, using the current font encoding.
     * <p>
     * This is an optional operation from the viewpoint of an
     * {@link ICSInterpreter}. It is called only "manually" in content creation
     * devices. This method may throw an {@link UnsupportedOperationException}.
     * <p>
     * PDF graphics operator "Tj"
     *
     * @param text The text value to be shown using the current fonts encoding.
     */
    public void textShow(String text);

    /**
     * Set the glyph width for a type 3 font.
     * <p>
     * PDF graphics operator "d0"
     *
     * @param x The glyph width
     * @param y must be 0
     */
    public void textT3SetGlyphWidth(float x, float y);

    /**
     * Set the glyph width and bounding box for a type 3 font.
     * <p>
     * PDF graphics operator "d1"
     *
     * @param x   The glyph width.
     * @param y   must be 0
     * @param llx lower left x of bounding box
     * @param lly lower left y of bounding box
     * @param urx upper right x of bounding box
     * @param ury upper right y of bounding box
     */
    public void textT3SetGlyphWidthBB(float x, float y, float llx, float lly, float urx, float ury);

    /**
     * Modify the current transformation matrix by concatenating the
     * transformations.
     * <p>
     * <p>
     * PDF graphics operator "cm"
     * </p>
     *
     * @param a operand 1,1 in the matrix
     * @param b operand 1,2 in the matrix
     * @param c operand 2,1 in the matrix
     * @param d operand 2,2 in the matrix
     * @param e operand 3,1 in the matrix
     * @param f operand 3,2 in the matrix
     */
    public void transform(float a, float b, float c, float d, float e, float f);
}
