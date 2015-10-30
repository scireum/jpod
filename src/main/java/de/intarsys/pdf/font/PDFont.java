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

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.encoding.MacOSRomanEncoding;
import de.intarsys.pdf.encoding.StandardEncoding;
import de.intarsys.pdf.pd.PDObject;
import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.stream.StreamTools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * A PDF font object.
 */
public abstract class PDFont extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * de.intarsys.pdf.cos.COSBasedObject.MetaClass#doDetermineClass(de.
         * intarsys.pdf.cos.COSObject)
         */
        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            COSDictionary dict;

            dict = object.asDictionary();
            if (dict == null) {
                throw new IllegalArgumentException("font object is not a COSDictionary as required");
            }
            COSName type = dict.get(DK_Type).asName();
            if (type == null) {
                throw new IllegalArgumentException("Dictionary has no type");
            }
            if (!type.equals(CN_Type_Font)) {
                throw new IllegalArgumentException("type <" + type + "> is not a valid font type");
            }
            COSName subtype = dict.get(DK_Subtype).asName();
            if (subtype == null) {
                throw new IllegalArgumentException("font not identified by subtype");
            }
            if (subtype.equals(CN_Subtype_Type1)) {
                return PDFontType1.META;
            } else if (subtype.equals(CN_Subtype_TrueType)) {
                if (dict.get(DK_FontDescriptor).isNull()) {
                    /*
					 * treat as if Type1 was specified, because that's probably
					 * what the creator meant; further processing would yield
					 * wrong results anyway as FontDescriptor is a required
					 * entry for TrueType fonts
					 */
                    return PDFontType1.META;
                }
                return PDFontTrueType.META;
            } else if (subtype.equals(CN_Subtype_MMType1)) {
                return PDFontMMType1.META;
            } else if (subtype.equals(CN_Subtype_Type0)) {
                return PDFontType0.META;
            } else if (subtype.equals(CN_Subtype_Type3)) {
                return PDFontType3.META;
            } else if (subtype.equals(CN_Subtype_CIDFontType0)) {
                return CIDFontType0.META;
            } else if (subtype.equals(CN_Subtype_CIDFontType2)) {
                return CIDFontType2.META;
            }
            throw new IllegalArgumentException("font subtype <" + subtype + "> not supported");
        }

        @Override
        public Class getRootClass() {
            return PDFont.class;
        }
    }

    private static final Attribute ATTR_FONTFAMILY = new Attribute("fontfamily");

    private static final Attribute ATTR_FONTNAME = new Attribute("fontname");

    private static final Attribute ATTR_FONTSTYLE = new Attribute("fontstyle");

    public static final COSName CN_Subtype_CIDFontType0 = COSName.constant("CIDFontType0"); //$NON-NLS-1$

    public static final COSName CN_Subtype_CIDFontType2 = COSName.constant("CIDFontType2"); //$NON-NLS-1$

    public static final COSName CN_Subtype_MMType1 = COSName.constant("MMType1"); //$NON-NLS-1$

    public static final COSName CN_Subtype_TrueType = COSName.constant("TrueType"); //$NON-NLS-1$

    public static final COSName CN_Subtype_Type0 = COSName.constant("Type0"); //$NON-NLS-1$

    public static final COSName CN_Subtype_Type1 = COSName.constant("Type1"); //$NON-NLS-1$

    public static final COSName CN_Subtype_Type3 = COSName.constant("Type3"); //$NON-NLS-1$

    public static final COSName CN_Type_Font = COSName.constant("Font"); //$NON-NLS-1$

    public static final COSName DK_BaseFont = COSName.constant("BaseFont"); //$NON-NLS-1$

    public static final COSName DK_Encoding = COSName.constant("Encoding"); //$NON-NLS-1$

    public static final COSName DK_FirstChar = COSName.constant("FirstChar"); //$NON-NLS-1$

    public static final COSName DK_FontDescriptor = COSName.constant("FontDescriptor"); //$NON-NLS-1$

    public static final COSName DK_LastChar = COSName.constant("LastChar"); //$NON-NLS-1$

    public static final COSName DK_Name = COSName.constant("Name"); //$NON-NLS-1$

    public static final COSName DK_ToUnicode = COSName.constant("ToUnicode"); //$NON-NLS-1$

    public static final COSName DK_Widths = COSName.constant("Widths"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private static CMap UNDEFINED = new IdentityCMap();

    public static String getFontFamilyName(String name) {
        if (name == null) {
            return null;
        }
        int posPlus = name.indexOf('+');
        if (posPlus > 0) {
            name = name.substring(posPlus + 1);
        }
        int posMinus = name.lastIndexOf('-');
        if (posMinus > 0) {
            name = name.substring(0, posMinus);
        }
        int posComma = name.indexOf(',');
        if (posComma > 0) {
            name = name.substring(0, posComma);
        }
        return name;
    }

    /**
     * extracts the "name" portion from the given font name string
     *
     * @param name a font name
     * @return font name's "name" portion
     */
    public static String getFontName(String name) {
        if (name == null) {
            return null;
        }
        int posPlus = name.indexOf('+');
        if (posPlus > 0) {
            name = name.substring(posPlus + 1);
        }
        return name;
    }

    /**
     * extracts the "style" portion from the given font name
     *
     * @param name a font name
     * @return font name's "style" portion
     */
    public static PDFontStyle getFontStyle(String name) {
        if (name == null) {
            return PDFontStyle.REGULAR;
        }
        int posMinus = name.lastIndexOf('-');
        if (posMinus > 0) {
            name = name.substring(posMinus + 1);
        }
        int posComma = name.indexOf(',');
        if (posComma > 0) {
            name = name.substring(posComma + 1).trim();
        }
        return PDFontStyle.getFontStyle(name);
    }

    // the encoding used for this font
    private Encoding cachedEncoding;

    // some detail information about the font
    private PDFontDescriptor cachedFontDescriptor;

    private CMap cachedToUnicode = UNDEFINED;

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDFont(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
     */
    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_Font;
    }

    /**
     * The font descriptor for a builtin font.
     *
     * @return The font descriptor for a builtin font
     * @throws IllegalStateException
     */
    protected PDFontDescriptor createBuiltinFontDescriptor() {
        // this may happen, there are strange documents around that depend on
        // certain TrueTypes to be present.
        // Note from EHK: I'm treating some strange TrueTypes as Type1 now, so
        // maybe doesn't happen anymore
        return null;
    }

    /**
     * Fill the correct width values into an array of glyph widths for a builtin
     * font. This is a valid implementation for type1 builtin fonts only.
     *
     * @param result The array to hold the glyph widths.
     * @return The array of widths for the defined range of chars in the font
     */
    protected int[] createBuiltInWidths(int[] result) {
        return result;
    }

    /**
     * Fill an array of glyph widths from the definition prepared by the font
     * dictionary. The widths in the font are declared in the range from the
     * first supported code point to the last code point. The code point selects
     * a glyph out of the font depending on the encoding by the font, the
     * corresponding entry in the width array defines its width.
     *
     * @param result The array to hold the correct widths.
     * @param array  The COSArray defining the widths.
     * @return The array of widths for the defined range of chars in the font
     */
    protected int[] createDeclaredWidths(int[] result, COSArray array) {
        int i = getFirstChar();
        for (Iterator it = array.iterator(); it.hasNext(); ) {
            COSNumber width = ((COSObject) it.next()).asNumber();
            if (width != null) {
                result[i] = width.intValue();
            }
            i++;
        }
        return result;
    }

    /**
     * get an encoding object that describes this fonts NATIVE encoding (if any)
     *
     * @return an encoding
     */
    protected Encoding createDefaultEncoding() {
        return StandardEncoding.UNIQUE;
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
     * @throws IllegalArgumentException When the encoding defined in the font is not supported.
     */
    protected Encoding createEncoding() {
        COSObject encoding = cosGetField(PDFont.DK_Encoding);
        if (encoding.isNull()) {
            return createDefaultEncoding();
        }
        if (encoding instanceof COSName) {
            try {
                return Encoding.createNamed((COSName) encoding);
            } catch (Exception e) {
                // found PDF where the base name was /NULL...
                return createDefaultEncoding();
            }
        }
        if (encoding instanceof COSDictionary) {
            return DifferenceEncoding.create((COSDictionary) encoding, this);
        }
        throw new IllegalArgumentException("encoding not supported");
    }

    protected int createFirstChar() {
        return 0;
    }

    /**
     * @return the lazily created font descriptor of this font
     */
    protected PDFontDescriptor createFontDescriptor() {
        COSObject base = cosGetField(DK_FontDescriptor);
        if (base.isNull()) {
            return createBuiltinFontDescriptor();
        }
        return (PDFontDescriptorEmbedded) PDFontDescriptorEmbedded.META.createFromCos(base);
    }

    protected int createLastChar() {
        return 255;
    }

    /**
     * construct a array of glyph widths for the current font the widths may be
     * defined in the /Widths entry of the pdf font or in the font metric (afm)
     * of a builtin font
     *
     * @return the array of widths for the defined range of chars in the font
     */
    protected int[] createWidths() {
        int[] result = new int[256];
        int missing = getMissingWidth();
        for (int i = 0; i < 256; i++) {
            result[i] = missing;
        }

        COSArray base = cosGetField(DK_Widths).asArray();
        if (base == null) {
            return createBuiltInWidths(result);
        }
        return createDeclaredWidths(result, base);
    }

    public void dumpFontFile(File file) {
        PDFontDescriptorEmbedded fd = (PDFontDescriptorEmbedded) getFontDescriptor();
        if (fd == null) {
            return;
        }
        byte[] data = fd.getFontFile();
        if (data == null) {
            data = fd.getFontFile2();
        }
        if (data == null) {
            data = fd.getFontFile3();
        }
        if (data == null) {
            return;
        }
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
        } catch (Exception e) {
            // ignore
        } finally {
            StreamTools.close(os);
        }
    }

    /**
     * @return the base font for this font dictionary
     */
    public COSName getBaseFont() {
        return cosGetField(DK_BaseFont).asName();
    }

    /**
     * The encoding of the glyphs in the font
     *
     * @return The encoding of the glyphs in the font
     */
    public Encoding getEncoding() {
        if (cachedEncoding == null) {
            cachedEncoding = createEncoding();
        }
        return cachedEncoding;
    }

    /**
     * The first (encoded) codepoint defined in the font.
     *
     * @return The first (encoded) codepoint defined in the font
     */
    public int getFirstChar() {
        COSNumber base = cosGetField(DK_FirstChar).asInteger();
        if (base == null) {
            return createFirstChar();
        }
        return base.intValue();
    }

    /**
     * The {@link PDFontDescriptor} object for this font.
     *
     * @return The {@link PDFontDescriptor} object for this font
     */
    public PDFontDescriptor getFontDescriptor() {
        if (cachedFontDescriptor == null) {
            cachedFontDescriptor = createFontDescriptor();
        }
        return cachedFontDescriptor;
    }

    public String getFontFamilyName() {
        try {
            PDFontDescriptor fontDescriptor = getFontDescriptor();
            if (fontDescriptor != null) {
                String result = fontDescriptor.getFontFamily();
                if (result != null) {
                    return result;
                }
            }
        } catch (Exception e) {
            //
        }
        return PDFont.getFontFamilyName(getBaseFont().stringValue());
    }

    public String getFontName() {
        return getBaseFont().stringValue();
    }

    public String getFontNameNormalized() {
        return PDFont.getFontName(getFontName());
    }

    public PDFontStyle getFontStyle() {
        return getFontStyle(getBaseFont().stringValue());
    }

    abstract public String getFontType();

    /**
     * The {@link PDGlyphs} instance for the encoded codepoint.
     *
     * @param codepoint
     * @return The {@link PDGlyphs} instance for the encoded codepoint.
     */
    abstract public PDGlyphs getGlyphsEncoded(int codepoint);

    /**
     * The glyph width of an encoded codepoint in the font.
     * <p>
     * In the standard case for single byte encoded fonts, the codepoint is the
     * index in the /Widths array, holding the glyph width.
     * <p>
     * For multibyte fonts, see {@link PDFontType0}.
     *
     * @param codepoint The codepoint
     * @return The glyph width of an encoded codepoint in the font
     */
    abstract public int getGlyphWidthEncoded(int codepoint);

    /**
     * @return the last (encoded) codepoint defined in the font
     */
    public int getLastChar() {
        COSNumber base = cosGetField(DK_LastChar).asInteger();
        if (base == null) {
            return createLastChar();
        }
        return base.intValue();
    }

    public String getLookupFontFamilyName() {
        String result = (String) getAttribute(ATTR_FONTFAMILY);
        if (result == null) {
            result = getFontFamilyName();
            setAttribute(ATTR_FONTFAMILY, result);
        }
        return result;
    }

    public String getLookupFontName() {
        String result = (String) getAttribute(ATTR_FONTNAME);
        if (result == null) {
            result = getFontNameNormalized();
            setAttribute(ATTR_FONTNAME, result);
        }
        return result;
    }

    public PDFontStyle getLookupFontStyle() {
        PDFontStyle result = (PDFontStyle) getAttribute(ATTR_FONTSTYLE);
        if (result == null) {
            result = getFontStyle();
            setAttribute(ATTR_FONTSTYLE, result);
        }
        return result;
    }

    /**
     * This is a special mapping that is used if we have a font on the physical
     * device using a Macintosh Roman encoding character map.
     * <p>
     * <p>
     * See PDF docs, "Encodings for True Type fonts".
     * </p>
     *
     * @param codePoint
     * @return The unicode value for <code>codePoint</code>
     */
    public int getMacintoshRomanCode(int codePoint) {
        String glyphName = getEncoding().getGlyphName(codePoint);
        return MacOSRomanEncoding.UNIQUE.getEncoded(glyphName);
    }

    /**
     * @return the width we should use for a missing/undefined glyph width
     */
    public int getMissingWidth() {
        if (getFontDescriptor() == null) {
            return 0;
        }
        return getFontDescriptor().getMissingWidth();
    }

    /**
     * The {@link PDGlyphs} denoted by the next byte or bytes in the input
     * stream.
     * <p>
     * For single byte encoded fonts a single byte is read and the associated
     * {@link PDGlyphs} is returned. For CID fonts, the appropriate number of
     * bytes is read form the input stream to select the {@link PDGlyphs}.
     *
     * @param is The input stream on the {@link COSString} bytes
     * @return The next {@link PDGlyphs} referenced by the input stream.
     * @throws IOException
     */
    abstract public PDGlyphs getNextGlyphsEncoded(ByteArrayInputStream is) throws IOException;

    public CMap getToUnicode() {
        if (cachedToUnicode == UNDEFINED) {
            try {
                cachedToUnicode = (CMap) CMap.META.createFromCos(cosGetField(DK_ToUnicode));
            } catch (RuntimeException e) {
                cachedToUnicode = null;
                throw e;
            }
        }
        return cachedToUnicode;
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedEncoding = null;
        cachedFontDescriptor = null;
        cachedToUnicode = UNDEFINED;
    }

    /**
     * Answer true if this font's program is embedded within the document.
     *
     * @return Answer true if this font's program is embedded within the
     * document.
     */
    public boolean isEmbedded() {
        // shortcut for builtin fonts
        COSObject base = cosGetField(DK_FontDescriptor);
        if (base.isNull() && !(this instanceof PDFontType0)) {
            return false;
        }
        if (getFontDescriptor() == null) {
            return false;
        }
        if (getFontDescriptor().getFontFile() != null) {
            return true;
        }
        if (getFontDescriptor().getFontFile2() != null) {
            return true;
        }
        if (getFontDescriptor().getFontFile3() != null) {
            return true;
        }
        return false;
    }

    /**
     * Answer true if this is one of the 14 standard fonts. TODO 2 implement
     *
     * @return Answer true if this is one of the 14 standard fonts.
     */
    public boolean isStandardFont() {
        return false;
    }

    /**
     * Answer true if this font is partially embedded in the document.
     *
     * @return Answer true if this font is partially embedded in the document.
     */
    public boolean isSubset() {
        byte[] name = getBaseFont().byteValue();
        if (name.length > 7) {
            return name[6] == '+';
        }
        // filter some more ill defined fonts
        if (getFirstChar() > 32) {
            return true;
        }
        if (getLastChar() < 128) {
            return true;
        }
        return false;
    }

    public void setBaseFont(String name) {
        setFieldName(DK_BaseFont, name);
    }

    /**
     * set an encoding for the font
     *
     * @param newFontEncoding the new encoding to use
     */
    public void setEncoding(Encoding newFontEncoding) {
        cachedEncoding = newFontEncoding;
        if (newFontEncoding != null) {
            COSObject ref = cachedEncoding.cosGetObject();
            if (ref == null || ref.isNull()) {
                cosRemoveField(DK_Encoding);
            } else {
                cosSetField(DK_Encoding, ref);
            }
        } else {
            cosRemoveField(DK_Encoding);
        }
    }

    public void setFontDescriptor(PDFontDescriptor descriptor) {
        cachedFontDescriptor = descriptor;
        setFieldObject(DK_FontDescriptor, cachedFontDescriptor);
    }

    public void setLookupFontFamilyName(String name) {
        if (name == null) {
            return;
        }
        setAttribute(ATTR_FONTFAMILY, name);
    }

    public void setLookupFontName(String name) {
        if (name == null) {
            return;
        }
        setAttribute(ATTR_FONTNAME, name);
    }

    public void setLookupFontStyle(PDFontStyle newStyle) {
        if (newStyle == null) {
            return;
        }
        setAttribute(ATTR_FONTSTYLE, newStyle);
    }

    /**
     * set the to unicode mapping
     *
     * @param newToUnicode the new to unicode to use
     */
    public void setToUnicode(CMap newToUnicode) {
        cachedToUnicode = UNDEFINED;
        setFieldObject(DK_ToUnicode, newToUnicode);
    }

    @Override
    public String toString() {
        return cosGetSubtype().stringValue() + "-Font " + getBaseFont().toString() + " (" + getEncoding() + ")";
    }
}
