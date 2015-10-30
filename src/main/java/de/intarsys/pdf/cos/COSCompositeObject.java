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

import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A superclass implementation for all containers of other {@link COSObject}
 * instances.
 */
abstract public class COSCompositeObject extends COSObject implements ICOSContainer, IAttributeSupport {
    /**
     * Generic attribute support
     */
    protected AttributeMap attributes;

    /**
     * Collection of listeners to object changes
     */
    protected List objectListeners;

    protected COSCompositeObject() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @seede.intarsys.pdf.cos.COSObject#addObjectListener(de.intarsys.pdf.cos.
     * ICOSObjectListener)
     */
    @Override
    public void addObjectListener(ICOSObjectListener listener) {
        List newListeners;
        if (objectListeners == null) {
            newListeners = new ArrayList();
        } else {
            newListeners = new ArrayList(objectListeners);
        }
        newListeners.add(listener);
        objectListeners = newListeners;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSContainer#associate(de.intarsys.pdf.cos.ICOSContainer
     * , de.intarsys.pdf.cos.COSObject)
     */
    public ICOSContainer associate(ICOSContainer newContainer, COSObject object) {
        if (newContainer == this) {
            // error ?
            return this;
        }

        // sorry, this is an error
        throw new IllegalStateException("object may only be contained once (use indirect object)"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSContainer#containable(de.intarsys.pdf.cos.COSObject
     * )
     */
    public COSDocumentElement containable(COSObject object) {
        return object;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyDeep()
     */
    @Override
    public final COSObject copyDeep() {
        return copyDeep(new HashMap());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSDocumentElement#copyDeep(java.util.Map)
     */
    @Override
    public COSObject copyDeep(Map copied) {
        COSObject result = copyBasic();
        if (isIndirect()) {
            result.beIndirect();
            copied.put(getIndirectObject(), result);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @seede.intarsys.pdf.cos.ICOSContainer#disassociate(de.intarsys.pdf.cos.
     * ICOSContainer, de.intarsys.pdf.cos.COSObject)
     */
    public ICOSContainer disassociate(ICOSContainer oldContainer, COSObject object) {
        if (oldContainer == this) {
            // object removed from container
            object.basicSetContainer(COSObject.NULL_CONTAINER);
            return COSObject.NULL_CONTAINER;
        }

        // sorry, this is an error
        throw new IllegalStateException("association inconsistent"); //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#getAttribute(java.lang.
     * Object)
     */
    final synchronized public Object getAttribute(Object key) {
        if (attributes == null) {
            return null;
        }
        return attributes.getAttribute(key);
    }

    /**
     * Propagate up the containment hierarchy
     *
     * @see de.intarsys.pdf.cos.ICOSContainer#harden(de.intarsys.pdf.cos.COSObject)
     */
    public void harden(COSObject object) {
        container.harden(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#isObjectListenerAvailable()
     */
    @Override
    public boolean isObjectListenerAvailable() {
        if (objectListeners == null) {
            return false;
        } else {
            return !objectListeners.isEmpty();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#isPrimitive()
     */
    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean mayBeSwapped() {
        return true;
    }

    /*
     * reference count from non indirect references is always 1
     *
     * @see de.intarsys.pdf.cos.ICOSContainer#referenceCount()
     */
    public int referenceCount() {
        return 1;
    }

    /**
     * Change the reference to the object contained in this to an indirect one
     * via reference.
     * <p>
     * <p>
     * This method must be redefined by all containers to reflect the new
     * reference type in their child references.
     * </p>
     * <p>
     * <p>
     * This event is delegated to the document to create the correct state for a
     * new indirect object. If a document is not yet present, the state is
     * changed when the COSObject (s) are added to the document finally. This
     * can happen when constructing a COSObject graph "offline" and later add it
     * to the document.
     * </p>
     * <p>
     * <p>
     * From the COS invariants you can be sure that the object referenced by ref
     * is contained in this at most once
     * </p>
     *
     * @param object
     */
    public COSIndirectObject referenceIndirect(COSObject object) {
        return COSIndirectObject.create(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSContainer#register(de.intarsys.pdf.cos.COSObject)
     */
    public void register(COSDocumentElement object) {
        COSDocument doc = getDoc();
        if (doc != null) {
            object.registerWith(doc);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.COSObject#registerWith(de.intarsys.pdf.cos.COSDocument
     * )
     */
    @Override
    protected void registerWith(COSDocument doc) {
        // register descendents
        for (Iterator i = basicIterator(); i.hasNext(); ) {
            COSDocumentElement element = (COSDocumentElement) i.next();
            element.registerWith(doc);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#removeAttribute(java.lang
     * .Object)
     */
    final synchronized public Object removeAttribute(Object key) {
        if (attributes != null) {
            attributes.removeAttribute(key);
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.COSObject#removeObjectListener(de.intarsys.pdf.cos
     * .ICOSObjectListener)
     */
    @Override
    public void removeObjectListener(ICOSObjectListener listener) {
        if (objectListeners == null) {
            return;
        }
        List newListeners;
        newListeners = new ArrayList(objectListeners);
        newListeners.remove(listener);
        objectListeners = newListeners;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSContainer#restoreStateContainer(de.intarsys.pdf
     * .cos.ICOSContainer)
     */
    public ICOSContainer restoreStateContainer(ICOSContainer pContainer) {
        return pContainer;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSContainer#saveStateContainer()
     */
    public ICOSContainer saveStateContainer() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.attribute.IAttributeSupport#setAttribute(java.lang.
     * Object, java.lang.Object)
     */
    final synchronized public Object setAttribute(Object key, Object value) {
        if (attributes == null) {
            attributes = new AttributeMap(1);
        }
        return attributes.setAttribute(key, value);
    }

    /**
     * Propagate up the containment hierarchy
     *
     * @see de.intarsys.pdf.cos.ICOSContainer#soften(de.intarsys.pdf.cos.COSObject)
     */
    public void soften(COSObject object) {
        container.soften(object);
    }

    @Override
    protected void triggerChanged(Object slot, Object oldValue, Object newValue) {
        if (objectListeners == null) {
            return;
        }
        for (Iterator it = objectListeners.iterator(); it.hasNext(); ) {
            ICOSObjectListener listener = (ICOSObjectListener) it.next();
            listener.changed(this, slot, oldValue, newValue);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.ICOSContainer#willChange(de.intarsys.pdf.cos.COSObject
     * )
     */
    public void willChange(COSObject change) {
        container.willChange(change);
    }
}
