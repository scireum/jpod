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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.encoding.GlyphNameMap;
import de.intarsys.pdf.encoding.MappedEncoding;

import java.util.Iterator;

/**
 * An encoding defined as a delta to another base encoding.
 * <p>
 * <p>
 * <p>
 * <pre>
 *   the encoding difference is defined in the form
 *   ...
 *   num name name name
 *   num name..
 *   ...
 * </pre>
 * <p>
 * </p>
 * <p>
 * <p>
 * where num is the codepoint for the first glyph name in the list. The
 * following names are mapped to the codepoint of the predecessor + 1. Multiple
 * redefinition offsets can be defined.
 * </p>
 */
public class DifferenceEncoding extends Encoding {

    public static final COSName DK_Differences = COSName.constant("Differences");

    public static final COSName DK_BaseEncoding = COSName.constant("BaseEncoding");

    /**
     * Create the difference encoding from the values defined in the
     * <code>dict</code>.
     *
     * @param dict The dictionary defining the difference.
     * @param font The font defining the base encoding.
     * @return The difference encoding from the values defined in the
     * <code>dict</code>.
     */
    public static Encoding create(COSDictionary dict, PDFont font) {
        DifferenceEncoding encoding = new DifferenceEncoding(dict);
        COSObject base = dict.get(DK_BaseEncoding);
        Encoding baseEncoding = null;
        if (base.isNull()) {
            baseEncoding = font.createDefaultEncoding();
        }
        if (base instanceof COSName) {
            try {
                baseEncoding = Encoding.createNamed((COSName) base);
            } catch (Exception e) {
                // found PDF where the base name was /NULL...
                baseEncoding = font.createDefaultEncoding();
            }
        }
        encoding.setBaseEncoding(baseEncoding);
        MappedEncoding differenceEncoding = new MappedEncoding();
        encoding.setDifferenceEncoding(differenceEncoding);

        COSArray differences = dict.get(DK_Differences).asArray();
        if (differences != null) {
            int start = -1;
            for (Iterator it = differences.iterator(); it.hasNext(); ) {
                COSObject element = (COSObject) it.next();
                if (element instanceof COSNumber) {
                    start = ((COSNumber) element).intValue();
                }
                if (element instanceof COSName && (start > -1)) {
                    String name = ((COSName) element).stringValue();
                    differenceEncoding.addEncoding(start, name);
                    start++;
                }
            }
        }
        return encoding;
    }

    // the dictionary object representing the encoding
    final private COSDictionary dict;

    // the base encoding
    private Encoding baseEncoding;

    // the difference declaration
    private MappedEncoding differenceEncoding;

    protected DifferenceEncoding(COSDictionary dict) {
        super();
        this.dict = dict;
    }

    /**
     * Return the COSDictionary used to define the difference in the encoding.
     *
     * @return Return the COSDictionary used to define the difference in the
     * encoding.
     */
    protected COSDictionary cosGetDict() {
        return dict;
    }

    @Override
    public COSObject cosGetObject() {
        return cosGetDict();
    }

    /**
     * Return the base encoding against which we defined the difference.
     *
     * @return Return the base encoding against which we defined the difference.
     */
    protected Encoding getBaseEncoding() {
        return baseEncoding;
    }

    @Override
    public int getDecoded(int codepoint) {
        int code = getDifferenceEncoding().getDecoded(codepoint);
        if (code == -1) {
            code = getBaseEncoding().getDecoded(codepoint);
        }
        return code;
    }

    @Override
    public int getDifferenceDecoded(int codePoint) {
        int code = getDifferenceEncoding().getDecoded(codePoint);
        if (code == -1) {
            return codePoint;
        }
        return code;
    }

    /**
     * Get the {@link MappedEncoding} built from the differences dictionary.
     *
     * @return The {@link MappedEncoding} built from the differences dictionary.
     */
    protected MappedEncoding getDifferenceEncoding() {
        return differenceEncoding;
    }

    @Override
    public String getDifferenceGlyphName(int codePoint) {
        String name = getDifferenceEncoding().getGlyphName(codePoint);
        if ((name == null) || name.equals(GlyphNameMap.GLYPH_NOTDEF)) {
            return null;
        }
        return name;
    }

    @Override
    public int getEncoded(int character) {
        int code = getDifferenceEncoding().getEncoded(character);
        if (code == -1) {
            code = getBaseEncoding().getEncoded(character);
        }
        return code;
    }

    @Override
    public int getEncoded(java.lang.String name) {
        int code = getDifferenceEncoding().getEncoded(name);
        if (code == -1) {
            code = getBaseEncoding().getEncoded(name);
        }
        return code;
    }

    @Override
    public String getGlyphName(int codePoint) {
        String name = getDifferenceEncoding().getGlyphName(codePoint);
        if (name == null || GlyphNameMap.GLYPH_NOTDEF.equals(name)) {
            name = getBaseEncoding().getGlyphName(codePoint);
        }
        return name;
    }

    @Override
    public String getName() {
        return "DifferenceEncoding";
    }

    @Override
    public boolean isFontSpecificEncoding() {
        return getBaseEncoding().isFontSpecificEncoding();
    }

    /**
     * Set the base encoding against which we defined the difference.
     *
     * @param newBaseEncoding The base encoding against which we defined the difference.
     */
    private void setBaseEncoding(Encoding newBaseEncoding) {
        baseEncoding = newBaseEncoding;
    }

    /**
     * Set the {@link MappedEncoding} decoded from the differences dictionary.
     *
     * @param newDifferenceEncoding The {@link MappedEncoding} decoded from the differences
     *                              dictionary.
     */
    protected void setDifferenceEncoding(MappedEncoding newDifferenceEncoding) {
        differenceEncoding = newDifferenceEncoding;
    }
}
