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
package de.intarsys.pdf.encoding;

import de.intarsys.pdf.cos.COSObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A MappedEncoding is an explicit map from a codepoint to a character.
 * <p>
 * <p>
 * A MappedEncoding can be constructed individually (as in a DifferenceEncoding)
 * or be one of the predefined well known encodings that are implemented using
 * unique instances.
 * </p>
 */
public class MappedEncoding extends Encoding {
    /**
     * The number of 1:1 mappings from character to code points.
     * <p>
     * <p>
     * This is done for the first 256 bytes of character to cache the most often
     * used chars.
     * </p>
     */
    private static final int ARRAY_MAPPING_SIZE = 256;

    /**
     * encode characters indexes
     */
    private Map indexedEncoding = new HashMap();

    /**
     * encode character names
     */
    private Map namedEncoding = new HashMap();

    /**
     * encode characters performance optimization
     */
    private int[] fastEncoding = new int[ARRAY_MAPPING_SIZE];

    /**
     * decode codepoints
     */
    private int[] indexDecoding = new int[ARRAY_MAPPING_SIZE];

    /**
     * decode character names. This is for performance reasons as it is needed
     * for computing a text's width.
     */
    private String[] nameDecoding = new String[ARRAY_MAPPING_SIZE];

    {
        for (int i = 0; i < ARRAY_MAPPING_SIZE; i++) {
            fastEncoding[i] = -1;
        }
        for (int i = 0; i < indexDecoding.length; i++) {
            indexDecoding[i] = -1;
        }
        for (int i = 0; i < nameDecoding.length; i++) {
            nameDecoding[i] = GlyphNameMap.GLYPH_NOTDEF;
        }
    }

    public MappedEncoding() {
        super();
    }

    /**
     * When constructing manually, one can define a map from
     * <code>codePoint</code> to <code>name</code> with this method.
     *
     * @param codePoint The codePoint from 0..255 where the character should be
     *                  mapped.
     * @param name      The name of the character to be mapped.
     */
    public void addEncoding(int codePoint, String name) {
        int unicode = GlyphNameMap.Standard.getUnicode(name);
        defineEntry(codePoint, unicode, name);
    }

    /**
     * Add a known complete tuple. THis is used if we do not have a name/unicode
     * standard map (for example in symbolic fonts).
     *
     * @param codePoint The codePoint from 0..255 where the character should be
     *                  mapped.
     * @param name      The name of the character to be mapped.
     * @param character The character value
     */
    public void addEncoding(int codePoint, String name, int character) {
        defineEntry(codePoint, character, name);
    }

    @Override
    public COSObject cosGetObject() {
        return null;
    }

    /**
     * Define an entry that establishes a relationship between codepoint,
     * character and Adobe glyph name.
     *
     * @param codepoint The codepoint.
     * @param character The character.
     * @param name      The Adobe glyph name.
     */
    protected void defineEntry(int codepoint, int character, String name) {
        // byte/name association
        nameDecoding[codepoint] = name;
        Integer codePointInteger = new Integer(codepoint);
        namedEncoding.put(name, codePointInteger);
        // byte character association
        indexDecoding[codepoint] = character;
        indexedEncoding.put(new Integer(character), codePointInteger);
        //
        if ((character >= 0) && (character < ARRAY_MAPPING_SIZE)) {
            fastEncoding[character] = codepoint;
        }
    }

    @Override
    public int getDecoded(int byteValue) {
        return indexDecoding[byteValue];
    }

    @Override
    public int getEncoded(int character) {
        if ((character >= 0) && (character < ARRAY_MAPPING_SIZE)) {
            return fastEncoding[character];
        } else {
            Integer ii = (Integer) indexedEncoding.get(new Integer(character));
            if (ii == null) {
                return -1;
            }
            return ii.intValue();
        }
    }

    @Override
    public int getEncoded(String name) {
        Integer codePoint = (Integer) namedEncoding.get(name);
        if (codePoint == null) {
            return -1;
        } else {
            return codePoint.intValue();
        }
    }

    @Override
    public String getGlyphName(int codePoint) {
        if ((codePoint < 0) || (codePoint > 255)) {
            return GlyphNameMap.GLYPH_NOTDEF;
        }
        return nameDecoding[codePoint];
    }

    /**
     * The internal representation of the decoding map from codepoints to
     * characters.
     *
     * @return The internal representation of the decoding map from codepoints
     * to characters.
     */
    protected int[] getIndexDecoding() {
        return indexDecoding;
    }

    /**
     * The internal representation of the encoding map from characters to
     * codepoints.
     *
     * @return The internal representation of the encoding map from characters
     * to codepoints.
     */
    protected Map getIndexedEncoding() {
        return indexedEncoding;
    }

    @Override
    public String getName() {
        return "MappedEncoding";
    }

    /**
     * The internal representation of the decoding map from codePoint to
     * character name.
     *
     * @return The internal representation of the decoding map from codePoint to
     * character name.
     */
    protected String[] getNameDecoding() {
        return nameDecoding;
    }

    /**
     * The internal representation of the encoding map from characters names to
     * codepoint.
     *
     * @return The internal representation of the encoding map from characters
     * names to codepoint.
     */
    protected Map getNamedEncoding() {
        return namedEncoding;
    }
}
