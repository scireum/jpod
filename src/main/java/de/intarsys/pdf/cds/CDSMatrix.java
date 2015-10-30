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

import java.awt.geom.AffineTransform;

/**
 * The implementation of the pdf transformation matrix.
 * <p>
 * <p>
 * Each matrix is represented as an array.
 * <p>
 * <pre>
 *       The transformation matrix:
 *
 *                    +       +
 *                    | a b 0 |
 *                    | c d 0 |
 *                    | e f 1 |
 *                    +       +
 *
 *       is written as a COSArray in the form:
 *
 *                    [ a b c d e f ]
 *
 *       The identity transformation has the form:
 *
 *                    [ 1 0 0 1 0 0 ]
 * </pre>
 * <p>
 * </p>
 * <p>
 * <p>
 * Coordinates could be transformed by a matrix.
 * <p>
 * <pre>
 *       A coordinate transformation is defined as:
 *                                             +       +
 *                                             | a b 0 |
 *                    [ x' y' 1 ] = [ x y 1] * | c d 0 |
 *                                             | e f 1 |
 *                                             +       +
 *
 *       so that
 *
 *                      x' = x*a + y*c + e
 *                      y' = x*b + y*d + f
 * </pre>
 * <p>
 * </p>
 */
public class CDSMatrix extends CDSBase {
    /**
     * Static information needed for rotation: rotate 0 degress (identity
     * matrix)
     */
    public static float[] MATRIX_0 = {1, 0, 0, 1, 0, 0};

    /**
     * Static information needed for rotation: rotate 90 degrees
     */
    public static float[] MATRIX_90 = {0, 1, -1, 0, 0, 0};

    /**
     * Static information needed for rotation: rotate 180 degrees
     */
    public static float[] MATRIX_180 = {-1, 0, 0, -1, 0, 0};

    /**
     * Static information needed for rotation: rotate 270 degrees
     */
    public static float[] MATRIX_270 = {0, -1, 1, 0, 0, 0};

    /**
     * Create a {@link CDSMatrix} from an {@code array} holding the
     * transformation parameters.
     *
     * @param array The base {@link COSArray}
     * @return Create a {@link CDSMatrix} from {@code array}
     */
    public static CDSMatrix createFromCOS(COSArray array) {
        if (array == null) {
            return null;
        }
        CDSMatrix matrix = (CDSMatrix) array.getAttribute(CDSMatrix.class);
        if (matrix == null) {
            matrix = new CDSMatrix(array);
            array.setAttribute(CDSMatrix.class, matrix);
        }
        return matrix;
    }

    private float a;

    private float b;

    private float c;

    private float d;

    private float e;

    private float f;

    /**
     * CDSMatrix constructor.
     * <p>
     * Create a new identity matrix
     */
    public CDSMatrix() {
        super(COSArray.createWith(1, 0, 0, 1, 0, 0));
        a = 1;
        b = 0;
        c = 0;
        d = 1;
        e = 0;
        f = 0;
    }

    /**
     * Create a CDSMatrix based on the array in the parameter.
     *
     * @param newM The array defining the matrix.
     */
    protected CDSMatrix(COSArray newM) {
        super(newM);
        a = getA();
        b = getB();
        c = getC();
        d = getD();
        e = getE();
        f = getF();
    }

    /**
     * Create a CDSMatrix based on the values in the parameter.
     */
    public CDSMatrix(float a, float b, float c, float d, float e, float f) {
        super(COSArray.createWith(a, b, c, d, e, f));
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    /**
     * Create a CDSMatrix based on the array in the parameter. The array must
     * have 6 elements.
     *
     * @param data The array defining the matrix.
     */
    public CDSMatrix(float[] data) {
        super(COSArray.createWith(data[0], data[1], data[2], data[3], data[4], data[5]));
        a = data[0];
        b = data[1];
        c = data[2];
        d = data[3];
        e = data[4];
        f = data[5];
    }

    /**
     * Create a copy of the receiver
     *
     * @return a new copy of the receiver
     */
    public CDSMatrix copy() {
        return new CDSMatrix((COSArray) cosGetObject().copyShallow());
    }

    /**
     * Return the matrix element "a".
     *
     * @return The matrix element "a".
     */
    public float getA() {
        return ((COSNumber) cosGetArray().get(0)).floatValue();
    }

    /**
     * Return the matrix element "b".
     *
     * @return The matrix element "b".
     */
    public float getB() {
        return ((COSNumber) cosGetArray().get(1)).floatValue();
    }

    /**
     * Return the matrix element "c".
     *
     * @return The matrix element "c".
     */
    public float getC() {
        return ((COSNumber) cosGetArray().get(2)).floatValue();
    }

    /**
     * Return the matrix element "d".
     *
     * @return The matrix element "d".
     */
    public float getD() {
        return ((COSNumber) cosGetArray().get(3)).floatValue();
    }

    /**
     * Return the matrix element "e".
     *
     * @return The matrix element "e".
     */
    public float getE() {
        return ((COSNumber) cosGetArray().get(4)).floatValue();
    }

    /**
     * Return the matrix element "f".
     *
     * @return The matrix element "f".
     */
    public float getF() {
        return ((COSNumber) cosGetArray().get(5)).floatValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        a = getA();
        b = getB();
        c = getC();
        d = getD();
        e = getE();
        f = getF();
    }

    /**
     * Concatenate this transformation with a rotation transformation.
     *
     * @param angle Rotation angle in radians
     */
    public void rotate(float angle) {
        AffineTransform transform = toTransform();
        transform.rotate(angle);
        setTransformation(transform);
    }

    /**
     * Concatenate this transformation with a scaling transformation.
     *
     * @param sx The scale factor in x direction
     * @param sy The scale factor in y direction
     */
    public void scale(float sx, float sy) {
        setA(getA() * sx);
        setB(getB() * sx);
        setC(getC() * sy);
        setD(getD() * sy);
    }

    /**
     * Concatenate this transformation with a scaling transformation.
     *
     * @param v The scale factor.
     */
    public void scale(float[] v) {
        setA(getA() * v[0]);
        setB(getB() * v[0]);
        setC(getC() * v[1]);
        setD(getD() * v[1]);
    }

    /**
     * Set the matrix element "a".
     *
     * @param num The new matrix element "a".
     */
    public void setA(float num) {
        cosGetArray().set(0, COSFixed.create(num));
    }

    /**
     * Set the matrix element "b".
     *
     * @param num The new matrix element "b".
     */
    public void setB(float num) {
        cosGetArray().set(1, COSFixed.create(num));
    }

    /**
     * Set the matrix element "c".
     *
     * @param num The new matrix element "c".
     */
    public void setC(float num) {
        cosGetArray().set(2, COSFixed.create(num));
    }

    /**
     * Set the matrix element "d".
     *
     * @param num The new matrix element "d".
     */
    public void setD(float num) {
        cosGetArray().set(3, COSFixed.create(num));
    }

    /**
     * Set the matrix element "e".
     *
     * @param num The new matrix element "e".
     */
    public void setE(float num) {
        cosGetArray().set(4, COSFixed.create(num));
    }

    /**
     * Set the matrix element "f".
     *
     * @param num The new matrix element "f".
     */
    public void setF(float num) {
        cosGetArray().set(5, COSFixed.create(num));
    }

    /**
     * Set all matrix elements "a" - "f"
     *
     * @param transform The matrix defining the new parameters
     */
    public void setTransformation(AffineTransform transform) {
        double[] data = new double[6];
        transform.getMatrix(data);
        cosGetArray().set(0, COSFixed.create(data[0]));
        cosGetArray().set(1, COSFixed.create(data[1]));
        cosGetArray().set(2, COSFixed.create(data[2]));
        cosGetArray().set(3, COSFixed.create(data[3]));
        cosGetArray().set(4, COSFixed.create(data[4]));
        cosGetArray().set(5, COSFixed.create(data[5]));
    }

    /**
     * Set all matrix elements "a" - "f"
     *
     * @param a The new matrix element "a".
     * @param b The new matrix element "b".
     * @param c The new matrix element "c".
     * @param d The new matrix element "d".
     * @param e The new matrix element "e".
     * @param f The new matrix element "f".
     */
    public void setTransformation(float a, float b, float c, float d, float e, float f) {
        cosGetArray().set(0, COSFixed.create(a));
        cosGetArray().set(1, COSFixed.create(b));
        cosGetArray().set(2, COSFixed.create(c));
        cosGetArray().set(3, COSFixed.create(d));
        cosGetArray().set(4, COSFixed.create(e));
        cosGetArray().set(5, COSFixed.create(f));
    }

    /**
     * Set all matrix elements "a" - "f"
     *
     * @param data The array defining the new parameters
     */
    public void setTransformation(float[] data) {
        cosGetArray().set(0, COSFixed.create(data[0]));
        cosGetArray().set(1, COSFixed.create(data[1]));
        cosGetArray().set(2, COSFixed.create(data[2]));
        cosGetArray().set(3, COSFixed.create(data[3]));
        cosGetArray().set(4, COSFixed.create(data[4]));
        cosGetArray().set(5, COSFixed.create(data[5]));
    }

    /**
     * Create an {@link AffineTransform} that corresponds to this.
     *
     * @return Create an {@link AffineTransform} that corresponds to this.
     */
    public AffineTransform toTransform() {
        return new AffineTransform(a, b, c, d, e, f);
    }

    /**
     * Transform a vector {@code v} using this.
     *
     * @param v The vector that will be transformed.
     * @return The transformed vector.
     */
    public float[] transform(float[] v) {
        int len = v.length;
        float[] result = new float[len];
        int i = 0;
        int iinc = 1;
        while (iinc < len) {
            result[i] = (a * v[i]) + (c * v[iinc]) + e;
            result[iinc] = (b * v[i]) + (d * v[iinc]) + f;
            i += 2;
            iinc += 2;
        }
        return result;
    }

    /**
     * Concatenate this transformation with a translation transformation.
     *
     * @param x The translation in x direction
     * @param y The translation in y direction
     */
    public void translate(float x, float y) {
        setE(getE() + x * getA() + y * getB());
        setF(getF() + x * getC() + y * getD());
    }

    /**
     * Concatenate this transformation with a translation transformation.
     *
     * @param v The translation vector
     */
    public void translate(float[] v) {
        setE(getE() + v[0] * getA() + v[1] * getB());
        setF(getF() + v[0] * getC() + v[1] * getD());
    }
}
