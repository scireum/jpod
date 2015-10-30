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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.pd.PDObject;

/**
 * the detail information about a font.
 */
public abstract class PDFontDescriptor extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        public Class getRootClass() {
            return PDFontDescriptor.class;
        }
    }

    public static final COSName CN_Type_FontDescriptor = COSName.constant("FontDescriptor"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());
    private FontDescriptorFlags flags = new FontDescriptorFlags(this);

    protected PDFontDescriptor(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
     */
    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_FontDescriptor;
    }

    public COSStream cosGetFontFile() {
        return null;
    }

    public COSStream cosGetFontFile2() {
        return null;
    }

    public COSStream cosGetFontFile3() {
        return null;
    }

    /**
     * The font ascent.
     *
     * @return The font ascent.
     */
    public abstract float getAscent();

    /**
     * The font average width.
     *
     * @return The font average width.
     */
    public abstract float getAvgWidth();

    /**
     * The font capital height.
     *
     * @return The capital height.
     */
    public abstract float getCapHeight();

    public abstract String getCharSet();

    /**
     * The font descent.
     *
     * @return The font descent.
     */
    public abstract float getDescent();

    public FontDescriptorFlags getFlags() {
        return flags;
    }

    /**
     * The font flags.
     *
     * @return The font flags.
     */
    public abstract int getFlagsValue();

    /**
     * The character enclosing rectangle.
     *
     * @return The character enclosing rectangle.
     */
    public abstract CDSRectangle getFontBB();

    /**
     * The font family name.
     *
     * @return The font name.
     */
    public abstract String getFontFamily();

    /**
     * The data making up a Type1 font program.
     *
     * @return The data making up a Type1 font program.
     */
    public byte[] getFontFile() {
        return null;
    }

    /**
     * The data making up a TrueType font program
     *
     * @return The data making up a TrueType font program
     */
    public byte[] getFontFile2() {
        return null;
    }

    /**
     * data for a font type specified in the /Subtpye entry of the stream
     * dictionary.
     *
     * @return data for a font type specified in the /Subtpye entry of the
     * stream dictionary.
     */
    public byte[] getFontFile3() {
        return null;
    }

    /**
     * The font name.
     *
     * @return The font name.
     */
    public abstract String getFontName();

    /**
     * The font stretch value, one of
     * <ul>
     * <li>UltraCondensed</li>
     * <li>ExtraCondensed</li>
     * <li>Condensed</li>
     * <li>SemiCondensed</li>
     * <li>Normal</li>
     * <li>SemiExpanded</li>
     * <li>Expanded</li>
     * <li>ExtraExpanded</li>
     * <li>UltraExpanded</li>
     * </ul>
     *
     * @return The font stretch value
     */
    public abstract String getFontStretch();

    /**
     * The font weight value, one of
     * <ul>
     * <li>100</li>
     * <li>200</li>
     * <li>300</li>
     * <li>400 (normal)</li>
     * <li>500</li>
     * <li>600</li>
     * <li>700 (bold)</li>
     * <li>800</li>
     * <li>900</li>
     * </ul>
     *
     * @return The font stretch value
     */
    public abstract int getFontWeight();

    /**
     * The font italic angle.
     *
     * @return The font italic angle.
     */
    public abstract float getItalicAngle();

    /**
     * The font leading.
     *
     * @return The font leading.
     */
    public abstract int getLeading();

    /**
     * The font character maximal width.
     *
     * @return The font character maximal width.
     */
    public abstract int getMaxWidth();

    /**
     * The width to use when definition is missing.
     *
     * @return The width to use when definition is missing.
     */
    public abstract int getMissingWidth();

    /**
     * The font horizontal stem.
     *
     * @return The font horizontal stem.
     */
    public abstract int getStemH();

    /**
     * The font vertical stem.
     *
     * @return The font vertical stem.
     */
    public abstract int getStemV();

    /**
     * The height of "X".
     *
     * @return The height of "X".
     */
    public abstract float getXHeight();

    public boolean isAllCap() {
        return flags.isAllCap();
    }

    public boolean isBuiltin() {
        return false;
    }

    public boolean isFixedPitch() {
        return flags.isFixedPitch();
    }

    public boolean isForceBold() {
        return flags.isForceBold();
    }

    public boolean isItalic() {
        return flags.isItalic();
    }

    public boolean isNonsymbolic() {
        return flags.isNonsymbolic();
    }

    public boolean isScript() {
        return flags.isScript();
    }

    public boolean isSerif() {
        return flags.isSerif();
    }

    public boolean isSmallCap() {
        return flags.isSmallCap();
    }

    public boolean isSymbolic() {
        return flags.isSymbolic();
    }

    public void setAllCap(boolean flag) {
        flags.setAllCap(flag);
    }

    public abstract void setCharSet(String charset);

    public void setFixedPitch(boolean flag) {
        flags.setFixedPitch(flag);
    }

    public abstract void setFlagsValue(int value);

    public void setFontFamily(String value) {
        //
    }

    public void setFontFile(byte[] data) {
        //
    }

    public void setFontFile2(byte[] data) {
        //
    }

    public void setFontFile3(byte[] data) {
        //
    }

    public void setFontName(String value) {
        //
    }

    public abstract void setFontStretch(String stretch);

    public abstract void setFontWeight(int weight);

    public void setForceBold(boolean flag) {
        flags.setForceBold(flag);
    }

    public void setItalic(boolean flag) {
        flags.setItalic(flag);
    }

    public void setNonsymbolic(boolean flag) {
        flags.setNonsymbolic(flag);
    }

    public void setScript(boolean flag) {
        flags.setScript(flag);
    }

    public void setSerif(boolean flag) {
        flags.setSerif(flag);
    }

    public void setSmallCap(boolean flag) {
        flags.setSmallCap(flag);
    }

    public void setSymbolic(boolean flag) {
        flags.setSymbolic(flag);
    }
}
