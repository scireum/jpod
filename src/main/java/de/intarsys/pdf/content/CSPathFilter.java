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
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDShading;

/**
 * Only path related operations will reach the device.
 * 
 */
public class CSPathFilter extends CSDeviceFilter {

	public CSPathFilter(ICSDevice device) {
		super(device);
	}

	@Override
	protected void doImage(COSName name, PDImage image) throws CSException {
		//
	}

	@Override
	public void doShading(COSName resourceName, PDShading shading) {
		//
	}

	@Override
	public void inlineImage(PDImage img) {
		//
	}

	@Override
	public void markedContentBegin(COSName tag) {
		//
	}

	@Override
	public void markedContentBeginProperties(COSName tag, COSName resourceName,
			COSDictionary properties) {
		//
	}

	@Override
	public void markedContentEnd() {
		//
	}

	@Override
	public void markedContentPoint(COSName tag) {
		//
	}

	@Override
	public void markedContentPointProperties(COSName tag, COSName resourceName,
			COSDictionary properties) {
		//
	}

	@Override
	public void textBegin() {
		//
	}

	@Override
	public void textEnd() {
		//
	}

	@Override
	public void textLineMove(float dx, float dy) {
		//
	}

	@Override
	public void textLineNew() {
		//
	}

	@Override
	public void textMove(float dx, float dy) {
		//
	}

	@Override
	public void textMoveTo(float x, float y) {
		//
	}

	@Override
	public void textSetCharSpacing(float charSpacing) {
		//
	}

	@Override
	public void textSetFont(COSName resourceName, PDFont font, float size) {
		//
	}

	@Override
	public void textSetHorizontalScaling(float scale) {
		//
	}

	@Override
	public void textSetLeading(float leading) {
		//
	}

	@Override
	public void textSetRenderingMode(int renderingMode) {
		//
	}

	@Override
	public void textSetRise(float rise) {
		//
	}

	@Override
	public void textSetTransform(float a, float b, float c, float d, float e,
			float f) {
		//
	}

	@Override
	public void textSetWordSpacing(float wordSpacing) {
		//
	}

	@Override
	public void textShow(byte[] text, int offset, int length) {
		//
	}

	@Override
	public void textShow(char[] chars, int offset, int length) {
		//
	}

	@Override
	public void textShow(String text) {
		//
	}

	@Override
	public void textT3SetGlyphWidth(float x, float y) {
		//
	}

	@Override
	public void textT3SetGlyphWidthBB(float x, float y, float llx, float lly,
			float urx, float ury) {
		//
	}

}
