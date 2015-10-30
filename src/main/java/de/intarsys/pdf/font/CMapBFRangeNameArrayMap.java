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
package de.intarsys.pdf.font;

import java.util.Iterator;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.encoding.GlyphNameMap;

/**
 * A special map from a character code range to a character name range.
 * 
 */
public class CMapBFRangeNameArrayMap extends CMapRangeMap {

	final private int[] destinationCodes;

	final private COSArray names;

	public CMapBFRangeNameArrayMap(byte[] start, byte[] end, COSArray names) {
		super(start, end);
		this.names = names;
		destinationCodes = new int[names.size()];
		int i = 0;
		for (Iterator it = names.iterator(); it.hasNext();) {
			COSObject name = ((COSObject) it.next()).asName();
			if (name == null) {
				destinationCodes[i] = 0;
			} else {
				destinationCodes[i] = GlyphNameMap.Standard.getUnicode(name
						.stringValue());
			}
			i++;
		}
	}

	@Override
	public char[] toChars(int codepoint) {
		int index = codepoint - start;
		if (index < 0 || index >= destinationCodes.length) {
			return null;
		}
		return new char[] { (char) (destinationCodes[index]) };
	}

	@Override
	public int toCID(int codepoint) {
		int index = codepoint - start;
		if (index < 0 || index >= destinationCodes.length) {
			return 0;
		}
		return destinationCodes[index];
	}

	@Override
	public int toCodepoint(int cid) {
		for (int i = 0; i < destinationCodes.length; i++) {
			if (cid == destinationCodes[i]) {
				return start + i;
			}
		}
		return 0;
	}
}
