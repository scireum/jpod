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

import de.intarsys.pdf.pd.PDResources;

import java.util.Map;

/**
 * An interpreter for {@link CSContent} PDF graphics streams. Indirect
 * references are resolved using <code>resources</code>.
 * <code>resources</code> may be null if not needed.
 */
public interface ICSInterpreter {
    /**
     * Process <code>content</code> using <code>resources</code> if needed.
     * <p>
     * This method may be called reentrant when a form XObject is encountered.
     *
     * @param content   The PDF content stream to be interpreted.
     * @param resources The {@link PDResources} repository for the content stream
     */
    public void process(CSContent content, PDResources resources);

    /**
     * Transparent options used by the interpreter implementation. The method
     * may return <code>null</code>.
     *
     * @return Transparent options used by the interpreter implementation.
     */
    public Map getOptions();

    /**
     * The currently active {@link ICSExceptionHandler} for the interpreter.
     * <p>
     * The {@link ICSExceptionHandler} is responsible for acting on the
     * {@link CSException} instances thrwoed while processing a
     * {@link CSContent}.
     *
     * @return The currently active {@link ICSExceptionHandler} for the
     * interpreter.
     */
    public ICSExceptionHandler getExceptionHandler();

    /**
     * Assign the {@link ICSExceptionHandler} for the interpreter.
     *
     * @param errorHandler The new {@link ICSExceptionHandler}.
     */
    public void setExceptionHandler(ICSExceptionHandler errorHandler);
}
