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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDAction;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The standard {@link IActionProcessor} implementation.
 */
public class StandardActionProcessor implements IActionProcessor {
    /**
     * The logger to be used in this package
     */
    private static Logger Log = PACKAGE.Log;

    /**
     * The factory used in this interpreter.
     */
    private IActionHandlerRegistry factory = ActionHandlerRegistry.get();

    protected IActionHandlerRegistry getFactory() {
        return factory;
    }

    protected void handleException(ActionException e) {
        Log.logp(Level.WARNING, "", "", "PDF action processing error", e); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    protected void processAction(TriggerEvent event, COSName actionType, COSObject actionDefinition) {
        COSDocument doc = actionDefinition.getDoc();
        if (doc != null) {
            if (!ActionTools.isEnabled(doc, actionType)) {
                return;
            }
        }
        IActionHandler handler = getFactory().lookupActionHandler(actionType);
        if (handler != null) {
            try {
                handler.process(event, actionDefinition);
            } catch (ActionException e) {
                handleException(e);
            }
        }
    }

    @Override
    public void process(TriggerEvent event, COSName actionType, COSObject actionDefinition) {
        processAction(event, actionType, actionDefinition);
        if (actionDefinition instanceof COSDictionary) {
            COSDictionary defDict = (COSDictionary) actionDefinition;
            PDAction action = (PDAction) PDAction.META.createFromCos(defDict);
            List next = action.getNext();
            if (next != null) {
                for (Iterator it = next.iterator(); it.hasNext(); ) {
                    PDAction child = (PDAction) it.next();
                    process(event, child.cosGetActionType(), child.cosGetObject());
                }
            }
        }
        event.setExecuted(true);
    }
}
