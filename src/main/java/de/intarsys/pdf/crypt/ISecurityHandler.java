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

import de.intarsys.pdf.st.STDocument;

/**
 * The security handler as defined in the PDF spec. This is the exchangeable
 * behavior in the PDF security spec.
 * <p>
 * The {@link ISystemSecurityHandler} is asked for de/encryption, which is done
 * by himself for the defined standard encryptions, using {@link ICryptHandler}
 * instances for RC4 and AES. These {@link ICryptHandler} instances are
 * initialized using the encryption key provided by this
 * {@link ISecurityHandler}.
 * <p>
 * With /V 4 encryption, the application may ask for "transparent"
 * de/encryption, in which case it is forwarded to the installed
 * {@link ISecurityHandler} itself.
 */
public interface ISecurityHandler extends ICryptHandler {

    /**
     * Associate this {@link ISecurityHandler} with a {@link STDocument}.
     * <p>
     * The {@link ISecurityHandler} should add all its private information to
     * the document structure, in particular to the /Encrypt dictionary.
     *
     * @param doc
     */
    void attach(STDocument doc) throws COSSecurityException;

    /**
     * Perform an authentication.
     *
     * @throws COSSecurityException
     */
    void authenticate() throws COSSecurityException;

    /**
     * Disassociate this {@link ISecurityHandler} from {@link STDocument}.
     * <p>
     * The {@link ISecurityHandler} should remove all its private information
     * from the document structure, in particular from the /Encrypt dictionary.
     *
     * @param doc
     */
    void detach(STDocument doc) throws COSSecurityException;

    /**
     * The crypt key created by this {@link ISecurityHandler}. The crypt key is
     * used by the {@link ISystemSecurityHandler} (or the {@link ICryptHandler}
     * to initialize its cryptographic functions.
     * <p>
     * The crypt key is valid after the authentication.
     *
     * @return The crypt key created by this {@link ISecurityHandler}.
     */
    byte[] getCryptKey();

    /**
     * Initialize this {@link ISecurityHandler} with a {@link STDocument}.
     * <p>
     * The {@link ISecurityHandler} should initialize its state from the
     * information in the document structure, in particular from the /Encrypt
     * dictionary.
     *
     * @param doc
     * @throws COSSecurityException
     */
    void initialize(STDocument doc) throws COSSecurityException;

    /**
     * The associated {@link STDocument}.
     *
     * @return The associated {@link STDocument}.
     */
    STDocument stGetDoc();
}
