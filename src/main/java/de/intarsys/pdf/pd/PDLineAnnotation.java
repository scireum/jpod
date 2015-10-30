package de.intarsys.pdf.pd;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

public class PDLineAnnotation extends PDMarkupAnnotation {

	/**
	 * The meta class implementation
	 */
	static public class MetaClass extends PDMarkupAnnotation.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDLineAnnotation(object);
		}
	}

	/**
	 * The meta class instance
	 */
	static public final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public PDLineAnnotation(COSObject object) {
		super(object);
	}

	@Override
	protected COSName cosGetExpectedSubtype() {
		return PDMarkupAnnotation.CN_Subtype_Line;
	}

	@Override
	public float getMinHeight() {
		float width = getBorderStyleWidth();
		return width;
	}

	@Override
	public float getMinWidth() {
		float width = getBorderStyleWidth();
		return width;
	}

	@Override
	protected void updateStateRectangle(CDSRectangle oldRectangle,
			CDSRectangle newRectangle) {
		COSArray line = cosGetField(PDMarkupAnnotation.DK_L).asArray();
		if (line == null) {
			return;
		}
		// get old rectangle for scaling
		double oldX = oldRectangle.getLowerLeftX();
		double oldY = oldRectangle.getLowerLeftY();
		double oldWidth = oldRectangle.getWidth();
		double oldHeight = oldRectangle.getHeight();
		double dX = newRectangle.getLowerLeftX() - oldX;
		double dY = newRectangle.getLowerLeftY() - oldY;
		double fX = newRectangle.getWidth() / oldWidth;
		double fY = newRectangle.getHeight() / oldHeight;
		// scale
		COSArray newLine = COSArray.create();
		cosSetField(PDMarkupAnnotation.DK_L, newLine);
		COSNumber cosX = (COSNumber) line.get(0);
		COSNumber cosY = (COSNumber) line.get(1);
		float x = cosX.floatValue();
		float y = cosY.floatValue();
		newLine.add(COSFixed.create((float) ((x - oldX) * fX + oldX + dX)));
		newLine.add(COSFixed.create((float) ((y - oldY) * fY + oldY + dY)));
		cosX = (COSNumber) line.get(2);
		cosY = (COSNumber) line.get(3);
		x = cosX.floatValue();
		y = cosY.floatValue();
		newLine.add(COSFixed.create((float) ((x - oldX) * fX + oldX + dX)));
		newLine.add(COSFixed.create((float) ((y - oldY) * fY + oldY + dY)));
	}
}
