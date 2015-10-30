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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A character map. This object can map from codepoints to CID's which can be
 * used to select glyphs in a CID keyed font object.
 */
abstract public class CMap extends COSBasedObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            if (object instanceof COSName) {
                CMap result = null;
                result = IdentityCMap.getSingleton((COSName) object);
                if (result == null) {
                    result = NamedCMap.loadCMap((COSName) object);
                }
                if (result == null) {
                    result = IdentityCMap.SINGLETON;
                }
                return result;
            } else if (object instanceof COSStream) {
                return new InternalCMap(object);
            } else {
                throw new IllegalArgumentException("CMap must be defined using COSStream or COSName");
            }
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static int toInt(byte[] bytes) {
        int result = 0;
        for (int i = 0; i < bytes.length; i++) {
            result = (result << 8) + (bytes[i] & 0xFF);
        }
        return result;
    }

    public static int toInt(byte[] bytes, int offset, int length) {
        int result = 0;
        int end = offset + length;
        for (int i = offset; i < end; i++) {
            result = (result << 8) + (bytes[i] & 0xFF);
        }
        return result;
    }

    protected CMap(COSObject object) {
        super(object);
    }

    /**
     * Get the char[] for the codepoint or null if not available. This is used
     * in /ToUnicode mappings where a codepoint may map to a string (as for
     * example in ligatures).
     *
     * @param codepoint The codepoint
     * @return The corresponding char[] for the codepoint.
     */
    abstract public char[] getChars(int codepoint);

    /**
     * Get the character for the codepoint or -1 if not available.
     *
     * @param codepoint The codepoint
     * @return Get the character for the codepoint or -1 if not available.
     */
    abstract public int getDecoded(int codepoint);

    /**
     * Get the codepoint for the the character or -1 if invalid.
     *
     * @param character The character to look up.
     * @return Get the codepoint for the the character or -1 if invalid.
     */
    abstract public int getEncoded(int character);

    /**
     * Get the next decoded character from the input stream. This method reads
     * as much bytes as needed by the encoding and returns the decoded
     * character.
     *
     * @param is The input stream with encoded data.
     * @return The next decoded character from the input stream.
     * @throws IOException
     */
    abstract public int getNextDecoded(InputStream is) throws IOException;

    /**
     * The next codepoint from the input stream. This method reads as much bytes
     * as needed by the encoding and returns the complete multibyte codepoint.
     *
     * @param is The input stream with encoded data.
     * @return The next codepoint from the input stream.
     * @throws IOException
     */
    abstract public int getNextEncoded(InputStream is) throws IOException;

    /**
     * Put the next character onto the input stream after encoding. This method
     * writes as much bytes as needed by the encoding.
     *
     * @param os        The stream to write the bytes.
     * @param character The character to be encoded.
     * @throws IOException
     */
    abstract public void putNextDecoded(OutputStream os, int character) throws IOException;

    /**
     * Put the next codepoint onto the input stream. This method writes as much
     * bytes as needed by the encoding.
     *
     * @param os        The stream to write the bytes.
     * @param codepoint The codepoint.
     * @throws IOException
     */
    abstract public void putNextEncoded(OutputStream os, int codepoint) throws IOException;
}
