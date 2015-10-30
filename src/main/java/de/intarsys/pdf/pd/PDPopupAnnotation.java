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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * The implementation of a popup window definition within the document. The
 * popup window is associated with other annotations to enable the user to give
 * comments and replies.
 */
public class PDPopupAnnotation extends PDAnnotation {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends PDAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDPopupAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_Subtype_Popup = COSName.constant("Popup");

    public static final COSName DK_T = COSName.constant("T");

    public static final COSName DK_Parent = COSName.constant("Parent");

    public static final COSName DK_Open = COSName.constant("Open");

    protected PDPopupAnnotation(COSObject object) {
        super(object);
    }

    public String getText() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return getFieldString(DK_T, "");
        } else {
            return parent.getFieldString(DK_T, "");
        }
    }

    public PDAnnotation getParent() {
        return (PDAnnotation) PDAnnotation.META.createFromCos(cosGetField(DK_Parent));
    }

    public void setParent(PDAnnotation parent) {
        setFieldObject(DK_Parent, parent);
    }

    public String getContents() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return super.getContents();
        } else {
            return parent.getContents();
        }
    }

    public void setContents(String contents) {
        PDAnnotation parent = getParent();
        if (parent == null) {
            setFieldMLString(DK_Contents, contents);
        } else {
            parent.setFieldMLString(DK_Contents, contents);
        }
    }

    public String getSubject() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return getFieldString(PDMarkupAnnotation.DK_Subj, "");
        } else {
            return parent.getFieldString(PDMarkupAnnotation.DK_Subj, "");
        }
    }

    public PDPage getPage() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return super.getPage();
        } else {
            return parent.getPage();
        }
    }

    public void setSubject(String subject) {
        PDAnnotation parent = getParent();
        if (parent == null) {
            setFieldString(PDMarkupAnnotation.DK_Subj, subject);
        } else {
            parent.setFieldString(PDMarkupAnnotation.DK_Subj, subject);
        }
    }

    public CDSDate getModified() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return super.getModified();
        } else {
            return parent.getModified();
        }
    }

    public float[] getColor() {
        PDAnnotation parent = getParent();
        if (parent == null) {
            return super.getColor();
        } else {
            return parent.getColor();
        }
    }

    public boolean isOpen() {
        return getFieldBoolean(DK_Open, false);
    }

    public void setOpen(boolean open) {
        setFieldBoolean(DK_Open, open);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
     */
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_Popup;
    }

    public String getSubtypeLabel() {
        return "Popup";
    }
}
