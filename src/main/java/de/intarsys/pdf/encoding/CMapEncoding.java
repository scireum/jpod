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
import de.intarsys.pdf.font.CMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This is a wrapper implementation around the CMap definition in the /Encoding
 * entry for a Type0 font.
 */
public class CMapEncoding extends Encoding {

    final private CMap cmap;

    public CMapEncoding(CMap map) {
        super();
        this.cmap = map;
    }

    @Override
    public COSObject cosGetObject() {
        return null;
    }

    @Override
    public int getDecoded(int codepoint) {
        return cmap.getDecoded(codepoint);
    }

    @Override
    public int getEncoded(int character) {
        return cmap.getEncoded(character);
    }

    @Override
    public int getEncoded(String name) {
        return getEncoded(GlyphNameMap.Standard.getUnicode(name));
    }

    @Override
    public String getGlyphName(int codepoint) {
        return GlyphNameMap.Standard.getGlyphName(getDecoded(codepoint));
    }

    @Override
    public String getName() {
        return "CMapEncoding";
    }

    @Override
    public int getNextDecoded(InputStream is) throws IOException {
        return cmap.getNextDecoded(is);
    }

    @Override
    public int getNextEncoded(InputStream is) throws IOException {
        return cmap.getNextEncoded(is);
    }

    @Override
    public void putNextDecoded(OutputStream os, int character) throws IOException {
        cmap.putNextDecoded(os, character);
    }

    @Override
    public void putNextEncoded(OutputStream os, int codepoint) throws IOException {
        cmap.putNextEncoded(os, codepoint);
    }
}
