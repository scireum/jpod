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

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.tools.stream.StreamTools;
import de.intarsys.tools.xml.EntityDecoder;

/**
 * A proxy implementation of an {@link IFormHandler}. This one resolves
 * entities contained within field values before passing them to the delegate
 * instance. This is useful e.g. when dealing with HTTP request parameters.
 */
public class EntityFormHandler extends ProxyFormHandler {

	private static final Logger Log = PACKAGE.Log;

	public EntityFormHandler(IFormHandler delegate) {
		super(delegate);
	}

	protected String resolve(String value) {
		EntityDecoder decoder = new EntityDecoder(new StringReader(value),
				false);
		try {
			return StreamTools.toString(decoder);
		} catch (IOException e) {
			Log.log(Level.SEVERE, e.getMessage(), e);
			return value;
		} finally {
			StreamTools.close(decoder);
		}
	}

	@Override
	public void setFieldValue(Object fieldRef, Object value) {
		if (value instanceof String) {
			value = resolve((String) value);
		}
		super.setFieldValue(fieldRef, value);
	}

}
