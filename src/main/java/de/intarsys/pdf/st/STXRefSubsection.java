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

import java.util.ArrayList;
import java.util.List;

/**
 * A XRef subsection in a PDF document.
 * <p>
 * A XRef section consists of at least one subsection. The subsections describe
 * the objects that have changed with regard to the previous document version.
 * <p>
 * Each subsection contains entries for a contiguous range of objects. The
 * serialized form starts with 2 number, the object number of the first entry
 * and the number of entries. Following this there is one line for each entry in
 * the form
 * <p>
 * In Use entry: {@code
 * offset[10] " " generation[5] " " n eol[2]
 * }
 * <p>
 * Free entry: {@code
 * next free[10] " " generation[5] " " f eol[2]
 * }
 */
public class STXRefSubsection {
    private STXRefSubsection next;

    private List entries;

    private int start;

    // record size for performance sake
    private int size;

    private STXRefSection xRefSection;

    protected STXRefSubsection(int start) {
        this(null, start);
    }

    public STXRefSubsection(STXRefSection xRefSection, int start) {
        this.xRefSection = xRefSection;
        this.start = start;
        this.entries = new ArrayList();
        this.size = 0;
    }

    protected void addEntry(STXRefEntry entry) {
        int number = entry.getObjectNumber();
        if (number < getStart()) {
            throw new IllegalArgumentException("can't add object with number " + number);
        }
        int end = start + size;
        if (start <= number && number < end) {
            entries.set(number - start, entry);
            return;
        }
        if (number == end) {
            // fits to end of list
            entries.add(entry);
            size++;
            checkNext();
            return;
        }
        throw new IllegalArgumentException("can't add object with number " + number);
    }

    protected void checkNext() {
        if (getNext() == null) {
            return;
        }
        if (getSize() == 0) {
            start = getNext().getStart();
            mergeWithNext();
            return;
        }
        if ((start + getSize()) == getNext().getStart()) {
            mergeWithNext();
        }
    }

    protected List getEntries() {
        return entries;
    }

    protected STXRefEntry getEntry(int objectNumber) {
        return (STXRefEntry) entries.get(objectNumber - start);
    }

    protected STXRefSubsection getNext() {
        return next;
    }

    public int getSize() {
        return size;
    }

    public int getStart() {
        return start;
    }

    public int getStop() {
        return start + getSize();
    }

    protected STXRefSection getXRefSection() {
        return xRefSection;
    }

    protected boolean isInSection(int objectNumber) {
        return ((objectNumber >= start) && (objectNumber < (start + getSize())));
    }

    protected void mergeWithNext() {
        entries.addAll(getNext().getEntries());
        size = entries.size();
        setNext(getNext().getNext());
    }

    protected void setNext(STXRefSubsection next) {
        this.next = next;
    }

    protected void setXRefSection(STXRefSection xRefSection) {
        this.xRefSection = xRefSection;
    }
}
