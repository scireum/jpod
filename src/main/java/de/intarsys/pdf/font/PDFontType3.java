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
package de.intarsys.pdf.font;

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.IResourcesProvider;
import de.intarsys.pdf.pd.PDResources;

/**
 * 
 */
public class PDFontType3 extends PDSingleByteFont implements IResourcesProvider {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDFont.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDFontType3(object);
		}
	}

	// additional entries from a type 3 font dict
	public static final COSName DK_FontBBox = COSName.constant("FontBBox");

	public static final COSName DK_FontMatrix = COSName.constant("FontMatrix");

	public static final COSName DK_CharProcs = COSName.constant("CharProcs");

	public static final COSName DK_Resources = COSName.constant("Resources");

	public static final COSName DK_ToUnicode = COSName.constant("ToUnicode");

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	/**
	 * @param object
	 */
	public PDFontType3(COSObject object) {
		super(object);
	}

	public COSDictionary cosGetCharProcs() {
		return cosGetField(DK_CharProcs).asDictionary();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
	 */
	@Override
	protected COSName cosGetExpectedSubtype() {
		return CN_Subtype_Type3;
	}

	public COSDictionary cosSetCharProcs(COSDictionary newDict) {
		return cosSetField(DK_CharProcs, newDict).asDictionary();
	}

	@Override
	protected PDFontDescriptor createBuiltinFontDescriptor() {
		return new PDFontDescriptorType3(this);
	}

	public CDSRectangle getFontBB() {
		return CDSRectangle.createFromCOS(cosGetField(DK_FontBBox).asArray());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.font.IFont#getFontFamilyName()
	 */
	@Override
	public String getFontFamilyName() {
		return "Helvetica";
	}

	public CDSMatrix getFontMatrix() {
		return CDSMatrix.createFromCOS(cosGetField(DK_FontMatrix).asArray());
	}

	@Override
	public String getFontName() {
		return "Helvetica";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.font.IFont#getFontStyle()
	 */
	@Override
	public PDFontStyle getFontStyle() {
		return PDFontStyle.REGULAR;
	}

	@Override
	public String getFontType() {
		return "Type3";
	}

	@Override
	public int getGlyphWidthEncoded(int codePoint) {
		int width = super.getGlyphWidthEncoded(codePoint);
		float[] vector = new float[] { width, 0 };
		// convert to text space
		vector = getFontMatrix().transform(vector);
		// normalize to thousandths off one text space unit
		return (int) (vector[0] * 1000f);
	}

	/**
	 * The {@link PDResources}.
	 * 
	 * @return The {@link PDResources}.
	 */
	public PDResources getResources() {
		COSDictionary dict = cosGetField(DK_Resources).asDictionary();
		return (PDResources) PDResources.META.createFromCos(dict);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.font.PDFont#isEmbedded()
	 */
	@Override
	public boolean isEmbedded() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.font.PDFont#isSubset()
	 */
	@Override
	public boolean isSubset() {
		return false;
	}

	public void setFontBB(CDSRectangle fontBB) {
		setFieldObject(DK_FontBBox, fontBB);
	}

	public void setFontMatrix(CDSMatrix fontMatrix) {
		setFieldObject(DK_FontMatrix, fontMatrix);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.IResourcesProvider#setResources(de.intarsys.pdf.pd.PDResources)
	 */
	public void setResources(PDResources resources) {
		cosSetField(DK_Resources, resources.cosGetDict());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return cosGetSubtype() + "-Font "; //$NON-NLS-1$
	}
}
