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

import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.string.StringTools;

import java.io.IOException;
import java.util.Iterator;

/**
 * An abstract superclass for implementing a XRef serializer.
 */
public abstract class AbstractXRefWriter implements IXRefEntryVisitor {
    private COSWriter cosWriter;

    private IRandomAccess randomAccess;

    public static final byte[] STARTXREF = "startxref".getBytes(); //$NON-NLS-1$

    protected IRandomAccess getRandomAccess() {
        return randomAccess;
    }

    protected void setRandomAccess(IRandomAccess randomAccess) {
        this.randomAccess = randomAccess;
    }

    public AbstractXRefWriter(COSWriter cosWriter) {
        this.cosWriter = cosWriter;
    }

    protected void finish(STXRefSection xRefSection) throws IOException {
        writeStartXRef(xRefSection.getOffset());
    }

    protected void writeStartXRef(long offset) throws IOException {
        getCosWriter().write(AbstractXRefWriter.STARTXREF);
        getCosWriter().writeEOL();
        getCosWriter().write(StringTools.toByteArray(Long.toString(offset)));
        getCosWriter().writeEOL();
    }

    public COSWriter getCosWriter() {
        return cosWriter;
    }

    protected abstract byte[] getTypeCompressed();

    protected abstract byte[] getTypeFree();

    protected abstract byte[] getTypeOccupied();

    protected void initialize(STXRefSection xRefSection) throws IOException {
        xRefSection.setOffset(getCosWriter().getRandomAccess().getOffset());
        int size = xRefSection.getMaxObjectNumber();
        if (xRefSection.getPrevious() != null) {
            size = Math.max(xRefSection.getPrevious().getSize(), size);
        }
        xRefSection.setSize(size);
    }

    protected void visitFromSection(STXRefSection xRefSection) throws IOException {
        Iterator i = xRefSection.subsectionIterator();
        while (i.hasNext()) {
            STXRefSubsection section = (STXRefSubsection) i.next();
            visitFromSubsection(section);
            for (Iterator ie = section.getEntries().iterator(); ie.hasNext(); ) {
                try {
                    ((STXRefEntry) ie.next()).accept(this);
                } catch (XRefEntryVisitorException e) {
                    // in this context the exception type is always an
                    // IOException
                    throw (IOException) e.getCause();
                }
            }
        }
    }

    public void visitFromCompressed(STXRefEntryCompressed entry) throws XRefEntryVisitorException {
        try {
            write(entry.getStreamObjectNumber(), entry.getIndex(), getTypeCompressed());
        } catch (IOException e) {
            throw new XRefEntryVisitorException(e);
        }
    }

    public void visitFromFree(STXRefEntryFree entry) throws XRefEntryVisitorException {
        try {
            write(entry.getNextFreeObjectNumber(), entry.getGenerationNumber(), getTypeFree());
        } catch (IOException e) {
            throw new XRefEntryVisitorException(e);
        }
    }

    public void visitFromOccupied(STXRefEntryOccupied entry) throws XRefEntryVisitorException {
        try {
            write((int) entry.getOffset(), entry.getGenerationNumber(), getTypeOccupied());
        } catch (IOException e) {
            throw new XRefEntryVisitorException(e);
        }
    }

    protected abstract void visitFromSubsection(STXRefSubsection section) throws IOException;

    protected abstract void write(int col1, int col2, byte[] type) throws IOException;

    public void writeXRef(STXRefSection xRefSection) throws IOException {
        initialize(xRefSection);
        visitFromSection(xRefSection);
        finish(xRefSection);
    }
}
