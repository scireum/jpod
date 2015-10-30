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

/**
 *
 */
public class PNGPaethPrediction extends PNGPrediction {

    public PNGPaethPrediction(COSDictionary options) {
        super(options);
    }

    @Override
    public void decodeRow(byte[] source, int sourceOffset, byte[] result, int resultOffset) {
        int raw;
        int left;
        int above;
        int upperLeft;
        int colors = getColors();

        if (getBitsPerComponent() != 8) {
            // TODO 2 @ehk implement
            return;
        }

        sourceOffset = sourceOffset + 1;

        if (sourceOffset == 1) {
            System.arraycopy(source, 1, result, resultOffset, colors);

            for (int x = 1; x < getResultRowSize(); x++) {
                raw = source[1 + x] & 0xff;
                left = result[(resultOffset + x) - colors] & 0xff;
                above = 0;
                upperLeft = 0;
                result[resultOffset + x] = (byte) ((raw + paethPredictor(left, above, upperLeft)) & 0xff);
            }
            return;
        }

        for (int c = 0; c < colors; c++) {
            raw = source[sourceOffset + c] & 0xff;
            left = 0;
            above = result[(resultOffset + c) - getResultRowSize()] & 0xff;
            upperLeft = 0;
            result[resultOffset + c] = (byte) ((raw + paethPredictor(left, above, upperLeft)) & 0xff);
        }

        for (int x = colors; x < getResultRowSize(); x++) {
            raw = source[sourceOffset + x] & 0xff;
            left = result[(resultOffset + x) - colors] & 0xff;
            above = result[(resultOffset + x) - getResultRowSize()] & 0xff;
            upperLeft = result[(resultOffset + x) - getResultRowSize() - colors] & 0xff;
            result[resultOffset + x] = (byte) ((raw + paethPredictor(left, above, upperLeft)) & 0xff);
        }
    }

    private int paethPredictor(int left, int above, int upperLeft) {
        int p = (left + above) - upperLeft; // initial estimate
        int pa = Math.abs(p - left); // distances to left, above, upper left
        int pb = Math.abs(p - above);
        int pc = Math.abs(p - upperLeft);

        // return nearest of a,b,c, breaking ties in order left, above, upper
        // left.
        if ((pa <= pb) && (pa <= pc)) {
            return left;
        }
        if (pb <= pc) {
            return above;
        }
        return upperLeft;
    }
}
