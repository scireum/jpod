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
package de.intarsys.pdf.font;

import de.intarsys.cwt.font.afm.AFM;
import de.intarsys.cwt.font.afm.AFMChar;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.encoding.GlyphNameMap;

/**
 * The builtin encoding of AFM defined fonts.
 */
public class AFMEncoding extends Encoding {
	//
	final private AFM afm;

	/**
	 * AFMEncoding constructor comment.
	 * 
	 * @param afm
	 *            The {@link AFM} object defining the encoding.
	 */
	public AFMEncoding(AFM afm) {
		super();
		this.afm = afm;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.intarsys.pdf.encoding.Encoding#getCosObject(de.intarsys.pdf.cos.
	 * COSDocument)
	 */
	@Override
	public COSObject cosGetObject() {
		return null;
	}

	/**
	 * Return the underlying Adobe font metrics.
	 * 
	 * @return Return the wrapped Adobe font metrics.
	 */
	public AFM getAfm() {
		return afm;
	}

	@Override
	public int getDecoded(int codepoint) {
		String glyphName = getGlyphName(codepoint);
		if (glyphName == null) {
			return -1;
		} else {
			return GlyphNameMap.Standard.getUnicode(glyphName);
		}
	}

	@Override
	public int getEncoded(int character) {
		String glyphName = GlyphNameMap.Standard.getGlyphName(character);
		return getEncoded(glyphName);
	}

	@Override
	public int getEncoded(String name) {
		AFMChar c = getAfm().getCharByName(name);
		if (c != null) {
			return c.getCode();
		}
		return -1;
	}

	@Override
	public String getGlyphName(int codePoint) {
		AFMChar c = getAfm().getCharByCode(codePoint);
		if (c != null) {
			return c.getName();
		}
		return GlyphNameMap.GLYPH_NOTDEF;
	}

	@Override
	public String getName() {
		return "AFMEncoding";
	}

}
