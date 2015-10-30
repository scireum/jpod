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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * An {@link ICryptHandler}implementing the AES algorithm.
 * 
 */
public class AESCryptHandler extends StandardCryptHandler {
	public static final String KEY_ALGORITHM = "AES"; //$NON-NLS-1$
	public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"; //$NON-NLS-1$
	public static final String DIGEST_ALGORITHM = "MD5"; //$NON-NLS-1$
	private int blockSize;

	@Override
	synchronized protected byte[] basicDecrypt(byte[] data,
			byte[] encryptionKey, int objectNum, int genNum)
			throws COSSecurityException {
		try {
			updateHash(encryptionKey, objectNum, genNum);
			byte[] keyBase = md.digest();
			IvParameterSpec ivSpec = new IvParameterSpec(data, 0, blockSize);
			SecretKey skeySpec = new SecretKeySpec(keyBase, 0, length,
					KEY_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivSpec);
			return cipher.doFinal(data, blockSize, data.length - blockSize);
		} catch (Exception e) {
			throw new COSSecurityException(e);
		}
	}

	@Override
	synchronized protected byte[] basicEncrypt(byte[] data,
			byte[] encryptionKey, int objectNum, int genNum)
			throws COSSecurityException {
		try {
			updateHash(encryptionKey, objectNum, genNum);
			byte[] keyBase = md.digest();
			byte[] initVector = cipher.getIV();
			if (initVector == null) {
				initVector = new byte[16];
			}
			IvParameterSpec ivSpec = new IvParameterSpec(initVector, 0,
					initVector.length);
			SecretKey skeySpec = new SecretKeySpec(keyBase, 0, length,
					KEY_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivSpec);
			byte[] encrypted = cipher.doFinal(data, 0, data.length);
			byte[] result = new byte[initVector.length + encrypted.length];
			System.arraycopy(initVector, 0, result, 0, initVector.length);
			System.arraycopy(encrypted, 0, result, initVector.length,
					encrypted.length);
			return result;
		} catch (Exception e) {
			throw new COSSecurityException(e);
		}
	}

	@Override
	public void initialize(byte[] pCryptKey) throws COSSecurityException {
		super.initialize(pCryptKey);
		try {
			md = MessageDigest.getInstance(DIGEST_ALGORITHM);
			cipher = Cipher.getInstance(CIPHER_ALGORITHM);
			blockSize = cipher.getBlockSize();
		} catch (NoSuchAlgorithmException e) {
			throw new COSSecurityException(e);
		} catch (NoSuchPaddingException e) {
			throw new COSSecurityException(e);
		}
	}

	@Override
	protected void updateHash(byte[] encryptionKey, int objectNum, int genNum) {
		super.updateHash(encryptionKey, objectNum, genNum);
		md.update(new byte[] { 0x73, 0x41, 0x6c, 0x54 });
	}

}
