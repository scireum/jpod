package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDSquigglyAnnotation extends PDTextMarkupAnnotation {

    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDMarkupAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDSquigglyAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    protected PDSquigglyAnnotation(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return PDTextMarkupAnnotation.CN_Subtype_Squiggly;
    }
}
