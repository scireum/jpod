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
package de.intarsys.pdf.app.acroform;

import java.util.List;

import de.intarsys.pdf.pd.PDDocument;

/**
 * An abstract convenience implementation of an {@link IFormHandler}. This one
 * delegates its tasks to some other {@link IFormHandler} instance.
 */
public abstract class ProxyFormHandler implements IFormHandler {

	private IFormHandler delegate;

	protected ProxyFormHandler(IFormHandler delegate) {
		super();
		this.delegate = delegate;
	}

	public IFormHandler getDelegate() {
		return delegate;
	}

	public PDDocument getDoc() {
		return getDelegate().getDoc();
	}

	public String getFieldValue(Object fieldRef) {
		return getDelegate().getFieldValue(fieldRef);
	}

	public boolean isCalculate() {
		return getDelegate().isCalculate();
	}

	public boolean isValidate() {
		return getDelegate().isValidate();
	}

	public void recalculate() {
		getDelegate().recalculate();
	}

	public void recalculate(Object fieldRef) {
		getDelegate().recalculate(fieldRef);
	}

	public void resetFields() {
		getDelegate().resetFields();
	}

	public void resetFields(List fieldNames, boolean invert) {
		getDelegate().resetFields(fieldNames, invert);
	}

	public void setCalculate(boolean calculate) {
		getDelegate().setCalculate(calculate);
	}

	public void setFieldValue(Object fieldRef, Object value) {
		getDelegate().setFieldValue(fieldRef, value);
	}

	public void setValidate(boolean validate) {
		getDelegate().setValidate(validate);
	}

}
