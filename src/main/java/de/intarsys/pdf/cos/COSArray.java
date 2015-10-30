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
package de.intarsys.pdf.cos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A COSArray represents an indexed collection of {@link COSDocumentElement}
 * instances.
 * <p>
 * Using the standard access methods, always "dereferenced" {@link COSObject}
 * instances are returned. Use the "basic" falvor of methods to access the
 * optional {@link COSIndirectObject}.
 */
public class COSArray extends COSCompositeObject {
    /**
     * Create an empty {@link COSArray}.
     *
     * @return Create an empty {@link COSArray}.
     */
    public static COSArray create() {
        return new COSArray();
    }

    /**
     * Create an empty {@link COSArray} with a preallocated size.
     *
     * @return Create an empty {@link COSArray}.
     */
    public static COSArray create(int size) {
        return new COSArray(size);
    }

    /**
     * Shortcut for fast creation of rectangle arrays
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @return a new COSArray
     */
    public static COSArray createWith(float a, float b, float c, float d) {
        COSArray result = new COSArray(4);
        result.basicAddSilent(COSFixed.create(a));
        result.basicAddSilent(COSFixed.create(b));
        result.basicAddSilent(COSFixed.create(c));
        result.basicAddSilent(COSFixed.create(d));
        return result;
    }

    /**
     * Shortcut for fast creation of matrix arrays
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @return a new COSArray
     */
    public static COSArray createWith(float a, float b, float c, float d, float e, float f) {
        COSArray result = new COSArray(6);
        result.basicAddSilent(COSFixed.create(a));
        result.basicAddSilent(COSFixed.create(b));
        result.basicAddSilent(COSFixed.create(c));
        result.basicAddSilent(COSFixed.create(d));
        result.basicAddSilent(COSFixed.create(e));
        result.basicAddSilent(COSFixed.create(f));
        return result;
    }

    /**
     * the list of document elements contained
     */
    private final List objects;

    protected COSArray() {
        super();
        this.objects = new ArrayList();
    }

    protected COSArray(int size) {
        super();
        this.objects = new ArrayList(size);
    }

    protected COSArray(List objects) {
        super();
        this.objects = objects;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor)
     */
    @Override
    public java.lang.Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        return visitor.visitFromArray(this);
    }

    /**
     * Add a {@link COSObject} to the collection.
     * <p>
     * <p>
     * This method takes care of change propagation for incremental writing.
     * </p>
     * <p>
     * <p>
     * this method should be used by the application level programmer to ensure
     * he deals not with references.
     * </p>
     *
     * @param object the object to be added
     * @return this
     */
    public COSArray add(COSObject object) {
        willChange(this);
        basicAddPropagate(object);
        if (objectListeners != null) {
            triggerChanged(objects.size(), COSNull.NULL, object);
        }
        return this;
    }

    /**
     * Add a {@link COSObject} to the collection.
     * <p>
     * <p>
     * This method takes care of change propagation for incremental writing.
     * </p>
     * <p>
     * <p>
     * this method should be used by the application level programmer to ensure
     * he deals not with references.
     * </p>
     *
     * @param index  The index where to insert {@code object}
     * @param object the object to be added
     * @return this
     */
    public COSArray add(int index, COSObject object) {
        willChange(this);
        basicAddPropagate(index, object);
        if (objectListeners != null) {
            triggerChanged(index, COSNull.NULL, object);
        }
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#getCOSArray()
     */
    @Override
    public COSArray asArray() {
        return this;
    }

    /**
     * Add a document element (an object or a reference) to the collection.
     * <p>
     * <p>
     * This method should only be used for low level programming (parser).
     * </p>
     *
     * @param element the element to be added
     * @return The receiver.
     */
    protected COSArray basicAddPropagate(COSDocumentElement element) {
        COSObject dereferenced = element.dereference();
        COSDocumentElement containable = element.containable();
        willChange(dereferenced);
        ICOSContainer newContainer = containable.addContainer(this);
        objects.add(containable);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        return this;
    }

    /**
     * add a document element (an object or a reference) to the collection.
     * <p>
     * <p>
     * this method should only be used for low level programming (parser).
     * </p>
     *
     * @param index   The index where to insert {@code object}
     * @param element the element to be added
     * @return this
     */
    protected COSArray basicAddPropagate(int index, COSDocumentElement element) {
        COSObject dereferenced = element.dereference();
        COSDocumentElement containable = element.containable();
        willChange(dereferenced);
        ICOSContainer newContainer = containable.addContainer(this);
        objects.add(index, containable);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        return this;
    }

    /**
     * Add a document element (an object or a reference) to the collection.
     * <p>
     * <p>
     * The change is not propagated.
     * </p>
     * This should not be used by the application level programmer. It is public
     * for package visibility reasons.
     *
     * @param element the element to be added
     * @return The receiver.
     */
    public COSArray basicAddSilent(COSDocumentElement element) {
        COSDocumentElement containable = element.containable();
        containable.addContainer(this);
        objects.add(containable);
        return this;
    }

    /**
     * Remove all elements from the receiver.
     */
    protected void basicClearPropagate() {
        List oldObjects = new ArrayList(objects);
        objects.clear();
        for (Iterator i = oldObjects.iterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            COSObject dereferenced = element.dereference();
            willChange(dereferenced);
            ICOSContainer newContainer = element.removeContainer(this);
            dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        }
    }

    /**
     * Get the {@link COSDocumentElement} (an object or a reference) from this
     * at the specified index.
     * <p>
     * This method should only be used for low level programming.
     *
     * @param index The index into this
     * @return Get the {@link COSDocumentElement} (an object or a reference)
     * from this at the specified index.
     */
    public COSDocumentElement basicGet(int index) {
        return (COSDocumentElement) objects.get(index);
    }

    /**
     * The index within this where {@code element} can be found or -1.
     *
     * @param element The element to be searched within this.
     * @return The index of {@code element} or -1 if nothing found.
     */
    protected int basicIndexOf(COSDocumentElement element) {
        COSDocumentElement containable = element.containable();
        int i = 0;
        for (Iterator it = basicIterator(); it.hasNext(); ) {
            COSDocumentElement current = (COSDocumentElement) it.next();
            if (containable == current) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * An iterator that returns all contained {@link COSDocumentElement}
     * instances without dereferencing.
     * <p>
     * <p>
     * This should be used in low level programming.
     * </p>
     *
     * @return An iterator that returns all contained {@link COSDocumentElement}
     * instances without dereferencing.
     */
    @Override
    public Iterator<COSDocumentElement> basicIterator() {
        return objects.iterator();
    }

    /**
     * Remove {@code otherElement} from this.
     *
     * @param otherElement The element to be removed.
     * @return {@code true} if element was removed.
     */
    protected boolean basicRemovePropagate(COSDocumentElement otherElement) {
        COSObject dereferenced = otherElement.dereference();
        COSDocumentElement containable = otherElement.containable();
        for (Iterator i = basicIterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            if (containable == element) {
                i.remove();
                willChange(dereferenced);
                ICOSContainer newContainer = containable.removeContainer(this);
                dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
                return true;
            }
        }
        return false;
    }

    /**
     * Remove element at {@code index}.
     *
     * @param index The index within this to be removed.
     * @return The {@link COSDocumentElement} that was removed at the specified
     * index.
     */
    protected COSDocumentElement basicRemovePropagate(int index) {
        COSDocumentElement removed = (COSDocumentElement) objects.remove(index);
        COSObject dereferenced = removed.dereference();
        willChange(dereferenced);
        ICOSContainer newContainer = removed.removeContainer(this);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        return removed;
    }

    /**
     * replace the object at index <{@code i} with {@code element}.
     *
     * @param i       the index
     * @param element the object to put at the specified index
     * @return The previously contained object
     */
    protected COSDocumentElement basicSetPropagate(int i, COSDocumentElement element) {
        COSObject dereferenced = element.dereference();
        COSDocumentElement containable = element.containable();
        //
        willChange(dereferenced);
        ICOSContainer newContainer = containable.addContainer(this);
        COSDocumentElement oldContainable = (COSDocumentElement) objects.set(i, containable);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        //
        if (oldContainable != containable) {
            COSObject oldDereferenced = oldContainable.dereference();
            willChange(oldDereferenced);
            newContainer = oldContainable.removeContainer(this);
            oldDereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        }
        return oldContainable;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        return objects.toString();
    }

    /**
     * Remove all elements from this.
     */
    public void clear() {
        willChange(this);
        basicClearPropagate();
        if (objectListeners != null) {
            triggerChanged(-1, null, null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyBasic(de.intarsys.pdf.cos.COSDocument)
     */
    @Override
    protected COSObject copyBasic() {
        return create(size());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyDeep(java.util.Map)
     */
    @Override
    public COSObject copyDeep(Map copied) {
        COSArray result = (COSArray) super.copyDeep(copied);
        for (Iterator i = basicIterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            COSObject copy = element.copyDeep(copied);
            result.basicAddSilent(copy);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyShallow()
     */
    @Override
    public COSObject copyShallow() {
        COSArray result = (COSArray) super.copyShallow();
        for (Iterator i = basicIterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            result.basicAddSilent(element.copyShallowNested());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyNet(java.util.Map)
     */
    @Override
    protected COSObject copySubGraph(Map copied) {

        // for (Iterator i = basicIterator(); i.hasNext();) {
        // COSDocumentElement element = (COSDocumentElement) i.next();
        // COSObject copy = null;
        // if (element.isReference()) {
        // copy = (COSObject) copied.get(element);
        // if (copy == null) {
        // if (value.hasNavigationPathTo(this)) {
        // copy = value.copySubGraph(copied);
        // } else {
        // copy = value;
        // copied.put(value.getIndirectObject(), copy);
        // }
        // }
        // } else {
        // copy = value.copySubGraph(copied);
        // }
        // result.basicAdd(copy);
        // }
        return super.copySubGraph(copied);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean equals(Object o, PairRegister visited) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        COSArray other = (COSArray) o;
        if (visited.check(this, other)) {
            // We've already seen this pair.
            return true;
        }

        if (this.size() != other.size()) {
            return false;
        }

        Iterator<COSDocumentElement> iThis = this.basicIterator();
        Iterator<COSDocumentElement> iOther = other.basicIterator();
        while (iThis.hasNext()) {
            COSObject oThis = iThis.next().dereference();
            COSObject oOther = iOther.next().dereference();
            if (!oThis.equals(oOther, visited)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The {@link COSObject} at the given index. Any index outisde the valid
     * array range results in COSNull (compare Adobe Core ApI Reference).
     *
     * @param index The index of the {@link COSObject} to select from this.
     * @return The {@link COSObject} at the given index or {@link COSNull}.
     */
    public COSObject get(int index) {
        try {
            return ((COSDocumentElement) objects.get(index)).dereference();
        } catch (IndexOutOfBoundsException ignored) {
            return COSNull.NULL;
        }
    }

    /**
     * A copy of all COSObject's in this.
     * <p>
     * <p>
     * Indirect objects and dangling references are handled by this method.
     * </p>
     *
     * @return A copy of all COSObject's in this.
     */
    public List getObjects() {
        int size = size();
        List result = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            result.add(get(i));
        }
        return result;
    }

    /**
     * ATTENTION: this implementation returns a hash code that does not remain
     * constant when manipulating the arrays content
     *
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 17;
        for (Iterator iThis = this.basicIterator(); iThis.hasNext(); ) {
            COSDocumentElement oThis = (COSDocumentElement) iThis.next();
            if (oThis.isReference() || ((COSObject) oThis).isPrimitive()) {
                result = (result + oThis.hashCode()) * 34;
            } else {
                result = (result + 17) * 34;
            }
        }
        return result;
    }

    /**
     * The index of {@code object} within this or -1 if not found.
     *
     * @param object The object to be searched within this.
     * @return The index of {@code object} within this or -1 if not
     * found.
     */
    public int indexOf(COSObject object) {
        return basicIndexOf(object);
    }

    /**
     * {@code true} if {@code this.size() == 0}.
     *
     * @return {@code true} if {@code this.size() == 0}.
     */
    public boolean isEmpty() {
        return objects.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#iterator()
     */
    @Override
    public Iterator<COSObject> iterator() {
        return new Iterator<COSObject>() {
            private int index = 0;

            private int size = size();

            @Override
            public boolean hasNext() {
                return index < size;
            }

            @Override
            public COSObject next() {
                return get(index++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSCompositeObject#referenceIndirect(de.intarsys.pdf.cos.COSObject)
     */
    @Override
    public COSIndirectObject referenceIndirect(COSObject object) {
        COSIndirectObject ref = super.referenceIndirect(object);
        int index = getObjects().indexOf(object);
        if (index >= 0) {
            objects.set(index, ref);
        }
        return ref;
    }

    /**
     * Remove {@code object} from this. If {@code object} is not
     * contained, nothing happens.
     * <p>
     * <p>
     * This method cycles all elements which may cause heavy lazy loading.
     * </p>
     *
     * @param object The object to remove from this.
     * @return {@code true} if {@code object} was removed.
     */
    public boolean remove(COSObject object) {
        willChange(this);
        boolean result = basicRemovePropagate(object);
        if ((objectListeners != null) && result) {
            triggerChanged(-1, object, COSNull.NULL);
        }
        return result;
    }

    /**
     * Remove the object at {@code index} from the collection.
     *
     * @param index The index of the object to remove from the collection.
     * @return The object previously stored at the index.
     */
    public COSObject remove(int index) {
        willChange(this);
        COSDocumentElement element = basicRemovePropagate(index);
        COSObject object = element.dereference();
        if (objectListeners != null) {
            triggerChanged(index, object, COSNull.NULL);
        }
        return object;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#restoreState(java.lang.Object)
     */
    @Override
    public void restoreState(Object object) {
        super.restoreState(object);
        objects.clear();
        objects.addAll(((COSArray) object).objects);
        if (objectListeners != null) {
            triggerChanged(-1, null, null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    @Override
    public Object saveState() {
        COSArray result = new COSArray(new ArrayList(this.objects));
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * Replace the object at index {@code i} with {@code object}.
     *
     * @param i      The index
     * @param object The object to put at the specified index
     * @return The previuosly referenced object
     */
    public COSObject set(int i, COSObject object) {
        willChange(this);
        COSDocumentElement element = basicSetPropagate(i, object);
        COSObject oldObject = element.dereference();
        if (objectListeners != null) {
            triggerChanged(i, oldObject, object);
        }
        return oldObject;
    }

    /**
     * The number of elements in this.
     *
     * @return The number of elements in this.
     */
    public int size() {
        return objects.size();
    }

    public COSDocumentElement[] toArray() {
        return (COSDocumentElement[]) objects.toArray(new COSDocumentElement[objects.size()]);
    }

    protected void triggerChanged(int slot, COSObject oldValue, COSObject newValue) {
        if (objectListeners == null) {
            return;
        }
        Integer slotObject = Integer.valueOf(slot);
        for (Iterator it = objectListeners.iterator(); it.hasNext(); ) {
            ICOSObjectListener listener = (ICOSObjectListener) it.next();
            listener.changed(this, slotObject, oldValue, newValue);
        }
    }
}
