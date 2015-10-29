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

import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontStyle;

/**
 * A "search" template or query to look up {@link PDFont} objects.
 * 
 */
public interface IFontQuery {

	public static final String TYPE_TRUETYPE = "TrueType"; //$NON-NLS-1$
	public static final String TYPE_BUILTIN = "Builtin"; //$NON-NLS-1$
	public static final String TYPE_TYPE1 = "Type1"; //$NON-NLS-1$
	public static final String TYPE_ANY = "Any"; //$NON-NLS-1$

	/**
	 * The desired encoding for the {@link PDFont}.
	 * 
	 * @return The desired encoding for the {@link PDFont}.
	 */
	public Encoding getEncoding();

	/**
	 * The desired font family for the {@link PDFont}.
	 * 
	 * @return The desired font family for the {@link PDFont}.
	 */
	public String getFontFamilyName();

	/**
	 * The desired font name for the {@link PDFont}.
	 * 
	 * @return The desired font name for the {@link PDFont}.
	 */
	public String getFontName();

	/**
	 * The desired font style for the {@link PDFont}.
	 * 
	 * @return The desired font style for the {@link PDFont}.
	 */
	public PDFontStyle getFontStyle();

	/**
	 * The desired font type for the {@link PDFont} (such as "Type1" or
	 * "TrueType"). <code>null</code>indicates a font of any type.
	 * 
	 * @return The desired font type for the {@link PDFont}.
	 */
	public String getFontType();
}
