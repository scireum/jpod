package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.tools.string.StringTools;

import java.util.ArrayList;
import java.util.List;

public class PDSignatureLock extends PDObject {

    static public class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDSignatureLock(object);
        }
    }

    public static final COSName CN_Type_SigFieldLock = COSName.constant("SigFieldLock"); //$NON-NLS-1$

    public static final COSName DK_Action = COSName.constant("Action"); //$NON-NLS-1$

    public static final COSName DK_Fields = COSName.constant("Fields"); //$NON-NLS-1$

    public static final COSName CN_All = COSName.constant("All"); //$NON-NLS-1$

    public static final COSName CN_Exclude = COSName.constant("Exclude"); //$NON-NLS-1$

    public static final COSName CN_Include = COSName.constant("Include"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public PDSignatureLock(COSObject object) {
        super(object);
    }

    public COSName cosGetAction() {
        return cosGetField(DK_Action).asName();
    }

    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_SigFieldLock;
    }

    public COSArray cosGetFields() {
        return cosGetField(DK_Fields).asArray();
    }

    public void cosSetAction(COSName action) {
        cosSetField(DK_Action, action);
    }

    public void cosSetFields(COSArray fields) {
        cosSetField(DK_Fields, fields);
    }

    public List<String> getFields() {
        List<String> fields = new ArrayList<String>();
        COSArray cosFields = cosGetFields();
        if (cosFields != null) {
            for (int i = 0; i < cosFields.size(); i++) {
                COSString cosFieldName = cosFields.get(i).asString();
                fields.add(cosFieldName.stringValue());
            }
        }
        return fields;
    }

    public boolean isActionAll() {
        return CN_All.equals(cosGetAction());
    }

    public boolean isActionExclude() {
        return CN_Exclude.equals(cosGetAction());
    }

    public boolean isActionInclude() {
        return CN_Include.equals(cosGetAction());
    }

    public void setFields(List<String> fields) {
        if (fields == null) {
            cosSetField(DK_Fields, null);
        } else {
            COSArray cosFields = COSArray.create(fields.size());
            for (String field : fields) {
                if (StringTools.isEmpty(field)) {
                    continue;
                }
                cosFields.add(COSString.create(field));
            }
            cosSetField(DK_Fields, cosFields);
        }
    }
}
