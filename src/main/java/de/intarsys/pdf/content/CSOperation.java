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
package de.intarsys.pdf.content;

import java.util.Arrays;
import java.util.Iterator;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * Represents the operations that make up a content stream.
 * 
 * <p>
 * The content stream is a sequence of operations, with any operation being a
 * list of operands followed by the operator.
 * </p>
 */
public class CSOperation {
	static public final COSName OPERAND_Tx = COSName.constant("Tx"); //$NON-NLS-1$

	/**
	 * More efficient internal representation of operator
	 */
	private byte[] operatorToken;

	/** The operands to the operator */
	private COSObject[] operands;

	private Object cache;

	public CSOperation(byte[] operatorToken, COSObject[] operands) {
		super();
		this.operatorToken = operatorToken;
		this.operands = operands;
	}

	public CSOperation(CSOperator operator) {
		this(operator, new COSObject[0]);
	}

	public CSOperation(CSOperator operator, COSObject[] operands) {
		super();
		this.operatorToken = operator.getToken();
		this.operands = operands;
	}

	/**
	 * Add an operand at the end of the current operand list.
	 * 
	 * @param object
	 *            The new operand to add.
	 */
	public void addOperand(COSObject object) {
		COSObject[] newOperands = new COSObject[operands.length + 1];
		System.arraycopy(operands, 0, newOperands, 0, operands.length);
		newOperands[newOperands.length - 1] = object;
		operands = newOperands;
	}

	public Object getCache() {
		return cache;
	}

	/**
	 * The operand at index <code>i</code>.
	 * 
	 * @param i
	 *            The index of the perand in the operand's list.
	 * 
	 * @return The operand at index <code>i</code>.
	 */
	public COSObject getOperand(int i) {
		return operands[i];
	}

	/**
	 * The iterator over all operands.
	 * 
	 * @return The iterator over all operands.
	 */
	public Iterator getOperands() {
		// todo speed up this operation
		return Arrays.asList(operands).iterator();
	}

	/**
	 * The operator of the operation.
	 * 
	 * @return The operator of the operation.
	 */
	public CSOperator getOperator() {
		return new CSOperator(operatorToken);
	}

	public byte[] getOperatorToken() {
		return operatorToken;
	}

	/**
	 * Answer <code>true</code> if an operator is already defined.
	 * 
	 * @return Answer <code>true</code> if an operator is already defined.
	 */
	public boolean hasOperator() {
		return operatorToken != null;
	}

	/**
	 * <code>true</code> if this is a "begin marked content" operation with
	 * the operand <code>mark</code> (or any operation when mark is null).
	 * 
	 * @param mark
	 *            The requested operand to the operation.
	 * 
	 * @return <code>true</code> if this is a "begin marked content" operation
	 *         with the operand <code>mark</code>.
	 */
	public boolean isOpBeginMarkedContent(COSName mark) {
		if (matchesOperator(CSOperators.CSO_BMC)) {
			if ((mark == null) || (operandSize() == 0)) {
				return true;
			}
			COSObject operand = getOperand(0);
			if (operand instanceof COSName && ((COSName) operand).equals(mark)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if this is a "end marked content" operation.
	 * 
	 * @return <code>true</code> if this is a "end marked content" operation
	 */
	public boolean isOpEndMarkedContent() {
		if (matchesOperator(CSOperators.CSO_EMC)) {
			return true;
		}
		return false;
	}

	/**
	 * Answer <code>true</code> if this operator's name matches the token
	 * <code>other</code>.
	 * 
	 * @param other
	 *            token to check against the operators name.
	 * 
	 * @return Answer <code>true</code> if this operator's name matches the
	 *         token <code>other</code>.
	 */
	public boolean matchesOperator(CSOperator other) {
		return Arrays.equals(operatorToken, (other).getToken());
	}

	/**
	 * Answer the number of operands.
	 * 
	 * @return the number of operands.
	 */
	public int operandSize() {
		return operands.length;
	}

	public void setCache(Object cache) {
		this.cache = cache;
	}

	/**
	 * Set operand at index <code>i</code> to <code>objec</code>.
	 * 
	 * @param i
	 *            The index of the perand in the operand's list.
	 * @param object
	 *            the operand object
	 * 
	 * @return The previous operand at index <code>i</code>.
	 */
	public COSObject setOperand(int i, COSObject object) {
		COSObject old = operands[i];
		operands[i] = object;
		return old;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator it = getOperands(); it.hasNext();) {
			COSObject operand = (COSObject) it.next();
			sb.append(operand.toString());
			sb.append(" "); //$NON-NLS-1$
		}
		sb.append(new String(operatorToken));
		return sb.toString();
	}
}
