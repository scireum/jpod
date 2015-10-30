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
import java.io.InputStream;
import java.io.Reader;

/**
 * A reader that uses a PDF style encoding to map byte code to unicode.
 */
public class MappedReader extends Reader {
	/** the encoding we use to decode/encode the byte stream */
	private Encoding encoding;

	/** the stream we read from */
	private InputStream inStream;

	/**
	 * Create a MappedReader
	 * 
	 * @param is
	 *            The underlying byte stream.
	 * @param encoding
	 *            The encoding to use.
	 */
	public MappedReader(InputStream is, Encoding encoding) {
		super(is);
		setInStream(is);
		setEncoding(encoding);
	}

	/**
	 * @see java.io.Reader#close()
	 */
	@Override
	public void close() throws IOException {
		synchronized (lock) {
			if (inStream == null) {
				return;
			}
			inStream.close();
			inStream = null;
		}
	}

	/**
	 * Check to make sure that the stream has not been closed
	 * 
	 * @throws IOException
	 *             if the inStream is null.
	 */
	protected void ensureOpen() throws IOException {
		if (inStream == null) {
			throw new IOException("Stream closed");
		}
	}

	/**
	 * The encoding used by this reader.
	 * 
	 * @return The encoding used by this reader.
	 */
	public Encoding getEncoding() {
		return encoding;
	}

	/**
	 * Read characters into a portion of an array. This method will block until
	 * some input is available, an I/O error occurs, or the end of the stream is
	 * reached.
	 * 
	 * @param cbuf
	 *            Destination buffer
	 * @param off
	 *            Offset at which to start storing characters
	 * @param len
	 *            Maximum number of characters to read
	 * 
	 * @return The number of characters read, or -1 if the end of the stream has
	 *         been reached
	 * 
	 * @exception IOException
	 *                If an I/O error occurs
	 * @throws IndexOutOfBoundsException
	 */
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		synchronized (lock) {
			ensureOpen();
			if ((off < 0) || (off > cbuf.length) || (len < 0)
					|| ((off + len) > cbuf.length) || ((off + len) < 0)) {
				throw new IndexOutOfBoundsException();
			} else if (len == 0) {
				return 0;
			}

			int stop = off + len;
			int c = 0;
			for (int i = off; i < stop; i++) {
				c = encoding.getNextDecoded(inStream);
				if (c == -1) {
					if (i == off) {
						return -1;
					}
					return i - off;
				}
				cbuf[i] = (char) c;
			}
			return len;
		}
	}

	/**
	 * Set the encoding to be used by this reader.
	 * 
	 * @param encoding
	 *            The new encoding to use.
	 */
	private void setEncoding(Encoding encoding) {
		this.encoding = encoding;
	}

	protected void setInStream(java.io.InputStream newInStream) {
		inStream = newInStream;
	}
}
