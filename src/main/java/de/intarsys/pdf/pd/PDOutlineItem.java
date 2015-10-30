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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * A document outline item. This is used to represent all tree elements in a PDF
 * outline tree.
 */
public class PDOutlineItem extends PDOutlineNode {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDOutlineNode.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDOutlineItem(object);
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            // there are documents around that do not correctly contain
            // a /Parent reference - so we can't really determine type but from
            // client context
            if (object instanceof COSDictionary) {
                return PDOutlineItem.META;
            }
            return super.doDetermineClass(object);
        }
    }

    public static final COSName DK_A = COSName.constant("A"); //$NON-NLS-1$

    public static final COSName DK_C = COSName.constant("C"); //$NON-NLS-1$

    public static final COSName DK_Dest = COSName.constant("Dest"); //$NON-NLS-1$

    public static final COSName DK_F = COSName.constant("F"); //$NON-NLS-1$

    public static final COSName DK_Next = COSName.constant("Next"); //$NON-NLS-1$

    public static final COSName DK_Parent = COSName.constant("Parent"); //$NON-NLS-1$

    public static final COSName DK_Prev = COSName.constant("Prev"); //$NON-NLS-1$

    public static final COSName DK_SE = COSName.constant("SE"); //$NON-NLS-1$

    public static final COSName DK_Title = COSName.constant("Title"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    protected PDOutlineItem(COSObject object) {
        super(object);
    }

    /**
     * The flags as integer value.
     *
     * @return The flags as integer value.
     */
    public int basicGetFlags() {
        return getFieldInt(DK_F, 0);
    }

    /**
     * Assign the flags from an integer value.
     *
     * @param value The new flags
     */
    public void basicSetFlags(int value) {
        setFieldInt(DK_F, value);
    }

    /**
     * The color values to be used for this.
     *
     * @return The color values to be used for this.
     */
    public COSArray cosGetColor() {
        return cosGetField(DK_C).asArray();
    }

    /**
     * The {@link PDAction} executed when the outline item is activated.
     *
     * @return The {@link PDAction} executed when the outline item is activated.
     */
    public PDAction getAction() {
        return (PDAction) PDAction.META.createFromCos(cosGetField(DK_A));
    }

    /**
     * The {@link PDDestination} to be displayed when the outline item is
     * activated.
     *
     * @return The {@link PDDestination} to be displayed when the outline item
     * is activated.
     */
    public PDDestination getDestination() {
        return (PDDestination) PDDestination.META.createFromCos(cosGetField(DK_Dest));
    }

    /**
     * The {@link OutlineItemFlags}.
     *
     * @return The {@link OutlineItemFlags}.
     */
    public OutlineItemFlags getFlags() {
        return new OutlineItemFlags(this);
    }

    /**
     * The next {@link PDOutlineItem} within the items linked list.
     *
     * @return The next {@link PDOutlineItem} within the items linked list.
     */
    public PDOutlineItem getNext() {
        return (PDOutlineItem) PDOutlineItem.META.createFromCos(cosGetField(DK_Next));
    }

    /**
     * The parent item.
     *
     * @return The parent item.
     */
    @Override
    public PDOutlineNode getParent() {
        return (PDOutlineNode) PDOutlineNode.META.createFromCos(cosGetField(DK_Parent));
    }

    /**
     * The previous {@link PDOutlineItem} within the items linked list.
     *
     * @return The previous {@link PDOutlineItem} within the items linked list.
     */
    public PDOutlineItem getPrev() {
        return (PDOutlineItem) PDOutlineItem.META.createFromCos(cosGetField(DK_Prev));
    }

    /**
     * The title to be displayed for this.
     *
     * @return The title to be displayed for this.
     */
    public String getTitle() {
        return getFieldString(DK_Title, "");
    }

    /**
     * Assign the {@link PDAction} to be executed when the outline is activated.
     *
     * @param action The {@link PDAction} to be executed.
     */
    public void setAction(PDAction action) {
        setFieldObject(DK_A, action);
    }

    /**
     * Assign the {@link PDDestination} to be displayed when the outline is
     * activated.
     *
     * @param destination The {@link PDDestination} to be displayed.
     */
    public void setDestination(PDDestination destination) {
        setFieldObject(DK_Dest, destination);
    }

    protected void setNext(PDOutlineItem next) {
        setFieldObject(DK_Next, next);
    }

    protected void setParent(PDOutlineNode parent) {
        setFieldObject(DK_Parent, parent);
    }

    protected void setPrev(PDOutlineItem prev) {
        setFieldObject(DK_Prev, prev);
    }

    /**
     * Assign the outline title
     *
     * @param title The new outline title.
     */
    public void setTitle(String title) {
        setFieldString(DK_Title, title);
    }
}
