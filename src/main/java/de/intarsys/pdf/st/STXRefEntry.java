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
import de.intarsys.pdf.parser.COSLoadException;

import java.io.IOException;

/**
 * Abstract superclass for a XRef entry.
 * <p>
 * The XRef entry describes an object slot in a XRef. The entry consists of an
 * offset in the file, a generation number and a token indicating if this entry
 * is free.
 */
public abstract class STXRefEntry implements Comparable {

    private final int objectNumber;

    private final short generationNumber;

    protected STXRefEntry(int objectNumber, int generationNumber) {
        this.objectNumber = objectNumber;
        this.generationNumber = (short) generationNumber;
    }

    public abstract void accept(IXRefEntryVisitor visitor) throws XRefEntryVisitorException;

    public int compareTo(Object obj) {
        if (obj instanceof STXRefEntry) {
            return (getObjectNumber() - ((STXRefEntry) obj).getObjectNumber());
        }
        return -1;
    }

    public abstract STXRefEntry copy();

    public abstract STXRefEntryOccupied fill(int pos);

    public abstract long getColumn1();

    public abstract int getColumn2();

    public int getGenerationNumber() {
        return generationNumber & 0xffff;
    }

    public int getObjectNumber() {
        return objectNumber;
    }

    abstract public boolean isFree();

    public abstract COSObject load(STDocument doc, ISystemSecurityHandler securityHandler)
            throws IOException, COSLoadException;

    @Override
    public String toString() {
        return getObjectNumber() + " " + getGenerationNumber() + " R"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    abstract protected void unlink();
}
