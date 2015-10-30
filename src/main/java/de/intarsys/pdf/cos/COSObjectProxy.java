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
package de.intarsys.pdf.cos;

import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.randomaccess.IRandomAccess;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * A "proxy object" used to intercept the visitor callback from the serializer.
 * <p>
 * You can use this for example to implement an elegant solution for PDF
 * signing, which is defined on the serialized bytes, except some defined byte
 * range. The proxy mechanics allow for creating a placeholder for the signature
 * data, filled in at the correct position later by some post processing.
 */
abstract public class COSObjectProxy extends COSCompositeObject implements Cloneable {
    /**
     * The position when the signal from the serializer was detected
     */
    private long position = -1;

    private int length;

    private COSObject object;

    /**
     * Create a {@link COSObjectProxy}
     */
    public COSObjectProxy() {
        //
    }

    @Override
    public Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        if (object != null) {
            return object.accept(visitor);
        }
        if (visitor instanceof ICOSProxyVisitor) {
            return ((ICOSProxyVisitor) visitor).visitFromProxy(this);
        }
        return null;
    }

    @Override
    public Iterator basicIterator() {
        if (object == null) {
            return Collections.emptyIterator();
        }
        return object.basicIterator();
    }

    @Override
    protected String basicToString() {
        if (object == null) {
            return "COSObjectProxy: empty"; //$NON-NLS-1$
        }
        return object.stringValue();
    }

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    protected COSObject copyBasic() {
        COSObjectProxy proxy = (COSObjectProxy) clone();
        proxy.reserveData(getLength());
        return proxy;
    }

    @Override
    public COSObject copyDeep(Map copied) {
        if (object != null) {
            return object.copyDeep(copied);
        }
        return super.copyDeep(copied);
    }

    @Override
    public COSObject copyShallow() {
        if (object != null) {
            return object.copyShallow();
        }
        return super.copyShallow();
    }

    /**
     * The wrapped {@link COSObject}, according to COS level semantics. This
     * method returns never null.
     *
     * @return The wrapped {@link COSObject}, according to COS level semantics.
     */
    public COSObject cosGetObject() {
        return object == null ? COSNull.NULL : object;
    }

    /**
     * Realize the represented object.
     *
     * @param randomAccessData
     * @return the represented object
     * @throws IOException
     */
    abstract protected COSObject createCOSObject(IRandomAccess randomAccessData) throws IOException;

    @Override
    public COSObject dereference() {
        return (object == null) ? this : object;
    }

    /**
     * Attention: The user must handle encryption by himself. The COSWriter
     * doesn't handle encryption in this state.
     *
     * @param writer
     * @throws IOException
     */
    public void ended(COSWriter writer) throws IOException {
        if (object != null) {
            // already done
            return;
        }
        IOException createObjException = null;
        IRandomAccess data = writer.getRandomAccess();
        data.mark();
        try {
            try {
                object = createCOSObject(data);
            } catch (IOException e) {
                createObjException = e;
            }
            data.seek(getPosition());
            if (object != null) {
                writer.writeObject(object);
            } else {
                writer.writeObject(COSNull.create());
            }
            long endPosition = getPosition() + getLength();
            if (data.getOffset() < endPosition) {
                long dif = endPosition - data.getOffset();
                byte[] padding = new byte[(int) dif];
                Arrays.fill(padding, (byte) ' ');
                writer.write(padding);
            } else if (data.getOffset() > endPosition) {
                throw new IOException("Destroyed document, wrote more bytes than reserved!"); //$NON-NLS-1$
            }
            if (createObjException != null) {
                throw createObjException;
            }
        } finally {
            data.reset();
        }
    }

    /**
     * The length within the data stream for the serialization of this.
     *
     * @return The length within the data stream for the serialization of this.
     */
    public int getLength() {
        return length;
    }

    /**
     * The object represented by the proxy.
     *
     * @return The object represented by the proxy.
     */
    public COSObject getObject() {
        return object;
    }

    /**
     * The position within the data stream for the serialization of this.
     *
     * @return The position within the data stream for the serialization of
     * this.
     */
    public long getPosition() {
        return position;
    }

    @Override
    public Iterator<COSObject> iterator() {
        if (object == null) {
            return Collections.emptyIterator();
        }
        return object.iterator();
    }

    @Override
    public boolean mayBeSwapped() {
        return false;
    }

    /**
     * Reserve <code>length</code> bytes for the serialization of this.
     *
     * @param pLength Number of bytes to be reserved.
     */
    public void reserveData(int pLength) {
        this.length = pLength;
    }

    @Override
    public Object saveState() {
        return null;
    }

    protected void setObject(COSObject object) {
        this.object = object;
    }

    /**
     * Assign the position within the data stream.
     *
     * @param position The position within the data stream.
     */
    public void setPosition(long position) {
        this.position = position;
    }
}
