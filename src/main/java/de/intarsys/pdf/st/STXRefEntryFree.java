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

import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;

/**
 * Represents a free object entry in a pdf xref table.
 */
public class STXRefEntryFree extends STXRefEntry {
    /**
     * a link to the next free XRef entry
     */
    private STXRefEntryFree next = this;

    /**
     * a link to the prev free XRef entry
     */
    private STXRefEntryFree prev = this;

    private int nextFreeObject;

    public STXRefEntryFree(int objectNumber, int generationNumber, int nextFreeObject) {
        super(objectNumber, generationNumber);
        this.nextFreeObject = nextFreeObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#accept(de.intarsys.pdf.storage.IXRefEntryVisitor)
     */
    @Override
    public void accept(IXRefEntryVisitor visitor) throws XRefEntryVisitorException {
        visitor.visitFromFree(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#copy()
     */
    @Override
    public STXRefEntry copy() {
        return new STXRefEntryFree(getObjectNumber(), getGenerationNumber(), getNextFreeObjectNumber());
    }

    /**
     * Add a new free entry in the linked list of free entries. The linked list
     * is formed by the head entry with index 0. This entry and all subsequent
     * hold a reference to their successor and predecessor. Adding a new entry
     * means that we look up the entry in the linked list, starting at the head,
     * that has the highest index smaller than the index of the new entry. The
     * new entry is inserted after that position in the linked list.
     *
     * @param entry The new entry to insert in the linked list.
     */
    public void enqueue(STXRefEntryFree entry) {
        int prevIndex = getPrev().getObjectNumber();
        if ((prevIndex == 0) || (prevIndex < entry.getObjectNumber())) {
            entry.setNext(this);
            entry.setPrev(getPrev());
            getPrev().setNext(entry);
            this.setPrev(entry);
        } else {
            getPrev().enqueue(entry);
        }
    }

    @Override
    public STXRefEntryOccupied fill(int pos) {
        unlink();

        /*
         * STXRefEntry newEntry = STXRefEntryOccupied.create( getObjectNumber(),
         * pos, getGenerationNumber() ); return newEntry.fill(pos);
         */
        return null;
    }

    @Override
    public long getColumn1() {
        return getNextFreeObjectNumber();
    }

    @Override
    public int getColumn2() {
        return getGenerationNumber();
    }

    protected STXRefEntryFree getNext() {
        return next;
    }

    public int getNextFreeObjectNumber() {
        return nextFreeObject;
    }

    protected STXRefEntryFree getPrev() {
        return prev;
    }

    @Override
    public boolean isFree() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#loadObject(de.intarsys.pdf.cos.COSIndirectObject)
     */
    @Override
    public COSObject load(STDocument doc, ISystemSecurityHandler securityHandler) {
        return null;
    }

    private void setNext(STXRefEntryFree free) {
        next = free;
    }

    private void setPrev(STXRefEntryFree free) {
        prev = free;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.writer.XRefEntry#unlink()
     */
    @Override
    protected void unlink() {
        getPrev().setNext(getNext());
        getNext().setPrev(getPrev());
        setPrev(this);
        setNext(this);
    }
}
