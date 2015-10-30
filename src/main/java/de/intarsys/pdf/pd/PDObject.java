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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This is the abstract superclass for all complex PD level objects.
 * <p>
 * <p>
 * PD Level objects provide the PDF semantics on top of the basic datatypes of
 * COS Level objects. PDObject provides generic methods used along all subtypes.
 * <p>
 * PDObjects should be created only using the factory methods of their meta
 * classes to ensure the semantics implemented in the PD layer, as for example
 * PD object identity, subclass selection or proper initialization. <br>
 * Example: <br>
 * <code>
 * PDPage page = (PDPage)PDPage.META.createNew();
 * </code>
 * </p>
 */
abstract public class PDObject extends COSBasedObject {
    /**
     * The meta class implementation
     */
    static public class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    // keys
    static public final COSName DK_Type = COSName.constant("Type"); //

    public static final COSName DK_Subtype = COSName.constant("Subtype");

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDObject(COSObject object) {
        super(object);
    }

    protected COSName cosGetExpectedSubtype() {
        return null;
    }

    protected COSName cosGetExpectedType() {
        return null;
    }

    /**
     * Convenience method to access fields in {@link COSDictionary} based
     * {@link PDObject} instances. This method supports "inheritance" on
     * hierarchical structured {@link PDObject} instances. It returns the field
     * either from the receiver or one of the known descendants.
     *
     * @param name the field to read
     * @return the content of the named field in the PD object or one of its
     * descendants - COSNull is the field is not found
     */
    public COSObject cosGetFieldDescendant(COSName name) {
        COSObject cosBase = cosGetField(name);
        if (cosBase.isNull()) {
            List children = getGenericChildren();
            if (children != null) {
                for (Iterator i = children.iterator(); i.hasNext(); ) {
                    PDObject child = (PDObject) i.next();
                    cosBase = child.cosGetFieldDescendant(name);
                    if (!cosBase.isNull()) {
                        return cosBase;
                    }
                }
            }
        }
        return cosBase;
    }

    /**
     * Convenience method to access fields in {@link COSDictionary} based
     * {@link PDObject} instances.
     * <p>
     * This method supports "inheritance" on hierarchical structured
     * {@link PDObject} instances. It returns the field either from the receiver
     * or one of its parents.
     *
     * @param name the field to read
     * @return the content of the named field in the PD object or one of its
     * parents - COSNull if the field is not found
     */
    public COSObject cosGetFieldInheritable(COSName name) {
        COSObject cosBase = cosGetField(name);
        if (cosBase.isNull() && (getGenericParent() != null)) {
            return getGenericParent().cosGetFieldInheritable(name);
        }
        return cosBase;
    }

    /**
     * Convenience method to access inherited fields in {@link COSDictionary}
     * based {@link PDObject} instances.
     * <p>
     * This method supports "inheritance" on hierarchical structured
     * {@link PDObject} instances. It returns the inherited field value from one
     * of the receiver's parents or {@link COSNull}.
     *
     * @param name the field to read
     * @return the content of the named field in one of the receivers parents -
     * COSNull if the field is not found
     */
    public COSObject cosGetFieldInherited(COSName name) {
        if (getGenericParent() != null) {
            return getGenericParent().cosGetFieldInheritable(name);
        }
        return COSNull.NULL;
    }

    /**
     * The /Subtype field of this {@link PDObject} or null. This method is not
     * supported on {@link PDObject} instances that are not based on a
     * {@link COSDictionary}
     *
     * @return The /Subtype field of this.
     */
    final public COSName cosGetSubtype() {
        return cosGetDict().get(DK_Subtype).asName();
    }

    /**
     * The /Type field of this {@link PDObject} or null. This method is not
     * supported on {@link PDObject} instances that are not based on a
     * {@link COSDictionary}
     *
     * @return The /Type field of this.
     */
    final public COSName cosGetType() {
        return cosGetField(DK_Type).asName();
    }

    /**
     * Convenience method to access fields in {@link COSDictionary} based
     * {@link PDObject} instances. This method supports "inheritance" on
     * hierarchical structured {@link PDObject} instances. It removes a field in
     * the receiver and all its descendants.
     *
     * @param name the field to remove from the receiver
     * @return The object previously associated with <code>name</code> in this
     */
    public COSObject cosRemoveFieldInheritable(COSName name) {
        COSObject result = cosRemoveField(name);
        List children = getGenericChildren();
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                PDObject child = (PDObject) i.next();
                child.cosRemoveFieldInheritable(name);
            }
        }
        return result;
    }

    /**
     * Convenience method to access fields in {@link COSDictionary} based
     * {@link PDObject} instances. This method supports "inheritance" on
     * hierarchical structured {@link PDObject} instances. It sets a field value
     * in the receiver when the field is inheritable. This method removes the
     * field from every child to make sure it uses the inherited value.
     *
     * @param name   the field to set
     * @param cosObj the object to set in the field
     * @return The object previously associated with <code>name</code> in this
     */
    public COSObject cosSetFieldInheritable(COSName name, COSObject cosObj) {
        COSObject result = cosSetField(name, cosObj);
        List children = getGenericChildren();
        if (children != null) {
            for (Iterator i = children.iterator(); i.hasNext(); ) {
                PDObject child = (PDObject) i.next();
                child.cosRemoveFieldInheritable(name);
            }
        }
        return result;
    }

    /**
     * Set the /Subtype field of this {@link PDObject}. This method is not
     * supported on {@link PDObject} instances that are not based on a
     * {@link COSDictionary}
     *
     * @return The /Subtype previously associated with this.
     */
    final public COSName cosSetSubtype(COSName newType) {
        return cosSetField(DK_Subtype, newType).asName();
    }

    /**
     * Set the /Type field of this {@link PDObject}. This method is not
     * supported on {@link PDObject} instances that are not based on a
     * {@link COSDictionary}
     *
     * @return The /Type previously associated with this.
     */
    final public COSName cosSetType(COSName newType) {
        return cosSetField(DK_Type, newType).asName();
    }

    /**
     * Try the best in finding the PDDocument for this PDObject.
     *
     * @return Try the best in finding the PDDocument for this PDObject.
     */
    public PDDocument getDoc() {
        COSDocument cosDoc = cosGetObject().getDoc();
        if (cosDoc == null) {
            return null;
        }
        return PDDocument.createFromCos(cosDoc);
    }

    /**
     * Get a collection of {@link PDObject} children if the receiver is a node
     * in a hierarchical structure (like page nodes or form fields).
     * <p>
     * <p>
     * This enables the generic implementation of inherited field values and so
     * on.
     * </p>
     * <p>
     * <p>
     * A concrete PDObject implementation supporting inheritance should
     * implement this method.
     * </p>
     *
     * @return A collection of {@link PDObject} children if the receiver is a
     * node in a hierarchical structure .
     */
    public List getGenericChildren() {
        return Collections.emptyList();
    }

    /**
     * The parent of the receiver if it is a node in a hierarchical structure
     * (like page nodes or form fields).
     * <p>
     * <p>
     * A concrete PDObject implementation supporting inheritance should
     * implement this method.
     * </p>
     *
     * @return The parent of the receiver if it is a node in a hierarchical
     * structure (like page nodes or form fields).
     */
    public PDObject getGenericParent() {
        return null;
    }

    protected List getPDObjects(COSName key, COSBasedObject.MetaClass metaclass, boolean addListener) {
        COSArray array = cosGetField(key).asArray();
        if (array != null) {
            List result = new ArrayList();
            Iterator i = array.iterator();
            while (i.hasNext()) {
                COSBasedObject pdObject = metaclass.createFromCos((COSObject) i.next());
                if (pdObject != null) {
                    result.add(pdObject);
                }
            }
            if (addListener) {
                array.addObjectListener(this);
            }
            return result;
        }
        return Collections.emptyList();
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        COSName type = cosGetExpectedType();
        if (type != null) {
            cosSetField(DK_Type, type.copyShallow());
        }
        COSName subtype = cosGetExpectedSubtype();
        if (subtype != null) {
            cosSetField(DK_Subtype, subtype.copyShallow());
        }
    }

    /**
     * Set the parent of the receiver if it is a node in a hierarchical
     * structure (like page nodes or form fields).
     * <p>
     * <p>
     * A concrete PDObject implementation supporting inheritance should
     * implement this method.
     * </p>
     *
     * @param parent The new parent object.
     */
    public void setGenericParent(PDObject parent) {
        // do nothing by default
    }

    protected void setPDObjects(COSName key, List list) {
        if (list == null) {
            cosRemoveField(key);
            return;
        }
        COSArray array = cosGetField(key).asArray();
        if (array == null) {
            array = COSArray.create();
        } else {
            array.clear();
        }
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            array.add(((PDObject) i.next()).cosGetDict());
        }
        cosSetField(key, array);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (cosGetObject() == null) {
            return "a " + getClass().getName();
        }
        if (cosGetObject().isIndirect()) {
            return "[" + cosGetObject().getIndirectObject().toString() + "] " + getClass().getName();
        }
        return "a " + getClass().getName();
    }
}
