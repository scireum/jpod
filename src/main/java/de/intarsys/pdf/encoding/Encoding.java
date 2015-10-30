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

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.font.CMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * An Encoding defines the mapping from a codepoint to a character or glyph in
 * the font.
 * <p>
 * Most fonts use single byte encodings, so shortcut methods are provided in
 * favor of these.
 * <p>
 * Every font has a built in encoding that can be changed by definen an explicit
 * encoding with a PDFont. This explicit change may be in the form of a
 * "differences" encoding in an explicit dictionary or by means of a named
 * encoding, one of "MacRomanEncoding" or "WinAnsiEncoding".
 * </p>
 * Composite fonts use a still more complicated technique, based on {@link CMap}
 * instances, defining a mapping from multibyte codepoints to character
 * selectors.
 * <p>
 * <p>
 * todo 2 "MacExpertEncoding" is not supported.
 * </p>
 * <p>
 * <p>
 * If no encoding is found in the font implementation or the font dictionary,
 * "StandardEncoding" is used.
 * </p>
 */
public abstract class Encoding {
    public static final COSName CN_MacRomanEncoding = COSName.constant("MacRomanEncoding"); //$NON-NLS-1$

    public static final COSName CN_StandardEncoding = COSName.constant("StandardEncoding"); //$NON-NLS-1$

    public static final COSName CN_WinAnsiEncoding = COSName.constant("WinAnsiEncoding"); //$NON-NLS-1$

    public static final String NAME_a = "a"; //$NON-NLS-1$

    public static final String NAME_A = "A"; //$NON-NLS-1$

    public static final String NAME_aacute = "aacute"; //$NON-NLS-1$

    public static final String NAME_Aacute = "Aacute"; //$NON-NLS-1$

    public static final String NAME_acircumflex = "acircumflex"; //$NON-NLS-1$

    public static final String NAME_Acircumflex = "Acircumflex"; //$NON-NLS-1$

    public static final String NAME_acute = "acute"; //$NON-NLS-1$

    public static final String NAME_adieresis = "adieresis"; //$NON-NLS-1$

    public static final String NAME_Adieresis = "Adieresis"; //$NON-NLS-1$

    public static final String NAME_ae = "ae"; //$NON-NLS-1$

    public static final String NAME_AE = "AE"; //$NON-NLS-1$

    public static final String NAME_agrave = "agrave"; //$NON-NLS-1$

    public static final String NAME_Agrave = "Agrave"; //$NON-NLS-1$

    public static final String NAME_ampersand = "ampersand"; //$NON-NLS-1$

    public static final String NAME_aring = "aring"; //$NON-NLS-1$

    public static final String NAME_Aring = "Aring"; //$NON-NLS-1$

    public static final String NAME_asciicircum = "asciicircum"; //$NON-NLS-1$

    public static final String NAME_asciitilde = "asciitilde"; //$NON-NLS-1$

    public static final String NAME_asterisk = "asterisk"; //$NON-NLS-1$

    public static final String NAME_at = "at"; //$NON-NLS-1$

    public static final String NAME_atilde = "atilde"; //$NON-NLS-1$

    public static final String NAME_Atilde = "Atilde"; //$NON-NLS-1$

    public static final String NAME_b = "b"; //$NON-NLS-1$

    public static final String NAME_B = "B"; //$NON-NLS-1$

    public static final String NAME_backslash = "backslash"; //$NON-NLS-1$

    public static final String NAME_bar = "bar"; //$NON-NLS-1$

    public static final String NAME_braceleft = "braceleft"; //$NON-NLS-1$

    public static final String NAME_braceright = "braceright"; //$NON-NLS-1$

    public static final String NAME_bracketleft = "bracketleft"; //$NON-NLS-1$

    public static final String NAME_bracketright = "bracketright"; //$NON-NLS-1$

    public static final String NAME_breve = "breve"; //$NON-NLS-1$

    public static final String NAME_brokenbar = "brokenbar"; //$NON-NLS-1$

    public static final String NAME_bullet = "bullet"; //$NON-NLS-1$

    public static final String NAME_c = "c"; //$NON-NLS-1$

    public static final String NAME_C = "C"; //$NON-NLS-1$

    public static final String NAME_caron = "caron"; //$NON-NLS-1$

    public static final String NAME_ccedilla = "ccedilla"; //$NON-NLS-1$

    public static final String NAME_Ccedilla = "Ccedilla"; //$NON-NLS-1$

    public static final String NAME_cedilla = "cedilla"; //$NON-NLS-1$

    public static final String NAME_cent = "cent"; //$NON-NLS-1$

    public static final String NAME_circumflex = "circumflex"; //$NON-NLS-1$

    public static final String NAME_colon = "colon"; //$NON-NLS-1$

    public static final String NAME_comma = "comma"; //$NON-NLS-1$

    public static final String NAME_copyright = "copyright"; //$NON-NLS-1$

    public static final String NAME_currency = "currency"; //$NON-NLS-1$

    public static final String NAME_d = "d"; //$NON-NLS-1$

    public static final String NAME_D = "D"; //$NON-NLS-1$

    public static final String NAME_dagger = "dagger"; //$NON-NLS-1$

    public static final String NAME_daggerdbl = "daggerdbl"; //$NON-NLS-1$

    public static final String NAME_degree = "degree"; //$NON-NLS-1$

    public static final String NAME_dieresis = "dieresis"; //$NON-NLS-1$

    public static final String NAME_divide = "divide"; //$NON-NLS-1$

    public static final String NAME_dollar = "dollar"; //$NON-NLS-1$

    public static final String NAME_dotaccent = "dotaccent"; //$NON-NLS-1$

    public static final String NAME_dotlessi = "dotlessi"; //$NON-NLS-1$

    public static final String NAME_e = "e"; //$NON-NLS-1$

    public static final String NAME_E = "E"; //$NON-NLS-1$

    public static final String NAME_eacute = "eacute"; //$NON-NLS-1$

    public static final String NAME_Eacute = "Eacute"; //$NON-NLS-1$

    public static final String NAME_ecircumflex = "ecircumflex"; //$NON-NLS-1$

    public static final String NAME_Ecircumflex = "Ecircumflex"; //$NON-NLS-1$

    public static final String NAME_edieresis = "edieresis"; //$NON-NLS-1$

    public static final String NAME_Edieresis = "Edieresis"; //$NON-NLS-1$

    public static final String NAME_egrave = "egrave"; //$NON-NLS-1$

    public static final String NAME_Egrave = "Egrave"; //$NON-NLS-1$

    public static final String NAME_eight = "eight"; //$NON-NLS-1$

    public static final String NAME_ellipsis = "ellipsis"; //$NON-NLS-1$

    public static final String NAME_emdash = "emdash"; //$NON-NLS-1$

    public static final String NAME_endash = "endash"; //$NON-NLS-1$

    public static final String NAME_equal = "equal"; //$NON-NLS-1$

    public static final String NAME_eth = "eth"; //$NON-NLS-1$

    public static final String NAME_Eth = "Eth"; //$NON-NLS-1$

    public static final String NAME_Euro = "Euro"; //$NON-NLS-1$

    public static final String NAME_exclam = "exclam"; //$NON-NLS-1$

    public static final String NAME_exclamdown = "exclamdown"; //$NON-NLS-1$

    public static final String NAME_f = "f"; //$NON-NLS-1$

    public static final String NAME_F = "F"; //$NON-NLS-1$

    public static final String NAME_fi = "fi"; //$NON-NLS-1$

    public static final String NAME_five = "five"; //$NON-NLS-1$

    public static final String NAME_fl = "fl"; //$NON-NLS-1$

    public static final String NAME_florin = "florin"; //$NON-NLS-1$

    public static final String NAME_four = "four"; //$NON-NLS-1$

    public static final String NAME_fraction = "fraction"; //$NON-NLS-1$

    public static final String NAME_g = "g"; //$NON-NLS-1$

    public static final String NAME_G = "G"; //$NON-NLS-1$

    public static final String NAME_germandbls = "germandbls"; //$NON-NLS-1$

    public static final String NAME_grave = "grave"; //$NON-NLS-1$

    public static final String NAME_greater = "greater"; //$NON-NLS-1$

    public static final String NAME_guillemotleft = "guillemotleft"; //$NON-NLS-1$

    public static final String NAME_guillemotright = "guillemotright"; //$NON-NLS-1$

    public static final String NAME_guilsinglleft = "guilsinglleft"; //$NON-NLS-1$

    public static final String NAME_guilsinglright = "guilsinglright"; //$NON-NLS-1$

    public static final String NAME_h = "h"; //$NON-NLS-1$

    public static final String NAME_H = "H"; //$NON-NLS-1$

    public static final String NAME_hungarumlaut = "hungarumlaut"; //$NON-NLS-1$

    public static final String NAME_hyphen = "hyphen"; //$NON-NLS-1$

    public static final String NAME_i = "i"; //$NON-NLS-1$

    public static final String NAME_I = "I"; //$NON-NLS-1$

    public static final String NAME_iacute = "iacute"; //$NON-NLS-1$

    public static final String NAME_Iacute = "Iacute"; //$NON-NLS-1$

    public static final String NAME_icircumflex = "icircumflex"; //$NON-NLS-1$

    public static final String NAME_Icircumflex = "Icircumflex"; //$NON-NLS-1$

    public static final String NAME_idieresis = "idieresis"; //$NON-NLS-1$

    public static final String NAME_Idieresis = "Idieresis"; //$NON-NLS-1$

    public static final String NAME_igrave = "igrave"; //$NON-NLS-1$

    public static final String NAME_Igrave = "Igrave"; //$NON-NLS-1$

    public static final String NAME_j = "j"; //$NON-NLS-1$

    public static final String NAME_J = "J"; //$NON-NLS-1$

    public static final String NAME_k = "k"; //$NON-NLS-1$

    public static final String NAME_K = "K"; //$NON-NLS-1$

    public static final String NAME_l = "l"; //$NON-NLS-1$

    public static final String NAME_L = "L"; //$NON-NLS-1$

    public static final String NAME_less = "less"; //$NON-NLS-1$

    public static final String NAME_logicalnot = "logicalnot"; //$NON-NLS-1$

    public static final String NAME_lslash = "lslash"; //$NON-NLS-1$

    public static final String NAME_Lslash = "Lslash"; //$NON-NLS-1$

    public static final String NAME_m = "m"; //$NON-NLS-1$

    public static final String NAME_M = "M"; //$NON-NLS-1$

    public static final String NAME_macron = "macron"; //$NON-NLS-1$

    public static final String NAME_minus = "minus"; //$NON-NLS-1$

    public static final String NAME_mu = "mu"; //$NON-NLS-1$

    public static final String NAME_multiply = "multiply"; //$NON-NLS-1$

    public static final String NAME_n = "n"; //$NON-NLS-1$

    public static final String NAME_N = "N"; //$NON-NLS-1$

    public static final String NAME_nine = "nine"; //$NON-NLS-1$

    public static final String NAME_ntilde = "ntilde"; //$NON-NLS-1$

    public static final String NAME_Ntilde = "Ntilde"; //$NON-NLS-1$

    public static final String NAME_numbersign = "numbersign"; //$NON-NLS-1$

    public static final String NAME_o = "o"; //$NON-NLS-1$

    public static final String NAME_O = "O"; //$NON-NLS-1$

    public static final String NAME_oacute = "oacute"; //$NON-NLS-1$

    public static final String NAME_Oacute = "Oacute"; //$NON-NLS-1$

    public static final String NAME_ocircumflex = "ocircumflex"; //$NON-NLS-1$

    public static final String NAME_Ocircumflex = "Ocircumflex"; //$NON-NLS-1$

    public static final String NAME_odieresis = "odieresis"; //$NON-NLS-1$

    public static final String NAME_Odieresis = "Odieresis"; //$NON-NLS-1$

    public static final String NAME_oe = "oe"; //$NON-NLS-1$

    public static final String NAME_OE = "OE"; //$NON-NLS-1$

    public static final String NAME_ogonek = "ogonek"; //$NON-NLS-1$

    public static final String NAME_ograve = "ograve"; //$NON-NLS-1$

    public static final String NAME_Ograve = "Ograve"; //$NON-NLS-1$

    public static final String NAME_one = "one"; //$NON-NLS-1$

    public static final String NAME_onehalf = "onehalf"; //$NON-NLS-1$

    public static final String NAME_onequarter = "onequarter"; //$NON-NLS-1$

    public static final String NAME_onesuperior = "onesuperior"; //$NON-NLS-1$

    public static final String NAME_ordfeminine = "ordfeminine"; //$NON-NLS-1$

    public static final String NAME_ordmasculine = "ordmasculine"; //$NON-NLS-1$

    public static final String NAME_oslash = "oslash"; //$NON-NLS-1$

    public static final String NAME_Oslash = "Oslash"; //$NON-NLS-1$

    public static final String NAME_otilde = "otilde"; //$NON-NLS-1$

    public static final String NAME_Otilde = "Otilde"; //$NON-NLS-1$

    public static final String NAME_p = "p"; //$NON-NLS-1$

    public static final String NAME_P = "P"; //$NON-NLS-1$

    public static final String NAME_paragraph = "paragraph"; //$NON-NLS-1$

    public static final String NAME_parenleft = "parenleft"; //$NON-NLS-1$

    public static final String NAME_parenright = "parenright"; //$NON-NLS-1$

    public static final String NAME_percent = "percent"; //$NON-NLS-1$

    public static final String NAME_period = "period"; //$NON-NLS-1$

    public static final String NAME_periodcentered = "periodcentered"; //$NON-NLS-1$

    public static final String NAME_perthousand = "perthousand"; //$NON-NLS-1$

    public static final String NAME_plus = "plus"; //$NON-NLS-1$

    public static final String NAME_plusminus = "plusminus"; //$NON-NLS-1$

    public static final String NAME_q = "q"; //$NON-NLS-1$

    public static final String NAME_Q = "Q"; //$NON-NLS-1$

    public static final String NAME_question = "question"; //$NON-NLS-1$

    public static final String NAME_questiondown = "questiondown"; //$NON-NLS-1$

    public static final String NAME_quotedbl = "quotedbl"; //$NON-NLS-1$

    public static final String NAME_quotedblbase = "quotedblbase"; //$NON-NLS-1$

    public static final String NAME_quotedblleft = "quotedblleft"; //$NON-NLS-1$

    public static final String NAME_quotedblright = "quotedblright"; //$NON-NLS-1$

    public static final String NAME_quoteleft = "quoteleft"; //$NON-NLS-1$

    public static final String NAME_quoteright = "quoteright"; //$NON-NLS-1$

    public static final String NAME_quotesinglbase = "quotesinglbase"; //$NON-NLS-1$

    public static final String NAME_quotesingle = "quotesingle"; //$NON-NLS-1$

    public static final String NAME_r = "r"; //$NON-NLS-1$

    public static final String NAME_R = "R"; //$NON-NLS-1$

    public static final String NAME_registered = "registered"; //$NON-NLS-1$

    public static final String NAME_ring = "ring"; //$NON-NLS-1$

    public static final String NAME_s = "s"; //$NON-NLS-1$

    public static final String NAME_S = "S"; //$NON-NLS-1$

    public static final String NAME_scaron = "scaron"; //$NON-NLS-1$

    public static final String NAME_Scaron = "Scaron"; //$NON-NLS-1$

    public static final String NAME_section = "section"; //$NON-NLS-1$

    public static final String NAME_semicolon = "semicolon"; //$NON-NLS-1$

    public static final String NAME_seven = "seven"; //$NON-NLS-1$

    public static final String NAME_six = "six"; //$NON-NLS-1$

    public static final String NAME_slash = "slash"; //$NON-NLS-1$

    public static final String NAME_space = "space"; //$NON-NLS-1$

    public static final String NAME_sterling = "sterling"; //$NON-NLS-1$

    public static final String NAME_t = "t"; //$NON-NLS-1$

    public static final String NAME_T = "T"; //$NON-NLS-1$

    public static final String NAME_thorn = "thorn"; //$NON-NLS-1$

    public static final String NAME_Thorn = "Thorn"; //$NON-NLS-1$

    public static final String NAME_three = "three"; //$NON-NLS-1$

    public static final String NAME_threequarters = "threequarters"; //$NON-NLS-1$

    public static final String NAME_threesuperior = "threesuperior"; //$NON-NLS-1$

    public static final String NAME_tilde = "tilde"; //$NON-NLS-1$

    public static final String NAME_trademark = "trademark"; //$NON-NLS-1$

    public static final String NAME_two = "two"; //$NON-NLS-1$

    public static final String NAME_twosuperior = "twosuperior"; //$NON-NLS-1$

    public static final String NAME_u = "u"; //$NON-NLS-1$

    public static final String NAME_U = "U"; //$NON-NLS-1$

    public static final String NAME_uacute = "uacute"; //$NON-NLS-1$

    public static final String NAME_Uacute = "Uacute"; //$NON-NLS-1$

    public static final String NAME_ucircumflex = "ucircumflex"; //$NON-NLS-1$

    public static final String NAME_Ucircumflex = "Ucircumflex"; //$NON-NLS-1$

    public static final String NAME_udieresis = "udieresis"; //$NON-NLS-1$

    public static final String NAME_Udieresis = "Udieresis"; //$NON-NLS-1$

    public static final String NAME_ugrave = "ugrave"; //$NON-NLS-1$

    public static final String NAME_Ugrave = "Ugrave"; //$NON-NLS-1$

    public static final String NAME_underscore = "underscore"; //$NON-NLS-1$

    public static final String NAME_v = "v"; //$NON-NLS-1$

    public static final String NAME_V = "V"; //$NON-NLS-1$

    public static final String NAME_w = "w"; //$NON-NLS-1$

    public static final String NAME_W = "W"; //$NON-NLS-1$

    public static final String NAME_x = "x"; //$NON-NLS-1$

    public static final String NAME_X = "X"; //$NON-NLS-1$

    public static final String NAME_y = "y"; //$NON-NLS-1$

    public static final String NAME_Y = "Y"; //$NON-NLS-1$

    public static final String NAME_yacute = "yacute"; //$NON-NLS-1$

    public static final String NAME_Yacute = "Yacute"; //$NON-NLS-1$

    public static final String NAME_ydieresis = "ydieresis"; //$NON-NLS-1$

    public static final String NAME_Ydieresis = "Ydieresis"; //$NON-NLS-1$

    public static final String NAME_yen = "yen"; //$NON-NLS-1$

    public static final String NAME_z = "z"; //$NON-NLS-1$

    public static final String NAME_Z = "Z"; //$NON-NLS-1$

    public static final String NAME_zcaron = "zcaron"; //$NON-NLS-1$

    public static final String NAME_Zcaron = "Zcaron"; //$NON-NLS-1$

    public static final String NAME_zero = "zero"; //$NON-NLS-1$

    /**
     * "Create" one of the well known encodings.
     *
     * @param name The name of the encoding to create.
     * @return The encoding implementation.
     * @throws IllegalArgumentException When the encoding is not supported.
     */
    public static Encoding createNamed(COSName name) {
        if (CN_WinAnsiEncoding.equals(name)) {
            return WinAnsiEncoding.UNIQUE;
        }
        if (CN_MacRomanEncoding.equals(name)) {
            return MacRomanEncoding.UNIQUE;
        }
        if (CN_StandardEncoding.equals(name)) {
            return StandardEncoding.UNIQUE;
        }
        // CMap predefined encodings not implemented here!
        throw new IllegalArgumentException("encoding not supported");
    }

    /**
     * Create a new Encoding
     */
    protected Encoding() {
        super();
    }

    /**
     * Get an object that can be used as a representation of the receiver
     * encoding within <code>doc</code>.
     *
     * @return Get an object that can be used as a representation of the
     * receiver encoding within <code>doc</code>.
     */
    public abstract COSObject cosGetObject();

    /**
     * Create a reader on the InputStream <code>is</code> that uses the encoding
     * defined in the receiver.
     *
     * @param is The input stream to read.
     * @return Create a reader on the InputStream <code>is</code> that uses the
     * encoding defined in the receiver.
     */
    public Reader createReader(InputStream is) {
        return new MappedReader(is, this);
    }

    /**
     * Create a writer on the OutputStream <code>os</code> that uses the
     * encoding defined in the receiver.
     *
     * @param os The output stream to be written.
     * @return Create a writer on the OutputStream <code>os</code> that uses the
     * encoding defined in the receiver.
     */
    public Writer createWriter(OutputStream os) {
        return new MappedWriter(os, this);
    }

    /**
     * Decode an array of bytes to characters.
     *
     * @param bytes
     * @return The resulting {@link String}.
     */
    public String decode(byte[] bytes) {
        return decode(bytes, 0, bytes.length);
    }

    public String decode(byte[] bytes, int offset, int length) {
        StringWriter w = new StringWriter(length);
        decode(w, bytes, offset, length);
        return w.toString();
    }

    public void decode(Writer w, byte[] bytes, int offset, int length) {
        InputStream bis = new ByteArrayInputStream(bytes, offset, length);
        int temp = offset + length;
        try {
            int c = getNextDecoded(bis);
            while (c != -1) {
                w.write(c);
                c = getNextDecoded(bis);
            }
        } catch (IOException e) {
            // this does not happen
        }
    }

    /**
     * Encode an array of characters.
     *
     * @param value
     * @return The bytes representing the encoded characters.
     */
    public byte[] encode(char[] value) {
        return encode(value, 0, value.length);
    }

    public byte[] encode(char[] value, int offset, int length) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
        encode(bos, value, offset, length);
        return bos.toByteArray();
    }

    public void encode(OutputStream bos, char[] value, int offset, int length) {
        int temp = offset + length;
        try {
            for (int i = offset; i < temp; i++) {
                putNextDecoded(bos, value[i]);
            }
        } catch (IOException e) {
            // this does not happen
        }
    }

    /**
     * Encode a {@link String}.
     *
     * @param value The string to be encoded
     * @return Encode a {@link String}.
     */
    public byte[] encode(String value) {
        int temp = value.length();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(temp);
        try {
            for (int i = 0; i < temp; i++) {
                putNextDecoded(bos, value.charAt(i));
            }
        } catch (IOException e) {
            // this does not happen
        }
        return bos.toByteArray();
    }

    /**
     * Get the character for the codepoint or -1 if not available.
     *
     * @param codepoint The codepoint
     * @return Get the character for the codepoint or -1 if not available.
     */
    public abstract int getDecoded(int codepoint);

    public int getDifferenceDecoded(int codePoint) {
        return codePoint;
    }

    public String getDifferenceGlyphName(int codePoint) {
        return null;
    }

    /**
     * Get the codepoint for the the character or -1 if invalid.
     *
     * @param character The character to look up.
     * @return Get the codepoint for the the character or -1 if invalid.
     */
    public abstract int getEncoded(int character);

    /**
     * Get the codepoint for the the named character or -1 if invalid.
     *
     * @param name The character name to look up.
     * @return Get the codepoint for the the named character or -1 if invalid.
     */
    public abstract int getEncoded(String name);

    /**
     * Get the character name for a given encoded codepoint. If no mapping is
     * defined, return ".notdef".
     *
     * @param codepoint The encoded codepoint.
     * @return The glyph name of the character referenced by
     * <code>codepoint</code> or ".notdef".
     */
    public abstract String getGlyphName(int codepoint);

    /**
     * The name of this encoding.
     *
     * @return The name of this encoding.
     */
    public abstract String getName();

    /**
     * Get the next decoded character from the input stream. This method reads
     * as much bytes as needed by the encoding if it is a multibyte encoding and
     * returns the decoded character.
     *
     * @param is The input stream with encoded data.
     * @return The next character from the input stream.
     * @throws IOException
     */
    public int getNextDecoded(InputStream is) throws IOException {
        int value = is.read();
        if (value == -1) {
            return -1;
        }
        return getDecoded(value & 0xff);
    }

    /**
     * The next codepoint from the input stream. This method reads as much bytes
     * as needed by the encoding if it is a multibyte encoding and returns the
     * complete multibyte codepoint.
     *
     * @param is The input stream with encoded data.
     * @return The next codepoint from the input stream.
     * @throws IOException
     */
    public int getNextEncoded(InputStream is) throws IOException {
        return is.read();
    }

    /**
     * The codepoint for the character or a valid replacement if invalid.
     *
     * @param character The character to look up.
     * @return The codepoint for the character or a valid replacement if
     * invalid.
     */
    public int getValidEncoded(int character) {
        int i = getEncoded(character);
        if (i == -1) {
            return ' ';
        }
        return i;
    }

    /**
     * The codepoint for the character or a valid replacement if invalid.
     *
     * @param name The glyph name.
     * @return The codepoint for the character or a valid replacement if
     * invalid.
     */
    public int getValidEncoded(String name) {
        int i = getEncoded(name);
        if (i == -1) {
            return ' ';
        }
        return i;
    }

    /**
     * <code>true</code> if <code>chars</code> contains only characters that can
     * be encoded using this encoding.
     *
     * @param chars The array of characters
     * @return <code>true</code> if <code>chars</code> contains only characters
     * that can be encoded using this encoding.
     */
    public boolean isEncodable(char[] chars) {
        return isEncodable(chars, 0, chars.length);
    }

    /**
     * <code>true</code> if <code>chars</code> contains only characters that can
     * be encoded using this encoding.
     *
     * @param chars The array of characters
     * @return <code>true</code> if <code>chars</code> contains only characters
     * that can be encoded using this encoding.
     */
    public boolean isEncodable(char[] chars, int offset, int count) {
        for (int i = offset; i < offset + count; i++) {
            if (getEncoded(chars[i]) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * <code>true</code> if <code>value</code> contains only characters that can
     * be encoded using this encoding.
     *
     * @param value The string value to be checked
     * @return <code>true</code> if <code>value</code> contains only characters
     * that can be encoded using this encoding.
     */
    public boolean isEncodable(String value) {
        char[] chars = value.toCharArray();
        return isEncodable(chars);
    }

    /**
     * Answer true if this encoding is specific to and embedded into the font
     * program itself so that we can not derive any mapping information from
     * here.
     *
     * @return Answer true if this encoding is specific to and embedded into the
     * font program itself
     */
    public boolean isFontSpecificEncoding() {
        return false;
    }

    /**
     * Put the next character onto the input stream after encoding. This method
     * writes as much bytes as needed by the encoding if it is a multibyte
     * encoding.
     *
     * @param os        The stream to write the bytes.
     * @param character The character to be encoded.
     * @throws IOException
     */
    public void putNextDecoded(OutputStream os, int character) throws IOException {
        int i = getEncoded(character);
        if (i == -1) {
            os.write(' ');
        } else {
            os.write(i);
        }
    }

    /**
     * Put the next codepoint onto the input stream. This method writes as much
     * bytes as needed by the encoding if it is a multibyte encoding.
     *
     * @param os        The stream to write the bytes.
     * @param codepoint The codepoint.
     * @throws IOException
     */
    public void putNextEncoded(OutputStream os, int codepoint) throws IOException {
        os.write(codepoint);
    }

    @Override
    public String toString() {
        return getName();
    }
}
