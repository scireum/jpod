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

import javax.crypto.Cipher;
import java.security.MessageDigest;

/**
 * An abstract superclass for the default implementation of
 * {@link ICryptHandler}. The concrete implementations provide the standard RC4
 * and AES algorithms.
 */
abstract public class StandardCryptHandler extends AbstractCryptHandler {
    /**
     * The cipher object to be used in encrypting/decrypting
     */
    protected Cipher cipher;

    /**
     * The message digest used throughout the encryption/decryption
     */
    protected MessageDigest md;

    /**
     * A buffer for the bytes stemming from the generation number
     */
    private byte[] generationBytes = new byte[2];

    /**
     * A buffer for the bytes stemming from the object number
     */
    private byte[] objectBytes = new byte[3];

    /**
     * The key that was computed for the encryption instance.
     */
    private byte[] cryptKey;

    abstract protected byte[] basicDecrypt(byte[] data, byte[] encryptionKey, int objectNum, int genNum)
            throws COSSecurityException;

    abstract protected byte[] basicEncrypt(byte[] data, byte[] encryptionKey, int objectNum, int genNum)
            throws COSSecurityException;

    protected void updateHash(byte[] encryptionKey, int objectNum, int genNum) {
        md.reset();
        md.update(encryptionKey);
        objectBytes[0] = (byte) (objectNum & 0xff);
        objectNum = objectNum >> 8;
        objectBytes[1] = (byte) (objectNum & 0xff);
        objectNum = objectNum >> 8;
        objectBytes[2] = (byte) (objectNum & 0xff);
        md.update(objectBytes);
        generationBytes[0] = (byte) (genNum & 0xff);
        genNum = genNum >> 8;
        generationBytes[1] = (byte) (genNum & 0xff);
        md.update(generationBytes);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.ISecurityHandler#decrypt(de.intarsys.pdf.cos.COSObjectKey,
     *      byte[])
     */
    public byte[] decrypt(COSObjectKey objectKey, byte[] bytes) throws COSSecurityException {
        if (bytes == null) {
            return null;
        }
        if (objectKey == null) {
            return bytes;
        }
        synchronized (this) {
            return basicDecrypt(bytes, getCryptKey(), objectKey.getObjectNumber(), objectKey.getGenerationNumber());
        }
    }

    public StandardCryptHandler() {
        super();
    }

    protected int length;

    public void initialize(byte[] pCryptKey) throws COSSecurityException {
        cryptKey = pCryptKey;
        length = cryptKey.length + 5;
        if (length > 16) {
            length = 16;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.ISecurityHandler#encrypt(de.intarsys.pdf.cos.COSObjectKey,
     *      byte[])
     */
    public byte[] encrypt(COSObjectKey objectKey, byte[] bytes) throws COSSecurityException {
        if (bytes == null) {
            return null;
        }
        if (objectKey == null) {
            return bytes;
        }
        synchronized (this) {
            return basicEncrypt(bytes, getCryptKey(), objectKey.getObjectNumber(), objectKey.getGenerationNumber());
        }
    }

    protected byte[] getCryptKey() {
        return cryptKey;
    }
}
