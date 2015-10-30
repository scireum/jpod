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

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A stream based mapping from character codes to CID's.
 * <p>
 * The data in the stream defines the mapping from character codes to a font
 * number and a character selector. The data must follow the syntax defined in
 * Adobe Technical Note #5014, Adobe CMap and CIDFont Files Specification.
 */
abstract public class StreamBasedCMap extends CMap {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends CMap.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }
    }

    public static final COSName DK_CIDSystemInfo = COSName.constant("CIDSystemInfo");

    public static final COSName DK_CMapName = COSName.constant("CMapName");

    public static final COSName DK_UseCMap = COSName.constant("UseCMap");

    public static final COSName DK_WMode = COSName.constant("WMode");

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private Map<COSName, COSObject> definitions = new HashMap<COSName, COSObject>();

    /**
     * The maps from bytes to CID's
     */
    private List maps = new ArrayList();

    /**
     * The notdef maps
     */
    private List notdefs = new ArrayList();

    /**
     * The codespace ranges
     */
    private CMapRange[][] ranges = new CMapRange[4][0];

    /**
     * @param object
     */
    protected StreamBasedCMap(COSObject object) {
        super(object);
    }

    @Override
    public COSDictionary cosGetDict() {
        return cosGetStream().getDict();
    }

    protected void addDefinition(COSName key, COSObject value) {
        definitions.put(key, value);
    }

    protected void addMap(CMapMap map) {
        maps.add(map);
    }

    protected void addNotdef(CMapMap notdef) {
        notdefs.add(notdef);
    }

    protected void addRange(CMapRange range) {
        int count = range.getByteCount();
        CMapRange[] rangeArray = ranges[count];
        CMapRange[] tempArray = new CMapRange[rangeArray.length + 1];
        System.arraycopy(rangeArray, 0, tempArray, 0, rangeArray.length);
        tempArray[rangeArray.length] = range;
        ranges[count] = tempArray;
    }

    protected boolean checkPrefix(byte[] bytes, int count) {
        for (int k = 0; k < ranges.length; k++) {
            CMapRange[] rangeArray = ranges[k];
            for (int i = 0; i < rangeArray.length; i++) {
                CMapRange range = rangeArray[i];
                if (range.checkPrefix(bytes, count)) {
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean checkRange(byte[] bytes, int count) {
        if (count >= ranges.length) {
            return false;
        }
        CMapRange[] rangeArray = ranges[count];
        for (int i = 0; i < rangeArray.length; i++) {
            CMapRange range = rangeArray[i];
            if (range.checkRange(bytes, count)) {
                return true;
            }
        }
        return false;
    }

    protected void do_beginbfchar(CSOperation operation) {
        // no op, ignore size
    }

    protected void do_beginbfrange(CSOperation operation) {
        // no op, ignore size
    }

    protected void do_begincidchar(CSOperation operation) {
        // no op, ignore size
    }

    protected void do_begincidrange(CSOperation operation) {
        // no op, ignore size
    }

    /**
     *
     */
    protected void do_begincmap(CSOperation operation) {
        // this is a no op
    }

    /**
     *
     */
    protected void do_begincodespacerange(CSOperation operation) {
        // no op, ignore size
    }

    /**
     *
     */
    protected void do_beginnotdefchar(CSOperation operation) {
        // no op, ignore size
    }

    /**
     *
     */
    protected void do_beginnotdefrange(CSOperation operation) {
        // no op, ignore size
    }

    protected void do_def(CSOperation operation) {
        // define key / value association
        Iterator it = operation.getOperands();
        COSObject operand = COSNull.NULL;
        if (it.hasNext()) {
            operand = (COSObject) it.next();
        }
        COSDictionary dict = operand.asDictionary();
        if (dict == null) {
            COSName key = operand.asName();
            if (key == null) {
                return;
            }
            COSObject value = COSNull.NULL;
            if (it.hasNext()) {
                value = (COSObject) it.next();
            }
            addDefinition(key, value);
        } else {
            Iterator<Map.Entry<COSName, COSObject>> eit = dict.entryIterator();
            while (eit.hasNext()) {
                Map.Entry<COSName, COSObject> entry = eit.next();
                COSName key = entry.getKey();
                COSObject value = entry.getValue();
                addDefinition(key, value);
            }
        }
    }

    /**
     *
     */
    protected void do_endbfchar(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSObject destination = (COSObject) it.next();
            CMapCharMap map;
            if (destination instanceof COSString) {
                byte[] destBytes = ((COSString) destination).byteValue();
                if (destBytes.length > 2) {
                    // this is special to /ToUnicode maps
                    map = new CMapBFCharStringMap(start.byteValue(), destBytes);
                } else {
                    map = new CMapBFCharCodeMap(start.byteValue(), destBytes);
                }
            } else {
                map = new CMapBFCharNameMap(start.byteValue(), (COSName) destination);
            }
            addMap(map);
        }
    }

    /**
     *
     */
    protected void do_endbfrange(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSString end = (COSString) it.next();
            COSObject destination = (COSObject) it.next();
            CMapRangeMap map;
            if (destination instanceof COSString) {
                byte[] destBytes = ((COSString) destination).byteValue();
                if (destBytes.length > 2) {
                    // this is special to /ToUnicode maps
                    map = new CMapBFRangeStringMap(start.byteValue(),
                                                   end.byteValue(),
                                                   ((COSString) destination).byteValue());
                } else {
                    map = new CMapBFRangeCodeMap(start.byteValue(),
                                                 end.byteValue(),
                                                 ((COSString) destination).byteValue());
                }
            } else {
                COSArray array = destination.asArray();
                if (array.get(0) instanceof COSString) {
                    // this is special to /ToUnicode maps
                    map = new CMapBFRangeStringArrayMap(start.byteValue(), end.byteValue(), (COSArray) destination);
                } else {
                    map = new CMapBFRangeNameArrayMap(start.byteValue(), end.byteValue(), (COSArray) destination);
                }
            }
            addMap(map);
        }
    }

    /**
     *
     */
    protected void do_endcidchar(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSInteger destination = (COSInteger) it.next();
            CMapCharMap map = new CMapCIDCharCodeMap(start.byteValue(), destination.intValue());
            addMap(map);
        }
    }

    /**
     *
     */
    protected void do_endcidrange(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSString end = (COSString) it.next();
            COSInteger destination = (COSInteger) it.next();
            CMapRangeMap map = new CMapCIDRangeCodeMap(start.byteValue(), end.byteValue(), destination.intValue());
            addMap(map);
        }
    }

    /**
     *
     */
    protected void do_endcmap(CSOperation operation) {
        // this is a no op
    }

    /**
     *
     */
    protected void do_endcodespacerange(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString startString = (COSString) it.next();
            if (!it.hasNext()) {
                break;
            }
            COSString endString = (COSString) it.next();
            CMapRange range = new CMapRange(startString.byteValue(), endString.byteValue());
            addRange(range);
        }
    }

    /**
     *
     */
    protected void do_endnotdefchar(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSInteger destination = (COSInteger) it.next();
            CMapCharMap map = new CMapCIDCharCodeMap(start.byteValue(), destination.intValue());
            addNotdef(map);
        }
    }

    /**
     *
     */
    protected void do_endnotdefrange(CSOperation operation) {
        Iterator it = operation.getOperands();
        while (it.hasNext()) {
            COSString start = (COSString) it.next();
            COSString end = (COSString) it.next();
            COSInteger destination = (COSInteger) it.next();
            CMapRangeMap map = new CMapNotDefRangeMap(start.byteValue(), end.byteValue(), destination.intValue());
            addNotdef(map);
        }
    }

    /**
     *
     */
    protected void do_usecmap(CSOperation operation) {
        // todo 2 cmap not yet supported
    }

    /**
     *
     */
    protected void do_usefont(CSOperation operation) {
        // not needed and not yet supported
    }

    @Override
    public char[] getChars(int codepoint) {
        for (Iterator it = maps.iterator(); it.hasNext(); ) {
            CMapMap map = (CMapMap) it.next();
            char[] result = map.toChars(codepoint);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public int getDecoded(int codepoint) {
        for (Iterator it = maps.iterator(); it.hasNext(); ) {
            CMapMap map = (CMapMap) it.next();
            int result = map.toCID(codepoint);
            if (result != 0) {
                return result;
            }
        }
        return CharacterSelector.NotdefCID;
    }

    public COSObject getDefinition(COSName key) {
        COSObject result = definitions.get(key);
        if (result == null) {
            return COSNull.NULL;
        }
        return result;
    }

    @Override
    public int getEncoded(int character) {
        for (Iterator it = maps.iterator(); it.hasNext(); ) {
            CMapMap map = (CMapMap) it.next();
            int result = map.toCodepoint(character);
            if (result != 0) {
                return result;
            }
        }
        return CharacterSelector.NotdefCID;
    }

    @Override
    public int getNextDecoded(InputStream is) throws IOException {
        return getDecoded(getNextEncoded(is));
    }

    @Override
    public int getNextEncoded(InputStream is) throws IOException {
        is.mark(4);
        byte[] bytes = new byte[4];
        int count = 0;
        while (count < 4) {
            int nextByte = is.read();
            if (nextByte == -1) {
                return -1;
            }
            bytes[count++] = (byte) nextByte;
            if (checkRange(bytes, count)) {
                return toInt(bytes, 0, count);
            }
        }
        is.reset();
        // invalid code, try to determine number of bytes to consume
        bytes = new byte[4];
        count = 0;
        while (count < 4) {
            is.mark(1);
            int nextByte = is.read();
            if (nextByte == -1) {
                return -1;
            }
            bytes[count++] = (byte) nextByte;
            if (!checkPrefix(bytes, count)) {
                // todo 1 this is not correct - must pad to next range size
                is.reset();
                count--;
                return toInt(bytes, 0, count);
            }
        }
        return -1;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.content.IContentStreamVisitor#visitFromContentStream(
     * de.intarsys.pdf.content.CSContent)
     */
    protected void initializeFromContent(CSContent content) {
        int len = content.size();
        for (int i = 0; i < len; i++) {
            CSOperation operation = content.getOperation(i);
            initializeFromOperation(operation);
        }
    }

    protected void initializeFromOperation(CSOperation operation) {
        if (operation.matchesOperator(CMapOperator.CMO_beginbfchar)) {
            do_beginbfchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_beginbfrange)) {
            do_beginbfrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_begincidchar)) {
            do_begincidchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_begincidrange)) {
            do_begincidrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_begincmap)) {
            do_begincmap(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_begincodespacerange)) {
            do_begincodespacerange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_beginnotdefchar)) {
            do_beginnotdefchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_beginnotdefrange)) {
            do_beginnotdefrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endbfchar)) {
            do_endbfchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endbfrange)) {
            do_endbfrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endcidchar)) {
            do_endcidchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endcidrange)) {
            do_endcidrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endcmap)) {
            do_endcmap(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endcodespacerange)) {
            do_endcodespacerange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endnotdefchar)) {
            do_endnotdefchar(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_endnotdefrange)) {
            do_endnotdefrange(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_usecmap)) {
            do_usecmap(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_usefont)) {
            do_usefont(operation);
        } else if (operation.matchesOperator(CMapOperator.CMO_def)) {
            do_def(operation);
        } else {
            // unknown operator
        }
    }

    @Override
    public void putNextDecoded(OutputStream os, int character) throws IOException {
        // write cid value high byte first
        os.write((character >> 8) & 0xff);
        os.write(character & 0xff);
    }

    @Override
    public void putNextEncoded(OutputStream os, int codepoint) throws IOException {
        // write cid value high byte first
        os.write((codepoint >> 8) & 0xff);
        os.write(codepoint & 0xff);
    }
}
