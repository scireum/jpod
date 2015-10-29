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
import java.util.Map;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

/**
 * A logical choice field within an AcroForm.
 * 
 */
public class PDAFChoiceField extends PDAcroFormField {
	static public class MetaClass extends PDAcroFormField.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDAFChoiceField(object);
		}
	}

	static public final COSName DK_Opt = COSName.constant("Opt");

	static public final COSName DK_TI = COSName.constant("TI");

	static public final COSName DK_I = COSName.constant("I");

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private List cachedOptionNames;

	private List cachedExportValues;

	protected PDAFChoiceField(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormField#cosGetExpectedFieldType()
	 */
	public COSName cosGetExpectedFieldType() {
		return CN_FT_Ch;
	}

	protected void createOptions() {
		COSArray cosOptions = getOptions();
		if ((cosOptions != null) && (cosOptions.size() > 0)) {
			cachedOptionNames = new ArrayList(cosOptions.size());
			cachedExportValues = new ArrayList(cosOptions.size());
			for (Iterator i = cosOptions.iterator(); i.hasNext();) {
				COSObject cosOption = (COSObject) i.next();
				String exportValue = "null"; //$NON-NLS-1$
				String optionName = "null"; //$NON-NLS-1$
				if (cosOption instanceof COSArray) {
					COSArray cosOptionArray = (COSArray) cosOption;
					exportValue = cosOptionArray.get(0).stringValue();
					optionName = cosOptionArray.get(1).stringValue();
				} else if (cosOption instanceof COSString) {
					COSString cosOptionString = (COSString) cosOption;
					exportValue = cosOptionString.stringValue();
					optionName = cosOptionString.stringValue();
				}
				cachedExportValues.add(exportValue);
				cachedOptionNames.add(optionName);
			}
		} else {
			cachedOptionNames = new ArrayList(0);
			cachedExportValues = cachedOptionNames;
		}
	}

	/**
	 * A List containing the option export values as Strings.
	 * 
	 * @return a List containing the option export values as Strings. The result
	 *         will never be null.
	 */
	public List getExportValues() {
		if (cachedExportValues == null) {
			createOptions();
		}
		return cachedExportValues;
	}

	/**
	 * A List containing the option names as Strings.
	 * 
	 * @return a List containing the option names as Strings. The result will
	 *         never be null.
	 */
	public List getOptionNames() {
		if (cachedOptionNames == null) {
			createOptions();
		}
		return cachedOptionNames;
	}

	protected COSArray getOptions() {
		return cosGetFieldInheritable(DK_Opt).asArray();
	}

	protected int getTopIndex() {
		return getFieldInt(DK_TI, 0);
	}

	/**
	 * The value stored in this field as a {@link List} of {@link String}
	 * objects
	 * 
	 * @return The value stored in this field as a {@link List} of
	 *         {@link String} objects
	 */
	public List getValueList() {
		List result;
		COSObject cosValue = super.cosGetValue();
		if (cosValue.isNull()) {
			result = new ArrayList(0);
			return result;
		}
		if (cosValue instanceof COSArray) {
			result = new ArrayList(((COSArray) cosValue).size());
			for (Iterator i = ((COSArray) cosValue).iterator(); i.hasNext();) {
				result.add(((COSObject) i.next()).stringValue());
			}
		} else {
			result = new ArrayList(1);
			result.add(cosValue.stringValue());
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormField#invalidateCaches()
	 */
	public void invalidateCaches() {
		super.invalidateCaches();
		cachedExportValues = null;
		cachedOptionNames = null;
	}

	/**
	 * Convenience method to access "Combo" flag.
	 */
	public boolean isCombo() {
		return getFieldFlags().isCombo();
	}

	/**
	 * Convenience method to access "CommitOnSelChange" flag.
	 */
	public boolean isCommitOnSelChange() {
		return getFieldFlags().isCommitOnSelChange();
	}

	/**
	 * Convenience method to access "Edit" flag.
	 */
	public boolean isEdit() {
		return getFieldFlags().isEdit();
	}

	/**
	 * Convenience method to access "MultiSelect" flag.
	 */
	public boolean isMultiSelect() {
		return getFieldFlags().isMultiSelect();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormField#isTypeCh()
	 */
	public boolean isTypeCh() {
		return true;
	}

	protected void setIndices(List newIndices) {
		// TODO 1 @wad cosSet type: array:integer
	}

	protected void setOptions(COSArray options) {
		cosSetFieldInheritable(DK_Opt, options);
	}

	/**
	 * Assign the currently active options.
	 * 
	 * @param options
	 *            A {@link Map} containing the new options.
	 */
	public void setOptions(Map options) {
		COSArray cosOptions = COSArray.create(options.size());
		for (Iterator i = options.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			if (value == null) {
				value = name;
			}
			COSArray cosOption = COSArray.create(2);
			cosOption.add(COSString.create(name));
			cosOption.add(COSString.create(value));
			cosOptions.add(cosOption);
		}
		setOptions(cosOptions);
	}

	/**
	 * Assign the currently active options.
	 * 
	 * @param names
	 * @param values
	 */
	public void setOptions(String[] names, String[] values) {
		COSArray cosOptions = COSArray.create(names.length);
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			String value = (String) values[i];
			if (value == null) {
				cosOptions.add(COSString.create(name));
			} else {
				COSArray cosOption = COSArray.create(2);
				cosOption.add(COSString.create(value));
				cosOption.add(COSString.create(name));
				cosOptions.add(cosOption);
			}
		}
		setOptions(cosOptions);
	}

	protected void setTopIndex(int newTopIndex) {
		if (newTopIndex == 0) { // default
			cosRemoveField(DK_TI);
		} else {
			setFieldInt(DK_TI, newTopIndex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormField#setValueString(java.lang.String)
	 */
	public void setValueString(String value) {
		if (value == null) {
			super.setValueString(value);
		} else {
			// replace every flavour of new line with a literal \r
			// this is recognized by acrobat as a new line indicator in text
			// values
			value = value.replace('\r', ' ');
			value = value.replace('\n', '\r');
			// set V field of field dict
			super.setValueString(value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAcroFormField#setValueStrings(java.util.List)
	 */
	public void setValueStrings(List values) {
		if ((values == null) || values.isEmpty()) {
			cosSetValue(null);
			return;
		}
		if (values.size() == 1) {
			setValueString((String) values.get(0));
			return;
		}
		COSArray array = COSArray.create(values.size());
		Iterator iter = values.iterator();
		while (iter.hasNext()) {
			String value = (String) iter.next();
			value = value.replace('\r', ' ');
			value = value.replace('\n', '\r');
			array.add(COSString.create(value));
		}

		// set V field of field dict
		cosSetValue(array);
	}
}
