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
import de.intarsys.cwt.font.afm.AFMChar;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.encoding.SymbolEncoding;
import de.intarsys.tools.locator.ClassResourceLocator;
import de.intarsys.tools.locator.ILocator;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * basic implementation for type 1 support
 */
public class PDFontType1 extends PDSingleByteFont {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDFont.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDFontType1(object);
        }
    }

    /**
     * Map of known alternative names for the builtin fonts
     */
    public static final Map<String, String> FONT_ALIASES;

    /**
     * Map of known deprecated alternative names for the builtin fonts
     */
    public static final Map<String, String> FONT_ALIASES_DEPRECATED;

    public static String FONT_Courier = "Courier"; //$NON-NLS-1$

    public static String FONT_Courier_Bold = "Courier-Bold"; //$NON-NLS-1$

    public static String FONT_Courier_BoldOblique = "Courier-BoldOblique"; //$NON-NLS-1$

    public static String FONT_Courier_Oblique = "Courier-Oblique"; //$NON-NLS-1$

    public static String FONT_Helvetica = "Helvetica"; //$NON-NLS-1$

    public static String FONT_Helvetica_Bold = "Helvetica-Bold"; //$NON-NLS-1$

    public static String FONT_Helvetica_BoldOblique = "Helvetica-BoldOblique"; //$NON-NLS-1$

    public static String FONT_Helvetica_Oblique = "Helvetica-Oblique"; //$NON-NLS-1$

    public static String FONT_Symbol = "Symbol"; //$NON-NLS-1$

    public static String FONT_Times_Bold = "Times-Bold"; //$NON-NLS-1$

    public static String FONT_Times_BoldItalic = "Times-BoldItalic"; //$NON-NLS-1$

    public static String FONT_Times_Italic = "Times-Italic"; //$NON-NLS-1$

    public static String FONT_Times_Roman = "Times-Roman"; //$NON-NLS-1$

    public static String FONT_ZapfDingbats = "ZapfDingbats"; //$NON-NLS-1$

    public static final String[] FONT_BUILTINS = {FONT_Courier,
                                                              FONT_Courier_Bold,
                                                              FONT_Courier_BoldOblique,
                                                              FONT_Courier_Oblique,
                                                              FONT_Helvetica,
                                                              FONT_Helvetica_Bold,
                                                              FONT_Helvetica_BoldOblique,
                                                              FONT_Helvetica_Oblique,
                                                              FONT_Symbol,
                                                              FONT_Times_Bold,
                                                              FONT_Times_BoldItalic,
                                                              FONT_Times_Italic,
                                                              FONT_Times_Roman,
                                                              FONT_ZapfDingbats};

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private static Map<String, AFM> builtins = new HashMap<String, AFM>();

    static {
        FONT_ALIASES = new HashMap<String, String>();
        // yourself
        FONT_ALIASES.put(FONT_Courier, FONT_Courier);
        FONT_ALIASES.put(FONT_Courier_Bold, FONT_Courier_Bold);
        FONT_ALIASES.put(FONT_Courier_Oblique, FONT_Courier_Oblique);
        FONT_ALIASES.put(FONT_Courier_BoldOblique, FONT_Courier_BoldOblique);
        FONT_ALIASES.put(FONT_Helvetica, FONT_Helvetica);
        FONT_ALIASES.put(FONT_Helvetica_Bold, FONT_Helvetica_Bold);
        FONT_ALIASES.put(FONT_Helvetica_Oblique, FONT_Helvetica_Oblique);
        FONT_ALIASES.put(FONT_Helvetica_BoldOblique, FONT_Helvetica_BoldOblique);
        FONT_ALIASES.put(FONT_Times_Roman, FONT_Times_Roman);
        FONT_ALIASES.put(FONT_Times_Bold, FONT_Times_Bold);
        FONT_ALIASES.put(FONT_Times_Italic, FONT_Times_Italic);
        FONT_ALIASES.put(FONT_Times_BoldItalic, FONT_Times_BoldItalic);
        FONT_ALIASES.put(FONT_ZapfDingbats, FONT_ZapfDingbats);
        FONT_ALIASES.put(FONT_Symbol, FONT_Symbol);
        // abbreviations used in pdfmarks
        FONT_ALIASES.put("Cour", FONT_Courier);
        FONT_ALIASES.put("CoBo", FONT_Courier_Bold);
        FONT_ALIASES.put("CoOb", FONT_Courier_Oblique);
        FONT_ALIASES.put("CoBO", FONT_Courier_BoldOblique);
        FONT_ALIASES.put("Helv", FONT_Helvetica);
        FONT_ALIASES.put("HeBo", FONT_Helvetica_Bold);
        FONT_ALIASES.put("HeOb", FONT_Helvetica_Oblique);
        FONT_ALIASES.put("HeBO", FONT_Helvetica_BoldOblique);
        FONT_ALIASES.put("TiRo", FONT_Times_Roman);
        FONT_ALIASES.put("TiBo", FONT_Times_Bold);
        FONT_ALIASES.put("TiIt", FONT_Times_Italic);
        FONT_ALIASES.put("TiBI", FONT_Times_BoldItalic);
        FONT_ALIASES.put("ZaDb", FONT_ZapfDingbats);
        FONT_ALIASES.put("Symb", FONT_Symbol);
        // valid alternatives (1.7 spec)
        FONT_ALIASES.put("CourierNew", FONT_Courier);
        FONT_ALIASES.put("CourierNew,Bold", FONT_Courier_Bold);
        FONT_ALIASES.put("CourierNew,Italic", FONT_Courier_Oblique);
        FONT_ALIASES.put("CourierNew,BoldItalic", FONT_Courier_BoldOblique);
        FONT_ALIASES.put("Arial", FONT_Helvetica);
        FONT_ALIASES.put("Arial,Bold", FONT_Helvetica_Bold);
        FONT_ALIASES.put("Arial,Italic", FONT_Helvetica_Oblique);
        FONT_ALIASES.put("Arial,BoldItalic", FONT_Helvetica_BoldOblique);
        FONT_ALIASES.put("TimesNewRoman", FONT_Times_Roman);
        FONT_ALIASES.put("TimesNewRoman,Bold", FONT_Times_Bold);
        FONT_ALIASES.put("TimesNewRoman,Italic", FONT_Times_Italic);
        FONT_ALIASES.put("TimesNewRoman,BoldItalic", FONT_Times_BoldItalic);
        //
        // deperecated maps
        FONT_ALIASES_DEPRECATED = new HashMap<String, String>();
        // alternatives given in pdf reference 1.4, no longer valid
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS", FONT_Times_Roman);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPSMT", FONT_Times_Roman);
        FONT_ALIASES_DEPRECATED.put("TimesNewRoman-Bold", FONT_Times_Bold);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-Bold", FONT_Times_Bold);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-BoldMT", FONT_Times_Bold);
        FONT_ALIASES_DEPRECATED.put("TimesNewRoman-Italic", FONT_Times_Italic);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-Italic", FONT_Times_Italic);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-ItalicMT", FONT_Times_Italic);
        FONT_ALIASES_DEPRECATED.put("TimesNewRoman-BoldItalic", FONT_Times_BoldItalic);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-BoldItalic", FONT_Times_BoldItalic);
        FONT_ALIASES_DEPRECATED.put("TimesNewRomanPS-BoldItalicMT", FONT_Times_BoldItalic);
        FONT_ALIASES_DEPRECATED.put("CourierNewPSMT", FONT_Courier);
        FONT_ALIASES_DEPRECATED.put("Courier,BoldItalic", FONT_Courier_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("CourierNew-BoldItalic", FONT_Courier_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("CourierNewPS-BoldItalicMT", FONT_Courier_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("Courier,Bold", FONT_Courier_Bold);
        FONT_ALIASES_DEPRECATED.put("CourierNew-Bold", FONT_Courier_Bold);
        FONT_ALIASES_DEPRECATED.put("CourierNewPS-BoldMT", FONT_Courier_Bold);
        FONT_ALIASES_DEPRECATED.put("Courier,Italic", FONT_Courier_Oblique);
        FONT_ALIASES_DEPRECATED.put("CourierNew-Italic", FONT_Courier_Oblique);
        FONT_ALIASES_DEPRECATED.put("CourierNewPS-ItalicMT", FONT_Courier_Oblique);
        FONT_ALIASES_DEPRECATED.put("Helvetica,Bold", FONT_Helvetica_Bold);
        FONT_ALIASES_DEPRECATED.put("Helvetica-Italic", FONT_Helvetica_Oblique);
        FONT_ALIASES_DEPRECATED.put("Helvetica,Italic", FONT_Helvetica_Oblique);
        FONT_ALIASES_DEPRECATED.put("Helvetica-BoldItalic", FONT_Helvetica_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("Helvetica,BoldItalic", FONT_Helvetica_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("ArialMT", FONT_Helvetica);
        FONT_ALIASES_DEPRECATED.put("Arial-Bold", FONT_Helvetica_Bold);
        FONT_ALIASES_DEPRECATED.put("Arial-BoldMT", FONT_Helvetica_Bold);
        FONT_ALIASES_DEPRECATED.put("Arial-Italic", FONT_Helvetica_Oblique);
        FONT_ALIASES_DEPRECATED.put("Arial-ItalicMT", FONT_Helvetica_Oblique);
        FONT_ALIASES_DEPRECATED.put("Arial-BoldItalic", FONT_Helvetica_BoldOblique);
        FONT_ALIASES_DEPRECATED.put("Arial-BoldItalicMT", FONT_Helvetica_BoldOblique);
    }

    /**
     * create a Type1 font object to be used in the pdf document
     *
     * @param name the name of the font to use
     * @return the new font created
     */
    public static PDFontType1 createNew(String name) {
        PDFontType1 font = (PDFontType1) PDFontType1.META.createNew();
        String baseFontName = PDFontType1.FONT_ALIASES.get(name);
        if (baseFontName == null) {
            baseFontName = PDFontType1.FONT_ALIASES_DEPRECATED.get(name);
            if (baseFontName == null) {
                baseFontName = name;
            }
        }
        font.setBaseFont(baseFontName);
        return font;
    }

    public static boolean isBuiltin(String name) {
        return name != null && name.equals(FONT_ALIASES.get(name));
    }

    public static boolean isBuiltinAlias(String name) {
        return FONT_ALIASES.get(name) != null;
    }

    public static boolean isBuiltinDeprecated(String name) {
        return FONT_ALIASES_DEPRECATED.get(name) != null;
    }

    /**
     * Lookup the {@link AFM} structure for the named builtin font.
     *
     * @param name
     * @return the {@link AFM} structure for the named builtin font.
     */
    public static synchronized AFM lookupBuiltinAFM(String name) {
        String aliased = FONT_ALIASES.get(name);
        if (aliased == null) {
            aliased = FONT_ALIASES_DEPRECATED.get(name);
            if (aliased == null) {
                return null;
            }
        }
        AFM result = builtins.get(aliased);
        if (result == null) {
            ILocator locator = new ClassResourceLocator(PDFontType1.class, aliased + ".afm");
            try {
                result = AFM.createFromLocator(locator);
                builtins.put(aliased, result);
            } catch (IOException e) {
                PACKAGE.Log.log(Level.WARNING, "builtin font metrics '" + aliased + "' load error", e);
            }
        }
        return result;
    }

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDFontType1(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
     */
    @Override
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_Type1;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFont#createBuiltinFontDescriptor()
     */
    @Override
    protected PDFontDescriptor createBuiltinFontDescriptor() {
        return new PDFontDescriptorAFM(lookupBuiltinAFM(getBaseFont().stringValue()));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFont#createBuiltInWidths(int[])
     */
    @Override
    protected int[] createBuiltInWidths(int[] result) {
        AFM afm = lookupBuiltinAFM(getBaseFont().stringValue());
        if (afm == null) {
            return result;
        }
        if (getEncoding().isFontSpecificEncoding()) {
            for (int i = 0; i < 256; i++) {
                AFMChar afmChar = afm.getCharByCode(i);
                if (afmChar != null) {
                    result[i] = afmChar.getWidth();
                }
            }
        } else {
            for (int i = 0; i < 256; i++) {
                String glyphName = getEncoding().getGlyphName(i);
                AFMChar afmChar = afm.getCharByName(glyphName);
                if (afmChar != null) {
                    result[i] = afmChar.getWidth();
                }
            }
        }
        return result;
    }

    @Override
    protected Encoding createDefaultEncoding() {
        if ((getFontDescriptor() != null) && getFontDescriptor().isSymbolic()) {
            return SymbolEncoding.UNIQUE;
        }
        AFM afm = lookupBuiltinAFM(getBaseFont().stringValue());
        if (afm == null) {
            return super.createDefaultEncoding();
        } else {
            return new AFMEncoding(afm);
        }
    }

    @Override
    protected int createFirstChar() {
        // check if there is some undefined code
        Encoding encoding = getEncoding();
        for (int i = 0; i <= 255; i++) {
            if (encoding.getDecoded(i) != -1) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected int createLastChar() {
        // check if there is some undefined code
        Encoding encoding = getEncoding();
        for (int i = 255; i >= 0; i--) {
            if (encoding.getDecoded(i) != -1) {
                return i;
            }
        }

        // ??
        return 0;
    }

    @Override
    public String getFontNameNormalized() {
        String name = super.getFontNameNormalized();
        String alias = FONT_ALIASES.get(name);
        if (alias != null) {
            return alias;
        }
        alias = FONT_ALIASES_DEPRECATED.get(name);
        if (alias != null) {
            return alias;
        }
        return name;
    }

    @Override
    public String getFontType() {
        return "Type1";
    }

    @Override
    public boolean isStandardFont() {
        if (cosGetField(DK_FontDescriptor).isNull()) {
            return true;
        }
        return Arrays.asList(FONT_BUILTINS).contains(cosGetField(DK_BaseFont));
    }
}
