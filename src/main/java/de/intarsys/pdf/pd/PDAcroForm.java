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

import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSTrue;

/**
 * The logical AcroForm hosted in a PDF document.
 * 
 */
public class PDAcroForm extends PDAcroFormNode {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDAcroFormNode.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDAcroForm(object);
		}

		@Override
		protected MetaClass doDetermineClass(COSObject object) {
			if (!(object instanceof COSDictionary)) {
				return null;
			}
			return this;
		}
	}

	/** The name of the fields entry. */
	static public final COSName DK_Fields = COSName.constant("Fields"); // //$NON-NLS-1$

	/** The name of the NeedApperances entry. */
	static public final COSName DK_NeedAppearances = COSName
			.constant("NeedAppearances"); // //$NON-NLS-1$

	/**
	 * The name of the SignatureFlags entry.
	 * <p>
	 * For a list of possible flags:
	 * </p>
	 * 
	 * @see de.intarsys.pdf.pd.AcroFormSigFlags
	 */
	static public final COSName DK_SigFlags = COSName.constant("SigFlags"); //$NON-NLS-1$

	/** The name of the CalculationOrder entry. */
	static public final COSName DK_CO = COSName.constant("CO"); //$NON-NLS-1$

	/** The name of the XFAResources entry. */
	static public final COSName DK_XFA = COSName.constant("XFA"); // //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private AcroFormSigFlags sigFlags;

	private List cachedFields;

	private boolean fieldsChecked = false;

	protected PDAcroForm(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormNode#addField(de.intarsys.pdf.pd.PDAcroFormField)
	 */
	@Override
	public void addField(PDAcroFormField field) {
		checkFields();
		cosAddField(field.cosGetDict());
		field.setParent(null);
		if (field.isTypeSig()) {
			getSigFlags().setSignatureExists(true);
		}
	}

	protected void checkFields() {
		// be careful not to propagate any changes -> deadlock prone
		synchronized (this) {
			if (fieldsChecked) {
				return;
			}
			fieldsChecked = true;
			// reconstruct form fields if there is an empty fields array
			COSArray fields = cosGetField(DK_Fields).asArray();
			if ((fields != null) && (fields.size() == 0)) {
				COSArray cosFields = reconstruct(getDoc());
				cosGetDict().basicPutSilent(DK_Fields, cosFields);
			}
		}
	}

	private void cosAddField(COSDictionary field) {
		COSArray cosFields = cosGetField(DK_Fields).asArray();
		if (cosFields == null) {
			cosFields = COSArray.create();
			cosFields.beIndirect();
			cosSetField(DK_Fields, cosFields);
		}
		cosFields.add(field);
	}

	/**
	 * The /XFA entry of this.
	 * 
	 * @return The /XFA entry of this.
	 */
	public COSObject cosGetXfa() {
		return cosGetField(DK_XFA);
	}

	protected void cosSetSigFlags(int newFlags) {
		if (newFlags != 0) { // default
			cosSetField(DK_SigFlags, COSInteger.create(newFlags));
		} else {
			cosRemoveField(DK_SigFlags);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormNode#getAcroForm()
	 */
	@Override
	public PDAcroForm getAcroForm() {
		return this;
	}

	/**
	 * A collection containing the PDAcroFormField objects in their calculation
	 * order or null if no /C events are defined.
	 * 
	 * @return A collection containing the PDAcroFormField objects in their
	 *         calculation order or null if no /C events are defined.
	 */
	public List getCalculationOrder() {
		return getPDObjects(DK_CO, PDAcroFormField.META, false);
	}

	/**
	 * The default resource dictionary.
	 * 
	 * <p>
	 * With 1.5 this is no longer supported as an entry in field dictionaries,
	 * only in the form itself.
	 * </p>
	 * 
	 * @return The default resource dictionary.
	 */
	public PDResources getDefaultResources() {
		return (PDResources) PDResources.META.createFromCos(cosGetField(DK_DR));
	}

	/**
	 * A list of all direct {@link PDAcroFormField} instances associated with
	 * this object.
	 * 
	 * @return A list of all direct {@link PDAcroFormField} instances associated
	 *         with this object.
	 */
	public List getFields() {
		checkFields();
		if (cachedFields == null) {
			cachedFields = getPDObjects(DK_Fields, PDAcroFormField.META, true);
		}
		return cachedFields;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#getGenericChildren()
	 */
	@Override
	public List getGenericChildren() {
		return getFields();
	}

	/**
	 * <code>true</code> if /NeedAppearances is set for this form.
	 * 
	 * @return <code>true</code> if /NeedAppearances is set for this form.
	 */
	public boolean getNeedAppearances() {
		return getFieldBoolean(DK_NeedAppearances, false);
	}

	/**
	 * The flags associated with an AcroForm.
	 * 
	 * @return The flags associated with an AcroForm.
	 */
	public AcroFormSigFlags getSigFlags() {
		if (sigFlags == null) {
			sigFlags = new AcroFormSigFlags(this);
		}
		return sigFlags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormNode#invalidateCaches()
	 */
	@Override
	public void invalidateCaches() {
		super.invalidateCaches();
		COSArray cosFields = cosGetField(DK_Fields).asArray();
		if (cosFields != null) {
			cosFields.removeObjectListener(this);
		}
		cachedFields = null;
	}

	/**
	 * <code>true</code> if this form has a signature field. This is NOT the
	 * same as the flag in the SigFlags entry but may be used to compute this
	 * entry.
	 * 
	 * @return <code>true</code> if this form has a signature field.
	 */
	public boolean isSignatureExists() {
		for (Iterator i = collectLeafFields().iterator(); i.hasNext();) {
			PDAcroFormField field = (PDAcroFormField) i.next();
			if (field.isTypeSig()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <code>true</code> if this form has a signed signature field.
	 * 
	 * @return <code>true</code> if this form has a signed signature field.
	 */
	public boolean isSigned() {
		for (Iterator i = collectLeafFields().iterator(); i.hasNext();) {
			PDAcroFormField field = (PDAcroFormField) i.next();
			if (field.isTypeSig() && ((PDAFSignatureField) field).isSigned()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This method scans the document for all WidgetAnnotation objects.
	 * 
	 * <p>
	 * This is done because some writer do not create a correct list of all
	 * PDAcroFormField objects in the AcroForm. In the case that the list of
	 * children is empty, we go and search ourselves for candidates...
	 * </p>
	 * 
	 * @param doc
	 *            The document to reconstruct.
	 */
	protected COSArray reconstruct(PDDocument doc) {
		COSArray result = COSArray.create();
		if (doc == null) {
			return result;
		}
		PDPageTree pageTree = doc.getPageTree();
		if (pageTree == null) {
			return result;
		}
		boolean signatureExists = false;
		for (PDPage page = pageTree.getFirstPage(); page != null; page = page
				.getNextPage()) {
			List annotations = page.getAnnotations();
			if (annotations == null) {
				continue;
			}
			for (Iterator it = annotations.iterator(); it.hasNext();) {
				PDAnnotation annot = (PDAnnotation) it.next();
				if (annot.isWidgetAnnotation()) {
					COSDictionary cosAnnot = annot.cosGetDict();
					result.basicAddSilent(cosAnnot);
					cosAnnot.basicRemoveSilent(PDAcroFormField.DK_Parent);
					signatureExists |= cosAnnot.get(PDAcroFormField.DK_FT)
							.equals(PDAcroFormField.CN_FT_Sig);
				}
			}
		}
		if (signatureExists) {
			int flags = getFieldInt(PDAcroForm.DK_SigFlags, 0);
			flags |= AcroFormSigFlags.Bit_AppendOnly
					| AcroFormSigFlags.Bit_SignatureExists;
			cosGetDict().basicPutSilent(PDAcroForm.DK_SigFlags,
					COSInteger.create(flags));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormNode#removeField(de.intarsys.pdf.pd.PDAcroFormField)
	 */
	@Override
	public boolean removeField(PDAcroFormField field) {
		getFields().remove(field);
		COSArray cosFields = cosGetField(DK_Fields).asArray();
		if (cosFields == null) {
			return false;
		}
		boolean removed = cosFields.remove(field.cosGetDict());
		getSigFlags().setSignatureExists(isSignatureExists());
		return removed;
	}

	protected void setCalculationOrder(List newCalculationOrder) {
		setPDObjects(DK_CO, newCalculationOrder);
	}

	/**
	 * Assign the default resource dictionary to be used with the form.
	 * 
	 * @param newResources
	 *            The new default resource dictionary.
	 */
	public void setDefaultResources(PDResources newResources) {
		setFieldObject(DK_DR, newResources);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#setGenericParent(de.intarsys.pdf.pd.PDObject)
	 */
	@Override
	public void setGenericParent(PDObject parent) {
		throw new IllegalStateException("AcroForm may not have a parent"); //$NON-NLS-1$
	}

	/**
	 * Set the /NeedAppearances field for the form. When <code>true</code>, a
	 * viewer application is required to re-create the visual appearances for
	 * the fields.
	 * 
	 * @param newNeedAppearances
	 *            The new state for /NewwdAppearances
	 */
	public void setNeedAppearances(boolean newNeedAppearances) {
		if (newNeedAppearances) {
			cosSetField(DK_NeedAppearances, COSTrue.create());
		} else { // default
			cosRemoveField(DK_NeedAppearances);
		}
	}

	@Override
	public String toString() {
		return "AcroForm " + super.toString(); //$NON-NLS-1$
	}
}
