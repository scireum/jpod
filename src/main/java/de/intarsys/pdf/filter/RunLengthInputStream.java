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
package de.intarsys.pdf.filter;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link InputStream} implementing the run length algorithm as defined in
 * the PDF spec.
 */
public class RunLengthInputStream extends FilterInputStream {

    private byte[] buffer = new byte[128];

    private int length = 0;

    private int pos = 0;

    public RunLengthInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int available() throws IOException {
        throw new IOException("method not supported");
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    @Override
    public int read() throws IOException {
        if (pos >= length) {
            int i = in.read();
            if (i == -1 || i == 128) {
                return -1;
            }
            pos = 0;
            if (i <= 127) {
                length = i + 1;
                int index = 0;
                while (index < length) {
                    int count = in.read(buffer, index, length - index);
                    if (count == -1) {
                        // unexpected end... but use what we have read..
                        length = index;
                    } else {
                        index += count;
                    }
                }
            } else {
                length = 257 - i;
                int copy = in.read();
                if (copy == -1) {
                    // unexpected end
                    length = 0;
                }
                int index = 0;
                while (index < length) {
                    buffer[index++] = (byte) copy;
                }
            }
            return read();
        } else {
            return buffer[pos++] & 0xff;
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        }
        if ((off < 0) || (off > b.length) || (len < 0) || ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;

        int i = 1;
        try {
            for (; i < len; i++) {
                c = read();
                if (c == -1) {
                    break;
                }
                if (b != null) {
                    b[off + i] = (byte) c;
                }
            }
        } catch (IOException ignored) {
        }
        return i;
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("method not supported");
    }

    @Override
    public long skip(long n) throws IOException {
        throw new IOException("method not supported");
    }
}
