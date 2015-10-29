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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.encoding.CMapEncoding;
import de.intarsys.pdf.encoding.Encoding;

/**
 * A composite (Type 0) font.
 */
public class PDFontType0 extends PDFont {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDFont.MetaClass {
		protected MetaClass(Class<?> instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDFontType0(object);
		}
	}

	public static final COSName DK_DescendantFonts = COSName
			.constant("DescendantFonts"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private CMap cachedMap;

	private Map<Integer, PDGlyphs> cachedGlyphs = new HashMap<Integer, PDGlyphs>();

	public PDFontType0(COSObject object) {
		super(object);
	}

	@Override
	protected COSName cosGetExpectedSubtype() {
		return CN_Subtype_Type0;
	}

	/**
	 * Create the encoding for the font. The encoding is specified either "by
	 * default", as a known encoding name or a completely user defined
	 * difference encoding.
	 * <p>
	 * This is redefined for composite fonts, which use a different
	 * implementation.
	 * 
	 * @return The encoding object for the font.
	 * 
	 * @throws IllegalArgumentException
	 *             When the encoding defined in the font is not supported.
	 */
	@Override
	protected Encoding createEncoding() {
		CMap map = getCMap();
		return new CMapEncoding(map);
	}

	/**
	 * The {@link CMap} associated with the Type0 font. The CMap defines a
	 * mapping from code points to character selectors.
	 * 
	 * @return The {@link CMap} associated with the Type0 font.
	 */
	public CMap getCMap() {
		if (cachedMap == null) {
			cachedMap = (CMap) CMap.META
					.createFromCos(cosGetField(DK_Encoding));
		}
		return cachedMap;
	}

	/**
	 * The descendant font (font program) for the Type0 font.
	 * 
	 * @return The descendant font (font program) for the Type0 font.
	 */
	public CIDFont getDescendantFont() {
		COSArray fonts = cosGetField(DK_DescendantFonts).asArray();
		if ((fonts == null) || (fonts.size() == 0)) {
			return null;
		}
		COSObject font = fonts.get(0);
		return (CIDFont) PDFont.META.createFromCos(font);
	}

	@Override
	public PDFontDescriptor getFontDescriptor() {
		return getDescendantFont().getFontDescriptor();
	}

	@Override
	public String getFontFamilyName() {
		return getDescendantFont().getFontFamilyName();
	}

	@Override
	public String getFontName() {
		return getDescendantFont().getFontName();
	}

	@Override
	public String getFontNameNormalized() {
		return getDescendantFont().getFontNameNormalized();
	}

	@Override
	public PDFontStyle getFontStyle() {
		return getDescendantFont().getFontStyle();
	}

	@Override
	public String getFontType() {
		return getDescendantFont().getFontType();
	}

	public int getGlyphIndex(int cid) {
		return getDescendantFont().getGlyphIndex(cid);
	}

	@Override
	public PDGlyphs getGlyphsEncoded(int codepoint) {
		// this codepoint may be true integer?
		PDGlyphs glyphs = cachedGlyphs.get(codepoint);
		if (glyphs == null) {
			glyphs = new PDGlyphs(this, codepoint);
			cachedGlyphs.put(codepoint, glyphs);
		}
		return glyphs;
	}

	@Override
	public int getGlyphWidthEncoded(int codePoint) {
		int cid = getCMap().getDecoded(codePoint);
		if (cid == -1) {
			return 0;
		}
		return getDescendantFont().getGlyphWidthCID(cid);
	}

	@Override
	public PDGlyphs getNextGlyphsEncoded(ByteArrayInputStream is)
			throws IOException {
		// read as many bytes as defined in the cmap
		int codepoint = getCMap().getNextEncoded(is);
		if (codepoint == -1) {
			return null;
		}
		return getGlyphsEncoded(codepoint);
	}

	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		cosSetField(PDFont.DK_Encoding, IdentityCMap.CN_Identity_H);
	}

	public void setDescendantFont(CIDFont font) {
		if (font == null) {
			cosRemoveField(DK_DescendantFonts);
			return;
		}
		COSArray fonts = cosGetField(DK_DescendantFonts).asArray();
		if (fonts == null) {
			fonts = COSArray.create();
			cosSetField(DK_DescendantFonts, fonts);
		}
		if (fonts.size() == 0) {
			fonts.add(font.cosGetObject());
		} else {
			fonts.set(0, font.cosGetObject());
		}
	}
}
