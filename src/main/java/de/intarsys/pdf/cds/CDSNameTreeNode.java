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
package de.intarsys.pdf.cds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.tools.collection.EmptyIterator;

/**
 * Implementation of the PDF name tree.
 * 
 */
public class CDSNameTreeNode extends CDSTreeNode {
	// todo 2 rewrite to META

	static public final COSName DK_Names = COSName.constant("Names"); //$NON-NLS-1$

	/**
	 * Create the correct concrete CDSTreeNode implementation for
	 * <code>node</code>.
	 * 
	 * @param node
	 *            The {@link COSDictionary} defining a CDSTreeNode subclass
	 *            instance
	 * 
	 * @return The concrete CDSTreeNode implementation for <code>node</code>.
	 */
	static public CDSNameTreeNode createFromCos(COSDictionary node) {
		if (node == null) {
			return null;
		}
		return new CDSNameTreeNode(node);
	}

	static public CDSTreeNode createIntermediate() {
		CDSNameTreeNode result = new CDSNameTreeNode(COSDictionary.create());
		result.createLimits();
		result.createKids();
		return result;
	}

	static public CDSNameTreeNode createLeaf() {
		CDSNameTreeNode result = new CDSNameTreeNode(COSDictionary.create());
		result.createLimits();
		result.createNames();
		return result;
	}

	static public CDSNameTreeNode createRootIntermediate() {
		CDSNameTreeNode result = new CDSNameTreeNode(COSDictionary.create());
		result.createKids();
		return result;
	}

	static public CDSNameTreeNode createRootLeaf() {
		CDSNameTreeNode result = new CDSNameTreeNode(COSDictionary.create());
		result.createNames();
		return result;
	}

	/**
	 * Create a CDSTreeNode based on the {@link COSDictionary}<code>
	 * dict</code>.
	 * 
	 * @param dict
	 *            The{@link COSDictionary} defining the receiver.
	 */
	protected CDSNameTreeNode(COSDictionary dict) {
		super(dict);
	}

	/**
	 * Add all children from <code>node</code>.
	 * 
	 * @param node
	 *            A {@link CDSNameTreeNode} whose children are copied.
	 */
	public void addAll(CDSNameTreeNode node) {
		for (Iterator i = node.iterator(); i.hasNext();) {
			CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
			COSString name = (COSString) entry.getName().copyOptional();
			COSObject value = entry.getValue().copyOptional();
			put(name, value);
		}
	}

	protected void checkLimits() {
		if (getLimits() == null || getLimits().size() != 2) {
			createLimits();
			updateLimits();
		}
	}

	/**
	 * Answer <code>true</code> if the receiver subtree contains a key that
	 * matches the parameter.
	 * 
	 * @param name
	 *            The key that is searched in the receiver subtree.
	 * 
	 * @return Answer <code>true</code> if the receiver subtree contains a key
	 *         that matches the parameter.
	 */
	public boolean contains(COSString name) {
		if (!mayContain(name)) {
			return false;
		}
		List tempKids = getKids();
		if (tempKids != null) {
			for (Iterator i = tempKids.iterator(); i.hasNext();) {
				CDSNameTreeNode node = (CDSNameTreeNode) i.next();
				if (node.contains(name)) {
					return true;
				}
			}
			return false;
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			for (Iterator i = tempEntries.iterator(); i.hasNext();) {
				CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
				int c = entry.getName().compareTo(name);
				if (c == 0) {
					return true;
				}
				if (c > 0) {
					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Create an initial value for the /Kids entry
	 */
	protected void createKids() {
		COSArray array = COSArray.create();
		cosGetDict().put(DK_Kids, array);
	}

	/**
	 * Create an initial value for the limits of the receiver subtree.
	 */
	protected void createLimits() {
		COSArray array = COSArray.create(2);
		array.add(COSNull.create());
		array.add(COSNull.create());
		cosGetDict().put(DK_Limits, array);
	}

	/**
	 * Create an initial value for the /Names entry
	 */
	protected void createNames() {
		COSArray array = COSArray.create();
		cosGetDict().put(DK_Names, array);
	}

	/**
	 * Answer the value associated with the key <code>name</code>. If no key
	 * is available that matches the parameter, <code>COSNull</code> is
	 * returned.
	 * 
	 * @param name
	 *            The key whose value is looked up.
	 * 
	 * @return Answer the value associated with the key <code>name</code>.
	 */
	public COSObject get(COSString name) {
		if (!mayContain(name)) {
			return COSNull.NULL;
		}
		List tempKids = getKids();
		if (tempKids != null) {
			for (Iterator i = tempKids.iterator(); i.hasNext();) {
				CDSNameTreeNode node = (CDSNameTreeNode) i.next();
				COSObject result = node.get(name);
				if (!result.isNull()) {
					return result;
				}
			}
			return COSNull.NULL;
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			for (Iterator i = tempEntries.iterator(); i.hasNext();) {
				CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
				int c = entry.getName().compareTo(name);
				if (c == 0) {
					return entry.getValue();
				}
				if (c > 0) {
					return COSNull.NULL;
				}
			}
		}
		return COSNull.NULL;
	}

	public List getEntries() {
		if (entries == null) {
			COSArray cosNames = cosGetDict().get(DK_Names).asArray();
			if (cosNames != null) {
				entries = new ArrayList();
				for (Iterator i = cosNames.iterator(); i.hasNext();) {
					COSString name = ((COSObject) i.next()).asString();
					if (!i.hasNext()) {
						break;
					}
					COSObject value = (COSObject) i.next();
					if (name != null) {
						CDSTreeEntry entry = new CDSNameTreeEntry(name, value);
						entries.add(entry);
					}
				}
			}
		}
		return entries;
	}

	public List getKids() {
		if (kids == null) {
			COSArray cosKids = cosGetDict().get(DK_Kids).asArray();
			if (cosKids != null) {
				kids = new ArrayList();
				for (Iterator i = cosKids.iterator(); i.hasNext();) {
					COSDictionary dict = ((COSObject) i.next()).asDictionary();
					if (dict != null) {
						CDSTreeNode kid = CDSNameTreeNode.createFromCos(dict);
						kids.add(kid);
					}
				}
			}
		}
		return kids;
	}

	/**
	 * Return the two element array containing the smallest and the largest key
	 * within the receiver subtree.
	 * 
	 * @return Return the two element array containing the smallest and the
	 *         largest key within the receiver subtree.
	 */
	public COSArray getLimits() {
		if (limits == null) {
			limits = cosGetDict().get(DK_Limits).asArray();
		}
		return limits;
	}

	/**
	 * The maximum key within the receiver subtree.
	 * 
	 * @return The maximum key within the receiver subtree.
	 */
	public COSString getMax() {
		if (getLimits() != null) {
			return getLimits().get(1).asString();
		}
		return null;
	}

	/**
	 * The minimum key within the receiver subtree.
	 * 
	 * @return The minimum key within the receiver subtree.
	 */
	public COSString getMin() {
		if (getLimits() != null) {
			return getLimits().get(0).asString();
		}
		return null;
	}

	@Override
	public boolean isLeaf() {
		return cosGetDict().containsKey(DK_Names);
	}

	/**
	 * An {@link Iterator} on all leaf fields in the subtree.
	 * 
	 * @return An {@link Iterator} on all leaf fields in the subtree.
	 */
	public Iterator iterator() {
		List tempKids = getKids();
		if (tempKids != null) {
			return new Iterator() {
				private Iterator thisIterator = getKids().iterator();

				private Iterator childIterator;

				public boolean hasNext() {
					if ((childIterator != null) && childIterator.hasNext()) {
						return true;
					}
					if (thisIterator.hasNext()) {
						CDSNameTreeNode current = (CDSNameTreeNode) thisIterator
								.next();
						childIterator = current.iterator();
						return hasNext();
					}
					return false;
				}

				public Object next() {
					if ((childIterator != null) && childIterator.hasNext()) {
						return childIterator.next();
					}
					if (thisIterator.hasNext()) {
						CDSNameTreeNode current = (CDSNameTreeNode) thisIterator
								.next();
						childIterator = current.iterator();
						return next();
					}
					throw new NoSuchElementException();
				}

				public void remove() {
					throw new UnsupportedOperationException();
				}
			};
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			return tempEntries.iterator();
		}
		return EmptyIterator.UNIQUE;
	}

	/**
	 * Answer <code>true</code> if the receiver MAY contain the key
	 * <code>name</code>.
	 * 
	 * <p>
	 * Thi means, <code>name</code> lies between the range defined by the
	 * lower und upper limit key of the receiver.
	 * </p>
	 * 
	 * @param name
	 *            The key name to lookup.
	 * 
	 * @return Answer <code>true</code> if the receiver MAY contain the key
	 *         <code>name</code>.
	 */
	public boolean mayContain(COSString name) {
		if ((getMin() == null) || (getMax() == null)) {
			return true;
		}
		if (name.compareTo(getMin()) < 0) {
			return false;
		}
		if (name.compareTo(getMax()) > 0) {
			return false;
		}
		return true;
	}

	/**
	 * Store <code>value</code> under the key given in <code>name</code>.
	 * 
	 * @param name
	 *            The name with which the value should be associated.
	 * @param value
	 *            The value to associate with the name.
	 * @return The object previously associated with <code>name</code> or
	 *         {@link COSNull}.
	 */
	public COSObject put(COSString name, COSObject value) {
		COSObject result = COSNull.NULL;
		List tempKids = getKids();
		if (tempKids != null) {
			CDSNameTreeNode insertNode = null;
			for (Iterator i = tempKids.iterator(); i.hasNext();) {
				CDSNameTreeNode node = (CDSNameTreeNode) i.next();
				insertNode = node;
				if (node.getMax().compareTo(name) > 0) {
					break;
				}
			}
			if (insertNode == null) {
				// no suitable kid found - create new
				// todo this algorithm is quite simple....
				insertNode = CDSNameTreeNode.createLeaf();
				getKids().add(insertNode);
				COSArray cosKids = cosGetDict().get(DK_Kids).asArray();
				cosKids.add(insertNode.cosGetObject());
			}
			insertNode.put(name, value);
			updateLimits();
			return result;
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			int index = 0;
			for (Iterator i = tempEntries.iterator(); i.hasNext();) {
				CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
				int c = entry.getName().compareTo(name);
				if (c == 0) {
					return entry.setValue(value);
				}
				if (c > 0) {
					break;
				}
				index++;
			}
			CDSTreeEntry entry = new CDSNameTreeEntry(name, value);
			tempEntries.add(index, entry);
			COSArray cosEntries = cosGetDict().get(DK_Names).asArray();
			int entryIndex = index * 2;
			cosEntries.add(entryIndex, value);
			cosEntries.add(entryIndex, name);
			updateLimits();
			return COSNull.NULL;
		}
		// ooops - should we create a /Names entry?
		return result;
	}

	/**
	 * Remove the mapping for key given in <code>name</code>.
	 * 
	 * @param name
	 *            The name fo the mapping to be removed
	 * @return The object previously associated with <code>name</code> or
	 *         {@link COSNull}.
	 */
	public COSObject remove(COSString name) {
		List tempKids = getKids();
		if (tempKids != null) {
			for (Iterator i = tempKids.iterator(); i.hasNext();) {
				CDSNameTreeNode node = (CDSNameTreeNode) i.next();
				if (node.getMax().compareTo(name) > 0) {
					COSObject result = node.remove(name);
					updateLimits();
					return result;
				}
			}
			return COSNull.NULL;
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			int index = 0;
			for (Iterator it = tempEntries.iterator(); it.hasNext();) {
				CDSNameTreeEntry entry = (CDSNameTreeEntry) it.next();
				int c = entry.getName().compareTo(name);
				if (c == 0) {
					it.remove();
					COSArray cosEntries = cosGetDict().get(DK_Names).asArray();
					int entryIndex = index * 2;
					cosEntries.remove(entryIndex);
					cosEntries.remove(entryIndex);
					COSObject result = entry.getValue();
					updateLimits();
					return result;
				}
				if (c > 0) {
					break;
				}
				index++;
			}
			return COSNull.NULL;
		}
		return COSNull.NULL;
	}

	protected void updateLimits() {
		if (getLimits() == null) {
			return;
		}
		List tempKids = getKids();
		if (tempKids != null) {
			if (tempKids.size() > 0) {
				CDSNameTreeNode minNode = (CDSNameTreeNode) tempKids.get(0);
				minNode.checkLimits();
				getLimits().set(0, minNode.getLimits().get(0).copyOptional());
				CDSNameTreeNode maxNode = (CDSNameTreeNode) tempKids
						.get(tempKids.size() - 1);
				maxNode.checkLimits();
				getLimits().set(1, maxNode.getLimits().get(1).copyOptional());
			}
		}
		List tempEntries = getEntries();
		if (tempEntries != null) {
			if (tempEntries.size() > 0) {
				CDSNameTreeEntry minEntry = (CDSNameTreeEntry) tempEntries
						.get(0);
				getLimits().set(0, minEntry.getName().copyOptional());
				CDSNameTreeEntry maxEntry = (CDSNameTreeEntry) tempEntries
						.get(tempEntries.size() - 1);
				getLimits().set(1, maxEntry.getName().copyOptional());
			}
		}
	}
}
