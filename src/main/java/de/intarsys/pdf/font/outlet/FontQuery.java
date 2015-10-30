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
package de.intarsys.pdf.font.outlet;

import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontStyle;

/**
 * A query for requesting {@link PDFont} instances from an {@link IFontFactory}
 * with the defined attributes.
 */
public class FontQuery implements IFontQuery {
    /**
     * A template font defining defaults for the requested font.
     */
    private PDFont baseFont;

    /**
     * The font style requested
     */
    private PDFontStyle overrideFontStyle = PDFontStyle.UNDEFINED;
    /**
     * The font type requested
     */
    private String overrideFontType = null;

    /**
     * The font family requested
     */
    private String overrideFontFamilyName;
    /**
     * The font name requested
     */
    private String overrideFontName;

    /**
     * The encoding requested
     */
    private Encoding overrideEncoding;

    public FontQuery() {
        super();
    }

    /**
     * Create a new {@link IFontQuery} based on another {@link PDFont}. USe the
     * setter methods to overwrite the attributes you want to be different from
     * {@code baseFont}.
     *
     * @param baseFont The font serivng as a template for this query.
     */
    public FontQuery(PDFont baseFont) {
        super();
        this.baseFont = baseFont;
    }

    public FontQuery(String fontName) {
        super();
        setOverrideFontName(fontName);
    }

    /**
     * Create a new {@link IFontQuery} denoting a font from the
     * {@code family} in style {@code style}.
     *
     * @param family The family of the FontQuery.
     * @param style  The style of the FontQuery.
     */
    public FontQuery(String family, PDFontStyle style) {
        super();
        setOverrideFontFamilyName(family);
        setOverrideFontStyle(style);
    }

    /**
     * Create a new {@link IFontQuery} denoting a font from the
     * {@code family} in style {@code style}.
     *
     * @param family The family of the FontQuery.
     * @param style  The style of the FontQuery.
     */
    public FontQuery(String family, String style) {
        super();
        setOverrideFontFamilyName(family);
        setOverrideFontStyle(PDFontStyle.getFontStyle(style));
    }

    protected boolean checkLastChar() {
        return getBaseFont() != null;
    }

    protected PDFont getBaseFont() {
        return baseFont;
    }

    @Override
    public Encoding getEncoding() {
        if (getOverrideEncoding() == null) {
            if (getBaseFont() == null) {
                return null;
            } else {
                return getBaseFont().getEncoding();
            }
        } else {
            return getOverrideEncoding();
        }
    }

    @Override
    public String getFontFamilyName() {
        if (getOverrideFontFamilyName() == null) {
            if (getBaseFont() == null) {
                return null;
            } else {
                return getBaseFont().getLookupFontFamilyName();
            }
        } else {
            return getOverrideFontFamilyName();
        }
    }

    @Override
    public String getFontName() {
        if (getOverrideFontName() == null) {
            if (getBaseFont() == null || getOverrideFontFamilyName() == null || getOverrideFontStyle() == null) {
                return null;
            } else {
                return getBaseFont().getFontNameNormalized();
            }
        } else {
            return getOverrideFontName();
        }
    }

    @Override
    public PDFontStyle getFontStyle() {
        if (getOverrideFontStyle() == PDFontStyle.UNDEFINED) {
            if (getBaseFont() == null) {
                return PDFontStyle.UNDEFINED;
            } else {
                return getBaseFont().getLookupFontStyle();
            }
        } else {
            return getOverrideFontStyle();
        }
    }

    @Override
    public String getFontType() {
        if (getOverrideFontType() == null) {
            if (getBaseFont() == null) {
                return null;
            } else {
                return getBaseFont().getFontType();
            }
        } else {
            return getOverrideFontType();
        }
    }

    /**
     * The required encoding for the result font.
     *
     * @return The required encoding for the result font.
     */
    public Encoding getOverrideEncoding() {
        return overrideEncoding;
    }

    /**
     * The required font family for the result font.
     *
     * @return The required font family for the result font.
     */
    public String getOverrideFontFamilyName() {
        return overrideFontFamilyName;
    }

    public String getOverrideFontName() {
        return overrideFontName;
    }

    /**
     * The required font style for the result font.
     *
     * @return The required font style for the result font.
     */
    public PDFontStyle getOverrideFontStyle() {
        return overrideFontStyle;
    }

    public String getOverrideFontType() {
        return overrideFontType;
    }

    /**
     * Set the encoding attribute for the font to be looked up.
     *
     * @param overrideEncoding The required encoding for the result font.
     */
    public void setOverrideEncoding(Encoding overrideEncoding) {
        this.overrideEncoding = overrideEncoding;
    }

    /**
     * Set the font family attribute for the font to be looked up.
     *
     * @param overrideFontFamilyName The required font family for the result font.
     */
    public void setOverrideFontFamilyName(String overrideFontFamilyName) {
        this.overrideFontFamilyName = overrideFontFamilyName;
    }

    public void setOverrideFontName(String overrideFontName) {
        this.overrideFontName = overrideFontName;
    }

    /**
     * Set the font style attribute for the font to be looked up.
     *
     * @param overrideFontStyle The required font style for the result font.
     */
    public void setOverrideFontStyle(PDFontStyle overrideFontStyle) {
        this.overrideFontStyle = overrideFontStyle;
    }

    public void setOverrideFontType(String overrideFontType) {
        this.overrideFontType = overrideFontType;
    }
}
