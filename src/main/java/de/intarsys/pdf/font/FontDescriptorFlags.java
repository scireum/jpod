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
package de.intarsys.pdf.font;

import de.intarsys.pdf.pd.AbstractBitFlags;

/**
 * The flags of a font descriptor.
 * <p>
 * The following bits are defined (more may exist).
 * </p>
 * <ul>
 * <li>0: fixed pitch
 * <li>1: serif
 * <li>2: symbolic
 * <li>3: script
 * <li>5: non symbolic
 * <li>6: italic
 * <li>16: all capital
 * <li>17: small caps
 * <li>18: force bold
 * </ul>
 */
public class FontDescriptorFlags extends AbstractBitFlags {
	public static final int Bit_FixedPitch = 1;

	public static final int Bit_Serif = 1 << 1;

	public static final int Bit_Symbolic = 1 << 2;

	public static final int Bit_Script = 1 << 3;

	public static final int Bit_Nonsymbolic = 1 << 5;

	public static final int Bit_Italic = 1 << 6;

	public static final int Bit_AllCap = 1 << 16;

	public static final int Bit_SmallCap = 1 << 17;

	public static final int Bit_ForceBold = 1 << 18;

	private PDFontDescriptor fontDescriptor;

	public FontDescriptorFlags(int value) {
		super(value);
	}

	public FontDescriptorFlags(PDFontDescriptor fontDescriptor) {
		super(fontDescriptor, null);
		this.fontDescriptor = fontDescriptor;
	}

	protected PDFontDescriptor getFontDescriptor() {
		return fontDescriptor;
	}

	@Override
	protected int getValueInObject() {
		return getFontDescriptor().getFlagsValue();
	}

	public boolean isAllCap() {
		return isSetAnd(Bit_AllCap);
	}

	public boolean isFixedPitch() {
		return isSetAnd(Bit_FixedPitch);
	}

	public boolean isForceBold() {
		return isSetAnd(Bit_ForceBold);
	}

	public boolean isItalic() {
		return isSetAnd(Bit_Italic);
	}

	public boolean isNonsymbolic() {
		return isSetAnd(Bit_Nonsymbolic);
	}

	public boolean isScript() {
		return isSetAnd(Bit_Script);
	}

	public boolean isSerif() {
		return isSetAnd(Bit_Serif);
	}

	public boolean isSmallCap() {
		return isSetAnd(Bit_SmallCap);
	}

	public boolean isSymbolic() {
		return isSetAnd(Bit_Symbolic);
	}

	public void setAllCap(boolean flag) {
		set(Bit_AllCap, flag);
	}

	public void setFixedPitch(boolean flag) {
		set(Bit_FixedPitch, flag);
	}

	public void setForceBold(boolean flag) {
		set(Bit_ForceBold, flag);
	}

	public void setItalic(boolean flag) {
		set(Bit_Italic, flag);
	}

	public void setNonsymbolic(boolean flag) {
		set(Bit_Nonsymbolic, flag);
		set(Bit_Symbolic, !flag);
	}

	public void setScript(boolean flag) {
		set(Bit_Script, flag);
	}

	public void setSerif(boolean flag) {
		set(Bit_Serif, flag);
	}

	public void setSmallCap(boolean flag) {
		set(Bit_SmallCap, flag);
	}

	public void setSymbolic(boolean flag) {
		set(Bit_Symbolic, flag);
		set(Bit_Nonsymbolic, !flag);
	}

	@Override
	protected void setValueInObject(int newValue) {
		getFontDescriptor().setFlagsValue(newValue);
	}
}
