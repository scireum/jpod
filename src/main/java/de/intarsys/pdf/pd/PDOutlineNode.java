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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Factoring out the commonalities between the PDOutline and PDOutlineItem.
 */
public abstract class PDOutlineNode extends PDObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            if (object instanceof COSDictionary) {
                COSDictionary dict = (COSDictionary) object;
                COSName type = dict.get(PDObject.DK_Type).asName();
                if (PDOutline.CN_Type_Outlines.equals(type)) {
                    return PDOutline.META;
                }
                if (dict.get(PDOutlineItem.DK_Parent).isNull() && dict.get(PDOutlineItem.DK_Title).isNull()) {
                    return PDOutline.META;
                }
                return PDOutlineItem.META;
            }
            return super.doDetermineClass(object);
        }

        @Override
        public Class getRootClass() {
            return PDOutlineNode.class;
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName DK_First = COSName.constant("First");

    public static final COSName DK_Last = COSName.constant("Last");

    public static final COSName DK_Count = COSName.constant("Count");

    protected PDOutlineNode(COSObject object) {
        super(object);
    }

    /**
     * Add a new {@link PDOutlineItem}.
     *
     * @param newItem The new item to be inserted at the end.
     */
    public void addItem(PDOutlineItem newItem) {
        changeCount(1);
        PDOutlineItem first = getFirst();
        if (first == null) {
            setFirst(newItem);
        }
        PDOutlineItem last = getLast();
        if (last != null) {
            last.setNext(newItem);
            newItem.setPrev(last);
        }
        setLast(newItem);
        newItem.setParent(this);
    }

    protected void changeCount(int value) {
        int newCount = getCount();
        if (newCount < 0) {
            newCount -= value;
        } else {
            newCount += value;
        }
        setCount(newCount);
        if (getParent() != null) {
            getParent().changeCount(value);
        }
    }

    /**
     * Collapse this node (mark the children invisible).
     *
     * @return <code>true </code> if the expansion state of the node changes.
     */
    public boolean collapse() {
        if (getCount() <= 0) {
            return false;
        }
        setCount(-getCount());
        if (getParent() != null) {
            getParent().changeCount(getCount());
        }
        return true;
    }

    /**
     * Expand this node (mark the children visible).
     *
     * @return <code>true </code> if the expansion state of the node changes.
     */
    public boolean expand() {
        if (getCount() >= 0) {
            return false;
        }
        setCount(-getCount());
        if (getParent() != null) {
            getParent().changeCount(getCount());
        }
        return true;
    }

    /**
     * The list of all child nodes for this.
     *
     * @return The list of all child nodes for this.
     */
    public List<PDOutlineItem> getChildren() {
        List<PDOutlineItem> result = new ArrayList<>();
        PDOutlineItem current = getFirst();
        while (current != null) {
            result.add(current);
            current = current.getNext();
        }
        return result;
    }

    /**
     * The number of child elements.
     *
     * @return The number of child elements.
     */
    public int getCount() {
        return getFieldInt(DK_Count, 0);
    }

    /**
     * The first child element in the linked list of children.
     *
     * @return The first child element in the linked list of children.
     */
    public PDOutlineItem getFirst() {
        return (PDOutlineItem) PDOutlineItem.META.createFromCos(cosGetField(DK_First));
    }

    /**
     * The last child element in the linked list of children.
     *
     * @return The last child element in the linked list of children.
     */
    public PDOutlineItem getLast() {
        return (PDOutlineItem) PDOutlineItem.META.createFromCos(cosGetField(DK_Last));
    }

    protected PDOutlineNode getParent() {
        return null;
    }

    /**
     * <code>true</code> if this is the outline (root element) itself.
     *
     * @return <code>true</code> if this is the outline (root element) itself.
     */
    public boolean isOutline() {
        return false;
    }

    /**
     * Remove a {@link PDOutlineItem} from this.
     *
     * @param pItem The item to be removed.
     * @return <code>true</code> if the item was removed.
     */
    public boolean removeItem(PDOutlineItem pItem) {
        if (pItem.getParent() != this) {
            return false;
        }
        changeCount(-1);
        if (pItem.getPrev() != null) {
            pItem.getPrev().setNext(pItem.getNext());
        }
        if (pItem.getNext() != null) {
            pItem.getNext().setPrev(pItem.getPrev());
        }
        if (getFirst() == pItem) {
            setFirst(pItem.getNext());
        }
        if (getLast() == pItem) {
            setLast(pItem.getPrev());
        }
        pItem.setParent(null);
        return true;
    }

    protected void setCount(int newCount) {
        setFieldInt(DK_Count, newCount);
    }

    protected void setFirst(PDOutlineItem first) {
        setFieldObject(DK_First, first);
    }

    protected void setLast(PDOutlineItem last) {
        setFieldObject(DK_Last, last);
    }
}
