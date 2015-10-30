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

import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.pd.PDDocument;

/**
 * A factory for {@link PDFont} instances.
 * <p>
 * The factory can create "relative" requests ("getXYZFlavor) as well as
 * "absolute" request defining the complete font attributes.
 */
public interface IFontFactory {
    /**
     * A font based on <code>font</code>, but with "bold" style.
     * <p>
     * The font returned may be either a new one or a font already in use in a
     * {@link PDDocument}. This decision is up to the factory.Be careful when
     * changing the font returned!
     * </p>
     *
     * @param font The base font to be "bolded".
     * @return A font based on <code>font</code>, but with "bold" style.
     * @throws FontFactoryException
     */
    public PDFont getBoldFlavor(PDFont font) throws FontFactoryException;

    /**
     * A font satisfying the conditions defined in <code>query</code>.
     * <p>
     * The font returned may be either a new one or a font already in use in a
     * {@link PDDocument}. This decision is up to the factory. Be careful when
     * changing the font returned!
     * </p>
     *
     * @param query A query defining the {@link PDFont} to be looked up.
     * @return A font satisfying the conditions defined in <code>query</code>.
     * @throws FontFactoryException
     */
    public PDFont getFont(IFontQuery query) throws FontFactoryException;

    /**
     * A font based on <code>font</code>, but with "italic" style.
     * <p>
     * The font returned may be either a new one or a font already in use in a
     * {@link PDDocument}. This decision is up to the factory.Be careful when
     * changing the font returned!
     * </p>
     *
     * @param font The base font to be "italicized".
     * @return A font based on <code>font</code>, but with "italic" style.
     * @throws FontFactoryException
     */
    public PDFont getItalicFlavor(PDFont font) throws FontFactoryException;

    /**
     * A font based on <code>font</code>, but with "regular" style.
     * <p>
     * The font returned may be either a new one or a font already in use in a
     * {@link PDDocument}. This decision is up to the factory. Be careful when
     * changing the font returned!
     * </p>
     *
     * @param font The base font to be "regularized".
     * @return A font based on <code>font</code>, but with "regular" style.
     * @throws FontFactoryException
     */
    public PDFont getRegularFlavor(PDFont font) throws FontFactoryException;

    /**
     * Register a new font available for public use.
     *
     * @param font The new font available for clients .
     */
    public void registerFont(PDFont font);
}
