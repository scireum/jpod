package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDDocumentTimeStamp extends PDSignature {

	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDDocumentTimeStamp(object);
		}
	}

	public static final COSName CN_Type_DocTimeStamp = COSName
			.constant("DocTimeStamp"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(
			MetaClass.class.getDeclaringClass());

	public PDDocumentTimeStamp(COSObject object) {
		super(object);
	}

	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_DocTimeStamp;
	}

}
