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

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

/**
 * Function implementation for stitching functions.
 */
public class PDStitchingFunction extends PDFunction {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDFunction.MetaClass {
		protected MetaClass(Class paramInstanceClass) {
			super(paramInstanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDStitchingFunction(object);
		}
	}

	private static final COSName DK_Bounds = COSName.constant("Bounds"); //$NON-NLS-1$

	private static final COSName DK_Encode = COSName.constant("Encode"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private static final COSName DK_Functions = COSName.constant("Functions"); //$NON-NLS-1$

	private float[] bounds;

	private float[] encode;

	private PDFunction[] functions;

	protected PDStitchingFunction(COSObject object) {
		super(object);

		COSArray cosBounds;
		COSArray cosEncode;
		int index;

		cosBounds = object.asDictionary().get(DK_Bounds).asArray();
		bounds = new float[cosBounds.size()];
		for (index = 0; index < cosBounds.size(); index++) {
			bounds[index] = ((COSNumber) cosBounds.get(index)).floatValue();
		}

		cosEncode = object.asDictionary().get(DK_Encode).asArray();
		encode = new float[cosEncode.size()];
		for (index = 0; index < cosEncode.size(); index++) {
			encode[index] = ((COSNumber) cosEncode.get(index)).floatValue();
		}

		// color space will be resolved lazily
	}

	@Override
	public float[] evaluate(float[] values) {
		int index;

		for (index = 0; index < bounds.length; index++) {
			if (values[0] < bounds[index]) {
				return getFunctions()[index].evaluate(values);
			}
		}
		return getFunctions()[index].evaluate(values);
	}

	public float[] getBounds() {
		return bounds;
	}

	public float[] getEncode() {
		return encode;
	}

	public PDFunction[] getFunctions() {
		if (functions == null) {
			COSArray cosFunctions;
			int index;

			cosFunctions = cosGetObject().asDictionary().get(DK_Functions)
					.asArray();
			functions = new PDFunction[cosFunctions.size()];
			for (index = 0; index < cosFunctions.size(); index++) {
				functions[index] = (PDFunction) PDFunction.META
						.createFromCos(cosFunctions.get(index));
			}
		}
		return functions;
	}

	@Override
	public int getOutputSize() {
		return getRange().size() / 2;
	}
}
