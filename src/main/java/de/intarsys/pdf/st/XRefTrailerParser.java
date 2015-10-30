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

import java.io.IOException;
import java.util.Arrays;

import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadError;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.parser.COSLoadWarning;
import de.intarsys.pdf.parser.PDFParser;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * A parser for XRef objects in "classical" format.
 */
public class XRefTrailerParser extends AbstractXRefParser {
	public XRefTrailerParser(STDocument doc, COSDocumentParser parser) {
		super(doc, parser);
	}

	protected void loadPrevious(IRandomAccess randomAccess,
			STXRefSection xRefSection) throws IOException, COSLoadException {
		int xrefStreamOffset = xRefSection.getXRefStmOffset();
		if (xrefStreamOffset != -1) {
			// this is for the mixed mode
			AbstractXRefParser parser = new XRefStreamParser(getDoc(),
					getParser());
			randomAccess.seek(xrefStreamOffset);
			try {
				STStreamXRefSection xrefStream = (STStreamXRefSection) parser
						.parse(randomAccess);
				((STTrailerXRefSection) xRefSection).setXRefStream(xrefStream);
			} catch (Exception e) {
				// todo create message
				// ignore, just like adobe does...
			}
		}
		int offset = xRefSection.getPreviousOffset();
		if (offset != -1) {
			AbstractXRefParser parser = new XRefTrailerParser(getDoc(),
					getParser());
			randomAccess.seek(offset);
			STXRefSection trailer = parser.parse(randomAccess);
			xRefSection.setPrevious(trailer);
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

	private STXRefEntry parseEntry(int objectNumber, byte[] entryBytes) {
		int offset = 0;
		for (int i = 0; i < 10; i++) {
			offset = (offset * 10) + (entryBytes[i] - '0');
		}

		int generation = 0;
		for (int i = 11; i < 16; i++) {
			generation = (generation * 10) + (entryBytes[i] - '0');
		}

		boolean inuse = (entryBytes[17] == 'n');
		if (inuse) {
			return new STXRefEntryOccupied(objectNumber, generation, offset);
		}
		return new STXRefEntryFree(objectNumber, generation, offset);
	}

	protected STXRefSection parseXRef(IRandomAccess randomAccess)
			throws IOException, COSLoadException {
		STTrailerXRefSection xRefSection = new STTrailerXRefSection(getDoc(),
				randomAccess.getOffset());
		parseXRefSection(randomAccess, xRefSection);
		xRefSection.cosSetDict(getDoc().getParser().parseTrailer(randomAccess));
		return xRefSection;
	}

	protected void parseXRefSection(IRandomAccess randomAccess,
			STTrailerXRefSection xRef) throws IOException, COSLoadException {
		getDoc().getParser().readSpaces(randomAccess);
		byte[] token = new byte[4];
		randomAccess.read(token);
		if (Arrays.equals(token, PDFParser.TOKEN_xref)) {
			token = new byte[PDFParser.TOKEN_trailer.length];
			long oldpos = -1;
			long pos = randomAccess.getOffset();

			// break from loop if we seem to repeat ourself
			while (oldpos != pos) {
				oldpos = pos;
				parseXRefSubsection(randomAccess, xRef);
				// maybe we got out of synch while stupidly parsing xref
				// todo 3 @kkr seems compliant, verify with test suite documents
				// once available
				getDoc().getParser().readSpaces(randomAccess);
				int c = randomAccess.read();
				randomAccess.seekBy(-1);
				if ((c == -1) || !PDFParser.isDigit(c)) {
					break;
				}
				pos = randomAccess.getOffset();
			}
		} else {
			COSLoadError e = new COSLoadError(
					"no 'xref' key found at character index "
							+ randomAccess.getOffset());
			handleError(e);
		}
	}

	protected void parseXRefSubsection(IRandomAccess randomAccess,
			STTrailerXRefSection xRef) throws IOException, COSLoadException {
		// read begining
		int beginningObject = getDoc().getParser().readInteger(randomAccess,
				false);

		// followed by white space
		int c = randomAccess.read();
		if (c == -1) {
			return;
		}
		if (c != 32) {
			COSLoadWarning pwarn = new COSLoadWarning(
					PDFParser.C_WARN_SINGLESPACE);
			pwarn.setHint(new Long(randomAccess.getOffset()));
			getParser().handleWarning(pwarn);
		}
		c = randomAccess.read();
		if (c == -1) {
			return;
		}
		randomAccess.seekBy(-1);
		if (!PDFParser.isDigit(c)) {
			COSLoadWarning pwarn = new COSLoadWarning(
					PDFParser.C_WARN_SINGLESPACE);
			pwarn.setHint(new Long(randomAccess.getOffset()));
			getParser().handleWarning(pwarn);
		}

		// read entry count
		int numEntries = getDoc().getParser().readInteger(randomAccess, true);
		// read spaces
		getDoc().getParser().readSpaces(randomAccess);
		byte[] buffer = new byte[20];
		STXRefEntry entry;
		for (int i = 0; i < numEntries; i++) {
			// read complete entry
			// todo 1 @mit this may be complete garbage
			c = randomAccess.read(buffer);
			if (c == -1) {
				// must fail anyway
				return;
			}
			entry = parseEntry(beginningObject + i, buffer);
			xRef.addEntry(entry);
		}
	}
}
