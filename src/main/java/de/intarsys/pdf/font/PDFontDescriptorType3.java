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

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSObject;

/**
 * The {@link PDFontDescriptor} that is used for a Type3 font, having no
 * explicit font descriptor of its own. This object allows polymorphic access to
 * the font information.
 */
public class PDFontDescriptorType3 extends PDFontDescriptor {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDFontDescriptor.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            throw new IllegalStateException("can not instantiate PDFontDescriptorType3 as COSBasedObject");
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * derived value for the leading
     */
    private int leading;

    /**
     * cached value for the bounding box
     */
    private CDSRectangle bb;

    private PDFontType3 font;

    /**
     * Create a font descriptor for a type 1 font.
     *
     * @param newFont type 1 font containing the definitions.
     */
    protected PDFontDescriptorType3(PDFontType3 font) {
        super(null);
        this.font = font;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getAscent()
     */
    @Override
    public float getAscent() {
        CDSRectangle bb = getFont().getFontBB();
        CDSMatrix matrix = getFont().getFontMatrix();
        return bb.getUpperRightY() * matrix.getD() * 1000;
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
        return getAscent();
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
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFlags()
     */
    @Override
    public int getFlagsValue() {
        return 0;
    }

    public PDFontType3 getFont() {
        return font;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getRectangle()
     */
    @Override
    public CDSRectangle getFontBB() {
        return getFont().getFontBB();
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
        return getFont().getFontName();
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
        return 0;
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
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getStemV()
     */
    @Override
    public int getStemV() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getXHeight()
     */
    @Override
    public float getXHeight() {
        return getAscent();
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
    }

    @Override
    public void setFontStretch(String stretch) {
    }

    @Override
    public void setFontWeight(int weight) {
    }
}
