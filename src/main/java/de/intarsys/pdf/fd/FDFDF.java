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
package de.intarsys.pdf.fd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

import java.util.List;

public class FDFDF extends FDObject {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends FDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new FDFDF(object);
        }
    }

    //
    public static final COSName DK_F = COSName.constant("F");

    public static final COSName DK_ID = COSName.constant("ID");

    public static final COSName DK_Fields = COSName.constant("Fields");

    public static final COSName DK_Status = COSName.constant("Status");

    public static final COSName DK_Pages = COSName.constant("Pages");

    public static final COSName DK_Encoding = COSName.constant("Encoding");

    public static final COSName DK_Annots = COSName.constant("Annots");

    public static final COSName DK_Differences = COSName.constant("Differences");

    public static final COSName DK_Target = COSName.constant("Target");

    public static final COSName DK_EmbeddedFDFs = COSName.constant("EmbeddedFDFs");

    public static final COSName DK_JavaScript = COSName.constant("JavaScript");

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    protected FDFDF(COSObject object) {
        super(object);
    }

    public void addField(FDField field) {
        cosAddField(field.cosGetDict());
    }

    public void cosAddField(COSDictionary field) {
        COSArray cosFields = cosGetField(DK_Fields).asArray();
        if (cosFields == null) {
            cosFields = COSArray.create();
            cosSetField(DK_Fields, cosFields);
        }
        cosFields.add(field);
    }

    public List getFields() {
        return getFDObjects(DK_Fields, FDField.META);
    }

    public String getFile() {
        COSString cosObject = cosGetField(DK_F).asString();
        if (cosObject != null) {
            return cosObject.stringValue();
        }
        return null;
    }

    public void setFields(List fields) {
        setFDObjects(DK_Fields, fields, true);
    }

    public void setFile(String file) {
        setFieldString(DK_F, file);
    }
}
