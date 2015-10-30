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

import de.intarsys.pdf.pd.AbstractBitFlags;

/**
 * A set of flags describing the access permissions for the document.
 * <p>
 * These flags are defined for the standard security handlers, you can not rely
 * on other handlers implementing the same logic.
 */
public class PermissionFlags extends AbstractBitFlags {

	// Bit position 1-2 are reserved, must be zero
	public static int Bit_Print = 1 << 2;

	public static int Bit_Modify = 1 << 3;

	public static int Bit_Copy = 1 << 4;

	public static int Bit_ModifyAnnotation = 1 << 5;

	// Bit position 7-8 are reserved, must be one
	public static int Bit_FillForm = 1 << 8;

	public static int Bit_Extract = 1 << 9;

	public static int Bit_Assemble = 1 << 10;

	public static int Bit_PrintHighQuality = 1 << 11;

	/**
	 * The handler for the encryption dictionary.
	 */
	protected StandardSecurityHandler handler;

	public PermissionFlags(int value) {
		super(value);
	}

	public PermissionFlags(StandardSecurityHandler handler) {
		super(handler.getEncryption(), null);
		this.handler = handler;
	}

	/**
	 * @return handler for the encryption dictionary.
	 */
	public StandardSecurityHandler getHandler() {
		return handler;
	}

	@Override
	protected int getValueInObject() {
		return getHandler().basicGetPermissionFlags();
	}

	public boolean mayAssemble() {
		return isSetAnd(Bit_Assemble);
	}

	public boolean mayCopy() {
		return isSetAnd(Bit_Copy);
	}

	public boolean mayExtract() {
		return isSetAnd(Bit_Extract);
	}

	public boolean mayFillForm() {
		return isSetAnd(Bit_FillForm);
	}

	public boolean mayModify() {
		return isSetAnd(Bit_Modify);
	}

	public boolean mayModifyAnnotation() {
		return isSetAnd(Bit_ModifyAnnotation);
	}

	public boolean mayPrint() {
		return isSetAnd(Bit_Print);
	}

	public boolean mayPrintHighQuality() {
		return isSetAnd(Bit_PrintHighQuality);
	}

	public void setMayAssemble(boolean value) {
		set(Bit_Assemble, value);
	}

	public void setMayCopy(boolean value) {
		set(Bit_Copy, value);
	}

	public void setMayExtract(boolean value) {
		set(Bit_Extract, value);
	}

	public void setMayFillForm(boolean value) {
		set(Bit_FillForm, value);
	}

	public void setMayModify(boolean value) {
		set(Bit_Modify, value);
	}

	public void setMayModifyAnnotation(boolean value) {
		set(Bit_ModifyAnnotation, value);
	}

	public void setMayPrint(boolean value) {
		set(Bit_Print, value);
	}

	public void setMayPrintHighQuality(boolean value) {
		set(Bit_PrintHighQuality, value);
	}

	@Override
	protected void setValueInObject(int newValue) {
		try {
			getHandler().basicSetPermissionFlags(newValue);
		} catch (COSSecurityException e) {
			throw new SecurityException("security exception", e);
		}
	}
}
