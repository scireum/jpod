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
import de.intarsys.tools.authenticate.IPasswordProvider;
import de.intarsys.tools.string.CharacterTools;
import de.intarsys.tools.string.StringTools;

/**
 * This object implements the standard authentication strategy for the
 * {@link StandardSecurityHandler}.
 * <p>
 * This is extracted to allow for the most flexible authentication in
 * interactive and batch or server environments.
 */
public class StandardAuthenticationHandler implements IAuthenticationHandler {

    private IPasswordProvider passwordProvider;

    private int retries = 3;

    private boolean useDefaultAuthentication = true;

    /**
     * This {@link IAuthenticationHandler} implements the standard
     * authentication strategy for the built in security handlers.
     * <p>
     * Applying the default (empty user) authentication can be switched off.
     * <p>
     * Password acquiring can be modified using the {@link PasswordProvider}.
     *
     * @see de.intarsys.pdf.crypt.IAuthenticationHandler#authenticate(de.intarsys.pdf.crypt.ISecurityHandler)
     */
    public void authenticate(ISecurityHandler securityHandler) throws COSSecurityException {
        if (!(securityHandler instanceof StandardSecurityHandler)) {
            throw new COSSecurityException("security handler not supported"); //$NON-NLS-1$
        }
        StandardSecurityHandler standardSecurityHandler = (StandardSecurityHandler) securityHandler;
        if (isUseDefaultAuthentication()) {
            if (standardSecurityHandler.authenticateUser(null)) {
                return;
            }
        }
        STDocument stDocument = securityHandler.stGetDoc();
        char[] password = PasswordProvider.getPassword(stDocument);
        if (password != null) {
            if (!authenticate(standardSecurityHandler, password)) {
                throw new COSSecurityException("wrong password"); //$NON-NLS-1$
            }
        } else {
            IPasswordProvider passwordProvider = PasswordProvider.getPasswordProvider(stDocument);
            if (passwordProvider == null) {
                passwordProvider = getPasswordProvider();
            }
            if (passwordProvider == null) {
                throw new COSSecurityException("password missing"); //$NON-NLS-1$
            }
            authenticate(standardSecurityHandler, passwordProvider);
        }
    }

    protected boolean authenticate(StandardSecurityHandler standardSecurityHandler, char[] password)
            throws COSSecurityException {
        byte[] bytes = CharacterTools.toByteArray(password);
        if (standardSecurityHandler.authenticateOwner(bytes)) {
            return true;
        }
        if (standardSecurityHandler.authenticateUser(bytes)) {
            return true;
        }
        return false;
    }

    protected void authenticate(StandardSecurityHandler standardSecurityHandler, IPasswordProvider passwordProvider)
            throws COSSecurityException {
        char[] password = null;
        try {
            while (true) {
                // query password
                password = passwordProvider.getPassword();
                if (password == null) {
                    throw new COSSecurityException("password missing"); //$NON-NLS-1$
                }
                if (authenticate(standardSecurityHandler, password)) {
                    return;
                }
                retries--;
                if (getRetries() <= 0) {
                    throw new COSSecurityException("wrong password"); //$NON-NLS-1$
                }
            }
        } finally {
            StringTools.clear(password);
        }
    }

    public IPasswordProvider getPasswordProvider() {
        if (passwordProvider == null) {
            return PasswordProvider.get();
        }
        return passwordProvider;
    }

    public int getRetries() {
        return retries;
    }

    public boolean isUseDefaultAuthentication() {
        return useDefaultAuthentication;
    }

    public void setPasswordProvider(IPasswordProvider passwordProvider) {
        this.passwordProvider = passwordProvider;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void setUseDefaultAuthentication(boolean useDefaultAuthentication) {
        this.useDefaultAuthentication = useDefaultAuthentication;
    }
}
