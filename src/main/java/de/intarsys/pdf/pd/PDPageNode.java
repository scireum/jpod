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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.util.List;

/**
 * An abstract superclass for pages and page tree nodes
 */
public abstract class PDPageNode extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            COSName type = ((COSDictionary) object).get(DK_Type).asName();
            if (CN_Type_Pages.equals(type)) {
                return PDPageTree.META;
            }
            return PDPage.META;
        }

        @Override
        public Class getRootClass() {
            return PDPageNode.class;
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName DK_Parent = COSName.constant("Parent");

    public static final COSName CN_Type_Pages = COSName.constant("Pages");

    public static final COSName CN_Type_Page = COSName.constant("Page");

    public static final COSName DK_Rotate = COSName.constant("Rotate"); //$NON-NLS-1$

    /**
     * the parent node of this, required except in root node
     */
    private PDPageTree cachedParent;

    private CDSRectangle cachedCropBox;

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDPageNode(COSObject object) {
        super(object);
    }

    abstract protected void collectAnnotations(List annotations);

    /**
     * @deprecated
     */
    @Deprecated
    public void dispose() {
        PDPageTree parent = getParent();
        if (parent != null) {
            parent.removeNode(this);
        }
    }

    /**
     * The total number of pages represented by this node.
     *
     * @return The total number of pages represented by this node.
     */
    public abstract int getCount();

    /**
     * The rectangle in user space coordinates defining the visible region of
     * the page. User space is measured in 1/72 inch initially
     *
     * @return The rectangle in user space coordinates defining the visible
     * region of the page
     */
    public CDSRectangle getCropBox() {
        if (cachedCropBox == null) {
            COSArray array = cosGetFieldInheritable(PDPage.DK_CropBox).asArray();
            if (array == null) {
                cachedCropBox = getMediaBox(); // default for CropBox
            } else {
                cachedCropBox = CDSRectangle.createFromCOS(array);
            }
        }
        return cachedCropBox;
    }

    /**
     * The first {@link PDAnnotation} linked on this page.
     *
     * @return The first {@link PDAnnotation} linked on this page.
     */
    public PDAnnotation getFirstAnnotation() {
        PDPage currentPage = getFirstPage();
        while (currentPage != null) {
            PDAnnotation annotation = currentPage.getFirstAnnotation();
            if (annotation != null) {
                return annotation;
            }
            currentPage = currentPage.getNextPage();
        }
        return null;
    }

    /**
     * Get the first node within the receiver or the receiver if it is not a
     * collection (page tree).
     * <p>
     * <p>
     * This may return null if the receiver is an empty collection.
     * </p>
     *
     * @return Get the first node within the receiver or the receiver if it is
     * not a collection (page tree).
     */
    public abstract PDPageNode getFirstNode();

    /**
     * Get the first page (leaf node) within the receiver hierarchy.
     *
     * @return Get the first page (leaf node) within the receiver hierarchy.
     */
    public abstract PDPage getFirstPage();

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#getGenericParent()
     */
    @Override
    public PDObject getGenericParent() {
        return getParent();
    }

    /**
     * The last {@link PDAnnotation} linked on this page.
     *
     * @return The last {@link PDAnnotation} linked on this page.
     */
    public PDAnnotation getLastAnnotation() {
        PDPage currentPage = getLastPage();
        while (currentPage != null) {
            PDAnnotation annotation = currentPage.getLastAnnotation();
            if (annotation != null) {
                return annotation;
            }
            currentPage = currentPage.getPreviousPage();
        }
        return null;
    }

    /**
     * Get the last node within the receiver or the receiver if it is not a
     * collection (page tree).
     * <p>
     * <p>
     * This may return null if the receiver is an empty collection.
     * </p>
     *
     * @return Get the last node within the receiver or the receiver if it is
     * not a collection (page tree).
     */
    public abstract PDPageNode getLastNode();

    /**
     * Get the last page (leaf node) within the receiver hierarchy.
     *
     * @return Get the last page (leaf node) within the receiver hierarchy.
     */
    public abstract PDPage getLastPage();

    /**
     * The rectangle in user space coordinates defining the physical page
     * boundaries. user space is measured in 1/72 inch initially
     *
     * @return The rectangle in user space coordinates defining the physical
     * page boundaries
     */
    public CDSRectangle getMediaBox() {
        COSArray array = cosGetFieldInheritable(PDPage.DK_MediaBox).asArray();
        if (array == null) {
            // tood 1 @lazy
            // todo 3 this can't happen: MediaBox is a required attribute
            setMediaBox(new CDSRectangle(CDSRectangle.SIZE_A4));
            array = (COSArray) cosGetField(PDPage.DK_MediaBox);
        }
        return CDSRectangle.createFromCOS(array);
    }

    /**
     * Get the next node after the receiver.
     *
     * @return Get the next node after the receiver.
     */
    public PDPageNode getNextNode() {
        PDPageTree tree = getParent();
        if (tree == null) {
            return null;
        }
        return tree.getNextNode(this);
    }

    /**
     * The zero based index of <code>this</code> within the document.
     *
     * @return The zero based index of <code>this</code> within the document.
     */
    public int getNodeIndex() {
        PDPageTree myParent = getParent();
        if (myParent == null) {
            return 0;
        }
        return myParent.getNodeIndex(this);
    }

    /**
     * The page at <code>index</code> within the receivers subtree.
     *
     * @param index The page index
     * @return The page at <code>index</code> within the receivers subtree.
     */
    public PDPage getPageAt(int index) {
        if (index >= getCount()) {
            PDPageNode next = getNextNode();
            if (next == null) {
                return null;
            }
            return next.getPageAt(index - getCount());
        }
        if (index < 0) {
            PDPageNode previous = getPreviousNode();
            if (previous == null) {
                return null;
            }
            return previous.getPageAt(previous.getCount() + index);
        }
        PDPageNode first = getFirstNode();
        if (first == null) {
            return null;
        }
        return first.getPageAt(index);
    }

    /**
     * The parent node if available. The root tree node of the document has no
     * parent.
     *
     * @return The parent node if available.
     */
    public PDPageTree getParent() {
        if (cachedParent == null) {
            COSDictionary parentDict = cosGetField(DK_Parent).asDictionary();
            if (parentDict != null) {
                cachedParent = (PDPageTree) PDPageNode.META.createFromCos(parentDict);
            }
        }
        return cachedParent;
    }

    /**
     * The previous node .
     *
     * @return The previous node.
     */
    public PDPageNode getPreviousNode() {
        PDPageTree tree = getParent();
        if (tree == null) {
            return null;
        }
        return tree.getPreviousNode(this);
    }

    /**
     * The number of degrees by which the page should be rotated clockwise when
     * displayed or printed. The value must be a multiple of 90. Default value:
     * 0.
     * <p>
     * <p>
     * <b>Notice: the value is inheritable</b>
     * </p>
     *
     * @return Rotation as a multiple of 90
     */
    public int getRotate() {
        COSInteger rotate = cosGetFieldInheritable(DK_Rotate).asInteger();
        if (rotate == null) {
            return 0;
        }
        return rotate.intValue();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedParent = null;
        cachedCropBox = null;
    }

    /**
     * Answer <code>true</code> if this is a single page node.
     *
     * @return Answer <code>true</code> if this is a single page node.
     */
    public abstract boolean isPage();

    /**
     * <code>true</code> if this page node object is a valid participant in
     * the documents page tree.
     *
     * @return <code>true</code> if this page node object is a valid
     * participant in the documents page tree.
     */
    public abstract boolean isValid();

    /**
     * Set the rectangle in user space coordinates defining the visible region
     * of the page. user space is measured in 1/72 inch initially
     *
     * @param rect The rectangle defining the visible page region
     */
    public void setCropBox(CDSRectangle rect) {
        setFieldObject(PDPage.DK_CropBox, rect);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#setGenericParent(de.intarsys.pdf.pd.PDObject)
     */
    @Override
    public void setGenericParent(PDObject newParent) {
        if (!(newParent instanceof PDPageTree)) {
            throw new IllegalArgumentException("parent must be a PDPAgeTree");
        }
        setParent((PDPageTree) newParent);
    }

    /**
     * Set the rectangle in user space coordinates defining the physical page
     * boundaries. user space is measured in 1/72 inch initially
     *
     * @param rect The rectangle defining the physical page boundaries
     */
    public void setMediaBox(CDSRectangle rect) {
        setFieldObject(PDPage.DK_MediaBox, rect);
    }

    /**
     * Sets the parent.
     *
     * @param parent The parent to set
     */
    protected void setParent(PDPageTree parent) {
        setFieldObject(DK_Parent, parent);
    }

    /**
     * The number of degrees by which the page should be rotated clockwise when
     * displayed or printed. The value must be a multiple of 90. If a value of 0
     * is set, which is the default, the field will be cleared.
     * <p>
     * <p>
     * <b>Notice: This object and its children are affected on a change!</b>
     * </p>
     *
     * @param rotate A multiple of 90, the value is <b>not</b> checked for a legal
     *               value
     */
    public void setRotate(int rotate) {
        COSObject newObject = COSInteger.create(rotate);
        COSObject inheritedObject = cosGetFieldInherited(DK_Rotate);
        if (newObject.equals(inheritedObject)) {
            // same as parent - remove
            cosSetFieldInheritable(DK_Rotate, null);
        } else if (inheritedObject.isNull() && rotate == 0) {
            // default
            cosSetFieldInheritable(DK_Rotate, null);
        } else {
            cosSetFieldInheritable(DK_Rotate, newObject);
        }
    }
}
