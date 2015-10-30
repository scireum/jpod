package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

public class PDSignatureSeedValue extends PDObject {

    static public class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDSignatureSeedValue(object);
        }
    }

    public static final COSName CN_Type_SV = COSName.constant("SV"); //$NON-NLS-1$

    public static final COSName DK_Filter = COSName.constant("Filter"); //$NON-NLS-1$

    public static final COSName DK_SubFilter = COSName.constant("SubFilter"); //$NON-NLS-1$

    public static final COSName DK_DigestMethod = COSName.constant("DigestMethod"); //$NON-NLS-1$

    public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$

    public static final COSName DK_Cert = COSName.constant("Cert"); //$NON-NLS-1$

    public static final COSName DK_Reasons = COSName.constant("Reasons"); //$NON-NLS-1$

    public static final COSName DK_MDP = COSName.constant("MDP"); //$NON-NLS-1$

    public static final COSName DK_TimeStamp = COSName.constant("TimeStamp"); //$NON-NLS-1$

    public static final COSName DK_LegalAttestation = COSName.constant("LegalAttestation"); //$NON-NLS-1$

    public static final COSName DK_AddRevInfo = COSName.constant("AddRevInfo"); //$NON-NLS-1$

    public static final COSName DK_Ff = COSName.constant("Ff"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDSignatureSeedValue(COSObject object) {
        super(object);
    }

    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_SV;
    }
}
