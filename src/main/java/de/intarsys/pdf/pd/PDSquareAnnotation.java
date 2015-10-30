package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDSquareAnnotation extends PDMarkupAnnotation {

    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDMarkupAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDSquareAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDSquareAnnotation(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return PDMarkupAnnotation.CN_Subtype_Square;
    }

    @Override
    public float getDefaultHeight() {
        return 30;
    }

    @Override
    public float getDefaultWidth() {
        return 120;
    }
}
