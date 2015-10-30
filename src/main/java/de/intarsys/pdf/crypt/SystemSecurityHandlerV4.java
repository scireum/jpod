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

import de.intarsys.pdf.cos.COSBoolean;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.filter.Filter;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link ISystemSecurityHandler} implementing /V 4 of the PDF spec.
 * <p>
 * The crypt handlers are created lazy and never changed - it is unwise to
 * change any encryption parameters afterwards!
 */
public class SystemSecurityHandlerV4 extends SystemSecurityHandler {

    public static final COSName CN_AESV2 = COSName.constant("AESV2"); //$NON-NLS-1$

    public static final COSName CN_CryptFilter = COSName.constant("CryptFilter");

    public static final COSName CN_None = COSName.constant("None"); //$NON-NLS-1$

    public static final COSName CN_V2 = COSName.constant("V2"); //$NON-NLS-1$

    public static final COSName DK_CFM = COSName.constant("CFM"); //$NON-NLS-1$

    public static final COSName DK_NAME = COSName.constant("Name"); //$NON-NLS-1$

    public static final COSName DK_TYPE = COSName.constant("Type");

    private Map<COSName, ICryptHandler> cryptHandlers = new HashMap<COSName, ICryptHandler>();

    protected SystemSecurityHandlerV4(COSDictionary dict) {
        super(dict);
    }

    public void cosAddCryptFilter(COSName name, COSDictionary cryptFilterDict) {
        COSDictionary encryptDict = cosGetEncryption();
        COSDictionary cryptFilters = encryptDict.get(COSEncryption.DK_CF).asDictionary();
        if (cryptFilters == null) {
            cryptFilters = COSDictionary.create();
            encryptDict.put(COSEncryption.DK_CF, cryptFilters);
        }
        cryptFilters.put(name, cryptFilterDict);
    }

    public COSDictionary cosGetCryptFilters() {
        COSDictionary encryptDict = cosGetEncryption();
        return encryptDict.get(COSEncryption.DK_CF).asDictionary();
    }

    public void cosRemoveCryptFilter(COSName name) {
        COSDictionary encryptDict = cosGetEncryption();
        COSDictionary cryptFilters = encryptDict.get(COSEncryption.DK_CF).asDictionary();
        if (cryptFilters == null) {
            return;
        }
        cryptFilters.remove(name);
    }

    protected COSDictionary createCryptFilterAES() {
        COSDictionary cryptFilterDict = COSDictionary.create();
        cryptFilterDict.put(DK_TYPE, CN_CryptFilter);
        cryptFilterDict.put(DK_CFM, CN_AESV2);
        cryptFilterDict.put(COSEncryption.DK_Length, COSInteger.create(128));
        return cryptFilterDict;
    }

    protected COSDictionary createCryptFilterArcFour() {
        COSDictionary cryptFilterDict = COSDictionary.create();
        cryptFilterDict.put(DK_TYPE, CN_CryptFilter);
        cryptFilterDict.put(DK_CFM, CN_V2);
        cryptFilterDict.put(COSEncryption.DK_Length, COSInteger.create(128));
        return cryptFilterDict;
    }

    protected COSDictionary createCryptFilterNone() {
        COSDictionary cryptFilterDict = COSDictionary.create();
        cryptFilterDict.put(DK_TYPE, CN_CryptFilter);
        cryptFilterDict.put(DK_CFM, CN_None);
        cryptFilterDict.put(COSEncryption.DK_Length, COSInteger.create(128));
        return cryptFilterDict;
    }

    protected ICryptHandler createCryptHandler(COSName name) throws COSSecurityException {
        if (COSEncryption.CN_IDENTITY.equals(name)) {
            // can't redefine...
            return new IdentityCryptHandler();
        }
        COSDictionary dict = getEncryption().getCryptFilterDict(name);
        if (dict == null) {
            // can't redefine...
            return new IdentityCryptHandler();
        }
        COSName method = dict.get(DK_CFM).asName();
        if (CN_V2.equals(method)) {
            StandardCryptHandler result = new ArcFourCryptHandler();
            result.initialize(getSecurityHandler().getCryptKey());
            return result;
        }
        if (CN_AESV2.equals(method)) {
            StandardCryptHandler result = new AESCryptHandler();
            result.initialize(getSecurityHandler().getCryptKey());
            return result;
        }
        return getSecurityHandler();
    }

    @Override
    public byte[] decryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException {
        COSName name = getEncryption().getCryptFilterNameFile();
        ICryptHandler handler = getCryptHandler(name);
        return handler.decrypt(key, bytes);
    }

    @Override
    public byte[] decryptStream(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException {
        COSName name = COSEncryption.CN_IDENTITY;
        if (COSStream.hasFilter(dict, Filter.CN_Filter_Crypt)) {
            COSDictionary decodeParams = COSStream.getDecodeParams(dict, Filter.CN_Filter_Crypt);
            if (decodeParams != null) {
                COSName paramName = decodeParams.get(DK_NAME).asName();
                if (paramName != null) {
                    name = paramName;
                }
            }
        } else {
            name = getEncryption().getCryptFilterNameString();
        }
        ICryptHandler handler = getCryptHandler(name);
        return handler.decrypt(key, bytes);
    }

    @Override
    public byte[] decryptString(COSObjectKey key, byte[] bytes) throws COSSecurityException {
        COSName name = getEncryption().getCryptFilterNameString();
        ICryptHandler handler = getCryptHandler(name);
        return handler.decrypt(key, bytes);
    }

    @Override
    public byte[] encryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException {
        COSName name = getEncryption().getCryptFilterNameFile();
        ICryptHandler handler = getCryptHandler(name);
        return handler.encrypt(key, bytes);
    }

    @Override
    public byte[] encryptStream(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException {
        if (!isEnabled()) {
            return bytes;
        }
        COSName name = COSEncryption.CN_IDENTITY;
        if (COSStream.hasFilter(dict, Filter.CN_Filter_Crypt)) {
            COSDictionary decodeParams = COSStream.getDecodeParams(dict, Filter.CN_Filter_Crypt);
            if (decodeParams != null) {
                COSName paramName = decodeParams.get(DK_NAME).asName();
                if (paramName != null) {
                    name = paramName;
                }
            }
        } else {
            name = getEncryption().getCryptFilterNameString();
        }
        ICryptHandler handler = getCryptHandler(name);
        return handler.encrypt(key, bytes);
    }

    @Override
    public byte[] encryptString(COSObjectKey key, byte[] bytes) throws COSSecurityException {
        if (!isEnabled()) {
            return bytes;
        }
        COSName name = getEncryption().getCryptFilterNameString();
        ICryptHandler handler = getCryptHandler(name);
        return handler.encrypt(key, bytes);
    }

    public ICryptHandler getCryptHandler(COSName name) throws COSSecurityException {
        ICryptHandler cryptHandler = cryptHandlers.get(name);
        if (cryptHandler == null) {
            cryptHandler = createCryptHandler(name);
            cryptHandlers.put(name, cryptHandler);
        }
        return cryptHandler;
    }

    @Override
    public int getLength() {
        // this is underspecified in PDF spec.
        // each crypt filter may have its own length, but how to create
        // owner and user password?
        return 128;
    }

    @Override
    public int getVersion() {
        return 4;
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        COSEncryption encryption = getEncryption();
        // this is in contrast to the PDF spec - but needed for Reader 7 to open
        // file correctly
        encryption.setFieldInt(COSEncryption.DK_Length, 128);
        COSName standardFilterName = COSName.constant("StdCF");
        setEncryptionMethodAES(standardFilterName);
        setEncryptionFilterStream(standardFilterName);
        setEncryptionFilterString(standardFilterName);
        setEncryptMetadata(true);
    }

    public void setEncryptionFilterStream(COSName name) {
        COSDictionary encryptDict = cosGetEncryption();
        encryptDict.put(COSEncryption.DK_StmF, name);
    }

    public void setEncryptionFilterString(COSName name) {
        COSDictionary encryptDict = cosGetEncryption();
        encryptDict.put(COSEncryption.DK_StrF, name);
    }

    public void setEncryptionMethodAES(COSName filterName) {
        cosAddCryptFilter(filterName, createCryptFilterAES());
    }

    public void setEncryptionMethodArcFour(COSName filterName) {
        cosAddCryptFilter(filterName, createCryptFilterArcFour());
    }

    public void setEncryptionMethodNone(COSName filterName) {
        cosAddCryptFilter(filterName, createCryptFilterNone());
    }

    public void setEncryptMetadata(boolean value) {
        COSDictionary encryptDict = cosGetEncryption();
        encryptDict.put(COSEncryption.DK_EncryptMetadata, COSBoolean.create(value));
    }
}
