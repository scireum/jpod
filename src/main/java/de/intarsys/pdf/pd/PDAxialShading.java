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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

/**
 * Use axial shading for filling the shape.
 * 
 */
public class PDAxialShading extends PDShading {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDShading.MetaClass {
		protected MetaClass(Class paramInstanceClass) {
			super(paramInstanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDAxialShading(object);
		}
	}

	private static final COSName DK_Coords = COSName.constant("Coords"); //$NON-NLS-1$

	private static final COSName DK_Domain = COSName.constant("Domain"); //$NON-NLS-1$

	private static final COSName DK_Extend = COSName.constant("Extend"); //$NON-NLS-1$

	private static final COSName DK_Function = COSName.constant("Function"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private float[] coords;

	private float[] domain;

	private boolean[] extend;

	private PDFunction function;

	protected PDAxialShading(COSObject object) {
		super(object);

		COSArray cosCoords;
		COSObject cosDomain;
		COSObject cosExtend;

		cosCoords = object.asDictionary().get(DK_Coords).asArray();
		coords = new float[4];
		for (int index = 0; index < 4; index++) {
			coords[index] = ((COSNumber) cosCoords.get(index)).floatValue();
		}

		cosDomain = object.asDictionary().get(DK_Domain);
		if (cosDomain.isNull()) {
			domain = new float[] { 0, 1 };
		} else {
			domain = new float[2];
			for (int index = 0; index < 2; index++) {
				domain[index] = ((COSNumber) cosDomain.asArray().get(index))
						.floatValue();
			}
		}

		cosExtend = object.asDictionary().get(DK_Extend);
		if (cosExtend.isNull()) {
			extend = new boolean[] { false, false };
		} else {
			extend = new boolean[2];
			for (int index = 0; index < 2; index++) {
				extend[index] = cosExtend.asArray().get(index).asBoolean()
						.booleanValue();
			}
		}

		// function will be resolved lazily
	}

	public float[] getCoords() {
		return coords;
	}

	public float[] getDomain() {
		return domain;
	}

	public PDFunction getFunction() {
		if (function == null) {
			function = (PDFunction) PDFunction.META
					.createFromCos(((COSDictionary) cosGetObject())
							.get(DK_Function));
		}
		return function;
	}

	@Override
	public int getShadingType() {
		return SHADING_TYPE_AXIAL;
	}
}
