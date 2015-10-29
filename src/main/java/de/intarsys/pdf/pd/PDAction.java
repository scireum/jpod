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
package de.intarsys.pdf.pd;

import java.util.ArrayList;
import java.util.List;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * A PDF document can be interative by defining actions for different events.
 * This is an abstract superclass for the implementation of the various action
 * types.
 * 
 */
abstract public class PDAction extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.COSBasedObject.MetaClass#doDetermineClass(de.intarsys.pdf.cos.COSObject)
		 */
		@Override
		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			if (object instanceof COSArray) {
				return PDActionGoTo.META;
			}
			COSName actionType = ((COSDictionary) object).get(DK_S).asName();
			if (actionType == null) {
				throw new IllegalArgumentException("action type not specified");
			}
			if (actionType.equals(PDActionSubmitForm.CN_ActionType_SubmitForm)) {
				return PDActionSubmitForm.META;
			}
			if (actionType.equals(PDActionResetForm.CN_ActionType_ResetForm)) {
				return PDActionResetForm.META;
			}
			if (actionType.equals(PDActionGoTo.CN_ActionType_GoTo)) {
				return PDActionGoTo.META;
			}
			if (actionType.equals(PDActionGoToR.CN_ActionType_GoToR)) {
				return PDActionGoToR.META;
			}
			if (actionType.equals(PDActionJavaScript.CN_ActionType_JavaScript)) {
				return PDActionJavaScript.META;
			}
			if (actionType.equals(PDActionNamed.CN_ActionType_Named)) {
				return PDActionNamed.META;
			}
			if (actionType.equals(PDActionURI.CN_ActionType_URI)) {
				return PDActionURI.META;
			}
			if (actionType.equals(PDActionLaunch.CN_ActionType_Launch)) {
				return PDActionLaunch.META;
			}
			if (actionType.equals(PDActionRendition.CN_ActionType_Rendition)) {
				return PDActionRendition.META;
			}
			return PDActionAny.META;
		}

		@Override
		public Class getRootClass() {
			return PDAction.class;
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	static public final COSName DK_S = COSName.constant("S");

	static public final COSName DK_Next = COSName.constant("Next");

	static public final COSName CN_Type_Action = COSName.constant("Action");

	protected PDAction(COSObject object) {
		super(object);
	}

	/**
	 * Add a new {@link PDAction} to be executed after this.
	 * 
	 * @param action
	 *            The new {@link PDAction}
	 */
	public void addNext(PDAction action) {
		COSArray nextArray = null;
		COSObject dictNext = cosGetField(DK_Next); // can be Array or Dict

		if (dictNext.isNull()) {
			nextArray = COSArray.create(1);
			cosSetField(DK_Next, nextArray);
		} else {
			if (dictNext instanceof COSArray) {
				nextArray = (COSArray) dictNext;
			} else if (dictNext instanceof COSDictionary) {
				nextArray = COSArray.create(2);
				nextArray.add(dictNext);
				cosSetField(DK_Next, nextArray);
			}
		}
		nextArray.add(action.cosGetDict());
	}

	/**
	 * The real action type.
	 * 
	 * @return The real action type.
	 */
	public COSName cosGetActionType() {
		if (cosGetObject() instanceof COSDictionary) {
			return cosGetField(DK_S).asName();
		} else {
			// fallback for actions defined implicitly basing on other types
			return cosGetExpectedActionType();
		}
	}

	/**
	 * The action type expected for a {@link PDAction} of the instantiated
	 * class.
	 * 
	 * @return The action type expected for a {@link PDAction} of the
	 *         instantiated class.
	 */
	abstract public COSName cosGetExpectedActionType();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Action;
	}

	/**
	 * The {@link List} of {@link PDAction} instances that must be executed
	 * after this.
	 * 
	 * @return The {@link List} of {@link PDAction} instances that must be
	 *         executed after this.
	 */
	public List getNext() {
		List result = null;
		if (!(cosGetObject() instanceof COSDictionary)) {
			return null;
		}
		COSObject dictNext = cosGetField(DK_Next); // can be Array or Dict
		if (dictNext instanceof COSDictionary) {
			result = new ArrayList();
			result.add(PDAction.META.createFromCos(dictNext));
		} else if (dictNext instanceof COSArray) {
			result = getPDObjects(DK_Next, PDAction.META, false);
		}
		return result;
	}

	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		//
		COSName actionType = cosGetExpectedActionType();
		if (actionType != null) {
			cosSetField(DK_S, cosGetExpectedActionType().copyShallow());
		}
	}

	/**
	 * Remove a {@link PDAction} from the actions to be executed after this.
	 * 
	 * @param action
	 *            The {@link PDAction} to be removed
	 */
	public void removeNext(PDAction action) {
		COSArray nextArray = null;
		COSObject dictNext = cosGetField(DK_Next); // can be Array or Dict

		if (dictNext.isNull()) {
			//
		} else {
			if (dictNext instanceof COSArray) {
				nextArray = (COSArray) dictNext;
				nextArray.remove(action.cosGetObject());
			} else if (dictNext instanceof COSDictionary) {
				if (dictNext == action.cosGetObject()) {
					cosRemoveField(DK_Next);
				}
			}
		}
	}
}
