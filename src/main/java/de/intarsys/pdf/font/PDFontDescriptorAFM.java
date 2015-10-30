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
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSObject;

import java.util.StringTokenizer;

/**
 * The {@link PDFontDescriptor} that is used when no explicit /FontDescriptor is
 * available in the PDF document. This will happen only when a built in font is
 * used.
 */
public class PDFontDescriptorAFM extends PDFontDescriptor {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDFontDescriptor.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            throw new IllegalStateException("can not instantiate PDFontDescriptorAFM as COSBasedObject");
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private AFM afm;

    /**
     * derived value for the leading
     */
    private int leading;

    /**
     * cached value for the bounding box
     */
    private CDSRectangle bb;

    /**
     * Create a font descriptor for a type 1 font.
     *
     * @param newFont type 1 font containing the definitions.
     */
    protected PDFontDescriptorAFM(AFM afm) {
        super(null);
        this.afm = afm;
    }

    public AFM getAfm() {
        return afm;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getAscent()
     */
    @Override
    public float getAscent() {
        String value = getAfm().getAttribute("Ascender");
        if (value == null) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getAvgWidth()
     */
    @Override
    public float getAvgWidth() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getCapHeight()
     */
    @Override
    public float getCapHeight() {
        String value = getAfm().getAttribute("CapHeight");
        if (value == null) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    @Override
    public String getCharSet() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getDescent()
     */
    @Override
    public float getDescent() {
        String value = getAfm().getAttribute("Descender");
        if (value == null) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFlags()
     */
    @Override
    public int getFlagsValue() {
        // todo 1 font support correct flags
        // AFM afm = getFont().getAfm();
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getRectangle()
     */
    @Override
    public CDSRectangle getFontBB() {
        if (bb == null) {
            float llx = 0;
            float lly = 0;
            float urx = 0;
            float ury = 0;
            String value = getAfm().getAttribute("FontBBox");
            if (value != null) {
                StringTokenizer st = new StringTokenizer(value);
                if (st.hasMoreTokens()) {
                    llx = Float.parseFloat(st.nextToken());
                }
                if (st.hasMoreTokens()) {
                    lly = Float.parseFloat(st.nextToken());
                }
                if (st.hasMoreTokens()) {
                    urx = Float.parseFloat(st.nextToken());
                }
                if (st.hasMoreTokens()) {
                    ury = Float.parseFloat(st.nextToken());
                }
            }
            bb = new CDSRectangle(llx, lly, urx, ury);
        }
        return bb;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFontFamily()
     */
    @Override
    public String getFontFamily() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFontName()
     */
    @Override
    public String getFontName() {
        if (getAfm() == null) {
            return "";
        }
        String value = getAfm().getAttribute("FontName");
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    @Override
    public String getFontStretch() {
        return null;
    }

    @Override
    public int getFontWeight() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getItalicAngle()
     */
    @Override
    public float getItalicAngle() {
        String value = getAfm().getAttribute("ItalicAngle");
        if (value == null) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getLeading()
     */
    @Override
    public int getLeading() {
        if (leading == 0) {
            // compute a default for the leading
            CDSRectangle rect = getFontBB();
            leading = (int) (rect.getUpperRightY() - rect.getLowerLeftY());
        }
        return leading;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getMaxWidth()
     */
    @Override
    public int getMaxWidth() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getMissingWidth()
     */
    @Override
    public int getMissingWidth() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getStemH()
     */
    @Override
    public int getStemH() {
        String value = getAfm().getAttribute("StdHW");
        if (value == null) {
            return 0;
        }
        return (int) Float.parseFloat(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getStemV()
     */
    @Override
    public int getStemV() {
        String value = getAfm().getAttribute("StdVW");
        if (value == null) {
            return 0;
        }
        return (int) Float.parseFloat(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getXHeight()
     */
    @Override
    public float getXHeight() {
        String value = getAfm().getAttribute("XHeight");
        if (value == null) {
            return 0;
        }
        return Float.parseFloat(value);
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public boolean isNonsymbolic() {
        return !isSymbolic();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#isSymbolic()
     */
    @Override
    public boolean isSymbolic() {
        String name = getFontName();
        return PDFontType1.FONT_ZapfDingbats.equals(name) || PDFontType1.FONT_Symbol.equals(name);
    }

    @Override
    public void setCharSet(String charset) {
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#setFlagsValue(int)
     */
    @Override
    public void setFlagsValue(int value) {
        // TODO 2
    }

    @Override
    public void setFontStretch(String stretch) {
    }

    @Override
    public void setFontWeight(int weight) {
    }
}
