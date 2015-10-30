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

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;

/**
 * The representation of the glyphs for a codepoint in the {@link PDFont}.
 */
public class PDGlyphs implements IAttributeSupport {

    private final AttributeMap attributes = new AttributeMap();

    private final int codepoint;

    private final PDFont font;

    private Boolean whitespace;

    private float width;

    public PDGlyphs(PDFont font, int codepoint) {
        super();
        this.font = font;
        this.codepoint = codepoint;
        this.width = font.getGlyphWidthEncoded(codepoint);
    }

    public double getAscent() {
        return font.getFontDescriptor().getAscent();
    }

    @Override
    public final Object getAttribute(Object key) {
        return attributes.getAttribute(key);
    }

    public char[] getChars() {
        CMap toUnicode = font.getToUnicode();
        if (toUnicode == null) {
            return new char[]{(char) font.getEncoding().getDecoded(codepoint)};
        }
        return toUnicode.getChars(codepoint);
    }

    public int getCodepoint() {
        return codepoint;
    }

    public int getDecoded() {
        return font.getEncoding().getDecoded(codepoint);
    }

    public double getDescent() {
        return font.getFontDescriptor().getDescent();
    }

    public PDFont getFont() {
        return font;
    }

    public String getGlyphName() {
        return font.getEncoding().getGlyphName(codepoint);
    }

    public int getUnicode() {
        try {
            CMap toUnicode = font.getToUnicode();
            if (toUnicode == null) {
                return font.getEncoding().getDecoded(codepoint);
            }
            return toUnicode.getDecoded(codepoint);
        } catch (Exception ignored) {
            return font.getEncoding().getDecoded(codepoint);
        }
    }

    public float getWidth() {
        return width;
    }

    public boolean isWhitespace() {
        if (whitespace != null) {
            return whitespace;
        }
        int c = getDecoded();
        whitespace = c == 0 || c == 32;
        return whitespace;
    }

    @Override
    public final Object removeAttribute(Object key) {
        return attributes.removeAttribute(key);
    }

    public void reset() {
        this.width = font.getGlyphWidthEncoded(codepoint);
    }

    @Override
    public final Object setAttribute(Object key, Object o) {
        return attributes.setAttribute(key, o);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("glyph "); //$NON-NLS-1$
        sb.append((char) getUnicode());
        sb.append(", cp "); //$NON-NLS-1$
        sb.append(codepoint);
        sb.append(", uc "); //$NON-NLS-1$
        sb.append(getUnicode());
        return sb.toString();
    }
}
