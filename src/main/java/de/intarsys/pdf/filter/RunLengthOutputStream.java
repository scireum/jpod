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
 * An output stream support run length encoding defined in the PDF spec.
 * <p>
 * THIS IS NOT YET TESTED
 */
public class RunLengthOutputStream extends FilterOutputStream {

    private byte[] buffer = new byte[128];

    private int pos = 0;

    private int count = 0;

    private int last = -1;

    public RunLengthOutputStream(OutputStream out) {
        super(out);
    }

    @Override
    public void close() throws IOException {
        if (last != -1) {
            push(-1);
        }
        // write out buffers
        flushBuffer();
        flushCopies();
        out.write((byte) 128);
        super.close();
    }

    protected void flushBuffer() throws IOException {
        if (pos > 0) {
            out.write((byte) (257 - pos));
            out.write(buffer, 0, pos);
            pos = 0;
        }
    }

    protected void flushCopies() throws IOException {
        if (count > 1) {
            out.write((byte) count - 1);
            out.write((byte) last);
            count = 0;
            last = -1;
        }
    }

    protected void push(int b) throws IOException {
        if (b == last) {
            flushBuffer();
            count++;
            if (count == 128) {
                flushCopies();
            }
        } else {
            flushCopies();
            buffer[pos++] = (byte) last;
            if (pos == 128) {
                flushBuffer();
            }
            count = 1;
            last = b;
        }
    }

    @Override
    public void write(int b) throws IOException {
        // one byte readahead
        if (last != -1) {
            push(b);
        } else {
            count = 1;
            last = b;
        }
    }
}
