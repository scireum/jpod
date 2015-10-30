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

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * A CID indexed font.
 * <p>
 * This is a wrapper around a Type 1 or TrueType Font that is indexed using
 * CID's.
 * <p>
 * This is a a subclass of PDFont only for implementation reasons.
 * 
 */
abstract public class CIDFont extends PDFont {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDFont.MetaClass {
		protected MetaClass(Class<?> instanceClass) {
			super(instanceClass);
		}
	}

	public static final COSName DK_CIDSystemInfo = COSName
			.constant("CIDSystemInfo"); //$NON-NLS-1$

	public static final COSName DK_DW = COSName.constant("DW"); //$NON-NLS-1$

	public static final COSName DK_W = COSName.constant("W"); //$NON-NLS-1$

	public static final COSName DK_DW2 = COSName.constant("DW2"); //$NON-NLS-1$

	public static final COSName DK_W2 = COSName.constant("W2"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private CIDWidthMap map = null;

	protected CIDFont(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.font.PDFont#createBuiltinFontDescriptor()
	 */
	@Override
	protected PDFontDescriptor createBuiltinFontDescriptor() {
		return null;
	}

	public CIDSystemInfo getCIDSystemInfo() {
		return (CIDSystemInfo) CIDSystemInfo.META
				.createFromCos(cosGetField(DK_CIDSystemInfo));
	}

	public CIDWidthMap getCIDWidthMap() {
		if (map == null) {
			map = (CIDWidthMap) CIDWidthMap.META
					.createFromCos(cosGetField(DK_W));
		}
		return map;
	}

	public int getDefaultGlyphWidth() {
		return getFieldInt(DK_DW, 1000);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.font.IFont#getFontStyle()
	 */
	@Override
	public PDFontStyle getFontStyle() {
		return PDFontStyle.REGULAR;
	}

	abstract public int getGlyphIndex(int cid);

	@Override
	public PDGlyphs getGlyphsEncoded(int codepoint) {
		throw new UnsupportedOperationException(
				"CIDFont can not be used directly");
	}

	public int getGlyphWidthCID(int cid) {
		CIDWidthMap map = getCIDWidthMap();
		if (map == null) {
			return getFieldInt(DK_DW, 1000);
		}
		int width = getCIDWidthMap().getWidth(cid);
		if (width == -1) {
			return getFieldInt(DK_DW, 1000);
		}
		return width;
	}

	@Override
	public int getGlyphWidthEncoded(int codepoint) {
		throw new UnsupportedOperationException(
				"CIDFont can not be used directly");
	}

	@Override
	public PDGlyphs getNextGlyphsEncoded(ByteArrayInputStream is) {
		throw new UnsupportedOperationException(
				"CIDFont can not be used directly");
	}

	public void setCIDSystemInfo(CIDSystemInfo info) {
		setFieldObject(DK_CIDSystemInfo, info);
	}

	public void setCIDWidthMap(CIDWidthMap map) {
		setFieldObject(DK_W, map);
	}

	public void setDefaultGlyphWidth(int value) {
		setFieldInt(DK_DW, value);
	}
}
