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

public class PDCollection extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDCollection(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(
			MetaClass.class.getDeclaringClass());

	static public final COSName CN_Type_Collection = COSName
			.constant("Collection");

	static public final COSName DK_Schema = COSName.constant("Schema");

	static public final COSName DK_D = COSName.constant("D");

	static public final COSName DK_View = COSName.constant("View");

	static public final COSName DK_Sort = COSName.constant("Sort");

	public static final COSName CN_View_D = COSName.constant("D");

	public static final COSName CN_View_T = COSName.constant("T");

	public static final COSName CN_View_H = COSName.constant("H");

	protected PDCollection(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Collection;
	}

	public COSName cosGetView() {
		COSName view = cosGetField(DK_View).asName();
		if (view == null) {
			view = CN_View_D;
		}
		return view;
	}

	public void cosSetView(COSName view) {
		cosSetField(DK_View, view);
	}

	public boolean isViewDetails() {
		return CN_View_D.equals(cosGetView());
	}

	public boolean isViewHidden() {
		return CN_View_T.equals(cosGetView());
	}

	public boolean isViewTiles() {
		return CN_View_T.equals(cosGetView());
	}

}
