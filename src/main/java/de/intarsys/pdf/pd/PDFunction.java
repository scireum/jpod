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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * Abstract superclass for PDF function objects.
 */
abstract public class PDFunction extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		public Class getRootClass() {
			return PDFunction.class;
		}

		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			COSDictionary dict = null;
			if (object instanceof COSStream) {
				dict = ((COSStream) object).getDict();
			} else if (object instanceof COSDictionary) {
				dict = (COSDictionary) object;
			}
			if (dict == null) {
				throw new IllegalArgumentException(
						"No Function dictionary available");
			}
			COSInteger type = dict.get(DK_FunctionType).asInteger();
			if (type == null) {
				throw new IllegalArgumentException(
						"Function dictionary has no type");
			}
			if (type.intValue() == 0) {
				return PDSampledFunction.META;
			}
			if (type.intValue() == 2) {
				return PDInterpolationFunction.META;
			}
			if (type.intValue() == 3) {
				return PDStitchingFunction.META;
			}
			if (type.intValue() == 4) {
				return PDPostScriptFunction.META;
			}
			throw new IllegalArgumentException("Function type " + type
					+ " not supported");
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	/** Common names */
	static public final COSName DK_FunctionType = COSName
			.constant("FunctionType");

	static public final COSName DK_Domain = COSName.constant("Domain");

	static public final COSName DK_Range = COSName.constant("Range");

	protected PDFunction(COSObject object) {
		super(object);
	}

	abstract public float[] evaluate(float[] values);

	public float getDomainMax(int dimension) {
		return ((COSNumber) cosGetDomain().get((dimension * 2) + 1))
				.floatValue();
	}

	public float getDomainMin(int dimension) {
		return ((COSNumber) cosGetDomain().get(dimension * 2)).floatValue();
	}

	public int getInputSize() {
		return cosGetDomain().size() / 2;
	}

	abstract public int getOutputSize();

	public COSArray cosGetDomain() {
		return cosGetDict().get(DK_Domain).asArray();
	}

	public COSArray getRange() {
		return cosGetDict().get(DK_Range).asArray();
	}

	public float getRangeMax(int dimension) {
		return ((COSNumber) getRange().get((dimension * 2) + 1)).floatValue();
	}

	public float getRangeMin(int dimension) {
		return ((COSNumber) getRange().get(dimension * 2)).floatValue();
	}

	protected float clip(float x, float min, float max) {
		if (x < min) {
			return min;
		}
		if (x > max) {
			return max;
		}
		return x;
	}

	protected float[] dummyResult() {
		float[] result = new float[getOutputSize()];
		for (int i = 0; i < result.length; i++) {
			result[i] = 0.5f;
		}
		return result;
	}
}
