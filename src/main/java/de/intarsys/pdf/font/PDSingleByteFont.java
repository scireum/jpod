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

import de.intarsys.pdf.cos.COSObject;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

/**
 * A common superclass for the single byte encoded font flavors.
 */
public abstract class PDSingleByteFont extends PDFont {

    private PDGlyphs[] cachedGlyphs = new PDGlyphs[256];

    // an array for the width of each glyph used
    private int[] cachedWidths;

    protected PDSingleByteFont(COSObject object) {
        super(object);
    }

    @Override
    public PDGlyphs getGlyphsEncoded(int codepoint) {
        // we can access the cache directly as we expect a one byte codepoint
        PDGlyphs glyphs = cachedGlyphs[codepoint];
        if (glyphs == null) {
            glyphs = new PDGlyphs(this, codepoint);
            cachedGlyphs[codepoint] = glyphs;
        }
        return glyphs;
    }

    /**
     * The glyph width of a codepoint in the font. codepoint refers to the
     * encoded (possibly multibyte) value in the COSString.
     * <p>
     * In the standard case for single byte encoded fonts, the codepoint is the
     * index in the /Widths array, holding the glyph width.
     * <p>
     * For multibyte fonts, see {@link PDFontType0}.
     *
     * @param codepoint The codepoint
     * @return The glyph width of a codepoint in the font
     */
    @Override
    public int getGlyphWidthEncoded(int codepoint) {
        if (cachedWidths == null) {
            cachedWidths = createWidths();
        }
        if (codepoint < 0 || codepoint > cachedWidths.length) {
            return getMissingWidth();
        }
        return cachedWidths[codepoint];
    }

    /**
     * The array of glyph widths.
     *
     * @return The array of glyph widths.
     */
    public int[] getGlyphWidths() {
        if (cachedWidths == null) {
            cachedWidths = createWidths();
        }
        return cachedWidths;
    }

    @Override
    public PDGlyphs getNextGlyphsEncoded(ByteArrayInputStream is) {
        int codepoint = is.read();
        if (codepoint == -1) {
            return null;
        }
        return getGlyphsEncoded(codepoint);
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedWidths = null;
        Arrays.fill(cachedGlyphs, null);
    }
}
