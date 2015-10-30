package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDCircleAnnotation extends PDMarkupAnnotation {

    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDMarkupAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDCircleAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDCircleAnnotation(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return PDMarkupAnnotation.CN_Subtype_Circle;
    }

    @Override
    public float getDefaultHeight() {
        return 30;
    }

    @Override
    public float getDefaultWidth() {
        return 30;
    }
}
