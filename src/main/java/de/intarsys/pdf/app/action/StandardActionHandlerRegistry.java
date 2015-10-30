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

import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation for a action handler factory.
 */
public class StandardActionHandlerRegistry implements IActionHandlerRegistry {
    /**
     * The mapping from action types to action handler objects.
     */
    private Map<COSName, IActionHandler> nameMap;

    /**
     * A default handler if none is detected.
     */
    private IActionHandler defaultHandler;

    public StandardActionHandlerRegistry() {
        nameMap = new HashMap<COSName, IActionHandler>(5);
    }

    @Override
    public synchronized IActionHandler[] getActionHandlers() {
        return nameMap.values().toArray(new IActionHandler[nameMap.size()]);
    }

    public synchronized IActionHandler getDefaultHandler() {
        return defaultHandler;
    }

    @Override
    public synchronized IActionHandler lookupActionHandler(COSName actionType) {
        return nameMap.get(actionType);
    }

    @Override
    public synchronized void registerActionHandler(IActionHandler handler) {
        nameMap.put(handler.getActionType(), handler);
    }

    public synchronized void registerDefaultHandler(IActionHandler handler) {
        defaultHandler = handler;
    }

    @Override
    public synchronized void unregisterActionHandler(IActionHandler handler) {
        nameMap.remove(handler.getActionType());
    }
}
