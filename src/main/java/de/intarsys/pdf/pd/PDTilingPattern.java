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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

/**
 * A tile used to fill the shape.
 * 
 */
public class PDTilingPattern extends PDPattern {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDPattern.MetaClass {
		protected MetaClass(Class<?> paramInstanceClass) {
			super(paramInstanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDTilingPattern(object);
		}
	}

	private static final COSName DK_BBox = COSName.constant("BBox"); //$NON-NLS-1$
	private static final COSName DK_Resources = COSName.constant("Resources"); //$NON-NLS-1$
	private static final COSName DK_XStep = COSName.constant("XStep"); //$NON-NLS-1$
	private static final COSName DK_YStep = COSName.constant("YStep"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	/**
	 * Cached content of the pattern.
	 * 
	 * <p>
	 * Requesting the content is expensive.
	 * </p>
	 */
	private CSContent cachedContent;
	private float xStep;
	private float yStep;

	protected PDTilingPattern(COSObject object) {
		super(object);

		COSNumber cosXStep;
		COSNumber cosYStep;

		cosXStep = cosGetField(DK_XStep).asNumber();
		xStep = cosXStep.floatValue();

		cosYStep = cosGetField(DK_YStep).asNumber();
		yStep = cosYStep.floatValue();
	}

	@Override
	public COSDictionary cosGetDict() {
		return cosGetStream().getDict();
	}

	public CDSRectangle getBoundingBox() {
		COSArray array = cosGetField(DK_BBox).asArray();
		if (array == null) {
			return null;
		}
		return CDSRectangle.createFromCOS(array);
	}

	public CSContent getContentStream() {
		if (cachedContent == null) {
			cachedContent = CSContent.createFromCos(cosGetStream());
		}
		return cachedContent;
	}

	@Override
	public int getPatternType() {
		return PATTERN_TYPE_TILING;
	}

	/**
	 * The resource dictionary of the receiver pattern. This method can return
	 * null if no resource dictionary is available. Spec lists resource
	 * dictionary as required however.
	 * 
	 * @return The resource dictionary of the receiver pattern.
	 */
	public PDResources getResources() {
		COSDictionary r = cosGetField(DK_Resources).asDictionary();
		return (PDResources) PDResources.META.createFromCos(r);
	}

	public float getXStep() {
		return xStep;
	}

	public float getYStep() {
		return yStep;
	}
}
