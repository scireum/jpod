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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.intarsys.tools.component.ISaveStateSupport;

/**
 * Abstract superclass for all COS level object types
 */
abstract public class COSObject extends COSDocumentElement implements
		ISaveStateSupport, Iterable<COSObject> {
	/**
	 * This is the container for template objects. Template objects can be
	 * created static in the application and are copied behind the scenes when
	 * integrated in a document.
	 */
	public static final ICOSContainer CONSTANT_CONTAINER = new ICOSContainer() {
		public ICOSContainer associate(ICOSContainer newContainer,
				COSObject object) {
			throw new IllegalStateException("constants can not be contained"); //$NON-NLS-1$
		}

		public COSDocumentElement containable(COSObject object) {
			return object.copyDeep().containable();
		}

		public ICOSContainer disassociate(ICOSContainer oldContainer,
				COSObject object) {
			throw new IllegalStateException("constants can not be contained"); //$NON-NLS-1$
		}

		public COSDocument getDoc() {
			return null;
		}

		public void harden(COSObject object) {
			// ignore
		}

		/**
		 * reference count for template objects is 0.
		 * 
		 * @return 0
		 * @see de.intarsys.pdf.cos.ICOSContainer#referenceCount()
		 */
		public int referenceCount() {
			return 0;
		}

		public COSIndirectObject referenceIndirect(COSObject object) {
			throw new IllegalStateException("constants can not be indirect"); //$NON-NLS-1$
		}

		public void register(COSDocumentElement object) {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.ICOSContainer#restoreStateContainer(de.intarsys.pdf.cos.ICOSContainer)
		 */
		public ICOSContainer restoreStateContainer(ICOSContainer container) {
			return container;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.ICOSContainer#storeStateContainer()
		 */
		public ICOSContainer saveStateContainer() {
			return this;
		}

		public void soften(COSObject object) {
			// ignore
		}

		public void willChange(COSObject object) {
			// do nothing
		}
	};

	/**
	 * This is the default container for non - contained objects.
	 * 
	 */
	public static final ICOSContainer NULL_CONTAINER = new ICOSContainer() {
		public ICOSContainer associate(ICOSContainer newContainer,
				COSObject object) {
			object.basicSetContainer(newContainer);
			newContainer.register(object);
			return newContainer;
		}

		public COSDocumentElement containable(COSObject object) {
			return object;
		}

		public ICOSContainer disassociate(ICOSContainer oldContainer,
				COSObject object) {
			throw new IllegalStateException("association inconsistent"); //$NON-NLS-1$
		}

		public COSDocument getDoc() {
			return null;
		}

		public void harden(COSObject object) {
			// ignore
		}

		/**
		 * reference count for new objects is 0.
		 * 
		 * @return 0
		 * @see de.intarsys.pdf.cos.ICOSContainer#referenceCount()
		 */
		public int referenceCount() {
			return 0;
		}

		public COSIndirectObject referenceIndirect(COSObject object) {
			return COSIndirectObject.create(object);
		}

		public void register(COSDocumentElement object) {
			// do nothing
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.ICOSContainer#restoreStateContainer(de.intarsys.pdf.cos.ICOSContainer)
		 */
		public ICOSContainer restoreStateContainer(ICOSContainer container) {
			return container;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.ICOSContainer#storeStateContainer()
		 */
		public ICOSContainer saveStateContainer() {
			return this;
		}

		public void soften(COSObject object) {
			// ignore
		}

		public void willChange(COSObject object) {
			// do nothing
		}
	};

	public static final Object SLOT_CONTAINER = new Object();

	/**
	 * The optional back-reference to the object "containing" this if any (for
	 * example an array).
	 * 
	 * <p>
	 * A literal object may only be contained once, an indirect referenced
	 * object is contained by the indirect reference
	 * </p>
	 */
	protected ICOSContainer container;

	protected COSObject() {
		container = NULL_CONTAINER;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#addContainer(de.intarsys.pdf.cos.ICOSContainer)
	 */
	@Override
	protected ICOSContainer addContainer(ICOSContainer newContainer) {
		return container.associate(newContainer, this);
	}

	/**
	 * Add a listener for object changes.
	 * 
	 * @param listener
	 *            The listener to be informed about changes.
	 */
	abstract public void addObjectListener(ICOSObjectListener listener);

	/**
	 * <code>this</code> as a {@link COSArray} or <code>null</code>
	 * 
	 * @return <code>this</code> as a COSArray or <code>null</code>
	 */
	public COSArray asArray() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSBoolean} or <code>null</code>
	 */
	public COSBoolean asBoolean() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSDictionary} or
	 *         <code>null</code>
	 */
	public COSDictionary asDictionary() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSFixed} or <code>null</code>
	 */
	public COSFixed asFixed() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSInteger} or <code>null</code>
	 */
	public COSInteger asInteger() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSName} or <code>null</code>
	 */
	public COSName asName() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSNull} or <code>null</code>
	 */
	public COSNull asNull() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSNumber} or <code>null</code>
	 */
	public COSNumber asNumber() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSStream} or <code>null</code>
	 */
	public COSStream asStream() {
		return null;
	}

	/**
	 * @return <code>this</code> as a {@link COSString} or <code>null</code>
	 */
	public COSString asString() {
		return null;
	}

	/**
	 * An iterator over contained objects and references. The iterator is an
	 * empty iterator if this is not a container.
	 * 
	 * <p>
	 * This iterator returns COSDocumentElements, leaving references alone.
	 * </p>
	 * 
	 * @return Iterator over contained objects and references.
	 */
	abstract public Iterator<COSDocumentElement> basicIterator();

	protected void basicSetContainer(ICOSContainer newContainer) {
		container = newContainer;
	}

	/**
	 * A string representation for the receiver.
	 * 
	 * @return A string representation for the receiver.
	 */
	abstract protected String basicToString();

	/**
	 * Declare this to be a constant. This declaration ensures that when using
	 * this in a document context a copy will be made.
	 * 
	 * @return The receiver.
	 */
	public COSObject beConstant() {
		container = CONSTANT_CONTAINER;
		return this;
	}

	/**
	 * Make an indirect object out of a direct one. An object can always be
	 * changed to an indirect one.
	 * <p>
	 * It is possible to morph existing objects into indirect ones, the objects
	 * in the hierarchy (container/document) are informed and will reflect the
	 * change.
	 * </p>
	 */
	public COSIndirectObject beIndirect() {
		return container.referenceIndirect(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#containable(de.intarsys.pdf.cos.COSDocumentElement)
	 */
	@Override
	public COSDocumentElement containable() {
		return container.containable(this);
	}

	/**
	 * Create a new instance of the receiver that may be used as the base object
	 * for a copy. The result is an uninitialized instance of the receiver. The
	 * attributes are copied depending on the strategy (deep, shallow).
	 * 
	 * @return The new instance of a COSObject
	 */
	abstract protected COSObject copyBasic();

	/**
	 * Make a deep copy of the receiver within the same document. The result is
	 * a "PDF semantic" deep copy, implementation artifacts as "attributes" and
	 * listeners are NOT copied.
	 * 
	 * <p>
	 * The algorithm copies <code>this</code> along with all outgoing
	 * references (recursively).
	 * </p>
	 * 
	 * <p>
	 * Object identity is preserved.
	 * </p>
	 * 
	 * <p>
	 * Be careful when copying objects, as there are semantics that may NOT be
	 * recognized by this method.
	 * </p>
	 * 
	 * @return the object copied recursively
	 */
	abstract public COSObject copyDeep();

	/**
	 * Make a deep copy of the receiver within the same document. The result is
	 * a "PDF semantic" deep copy, implementation artifacts as "attributes" and
	 * listeners are NOT copied.
	 * <p>
	 * The <code>copied</code> map is used to identify objects copied in
	 * earlier runs of this method to avoid duplicating resources used in
	 * different copy targets (for example the pages of a document).
	 * <code>copied</code> is modified while executing <code>copyDeep</code>
	 * and contains a mapping from indirect objects in the original document to
	 * copied objects.
	 * <p>
	 * The algorithm copies <code>this</code> along with all outgoing
	 * references (recursively).
	 * </p>
	 * 
	 * <p>
	 * Object identity is preserved.
	 * </p>
	 * 
	 * <p>
	 * Be careful when copying objects, as there are semantics that may NOT be
	 * recognized by this method.
	 * </p>
	 * 
	 * @return the object copied recursively
	 */
	@Override
	abstract public COSObject copyDeep(Map copied);

	/**
	 * Make a copy of the receiver within the same document. A copy is made only
	 * if we have an object that may not be inserted in multiple containers.
	 * This means all direct objects are (recursively) copied, all indirect
	 * objects return the receiver.
	 * 
	 * <p>
	 * Be careful when copying objects, as there are semantics that may NOT be
	 * recognized by this method.
	 * </p>
	 * 
	 * @return The optional copy.
	 */
	public final COSObject copyOptional() {
		if (isIndirect()) {
			return this;
		}
		return copyShallow();
	}

	/**
	 * Make a copy of the receiver.
	 * 
	 * <p>
	 * A copy is made of the receiver and after this recursively of all not
	 * indirect objects.
	 * </p>
	 * 
	 * <p>
	 * Be careful when copying objects, as there are semantics that may NOT be
	 * recognized by this method.
	 * </p>
	 * 
	 * @return The object copied
	 */
	public COSObject copyShallow() {
		COSObject result = copyBasic();
		if (isIndirect()) {
			result.beIndirect();
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
		return copyShallow();
	}

	/**
	 * Make a copy of the receiver within the same document.
	 * 
	 * <p>
	 * The algorithm copies <code>this</code> along with all outgoing
	 * references (recursively) that themselve have a navigation path to
	 * <code>this</code>. The result is a new subgraph extending from the
	 * copy of <code>this</code> where no navigation path leads back to
	 * <code>this</code>.
	 * </p>
	 * 
	 * <p>
	 * Object identity is preserved.
	 * </p>
	 * 
	 * <p>
	 * Be careful when copying objects, as there are semantics that may NOT be
	 * recognized by this method.
	 * </p>
	 * 
	 * @return the object copied recursively
	 */
	public final COSObject copySubGraph() {
		return copySubGraph(new HashMap());
	}

	/**
	 * The implementation of {@link
	 * de.intarsys.pdf.cos.COSObject#copySubGraph()}. The parameters
	 * <code>copied</code>keeps track of already copied objects to deal with
	 * cyclic references.
	 * 
	 * @see de.intarsys.pdf.cos.COSObject#copySubGraph()
	 */
	protected COSObject copySubGraph(Map copied) {
		COSObject result = copyBasic();
		if (isIndirect()) {
			result.beIndirect();
			copied.put(getIndirectObject(), result);
		}
		return result;
	}

	/**
	 * return the real object. this is needed for polymorphic handling of
	 * document elements. at application programming level only
	 * {@link COSObject}, never {@link COSIndirectObject} is seen.
	 * 
	 * @return {@link de.intarsys.pdf.cos.COSObject}
	 */
	@Override
	public COSObject dereference() {
		return this;
	}

	/**
	 * Answer the object that contains this. The container is never null.
	 * 
	 * @return Answer the object that contains this.
	 */
	public ICOSContainer getContainer() {
		return container;
	}

	/**
	 * The document that contains this.
	 * 
	 * <p>
	 * This may return null, as COSObject graphs may be created "offline" and
	 * add to the document as a whole.
	 * </p>
	 * 
	 * <p>
	 * The document is evaluated via the COSObject graph hierarchy that finally
	 * must be contained within a document.
	 * </p>
	 * 
	 * @return The document that contains this.
	 */
	@Override
	public COSDocument getDoc() {
		return container.getDoc();
	}

	/**
	 * return the indirect object for the receiver. application level
	 * programmers should not use this method. this is needed for creating a
	 * physical representation of the document (serializing)
	 * 
	 * @return the indirect object for the receiver
	 */
	public COSIndirectObject getIndirectObject() {
		if (container instanceof COSIndirectObject) {
			return (COSIndirectObject) container;
		} else {
			return null;
		}
	}

	/**
	 * "Cast" the receiver to a Java boolean. This is a convenience method to
	 * ease programming with {@link COSObject}.
	 * 
	 * @param defaultValue
	 *            The default value to return.
	 * 
	 * @return The value of the receiver if not null and type compatible, the
	 *         defaultValue otherwise.
	 */
	public boolean getValueBoolean(boolean defaultValue) {
		COSBoolean tempCos = asBoolean();
		if (tempCos == null) {
			return defaultValue;
		}
		return tempCos.booleanValue();
	}

	/**
	 * "Cast" the receiver to a Java byte[].This is a convenience method to ease
	 * programming with {@link COSObject}.
	 * 
	 * @param defaultValue
	 *            The default value to return.
	 * 
	 * @return The value of the receiver if not null and type compatible, the
	 *         defaultValue otherwise.
	 */
	public byte[] getValueBytes(byte[] defaultValue) {
		COSObject tempCos;
		tempCos = asString();
		if (tempCos != null) {
			return ((COSString) tempCos).byteValue();
		}
		tempCos = asName();
		if (tempCos != null) {
			return ((COSName) tempCos).byteValue();
		}
		tempCos = asStream();
		if (tempCos != null) {
			return ((COSStream) tempCos).getDecodedBytes();
		}
		return defaultValue;
	}

	/**
	 * "Cast" the receiver to a Java float.This is a convenience method to ease
	 * programming with {@link COSObject}.
	 * 
	 * @param defaultValue
	 *            The default value to return.
	 * 
	 * @return The value of the receiver if not null and type compatible, the
	 *         defaultValue otherwise.
	 */
	public float getValueFloat(float defaultValue) {
		COSNumber tempCos = asNumber();
		if (tempCos == null) {
			return defaultValue;
		}
		return tempCos.floatValue();
	}

	/**
	 * "Cast" the receiver to a Java int.This is a convenience method to ease
	 * programming with {@link COSObject}.
	 * 
	 * @param defaultValue
	 *            The default value to return.
	 * 
	 * @return The value of the receiver if not null and type compatible, the
	 *         defaultValue otherwise.
	 */
	public int getValueInteger(int defaultValue) {
		COSNumber tempCos = asNumber();
		if (tempCos == null) {
			return defaultValue;
		}
		return tempCos.intValue();
	}

	/**
	 * "Cast" the receiver to a Java String.This is a convenience method to ease
	 * programming with {@link COSObject}.
	 * 
	 * @param defaultValue
	 *            The default value to return.
	 * 
	 * @return The value of the receiver if not null and type compatible, the
	 *         defaultValue otherwise.
	 */
	public String getValueString(String defaultValue) {
		COSObject tempCos;
		tempCos = asString();
		if (tempCos != null) {
			return tempCos.stringValue();
		}
		tempCos = asName();
		if (tempCos != null) {
			return tempCos.stringValue();
		}
		tempCos = asStream();
		if (tempCos != null) {
			return tempCos.stringValue();
		}
		return defaultValue;
	}

	/**
	 * "Harden" the reference to this object. The indirect object at the root of
	 * the containment hierarchy will force a strong reference to its root
	 * COSObject.
	 */
	public void harden() {
		container.harden(this);
	}

	/**
	 * Answer <code>true</code> if the receiver has a navigation path to
	 * <code>other</code>.
	 * 
	 * @param other
	 *            The object we search a path to from <code>this</code>.
	 * 
	 * @return Answer <code>true</code> if the receiver has a navigation path
	 *         to <code>other</code>.
	 */
	protected boolean hasNavigationPathTo(COSObject other) {
		return false;
	}

	/**
	 * <code>true</code> if this object is not contained in a document
	 * directly or indirectly. This is especially true when an object is new (or
	 * reset to this state when an undo happend). Remember that an object can
	 * still be garbage, even if it is not dangling.
	 * 
	 * @return <code>true</code> if this object is not contained in a document
	 *         directly or indirectly.
	 */
	public boolean isDangling() {
		return container.referenceCount() == 0;
	}

	/**
	 * Answer <code>true</code> if this object is an indirect one.
	 * 
	 * @return <code>true</code> if this object is an indirect one.
	 */
	public boolean isIndirect() {
		return container instanceof COSIndirectObject;
	}

	/**
	 * answer true if receiver is the null object
	 * 
	 * @return true if receiver is the null object
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * answer true if receiver is a number
	 * 
	 * @return answer true if receiver is a number
	 */
	public boolean isNumber() {
		return false;
	}

	/**
	 * <code>true</code> if an {@link ICOSObjectListener} is registered. This
	 * is for test purposes.
	 */
	abstract public boolean isObjectListenerAvailable();

	/**
	 * Answer true if this object is of a primitive type
	 * 
	 * @return Answer true if this object is of a primitive type
	 */
	abstract public boolean isPrimitive();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#isSwapped()
	 */
	@Override
	public boolean isSwapped() {
		return isIndirect() && getIndirectObject().isSwapped();
	}

	/**
	 * An iterator over contained objects. The iterator is an empty iterator if
	 * this is not a container.
	 * 
	 * <p>
	 * This iterator returns only COSObject instances, references are
	 * dereferenced.
	 * </p>
	 * 
	 * @return Iterator over contained objects.
	 */
	@Override
	abstract public Iterator<COSObject> iterator();

	/**
	 * <code>true</code> if this object may be swapped from memory by the
	 * garbage collector.
	 * 
	 * @return <code>true</code> if this object may be swapped from memory by
	 *         the garbage collector.
	 */
	public boolean mayBeSwapped() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSDocumentElement#removeContainer(de.intarsys.pdf.cos.ICOSContainer)
	 */
	@Override
	protected ICOSContainer removeContainer(ICOSContainer oldContainer) {
		return container.disassociate(oldContainer, this);
	}

	/**
	 * Remove a listener for object changes.
	 * 
	 * @param listener
	 *            The listener to be removed.
	 */
	abstract public void removeObjectListener(ICOSObjectListener listener);

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.tools.component.ISaveStateSupport#restoreState(java.lang.Object)
	 */
	public void restoreState(Object saveState) {
		container = container
				.restoreStateContainer(((COSObject) saveState).container);
	}

	/**
	 * "Soften" the reference to this object. The indirect object at the root of
	 * the containment hierarchy will release a strong reference to its root
	 * COSObject.
	 */
	public void soften() {
		container.soften(this);
	}

	/**
	 * A string representation for the receiver. This is a very "soft"
	 * definition. Do not rely on any specific format to be returned here.
	 * 
	 * @return A string representation for the receiver.
	 */
	public String stringValue() {
		return basicToString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return basicToString();
	}

	abstract protected void triggerChanged(Object slot, Object oldValue,
			Object newValue);

}
