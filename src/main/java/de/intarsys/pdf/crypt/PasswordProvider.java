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

import java.util.Map;

/**
 * A VM singleton for the PDF related {@link IPasswordProvider}.
 * <p>
 * Using this factory one can implement different strategies for the
 * {@link IAuthenticationHandler} to receive its passwords.
 * <p>
 * First, a password attached to the document itself (via the options when
 * opening) can be looked up. <br>
 * Second, an {@link IPasswordProvider} attached to the document itself (via the
 * options when opening) can be looked up. <br>
 * Third, a global {@link IPasswordProvider} attached to the current thread can
 * be looked up. <br>
 * Fourth, a globally unique {@link IPasswordProvider} can be looked up.
 * <p>
 * While an {@link ISecurityHandler} or {@link IAuthenticationHandler} does not
 * need to use these features, the standard implementations do.
 */
public class PasswordProvider {

    static public final String ATTR_PASSWORD = "password";

    static public final String ATTR_PASSWORDPROVIDER = "passwordProvider";

    private static final ThreadLocal<IPasswordProvider> threadLocal = new ThreadLocal<IPasswordProvider>();

    /**
     * The default factory.
     */
    private static IPasswordProvider Unique;

    /**
     * Return the unique factory.
     *
     * @return Return the unique factory.
     */
    static public IPasswordProvider get() {
        if (getThreadLocal() == null) {
            return Unique;
        }
        return getThreadLocal();
    }

    static public char[] getPassword(STDocument doc) {
        Object password = doc.getAttribute(ATTR_PASSWORD);
        if (password instanceof String) {
            return ((String) password).toCharArray();
        }
        if (password instanceof char[]) {
            return (char[]) password;
        }
        if (password instanceof byte[]) {
            return CharacterTools.toCharArray((byte[]) password);
        }
        return null;
    }

    static public IPasswordProvider getPasswordProvider(STDocument doc) {
        Object passwordProvider = doc.getAttribute(ATTR_PASSWORDPROVIDER);
        if (passwordProvider instanceof IPasswordProvider) {
            return (IPasswordProvider) passwordProvider;
        }
        return null;
    }

    private static IPasswordProvider getThreadLocal() {
        return threadLocal.get();
    }

    /**
     * Set the unique factory.
     *
     * @param unique The new unique factory.
     */
    static public void set(IPasswordProvider unique) {
        Unique = unique;
    }

    public static void setPassword(Map options, char[] password) {
        options.put(ATTR_PASSWORD, password);
    }

    public static void setPasswordProvider(Map options, IPasswordProvider passwordProvider) {
        options.put(ATTR_PASSWORDPROVIDER, passwordProvider);
    }

    public static void setThreadLocal(IPasswordProvider passwordProvider) {
        threadLocal.set(passwordProvider);
    }

    /**
     * Create a Factory
     */
    private PasswordProvider() {
        super();
    }
}
