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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a collection of associations (Map).
 * <p>
 * <p>
 * The keys of the association are COSName objects, the value may be any
 * COSDocumentElement
 * </p>
 */
public class COSDictionary extends COSCompositeObject {
    public static class Entry implements Map.Entry {
        private COSName key;

        private COSDocumentElement value;

        public Entry(COSName key, COSDocumentElement value) {
            this.key = key;
            this.value = value;
        }

        public Object getKey() {
            return key;
        }

        public Object getValue() {
            return value.dereference();
        }

        public Object setValue(Object newValue) {
            return value = (COSDocumentElement) newValue;
        }
    }

    /**
     * Create an empty {@link COSDictionary}.
     *
     * @return Create an empty {@link COSDictionary}.
     */
    public static COSDictionary create() {
        return new COSDictionary();
    }

    /**
     * Create an empty {@link COSDictionary} with an initial capacity.
     *
     * @return Create an empty {@link COSDictionary} with an initial capacity.
     */
    public static COSDictionary create(int size) {
        return new COSDictionary(size);
    }

    /**
     * the map from a COSName to a COSDocumentElement
     */
    private final Map objects;

    protected COSDictionary() {
        super();
        this.objects = new HashMap();
    }

    protected COSDictionary(int size) {
        super();
        this.objects = new HashMap(size);
    }

    protected COSDictionary(Map objects) {
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
        return visitor.visitFromDictionary(this);
    }

    /**
     * Add all objects from <code>dict</code>. Associations already available
     * in this are replaced with new content .
     *
     * @param dict The collection of associations to add to this.
     */
    public void addAll(COSDictionary dict) {
        willChange(this);
        for (Iterator i = dict.basicEntryIterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSName key = (COSName) entry.getKey();
            COSDocumentElement element = (COSDocumentElement) entry.getValue();
            basicPutPropagate(key, element.copyShallowNested());
        }
        if (objectListeners != null) {
            triggerChanged(null, null, null);
        }
    }

    /**
     * Add all values from <code>dict</code> that are not yet defined in the
     * receiver.
     *
     * @param dict The dictionary with the associations to add.
     */
    public void addIfAbsent(COSDictionary dict) {
        boolean changed = false;
        for (Iterator i = dict.basicEntryIterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSName key = (COSName) entry.getKey();
            COSDocumentElement element = (COSDocumentElement) entry.getValue();
            if (!containsKey(key)) {
                if (!changed) {
                    changed = true;
                    willChange(this);
                }
                COSObject newObject = element.copyShallowNested().dereference();
                basicPutPropagate(key, newObject);
            }
        }
        if (changed && (objectListeners != null)) {
            triggerChanged(null, null, null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#getCOSDictionary()
     */
    @Override
    public COSDictionary asDictionary() {
        return this;
    }

    /**
     * Remove all associations from this.
     */
    protected void basicClearPropagate() {
        for (Iterator i = objects.values().iterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            COSObject dereferenced = element.dereference();
            willChange(dereferenced);
            ICOSContainer newContainer = element.removeContainer(this);
            dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        }
        objects.clear();
    }

    /**
     * An iterator over all entries. The value of the entries returned are the
     * not - dereferenced elements.
     *
     * @return An interator over all entries.
     */
    public Iterator basicEntryIterator() {
        return objects.entrySet().iterator();
    }

    /**
     * The {@link COSDocumentElement} associated with <code>key</code>.
     *
     * @param key The name to lookup
     * @return The {@link COSDocumentElement} associated with <code>key</code>.
     */
    public COSDocumentElement basicGet(COSName key) {
        return (COSDocumentElement) objects.get(key);
    }

    /**
     * An iterator over all values. The objects returned are not dereferenced.
     *
     * @return An iterator over all values.
     */
    @Override
    public Iterator basicIterator() {
        return objects.values().iterator();
    }

    /**
     * Add a {@link COSDocumentElement} to the collection.
     *
     * @param key     The key where to store the new element.
     * @param element The {@link COSDocumentElement} to store.
     * @return The {@link COSDocumentElement} associated with <code>key</code>
     * so far.
     */
    protected COSDocumentElement basicPutPropagate(COSName key, COSDocumentElement element) {
        COSObject dereferenced = element.dereference();
        COSDocumentElement containable = element.containable();
        //
        willChange(dereferenced);
        ICOSContainer newContainer = containable.addContainer(this);
        COSDocumentElement oldContainable = (COSDocumentElement) objects.put(key, containable);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        //
        if (oldContainable != null && oldContainable != containable) {
            COSObject oldDereferenced = oldContainable.dereference();
            willChange(oldDereferenced);
            newContainer = oldContainable.removeContainer(this);
            oldDereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        }
        return oldContainable;
    }

    /**
     * Add a document element to the collection.
     * <p>
     * The element is inserted without change propagation.
     * <p>
     * This should not be used by the application level programmer. It is public
     * for package visibility reasons.
     *
     * @param key     The key where to store the new element.
     * @param element The {@link COSDocumentElement} to store.
     * @return The {@link COSDocumentElement} associated with <code>key</code>
     * so far.
     */
    public COSDocumentElement basicPutSilent(COSName key, COSDocumentElement element) {
        COSDocumentElement containable = element.containable();
        containable.addContainer(this);
        COSDocumentElement oldContainable = (COSDocumentElement) objects.put(key, containable);
        if (oldContainable != null && oldContainable != containable) {
            oldContainable.removeContainer(this);
        }
        return oldContainable;
    }

    /**
     * Remove the element associated with <code>key</code> from the collection .
     *
     * @param key The key of the element to be removed
     * @return The {@link COSDocumentElement} removed or null.
     */
    protected COSDocumentElement basicRemovePropagate(COSName key) {
        COSDocumentElement element = (COSDocumentElement) objects.remove(key);
        if (element == null) {
            return null;
        }
        COSObject dereferenced = element.dereference();
        willChange(dereferenced);
        ICOSContainer newContainer = element.removeContainer(this);
        dereferenced.triggerChanged(COSObject.SLOT_CONTAINER, null, newContainer);
        return element;
    }

    /**
     * Remove the element associated with <code>key</code> from the collection .
     * <p>
     * The element is removed without change propagation.
     * <p>
     * This should not be used by the application level programmer. It is public
     * for package visibility reasons.
     *
     * @param key The key of the element to be removed
     * @return The {@link COSDocumentElement} removed or null.
     */
    public COSDocumentElement basicRemoveSilent(COSName key) {
        COSDocumentElement element = (COSDocumentElement) objects.remove(key);
        if (element == null) {
            return null;
        }
        element.removeContainer(this);
        return element;
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
     * Remove all associations from the receiver.
     */
    public void clear() {
        willChange(this);
        basicClearPropagate();
        if (objectListeners != null) {
            triggerChanged(null, null, null);
        }
    }

    /**
     * Answer true if <code>key</code> is a valid key in the collection .
     *
     * @param key The key whose existence is to be checked.
     * @return Answer true if <code>key</code> is a valid key in the
     * collection .
     */
    public boolean containsKey(COSName key) {
        return objects.containsKey(key);
    }

    /**
     * Answer <code>true</code> if <code>obj</code> is contained in the
     * collection
     *
     * @param obj The object to look up in the collection
     * @return Answer <code>true</code> if <code>obj</code> is contained in
     * the collection
     */
    public boolean containsValue(COSObject obj) {
        for (Iterator i = entryIterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSObject current = (COSObject) entry.getValue();
            if (current.equals(obj)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyBasic()
     */
    @Override
    protected COSObject copyBasic() {
        return create(size());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSCompositeObject#copyDeep(java.util.Map)
     */
    @Override
    public COSObject copyDeep(Map copied) {
        COSDictionary result = (COSDictionary) super.copyDeep(copied);
        for (Iterator i = objects.entrySet().iterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSName key = (COSName) entry.getKey();
            COSDocumentElement element = (COSDocumentElement) entry.getValue();
            COSObject copy = element.copyDeep(copied);
            result.basicPutSilent(key, copy);
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
        COSDictionary result = (COSDictionary) super.copyShallow();
        for (Iterator i = basicEntryIterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSName key = (COSName) entry.getKey();
            COSDocumentElement element = (COSDocumentElement) entry.getValue();
            result.basicPutSilent(key, element.copyShallowNested());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copySubGraph(java.util.Map)
     */
    @Override
    protected COSObject copySubGraph(Map copied) {
        COSDictionary result = (COSDictionary) super.copySubGraph(copied);

        // for (Iterator i = objects.entrySet().iterator(); i.hasNext();) {
        // Map.Entry entry = (Map.Entry) i.next();
        // COSName key = (COSName) entry.getKey();
        // COSDocumentElement element = (COSDocumentElement) entry.getValue();
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
        // result.basicPut(key, copy);
        // }
        return result;
    }

    /**
     * An iterator over all entries, returning a collection of {@link Entry}
     * instances. The {@link Entry} values are dereferenced.
     *
     * @return An iterator over all entries, returning a collection of
     * {@link Entry} instances.
     */
    public Iterator entryIterator() {
        return new Iterator() {
            private Iterator it = getObjects().entrySet().iterator();

            public boolean hasNext() {
                return it.hasNext();
            }

            public Object next() {
                Map.Entry entry = (Map.Entry) it.next();
                return new Entry((COSName) entry.getKey(), (COSDocumentElement) entry.getValue());
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        return this.equals(o, new PairRegister());
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

        COSDictionary other = (COSDictionary) o;
        if (visited.check(this, other)) {
            // We've already seen this pair.
            return true;
        }

        if (this.size() != other.size()) {
            return false;
        }

        for (COSName key : (Set<COSName>) this.keySet()) {
            COSObject oThis = this.get(key).dereference();
            COSObject oOther = other.get(key).dereference();
            if (!oThis.equals(oOther, visited)) {
                return false;
            }
        }

        return true;
    }

    /**
     * The {@link COSObject} associated with <code>key</code>.
     *
     * @param key The key to lookup
     * @return The {@link COSObject} associated with <code>key</code>.
     */
    public COSObject get(COSName key) {
        COSDocumentElement element = basicGet(key);
        return (element == null) ? COSNull.NULL : element.dereference();
    }

    protected Map getObjects() {
        return objects;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#iterator()
     */
    @Override
    public Iterator<COSObject> iterator() {
        return new Iterator<COSObject>() {
            private Iterator<COSDocumentElement> i = getObjects().values().iterator();

            @Override
            public boolean hasNext() {
                return i.hasNext();
            }

            @Override
            public COSObject next() {
                return i.next().dereference();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * The key of obj when it is contained in this or {@link COSNull}.
     *
     * @param obj The object to look up in the collection
     * @return The key of obj when it is contained in this or {@link COSNull}.
     */
    public COSObject keyOf(COSObject obj) {
        for (Iterator i = entryIterator(); i.hasNext(); ) {
            Map.Entry entry = (Map.Entry) i.next();
            COSObject current = (COSObject) entry.getValue();
            if (current.equals(obj)) {
                return (COSName) entry.getKey();
            }
        }
        return COSNull.NULL;
    }

    /**
     * The set of keys. Keys are {@link COSName} instances.
     *
     * @return The set of keys .
     */
    public Set keySet() {
        return objects.keySet();
    }

    /**
     * Add an association to the collection.
     *
     * @param key    The key where to store the object
     * @param object The object to store in the collection
     * @return The {@link COSObject} associated with <code>key</code> so far.
     */
    public COSObject put(COSName key, COSObject object) {
        if (object == null) {
            return remove(key);
        }
        willChange(this);
        COSDocumentElement oldElement = basicPutPropagate(key, object);
        COSObject oldObject = null;
        if (oldElement == null) {
            oldObject = COSNull.NULL;
        } else {
            oldObject = oldElement.dereference();
        }
        if (objectListeners != null) {
            triggerChanged(key, oldObject, object);
        }
        return oldObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#referenceIndirect(de.intarsys.pdf.cos.COSIndirectObject)
     */
    @Override
    public COSIndirectObject referenceIndirect(COSObject object) {
        COSIndirectObject ref = super.referenceIndirect(object);
        for (Iterator it = objects.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            if (entry.getValue() == object) {
                entry.setValue(ref);
                // there can be only one...
                break;
            }
        }
        return ref;
    }

    /**
     * Remove the element from the collection associated with <code>key</code>.
     *
     * @param key The key of the object to remove
     * @return The {@link COSObject} removed or null.
     */
    public COSObject remove(COSName key) {
        willChange(this);
        COSDocumentElement element = basicRemovePropagate(key);
        COSObject oldObject;
        if (element == null) {
            oldObject = COSNull.NULL;
        } else {
            oldObject = element.dereference();
            if (objectListeners != null) {
                triggerChanged(key, oldObject, COSNull.NULL);
            }
        }
        return oldObject;
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
        objects.putAll(((COSDictionary) object).objects);
        triggerChanged(null, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    public Object saveState() {
        COSObject result = new COSDictionary(new HashMap(this.objects));
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * The number of elements in this.
     *
     * @return The number of elements in this.
     */
    public int size() {
        return objects.size();
    }

    /**
     * A list of {@link COSObject} instances within this.
     *
     * @return A list of {@link COSObject} instances within this.
     */
    public List values() {
        List result = new ArrayList();
        for (Iterator it = iterator(); it.hasNext(); ) {
            result.add(it.next());
        }
        return result;
    }
}
