package de.intarsys.pdf.pd;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

import java.util.Iterator;

public class PDInkAnnotation extends PDMarkupAnnotation {

    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDMarkupAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDInkAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDInkAnnotation(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return PDMarkupAnnotation.CN_Subtype_Ink;
    }

    @Override
    public float getMinHeight() {
        return getBorderStyleWidth();
    }

    @Override
    public float getMinWidth() {
        return getBorderStyleWidth();
    }

    @Override
    protected void updateStateRectangle(CDSRectangle oldRectangle, CDSRectangle newRectangle) {
        COSArray traces = cosGetField(PDMarkupAnnotation.DK_InkList).asArray();
        if (traces == null) {
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
        COSArray newTraces = COSArray.create();
        cosSetField(PDMarkupAnnotation.DK_InkList, newTraces);
        if (traces == null) {
            return;
        }
        for (Iterator itTraces = traces.iterator(); itTraces.hasNext(); ) {
            COSArray trace = ((COSObject) itTraces.next()).asArray();
            if (trace == null) {
                continue;
            }
            COSArray newTrace = COSArray.create();
            newTraces.add(newTrace);
            for (Iterator itTrace = trace.iterator(); itTrace.hasNext(); ) {
                COSNumber cosX = ((COSObject) itTrace.next()).asNumber();
                if (!itTrace.hasNext()) {
                    break;
                }
                COSNumber cosY = ((COSObject) itTrace.next()).asNumber();
                if (cosX != null && cosY != null) {
                    float x = cosX.floatValue();
                    float y = cosY.floatValue();
                    newTrace.add(COSFixed.create((float) ((x - oldX) * fX + oldX + dX)));
                    newTrace.add(COSFixed.create((float) ((y - oldY) * fY + oldY + dY)));
                }
            }
        }
    }
}
