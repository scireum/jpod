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

public class PNGAveragePrediction extends PNGPrediction {

	public PNGAveragePrediction(COSDictionary options) {
		super(options);
	}

	@Override
	protected void decodeRow(byte[] source, int sourceOffset, byte[] result,
			int resultOffset) {
		// Average(x) + floor((Raw(x-bpp)+Prior(x))/2)
		int raw;

		// Average(x) + floor((Raw(x-bpp)+Prior(x))/2)
		int left;

		// Average(x) + floor((Raw(x-bpp)+Prior(x))/2)
		int above;
		int colors = getColors();
		if (getBitsPerComponent() != 8) {
			// TODO 2 @ehk implement
			return;
		}

		sourceOffset = sourceOffset + 1;

		if (sourceOffset == 1) {
			for (int c = 0; c < colors; c++) {
				result[resultOffset + c] = source[sourceOffset + c];
			}

			for (int x = 1; x < getResultRowSize(); x++) {
				raw = source[sourceOffset + x] & 0xff;
				left = result[(resultOffset + x) - colors] & 0xff;
				above = 0;
				result[resultOffset + x] = (byte) (raw + ((left + above) / 2));
			}
			return;
		}

		for (int c = 0; c < colors; c++) {
			raw = source[sourceOffset + c] & 0xff;
			left = 0;
			above = result[(resultOffset + c) - getResultRowSize()] & 0xff;
			result[resultOffset + c] = (byte) (raw + ((left + above) / 2));
		}

		for (int x = colors; x < getResultRowSize(); x++) {
			raw = source[sourceOffset + x] & 0xff;
			left = result[(resultOffset + x) - colors] & 0xff;
			above = result[(resultOffset + x) - getResultRowSize()] & 0xff;
			result[resultOffset + x] = (byte) (raw + ((left + above) / 2));
		}
	}

}
