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
 * Input stream to decode binary data encoded in ASCII representable byte codes.
 * The algorithm maps four bytes of data to five ASCII printing characters (and
 * vice versa).
 * <p>
 * <p>
 * The exact specification can be found in [PDF] chapter 3.3.2.
 * </p>
 */
public class ASCII85InputStream extends FilterInputStream {
    private static final long CONST_85 = 85L;

    private static final long HIGH_BYTE = 0xFFL;

    private byte[] ascii;

    private byte[] b;

    private boolean eof;

    private int index;

    private int n;

    /**
     * Constructor
     *
     * @param is The input stream to actually read from.
     */
    public ASCII85InputStream(InputStream is) {
        super(is);
        index = 0;
        n = 0;
        eof = false;
        ascii = new byte[5];
        b = new byte[4];
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read()
     */
    @Override
    public final int read() throws IOException {
        if (index >= n) {
            if (eof) {
                return -1;
            }
            index = 0;

            int k;
            byte z;
            do {
                int zz = (byte) in.read();
                if (zz == -1) {
                    eof = true;
                    return -1;
                }
                z = (byte) zz;
            } while ((z == '\n') || (z == '\r') || (z == ' '));

            if ((z == '~') | (z == 'x')) {
                eof = true;
                ascii = null;
                b = null;
                n = 0;
                return -1;
            } else if (z == 'z') {
                b[0] = 0;
                b[1] = 0;
                b[2] = 0;
                b[3] = 0;
                n = 4;
            } else {
                ascii[0] = z; // may be EOF here....
                for (k = 1; k < 5; ++k) {
                    do {
                        int zz = (byte) in.read();
                        if (zz == -1) {
                            eof = true;
                            return -1;
                        }
                        z = (byte) zz;
                    } while ((z == '\n') || (z == '\r') || (z == ' '));
                    ascii[k] = z;
                    if ((z == '~') | (z == 'x')) {
                        break;
                    }
                }
                n = k - 1;
                if (n == 0) {
                    eof = true;
                    ascii = null;
                    b = null;
                    return -1;
                }
                if (k < 5) {
                    for (++k; k < 5; ++k) {
                        ascii[k] = 0x21;
                    }
                    eof = true;
                }

                // decode stream
                long t = 0;
                for (k = 0; k < 5; ++k) {
                    z = (byte) (ascii[k] - 0x21);
                    if ((z < 0) || (z > 93)) {
                        n = 0;
                        eof = true;
                        ascii = null;
                        b = null;
                        throw new IOException("Invalid data in Ascii85 stream");
                    }
                    t = (t * CONST_85) + z;
                }
                for (k = 3; k >= 0; --k) {
                    b[k] = (byte) (t & HIGH_BYTE);
                    t >>>= 8;
                }
            }
        }
        return b[index++] & 0xFF;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#read(byte[], int, int)
     */
    @Override
    public final int read(byte[] data, int offset, int len) throws IOException {
        if (eof && (index >= n)) {
            return -1;
        }
        for (int i = 0; i < len; i++) {
            if (index < n) {
                data[i + offset] = b[index++];
            } else {
                int t = read();
                if (t == -1) {
                    return i;
                }
                data[i + offset] = (byte) t;
            }
        }
        return len;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#available()
     */
    @Override
    public int available() throws IOException {
        throw new IOException("method not supported");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        ascii = null;
        eof = true;
        b = null;
        super.close();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#markSupported()
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#reset()
     */
    @Override
    public synchronized void reset() throws IOException {
        throw new IOException("method not supported");
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.InputStream#skip(long)
     */
    @Override
    public long skip(long bytes) throws IOException {
        throw new IOException("method not supported");
    }
}
