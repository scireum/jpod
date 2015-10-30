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

import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.tools.attribute.Attribute;

import java.util.Map;

/**
 * The standard implementation for {@link IFormHandlerFactory} creating a
 * {@link StandardFormHandler}.
 */
public class StandardFormHandlerFactory implements IFormHandlerFactory {

    private static final Attribute ATTR_FORMHANDLER = new Attribute("formhandler");

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.app.acroform.IFormHandlerFactory#createFormHandler(de.intarsys.pdf.pd.PDDocument)
     */
    synchronized public IFormHandler createFormHandler(PDDocument doc, Map options) {
        if (doc == null) {
            return null;
        }
        IFormHandler handler = (IFormHandler) doc.getAttribute(ATTR_FORMHANDLER);
        if (handler == null) {
            try {
                handler = new StandardFormHandler(doc);
                doc.setAttribute(ATTR_FORMHANDLER, handler);
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        return handler;
    }
}
