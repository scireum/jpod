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
package de.intarsys.pdf.cos;

/**
 * This class represents integer numbers in pdf
 */
public class COSInteger extends COSNumber {
	static public COSInteger create(byte[] bytes, int start, int length) {
		int result = 0;
		int end = start + length;
		boolean negative = false;
		byte prefix = bytes[start];
		if (prefix == '+') {
			start++;
		} else if (prefix == '-') {
			negative = true;
			start++;
		}
		for (int i = start; i < end; i++) {
			result = ((result * 10) + bytes[i]) - '0';
		}
		if (negative) {
			return new COSInteger(-result);
		}
		return new COSInteger(result);
	}

	static public COSInteger create(int value) {
		return new COSInteger(value);
	}

	static public COSInteger createStrict(byte[] bytes, int start, int length) {
		long resultLong = 0;
		int end = start + length;
		boolean negative = false;
		byte prefix = bytes[start];
		if (prefix == '+') {
			start++;
		} else if (prefix == '-') {
			negative = true;
			start++;
		}
		for (int i = start; i < end; i++) {
			resultLong = ((resultLong * 10) + bytes[i]) - '0';
		}
		if (resultLong > Integer.MAX_VALUE || resultLong < Integer.MIN_VALUE) {
			return null;
		}
		int resultInt = (int) resultLong;
		if (negative) {
			return new COSInteger(-resultInt);
		}
		return new COSInteger(resultInt);
	}

	/** the integer value represented */
	private final int value;

	protected COSInteger(int newValue) {
		value = newValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor)
	 */
	@Override
	public java.lang.Object accept(ICOSObjectVisitor visitor)
			throws COSVisitorException {
		return visitor.visitFromInteger(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#asInteger()
	 */
	@Override
	public COSInteger asInteger() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#basicToString()
	 */
	@Override
	protected String basicToString() {
		return String.valueOf(intValue());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#copyBasic()
	 */
	@Override
	protected COSObject copyBasic() {
		return new COSInteger(value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof COSInteger)) {
			return false;
		}
		return value == ((COSInteger) o).intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSNumber#floatValue()
	 */
	@Override
	public float floatValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSNumber#intValue()
	 */
	@Override
	public int intValue() {
		return value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
	 */
	public Object saveState() {
		COSInteger result = new COSInteger(value);
		result.container = this.container.saveStateContainer();
		return result;
	}
}
