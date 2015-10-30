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
package de.intarsys.pdf.st;

import de.intarsys.tools.enumeration.EnumItem;
import de.intarsys.tools.enumeration.EnumMeta;

/**
 * The enumeration of available write modes for the document.
 */
public class EnumWriteMode extends EnumItem {
	/**
	 * The meta data for the enumeration.
	 */
	final public static EnumMeta META = getMeta(EnumWriteMode.class);

	/** Defer decision until writing */
	public static final EnumWriteMode UNDEFINED = new EnumWriteMode("undefined"); //$NON-NLS-1$

	/** Force incremental writing */
	public static final EnumWriteMode INCREMENTAL = new EnumWriteMode(
			"incremental"); //$NON-NLS-1$

	/** Force full writing */
	public static final EnumWriteMode FULL = new EnumWriteMode("full"); //$NON-NLS-1$

	static {
		UNDEFINED.setDefault();
	}

	protected EnumWriteMode(String id) {
		super(id);
	}

	public boolean isIncremental() {
		return this == INCREMENTAL;
	}

	public boolean isFull() {
		return this == FULL;
	}

	public boolean isUndefined() {
		return this == UNDEFINED;
	}
}
