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
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;

import java.io.IOException;
import java.util.Iterator;

/**
 * A XRef serializer to a XRef stream.
 */
public class XRefStreamWriter extends AbstractXRefWriter {
    /**
     *
     */
    class SearchVisitor implements IXRefEntryVisitor {
        private long highestOffset = 0;

        private int highestGeneration = 0;

        private void checkGeneration(int generation) {
            if (generation > highestGeneration) {
                highestGeneration = generation;
            }
        }

        private void checkOffset(long offset) {
            if (offset > highestOffset) {
                highestOffset = offset;
            }
        }

        public int getHighestGeneration() {
            return highestGeneration;
        }

        public long getHighestOffset() {
            return highestOffset;
        }

        public void visitFromCompressed(STXRefEntryCompressed entry) {
            checkOffset(entry.getStreamObjectNumber());
            checkGeneration(entry.getIndex());
        }

        public void visitFromFree(STXRefEntryFree entry) {
            checkOffset(entry.getNextFreeObjectNumber());
            checkGeneration(entry.getGenerationNumber());
        }

        public void visitFromOccupied(STXRefEntryOccupied entry) {
            checkOffset(entry.getOffset());
            checkGeneration(entry.getGenerationNumber());
        }
    }

    public static byte[] TYPE_FREE = {0};

    public static byte[] TYPE_OCCUPIED = {1};

    public static byte[] TYPE_COMPRESSED = {2};

    private int[] wSize;

    public XRefStreamWriter(COSWriter cosWriter) {
        super(cosWriter);
    }

    private int byteSizeOf(int number) {
        int mask = 0xff000000;
        for (int size = 4; size > 0; size--) {
            if ((number & mask) != 0) {
                return size;
            }
            mask = mask >> 8;
        }
        return 0;
    }

    @Override
    protected void finish(STXRefSection xRefSection) throws IOException {
        byte[] innerBytes = ((RandomAccessByteArray) getRandomAccess()).toByteArray();
        ((STStreamXRefSection) xRefSection).cosGetStream().setDecodedBytes(innerBytes);
        getCosWriter().writeIndirectObject(((STStreamXRefSection) xRefSection).cosGetStream().getIndirectObject());
        super.finish(xRefSection);
    }

    @Override
    protected byte[] getTypeCompressed() {
        return TYPE_COMPRESSED;
    }

    @Override
    protected byte[] getTypeFree() {
        return TYPE_FREE;
    }

    @Override
    protected byte[] getTypeOccupied() {
        return TYPE_OCCUPIED;
    }

    @Override
    protected void initialize(STXRefSection xRefSection) throws IOException {
        super.initialize(xRefSection);
        STStreamXRefSection xrefStream = (STStreamXRefSection) xRefSection;
        setRandomAccess(new RandomAccessByteArray(null));
        initWSize(xrefStream);
        xrefStream.setIndex(COSArray.create());
        COSIndirectObject io = xrefStream.cosGetStream().getIndirectObject();
        int objectNumber = io.getObjectNumber();
        int generationNumber = io.getGenerationNumber();
        if (objectNumber == -1) {
            int size = xrefStream.getSize();
            objectNumber = size;
            generationNumber = 0;
            xrefStream.cosGetStream().getIndirectObject().setKey(objectNumber, generationNumber);
            xrefStream.setSize(size + 1);
        }
        xRefSection.addEntry(new STXRefEntryOccupied(objectNumber, generationNumber, xRefSection.getOffset()));
    }

    private void initWSize(STXRefSection xRefSection) {
        // search highest 2nd column
        SearchVisitor search = new SearchVisitor();

        for (Iterator i = xRefSection.entryIterator(); i.hasNext(); ) {
            STXRefEntry entry = ((STXRefEntry) i.next());
            try {
                entry.accept(search);
            } catch (XRefEntryVisitorException e) {
                // won't happen
            }
        }
        wSize = new int[3];
        wSize[0] = 1;
        wSize[1] = byteSizeOf((int) search.getHighestOffset());
        wSize[2] = byteSizeOf(search.getHighestGeneration());

        COSArray wArray = COSArray.create(3);
        for (int i = 0; i < 3; i++) {
            wArray.add(COSInteger.create(wSize[i]));
        }
        ((STStreamXRefSection) xRefSection).setW(wArray);
    }

    @Override
    protected void visitFromSubsection(STXRefSubsection section) {
        COSArray index = ((STStreamXRefSection) section.getXRefSection()).getIndex();
        index.add(COSInteger.create(section.getStart()));
        index.add(COSInteger.create(section.getSize()));
    }

    private void write(int data, int numBytes) throws IOException {
        switch (numBytes) {
            case 4:
                getRandomAccess().write((data & 0xFF000000) >> 24);
            case 3:
                getRandomAccess().write((data & 0x00FF0000) >> 16);
            case 2:
                getRandomAccess().write((data & 0x0000FF00) >> 8);
            case 1:
                getRandomAccess().write(data & 0x000000FF);
                break;
            default:

                // should not happen
        }
    }

    @Override
    protected void write(int col1, int col2, byte[] type) throws IOException {
        getRandomAccess().write(type);
        write(col1, wSize[1]);
        write(col2, wSize[2]);
    }
}
