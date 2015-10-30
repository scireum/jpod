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

import de.intarsys.pdf.cos.COSCompositeObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.st.STDocument;

/**
 * This object is responsible to manage the PDF documents security context and
 * object de/encryption. It implements the security mechanics as described in
 * the PDF spec up to revision 4.
 * <p>
 * The {@link ISystemSecurityHandler} dispatches de/encryption to either the
 * standard {@link ICryptHandler} instances or (with /V 4 encryption) the
 * {@link ISecurityHandler} installed.
 */
public interface ISystemSecurityHandler {

    /**
     * Associate this {@link ISystemSecurityHandler} with a {@link STDocument}.
     * <p>
     * The {@link ISystemSecurityHandler} should add all its private information
     * to the document structure, in particular to the /Encrypt dictionary.
     *
     * @param doc
     */
    public void attach(STDocument doc) throws COSSecurityException;

    /**
     * Perform an authentication. Authentication is in all versions forwarded to
     * the {@link ISecurityHandler}.
     *
     * @throws COSSecurityException
     */
    public void authenticate() throws COSSecurityException;

    public byte[] decryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException;

    public byte[] decryptStream(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException;

    public byte[] decryptString(COSObjectKey key, byte[] bytes) throws COSSecurityException;

    /**
     * Disassociate this {@link ISystemSecurityHandler} from {@link STDocument}.
     * <p>
     * The {@link ISystemSecurityHandler} should remove all its private
     * information from the document structure, in particular from the /Encrypt
     * dictionary.
     *
     * @param doc
     */
    public void detach(STDocument doc) throws COSSecurityException;

    public byte[] encryptFile(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException;

    public byte[] encryptStream(COSObjectKey key, COSDictionary dict, byte[] bytes) throws COSSecurityException;

    public byte[] encryptString(COSObjectKey key, byte[] bytes) throws COSSecurityException;

    /**
     * The currently active container object in a read or write process.
     *
     * @return The currently active container object.
     */
    public COSCompositeObject getContextObject();

    /**
     * The length of the encryption key in bits.
     *
     * @return The length of the encryption key in bits.
     */
    public int getLength();

    /**
     * The associated pluggable {@link ISecurityHandler}.
     * <p>
     * By default this is one of the {@link StandardSecurityHandler} instances,
     * implementing security behavior of the /Standard security defined in PDF
     * spec.
     *
     * @return The associated pluggable {@link ISecurityHandler}.
     */
    public ISecurityHandler getSecurityHandler();

    /**
     * Initialize this {@link ISystemSecurityHandler} with a {@link STDocument}.
     * <p>
     * The {@link ISystemSecurityHandler} should initialize its state from the
     * information in the document structure, in particular from the /Encrypt
     * dictionary.
     *
     * @param doc
     * @throws COSSecurityException
     */
    public void initialize(STDocument doc) throws COSSecurityException;

    /**
     * Pop the topmost container. This is called from the writer to
     * "contextualize" the encryption process.
     *
     * @return The previously active container.
     */
    public COSCompositeObject popContextObject();

    /**
     * Push the current container. This is called from the writer to
     * "contextualize" the encryption process.
     *
     * @param object The new active container.
     */
    public void pushContextObject(COSCompositeObject object);

    /**
     * Assign a new {@link ISecurityHandler}.
     *
     * @param securityHandler The new {@link ISecurityHandler}.
     * @throws COSSecurityException
     */
    public void setSecurityHandler(ISecurityHandler securityHandler) throws COSSecurityException;

    /**
     * The associated {@link STDocument}.
     *
     * @return The associated {@link STDocument}.
     */
    public STDocument stGetDoc();

    /**
     * Upon writing a new trailer dictionary is created and must be propagated
     * to the {@link ISystemSecurityHandler}.
     *
     * @param trailer
     */
    public void updateTrailer(COSDictionary trailer);
}
