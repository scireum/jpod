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
package de.intarsys.pdf.tools.kernel;

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.pd.PDPage;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Tool class for calculations regarding the PDF geometry.
 */
public class PDFGeometryTools {

    protected static final double RADIANS_MIN_270 = Math.toRadians(-270);
    protected static final double RADIANS_MIN_180 = Math.toRadians(-180);
    protected static final double RADIANS_MIN_90 = Math.toRadians(-90);

    /**
     * Given a device space transformation, apply the necessary transformation
     * steps to move the origin of the coordinate system to the lower left
     * corner of {@code rect} after rotating it clockwise by
     * {@code rotate}.
     * <p>
     * {@code transform} is modified
     *
     * @param transform
     * @param rotate
     * @param rect
     */
    public static void adjustTransform(AffineTransform transform, int rotate, Rectangle2D rect) {
        if (rotate == 0) {
            transform.translate(-rect.getMinX(), -rect.getMinY());
        } else if (rotate == 90) {
            transform.translate(-rect.getMinY(), rect.getMaxX());
            transform.rotate(RADIANS_MIN_90);
        } else if (rotate == 180) {
            transform.translate(rect.getMaxX(), rect.getMaxY());
            transform.rotate(RADIANS_MIN_180);
        } else if (rotate == 270) {
            transform.translate(rect.getMaxY(), -rect.getMinX());
            transform.rotate(RADIANS_MIN_270);
        } else {
            // this should not happen...
            transform.translate(-rect.getMinX(), -rect.getMinY());
        }
    }

    /**
     * Given a device space transformation, apply the necessary transformation
     * steps to move the origin of the coordinate system to the lower left
     * corner of {@code page}.
     * <p>
     * {@code transform} is modified
     *
     * @param transform
     * @param page
     */
    public static void adjustTransform(AffineTransform transform, PDPage page) {
        int rotate = PDFGeometryTools.normalizeRotate(page.getRotate());
        Rectangle2D rect = page.getCropBox().toNormalizedRectangle();
        adjustTransform(transform, rotate, rect);
    }

    /**
     * Normalize the rotation parameter to a positive multiple of 90 between 0
     * and 270.
     *
     * @param rotation
     * @return Normalize the rotation parameter to a positive multiple of 90
     * between 0 and 270.
     */
    public static int normalizeRotate(int rotation) {
        rotation = rotation % 360;
        if (rotation > 0) {
            return rotation - (rotation % 90);
        } else if (rotation == 0) {
            return 0;
        } else {
            rotation = 360 + rotation;
            return rotation - (rotation % 90);
        }
    }

    /**
     * Create the transformation of {@code rect} and return it.
     *
     * @param matrix The transformation to apply.
     * @param rect   The rectangle to be transformed. This is not changed.
     * @return The transformed rectangle
     */
    public static CDSRectangle transform(CDSMatrix matrix, CDSRectangle rect) {
        float[] vec = {rect.getLowerLeftX(), rect.getLowerLeftY(), rect.getUpperRightX(), rect.getUpperRightY()};
        float[] tVec = matrix.transform(vec);
        return new CDSRectangle(tVec);
    }

    /**
     * Tool class cannot be instantiated.
     */
    private PDFGeometryTools() {
    }
}
