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
package de.intarsys.pdf.app.action;

import de.intarsys.pdf.cos.COSName;

/**
 * A factory for action handler objects.
 * <p>
 * <p>
 * The action handler factory returns a registered handler for a given action
 * type.
 * </p>
 */
public interface IActionHandlerRegistry {
    /**
     * A collection of all registered {@link IActionHandler} instances.
     *
     * @return A collection of all registered {@link IActionHandler} instances.
     */
    IActionHandler[] getActionHandlers();

    /**
     * The {@link IActionHandler} for the specified {@code actionType}.
     *
     * @param actionType The type of action to be executed, for example /JavaScript
     * @return The {@link IActionHandler} able to process an action definition
     * of the specified type.
     */
    IActionHandler lookupActionHandler(COSName actionType);

    /**
     * Register an {@link IActionHandler}.
     *
     * @param handler The new handler
     */
    void registerActionHandler(IActionHandler handler);

    /**
     * Unregister an {@link IActionHandler}.
     *
     * @param handler The {@link IActionHandler} to be unregistered.
     */
    void unregisterActionHandler(IActionHandler handler);
}
