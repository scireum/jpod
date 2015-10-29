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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * The /Text annotation.
 */
public class PDTextAnnotation extends PDMarkupAnnotation {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDMarkupAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDTextAnnotation(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName CN_Subtype_Text = COSName.constant("Text");

	public static final COSName DK_Name = COSName.constant("Name");

	public static final COSName DK_Open = COSName.constant("Open");

	public static final COSName DK_State = COSName.constant("State");

	public static final COSName DK_StateModel = COSName.constant("StateModel");

	protected PDTextAnnotation(COSObject object) {
		super(object);
	}

	@Override
	protected COSName cosGetExpectedSubtype() {
		return PDTextAnnotation.CN_Subtype_Text;
	}

	@Override
	public float getDefaultHeight() {
		return 30;
	}

	@Override
	public float getDefaultWidth() {
		return 30;
	}

	public COSName getIconName() {
		return cosGetField(DK_Name).asName();
	}

	@Override
	public float getMinHeight() {
		return 10;
	}

	@Override
	public float getMinWidth() {
		return 10;
	}

	@Override
	public String getSubtypeLabel() {
		return "Text";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAnnotation#isPrint()
	 */
	@Override
	public boolean isPrint() {
		// never print text annotations
		// see implementation note 76 pdf 1.5
		return false;
	}

	public COSName setIconName(COSName name) {
		return cosSetField(DK_Name, name).asName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDAnnotation#supportsPrint()
	 */
	@Override
	public boolean supportsPrint() {
		// never print text annotations
		// see implementation note 76 pdf 1.5
		return false;
	}
}
