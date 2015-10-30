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
 * Support for lab color space.
 */
public class PDCSLab extends PDCSCIEBased {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDCSCIEBased.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		public COSBasedObject doCreateCOSBasedObjectBasic(COSObject object) {
			return new PDCSLab(object);
		}

	}

	private static final float[] BlackPointDefault = new float[] { 0.0f, 0.0f,
			0.0f };

	public static final COSName DK_BlackPoint = COSName.constant("BlackPoint"); //$NON-NLS-1$

	public static final COSName DK_Range = COSName.constant("Range"); //$NON-NLS-1$

	public static final COSName DK_WhitePoint = COSName.constant("WhitePoint"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private static final float[] RangeDefault = new float[] { -100.0f, 100.0f,
			-100.0f, 100.0f };

	private float[] blackPoint;

	private float[] range;

	private float[] whitePoint;

	protected PDCSLab(COSObject object) {
		super(object);

		COSDictionary dict;
		COSArray blackPointArray;
		COSArray rangeArray;
		COSArray whitePointArray;

		dict = ((COSArray) object).get(1).asDictionary();
		// TODO 3 @ehk dict == null ?
		blackPointArray = dict.get(PDCSLab.DK_BlackPoint).asArray();
		if (blackPointArray == null) {
			blackPoint = PDCSLab.BlackPointDefault;
		} else {
			blackPoint = new float[3];
			for (int index = 0; index < blackPoint.length; index++) {
				blackPoint[index] = ((COSNumber) blackPointArray.get(index))
						.floatValue();
			}
		}

		rangeArray = dict.get(PDCSLab.DK_Range).asArray();
		if (rangeArray == null) {
			range = PDCSLab.RangeDefault;
		} else {
			range = new float[4];

			for (int index = 0; index < 4; index++) {
				range[index] = ((COSNumber) rangeArray.get(index)).floatValue();
			}
		}

		whitePointArray = dict.get(PDCSLab.DK_WhitePoint).asArray();
		whitePoint = new float[3];
		for (int index = 0; index < whitePoint.length; index++) {
			whitePoint[index] = ((COSNumber) whitePointArray.get(index))
					.floatValue();
		}
	}

	public float[] getWhitePoint() {
		return whitePoint;
	}
}
