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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Output stream to encode binary data with ASCII representable byte codes. The
 * algorithm produces five ASCII printing characters from four bytes of binary
 * data.
 * <p>
 * <p>
 * The exact specification can be found in [PDF] chapter 3.3.2.
 * </p>
 */
public class ASCII85OutputStream extends FilterOutputStream {
    static private final long HIGH_VALUE = 0xFFFFFFFFL;

    static private final long CONST_85 = 85L;

    private byte[] indata;

    private byte[] outdata;

    private boolean flushed;

    private int count;

    private int lineBreak;

    private int maxline;

    /**
     * Constructor.
     *
     * @param out The output stream to write to.
     */
    public ASCII85OutputStream(OutputStream out) {
        super(out);
        lineBreak = 36 * 2;
        maxline = 36 * 2;
        count = 0;
        indata = new byte[4];
        outdata = new byte[5];
        flushed = true;
    }

    /**
     * Set the line length.
     *
     * @param l The line length.
     */
    public void setLineLength(int l) {
        if (lineBreak > l) {
            lineBreak = l;
        }
        maxline = l;
    }

    /**
     * This will get the length of the line.
     *
     * @return The line length attribute.
     */
    public int getLineLength() {
        return maxline;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.OutputStream#flush()
     */
    public final void flush() throws IOException {
        if (flushed) {
            return;
        }
        if (count > 0) {
            for (int i = count; i < 4; i++) {
                indata[i] = 0;
            }
            transform();
            if (outdata[0] == 'z') {
                for (int i = 0; i < 5; i++) // expand 'z',
                {
                    outdata[i] = (byte) '!';
                }
            }
            for (int i = 0; i < (count + 1); i++) {
                out.write(outdata[i]);
                if (--lineBreak == 0) {
                    out.write('\n');
                    lineBreak = maxline;
                }
            }
        }
        if (--lineBreak == 0) {
            out.write('\n');
        }
        count = 0;
        lineBreak = maxline;
        flushed = true;
        super.flush();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.OutputStream#write(int)
     */
    public void write(int b) throws IOException {
        flushed = false;
        indata[count++] = (byte) b;
        if (count < 4) {
            return;
        }
        transform();
        for (int i = 0; i < 5; i++) {
            if (outdata[i] == 0) {
                break;
            }
            out.write(outdata[i]);
            if (--lineBreak == 0) {
                out.write('\n');
                lineBreak = maxline;
            }
        }
        count = 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.OutputStream#write(byte[], int, int)
     */
    public final void write(byte[] b, int off, int sz) throws IOException {
        for (int i = 0; i < sz; i++) {
            if (count < 3) {
                indata[count++] = b[off + i];
            } else {
                write(b[off + i]);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.OutputStream#close()
     */
    public void close() throws IOException {
        try {
            flush();
            out.write('~');
            out.write('>');
            out.write('\n');
            super.close();
        } finally {
            indata = null;
            outdata = null;
        }
    }

    /**
     * This will transform the next four ascii bytes.
     */
    protected void transform() {
        long word;
        word = ((((indata[0] << 8) | (indata[1] & 0xFF)) << 16) | ((indata[2] & 0xFF) << 8) | (indata[3] & 0xFF))
               & HIGH_VALUE;
        // System.out.println("word=0x"+Long.toString(word,16)+" "+word);
        if (word == 0) {
            outdata[0] = (byte) 'z';
            outdata[1] = 0;
            return;
        }

        long x;
        x = word / (CONST_85 * CONST_85 * CONST_85 * CONST_85);
        // System.out.println("x0="+x);
        outdata[0] = (byte) (x + '!');
        word -= (x * CONST_85 * CONST_85 * CONST_85 * CONST_85);

        x = word / (CONST_85 * CONST_85 * CONST_85);
        // System.out.println("x1="+x);
        outdata[1] = (byte) (x + '!');
        word -= (x * CONST_85 * CONST_85 * CONST_85);

        x = word / (CONST_85 * CONST_85);
        // System.out.println("x2="+x);
        outdata[2] = (byte) (x + '!');
        word -= (x * CONST_85 * CONST_85);

        x = word / CONST_85;
        // System.out.println("x3="+x);
        outdata[3] = (byte) (x + '!');

        // word-=x*85L;
        // System.out.println("x4="+(word % 85L));
        outdata[4] = (byte) ((word % CONST_85) + '!');
    }
}
