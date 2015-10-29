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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.intarsys.pdf.cds.CDSNameTreeNode;
import de.intarsys.pdf.cds.CDSTreeEntry;
import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.IAdditionalActionSupport;
import de.intarsys.pdf.pd.PDAcroFormField;
import de.intarsys.pdf.pd.PDAction;
import de.intarsys.pdf.pd.PDActionGoTo;
import de.intarsys.pdf.pd.PDAdditionalActions;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDWidgetAnnotation;
import de.intarsys.tools.attribute.Attribute;

/**
 * Tool class for {@link PDAction} related tasks.
 */
public class ActionTools {

	/**
	 * Handle type for disable/enable action processing
	 * 
	 */
	public static class ActionDisablement {
		protected COSName actionType;
	}

	/**
	 * The key where we can find the static JavaScripts in the document catalog.
	 */
	static public final COSName DK_JavaScript = COSName.constant("JavaScript"); //$NON-NLS-1$

	/**
	 * The logger.
	 */
	static private final Logger Log = PACKAGE.Log;

	private static final Attribute ATTR_DISABLEDACTIONS = new Attribute(
			"disabledActions");

	static public TriggerEvent annotationTriggerBlurred(PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "blurred [" + annotation.toString() //$NON-NLS-1$ 
					+ "]"); //$NON-NLS-1$
		}
		PDWidgetAnnotation widget = (PDWidgetAnnotation) annotation;
		PDAcroFormField field = widget.getAcroFormField();
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_Bl);
		trigger.setTarget(field.getLogicalRoot());
		trigger.setValue(field.getValueString());
		trigger.setTargetName(field.getQualifiedName());
		ActionTools.process(trigger, field);
		return trigger;
	}

	static public TriggerEvent annotationTriggerFocus(PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "focus [" + annotation.toString() //$NON-NLS-1$
					+ "]"); //$NON-NLS-1$
		}
		PDWidgetAnnotation widget = (PDWidgetAnnotation) annotation;
		PDAcroFormField field = widget.getAcroFormField();
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_Fo);
		trigger.setTarget(field.getLogicalRoot());
		trigger.setValue(field.getValueString());
		trigger.setTargetName(field.getQualifiedName());
		ActionTools.process(trigger, field);
		return trigger;
	}

	static public TriggerEvent annotationTriggerKeystroke(
			PDAnnotation annotation, String change, boolean willCommit,
			int commitKey, boolean modifier, int selStart, int selEnd,
			boolean shift, String value) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "keystroke [" + annotation.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ 
		}
		PDWidgetAnnotation widget = (PDWidgetAnnotation) annotation;
		PDAcroFormField field = widget.getAcroFormField().getLogicalRoot();
		TriggerEvent trigger = new TriggerEvent(field.getDoc(),
				PDAdditionalActions.CN_trigger_K);
		trigger.setChange(change);
		trigger.setWillCommit(willCommit);
		trigger.setCommitKey(commitKey);
		trigger.setModifier(modifier);
		trigger.setSelStart(selStart);
		trigger.setSelEnd(selEnd);
		trigger.setShift(shift);
		trigger.setTarget(field.getLogicalRoot());
		trigger.setTargetName(field.getQualifiedName());
		trigger.setValue(value);
		ActionTools.process(trigger, field);
		return trigger;
	}

	static public TriggerEvent annotationTriggerMouseDown(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "mouse down [" //$NON-NLS-1$ 
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_D);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_D);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerMouseEnter(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "mouse enter [" //$NON-NLS-1$ 
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_E);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_E);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerMouseExit(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "mouse exit [" //$NON-NLS-1$ 
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_X);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_X);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerMouseUp(PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "mouse up [" + annotation.toString() //$NON-NLS-1$
					+ "]"); //$NON-NLS-1$
		}
		TriggerEvent trigger;
		COSDictionary cosAction = annotation.cosGetField(PDAnnotation.DK_A)
				.asDictionary();
		if (cosAction != null) {
			trigger = new TriggerEvent(annotation.getDoc(), null);
			trigger.setTarget(getTriggerEventTarget(annotation));
			COSName cosType = cosAction.get(PDAction.DK_S).asName();
			ActionProcessor.get().process(trigger, cosType, cosAction);
		} else {
			PDAction action = getAction(annotation,
					PDAdditionalActions.CN_trigger_U);
			if (action == null) {
				// fast path
				return null;
			}
			trigger = new TriggerEvent(annotation.getDoc(),
					PDAdditionalActions.CN_trigger_U);
			trigger.setTarget(getTriggerEventTarget(annotation));
			ActionProcessor.get().process(trigger, action.cosGetActionType(),
					action.cosGetObject());
		}
		return trigger;
	}

	static public TriggerEvent annotationTriggerPageClose(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "page close [" //$NON-NLS-1$ 
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_PC);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_PC);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerPageInvisible(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "page invisible [" //$NON-NLS-1$
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_PI);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_PI);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerPageOpen(PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "page open [" + annotation.toString() //$NON-NLS-1$ 
					+ "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_PO);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_PO);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	static public TriggerEvent annotationTriggerPageVisible(
			PDAnnotation annotation) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "page visible [" //$NON-NLS-1$
					+ annotation.toString() + "]"); //$NON-NLS-1$
		}
		PDAction action = getAction(annotation,
				PDAdditionalActions.CN_trigger_PV);
		if (action == null) {
			// fast path
			return null;
		}
		TriggerEvent trigger = new TriggerEvent(annotation.getDoc(),
				PDAdditionalActions.CN_trigger_PV);
		trigger.setTarget(getTriggerEventTarget(annotation));
		ActionProcessor.get().process(trigger, action.cosGetActionType(),
				action.cosGetObject());
		return trigger;
	}

	/**
	 * Set a flag with <code>doc</code> to signal that action processing for
	 * the type <code>actionType</code> should be disabled. This flag is
	 * honoured by the {@link StandardActionProcessor}.
	 * <p>
	 * Specify <code>null</code> as the <code>actionType</code> to disable
	 * processing of all actions.
	 * 
	 * @param doc
	 *            The document for which action processing is switched off.
	 * @param actionType
	 *            The type of actions no longer executed or <code>null</code>
	 *            for all action types.
	 * @return A handle that is used for re-enabling the processing. This
	 *         ensures that action processing can't be reestablished by
	 *         malicious code (as long as you don't leak the handle).
	 */
	static public ActionDisablement disableActions(COSDocument doc,
			COSName actionType) {
		List disablements = (List) doc.getAttribute(ATTR_DISABLEDACTIONS);
		if (disablements == null) {
			disablements = new ArrayList();
			doc.setAttribute(ATTR_DISABLEDACTIONS, disablements);
		}
		ActionDisablement disablement = new ActionDisablement();
		disablement.actionType = actionType;
		disablements.add(disablement);
		return disablement;
	}

	static public void documentProcessModules(PDDocument doc) {
		COSCatalog catalog = doc.getCatalog();
		if (catalog == null) {
			return;
		}
		COSDictionary names = catalog.cosGetNames();
		if (names == null) {
			return;
		}
		final CDSNameTreeNode tree = CDSNameTreeNode.createFromCos(names.get(
				DK_JavaScript).asDictionary());
		if (tree == null) {
			return;
		}
		for (Iterator it = tree.iterator(); it.hasNext();) {
			CDSTreeEntry entry = (CDSTreeEntry) it.next();
			// ignore scripts generated by Adobe
			COSObject cosActionName = entry.getKey();
			if (cosActionName != null && !cosActionName.isNull()) {
				String actionName = cosActionName.stringValue();
				if (actionName.toUpperCase().startsWith("!ADBE::")) { //$NON-NLS-1$
					continue;
				}
			}
			// execute script
			COSObject actionDefinition = entry.getValue();
			TriggerEvent event = new TriggerEvent(doc, null);
			ActionProcessor.get().process(event, DK_JavaScript,
					actionDefinition);
		}
	}

	static public TriggerEvent documentTriggerClose(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "document close [" + doc.getName() + "]" //$NON-NLS-1$ //$NON-NLS-2$
			);
		}
		TriggerEvent trigger = new TriggerEvent(doc,
				PDAdditionalActions.CN_trigger_DC);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);

		trigger = new TriggerEvent(doc, PDAdditionalActions.CN_trigger_WC);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);
		return trigger;
	}

	static public TriggerEvent documentTriggerDidPrint(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "did print [" + doc.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TriggerEvent trigger = new TriggerEvent(doc,
				PDAdditionalActions.CN_trigger_DP);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);
		return trigger;
	}

	static public TriggerEvent documentTriggerDidSave(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "did save [" + doc.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TriggerEvent trigger = new TriggerEvent(doc,
				PDAdditionalActions.CN_trigger_DS);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);
		return trigger;
	}

	static public TriggerEvent documentTriggerOpen(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "document open [" + doc.getName() + "]" //$NON-NLS-1$ //$NON-NLS-2$
			);
		}
		TriggerEvent trigger = new TriggerEvent(doc, null);
		trigger.setTarget(doc);
		COSObject action = doc.getCatalog().cosGetOpenAction();
		if (action.isNull()) {
			return null;
		}
		COSName cosType = null;
		if (action instanceof COSDictionary) {
			cosType = ((COSDictionary) action).get(PDAction.DK_S).asName();
		} else {
			cosType = PDActionGoTo.CN_ActionType_GoTo;
		}
		ActionProcessor.get().process(trigger, cosType, action);
		return trigger;
	}

	static public TriggerEvent documentTriggerWillPrint(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "will print [" + doc.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TriggerEvent trigger = new TriggerEvent(doc,
				PDAdditionalActions.CN_trigger_WP);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);
		return trigger;
	}

	static public TriggerEvent documentTriggerWillSave(PDDocument doc) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "will save [" + doc.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TriggerEvent trigger = new TriggerEvent(doc,
				PDAdditionalActions.CN_trigger_WS);
		trigger.setTarget(doc);
		ActionTools.process(trigger, doc);
		return trigger;
	}

	/**
	 * Remove a flag with <code>doc</code> to reestablish action processing
	 * for the type <code>actionType</code>. This flag is honoured by the
	 * {@link StandardActionProcessor}.
	 * <p>
	 * You must supply the handle from your call to "disableAction" in
	 * <code>disablement</code>.
	 * 
	 * @param doc
	 *            The document for which action processing is switched off.
	 * @param disablement
	 *            The handle from "disableActions"
	 */
	static public void enableActions(COSDocument doc,
			ActionDisablement disablement) {
		List disablements = (List) doc.getAttribute(ATTR_DISABLEDACTIONS);
		if (disablements == null) {
			disablements = new ArrayList();
			doc.setAttribute(ATTR_DISABLEDACTIONS, disablements);
		}
		disablements.remove(disablement);
	}

	static public TriggerEvent fieldTriggerCalculate(PDAcroFormField field,
			String value, PDAcroFormField source) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "calculate [" + field.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		field = field.getLogicalRoot();
		TriggerEvent trigger = new TriggerEvent(source, field.getDoc(),
				PDAdditionalActions.CN_trigger_C);
		trigger.setTarget(field);
		trigger.setValue(value);
		trigger.setTargetName(field.getQualifiedName());
		ActionTools.process(trigger, field);
		return trigger;
	}

	/**
	 * Format the value of the widget according to its formatting script. The
	 * result of the formatting is not stored but only used as the input for the
	 * widget appearance rendering.
	 */
	static public TriggerEvent fieldTriggerFormat(PDAcroFormField field,
			boolean willCommit, int commitKey, String value) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "format [" + field.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		field = field.getLogicalRoot();
		TriggerEvent trigger = new TriggerEvent(field.getDoc(),
				PDAdditionalActions.CN_trigger_F);
		trigger.setWillCommit(willCommit);
		trigger.setCommitKey(commitKey);
		trigger.setTarget(field.getLogicalRoot());
		trigger.setTargetName(field.getQualifiedName());
		trigger.setValue(value);
		ActionTools.process(trigger, field);
		return trigger;
	}

	static public TriggerEvent fieldTriggerValidate(PDAcroFormField field,
			String value) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "validate [" + field.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		field = field.getLogicalRoot();
		TriggerEvent trigger = new TriggerEvent(field.getDoc(),
				PDAdditionalActions.CN_trigger_V);
		trigger.setTarget(field.getLogicalRoot());
		trigger.setTargetName(field.getQualifiedName());
		trigger.setValue(value);
		ActionTools.process(trigger, field);
		return trigger;
	}

	protected static PDAction getAction(IAdditionalActionSupport actionSupport,
			COSName reason) {
		if (actionSupport == null) {
			return null;
		}
		PDAdditionalActions actions = actionSupport.getAdditionalActions();
		if (actions == null) {
			return null;
		}
		return actions.getAction(reason);
	}

	static protected Object getTriggerEventTarget(PDAnnotation annotation) {
		if (annotation.isWidgetAnnotation()) {
			return ((PDWidgetAnnotation) annotation).getAcroFormField()
					.getLogicalRoot();
		}
		return annotation;
	}

	/**
	 * <code>true</code> if execution of an action of type
	 * <code>actionType</code> should be allowed. This flag is honoured by the
	 * {@link StandardActionProcessor}.
	 * 
	 * @param doc
	 *            The document under inspection.
	 * @param actionType
	 *            The type of actions we want to execute.
	 * @return <code>true</code> if execution of an action of type
	 *         <code>actionType</code> should be allowed.
	 */
	static public boolean isEnabled(COSDocument doc, COSName actionType) {
		List disablements = (List) doc.getAttribute(ATTR_DISABLEDACTIONS);
		if (disablements == null) {
			return true;
		}
		for (Iterator it = disablements.iterator(); it.hasNext();) {
			ActionDisablement disablement = (ActionDisablement) it.next();
			if (disablement.actionType == null
					|| disablement.actionType.equals(actionType)) {
				return false;
			}
		}
		return true;
	}

	static public TriggerEvent pageTriggerClose(PDPage page) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "close [" + page.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ 
		}
		TriggerEvent trigger = new TriggerEvent(page.getDoc(),
				PDAdditionalActions.CN_trigger_C);
		trigger.setTarget(page);
		ActionTools.process(trigger, page);
		return trigger;
	}

	static public TriggerEvent pageTriggerOpen(PDPage page) {
		if (Log.isLoggable(Level.FINEST)) {
			Log.log(Level.FINEST, "open [" + page.toString() + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		TriggerEvent trigger = new TriggerEvent(page.getDoc(),
				PDAdditionalActions.CN_trigger_O);
		trigger.setTarget(page);
		ActionTools.process(trigger, page);
		return trigger;
	}

	/**
	 * Lookup a {@link PDAction} to be executed when <code>event</code> is
	 * triggered. The action is looked up in the <code>actionSupport</code>
	 * instance which defines the trigger - action association.
	 * 
	 * @param event
	 *            The event that causes an action to be processed.
	 * @param actionSupport
	 *            The target object whose action will be processed
	 */
	static protected void process(TriggerEvent event,
			IAdditionalActionSupport actionSupport) {
		if ((actionSupport == null)
				&& event.getTarget() instanceof IAdditionalActionSupport) {
			actionSupport = (IAdditionalActionSupport) event.getTarget();
		}
		if (actionSupport == null) {
			return;
		}
		PDAdditionalActions actions = actionSupport.getAdditionalActions();
		if (actions == null) {
			return;
		}
		PDAction action = actions.getAction(event.getReason());
		if (action == null) {
			return;
		}
		ActionProcessor.get().process(event, action.cosGetActionType(),
				action.cosGetObject());
	}
}
