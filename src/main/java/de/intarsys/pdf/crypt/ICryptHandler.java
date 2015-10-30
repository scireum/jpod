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

import de.intarsys.pdf.cos.COSObjectKey;

/**
 * The encryption/decryption algorithm for a PDF document. This object does the
 * real crypt work, whereas the context like initialization steps,
 * authentication, authorization is done by the {@link ISecurityHandler} and
 * {@link ISystemSecurityHandler}.
 * <p>
 * PDF security /V4 defines two standard algorithms, RC4 and AES based.
 * Additionally a transparent algorithm provided by a {@link ISecurityHandler}
 * can be used.
 */
public interface ICryptHandler {
	/**
	 * Decrypt any bytes in the context of COSObject referenced by the provided
	 * key.
	 * 
	 * @param key
	 *            of the object which provides the context
	 * @param bytes
	 *            to decrypt
	 * @return the decrypted bytes
	 * @throws COSSecurityException
	 */
	public byte[] decrypt(COSObjectKey key, byte[] bytes)
			throws COSSecurityException;

	/**
	 * Encrypt any bytes in the context of COSObject referenced by the provided
	 * key.
	 * 
	 * @param key
	 *            of the object which provides the context
	 * @param bytes
	 *            to decrypt
	 * @return the encrypted bytes
	 * @throws COSSecurityException
	 */
	public byte[] encrypt(COSObjectKey key, byte[] bytes)
			throws COSSecurityException;

}
