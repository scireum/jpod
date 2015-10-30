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
package de.intarsys.pdf.st;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.writer.COSWriter;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A section in a XRef.
 * <p>
 * The XRef allows random access to the objects in the PDF file.
 * <p>
 * A XRef section is the part of a pdf document starting with a "xref" token. It
 * consists of several, non contiguous subsections, one additional for each
 * incremental update.
 */
public abstract class STXRefSection {
    public static final COSName DK_XRefStm = COSName.constant("XRefStm"); //$NON-NLS-1$

    private STXRefSubsection xRefSubsection;

    private long offset = -1;

    private STDocument doc;

    private STXRefSection previous;

    protected STXRefSection(STDocument doc) {
        this(doc, -1);
    }

    protected STXRefSection(STDocument doc, long offset) {
        this.doc = doc;
        this.offset = offset;
        xRefSubsection = new STXRefSubsection(this, 0);
        xRefSubsection.addEntry(new STXRefEntryFree(0, 65535, 0));
    }

    public void addEntry(STXRefEntry entry) {
        STXRefSubsection prev = null;
        STXRefSubsection current = getXRefSubsection();
        int number = entry.getObjectNumber();
        while (current != null) {
            int start = current.getStart();
            int stop = current.getStop();
            if (number < start) {
                STXRefSubsection newSection = new STXRefSubsection(this, number);
                newSection.setNext(current);
                if (prev != null) {
                    prev.setNext(newSection);
                } else {
                    setXRefSubsection(newSection);
                }
                newSection.addEntry(entry);
                return;
            }
            if (start <= number && number <= stop) {
                current.addEntry(entry);
                return;
            }
            prev = current;
            current = current.getNext();
        }
        STXRefSubsection newSection = new STXRefSubsection(this, number);
        newSection.setNext(null);
        if (prev != null) {
            prev.setNext(newSection);
        } else {
            setXRefSubsection(newSection);
        }
        newSection.addEntry(entry);
    }

    public boolean contains(int number) {
        STXRefSubsection current = getXRefSubsection();
        while (current != null) {
            if (number < current.getStart()) {
                return false;
            }
            if (current.getStart() <= number && number < current.getStop()) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    /**
     * The "trailer" dictionary associated with the XRef section.
     *
     * @return The "trailer" dictionary associated with the XRef section.
     */
    public abstract COSDictionary cosGetDict();

    abstract public COSObject cosGetObject();

    protected void createNewSubsection(int pStart) {
    }

    public abstract STXRefSection createSuccessor();

    public Iterator entryIterator() {
        return new Iterator() {
            private Iterator currentIterator;

            private STXRefSubsection myNext = getXRefSubsection();

            public boolean hasNext() {
                if (currentIterator != null && currentIterator.hasNext()) {
                    return true;
                }
                if (myNext != null) {
                    currentIterator = myNext.getEntries().iterator();
                    myNext = myNext.getNext();
                    // must enter recursive!
                    return hasNext();
                }
                return false;
            }

            public Object next() {
                if (hasNext()) {
                    return currentIterator.next();
                }
                throw new NoSuchElementException();
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public STDocument getDoc() {
        return doc;
    }

    public STXRefEntry getEntry(int number) {
        STXRefSubsection current = getXRefSubsection();
        while (current != null) {
            if (number < current.getStart()) {
                return null;
            }
            if (current.getStart() <= number && number < current.getStop()) {
                return current.getEntry(number);
            }
            current = current.getNext();
        }
        return null;
    }

    public COSArray getID() {
        return cosGetDict().get(COSTrailer.DK_ID).asArray();
    }

    public int getIncrementalCount() {
        if (getPrevious() == null) {
            return 1;
        } else {
            // todo maybe we should check linearization in another way
            if (cosGetDict().get(COSTrailer.DK_Root).isNull()) {
                // part of linearized structure
                return getPrevious().getIncrementalCount();
            } else {
                return getPrevious().getIncrementalCount() + 1;
            }
        }
    }

    public int getMaxObjectNumber() {
        STXRefSubsection current = getXRefSubsection();
        while (current != null) {
            if (current.getNext() != null) {
                current = current.getNext();
            } else {
                return current.getStop();
            }
        }
        return 0;
    }

    public long getOffset() {
        return offset;
    }

    public STXRefSection getPrevious() {
        return previous;
    }

    /**
     * @return offset of previous trailer dict or -1 if none exists
     */
    public int getPreviousOffset() {
        COSInteger value = cosGetDict().get(COSTrailer.DK_Prev).asInteger();
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    protected int getPreviousXRefStmOffset() {
        COSInteger value = cosGetDict().get(DK_XRefStm).asInteger();
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    /**
     * The total number of indirect objects in the document.
     *
     * @return The total number of indirect objects in the document.
     */
    public int getSize() {
        COSInteger value = cosGetDict().get(COSTrailer.DK_Size).asInteger();
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    /**
     * The object number of the first object in this section.
     *
     * @return The object number of the first object in this section.
     */
    public int getStart() {
        return getXRefSubsection().getStart();
    }

    public abstract AbstractXRefWriter getWriter(COSWriter cosWriter);

    protected int getXRefStmOffset() {
        COSInteger value = cosGetDict().get(DK_XRefStm).asInteger();
        if (value == null) {
            return -1;
        }
        return value.intValue();
    }

    /**
     * The first subsection in this section. All other subsections are
     * implemented as a linked list.
     *
     * @return The first subsection in this section.
     */
    public STXRefSubsection getXRefSubsection() {
        return xRefSubsection;
    }

    protected abstract boolean isStreamed();

    public COSObject load(int objectNumber, ISystemSecurityHandler securityHandler)
            throws IOException, COSLoadException {
        if (contains(objectNumber)) {
            return getEntry(objectNumber).load(getDoc(), securityHandler);
        }
        if (getPrevious() != null) {
            return getPrevious().load(objectNumber, securityHandler);
        }
        return null;
    }

    protected void setCOSDoc(COSDocument doc) {
        doc.add(cosGetObject());
    }

    protected void setID(COSArray id) {
        cosGetDict().put(COSTrailer.DK_ID, id);
    }

    protected void setOffset(long offset) {
        this.offset = offset;
    }

    protected void setPrevious(STXRefSection xRefSection) {
        this.previous = xRefSection;
        if (getPreviousOffset() != xRefSection.getOffset()) {
            setPreviousOffset(xRefSection.getOffset());
        }
    }

    protected void setPreviousOffset(long offset) {
        cosGetDict().put(COSTrailer.DK_Prev, COSInteger.create((int) offset));
    }

    protected void setSize(int size) {
        cosGetDict().put(COSTrailer.DK_Size, COSInteger.create(size));
    }

    protected void setXRefStmOffset(long xrefStmOffset) {
        cosGetDict().put(DK_XRefStm, COSInteger.create((int) xrefStmOffset));
    }

    protected void setXRefSubsection(STXRefSubsection newXRef) {
        this.xRefSubsection = newXRef;
        STXRefSubsection current = getXRefSubsection();
        while (current != null) {
            current.setXRefSection(this);
            current = current.getNext();
        }
    }

    public Iterator subsectionIterator() {
        return new Iterator() {
            private STXRefSubsection current = getXRefSubsection();

            public boolean hasNext() {
                return current != null;
            }

            public Object next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                STXRefSubsection result = current;
                current = current.getNext();
                return result;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
