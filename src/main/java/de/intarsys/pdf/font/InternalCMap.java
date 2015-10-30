package de.intarsys.pdf.font;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.cos.COSObject;

public class InternalCMap extends StreamBasedCMap {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends StreamBasedCMap.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	protected InternalCMap(COSObject object) {
		super(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSBasedObject#initializeFromCos()
	 */
	@Override
	protected void initializeFromCos() {
		super.initializeFromCos();
		CSContent result = CSContent.createFromCos(cosGetStream());
		initializeFromContent(result);
	}
}
