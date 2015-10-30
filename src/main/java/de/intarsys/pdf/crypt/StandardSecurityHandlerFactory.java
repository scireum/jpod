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

/**
 * A standard implementation for the {@link ISecurityHandlerFactory}.
 */
public class StandardSecurityHandlerFactory implements ISecurityHandlerFactory {
    public static final COSName CN_Standard = COSName.constant("Standard"); //$NON-NLS-1$

    protected StandardSecurityHandlerFactory() {
        super();
    }

    public static final COSName DK_R = COSName.constant("R"); //$NON-NLS-1$

    @Override
    public ISecurityHandler getSecurityHandler(COSEncryption encryption) throws COSSecurityException {
        COSName name = encryption.getFilter();
        if (name == null) {
            throw new COSSecurityException("security handler not specified"); //$NON-NLS-1$
        }
        if (name.equals(CN_Standard)) {
            int revision = encryption.getFieldInt(DK_R, 0);
            if (revision == 2) {
                return new StandardSecurityHandlerR2();
            } else if (revision == 3) {
                return new StandardSecurityHandlerR3();
            } else if (revision == 4) {
                return new StandardSecurityHandlerR4();
            } else {
                return new StandardSecurityHandlerR2();
            }
        }

        // maybe provide a registry some day
        throw new COSSecurityException("no security handler '" //$NON-NLS-1$
                                       + name.stringValue() + "'"); //$NON-NLS-1$
    }
}
