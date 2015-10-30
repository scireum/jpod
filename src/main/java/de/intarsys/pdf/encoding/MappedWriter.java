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

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * A writer that uses a PDF style encoding to map unicode to byte code.
 */
public class MappedWriter extends Writer {
    /**
     * the encoding we use to encode the characters
     */
    private Encoding encoding;

    /**
     * the byte stream we write on
     */
    private OutputStream outStream;

    /**
     * Create a MappedWriter
     *
     * @param out      The underlying output byte stream.
     * @param encoding The encoding to use.
     */
    public MappedWriter(OutputStream out, Encoding encoding) {
        super(out);
        setOutStream(out);
        setEncoding(encoding);
    }

    /**
     * @see java.io.Writer#close()
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (outStream == null) {
                return;
            }
            flush();
            outStream.close();
            outStream = null;
        }
    }

    /**
     * Check to make sure that the stream has not been closed
     *
     * @throws IOException if the outStream is null.
     */
    private void ensureOpen() throws IOException {
        if (outStream == null) {
            throw new IOException("Stream closed");
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Writer#flush()
     */
    @Override
    public void flush() throws IOException {
        synchronized (lock) {
            outStream.flush();
        }
    }

    /**
     * The encoding used by this writer.
     *
     * @return The encoding used by this writer.
     */
    public Encoding getEncoding() {
        return encoding;
    }

    /**
     * The underlying output stream.
     *
     * @return The underlying output stream.
     */
    protected java.io.OutputStream getOutStream() {
        return outStream;
    }

    /**
     * Set the encoding to be used by this writer.
     *
     * @param encoding THe new encoding to be used.
     */
    private void setEncoding(Encoding encoding) {
        this.encoding = encoding;
    }

    private void setOutStream(java.io.OutputStream newOutStream) {
        outStream = newOutStream;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.io.Writer#write(char[], int, int)
     */
    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        synchronized (lock) {
            ensureOpen();
            if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
                throw new IndexOutOfBoundsException();
            } else if (len == 0) {
                return;
            }
            int stop = off + len;
            for (int i = off; i < stop; i++) {
                encoding.putNextDecoded(outStream, cbuf[i]);
            }
        }
    }
}
