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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * A TrueType based CID font.
 */
public class CIDFontType2 extends CIDFont {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends CIDFont.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new CIDFontType2(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName DK_CIDToGIDMap = COSName.constant("CIDToGIDMap");

    private byte[] mappingTable;

    public CIDFontType2(COSObject object) {
        super(object);
    }

    public COSObject cosGetCIDToGIDMap() {
        return cosGetField(DK_CIDToGIDMap);
    }

    @Override
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_CIDFontType2;
    }

    @Override
    public String getFontType() {
        return "TrueType";
    }

    @Override
    public int getGlyphIndex(int cid) {
        if (mappingTable == null) {
            COSObject map = cosGetCIDToGIDMap();
            if (map.isNull() || map instanceof COSName) {
                // identity
                // todo 2 speed up
                return cid;
            }
            mappingTable = ((COSStream) map).getDecodedBytes();
        }
        int index = cid << 1;
        int result;
        try {
            result = (mappingTable[index] & 0xff);
            result = (result << 8) + (mappingTable[index + 1] & 0xff);
        } catch (RuntimeException e) {
            // indexing error
            return 0;
        }
        return result;
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        cosSetField(DK_CIDToGIDMap, CIDToGIDMap.CN_Identity);
    }
}
