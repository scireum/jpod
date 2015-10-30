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
package de.intarsys.pdf.cds;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSNumber;

import java.awt.geom.Rectangle2D;

/**
 * The implementation of the pdf rectangle data type.
 * <p>
 * <p>
 * The specification of the data type is found in [PDF} chapter 3.8.3.
 * </p>
 * <p>
 * <p>
 * <p>
 * <pre>
 *              A rectangle is defined by
 *                   [llx, lly, urx, ury ]
 *              where
 *                   llx = lower left x coordinate
 *                   lly = lower left y coordinate
 *                   urx = upper right x coordinate
 *                   ury = upper right y coordinate
 *
 *                          urx
 *                           |
 *                           v
 *                     +-----+  &lt;-ury
 *                     |     |
 *              lly -&gt; +-----+
 *                     &circ;
 *                     |
 *                    llx
 *
 *
 * </pre>
 * <p>
 * An application should be prepared to get any two diagonally opposite corners
 * in the rectangle specification. Use "normalize()" to ensure a rectangle that
 * conforms to the above picture.
 * </p>
 */
public class CDSRectangle extends CDSBase {
    /**
     * Some common constants in rectangle
     */
	private static final float INCH_2_CM = 2.54f;

    private static final float A4_WIDTH = 21.0f;

    private static final float A4_HEIGHT = 29.7f;

    private static final float DPI = 72.0f;

	/*
     * paper sizes in mm A0 841 × 1189 B0 1000 × 1414 C0 17 × 1297 A1 594 × 841
	 * B1 707 × 1000 C1 648 × 917 A2 420 × 594 B2 500 × 707 C2 458 × 648 A3 297 ×
	 * 420 B3 353 × 500 C3 324 × 458 A4 210 × 297 B4 250 × 353 C4 229 × 324 A5
	 * 148 × 210 B5 176 × 250 C5 162 × 229 A6 105 × 148 B6 125 × 176 C6 114 ×
	 * 162 A7 74 × 105 B7 88 × 125 C7 81 × 114 A8 52 × 74 B8 62 × 88 C8 57 × 81
	 * A9 37 × 52 B9 44 × 62 C9 40 × 57 A10 26 × 37 B10 31 × 44 C10 28 × 40
	 */

    // todo 4 add some common paper sizes
	public static final float[] SIZE_A4 = {0, 0, (A4_WIDTH / INCH_2_CM * DPI), (A4_HEIGHT / INCH_2_CM * DPI)};

    /**
     * Create a {@link CDSRectangle} from an {@code array} holding the
     * rectangle coordinates.
     *
     * @param array The base {@link COSArray}
     * @return Create a {@link CDSRectangle} from {@code array}
     */
	public static CDSRectangle createFromCOS(COSArray array) {
        if (array == null) {
            return null;
        }
        CDSRectangle rect = (CDSRectangle) array.getAttribute(CDSRectangle.class);
        if (rect == null) {
            rect = new CDSRectangle(array);
            array.setAttribute(CDSRectangle.class, rect);
        }
        return rect;
    }

    private Rectangle2D cachedRectangle;

    private Rectangle2D cachedNormalizedRectangle;

    /**
     * CDTRectangle constructor comment.
     */
    public CDSRectangle() {
        super(COSArray.createWith(0, 0, 0, 0));
    }

    /**
     * CDTRectangle constructor. Create a new rectangle with given array.
     *
     * @param newR A four dimensional COSArray defining llx,lly, urx, ury.
     */
    protected CDSRectangle(COSArray newR) {
        super(newR);
    }

    /**
     * CDTRectangle constructor. Create a new rectangle with given size.
     *
     * @param llx the lower left x
     * @param lly the lower left y
     * @param urx the upper right x
     * @param ury the upper right y
     */
    public CDSRectangle(float llx, float lly, float urx, float ury) {
        super(COSArray.createWith(llx, lly, urx, ury));
    }

    /**
     * CDTRectangle constructor. Create a new rectangle with given array. The
     * array must have 4 elements of type float.
     *
     * @param rectArray A four dimensional array defining llx,lly, urx, ury.
     * @see CDSRectangle#CDSRectangle(float llx, float lly, float urx, float
     * ury)
     */
    public CDSRectangle(float[] rectArray) {
        this(rectArray[0], rectArray[1], rectArray[2], rectArray[3]);
    }

    public CDSRectangle(Rectangle2D rect) {
        super(COSArray.createWith((float) rect.getMinX(),
                                  (float) rect.getMinY(),
                                  (float) rect.getMaxX(),
                                  (float) rect.getMaxY()));
    }

    /**
     * {@code true} if x/y lies within this.
     *
     * @param x x coordinate to be checked.
     * @param y y coordinate to be checked.
     * @return {@code true} if x/y lies within this.
     */
    public boolean contains(double x, double y) {
        COSArray array = cosGetArray();
        float x0 = ((COSNumber) array.get(0)).floatValue();
        float x1 = ((COSNumber) array.get(2)).floatValue();
        if (x0 < x1) {
            if ((x < x0) || (x > x1)) {
                return false;
            }
        } else {
            if ((x > x0) || (x < x1)) {
                return false;
            }
        }
        float y0 = ((COSNumber) array.get(1)).floatValue();
        float y1 = ((COSNumber) array.get(3)).floatValue();
        if (y0 < y1) {
            return ((y >= y0) && (y <= y1));
        } else {
            return ((y >= y1) && (y <= y0));
        }
    }

    /**
     * {@code true} if x/y lies within this, with a "uncertainty" of
     * epsilon.
     *
     * @param x       x coordinate to be checked.
     * @param y       y coordinate to be checked.
     * @param epsilon The allowed range of uncertainty
     * @return {@code true} if x/y lies within this.
     */
    public boolean contains(double x, double y, double epsilon) {
        COSArray array = cosGetArray();
        float x0 = ((COSNumber) array.get(0)).floatValue();
        float x1 = ((COSNumber) array.get(2)).floatValue();
        if (x0 < x1) {
            if ((x < (x0 - epsilon)) || (x > (x1 + epsilon))) {
                return false;
            }
        } else {
            if ((x > (x0 + epsilon)) || (x < (x1 - epsilon))) {
                return false;
            }
        }
        float y0 = ((COSNumber) array.get(1)).floatValue();
        float y1 = ((COSNumber) array.get(3)).floatValue();
        if (y0 < y1) {
            return ((y >= (y0 - epsilon)) && (y <= (y1 + epsilon)));
        } else {
            return ((y >= (y1 - epsilon)) && (y <= (y0 + epsilon)));
        }
    }

    /**
     * Create a copy of the receiver
     *
     * @return a new copy of the receiver
     */
    public CDSRectangle copy() {
        return new CDSRectangle((COSArray) cosGetArray().copyShallow());
    }

    /**
     * Return the height (an absolute value) of the rectangle.
     *
     * @return Return the height (an absolute value) of the rectangle.
     */
    public float getHeight() {
        return Math.abs(getUpperRightY() - getLowerLeftY());
    }

    /**
     * The lower left x coordinate.
     *
     * @return The lower left x coordinate.
     */
    public float getLowerLeftX() {
        return ((COSNumber) cosGetArray().get(0)).floatValue();
    }

    /**
     * The lower left y coordinate.
     *
     * @return The lower left y coordinate.
     */
    public float getLowerLeftY() {
        return ((COSNumber) cosGetArray().get(1)).floatValue();
    }

    /**
     * The upper right x coordinate.
     *
     * @return The upper right x coordinate.
     */
    public float getUpperRightX() {
        return ((COSNumber) cosGetArray().get(2)).floatValue();
    }

    /**
     * The upper right y coordinate.
     *
     * @return The upper right y coordinate.
     */
    public float getUpperRightY() {
        return ((COSNumber) cosGetArray().get(3)).floatValue();
    }

    /**
     * Get the width (an absolute value) of the rectangle.
     *
     * @return Get the width (an absolute value) of the rectangle.
     */
    public float getWidth() {
        return Math.abs(getUpperRightX() - getLowerLeftX());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedNormalizedRectangle = null;
        cachedRectangle = null;
    }

    /**
     * Move the rectangle by a relative offset. The relationship of the opposite
     * corners is preserved by this method.
     *
     * @param dx The offset by which we move in x direction.
     * @param dy The offset by which we move in y direction.
     */
    public void move(float dx, float dy) {
        setLowerLeftX(getLowerLeftX() + dx);
        setLowerLeftY(getLowerLeftY() + dy);
        setUpperRightX(getUpperRightX() + dx);
        setUpperRightY(getUpperRightY() + dy);
    }

    /**
     * Move the rectangle to a new absolute position. The relationship of the
     * opposite corners is preserved by this method. The receiver is modified.
     *
     * @param x The new x position of the lower left corner.
     * @param y The new y position of the lower left corner.
     * @return {@code this}
     */
    public CDSRectangle moveTo(float x, float y) {
        float width = getUpperRightX() - getLowerLeftX();
        float height = getUpperRightY() - getLowerLeftY();
        setLowerLeftX(x);
        setLowerLeftY(y);
        setUpperRightX(width + x);
        setUpperRightY(height + y);
        return this;
    }

    /**
     * Adjust the corner coordinates so that lower left is really in the lower
     * left (this means returns the smallest coordinate values).
     * <p>
     * <p>
     * This method changes {@code this} in place!
     * </p>
     *
     * @return {@code this}
     */
    public CDSRectangle normalize() {
        float t1;
        float t2;
        if ((t1 = getLowerLeftX()) > (t2 = getUpperRightX())) {
            setLowerLeftX(t2);
            setUpperRightX(t1);
        }
        if ((t1 = getLowerLeftY()) > (t2 = getUpperRightY())) {
            setLowerLeftY(t2);
            setUpperRightY(t1);
        }
        return this;
    }

    /**
     * Resize the rectangle by moving the upper right corner.
     *
     * @param dx The distance we move the upper right x coordinate.
     * @param dy The distance we move the upper right y coordinate.
     */
    public void resize(float dx, float dy) {
        setUpperRightX(getUpperRightX() + dx);
        setUpperRightY(getUpperRightY() + dy);
    }

    /**
     * Resize the rectangle to a new width and heigth. The new width and heigth
     * are defined relative to the lower left corner as signed values.
     *
     * @param width  The new width of the rectangle.
     * @param height The new height of the rectangle.
     */
    public void resizeTo(float width, float height) {
        setUpperRightX(getLowerLeftX() + width);
        setUpperRightY(getLowerLeftY() + height);
    }

    /**
     * Set the corners of this.
     *
     * @param llx The lower left x coordinate
     * @param lly The lower left y coordinate
     * @param urx The upper right x coordinate
     * @param ury The upper right y coordinate
     */
    public void setCorners(float llx, float lly, float urx, float ury) {
        cosGetArray().set(0, COSFixed.create(llx));
        cosGetArray().set(1, COSFixed.create(lly));
        cosGetArray().set(2, COSFixed.create(urx));
        cosGetArray().set(3, COSFixed.create(ury));
    }

    /**
     * Set the height of this.
     *
     * @param height THe new height
     */
    public void setHeight(float height) {
        setUpperRightY(getLowerLeftY() + height);
    }

    /**
     * Set the lower left x coordinate.
     *
     * @param num The lower left x coordinate.
     */
    public void setLowerLeftX(float num) {
        cosGetArray().set(0, COSFixed.create(num));
    }

    /**
     * Set the lower left y coordinate.
     *
     * @param num The lower left y coordinate.
     */
    public void setLowerLeftY(float num) {
        cosGetArray().set(1, COSFixed.create(num));
    }

    /**
     * Set the upper right x coordinate.
     *
     * @param num The upper right x coordinate.
     */
    public void setUpperRightX(float num) {
        cosGetArray().set(2, COSFixed.create(num));
    }

    /**
     * Set the upper right y coordinate.
     *
     * @param num The upper right y coordinate.
     */
    public void setUpperRightY(float num) {
        cosGetArray().set(3, COSFixed.create(num));
    }

    /**
     * Set the width of this.
     *
     * @param width The new width.
     */
    public void setWidth(float width) {
        setUpperRightX(getLowerLeftX() + width);
    }

    public float[] toArray() {
        return new float[]{getLowerLeftX(), getLowerLeftY(), getUpperRightX(), getUpperRightY()};
    }

    /**
     * Construct a {@link Rectangle2D} object from the receiver. The rectangle
     * will be normalized before construction.
     *
     * @return The Rectangle2D created from the receiver.
     */
    public Rectangle2D toNormalizedRectangle() {
        if (cachedNormalizedRectangle == null) {
            float llx = getLowerLeftX();
            float lly = getLowerLeftY();
            float urx = getUpperRightX();
            float ury = getUpperRightY();
            float temp;
            if (llx > urx) {
                temp = llx;
                llx = urx;
                urx = temp;
            }
            if (lly > ury) {
                temp = lly;
                lly = ury;
                ury = temp;
            }
            cachedNormalizedRectangle = new Rectangle2D.Float(llx, lly, urx - llx, ury - lly);
        }
        return (Rectangle2D) cachedNormalizedRectangle.clone();
    }

    /**
     * Construct a {@link Rectangle2D} object from the receiver. The resulting
     * rectangle is not normalized, that means it may return a negative width or
     * height.
     *
     * @return The Rectangle2D created from the receiver.
     */
    public Rectangle2D toRectangle() {
        if (cachedRectangle == null) {
            float llx = getLowerLeftX();
            float lly = getLowerLeftY();
            float urx = getUpperRightX();
            float ury = getUpperRightY();
            cachedRectangle = new Rectangle2D.Float(llx, lly, urx - llx, ury - lly);
        }
        return (Rectangle2D) cachedRectangle.clone();
    }
}
