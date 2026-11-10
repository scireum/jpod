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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The mapping from Adobe glyph names to Unicode.
 * <p>
 * For a specification see
 * <a href="https://github.com/adobe-type-tools/agl-specification">Adobe glyph list specification</a>
 */
public class GlyphNameMap {
    /**
     * The number of 1:1 mappings from Unicode to code points.
     * <p>
     * This is done for the first 256 bytes to cache the most often used chars.
     */
    private static final int ARRAY_MAPPING_SIZE = 256;

    public static final GlyphNameMap Standard = new GlyphNameMap();

    public static final String GLYPH_NOTDEF = ".notdef"; //$NON-NLS-1$

    private static final Logger LOG = LogTools.getLogger(GlyphNameMap.class);

    private static final String ADOBE_GLYPH_LIST = "AdobeGlyphList.txt";

    private static final String PREFIX_UNI = "uni";

    private final Map<String, Integer> glyphNameToUnicode = new HashMap<>();

    private final Map<Integer, String> unicodeToGlyphName = new HashMap<>();

    /**
     * Unicode to glyph name optimization: most chars will be in the ascii range
     */
    private final String[] asciiToGlyphName = new String[ARRAY_MAPPING_SIZE];

    /**
     * Create a GlyphNameMap.
     */
    protected GlyphNameMap() {
        Arrays.fill(asciiToGlyphName, GLYPH_NOTDEF);
        load();
    }

    /**
     * Add an entry to the collection of known mappings.
     *
     * @param glyphName The adobe glyph name.
     * @param unicode   The Unicode code point.
     */
    protected void addEntry(String glyphName, int unicode) {
        glyphNameToUnicode.put(glyphName, unicode);
        unicodeToGlyphName.put(unicode, glyphName);
        if (unicode >= 0 && unicode < ARRAY_MAPPING_SIZE) {
            asciiToGlyphName[unicode] = glyphName;
        }
    }

    /**
     * Get the adobe glyph name for a Unicode code point or ".notdef" if not
     * available.
     *
     * @param unicode The Unicode code point to look up.
     * @return The adobe glyph name or ".notdef".
     */
    public String getGlyphName(int unicode) {
        if (unicode >= 0 && unicode < ARRAY_MAPPING_SIZE) {
            return asciiToGlyphName[unicode];
        }
        return unicodeToGlyphName.getOrDefault(unicode, GLYPH_NOTDEF);
    }

    /**
     * A read-only view of the map from glyph names to Unicode.
     *
     * @return A read-only view of the map from glyph names to Unicode.
     */
    protected Map<String, Integer> getGlyphNameToUnicode() {
        return Collections.unmodifiableMap(glyphNameToUnicode);
    }

    /**
     * Get the Unicode code point for an Adobe glyph name.
     * <p>
     * As defined by the Adobe glyph list specification, everything starting at the first period
     * is a variant suffix and is ignored, so "zero.tnum" maps to the character of "zero".
     * Names of the form "uniXXXX" denote the code point given in hex digits.
     * <p>
     * If the glyph name is unknown, -1 is returned.
     *
     * @param glyphName An adobe glyph name.
     * @return The Unicode code point for an Adobe glyph name or -1.
     */
    public int getUnicode(String glyphName) {
        if (glyphName == null) {
            return -1;
        }
        Integer result = glyphNameToUnicode.get(glyphName);
        if (result != null) {
            return result;
        }
        int suffixStart = glyphName.indexOf('.');
        if (suffixStart > 0) {
            return getUnicode(glyphName.substring(0, suffixStart));
        }
        return parseUniName(glyphName);
    }

    /**
     * Parses a name of the form "uniXXXX" without caching the result, as {@link #Standard} is
     * shared between threads.
     *
     * @param glyphName the glyph name without variant suffix
     * @return the denoted code point or -1, if the name isn't of that form
     */
    private int parseUniName(String glyphName) {
        if (!glyphName.startsWith(PREFIX_UNI)) {
            return -1;
        }
        try {
            int codePoint = Integer.parseInt(glyphName.substring(PREFIX_UNI.length()), 16);
            return Character.isValidCodePoint(codePoint) ? codePoint : -1;
        } catch (NumberFormatException exception) {
            // the name only starts with "uni" by chance, e.g. "unicorn"
            return -1;
        }
    }

    /**
     * A read-only view of the map from Unicode to glyph names.
     *
     * @return A read-only view of the map from Unicode to glyph names.
     */
    protected Map<Integer, String> getUnicodeToGlyphName() {
        return Collections.unmodifiableMap(unicodeToGlyphName);
    }

    protected void load() {
        try (InputStream is = getClass().getResourceAsStream(ADOBE_GLYPH_LIST)) {
            if (is == null) {
                LOG.log(Level.WARNING, "Adobe glyph list not found");
                return;
            }
            load(is);
        } catch (IOException exception) {
            LOG.log(Level.WARNING, "error loading Adobe glyph list", exception);
        }
    }

    /**
     * Loads the mappings of a glyph list.
     * <p>
     * Each line holds a glyph name and its code points in hex digits, separated by a semicolon,
     * e.g. "A;0041". If several code points are given, only the first one is used. Empty lines
     * and lines starting with "#" are skipped.
     *
     * @param is the stream to read the glyph list from
     * @throws IOException in case of an error while reading the stream
     */
    protected void load(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.US_ASCII));
        String line;
        while ((line = reader.readLine()) != null) {
            loadLine(line.trim());
        }
    }

    private void loadLine(String line) {
        int separatorIndex = line.indexOf(';');
        if (line.startsWith("#") || separatorIndex < 0) {
            return;
        }
        String codePoints = line.substring(separatorIndex + 1).trim();
        int codePointsEnd = codePoints.indexOf(' ');
        String firstCodePoint = codePointsEnd < 0 ? codePoints : codePoints.substring(0, codePointsEnd);
        addEntry(line.substring(0, separatorIndex), Integer.parseInt(firstCodePoint, 16));
    }
}
