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
package de.intarsys.pdf.st;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A parser for XRef streams.
 */
public class XRefStreamParser extends AbstractXRefParser {
	private int[] wSizeDefault = { 1, 2, 1 };

	private int[] wSize;

	private ByteArrayInputStream in;

	public XRefStreamParser(STDocument doc, COSDocumentParser parser) {
		super(doc, parser);
	}

	private void initWBytes(STStreamXRefSection xRef) {
		COSArray w = xRef.getW();
		if (w == null) {
			wSize = wSizeDefault;
			return;
		}
		wSize = new int[3];
		for (int i = 0; i < w.size(); i++) {
			wSize[i] = ((COSInteger) w.get(i)).intValue();
		}
		for (int i = w.size(); i < 3; i++) {
			wSize[i] = 0;
		}
	}

	protected void loadPrevious(IRandomAccess randomAccess,
			STXRefSection xRefSection) throws IOException, COSLoadException {
		int offset = xRefSection.getPreviousOffset();
		if (offset != -1) {
			AbstractXRefParser parser = new XRefStreamParser(getDoc(),
					getParser());
			randomAccess.seek(offset);
			STXRefSection xrefStream = parser.parse(randomAccess);
			xRefSection.setPrevious(xrefStream);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.st.AbstractXRefParser#parse(de.intarsys.tools.randomaccess.IRandomAccess)
	 */
	@Override
	public STXRefSection parse(IRandomAccess randomAcces) throws IOException,
			COSLoadException {
		STXRefSection xRefSection = parseXRef(randomAcces);
		loadPrevious(randomAcces, xRefSection);
		return xRefSection;
	}

	private void parseStreamContent(STStreamXRefSection section)
			throws IOException {
		initWBytes(section);
		in = new ByteArrayInputStream(section.cosGetStream().getDecodedBytes());
		COSArray index = section.getIndex();
		for (Iterator i = index.iterator(); i.hasNext();) {
			COSNumber cosStart = ((COSObject) i.next()).asNumber();
			if (!i.hasNext()) {
				continue;
			}
			COSNumber cosCount = ((COSObject) i.next()).asNumber();
			if (cosStart == null || cosCount == null) {
				continue;
			}
			int start = cosStart.intValue();
			int count = cosCount.intValue();
			for (int io = 0; io < count; io++) {
				int type = readType(in);
				switch (type) {
				case 0:
					section.addEntry(parseType0(in, start + io));
					break;
				case 1:
					section.addEntry(parseType1(in, start + io));
					break;
				case 2:
					section.addEntry(parseType2(in, start + io));
					break;
				default:
					parseTypeUnknown(in);
				}
			}
		}
	}

	private STXRefEntry parseType0(ByteArrayInputStream pIn, int objectNumber) {
		int nextFree = read(pIn, wSize[1], -1);
		int nextGenNum = read(pIn, wSize[2], -1);
		return new STXRefEntryFree(objectNumber, nextGenNum, nextFree);
	}

	private STXRefEntry parseType1(ByteArrayInputStream pIn, int objectNumber) {
		int offset = read(pIn, wSize[1], -1);
		int genNum = read(pIn, wSize[2], 0);
		return new STXRefEntryOccupied(objectNumber, genNum, offset);
	}

	private STXRefEntry parseType2(ByteArrayInputStream pIn, int objectNumber) {
		int streamObjectNumber = read(pIn, wSize[1], -1);
		int indexOfObject = read(pIn, wSize[2], 0);
		return new STXRefEntryCompressed(objectNumber, 0, streamObjectNumber,
				0, indexOfObject);
	}

	private void parseTypeUnknown(ByteArrayInputStream pIn) {
		read(pIn, wSize[1], -1);
		read(pIn, wSize[1], -1);
	}

	protected STXRefSection parseXRef(IRandomAccess randomAcces)
			throws IOException, COSLoadException {
		long offset = randomAcces.getOffset();
		// no security handler - /XRef streams may not be encrypted
		COSStream stream = (COSStream) getDoc().getParser()
				.parseIndirectObject(randomAcces, null);
		((COSIndirectObject) stream.containable()).setDirty(false);
		STStreamXRefSection xRefSection = new STStreamXRefSection(getDoc(),
				offset, stream);
		parseStreamContent(xRefSection);
		return xRefSection;
	}

	private int read(ByteArrayInputStream pIn, int numBytes, int defaultValue) {
		if (numBytes == 0) {
			return defaultValue;
		}
		int result = 0;
		for (int i = 0; i < numBytes; i++) {
			result <<= 8;
			result += pIn.read();
		}
		return result;
	}

	private int readType(ByteArrayInputStream pIn) {
		return read(pIn, wSize[0], 1);
	}
}
