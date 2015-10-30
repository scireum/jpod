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
package de.intarsys.pdf.font.outlet;

import de.intarsys.tools.provider.ProviderTools;

import java.util.Iterator;

/**
 * A singleton to access the current {@link IFontOutlet}.
 */
public class FontOutlet {

    private static boolean lookupProviders = true;

    private static IFontOutlet Unique;

    private FontOutlet() {
    }

    protected static IFontOutlet findProviders() {
        Iterator<IFontOutlet> ps = ProviderTools.providers(IFontOutlet.class);
        while (ps.hasNext()) {
            try {
                return ps.next();
            } catch (Throwable e) {
                // ignore and try on
            }
        }
        return null;
    }

    /**
     * The {@link IFontOutlet} singleton.
     *
     * @return The {@link IFontOutlet} singleton.
     */
    public static IFontOutlet get() {
        if (Unique == null) {
            init();
        }
        return Unique;
    }

    protected static void init() {
        if (lookupProviders) {
            Unique = findProviders();
        }
        if (Unique == null) {
            Unique = new NullFontOutlet();
        }
    }

    public static boolean isLookupProviders() {
        return lookupProviders;
    }

    /**
     * Set the {@link IFontOutlet} singleton.
     *
     * @param outlet The {@link IFontOutlet} singleton.
     */
    public static void set(IFontOutlet outlet) {
        Unique = outlet;
    }

    public static void setLookupProviders(boolean pLookupProviders) {
        lookupProviders = pLookupProviders;
    }
}
