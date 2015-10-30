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

import de.intarsys.tools.logging.LogTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The mapping from Adobe glyph names to Unicode.
 * <p>
 * For a specification see
 * http://www.adobe.com/devnet/opentype/archives/glyph.html
 */
public class GlyphNameMap {
    /**
     * The number of 1:1 mappings from unicode to code points.
     * <p>
     * <p>
     * This is done for the first 256 bytes to cache the most often used chars.
     * </p>
     */
    private static final int ARRAY_MAPPING_SIZE = 256;

    public static final GlyphNameMap Standard = new GlyphNameMap();

    public static final String GLYPH_NOTDEF = ".notdef"; //$NON-NLS-1$

    private static Logger Log = LogTools.getLogger(GlyphNameMap.class);

    private static final String AdobeGlyphList = "AdobeGlyphList.txt";

    private static final String PREFIX_UNI = "uni";

    private Map glyphNameToUnicode = new HashMap();

    private Map unicodeToGlyphName = new HashMap();

    /**
     * unicode to glyph name optimization: most chars will be in the ascii range
     */
    private String[] asciiToGlyphName = new String[ARRAY_MAPPING_SIZE];

    /**
     * Create a GlyphNameMap.
     */
    protected GlyphNameMap() {
        super();
        for (int i = 0; i < asciiToGlyphName.length; i++) {
            asciiToGlyphName[i] = GLYPH_NOTDEF;
        }
        load();
    }

    /**
     * Add an entry to the collection of known mappings.
     *
     * @param glyphName The adobe glyph name.
     * @param unicode   The unicode code point.
     */
    protected void addEntry(String glyphName, int unicode) {
        glyphNameToUnicode.put(glyphName, Integer.valueOf(unicode));
        unicodeToGlyphName.put(Integer.valueOf(unicode), glyphName);
        if ((unicode >= 0) && (unicode < ARRAY_MAPPING_SIZE)) {
            asciiToGlyphName[unicode] = glyphName;
        }
    }

    /**
     * Get the adobe glyph name for a unicode code point or ".notdef" if not
     * available.
     *
     * @param unicode The unicode code point to look up.
     * @return The adobe glyph name or ".notdef".
     */
    public String getGlyphName(int unicode) {
        if ((unicode >= 0) && (unicode < ARRAY_MAPPING_SIZE)) {
            return asciiToGlyphName[unicode];
        } else {
            String glyphName = (String) unicodeToGlyphName.get(Integer.valueOf(unicode));
            if (glyphName == null) {
                return GLYPH_NOTDEF;
            } else {
                return glyphName;
            }
        }
    }

    /**
     * The internal representation of the map from glyph names to unicode.
     *
     * @return The internal representation of the map from glyph names to
     * unicode.
     */
    protected Map getGlyphNameToUnicode() {
        return glyphNameToUnicode;
    }

    /**
     * Get the unicode code point for an Adobe glyph name.
     * <p>
     * <p>
     * If the glyph name is unknown, -1 is returned.
     * </p>
     *
     * @param glyphName An adobe glyph name.
     * @return The unicode code point for an Adobe glyph name or -1.
     */
    public int getUnicode(String glyphName) {
        Integer result = (Integer) glyphNameToUnicode.get(glyphName);
        if (result == null) {
            // Allow direct access to unicode characters when prefixed by 'uni'.
            if (glyphName != null && glyphName.startsWith(PREFIX_UNI)) {
                try {
                    result = Integer.parseInt(glyphName.substring(3), 16);
                    addEntry(glyphName, result);
                } catch (NumberFormatException e) {
                    // this starts only by hazard with "uni.."
                }
            }
        }
        if (result == null) {
            return -1;
        }
        return result.intValue();
    }

    /**
     * The internal representation of the map from unicode to glyph names .
     *
     * @return The internal representation of the map from unicode to glyph
     * names .
     */
    protected Map getUnicodeToGlyphName() {
        return unicodeToGlyphName;
    }

    protected void load() {
        InputStream is = getClass().getResourceAsStream(AdobeGlyphList);
        try {
            load(is);
        } catch (IOException ignored) {
            Log.log(Level.WARNING, "error loading Adobe glyph list");
        }
    }

    protected void load(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String name = null;
        String value = null;
        int i = is.read();
        while (i != -1) {
            if (i == '\r' || i == '\n' || i == ' ') {
                //
            } else if (i == '#') {
                i = is.read();
                while (i != -1 && i != '\n') {
                    // skip rest of line
                    i = is.read();
                }
            } else {
                while (i != -1) {
                    if (i == ';') {
                        name = sb.toString();
                        sb.setLength(0);
                    } else if (i == ' ' | i == '\r' || i == '\n') {
                        value = sb.toString();
                        sb.setLength(0);
                        while (i != -1 && i != '\n') {
                            // skip rest of line
                            i = is.read();
                        }
                        break;
                    } else {
                        sb.append((char) i);
                    }
                    i = is.read();
                }
                addEntry(name, Integer.parseInt(value, 16));
            }
            i = is.read();
        }
    }
}
