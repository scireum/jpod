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
package de.intarsys.pdf.font;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class CIDWidthMap extends COSBasedObject {

    /**
     * The meta class implementation
     */
    public static class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new CIDWidthMap(object);
        }

        @Override
        protected COSObject doCreateCOSObject() {
            return COSArray.create();
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private List<CIDWidthMapEntry> entries;

    protected CIDWidthMap(COSObject object) {
        super(object);
    }

    public void addWidth(int cid, int width) {
        entries.add(new CIDWidthMapEntry(cid, cid, width));
        cosGetArray().add(COSInteger.create(cid));
        cosGetArray().add(COSInteger.create(cid));
        cosGetArray().add(COSInteger.create(width));
    }

    protected void createMap(COSArray array) {
        entries = new ArrayList<>();
        if (array != null) {
            for (Iterator itMap = array.iterator(); itMap.hasNext(); ) {
                COSNumber element1 = ((COSObject) itMap.next()).asNumber();
                if (element1 == null || !itMap.hasNext()) {
                    break;
                }
                COSObject element2 = (COSObject) itMap.next();
                int start = element1.intValue();
                int stop = start;
                if (element2 instanceof COSNumber) {
                    stop = ((COSNumber) element2).intValue();
                    if (!itMap.hasNext()) {
                        break;
                    }
                    COSObject element3 = (COSObject) itMap.next();
                    int width = ((COSNumber) element3).intValue();
                    entries.add(new CIDWidthMapEntry(start, stop, width));
                } else if (element2 instanceof COSArray) {
                    COSArray widths = (COSArray) element2;
                    for (Iterator itWidths = widths.iterator(); itWidths.hasNext(); ) {
                        COSNumber widthObject = ((COSObject) itWidths.next()).asNumber();
                        int width = 0;
                        if (widthObject != null) {
                            width = widthObject.intValue();
                        }
                        entries.add(new CIDWidthMapEntry(start, stop, width));
                        start++;
                        stop++;
                    }
                }
            }
        }
    }

    public int getWidth(int cid) {
        for (Iterator<CIDWidthMapEntry> it = entries.iterator(); it.hasNext(); ) {
            CIDWidthMapEntry entry = it.next();
            if (entry.getStart() <= cid) {
                if (entry.getStop() >= cid) {
                    return entry.getWidth();
                }
            }
        }
        return -1;
    }

    public List<CIDWidthMapEntry> getEntries() {
        return entries;
    }

    @Override
    protected void initializeFromCos() {
        super.initializeFromCos();
        createMap(cosGetArray());
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        entries = new ArrayList();
    }
}
