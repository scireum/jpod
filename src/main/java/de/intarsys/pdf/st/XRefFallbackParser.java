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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadError;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.parser.PDFParser;
import de.intarsys.tools.randomaccess.IRandomAccess;

/**
 * <p>
 * A simple xref rebuilding parser.
 * </p>
 * <p>
 * The parser reads lines in the document and looks if the line begins with a
 * object definition, which looks like: number number "obj". Every object
 * definition found is added as a XRefEntry to the STDocument.
 * </p>
 * <p>
 * False object definitions can be read and they can cause a problem, if a
 * <b>real </b> object with the same object number is read after the false one.
 * On the other side: the document was already broke before this parser was
 * called.
 * </p>
 * 
 */
public class XRefFallbackParser extends AbstractXRefParser {
	private STTrailerXRefSection xRefSection;

	private List trailers = new ArrayList();

	public XRefFallbackParser(STDocument doc, COSDocumentParser parser) {
		super(doc, parser);
		this.xRefSection = new STTrailerXRefSection(doc);
	}

	/**
	 * Check if we found a root object
	 * 
	 * @throws IOException
	 * @throws COSLoadException
	 */
	private void checkXRefSections() throws IOException, COSLoadException {
		if (trailers.isEmpty()) {
			COSLoadError e = new COSLoadError("no trailer found");
			handleError(e);
		}
		boolean rootFound = false;
		for (int i = trailers.size() - 1; i >= 0; i--) {
			COSDictionary trailer = (COSDictionary) trailers.get(i);
			if (trailer.containsKey(COSTrailer.DK_Root)) {
				getXRefSection().cosSetDict(trailer);
				rootFound = true;
				break;
			}
		}
		if (rootFound == false) {
			COSLoadError e = new COSLoadError(
					"trailer doesn't contain a root entry");
			handleError(e);
		}
		getXRefSection().setSize(getXRefSection().getMaxObjectNumber());
	}

	protected STTrailerXRefSection getXRefSection() {
		return xRefSection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.st.AbstractXRefParser#parse(de.intarsys.tools.randomaccess.IRandomAccess)
	 */
	@Override
	public STXRefSection parse(IRandomAccess input) throws IOException,
			COSLoadException {
		input.seek(0);
		int objNumber;
		int genNumber;
		byte[] token;
		long offset;

		while (readUptoNewLine(input)) {
			offset = input.getOffset();
			try {
				COSDictionary trailer = getParser().parseTrailer(input);
				trailers.add(trailer);
				continue;
			} catch (IOException e) {
				// no trailer
			} catch (COSLoadException e) {
				// no trailer
			}
			try {
				objNumber = getParser().readInteger(input, true);
				if (objNumber != 0) {
					genNumber = getParser().readInteger(input, true);
					token = getParser().readToken(input);
					if (Arrays.equals(PDFParser.TOKEN_obj, token)) {
						getXRefSection().addEntry(
								new STXRefEntryOccupied(objNumber, genNumber,
										(int) offset));
						continue;
					}
				}
			} catch (IOException e) {
				// no obj
			}
			input.seek(offset);
		}

		checkXRefSections();
		return getXRefSection();
	}

	private boolean readUptoNewLine(IRandomAccess input) throws IOException {
		int i;
		while (true) {
			i = input.read();
			if (i == -1) {
				return false;
			}
			if (PDFParser.isEOL(i)) {
				if (i == PDFParser.CHAR_CR) {
					i = input.read();
					if (i == -1) {
						return false;
					}
					if (i != PDFParser.CHAR_LF) {
						input.seekBy(-1);
					}
				}
				return true;
			}
		}
	}
}
