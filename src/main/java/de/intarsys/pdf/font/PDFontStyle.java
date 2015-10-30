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

public class PDFontStyle {
    /**
     * The number of font styles
     */
    public static final int COUNT = 4;

    /**
     * The enumeration of supported font styles
     */
    public static final PDFontStyle UNDEFINED = new PDFontStyle("?", -1); //$NON-NLS-1$

    public static final PDFontStyle REGULAR = new PDFontStyle("Regular", 0); //$NON-NLS-1$

    public static final PDFontStyle ITALIC = new PDFontStyle("Italic", 1); //$NON-NLS-1$

    public static final PDFontStyle BOLD = new PDFontStyle("Bold", 2); //$NON-NLS-1$

    public static final PDFontStyle BOLD_ITALIC = new PDFontStyle("BoldItalic", 3); //$NON-NLS-1$

    public static PDFontStyle getFontStyle(String name) {
        if (name == null) {
            return REGULAR;
        }
        name = name.trim().toLowerCase();
        boolean bold = false;
        boolean italic = false;
        if (name.contains("bold")) { //$NON-NLS-1$
            bold = true;
        }
        if (name.contains("italic")) { //$NON-NLS-1$
            italic = true;
        }
        if (name.contains("oblique")) { //$NON-NLS-1$
            italic = true;
        }
        if (bold) {
            if (italic) {
                return BOLD_ITALIC;
            } else {
                return BOLD;
            }
        } else {
            if (italic) {
                return ITALIC;
            } else {
                return REGULAR;
            }
        }
    }

    /**
     * The external representation of the font style
     */
    private final String label;

    private final int index;

    private PDFontStyle(String label, int index) {
        this.label = label;
        this.index = index;
    }

    public PDFontStyle getBoldFlavor() {
        if (this == PDFontStyle.ITALIC) {
            return PDFontStyle.BOLD_ITALIC;
        } else {
            return PDFontStyle.BOLD;
        }
    }

    protected int getIndex() {
        return index;
    }

    public PDFontStyle getItalicFlavor() {
        if (this == PDFontStyle.BOLD) {
            return PDFontStyle.BOLD_ITALIC;
        } else {
            return PDFontStyle.ITALIC;
        }
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return getLabel();
    }
}
