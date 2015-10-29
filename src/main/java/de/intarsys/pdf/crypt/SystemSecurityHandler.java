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
package de.intarsys.pdf.crypt;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSCompositeObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.st.EnumWriteMode;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.pdf.st.STStreamXRefSection;

/**
 * An abstract superclass for implementing the PDF security process.
 * 
 */
abstract public class SystemSecurityHandler implements ISystemSecurityHandler {

	public static final int DEFAULT_LENGTH = 40;

	static public SystemSecurityHandler createFromSt(STDocument doc)
			throws COSSecurityException {
		SystemSecurityHandler systemSecurityHandler = null;
		COSDictionary dict = doc.cosGetTrailer().get(COSTrailer.DK_Encrypt)
				.asDictionary();
		if (dict != null) {
			int version = 0;
			COSNumber cosVersion = dict.get(COSEncryption.DK_V).asNumber();
			if (cosVersion != null) {
				version = cosVersion.intValue();
			}
			if (version == 0) {
				systemSecurityHandler = new SystemSecurityHandlerV0(dict);
			} else if (version == 1) {
				systemSecurityHandler = new SystemSecurityHandlerV1(dict);
			} else if (version == 2) {
				systemSecurityHandler = new SystemSecurityHandlerV2(dict);
			} else if (version == 3) {
				systemSecurityHandler = new SystemSecurityHandlerV3(dict);
			} else if (version == 4) {
				systemSecurityHandler = new SystemSecurityHandlerV4(dict);
			} else {
				throw new COSSecurityException("unsupported security version "
						+ version);
			}
			systemSecurityHandler.initialize(doc);
		}
		return systemSecurityHandler;
	}

	static public SystemSecurityHandler createNewV1() {
		COSDictionary dict = COSDictionary.create();
		dict.beIndirect();
		SystemSecurityHandler result = new SystemSecurityHandlerV1(dict);
		result.initializeFromScratch();
		return result;
	}

	static public SystemSecurityHandler createNewV2() {
		COSDictionary dict = COSDictionary.create();
		dict.beIndirect();
		SystemSecurityHandler result = new SystemSecurityHandlerV2(dict);
		result.initializeFromScratch();
		return result;
	}

	static public SystemSecurityHandler createNewV4() {
		COSDictionary dict = COSDictionary.create();
		dict.beIndirect();
		SystemSecurityHandler result = new SystemSecurityHandlerV4(dict);
		result.initializeFromScratch();
		return result;
	}

	final private COSDictionary cosEncryption;

	private COSArray currentCosIDs;

	final private COSEncryption encryption;

	private COSDictionary currentCosTrailer;

	private COSDictionary currentCosEncryption;

	private ISecurityHandler securityHandler;

	private STDocument stDoc;

	private COSCompositeObject[] contextStack = new COSCompositeObject[5];

	private short stackPtr = -1;

	private boolean enabled = true;

	protected SystemSecurityHandler(COSDictionary dict) {
		this.cosEncryption = dict;
		this.currentCosEncryption = cosEncryption;
		this.encryption = (COSEncryption) COSEncryption.META
				.createFromCos(dict);
	}

	public void attach(STDocument stDoc) throws COSSecurityException {
		this.stDoc = stDoc;
		currentCosTrailer = stDoc.cosGetTrailer();
		currentCosIDs = currentCosTrailer.get(COSTrailer.DK_ID).asArray();
		currentCosTrailer.put(COSTrailer.DK_Encrypt, cosGetEncryption());
		if (getSecurityHandler() != null) {
			getSecurityHandler().attach(stDoc);
		}
		forceFullWrite();
	}

	public void authenticate() throws COSSecurityException {
		securityHandler.authenticate();
	}

	public COSDictionary cosGetEncryption() {
		return cosEncryption;
	}

	public void detach(STDocument stDoc) throws COSSecurityException {
		if (getSecurityHandler() != null) {
			getSecurityHandler().detach(stDoc);
		}
		stDoc.cosGetTrailer().remove(COSTrailer.DK_Encrypt);
		forceFullWrite();
		// don't eliminate back reference
	}

	protected void forceFullWrite() {
		if (stGetDoc() == null) {
			return;
		}
		stGetDoc().setWriteModeHint(EnumWriteMode.FULL);
	}

	public COSCompositeObject getContextObject() {
		if (stackPtr < 0) {
			return null;
		}
		return contextStack[stackPtr];
	}

	public COSEncryption getEncryption() {
		return encryption;
	}

	public int getLength() {
		COSEncryption encryption = getEncryption();
		return encryption.getFieldInt(COSEncryption.DK_Length, DEFAULT_LENGTH);
	}

	public ISecurityHandler getSecurityHandler() {
		return securityHandler;
	}

	abstract public int getVersion();

	public void initialize(STDocument doc) throws COSSecurityException {
		this.stDoc = doc;
		initializeFromSt();
	}

	protected void initializeFromScratch() {
		COSEncryption encryption = getEncryption();
		encryption.cosSetField(COSEncryption.DK_V, COSInteger
				.create(getVersion()));
	}

	protected void initializeFromSt() throws COSSecurityException {
		COSEncryption encryption = getEncryption();
		securityHandler = SecurityHandlerFactory.get().getSecurityHandler(
				encryption);
		securityHandler.initialize(stGetDoc());
	}

	protected boolean isEnabled() {
		return enabled;
	}

	public COSCompositeObject popContextObject() {
		COSCompositeObject contextObject = contextStack[stackPtr--];
		// enable encryption when no longer in encryption dict of file id's
		if (contextObject == currentCosEncryption
				|| contextObject == currentCosIDs) {
			enabled = true;
		}
		if (contextObject instanceof COSStream) {
			COSDictionary dict = contextObject.asStream().getDict();
			if (dict != null
					&& dict.get(STStreamXRefSection.DK_Type).equals(
							STStreamXRefSection.CN_Type_XRef)) {
				// /XRef streams are not encrypted
				enabled = true;
			}
		}
		return contextObject;
	}

	public void pushContextObject(COSCompositeObject contextObject) {
		stackPtr++;
		if (stackPtr >= contextStack.length) {
			COSCompositeObject[] tempStack = new COSCompositeObject[contextStack.length + 5];
			System
					.arraycopy(contextStack, 0, tempStack, 0,
							contextStack.length);
			contextStack = tempStack;
		}
		contextStack[stackPtr] = contextObject;
		// do not encrypt within encryption dict and file id's
		if (contextObject == currentCosEncryption
				|| contextObject == currentCosIDs) {
			enabled = false;
		}
		if (contextObject instanceof COSStream) {
			COSDictionary dict = contextObject.asStream().getDict();
			if (dict != null
					&& dict.get(STStreamXRefSection.DK_Type).equals(
							STStreamXRefSection.CN_Type_XRef)) {
				// /XRef streams are not encrypted
				enabled = false;
			}
		}
	}

	public void setLength(int length) {
		COSEncryption encryption = getEncryption();
		encryption.setFieldInt(COSEncryption.DK_Length, length);
	}

	public void setSecurityHandler(ISecurityHandler pSecurityHandler)
			throws COSSecurityException {
		if (securityHandler != null) {
			securityHandler.detach(stGetDoc());
		}
		securityHandler = pSecurityHandler;
		if (securityHandler != null) {
			securityHandler.attach(stGetDoc());
		}
	}

	public STDocument stGetDoc() {
		return stDoc;
	}

	public void updateTrailer(COSDictionary trailer) {
		this.currentCosTrailer = trailer;
		this.currentCosIDs = currentCosTrailer.get(COSTrailer.DK_ID).asArray();
		this.currentCosEncryption = trailer.get(COSTrailer.DK_Encrypt)
				.asDictionary();
	}
}
