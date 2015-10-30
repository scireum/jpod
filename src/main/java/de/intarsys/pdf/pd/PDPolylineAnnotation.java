package de.intarsys.pdf.pd;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

import java.util.Iterator;

public class PDPolylineAnnotation extends PDMarkupAnnotation {

    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDMarkupAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDPolylineAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDPolylineAnnotation(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return PDMarkupAnnotation.CN_Subtype_PolyLine;
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
    protected void updateStateRectangle(CDSRectangle oldRectangle, CDSRectangle newRectangle) {
        COSArray vertices = cosGetField(PDMarkupAnnotation.DK_Vertices).asArray();
        if (vertices == null) {
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
        COSArray newVertices = COSArray.create();
        cosSetField(PDMarkupAnnotation.DK_Vertices, newVertices);
        if (vertices == null) {
            return;
        }
        for (Iterator itVertices = vertices.iterator(); itVertices.hasNext(); ) {
            COSNumber cosX = ((COSObject) itVertices.next()).asNumber();
            if (!itVertices.hasNext()) {
                break;
            }
            COSNumber cosY = ((COSObject) itVertices.next()).asNumber();
            if (cosX != null && cosY != null) {
                float x = cosX.floatValue();
                float y = cosY.floatValue();
                newVertices.add(COSFixed.create((float) ((x - oldX) * fX + oldX + dX)));
                newVertices.add(COSFixed.create((float) ((y - oldY) * fY + oldY + dY)));
            }
        }
    }
}
