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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSObjectKey;

/**
 * The {@link ISystemSecurityHandler} implementing /V 2 of the PDF spec.
 * <p>
 * The crypt handler is created lazy and never changed - it is unwise to change
 * any encryption parameters afterwards!
 * 
 */
public class SystemSecurityHandlerV2 extends SystemSecurityHandler {

	private ICryptHandler cryptHandler;

	protected SystemSecurityHandlerV2(COSDictionary dict) {
		super(dict);
	}

	protected ICryptHandler createCryptHandler() throws COSSecurityException {
		ArcFourCryptHandler result = new ArcFourCryptHandler();
		result.initialize(getSecurityHandler().getCryptKey());
		return result;
	}

	protected byte[] decrypt(COSObjectKey objectKey, byte[] bytes)
			throws COSSecurityException {
		return getCryptHandler().decrypt(objectKey, bytes);
	}

	public byte[] decryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes)
			throws COSSecurityException {
		return decrypt(key, bytes);
	}

	public byte[] decryptStream(COSObjectKey key, COSDictionary dict,
			byte[] bytes) throws COSSecurityException {
		return decrypt(key, bytes);
	}

	public byte[] decryptString(COSObjectKey key, byte[] bytes)
			throws COSSecurityException {
		return decrypt(key, bytes);
	}

	protected byte[] encrypt(COSObjectKey objectKey, byte[] bytes)
			throws COSSecurityException {
		if (!isEnabled()) {
			return bytes;
		}
		return getCryptHandler().encrypt(objectKey, bytes);
	}

	public byte[] encryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes)
			throws COSSecurityException {
		return encrypt(key, bytes);
	}

	public byte[] encryptStream(COSObjectKey key, COSDictionary dict,
			byte[] bytes) throws COSSecurityException {
		return encrypt(key, bytes);
	}

	public byte[] encryptString(COSObjectKey key, byte[] bytes)
			throws COSSecurityException {
		return encrypt(key, bytes);
	}

	protected ICryptHandler getCryptHandler() throws COSSecurityException {
		if (cryptHandler == null) {
			cryptHandler = createCryptHandler();
		}
		return cryptHandler;
	}

	@Override
	public int getVersion() {
		return 2;
	}

	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		COSEncryption encryption = getEncryption();
		encryption.setFieldInt(COSEncryption.DK_Length, 128);
	}
}
