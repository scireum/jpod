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
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

/**
 * The parameters for the PDTransformMethod algorithms.
 * 
 */
public class PDTransformParams extends PDObject {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDTransformParams(object);
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName CN_Type_TransformParams = COSName
			.constant("TransformParams"); //$NON-NLS-1$

	public static final COSName DK_P = COSName.constant("P"); //$NON-NLS-1$

	public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$

	/*
	 * All, Include, Exclude
	 */
	public static final COSName DK_Action = COSName.constant("Action"); //$NON-NLS-1$

	public static final COSName DK_Fields = COSName.constant("Fields"); //$NON-NLS-1$

	public static final COSName CN_All = COSName.constant("All"); //$NON-NLS-1$

	public static final COSName CN_Include = COSName.constant("Include"); //$NON-NLS-1$

	public static final COSName CN_Exclude = COSName.constant("Exclude"); //$NON-NLS-1$

	public static final COSName CN_Version_1_2 = COSName.constant("1.2");

	protected PDTransformParams(COSObject object) {
		super(object);
	}

	public COSName cosGetAction() {
		return cosGetField(DK_Action).asName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_TransformParams;
	}

	public COSArray cosGetFields() {
		return cosGetField(DK_Fields).asArray();
	}

	public void cosSetAction(COSName action) {
		cosSetField(DK_Action, action);
	}

	public void cosSetFields(COSArray fields) {
		cosSetField(DK_Fields, fields);
	}

	public List<String> getFields() {
		List<String> fields = new ArrayList<String>();
		COSArray cosFields = cosGetFields();
		if (cosFields != null) {
			for (int i = 0; i < cosFields.size(); i++) {
				COSString cosFieldName = cosFields.get(i).asString();
				fields.add(cosFieldName.stringValue());
			}
		}
		return fields;
	}

	public int getPermissions() {
		return getFieldInt(DK_P, 2);
	}

	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		cosSetField(DK_V, CN_Version_1_2);
	}

	public boolean isActionAll() {
		return CN_All.equals(cosGetAction());
	}

	public boolean isActionExclude() {
		return CN_Exclude.equals(cosGetAction());
	}

	public boolean isActionInclude() {
		return CN_Include.equals(cosGetAction());
	}

	public void setPermissions(int permissions) {
		cosSetField(DK_P, COSInteger.create(permissions));
	}

	public void setVersion(String version) {
		cosSetField(DK_V, COSName.create(version));
	}
}
