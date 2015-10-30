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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;

import java.io.IOException;

/**
 * A COSStream containing other COSObjects.
 */
public class COSObjectStream extends COSBasedObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new COSObjectStream(object);
        }
    }

    /**
     * The meta class instance
     */
    static public final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_Type_ObjStm = COSName.constant("ObjStm"); //$NON-NLS-1$

    public static final COSName DK_First = COSName.constant("First"); //$NON-NLS-1$

    public static final COSName DK_N = COSName.constant("N"); //$NON-NLS-1$

    public static final COSName DK_Extends = COSName.constant("Extends"); //$NON-NLS-1$

    private COSStream stream;

    private int[][] objectTable;

    private IRandomAccess randomAccess;

    protected COSObjectStream(COSObject stream) {
        super(((COSStream) stream).getDict());
        this.stream = (COSStream) stream;
    }

    public int getFirst() {
        return getFieldInt(DK_First, -1);
    }

    public int getN() {
        return getFieldInt(DK_N, 0);
    }

    private int getOffsetByIndex(int index, COSDocumentParser parser) throws IOException {
        if (objectTable == null) {
            objectTable = new int[getN()][2];
            getRandomAccess().seek(0);
            for (int i = 0; i < objectTable.length; i++) {
                objectTable[i][0] = parser.readInteger(getRandomAccess(), true);
                objectTable[i][1] = parser.readInteger(getRandomAccess(), true);
            }
        }
        return objectTable[index][1];
    }

    protected IRandomAccess getRandomAccess() throws IOException {
        if (randomAccess == null) {
            randomAccess = new RandomAccessByteArray(stream.getDecodedBytes());
        }
        return randomAccess;
    }

    public COSObject loadObject(int index, COSDocumentParser parser) throws IOException, COSLoadException {
        if (index >= getN()) {
            return null;
        }
        int offset = getOffsetByIndex(index, parser);
        getRandomAccess().seek(getFirst() + offset);
        return (COSObject) parser.parseElement(getRandomAccess());
    }

    public void parse(int index, COSDocumentParser parser) throws IOException, COSLoadException {
        if (index >= getN()) {
            return;
        }
        int offset = getOffsetByIndex(index, parser);
        getRandomAccess().seek(getFirst() + offset);
        parser.parseElement(getRandomAccess());
    }
}
