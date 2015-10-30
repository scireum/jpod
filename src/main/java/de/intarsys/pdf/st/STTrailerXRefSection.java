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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.writer.COSWriter;

import java.io.IOException;

/**
 * A section in a classical XRef.
 */
public class STTrailerXRefSection extends STXRefSection {
    private COSDictionary trailerDict;

    private STStreamXRefSection xRefStream;

    public STTrailerXRefSection(STDocument doc) {
        super(doc);
        this.trailerDict = COSDictionary.create();
    }

    public STTrailerXRefSection(STDocument doc, long offset) {
        super(doc, offset);
    }

    @Override
    public COSDictionary cosGetDict() {
        return trailerDict;
    }

    @Override
    public COSObject cosGetObject() {
        return trailerDict;
    }

    public void cosSetDict(COSDictionary pTrailerDict) {
        this.trailerDict = pTrailerDict;
    }

    @Override
    public STXRefSection createSuccessor() {
        STTrailerXRefSection newXRefSection = new STTrailerXRefSection(getDoc());
        // we must copy everything from trailer dict as it may have changes
        COSDictionary newTrailer = (COSDictionary) cosGetDict().copyShallow();
        newTrailer.remove(COSTrailer.DK_Prev);
        newTrailer.remove(STXRefSection.DK_XRefStm);
        newXRefSection.cosSetDict(newTrailer);
        newXRefSection.setPrevious(this);
        return newXRefSection;
    }

    @Override
    public AbstractXRefWriter getWriter(COSWriter cosWriter) {
        return new XRefTrailerWriter(cosWriter);
    }

    public STStreamXRefSection getXRefStream() {
        return xRefStream;
    }

    @Override
    protected boolean isStreamed() {
        return false;
    }

    @Override
    public COSObject load(int objectNumber, ISystemSecurityHandler securityHandler)
            throws IOException, COSLoadException {
        if (getXRefStream() != null) {
            COSObject loaded = getXRefStream().load(objectNumber, securityHandler);
            if (loaded != null) {
                return loaded;
            }
        }
        return super.load(objectNumber, securityHandler);
    }

    public void setXRefStream(STStreamXRefSection xRefStream) {
        this.xRefStream = xRefStream;
        setXRefStmOffset(xRefStream.getOffset());
    }
}
