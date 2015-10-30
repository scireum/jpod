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

import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.parser.COSLoadException;

import java.io.IOException;

/**
 * An entry in a XRef stream.
 */
public class STXRefEntryCompressed extends STXRefEntry {

    private final int streamObjectNumber;

    private final short streamGenerationNumber;

    private int index;

    public STXRefEntryCompressed(int objectNumber,
                                 int generationNumber,
                                 int streamObjectNumber,
                                 int streamGenerationNumber,
                                 int indexOfObject) {
        super(objectNumber, generationNumber);
        this.streamObjectNumber = streamObjectNumber;
        this.streamGenerationNumber = (short) streamGenerationNumber;
        this.index = indexOfObject;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#accept(de.intarsys.pdf.storage.IXRefEntryVisitor)
     */
    @Override
    public void accept(IXRefEntryVisitor visitor) throws XRefEntryVisitorException {
        visitor.visitFromCompressed(this);
    }

    @Override
    public STXRefEntry copy() {
        return new STXRefEntryCompressed(getObjectNumber(),
                                         getGenerationNumber(),
                                         getStreamObjectNumber(),
                                         getStreamGenerationNumber(),
                                         getIndex());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#fill(int)
     */
    @Override
    public STXRefEntryOccupied fill(int pos) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#getColumn1()
     */
    @Override
    public long getColumn1() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#getColumn2()
     */
    @Override
    public int getColumn2() {
        return 0;
    }

    public int getIndex() {
        return index;
    }

    public int getStreamGenerationNumber() {
        return streamGenerationNumber & 0xffff;
    }

    public int getStreamObjectNumber() {
        return streamObjectNumber;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#isFree()
     */
    @Override
    public boolean isFree() {
        return false;
    }

    @Override
    public COSObject load(STDocument doc, ISystemSecurityHandler securityHandler) throws IOException, COSLoadException {
        COSIndirectObject streamObjectRef =
                doc.getObjectReference(getStreamObjectNumber(), getStreamGenerationNumber());
        COSObject objStrmIO = streamObjectRef.dereference();
        if ((objStrmIO != null) && !objStrmIO.isNull()) {
            COSObjectStream strm = (COSObjectStream) COSObjectStream.META.createFromCos(objStrmIO);
            return strm.loadObject(index, doc.getParser());
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.STXRefEntry#unlink()
     */
    @Override
    protected void unlink() {
        //
    }
}
