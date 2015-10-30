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

import de.intarsys.pdf.cos.COSName;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * The {@link ISecurityHandler} implementing /R 4 of the PDF spec.
 */
public class StandardSecurityHandlerR4 extends StandardSecurityHandler {
    public static final String KEY_ALGORITHM = "RC4"; //$NON-NLS-1$

    public static final String CIPHER_ALGORITHM = "RC4"; //$NON-NLS-1$

    public static final String DIGEST_ALGORITHM = "MD5"; //$NON-NLS-1$

    public static final COSName DK_AuthEvent = COSName.constant("AuthEvent"); //$NON-NLS-1$

    /**
     * A byte sequence to be include in the hash under certain circumstances.
     */
    private static byte[] HIGH_BYTES = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

    public StandardSecurityHandlerR4() {
        super();
    }

    @Override
    public boolean authenticateOwner(byte[] owner) throws COSSecurityException {
        try {
            byte[] preparedOwner = prepareBytes(owner);
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            md.update(preparedOwner);
            byte[] key = md.digest();
            for (int i = 0; i < 50; i++) {
                md.update(key);
                key = md.digest();
            }
            // this is not sufficiently specified in the PDF spec
            // int length = getEncryption().getLength() / 8;
            int length = 16;
            byte[] encryptionKey = new byte[length];
            System.arraycopy(key, 0, encryptionKey, 0, length);
            //
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            if (cipher == null) {
                throw new COSSecurityException("RC4 cipher not found"); //$NON-NLS-1$
            }
            SecretKey skeySpec;
            byte[] encrypted = getO();
            byte[] tempEncKey = new byte[encryptionKey.length];
            for (int i = 19; i >= 0; i--) {
                for (int index = 0; index < encryptionKey.length; index++) {
                    tempEncKey[index] = (byte) (encryptionKey[index] ^ i);
                }
                skeySpec = new SecretKeySpec(tempEncKey, KEY_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                encrypted = cipher.doFinal(encrypted);
            }
            if (authenticateUser(encrypted)) {
                setActiveAccessPermissions(AccessPermissionsFull.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new COSSecurityException(e);
        }
    }

    @Override
    public boolean authenticateUser(byte[] user) throws COSSecurityException {
        byte[] entryU = getU();
        byte[] tempU = createUserPassword(user);
        if (entryU.length != tempU.length) {
            return false;
        }
        int length = 16;
        for (int i = 0; i < length; i++) {
            if (entryU[i] != tempU[i]) {
                return false;
            }
        }
        // if user is correctly authenticated, this key can be used for
        // decryption
        setCryptKey(createCryptKey(user));
        setActiveAccessPermissions(createAccessPermissions());
        return true;
    }

    @Override
    protected IAccessPermissions createAccessPermissions() {
        return new AccessPermissionsR3(getPermissionFlags());
    }

    @Override
    protected byte[] createCryptKey(byte[] password) throws COSSecurityException {
        try {
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            byte[] prepared = prepareBytes(password);
            md.update(prepared);
            md.update(getO());
            md.update(getPBytes());
            byte[] fd = getPermanentFileID();
            if (fd != null) {
                md.update(fd);
            }
            if (!isEncryptMetadata()) {
                md.update(HIGH_BYTES);
            }
            byte[] key = md.digest();
            // this is not sufficiently specified in the PDF spec
            // int length = getEncryption().getLength() / 8;
            int length = 16;
            for (int i = 0; i < 50; i++) {
                md.update(key, 0, length);
                key = md.digest();
            }
            byte[] result = new byte[length];
            System.arraycopy(key, 0, result, 0, length);
            return result;
        } catch (Exception e) {
            throw new COSSecurityException(e);
        }
    }

    @Override
    protected byte[] createOwnerPassword(byte[] owner, byte[] user) throws COSSecurityException {
        try {
            byte[] preparedOwner;
            if (owner == null) {
                preparedOwner = prepareBytes(user);
            } else {
                preparedOwner = prepareBytes(owner);
            }
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            md.update(preparedOwner);
            byte[] key = md.digest();
            // this is not sufficiently specified in the PDF spec
            // int length = getEncryption().getLength() / 8;
            int length = 16;
            for (int i = 0; i < 50; i++) {
                md.update(key, 0, length);
                key = md.digest();
            }
            byte[] encryptionKey = new byte[length];
            System.arraycopy(key, 0, encryptionKey, 0, length);
            //
            SecretKey skeySpec = new SecretKeySpec(encryptionKey, KEY_ALGORITHM);
            byte[] preparedUser = prepareBytes(user);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(preparedUser);

            byte[] tempKey = new byte[encryptionKey.length];
            for (int i = 1; i <= 19; i++) {
                for (int index = 0; index < encryptionKey.length; index++) {
                    tempKey[index] = (byte) (encryptionKey[index] ^ i);
                }
                skeySpec = new SecretKeySpec(tempKey, KEY_ALGORITHM);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                encrypted = cipher.doFinal(encrypted);
            }
            return encrypted;
        } catch (Exception e) {
            throw new COSSecurityException(e);
        }
    }

    @Override
    protected byte[] createUserPassword(byte[] user) throws COSSecurityException {
        try {
            byte[] encryptionKey = createCryptKey(user);
            MessageDigest md = MessageDigest.getInstance(DIGEST_ALGORITHM);
            md.update(PADDING);
            byte[] fd = getPermanentFileID();
            if (fd != null) {
                md.update(fd);
            }
            byte[] hash = md.digest();
            SecretKey skeySpec = new SecretKeySpec(encryptionKey, KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(hash);
            byte[] tempEncKey = new byte[encryptionKey.length];
            for (int i = 1; i <= 19; i++) {
                for (int index = 0; index < encryptionKey.length; index++) {
                    tempEncKey[index] = (byte) (encryptionKey[index] ^ i);
                }
                skeySpec = new SecretKeySpec(tempEncKey, KEY_ALGORITHM);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec);
                encrypted = cipher.doFinal(encrypted);
            }
            byte[] result = new byte[32];
            System.arraycopy(encrypted, 0, result, 0, 16);
            System.arraycopy(USER_R3_PADDING, 0, result, 16, 16);
            return result;
        } catch (Exception e) {
            throw new COSSecurityException(e);
        }
    }

    @Override
    public int getRevision() {
        return 4;
    }
}
