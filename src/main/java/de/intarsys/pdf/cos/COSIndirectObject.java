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

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.logging.Level;

import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.tools.logging.LogTools;
import de.intarsys.tools.resourcetracker.ResourceTracker;

/**
 * An object representing an indirect object within a COSDocument.
 * <p>
 * The indirect object provides transparent lazy access to the serialized
 * COSObject. Dereferencing this will deserialize the COSObject if not
 * available. On the other hand, this implementation must support the handling
 * of very large documents, so transparent garbage collection must be supported.
 * This is implemented using a two stage reference: A hard reference via
 * <code>object</code> is held to objects that should be hard wired in the
 * memory. A soft reference is held via <code>reference</code> to objects that
 * should be available to garbage collection. An object is "hard wired" when one
 * of the following conditions is true:<br>
 * <ul>
 * <li>An explicit call to "harden" is made.</li>
 * <li>The object or one of its descendants is changed.</li>
 * </ul>
 * <p>
 * You should not override equals or hash without really knowing what you do.
 * Part of the implementation depends on indirect objects being real unique.
 * </p>
 */
public class COSIndirectObject extends COSDocumentElement implements
		ICOSContainer {
	/**
	 * A tracker monitoring the disposal of COSObjects.
	 * <p>
	 * This is a debugging aid.
	 */
	private static ResourceTracker tracker = new ResourceTracker() {
		@Override
		protected void basicDispose(Object resource) {
			LogTools.getLogger(COSIndirectObject.class).log(Level.WARNING,
					"disposed " + resource);
		}
	};

	private static final byte F_DIRTY = 1;

	private static final byte F_FIXED = 2;

	/**
	 * Create an indirection for object.
	 * 
	 * @param object
	 *            The object that should be indirect.
	 * 
	 * @return The new indirect object.
	 */
	public static COSIndirectObject create(COSObject object) {
		COSIndirectObject ref = new COSIndirectObject();
		ref.setDirty(true);
		object.getContainer().register(ref);
		// set object after registration as it should need no recursive
		// registration
		// new dirty object, hard reference always
		ref.object = object;
		object.basicSetContainer(ref);
		return ref;
	}

	public static COSIndirectObject create(STDocument stDoc, COSObjectKey key) {
		COSIndirectObject ref = new COSIndirectObject();
		ref.setKey(key);
		ref.registerWith(stDoc);
		return ref;
	}

	public static COSIndirectObject create(STDocument stDoc, int objectNumber,
			int generationNumber) {
		COSIndirectObject ref = new COSIndirectObject();
		ref.setKey(objectNumber, generationNumber);
		ref.registerWith(stDoc);
		return ref;
	}

	private byte flags = 0;

	/**
	 * The referenced COS object. This may be a {@link Reference} object also,
	 * so this is typed to {@link Object}.
	 * 
	 */
	private Object object;

	/** The document hosting the indirect object. */
	private COSDocument doc;

	/**
	 * The storage level document for the indirect object
	 */
	private STDocument stDoc;

	private int objectNumber = -1;

	private short generationNumber = -1;

	/**
	 * number of known references.
	 */
	private short referenceCount = 0;

	protected COSIndirectObject() {
		super();
	}

	protected COSIndirectObject(COSIndirectObject indirectObject) {
		super();
		this.flags = indirectObject.flags;
		this.doc = indirectObject.doc;
		this.objectNumber = indirectObject.objectNumber;
		this.generationNumber = indirectObject.generationNumber;
		this.stDoc = indirectObject.stDoc;
		this.referenceCount = indirectObject.referenceCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.intarsys.pdf.cos.COSDocumentElement#accept(de.intarsys.pdf.cos.
	 * ICOSObjectVisitor)
	 */
	@Override
	public Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
		return visitor.visitFromIndirectObject(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.COSDocumentElement#addContainer(de.intarsys.pdf.cos
	 * .ICOSContainer)
	 */
	@Override
	protected ICOSContainer addContainer(ICOSContainer container) {
		return associate(container, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#associate(de.intarsys.pdf.cos.ICOSContainer
	 * , de.intarsys.pdf.cos.COSObject)
	 */
	public ICOSContainer associate(ICOSContainer newContainer, COSObject pObject) {
		newContainer.register(this);
		referenceCount++;
		return this;
	}

	/**
	 * Swap in the data for the indirect object.
	 * <p>
	 * ATTENTION: this method must return the newly read object on the stack to
	 * ensure a strong reference at least as long as the caller can access the
	 * result.
	 * 
	 * @return The newly read object.
	 */
	protected COSObject basicSwapIn() {
		try {
			return stGetDoc().load(this);
		} catch (IOException e) {
			throw new COSSwapException("io error reading object " + getKey(), e); //$NON-NLS-1$
		} catch (COSLoadException e) {
			throw new COSSwapException(
					"parse error reading object " + getKey(), e); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.COSDocumentElement#containable(de.intarsys.pdf.cos
	 * .COSDocumentElement)
	 */
	@Override
	public COSDocumentElement containable() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#containable(de.intarsys.pdf.cos.COSObject
	 * )
	 */
	public COSDocumentElement containable(COSObject pObject) {
		return this;
	}

	/**
	 * see copyDeep()
	 * 
	 * <p>
	 * This method keeps track of already copied objects to deal with cyclic
	 * references.
	 * </p>
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#copyDeep()
	 */
	@Override
	protected COSObject copyDeep(Map copied) {
		COSObject result = (COSObject) copied.get(this);
		if (result == null) {
			result = dereference().copyDeep(copied);
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#copyShallowNested()
	 */
	@Override
	protected COSDocumentElement copyShallowNested() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#dereference()
	 */
	@Override
	public COSObject dereference() {
		// synchronization is needed because of lazy loading in multithreaded
		// environment. synchronization is made against a read lock first. If
		// this fails, the read lock is released and the object is re-access
		// holding the access lock. Do NOT hold the read lock while holding the
		// access lock to avoid deadlocks arising from accessing indirect
		// objects and the access lock in opposite order (as is done when
		// writing the document).
		COSObject tempObject;
		synchronized (this) {
			tempObject = getObject();
		}
		// do NOT hold read lock when accessing !!
		if (tempObject == null) {
			Object lock = this;
			if (stGetDoc() != null) {
				lock = stGetDoc().getAccessLock();
			}
			synchronized (lock) {
				// must retry
				tempObject = getObject();
				if (tempObject == null) {
					try {
						// now swap with access lock held
						tempObject = swapIn();
						synchronized (this) {
							// and write with both locks
							setObject(tempObject);
						}
					} catch (COSRuntimeException e) {
						setObject(COSNull.create());
						if (doc != null) {
							doc.handleException(e);
						} else {
							throw e;
						}
					}
				}
			}
		}
		return tempObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.intarsys.pdf.cos.ICOSContainer#disassociate(de.intarsys.pdf.cos.
	 * ICOSContainer, de.intarsys.pdf.cos.COSObject)
	 */
	public ICOSContainer disassociate(ICOSContainer oldContainer,
			COSObject pObject) {
		referenceCount--;
		return this;
	}

	public boolean exists() {
		// synchronization is needed because of lazy loading in multithreaded
		// environment. synchronization is made against a read lock first. If
		// this fails, the read lock is released and the object is re-access
		// holding the access lock. Do NOT hold the read lock while holding the
		// access lock to avoid deadlocks arising from accessing indirect
		// objects and the access lock in opposite order (as is done when
		// writing the document).
		COSObject tempObject;
		synchronized (this) {
			tempObject = getObject();
		}
		if (tempObject != null) {
			return true;
		}
		// do NOT hold read lock when accessing !!
		Object lock = this;
		if (stGetDoc() != null) {
			lock = stGetDoc().getAccessLock();
		}
		synchronized (lock) {
			// must retry
			tempObject = getObject();
			if (tempObject != null) {
				return true;
			}
			return basicSwapIn() != null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#getDoc()
	 */
	@Override
	public COSDocument getDoc() {
		return doc;
	}

	public int getGenerationNumber() {
		return generationNumber & 0xffff;
	}

	/**
	 * The key for this.
	 * 
	 * @return The key for this.
	 */
	public COSObjectKey getKey() {
		COSObjectKey key = null;
		if (objectNumber == -1) {
			if (stGetDoc() != null) {
				key = stGetDoc().createObjectKey();
				objectNumber = key.getObjectNumber();
				generationNumber = (short) key.getGenerationNumber();
			}
		} else {
			key = new COSObjectKey(objectNumber, generationNumber);
		}
		return key;
	}

	protected COSObject getObject() {
		if (object == null) {
			return null;
		} else if (object instanceof Reference) {
			return (COSObject) ((Reference) object).get();
		} else {
			return (COSObject) object;
		}
	}

	public int getObjectNumber() {
		if (objectNumber == -1) {
			if (stGetDoc() != null) {
				COSObjectKey key = stGetDoc().createObjectKey();
				objectNumber = key.getObjectNumber();
				generationNumber = (short) key.getGenerationNumber();
			}
		}
		return objectNumber;
	}

	public void harden(COSObject pObject) {
		if (object instanceof Reference) {
			flags |= F_FIXED;
			object = ((Reference) object).get();
		}
	}

	/**
	 * <code>true</code> if the object graph referenced by this is changed.
	 * 
	 * @return <code>true</code> if the object graph referenced by this is
	 *         changed.
	 */
	public boolean isDirty() {
		return (flags & F_DIRTY) != 0;
	}

	/**
	 * <code>true</code> if this {@link COSIndirectObject} never has been
	 * swapped in.
	 * 
	 * @return <code>true</code> if this {@link COSIndirectObject} never has
	 *         been swapped in.
	 */
	public boolean isInitialState() {
		synchronized (this) {
			return object == null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#isReference()
	 */
	@Override
	public boolean isReference() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#isSwapped()
	 */
	@Override
	public boolean isSwapped() {
		synchronized (this) {
			return getObject() == null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.ICOSContainer#referenceCount()
	 */
	public int referenceCount() {
		return referenceCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#referenceIndirect(de.intarsys.pdf.cos
	 * .COSObject)
	 */
	public COSIndirectObject referenceIndirect(COSObject pObject) {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#register(de.intarsys.pdf.cos.COSObject)
	 */
	public void register(COSDocumentElement pObject) {
		if (doc != null) {
			pObject.registerWith(doc);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.COSDocumentElement#registerWith(de.intarsys.pdf.cos
	 * .COSDocument)
	 */
	@Override
	protected void registerWith(COSDocument newDoc) {
		if (doc == null) {
			doc = newDoc;
			registerWith(newDoc.stGetDoc());
			// register descendents
			COSObject tempObject = getObject();
			if (tempObject != null) {
				tempObject.registerWith(doc);
			}
		} else {
			if (doc != newDoc) {
				throw new IllegalStateException(
						"You can not merge objects from different documents"); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @param pSTDoc
	 */
	public void registerWith(STDocument pSTDoc) {
		this.stDoc = pSTDoc;
		pSTDoc.addObjectReference(this);
		if ((flags & F_DIRTY) != 0) {
			pSTDoc.addChangedReference(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.COSDocumentElement#removeContainer(de.intarsys.pdf
	 * .cos.ICOSContainer)
	 */
	@Override
	protected ICOSContainer removeContainer(ICOSContainer oldContainer) {
		return disassociate(oldContainer, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#restoreStateContainer(de.intarsys.pdf
	 * .cos.ICOSContainer)
	 */
	public ICOSContainer restoreStateContainer(ICOSContainer container) {
		COSIndirectObject indirectObject = (COSIndirectObject) container;
		this.flags = indirectObject.flags;
		// this.doc = indirectObject.doc;
		// this.encryptOnWrite = indirectObject.encryptOnWrite;
		// this.key = indirectObject.key;
		// this.stDoc = indirectObject.stDoc;
		this.referenceCount = indirectObject.referenceCount;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.ICOSContainer#storeStateContainer()
	 */
	public ICOSContainer saveStateContainer() {
		return new COSIndirectObject(this);
	}

	/**
	 * Set the dirty state of the indirect object
	 */
	public void setDirty(boolean pDirty) {
		if (pDirty) {
			if ((flags & F_DIRTY) == 0) {
				harden(null);
			}
			if (stGetDoc() != null) {
				stGetDoc().addChangedReference(this);
			}
			flags |= F_DIRTY;
		} else {
			if ((flags & F_DIRTY) != 0) {
				soften(null);
			}
			flags ^= F_DIRTY;
		}
	}

	/**
	 * Assign a {@link COSObjectKey} to this.
	 * 
	 * @param key
	 *            The new key.
	 */
	public void setKey(COSObjectKey key) {
		if (key == null) {
			objectNumber = -1;
			generationNumber = -1;
		} else {
			objectNumber = key.getObjectNumber();
			generationNumber = (short) key.getGenerationNumber();
		}
	}

	public void setKey(int objectNumber, int generationNumber) {
		this.objectNumber = objectNumber;
		this.generationNumber = (short) generationNumber;
	}

	/**
	 * Set the object for this reference. The reference is already registered
	 * with the document, the object is provided "lazy" by the storage.
	 * 
	 * @param newObject
	 *            the new object to set
	 */
	protected void setObject(COSObject newObject) {
		// COSObject oldObject = getObject();
		// if (oldObject != null) {
		// // completely destroy connection to old object - no longer reachable
		// oldObject.basicSetContainer(COSObject.NULL_CONTAINER);
		// }
		if (newObject.mayBeSwapped()) {
			this.object = new SoftReference<COSObject>(newObject);
			// debugging aid
			// this.reference = tracker.trackSoft(newObject, getKey());
		} else {
			this.object = newObject;
		}
		// set new container and register newObject
		newObject.basicSetContainer(this);
		if (doc != null) {
			newObject.registerWith(doc);
		}
	}

	public void soften(COSObject pObject) {
		// is fixed?
		if ((flags & F_FIXED) != 0 && ((COSObject) object).mayBeSwapped()) {
			flags ^= F_FIXED;
			object = new SoftReference(object);
		}
	}

	/**
	 * The ST level document.
	 * 
	 * @return The ST level document.
	 */
	final public STDocument stGetDoc() {
		return stDoc;
	}

	/**
	 * Swap in the data for the indirect object.
	 * <p>
	 * ATTENTION: this method must return the newly read object on the stack to
	 * ensure a strong reference at least as long as the caller can access the
	 * result.
	 * 
	 * @return The newly read object.
	 */
	protected COSObject swapIn() {
		COSObject loadedObject = basicSwapIn();
		if (loadedObject == null) {
			loadedObject = COSNull.create();
		}
		return loadedObject;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "" + getObjectNumber() + " " + getGenerationNumber() + " R" + "->"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.cos.ICOSContainer#willChange(de.intarsys.pdf.cos.COSObject
	 * )
	 */
	public void willChange(COSObject change) {
		if (getDoc() != null) {
			getDoc().willChange(change);
		}
		setDirty(true);
	}
}
