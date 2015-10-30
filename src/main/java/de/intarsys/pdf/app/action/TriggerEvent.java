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
import de.intarsys.pdf.pd.PDAdditionalActions;
import de.intarsys.tools.string.StringTools;

import java.util.EventObject;

/**
 * An event that leads to the processing of an action or action sequence.
 * <p>
 * The source of the event is always the COSObject responsible for the event,
 * for example the document for document level events, the field for field
 * events and so on.
 */
public class TriggerEvent extends EventObject {

    private static final Object NULL = new Object();

    /**
     * An optional name defining the reason for the event in the PDF notation,
     * for example "E" for enter annotation...
     */
    private COSName reason;

    /**
     * A flag for the outcome of an event handling action.
     * <p>
     * <p>
     * This is public for allowing JavaScript access.
     * </p>
     */
    private boolean rc = true;

    private String change;

    private boolean shift = false;

    private int selStart = 0;

    private int selEnd = 0;

    private Object target = null;

    private String targetName = ""; //$NON-NLS-1$

    private Object value = null;

    private boolean willCommit = false;

    private int commitKey = 0;

    private boolean modifier = false;

    private Object context;

    private Object args;

    private String type;

    private String name;

    private boolean executed = false;

    public TriggerEvent(Object context, COSName reason) {
        this(null, context, reason, null, null);
    }

    public TriggerEvent(Object source, Object context, COSName reason) {
        this(source, context, reason, null, null);
    }

    public TriggerEvent(Object source, Object context, COSName reason, String type, String name) {
        super((source == null) ? NULL : source);
        this.context = context;
        this.reason = reason;
        this.type = type;
        this.name = name;
    }

    public Object getArgs() {
        return args;
    }

    public String getChange() {
        return change;
    }

    public int getCommitKey() {
        return commitKey;
    }

    public Object getContext() {
        return context;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return PDAdditionalActions.getEventName(reason);
    }

    public boolean getRc() {
        return rc;
    }

    public COSName getReason() {
        return reason;
    }

    public int getSelEnd() {
        return selEnd;
    }

    public int getSelStart() {
        return selStart;
    }

    @Override
    public Object getSource() {
        Object tempSource = super.getSource();
        if (tempSource == NULL) {
            return null;
        }
        return tempSource;
    }

    public Object getTarget() {
        return target;
    }

    public String getTargetName() {
        return targetName;
    }

    public String getType() {
        if (type != null) {
            return type;
        }
        return PDAdditionalActions.getEventType(reason);
    }

    public Object getValue() {
        return value;
    }

    public String getValueString() {
        return value == null ? null : StringTools.safeString(value);
    }

    public boolean isExecuted() {
        return executed;
    }

    public boolean isModifier() {
        return modifier;
    }

    public boolean isRc() {
        return rc;
    }

    public boolean isShift() {
        return shift;
    }

    public boolean isWillCommit() {
        return willCommit;
    }

    public void setArgs(Object args) {
        this.args = args;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public void setCommitKey(int commitKey) {
        this.commitKey = commitKey;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    public void setModifier(boolean modifier) {
        this.modifier = modifier;
    }

    public void setRc(boolean rc) {
        this.rc = rc;
    }

    public void setSelEnd(int selEnd) {
        this.selEnd = selEnd;
    }

    public void setSelStart(int selStart) {
        this.selStart = selStart;
    }

    public void setShift(boolean shift) {
        this.shift = shift;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setWillCommit(boolean willCommit) {
        this.willCommit = willCommit;
    }
}
