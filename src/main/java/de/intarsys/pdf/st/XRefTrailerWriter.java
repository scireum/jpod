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
import de.intarsys.tools.string.StringTools;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * A XRef serializer to the classical XRef format.
 */
public class XRefTrailerWriter extends AbstractXRefWriter {
    public static final byte[] TYPE_FREE = "f".getBytes();

    public static final byte[] TYPE_OCCUPIED = "n".getBytes();

    public static final NumberFormat FORMAT_XREF_GENERATION = new DecimalFormat("00000");

    public static final NumberFormat FORMAT_XREF_OFFSET = new DecimalFormat("0000000000");

    public static final byte[] XREF = "xref".getBytes();

    public XRefTrailerWriter(COSWriter cosWriter) {
        super(cosWriter);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#initialize()
     */
    @Override
    protected void initialize(STXRefSection xRefSection) throws IOException {
        super.initialize(xRefSection);
        setRandomAccess(getCosWriter().getRandomAccess());
        getRandomAccess().write(XRefTrailerWriter.XREF);
        getRandomAccess().write(COSWriter.EOL);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#finish()
     */
    @Override
    protected void finish(STXRefSection xRefSection) throws IOException {
        getCosWriter().write(COSWriter.TRAILER);
        getCosWriter().writeEOL();
        getCosWriter().writeObject(xRefSection.cosGetDict());
        super.finish(xRefSection);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.IXRefEntryVisitor#visitFromCompressed(de.intarsys.pdf.storage.STXRefEntryCompressed)
     */
    @Override
    public void visitFromCompressed(STXRefEntryCompressed entry) throws XRefEntryVisitorException {
        // not supported, so write a free entry
        try {
            write(entry.getObjectNumber(), 65535, getTypeFree());
        } catch (IOException e) {
            throw new XRefEntryVisitorException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#write(int, int, byte[])
     */
    @Override
    protected void write(int col1, int col2, byte[] type) throws IOException {
        String stCol1 = XRefTrailerWriter.FORMAT_XREF_OFFSET.format(col1);
        String stCol2 = XRefTrailerWriter.FORMAT_XREF_GENERATION.format(col2);
        getRandomAccess().write(StringTools.toByteArray(stCol1));
        getRandomAccess().write(COSWriter.SPACE);
        getRandomAccess().write(StringTools.toByteArray(stCol2));
        getRandomAccess().write(COSWriter.SPACE);
        getRandomAccess().write(type);
        getRandomAccess().write(COSWriter.CRLF);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#visitFromSection(de.intarsys.pdf.storage.STXRefSubsection)
     */
    @Override
    protected void visitFromSubsection(STXRefSubsection section) throws IOException {
        getRandomAccess().write(StringTools.toByteArray(Integer.toString(section.getStart())));
        getRandomAccess().write(COSWriter.SPACE);
        getRandomAccess().write(StringTools.toByteArray(Integer.toString(section.getSize())));
        getRandomAccess().write(COSWriter.EOL);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#getTypeCompressed()
     */
    @Override
    protected byte[] getTypeCompressed() {
        return TYPE_FREE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#getTypeFree()
     */
    @Override
    protected byte[] getTypeFree() {
        return TYPE_FREE;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.storage.AbstractXRefWriter#getTypeOccupied()
     */
    @Override
    protected byte[] getTypeOccupied() {
        return TYPE_OCCUPIED;
    }
}
