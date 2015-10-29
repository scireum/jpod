package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDUnderlineAnnotation extends PDTextMarkupAnnotation {

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDMarkupAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDUnderlineAnnotation(object);
		}
	}

	/** The meta class instance */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected PDUnderlineAnnotation(COSObject object) {
		super(object);
	}

	@Override
	protected COSName cosGetExpectedSubtype() {
		return PDTextMarkupAnnotation.CN_Subtype_Underline;
	}
}
