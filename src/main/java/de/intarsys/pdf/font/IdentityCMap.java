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

import de.intarsys.pdf.cos.COSName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The generic 2 byte identity mapping (Identity-H and Identity-V).
 */
public class IdentityCMap extends CMap {

    public static final COSName CN_Identity_H = COSName.constant("Identity-H");

    public static final COSName CN_Identity_V = COSName.constant("Identity-V");

    public static IdentityCMap SINGLETON = new IdentityCMap();

    public static CMap getSingleton(COSName name) {
        if (name.equals(CN_Identity_H) || name.equals(CN_Identity_V)) {
            return IdentityCMap.SINGLETON;
        } else {
            return null;
        }
    }

    /**
     * @param object
     */
    protected IdentityCMap() {
        super(null);
    }

    @Override
    public char[] getChars(int codepoint) {
        int decoded = getDecoded(codepoint);
        if (decoded == -1) {
            return null;
        }
        return new char[]{(char) decoded};
    }

    @Override
    public int getDecoded(int value) {
        return value;
    }

    @Override
    public int getEncoded(int character) {
        return character;
    }

    @Override
    public int getNextDecoded(InputStream is) throws IOException {
        int hb = is.read();
        int lb = is.read();
        if (hb == -1 || lb == -1) {
            return -1;
        }
        return (hb << 8) + lb;
    }

    @Override
    public int getNextEncoded(InputStream is) throws IOException {
        int hb = is.read();
        int lb = is.read();
        if (hb == -1 || lb == -1) {
            return -1;
        }
        return (hb << 8) + lb;
    }

    @Override
    public void putNextDecoded(OutputStream os, int character) throws IOException {
        // write cid value high byte first
        os.write((character >> 8) & 0xff);
        os.write(character & 0xff);
    }

    @Override
    public void putNextEncoded(OutputStream os, int codepoint) throws IOException {
        // write cid value high byte first
        os.write((codepoint >> 8) & 0xff);
        os.write(codepoint & 0xff);
    }
}
