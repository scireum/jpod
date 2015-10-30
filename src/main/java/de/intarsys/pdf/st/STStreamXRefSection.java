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

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.writer.COSWriter;

/**
 * A section in a stream XRef.
 */
public class STStreamXRefSection extends STXRefSection {
	public static final COSName DK_Type = COSName.constant("Type");

	public static final COSName CN_Type_XRef = COSName.constant("XRef");

	public static final COSName DK_Index = COSName.constant("Index");

	public static final COSName DK_W = COSName.constant("W");

	private COSStream stream;

	public STStreamXRefSection(STDocument doc) {
		super(doc);
		this.stream = COSStream.create(COSDictionary.create());
		cosGetDict().put(DK_Type, CN_Type_XRef);
	}

	public STStreamXRefSection(STDocument doc, long offset, COSStream stream) {
		super(doc, offset);
		this.stream = stream;
	}

	@Override
	public COSDictionary cosGetDict() {
		return stream.getDict();
	}

	@Override
	public COSObject cosGetObject() {
		return stream;
	}

	public COSStream cosGetStream() {
		return stream;
	}

	protected void cosSetStream(COSStream pStream) {
		this.stream = pStream;
	}

	@Override
	public STXRefSection createSuccessor() {
		STStreamXRefSection newXRefSection = new STStreamXRefSection(getDoc());
		newXRefSection.cosGetDict().put(COSTrailer.DK_ID,
				cosGetDict().get(COSTrailer.DK_ID).copyShallow());
		newXRefSection.cosGetDict().put(COSTrailer.DK_Root,
				cosGetDict().get(COSTrailer.DK_Root));
		newXRefSection.cosGetDict().put(COSTrailer.DK_Info,
				cosGetDict().get(COSTrailer.DK_Info));
		COSObject encrypt = cosGetDict().get(COSTrailer.DK_Encrypt);
		if (!encrypt.isIndirect()) {
			encrypt = encrypt.copyShallow();
		}
		newXRefSection.cosGetDict().put(COSTrailer.DK_Encrypt, encrypt);
		newXRefSection.setPrevious(this);
		return newXRefSection;
	}

	protected COSArray getIndex() {
		COSArray index = cosGetDict().get(DK_Index).asArray();
		if (index == null) {
			index = COSArray.create(2);
			index.add(COSInteger.create(0));
			index.add(COSInteger.create(getSize()));
		}
		return index;
	}

	protected COSArray getW() {
		return cosGetDict().get(DK_W).asArray();
	}

	@Override
	public AbstractXRefWriter getWriter(COSWriter cosWriter) {
		return new XRefStreamWriter(cosWriter);
	}

	@Override
	protected boolean isStreamed() {
		return true;
	}

	public void setIndex(COSArray index) {
		cosGetDict().put(DK_Index, index);
	}

	protected void setW(COSArray wArray) {
		cosGetDict().put(DK_W, wArray);
	}
}
