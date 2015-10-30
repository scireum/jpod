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

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * Application specific transparently handled data objects.
 */
public class PDApplicationData extends PDObject {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDApplicationData(object);
        }
    }

    static public final COSName DK_LastModified = COSName.constant("LastModified");

    static public final COSName DK_Private = COSName.constant("Private");

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDApplicationData(COSObject object) {
        super(object);
    }

    /**
     * Assign the private data.
     *
     * @param data The private data for this.
     * @return The /Private entry previously associated with this.
     */
    public COSObject cosSetData(COSObject data) {
        return cosSetField(DK_Private, data);
    }

    /**
     * The private data entry or <code>COSNull</code>
     *
     * @return The private data entry or <code>COSNull</code>
     */
    public COSObject cosGetData() {
        return cosGetField(DK_Private);
    }

    /**
     * The timestamp of th elast modification.
     *
     * @return The timestamp of th elast modification.
     */
    public CDSDate getLastModification() {
        return CDSDate.createFromCOS(cosGetField(DK_LastModified).asString());
    }

    /**
     * Assign a new timestamp for the last modification.
     */
    public void touch() {
        setFieldObject(DK_LastModified, new CDSDate());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#initializeFromScratch()
     */
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        touch();
    }
}
