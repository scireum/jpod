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
package de.intarsys.pdf.filter;

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;

import java.io.IOException;

/**
 *
 */
public abstract class Prediction implements IPrediction {
    public static final COSName DK_BitsPerComponent = COSName.constant("BitsPerComponent"); //$NON-NLS-1$

    public static final COSName DK_Colors = COSName.constant("Colors"); //$NON-NLS-1$

    public static final COSName DK_Columns = COSName.constant("Columns"); //$NON-NLS-1$

    public static final COSName DK_Predictor = COSName.constant("Predictor"); //$NON-NLS-1$

    public static final int None = 1;

    public static final int PNGAverage = 13;

    public static final int PNGNone = 10;

    public static final int PNGOptimum = 15;

    public static final int PNGPaeth = 14;

    public static final int PNGSub = 11;

    public static final int PNGUp = 12;

    public static final int TIFF = 2;

    private int bitsPerComponent;

    private int colors;

    private int columns;

    private int resultRowSize;

    private int sourceRowSize;

    protected Prediction(COSDictionary options) {
        super();

        COSInteger value;

        value = options.get(DK_BitsPerComponent).asInteger();
        if (value == null) {
            bitsPerComponent = 8;
        } else {
            bitsPerComponent = value.intValue();
        }
        value = options.get(DK_Colors).asInteger();
        if (value == null) {
            colors = 1;
        } else {
            colors = value.intValue();
        }
        value = options.get(DK_Columns).asInteger();
        if (value == null) {
            columns = 1;
        } else {
            columns = value.intValue();
        }

        resultRowSize = computeResultRowSize();
        sourceRowSize = computeSourceRowSize();
    }

    public int computeResultRowSize() {
        // assume that bitsPerComponent is 8, 4, 2 or 1
        return (int) Math.ceil(((double) colors * columns) / (8 / bitsPerComponent));
    }

    public int computeSourceRowSize() {
        return computeResultRowSize();
    }

    @Override
    public byte[] decode(byte[] source) throws IOException {
        int rows;
        byte[] result;

        rows = source.length / sourceRowSize;
        result = new byte[rows * resultRowSize];
        for (int index = 0; index < rows; index++) {
            decodeRow(source, sourceRowSize * index, result, resultRowSize * index);
        }
        return result;
    }

    protected abstract void decodeRow(byte[] source, int sourceOffset, byte[] result, int resultOffset)
            throws IOException;

    public int getBitsPerComponent() {
        return bitsPerComponent;
    }

    public int getColors() {
        return colors;
    }

    public int getColumns() {
        return columns;
    }

    public int getResultRowSize() {
        return resultRowSize;
    }

    public int getSourceRowSize() {
        return sourceRowSize;
    }
}
