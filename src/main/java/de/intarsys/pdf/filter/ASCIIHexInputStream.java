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

import de.intarsys.tools.hex.HexTools;

/**
 * Input stream to decode binary data encoded in ascii representable byte codes.
 * The algorithm produces one byte of data from two ascii hex characters
 */
public class ASCIIHexInputStream extends FilterInputStream {
	private boolean eod = false;

	public ASCIIHexInputStream(InputStream in) {
		super(in);
	}

	static public boolean isWhitespace(int i) {
		return (i == ' ') || (i == '\t') || (i == '\r') || (i == '\n')
				|| (i == 12);
	}

	public int available() throws IOException {
		throw new IOException("method not supported");
	}

	public boolean markSupported() {
		return false;
	}

	public int read() throws IOException {
		int digit1 = 0;
		int digit2 = 0;
		int next = basicRead();
		if (next == -1) {
			return -1;
		}
		digit1 = HexTools.hexDigitToInt((char) next);
		if (digit1 == -1) {
			throw new IOException("<" + next + "> not a valid hex char");
		}
		next = basicRead();
		if (next != -1) {
			digit2 = HexTools.hexDigitToInt((char) next);
			if (digit2 == -1) {
				throw new IOException("<" + next + "> not a valid hex char");
			}
		}
		return (digit1 << 4) + digit2;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		if (b == null) {
			throw new NullPointerException();
		} else if ((off < 0) || (off > b.length) || (len < 0)
				|| ((off + len) > b.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return 0;
		}

		int c = read();
		if (c == -1) {
			return -1;
		}
		b[off] = (byte) c;

		int i = 1;
		for (; i < len; i++) {
			c = read();
			if (c == -1) {
				break;
			}
			if (b != null) {
				b[off + i] = (byte) c;
			}
		}
		return i;
	}

	public synchronized void reset() throws IOException {
		throw new IOException("method not supported");
	}

	public long skip(long n) throws IOException {
		throw new IOException("method not supported");
	}

	protected int basicRead() throws IOException {
		int next;
		if (eod) {
			return -1;
		}
		next = in.read();
		while (isWhitespace(next)) {
			next = in.read();
		}
		if ((next == -1) || (next == '>')) {
			eod = true;
			return -1;
		}
		return next;
	}
}
