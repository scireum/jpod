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

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSStream;

/**
 * A pattern to be used when filling a shape.
 *
 */
public abstract class PDPattern extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class<?> paramInstanceClass) {
			super(paramInstanceClass);
		}

		@Override
		public Class<?> getRootClass() {
			return PDPattern.class;
		}

		@Override
		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			COSDictionary dictionary;
			int patternType;

			if (object instanceof COSStream) {
				dictionary = ((COSStream) object).getDict();
			} else {
				dictionary = object.asDictionary();
			}
			patternType = dictionary.get(DK_PatternType).asInteger().intValue();

			switch (patternType) {
			case PATTERN_TYPE_TILING:
				return PDTilingPattern.META;
			case PATTERN_TYPE_SHADING:
				return PDShadingPattern.META;
			default:
				object.handleException(new COSRuntimeException(
						"unsupported pattern type " + patternType)); //$NON-NLS-1$
				return null;
			}
		}
	}

	public static final COSName CN_Type_Pattern = COSName.constant("Pattern"); //$NON-NLS-1$

	public static final COSName DK_Matrix = COSName.constant("Matrix"); //$NON-NLS-1$
	public static final COSName DK_PatternType = COSName
			.constant("PatternType"); //$NON-NLS-1$

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final int PATTERN_TYPE_SHADING = 2;

	public static final int PATTERN_TYPE_TILING = 1;

	protected PDPattern(COSObject object) {
		super(object);
	}

	public abstract int getPatternType();

	/*
	 * (non-Javadoc)
	 *
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Pattern;
	}

	public CDSMatrix getMatrix() {
		return CDSMatrix.createFromCOS(cosGetField(DK_Matrix).asArray());
	}

    public void setMatrix(CDSMatrix matrix) {
        setFieldObject(DK_Matrix, matrix);
    }
}
