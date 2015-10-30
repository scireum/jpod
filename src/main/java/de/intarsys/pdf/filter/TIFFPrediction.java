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

public class TIFFPrediction extends Prediction {
    public TIFFPrediction(COSDictionary options) {
        super(options);
    }

    protected void decodeRow(byte[] source, int sourceOffset, byte[] result, int resultOffset) {
        int colors;
        int columns;

        if (getBitsPerComponent() != 8) {
            // TODO 2 @ehk implement
            return;
        }

        colors = getColors();
        columns = getColumns();
        for (int index = 0; index < colors; index++) {
            result[resultOffset + index] = source[sourceOffset + index];
        }
        for (int byteIndex = 1; byteIndex < columns; byteIndex++) {
            for (int colorIndex = 0; colorIndex < colors; colorIndex++) {
                result[resultOffset + (byteIndex * colors) + colorIndex] =
                        (byte) (result[resultOffset + ((byteIndex - 1) * colors) + colorIndex] + source[sourceOffset + (
                                byteIndex
                                * colors) + colorIndex]);
            }
        }
    }

    public int getRowSize() {
        return getColumns() * getColors();
    }
}
