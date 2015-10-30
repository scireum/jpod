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
import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A page tree node is a container for pages (and other page tree nodes) within
 * a PFD document. All pages in the document are direct or indirect children of
 * the root page tree node in the {@link COSCatalog} object
 */
public class PDPageTree extends PDPageNode {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDPageNode.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDPageTree(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName DK_Kids = COSName.constant("Kids");

    public static final COSName DK_Count = COSName.constant("Count");

    private static final int MAX_KIDS = 1000;

    /**
     * the immediate children of this node
     */
    private SoftReference<List> cachedKids;

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDPageTree(COSObject object) {
        super(object);
    }

    /**
     * Add a page after at the designated index.
     *
     * @param index   the index which to insert the new child at
     * @param newNode the child page to add
     */
    public void addNode(int index, PDPageNode newNode) {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids == null) {
            cosKids = COSArray.create();
            cosSetField(DK_Kids, cosKids);
        }
        index = Math.min(index, cosKids.size());
        if (index < 0) {
            index = cosKids.size();
        }
        newNode.setParent(this);
        cosKids.add(index, newNode.cosGetDict());
        incrementCount(newNode.getCount());
    }

    /**
     * Add a page as immediate child at last position
     *
     * @param newNode the child page to create
     */
    public void addNode(PDPageNode newNode) {
        addNodeAfter(newNode, null);
    }

    /**
     * Add a page after the designated destination page. the destination page
     * must be in the receiver tree node
     *
     * @param newNode     the child page to add
     * @param destination the page after which to insert the new child
     */
    public void addNodeAfter(PDPageNode newNode, PDPageNode destination) {
        int destinationIndex = -1;
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if ((cosKids != null) && (destination != null)) {
            destinationIndex = cosKids.indexOf(destination.cosGetDict());
        }
        int newNodeIndex = -1;
        if (destinationIndex > -1) {
            newNodeIndex = destinationIndex + 1;
        }
        addNode(newNodeIndex, newNode);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#collectAnnotations(java.util.List)
     */
    @Override
    protected void collectAnnotations(List annotations) {
        for (Iterator it = getKids().iterator(); it.hasNext(); ) {
            PDPageNode childNode = (PDPageNode) it.next();
            childNode.collectAnnotations(annotations);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
     */
    @Override
    protected COSName cosGetExpectedType() {
        return CN_Type_Pages;
    }

    protected COSObject cosSetKids(COSArray newKids) {
        setCount(newKids.size());
        return cosSetField(DK_Kids, newKids);
    }

    protected void exchangeNode(PDPageNode oldNode, PDPageNode newNode) {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids == null) {
            return;
        }
        int index = cosKids.indexOf(oldNode.cosGetDict());
        if (index >= 0) {
            oldNode.setParent(null);
            newNode.setParent(this);
            cosKids.set(index, newNode.cosGetDict());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#getCount()
     */
    @Override
    public int getCount() {
        return getFieldInt(DK_Count, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#getFirstNode()
     */
    @Override
    public PDPageNode getFirstNode() {
        if (getKids().isEmpty()) {
            return null;
        }
        return (PDPageNode) getKids().get(0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#getFirstPage()
     */
    @Override
    public PDPage getFirstPage() {
        for (Iterator i = getKids().iterator(); i.hasNext(); ) {
            PDPageNode node = (PDPageNode) i.next();
            PDPage page = node.getFirstPage();
            if (page != null) {
                return page;
            }
        }
        return null;
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

    /**
     * Get the list of all {@link PDPageNode} instances that are children of the
     * receiver.
     *
     * @return an ArrayList
     */
    public List getKids() {
        List kids = null;
        if (cachedKids != null) {
            kids = cachedKids.get();
        }
        if (kids == null) {
            kids = new ArrayList();
            COSArray cosKids = cosGetField(DK_Kids).asArray();
            if (cosKids != null) {
                for (Iterator i = cosKids.iterator(); i.hasNext(); ) {
                    COSBasedObject page = PDPageNode.META.createFromCos((COSObject) i.next());
                    if (page != null) {
                        kids.add(page);
                    }
                }
                cosKids.addObjectListener(this);
            }
            cachedKids = new SoftReference<List>(kids);
        }
        return kids;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#getLast()
     */
    @Override
    public PDPageNode getLastNode() {
        List theKids = getKids();
        if (theKids.isEmpty()) {
            return null;
        }
        return (PDPageNode) theKids.get(theKids.size() - 1);
    }

    @Override
    public PDPage getLastPage() {
        List theKids = getKids();
        for (int i = theKids.size() - 1; i >= 0; i--) {
            PDPageNode node = (PDPageNode) theKids.get(i);
            PDPage result = node.getLastPage();
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Get the successor of node in the collection of children. If node is not a
     * direct child return null. If page is the last child, lookup the next page
     * via the parent of this.
     *
     * @param node The page whose successor is requested.
     * @return Get the next page in the collection of children after page.
     */
    protected PDPageNode getNextNode(PDPageNode node) {
        List children = getKids();
        int index = children.indexOf(node);
        if (index < 0) {
            return null;
        }
        if (index < (children.size() - 1)) {
            return (PDPageNode) children.get(index + 1);
        }
        if (getParent() == null) {
            return null;
        }
        return getParent().getNextNode(this);
    }

    /**
     * Get the next page in the collection of children after page. If page is
     * not a direct child return null. If page is the last child, lookup the
     * next page via the parent of this.
     *
     * @param page The page whose successor is requested.
     * @return Get the next page in the collection of children after page.
     */
    protected PDPage getNextPage(PDPage page) {
        PDPage nextPage = null;
        for (PDPageNode nextNode = getNextNode(page); nextNode != null; nextNode = nextNode.getNextNode()) {
            nextPage = nextNode.getFirstPage();
            if (nextPage != null) {
                break;
            }
        }
        return nextPage;
    }

    /**
     * The 0 based index of {@code node} within {@code this} and all
     * its ancestors, 0 if not found.
     *
     * @param node node whose position is determined.
     * @return The 0 based index of {@code node} within {@code this}
     * and all its ancestors, 0 if not found.
     */
    protected int getNodeIndex(PDPageNode node) {
        int nodePos = 0;
        for (Iterator i = getKids().iterator(); i.hasNext(); ) {
            PDPageNode child = (PDPageNode) i.next();
            if (child == node) {
                break;
            }
            nodePos += child.getCount();
        }
        PDPageTree parent = getParent();
        if (parent == null) {
            return nodePos;
        }
        return nodePos + parent.getNodeIndex(this);
    }

    /**
     * Get the predecessor of node in the collection of children. If node is not
     * a direct child return null. If page is the first child, lookup the
     * previous page via the parent of this.
     *
     * @param node The page whose predecessor is requested.
     * @return Get the predecessor of node in the collection of children.
     */
    protected PDPageNode getPreviousNode(PDPageNode node) {
        List children = getKids();
        int index = children.indexOf(node);
        if (index < 0) {
            return null;
        }
        if (index > 0) {
            return (PDPageNode) children.get(index - 1);
        }
        if (getParent() == null) {
            return null;
        }
        return getParent().getPreviousNode(this);
    }

    /**
     * Get the previous page in the collection of children before page. If page
     * is not a direct child return null. If page is the first child, lookup the
     * previous page via the parent of this.
     *
     * @param page The page whose predecessor is requested.
     * @return Get the previous page in the collection of children before page.
     */
    protected PDPage getPreviousPage(PDPage page) {
        PDPage previousPage = null;
        for (PDPageNode previousNode = getPreviousNode(page);
             previousNode != null;
             previousNode = previousNode.getPreviousNode()) {
            previousPage = previousNode.getLastPage();
            if (previousPage != null) {
                break;
            }
        }
        return previousPage;
    }

    /**
     * increment the number of pages and propagate change to parent node
     *
     * @param delta number of pages to add
     */
    protected void incrementCount(int delta) {
        int oldValue = getCount();
        cosSetField(DK_Count, COSInteger.create(oldValue + delta));
        if (getParent() != null) {
            getParent().incrementCount(delta);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDComplexBase#initializeFromScratch()
     */
    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        cosSetField(DK_Kids, COSArray.create());
        cosSetField(DK_Count, COSInteger.create(0));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#invalidateCaches()
     */
    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids != null) {
            cosKids.removeObjectListener(this);
        }
        cachedKids = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#isPage()
     */
    @Override
    public boolean isPage() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDPageNode#isValid()
     */
    @Override
    public boolean isValid() {
        PDPageTree tempParent = getParent();
        if (tempParent == null) {
            PDDocument tempDoc = getDoc();
            if (tempDoc == null) {
                return false;
            }
            // check root
            return tempDoc.getPageTree() == this;
        }
        return tempParent.isValid();
    }

    /**
     * Rebalance this.
     *
     * @return The new {@link PDPageTree} created or null if nothing changed.
     */
    public PDPageTree rebalance() {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids == null) {
            return null;
        }
        if (cosKids.size() > MAX_KIDS) {
            PDPageTree newNode = (PDPageTree) PDPageTree.META.createNew();
            if (getParent() != null) {
                getParent().exchangeNode(this, newNode);
            }
            newNode.addNode(this);
            return newNode;
        }
        return null;
    }

    /**
     * Remove a node
     *
     * @param node The child node to remove
     */
    public void removeNode(PDPageNode node) {
        COSArray cosKids = cosGetField(DK_Kids).asArray();
        if (cosKids != null) {
            if (cosKids.remove(node.cosGetObject())) {
                incrementCount(-node.getCount());
                node.setParent(null);
            }
        }
    }

    /**
     * Set the number of pages.
     *
     * @param newCount number of pages to add
     */
    protected void setCount(int newCount) {
        int oldValue = getCount();
        setFieldInt(DK_Count, newCount);
        if (getParent() != null) {
            getParent().incrementCount(newCount - oldValue);
        }
    }
}
