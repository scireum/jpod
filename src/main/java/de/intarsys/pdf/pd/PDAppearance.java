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
import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * The PDAppearance describes the visual content of a PDAnnotation.
 * <p>
 * The annotation supports different visual feedback dependent on the kind of
 * user interaction currently in effect.
 * 
 * <code>
 * - Normal
 * - Down
 * - Rollover
 * </code>
 * 
 * Additionaly, each of these appearances is supported for the different logical
 * states an annotation can enter (for example "On" and "Off" in a checkbox).
 * <p>
 * The PDForm objects responsible for this feedback are described here.
 * 
 */
public class PDAppearance extends PDObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDAppearance(object);
		}

		@Override
		protected boolean isIndirect() {
			return false;
		}
	}

	public static final COSName DK_D = COSName.constant("D");

	public static final COSName DK_N = COSName.constant("N");

	public static final COSName DK_R = COSName.constant("R");

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected PDAppearance(COSObject object) {
		super(object);
	}

	protected void collectAppearances(COSDictionary dict, List<PDForm> forms) {
		for (Iterator i = dict.values().iterator(); i.hasNext();) {
			COSObject entry = (COSObject) i.next();
			if (entry.isNull()) {
				continue;
			}
			if (entry instanceof COSStream) {
				PDForm form = (PDForm) PDForm.META.createFromCos(entry);
				if (form != null) {
					forms.add(form);
				}
			}
			if (entry instanceof COSDictionary) {
				COSDictionary entryDict = (COSDictionary) entry;
				collectAppearances(entryDict, forms);
			}
		}
	}

	/**
	 * The {@link PDForm} for state <code>state</code>. <code>key</code> defines
	 * the interaction context and is one of "/D", "/R" or "/N". If no
	 * {@link PDForm} is available, return the {@link PDForm} for the "/N"
	 * context.
	 * 
	 * @param key
	 *            The interaction context
	 * @param state
	 *            The {@link PDAnnotation} state.
	 * @return The {@link PDForm} for state <code>state</code>.
	 */
	public PDForm getAppearance(COSName key, COSName state) {
		PDForm form = getForm(key, state);
		if (form == null) {
			form = getNormalAppearance(state);
		}
		return form;
	}

	public PDForm getDownAppearance(COSName state) {
		return getForm(DK_D, state);
	}

	protected PDForm getForm(COSName key, COSName state) {
		COSObject cosObject = cosGetField(key);
		if (cosObject.isNull()) {
			return null;
		}
		if (cosObject instanceof COSStream) {
			return (PDForm) PDForm.META.createFromCos(cosObject);
		}
		if (cosObject instanceof COSDictionary && (state != null)) {
			COSDictionary dict = (COSDictionary) cosObject;
			COSStream stream = dict.get(state).asStream();
			return (PDForm) PDForm.META.createFromCos(stream);
		}
		return null;
	}

	/**
	 * Collects all appearance forms within this annotation dictionary.
	 * 
	 * @return The appearance forms.
	 */
	public List<PDForm> getForms() {
		List<PDForm> forms = new ArrayList<PDForm>();
		collectAppearances(cosGetDict(), forms);
		return forms;
	}

	public PDForm getNormalAppearance(COSName state) {
		return getForm(DK_N, state);
	}

	public PDForm getRolloverAppearance(COSName state) {
		return getForm(DK_R, state);
	}

	/**
	 * <code>true</code> if this appearance dictionary has valid contents. From
	 * time to time there may be an empty /AP stub around, in this case this
	 * method returns false.
	 * 
	 * @return <code>true</code> if this appearance dictionary has valid
	 *         contents.
	 */
	public boolean isDefined() {
		return getNormalAppearance(null) != null;
	}

	/**
	 * <code>true</code> if this appearance dictionary has valid contents for
	 * the requested rendering context and appearance state.
	 * 
	 * @return <code>true</code> if this appearance dictionary has valid
	 *         contents for the requested rendering context and appearance
	 *         state.
	 */
	public boolean isDefined(COSName key, COSName state) {
		PDForm form = getForm(key, state);
		if (form == null) {
			form = getNormalAppearance(state);
		}
		return form != null;
	}

	public void setDownAppearance(COSName state, PDForm form) {
		setForm(DK_D, state, form);
	}

	protected void setForm(COSName key, COSName state, PDForm form) {
		COSObject cosObject = cosGetField(key);
		if (state == null) {
			if (form != null) {
				cosSetField(key, form.cosGetStream());
			} else {
				cosRemoveField(key);
			}
		} else {
			if (!(cosObject instanceof COSDictionary)) {
				cosObject = COSDictionary.create();
				cosSetField(key, cosObject);
			}
			COSDictionary dict = (COSDictionary) cosObject;
			if (form != null) {
				dict.put(state, form.cosGetStream());
			} else {
				dict.remove(state);
			}
		}
	}

	public void setNormalAppearance(COSName state, PDForm form) {
		setForm(DK_N, state, form);
	}

	public void setRolloverAppearance(COSName state, PDForm form) {
		setForm(DK_R, state, form);
	}
}
