/*
 * Copyright (c) 2007, intarsys consulting GmbH
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of intarsys nor the names of its contributors may be used
 *   to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The ResetForm action.
 * <p>
 * When executed the action clears all field in an AcroForm.
 */
public class PDActionResetForm extends PDAction {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDAction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDActionResetForm(object);
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            return PDActionResetForm.META;
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_ActionType_ResetForm = COSName.constant("ResetForm");

    public static final COSName DK_Fields = COSName.constant("Fields");

    public static final COSName DK_Flags = COSName.constant("Flags");

    static public PDActionResetForm createNew() {
        PDActionResetForm result = (PDActionResetForm) PDActionResetForm.META.createNew();
        return result;
    }

    protected PDActionResetForm(COSObject object) {
        super(object);
    }

    protected int basicGetFlags() {
        // inheritance doesn't make any sense, so we ignore it right now
        return getFieldInt(DK_Flags, 0);
    }

    protected void basicSetFlags(int newFlags) {
        if (newFlags != 0) { // default
            cosSetField(DK_Flags, COSInteger.create(newFlags));
        } else {
            cosRemoveField(DK_Flags);
        }
    }

    @Override
    public COSName cosGetExpectedActionType() {
        return CN_ActionType_ResetForm;
    }

    /**
     * A list of field names (plain Java String) to be resetted or null.
     *
     * @return A list of field names (plain Java String) to be resetted or null.
     */
    public List getFields() {
        COSArray cosFields = cosGetField(DK_Fields).asArray();
        if (cosFields == null) {
            return null;
        }
        List fields = new ArrayList();
        for (int i = 0; i < cosFields.size(); i++) {
            COSObject cosField = cosFields.get(i);
            fields.add(cosField.stringValue());
        }
        return fields;
    }

    public boolean isExclude() {
        return (basicGetFlags() & 1) != 0;
    }
}
