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
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Abstract superclass for the different field types in an AcroForm.
 */
public abstract class PDAcroFormField extends PDAcroFormNode implements IAdditionalActionSupport {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDAcroFormNode.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        protected COSName cosGetFieldType(COSDictionary dict) {
            COSName type = dict.get(DK_FT).asName();
            if (type == null) {
                COSDictionary parent = dict.get(DK_Parent).asDictionary();
                if (parent != null) {
                    return cosGetFieldType(parent);
                }
            }
            return type;
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            if (!(object instanceof COSDictionary)) {
                return null;
            }
            COSObject fieldType = cosGetFieldType((COSDictionary) object);
            if (CN_FT_Tx.equals(fieldType)) {
                return PDAFTextField.META;
            } else if (CN_FT_Btn.equals(fieldType)) {
                return PDAFButtonField.META;
            } else if (CN_FT_Ch.equals(fieldType)) {
                return PDAFChoiceField.META;
            } else if (CN_FT_Sig.equals(fieldType)) {
                return PDAFSignatureField.META;
            } else {
                return PDAFIntermediateNode.META;
            }
        }
    }

    /**
     * The name for field types entry.
     */
    public static final COSName DK_FT = COSName.constant("FT"); //$NON-NLS-1$

    /**
     * Field type Button
     */
    public static final COSName CN_FT_Btn = COSName.constant("Btn"); //$NON-NLS-1$

    /**
     * Field type Text
     */
    public static final COSName CN_FT_Tx = COSName.constant("Tx"); //$NON-NLS-1$

    /**
     * Field type Choice
     */
    public static final COSName CN_FT_Ch = COSName.constant("Ch"); //$NON-NLS-1$

    /**
     * Field type Signature
     */
    public static final COSName CN_FT_Sig = COSName.constant("Sig"); //$NON-NLS-1$

    /**
     * The name for the parent entry.
     */
    public static final COSName DK_Parent = COSName.constant("Parent"); //$NON-NLS-1$

    /**
     * The name for the childrens entry.
     */
    public static final COSName DK_Kids = COSName.constant("Kids"); //$NON-NLS-1$

    /**
     * The name for the partial field name entry, also called the local name.
     */
    public static final COSName DK_T = COSName.constant("T"); //$NON-NLS-1$

    /**
     * The name for the AlternateFieldName entry.
     */
    public static final COSName DK_TU = COSName.constant("TU"); //$NON-NLS-1$

    /**
     * The name for the mapping entry.
     */
    public static final COSName DK_TM = COSName.constant("TM"); //$NON-NLS-1$

    /**
     * The name of the FieldFlags entry.
     * <p>
     * For a list of possible flags:
     * </p>
     *
     * @see de.intarsys.pdf.pd.AcroFormFieldFlags
     */
    public static final COSName DK_Ff = COSName.constant("Ff"); //$NON-NLS-1$

    /**
     * The name for the fields value entry.
     */
    public static final COSName DK_V = COSName.constant("V"); //$NON-NLS-1$

    /**
     * The key for the DefaultValue entry.
     */
    public static final COSName DK_DV = COSName.constant("DV"); //$NON-NLS-1$

    /**
     * alignment constant: 0: Left-justified
     */
    public static final int ALIGNMENT_LEFT = 0;

    /**
     * alignment constant: 1: Centered
     */
    public static final int ALIGNMENT_CENTER = 1;

    /**
     * alignment constant: 2: Right-justified
     */
    public static final int ALIGNMENT_RIGHT = 2;

    /**
     * The name for the DefaultStyle entry.
     */
    public static final COSName DK_DS = COSName.constant("DS"); //$NON-NLS-1$

    /**
     * The name for RichTextString value entry.
     */
    public static final COSName DK_RV = COSName.constant("RV"); //$NON-NLS-1$

    /**
     * supported additional action triggers
     */
    public static final Set ACROFORMFIELD_ACTION_TRIGGERS;

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * The name for the MaximumLength entry.
     */
    public static final COSName DK_MaxLen = COSName.constant("MaxLen"); // //$NON-NLS-1$

    static {
        ACROFORMFIELD_ACTION_TRIGGERS = new HashSet(5);
        ACROFORMFIELD_ACTION_TRIGGERS.add("K"); //$NON-NLS-1$
        ACROFORMFIELD_ACTION_TRIGGERS.add("F"); //$NON-NLS-1$
        ACROFORMFIELD_ACTION_TRIGGERS.add("V"); //$NON-NLS-1$
        ACROFORMFIELD_ACTION_TRIGGERS.add("C"); //$NON-NLS-1$
    }

    private AcroFormFieldFlags fieldFlags;

    private List cachedKids;

    protected PDAcroFormField(COSObject object) {
        super(object);
    }

    /**
     * Insert a new {@link PDAnnotation} object for the form field. The
     * annotation represents the visual component displaying the logical form
     * field. A form field can be represented by more than one annotation.
     *
     * @param annot The {@link PDAnnotation} to add to the field.
     */
    public void addAnnotation(PDWidgetAnnotation annot) {
        cosAddKid(annot.cosGetDict());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.pd.PDAcroFormNode#addField(de.intarsys.pdf.pd.PDAcroFormField
     * )
     */
    @Override
    public void addField(PDAcroFormField field) {
        cosAddKid(field.cosGetDict());
        if (field.isTypeSig()) {
            getAcroForm().getSigFlags().setSignatureExists(true);
        }
    }

    /**
     * Get the integer representing the field flags.
     *
     * @return Get the integer representing the field flags.
     */
    public int basicGetFieldFlags() {
        COSInteger flags = cosGetFieldInheritable(DK_Ff).asInteger();
        if (flags != null) {
            return flags.intValue();
        }
        return 0; // default
    }

    /**
     * Assign the integer representing the field flags.
     *
     * @param newFlags The new flags
     */
    public void basicSetFieldFlags(int newFlags) {
        if (getParent() != null) {
            COSInteger parentFlag = getParent().cosGetFieldInheritable(DK_Ff).asInteger();
            if ((parentFlag != null) && (parentFlag.intValue() == newFlags)) {
                cosRemoveField(DK_Ff); // clear own flag
                return;
            }
        }
        cosSetField(DK_Ff, COSInteger.create(newFlags));
    }

    protected boolean checkFieldType(COSName checkType) {
        return checkType.equals(cosGetFieldType());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAcroFormNode#collectLeafFields(java.util.List)
     */
    @Override
    protected List collectLeafFields(List result) {
        result.add(this);
        return result;
    }

    /**
     * Copy the receiver.
     * <p>
     * <p>
     * The copy made does NOT contain any children information.
     * </p>
     *
     * @return A copy of the receiver.
     */
    public PDAcroFormField copy() {
        // TODO 2 define good copy semantics
        COSDictionary dict = COSDictionary.create();
        dict.beIndirect();

        dict.put(DK_FT, cosGetField(DK_FT).copyShallow());
        PDAcroFormField copy = (PDAcroFormField) PDAcroFormField.META.createFromCos(dict);

        // copy the important parameters
        copy.cosSetField(DK_T, cosGetField(DK_T).copyShallow());
        copy.cosSetField(DK_V, cosGetField(DK_V).copyShallow());
        copy.cosSetField(DK_TU, cosGetField(DK_TU).copyShallow());
        copy.cosSetField(DK_TM, cosGetField(DK_TM).copyShallow());
        copy.cosSetField(DK_Ff, cosGetField(DK_Ff).copyShallow());
        copy.cosSetField(DK_DV, cosGetField(DK_DV).copyShallow());
        copy.cosSetField(DK_DA, cosGetField(DK_DA).copyShallow());
        copy.cosSetField(DK_Q, cosGetField(DK_Q).copyShallow());
        copy.cosSetField(PDAFChoiceField.DK_Opt, cosGetField(PDAFChoiceField.DK_Opt).copyShallow());
        copy.cosSetField(DK_MaxLen, cosGetField(DK_MaxLen).copyShallow());
        copy.cosSetField(PDAFChoiceField.DK_TI, cosGetField(PDAFChoiceField.DK_TI).copyShallow());
        copy.cosSetField(PDAFChoiceField.DK_I, cosGetField(PDAFChoiceField.DK_I).copyShallow());
        return copy;
    }

    protected void cosAddKid(COSDictionary dict) {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids == null) {
            cosKids = COSArray.create();
            cosSetField(DK_Kids, cosKids);
        }
        cosKids.add(dict);
        dict.put(DK_Parent, cosGetDict());
    }

    /**
     * Tries to return a valid /DV entry:
     * <ol>
     * <li>returns its own /DV entry, if it is not null</li>
     * <li>returns the /DV entry of its logical root, if it is not null</li>
     * <li>returns the first /DV entry found of the logical roots kids</li>
     * <li>null if nothing was found</li>
     * </ol>
     *
     * @return see description
     */
    public COSObject cosGetDefaultValue() {
        return cosGetValueEntry(DK_DV);
    }

    /**
     * The AcroForm field type expected for this.
     *
     * @return The AcroForm field type expected for this.
     */
    public abstract COSName cosGetExpectedFieldType();

    /**
     * The real AcroForm field type of this.
     *
     * @return The real AcroForm field type of this.
     */
    public COSName cosGetFieldType() {
        return cosGetFieldInheritable(DK_FT).asName();
    }

    /**
     * Tries to return a valid /V entry:
     * <ol>
     * <li>returns its own /V entry, if it is not null</li>
     * <li>returns the /V entry of its logical root, if it is not null</li>
     * <li>returns the first /V entry found of the logical roots kids</li>
     * <li>null if nothing was found</li>
     * </ol>
     *
     * @return see description
     */
    public COSObject cosGetValue() {
        return cosGetValueEntry(DK_V);
    }

    /**
     * Tries to return a valid value entry (/DV or /V):
     * <ol>
     * <li>returns its own value entry, if it is not null</li>
     * <li>returns the value entry of its logical root, if it is not null</li>
     * <li>returns the first value entry found of the logical roots kids</li>
     * <li>null if nothing was found</li>
     * </ol>
     *
     * @return see description
     */
    protected COSObject cosGetValueEntry(COSName key) {
        COSObject cosValue = cosGetField(key);
        if (cosValue.isNull()) {
            cosValue = getLogicalRoot().cosGetFieldDescendant(key);
        }
        return cosValue;
    }

    protected boolean cosRemoveKid(COSDictionary dict) {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids == null) {
            return false;
        }
        if (cosKids.remove(dict)) {
            dict.remove(DK_Parent);
            return true;
        }
        return false;
    }

    protected COSObject cosSetDefaultValue(COSObject newDefaultValue) {
        return cosSetFieldInheritable(DK_DV, newDefaultValue);
    }

    protected COSObject cosSetFieldType(COSName newFieldType) {
        return cosSetFieldInheritable(DK_FT, newFieldType);
    }

    protected void cosSetMappingName(String newMappingName) {
        setFieldName(DK_TM, newMappingName);
    }

    protected void cosSetRichTextString(String newRichTextString) {
        setFieldName(DK_RV, newRichTextString);
    }

    /**
     * Sets the /V entry in this node and removes all /V entries in child nodes.
     *
     * @param newValue
     * @return The /V entry previously associated with this.
     */
    public COSObject cosSetValue(COSObject newValue) {
        return cosSetFieldInheritable(DK_V, newValue);
    }

    /**
     * @param disposeEmptyAncestors
     * @deprecated
     */
    @Deprecated
    public void dispose(boolean disposeEmptyAncestors) {
        PDAcroFormField removeTarget = null;
        if (getLogicalRoot().cosGetDict() == cosGetDict()) {
            // I am the logical root
            removeTarget = getParent();
        } else {
            // just an anonymous leaf
            removeTarget = getLogicalRoot();
        }

        //
        if (removeTarget == null) {
            // remove from acro form
            getAcroForm().removeField(this);
        } else {
            removeTarget.removeField(this);
            if (disposeEmptyAncestors && (removeTarget.getKids().isEmpty())) {
                // target has no more kids, dispose it
                removeTarget.dispose(disposeEmptyAncestors);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAcroFormNode#getAcroForm()
     */
    @Override
    public PDAcroForm getAcroForm() {
        PDDocument doc = getDoc();
        if (doc == null) {
            return null;
        }
        return doc.getAcroForm();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.IAdditionalActionSupport#getAdditionalActions()
     */
    @Override
    public PDAdditionalActions getAdditionalActions() {
        COSDictionary field = cosGetField(DK_AA).asDictionary();
        return (PDAdditionalActions) PDAdditionalActions.META.createFromCos(field);
    }

    /**
     * The fields alternate name.
     *
     * @return The fields alternate name.
     */
    public String getAlternateFieldName() {
        return getFieldString(DK_TU, null);
    }

    @Override
    protected List getAnnotations(List annotations) {
        if (isAnnotation()) {
            annotations.add(PDAnnotation.META.createFromCos(cosGetDict()));
        }
        return super.getAnnotations(annotations);
    }

    /**
     * a acro form field may be associated with a widget annotation. somewhere
     * down in the hierarchy there MUST be an annotation. this method returns
     * the first annotation available.
     *
     * @return the first associated annotation object if available (or null)
     * @deprecated
     */
    @Deprecated
    public PDAnnotation getAnyAnnotation() {
        // todo 1 why not PDWIdgetAnnotation
        if (isAnnotation()) {
            return (PDAnnotation) PDAnnotation.META.createFromCos(cosGetDict());
        }
        for (Iterator i = getGenericChildren().iterator(); i.hasNext(); ) {
            PDAcroFormField kid = (PDAcroFormField) i.next();
            PDAnnotation result = kid.getAnyAnnotation();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    protected String getDefaultStyle() {
        return getFieldString(DK_DS, null);
    }

    /**
     * The value from cosGetDefaultValue() converted to a String
     *
     * @return The value from cosGetDefaultValue() converted to a String
     */
    public String getDefaultValueString() {
        COSObject value = cosGetDefaultValue();
        if (value.isNull()) {
            return null;
        }
        return value.stringValue();
    }

    /**
     * The flags associated with the form field.
     *
     * @return The flags associated with the form field.
     */
    public AcroFormFieldFlags getFieldFlags() {
        if (fieldFlags == null) {
            fieldFlags = new AcroFormFieldFlags(this);
        }
        return fieldFlags;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#getGenericChildren()
     */
    @Override
    public List getGenericChildren() {
        return getKids();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#getGenericParent()
     */
    @Override
    public PDObject getGenericParent() {
        PDObject parent = getParent();
        if (parent == null) {
            return getAcroForm();
        }
        return parent;
    }

    /**
     * The child nodes of this (named after the /Kids key in the defining
     * {@link COSDictionary});
     *
     * @return The child nodes of this.
     */
    public List getKids() {
        if (cachedKids == null) {
            cachedKids = getPDObjects(DK_Kids, PDAcroFormField.META, true);
            if (cachedKids == null) {
                // do not cache!
                return Collections.emptyList();
            }
        }
        return cachedKids;
    }

    /**
     * The local name for this node. This method returns null for an non
     * existing or empty /T entry in the defining {@link COSDictionary}.
     *
     * @return The local name for this node.
     */
    public String getLocalName() {
        return getLocalName(false);
    }

    /**
     * The local name for this node. This method returns null for an non
     * existing or empty /T entry in the base {@link COSDictionary}. The name is
     * transformed to canonical form if {@code canonical} is true.
     *
     * @param canonical Flag if the name shoul be in canonical form.
     * @return The local name for this node.
     */
    public String getLocalName(boolean canonical) {
        String name = getFieldString(DK_T, null);
        if (name != null && !name.trim().isEmpty()) { //$NON-NLS-1$
            return canonical ? canonicalize(name) : name;
        }
        return null;
    }

    /**
     * The logical parent node of this PDAcroFormField.
     * <p>
     * <p>
     * The logical parent is the next node up the parent hierarchy that has a
     * different qualified name, this means it is a node that does not designate
     * the same logical field as the receiver.
     * </p>
     *
     * @return the logical parent, or the form itself.
     */
    public PDAcroFormNode getLogicalParent() {
        PDAcroFormField root = getLogicalRoot();
        if (root == null) {
            return getAcroForm();
        }
        PDAcroFormField p = root.getParent();
        if (p == null) {
            return getAcroForm();
        }
        return p;
    }

    /**
     * The logical root node of this PDAcroFormField.
     * <p>
     * <p>
     * The logical root is the last node up the parent hierarchy that has the
     * same qualified name, this means the topmost node that designates the same
     * logical field as the receiver.
     * </p>
     *
     * @return the root field, or this.
     */
    public PDAcroFormField getLogicalRoot() {
        if (getLocalName(false) == null) {
            PDAcroFormField p = getParent();
            if (p == null) {
                return this;
            }
            return p.getLogicalRoot();
        }
        return this;
    }

    protected String getMappingName() {
        return getFieldString(DK_TM, null);
    }

    /**
     * The maximum length for this field or {@code null} if not defined.
     *
     * @return The maximum length for this field or {@code null} if not
     * defined.
     */
    public Integer getMaxLen() {
        COSInteger cosBase = cosGetFieldInheritable(DK_MaxLen).asInteger();
        if (cosBase != null) {
            return Integer.valueOf(cosBase.intValue());
        }
        cosBase = cosGetFieldDescendant(DK_MaxLen).asInteger();
        if (cosBase != null) {
            return Integer.valueOf(cosBase.intValue());
        }
        return null;
    }

    /**
     * The parent node of this.
     * <p>
     * AcroForm fields are arranged in a hierarchical structure, beginning with
     * the root AcroForm. Fields under the AcroForm return {@code null} as
     * their parent.
     *
     * @return The parent node of this.
     */
    public PDAcroFormField getParent() {
        COSDictionary dict = cosGetField(DK_Parent).asDictionary();
        if (dict != null) {
            return (PDAcroFormField) PDAcroFormField.META.createFromCos(dict);
        }
        return null;
    }

    /**
     * Returns the parent name, if any, or null.
     *
     * @param canonical True if the name should be canonical.
     * @return the name of the direct parent PDAcroFormField
     */
    protected String getParentName(boolean canonical) {
        if (getParent() == null) {
            return null;
        }
        return getParent().getQualifiedName(canonical);
    }

    /**
     * A fully qualified name for this.
     * <p>
     * <p>
     * The name of a acro form field is the concatenation of every node's name
     * in the hierarchy, separated with '.'.
     * </p>
     *
     * @return A fully qualified name for this.
     */
    public String getQualifiedName() {
        return getQualifiedName(false);
    }

    /**
     * A fully qualified name for this. If requested, the name is in canonical
     * format.
     * <p>
     * <p>
     * The name of a acro form field is the concatenation of every node's name
     * in the hierarchy, separated with '.'.
     * </p>
     *
     * @param canonical Flag if the name is in canonical format
     * @return A fully qualified name for this.
     */
    public String getQualifiedName(boolean canonical) {
        String partialName = getLocalName(canonical);
        String parentName = getParentName(canonical);

        String qualifiedName = parentName;
        if (partialName != null) {
            if (parentName == null) {
                qualifiedName = partialName;
            } else {
                qualifiedName = parentName + "." + partialName; //$NON-NLS-1$
            }
        }
        return qualifiedName;
    }

    protected String getRichTextString() {
        return getFieldString(DK_RV, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.pd.IAdditionalActionSupport#getSupportedTriggerEvents()
     */
    @Override
    public Set getSupportedTriggerEvents() {
        return ACROFORMFIELD_ACTION_TRIGGERS;
    }

    /**
     * The value from cosGetValue() converted to a String
     *
     * @return The value from cosGetValue() converted to a String
     */
    public String getValueString() {
        COSObject value = cosGetValue();
        if (value.isNull()) {
            return null;
        }
        return value.stringValue();
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        cosSetFieldType(cosGetExpectedFieldType());
        cosSetField(DK_T, COSString.create(new byte[0]));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAcroFormNode#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids != null) {
            cosKids.removeObjectListener(this);
        }
        if (cachedKids != null) {
            // name may have changed
            for (Iterator i = cachedKids.iterator(); i.hasNext(); ) {
                PDAcroFormField field = (PDAcroFormField) i.next();
                field.invalidateCaches();
            }
            cachedKids = null;
        }
    }

    /**
     * {@code true} if variable text in this is centered.
     *
     * @return {@code true} if variable text in this is centered.
     */
    public boolean isAlignmentCenter() {
        return getQuadding() == ALIGNMENT_CENTER;
    }

    /**
     * {@code true} if variable text in this is left aligned.
     *
     * @return {@code true} if variable text in this is left aligned.
     */
    public boolean isAlignmentLeft() {
        return getQuadding() == ALIGNMENT_LEFT;
    }

    /**
     * {@code true} if variable text in this is right aligned.
     *
     * @return {@code true} if variable text in this is right aligned.
     */
    public boolean isAlignmentRight() {
        return getQuadding() == ALIGNMENT_RIGHT;
    }

    /**
     * Returns true when the field is an annotation
     *
     * @return true when the field is an annotation
     */
    public boolean isAnnotation() {
        // checking subtype because "/Type Annot" is optional
        return !cosGetDict().get(PDObject.DK_Subtype).isNull();
    }

    /**
     * Convenience to access "comb" flag.
     */
    public boolean isComb() {
        return getFieldFlags().isComb();
    }

    /**
     * Convenience to access "DoNotScroll" flag.
     */
    public boolean isDoNotScroll() {
        return getFieldFlags().isDoNotScroll();
    }

    /**
     * Convenience to access "Multiline" flag.
     */
    public boolean isMultiline() {
        return getFieldFlags().isMultiline();
    }

    /**
     * Convenience to access "NoExport" flag.
     */
    public boolean isNoExport() {
        return getFieldFlags().isNoExport();
    }

    /**
     * Convenience to access "ReadOnly" flag.
     */
    public boolean isReadOnly() {
        return getFieldFlags().isReadOnly();
    }

    /**
     * Return true if this is a field of type "Btn".
     * <p>
     * <p>
     * "Btn" means this is a button style object.
     * </p>
     *
     * @return Return true if this is a field of type "Btn".
     */
    public boolean isTypeBtn() {
        return false;
    }

    /**
     * Return true if this is a field of type "Ch".
     * <p>
     * <p>
     * "Ch" means this is a choice object.
     * </p>
     *
     * @return Return true if this is a field of type "Ch".
     */
    public boolean isTypeCh() {
        return false;
    }

    /**
     * Return true if this is a field of type "Sig".
     * <p>
     * <p>
     * "Sig" means this is a text style object.
     * </p>
     *
     * @return Return true if this is a field of type "Sig".
     */
    public boolean isTypeSig() {
        return false;
    }

    /**
     * Return true if this is a text field.
     * <p>
     * <p>
     * "Tx" means this is a text style object.
     * </p>
     *
     * @return Return true if this is a field of type "Tx".
     */
    public boolean isTypeTx() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @seede.intarsys.pdf.pd.PDAcroFormNode#removeField(de.intarsys.pdf.pd.
     * PDAcroFormField)
     */
    @Override
    public boolean removeField(PDAcroFormField field) {
        boolean removed = cosRemoveKid(field.cosGetDict());
        getAcroForm().getSigFlags().setSignatureExists(getAcroForm().isSignatureExists());
        return removed;
    }

    /**
     * Reset this to its default value.
     */
    public void reset() {
        //
        COSObject value = cosGetDefaultValue();
        if (value != null) {
            value = value.copyOptional();
        }
        cosSetValue(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.pd.IAdditionalActionSupport#setActions(de.intarsys.pdf
     * .pd.PDAdditionalActions)
     */
    @Override
    public void setAdditionalActions(PDAdditionalActions actions) {
        setFieldObject(DK_AA, actions);
    }

    /**
     * Make variable text centered.
     */
    public void setAlignmentCenter() {
        setQuadding(ALIGNMENT_CENTER);
    }

    /**
     * Make variable text left aligned.
     */
    public void setAlignmentLeft() {
        setQuadding(ALIGNMENT_LEFT);
    }

    /**
     * Make variable text right aligned.
     */
    public void setAlignmentRight() {
        setQuadding(ALIGNMENT_RIGHT);
    }

    /**
     * Asssign a alternate field name.
     *
     * @param newAlternateFieldName The new alternate field name
     */
    public void setAlternateFieldName(String newAlternateFieldName) {
        setFieldString(DK_TU, newAlternateFieldName);
    }

    /**
     * Convenience method to access "Comb" flag.
     */
    public void setComb(boolean f) {
        getFieldFlags().setComb(f);
    }

    /**
     * Assign new default style
     *
     * @param newDefaultStyle The new default style.
     */
    protected void setDefaultStyle(String newDefaultStyle) {
        setFieldName(DK_DS, newDefaultStyle);
    }

    /**
     * Assign a new default value. The default value is used if no value (/V) is
     * available.
     *
     * @param value The new default value.
     */
    public void setDefaultValue(String value) {
        if (value == null) {
            cosSetDefaultValue(null);
        } else {
            cosSetDefaultValue(COSString.create(value));
        }
    }

    /**
     * Convenience method to access "DoNotScroll" flag.
     */
    public void setDoNotScroll(boolean f) {
        getFieldFlags().setDoNotScroll(f);
    }

    protected void setKids(List newKids) {
        setPDObjects(DK_Kids, newKids);
    }

    /**
     * Assign a new local name.
     *
     * @param newLocalName The new local name
     */
    public void setLocalName(String newLocalName) {
        setFieldString(DK_T, newLocalName);
    }

    /**
     * Set the maximum length for this field.
     *
     * @param newMaxLength The new maximum length
     */
    public void setMaxLength(Integer newMaxLength) {
        if (newMaxLength != null) {
            setFieldInt(DK_MaxLen, newMaxLength.intValue());
        } else {
            cosRemoveField(DK_MaxLen);
        }
    }

    /**
     * Convenience method to access "Multiline" flag.
     */
    public void setMultiline(boolean f) {
        getFieldFlags().setMultiline(f);
    }

    /**
     * Convenience method to access "NoExport" flag.
     */
    public void setNoExport(boolean f) {
        getFieldFlags().setNoExport(f);
    }

    protected void setParent(PDAcroFormField newParent) {
        setFieldObject(DK_Parent, newParent);
    }

    /**
     * Convenience method to access "ReadOnly" flag.
     */
    public void setReadOnly(boolean f) {
        getFieldFlags().setReadOnly(f);
    }

    /**
     * Set the value of this.
     *
     * @param value The new value of this, represented by a {@link String}.
     */
    public void setValueString(String value) {
        if (value == null) {
            cosSetValue(null);
        } else {
            cosSetValue(COSString.create(value));
        }
    }

    /**
     * Set the value of this to a {@link List} of {@link String} objects.
     *
     * @param values The new value.
     */
    public void setValueStrings(List values) {
        if ((values == null) || values.isEmpty()) {
            cosSetValue(null);
        } else {
            cosSetValue(COSString.create((String) values.get(0)));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#toString()
     */
    @Override
    public String toString() {
        String qName = getQualifiedName();
        if (qName == null) {
            return "unnamed AcroFormField " + super.toString(); //$NON-NLS-1$
        }
        return "AcroFormField " + qName + " " + super.toString(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * This is called from one of the field's annotation to set the field's
     * value to the appropriate state (along with keeping all other children in
     * synch).
     *
     * @param annotation
     */
    protected void triggerAnnotation(PDWidgetAnnotation annotation) {
        // this will make sense only for buttons...
    }
}
