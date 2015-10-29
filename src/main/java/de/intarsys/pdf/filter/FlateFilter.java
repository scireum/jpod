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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.tools.pool.GenericPool;
import de.intarsys.tools.pool.IPool;
import de.intarsys.tools.pool.IPoolObjectFactory;

/**
 * 
 */
public class FlateFilter extends StreamBasedFilter {

	static class PDFDeflaterOutputStream extends DeflaterOutputStream {

		public PDFDeflaterOutputStream(OutputStream out, Deflater def, int size) {
			super(out, def, size);
		}

		@Override
		public void close() throws IOException {
			super.close();
			FlateFilter.returnDeflater(def);
		}
	}

	static class PDFInflaterOutputStream extends InflaterInputStream {

		public PDFInflaterOutputStream(InputStream in, Inflater inf, int size) {
			super(in, inf, size);
		}

		@Override
		public void close() throws IOException {
			super.close();
			FlateFilter.returnInflater(inf);
		}
	}

	private static IPoolObjectFactory deflaterFactory = new IPoolObjectFactory() {

		public void destroyObject(Object obj) throws Exception {
			((Deflater) obj).end();
		}

		public void deactivateObject(Object obj) throws Exception {
			((Deflater) obj).reset();
		}

		public Object createObject() throws Exception {
			return new Deflater();
		}

		public void activateObject(Object obj) throws Exception {
			//
		}

	};

	private static IPoolObjectFactory inflaterFactory = new IPoolObjectFactory() {

		public void destroyObject(Object obj) throws Exception {
			((Inflater) obj).end();
		}

		public void deactivateObject(Object obj) throws Exception {
			((Inflater) obj).reset();
		}

		public Object createObject() throws Exception {
			return new Inflater();
		}

		public void activateObject(Object obj) throws Exception {
			//
		}

	};

	/**
	 * A pool for reusing the Deflater object.
	 */
	private static IPool DeflaterPool = new GenericPool(deflaterFactory);

	/**
	 * A pool for reusing the Inflater object.
	 */
	private static IPool InflaterPool = new GenericPool(inflaterFactory);

	/**
	 * 
	 */
	public FlateFilter(COSDictionary options) {
		super(options);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.filter.StreamBasedFilter#createInputFilterStream(java.io.InputStream)
	 */
	@Override
	protected InputStream createInputFilterStream(InputStream is)
			throws IOException {
		final Inflater inflater = borrowInflater();
		return new PDFInflaterOutputStream(is, inflater, 1024);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.filter.StreamBasedFilter#createOutputFilterStream(java.io.OutputStream)
	 */
	@Override
	protected OutputStream createOutputFilterStream(OutputStream os)
			throws IOException {
		final Deflater deflater = borrowDeflater();
		return new PDFDeflaterOutputStream(os, deflater, 1024);
	}

	protected static Deflater borrowDeflater() throws IOException {
		try {
			return (Deflater) DeflaterPool.checkout(-1);
		} catch (Exception e) {
			IOException ioe = new IOException("can't create deflater");
			ioe.initCause(e);
			throw ioe;
		}
	}

	protected static Inflater borrowInflater() throws IOException {
		try {
			return (Inflater) InflaterPool.checkout(-1);
		} catch (Exception e) {
			IOException ioe = new IOException("can't create inflater");
			ioe.initCause(e);
			throw ioe;
		}
	}

	protected static void returnInflater(Object inflater) {
		try {
			InflaterPool.checkin(inflater);
		} catch (Exception e) {
			// ignore
		}
	}

	protected static void returnDeflater(Object deflater) {
		try {
			DeflaterPool.checkin(deflater);
		} catch (Exception e) {
			// ignore
		}
	}

	@Override
	protected byte[] decode(byte[] source) throws IOException {
		byte[] decoded;
		IPrediction prediction;

		if ((source == null) || (source.length == 0)) {
			return new byte[0];
		}
		decoded = super.decode(source);
		if (getOptions() == null) {
			return decoded;
		}

		prediction = PredictionFactory.get().createPrediction(getOptions());
		return prediction.decode(decoded);
	}
}
