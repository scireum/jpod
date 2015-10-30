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
package de.intarsys.pdf.fd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the abstract superclass for all complex FD level objects.
 */
abstract public class FDObject extends COSBasedObject {
    /**
     * The meta class implementation
     */
    static public abstract class MetaClass extends COSBasedObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }
    }

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected FDObject(COSObject object) {
        super(object);
    }

    protected void cosMoveField(COSName key, COSDictionary source, COSDictionary dest) {
        // review warum move
        COSObject o = source.get(key);
        if (!o.isNull()) {
            source.remove(key);
            dest.put(key, o);
        }
    }

    protected List getFDObjects(COSName key, COSBasedObject.MetaClass metaclass) {
        COSArray array = cosGetField(key).asArray();
        if (array == null) {
            return null;
        }
        List result = new ArrayList();
        for (Iterator i = array.iterator(); i.hasNext(); ) {
            result.add(metaclass.createFromCos((COSObject) i.next()));
        }
        return result;
    }

    protected float[] getFloatArray(COSName key) {
        COSArray array = cosGetField(key).asArray();
        if (array == null) {
            return null;
        }
        float[] result = new float[array.size()];
        for (int i = 0; i < array.size(); i++) {
            COSNumber num = (COSNumber) array.get(i);
            result[i] = num.floatValue();
        }
        return result;
    }

    protected void setFDObjects(COSName key, List list, boolean emptyArrayIfListEmpty) {
        // todo 2 ugly
        if ((list == null) || list.isEmpty()) {
            if (emptyArrayIfListEmpty) {
                cosSetField(key, COSArray.create());
            } else {
                cosRemoveField(key);
            }
            return;
        }

        // todo 2 reuse existing array ?
        COSArray a = COSArray.create();
        cosSetField(key, a);

        for (Iterator i = list.iterator(); i.hasNext(); ) {
            a.add(((FDObject) i.next()).cosGetObject());
        }
    }

    protected void setFloatArray(COSName key, float[] array, boolean emptyArrayIfListEmpty) {
        if ((array == null) || (array.length == 0)) {
            if (emptyArrayIfListEmpty) {
                cosSetField(key, COSArray.create());
            } else {
                cosRemoveField(key);
            }
            return;
        }

        // todo 3 reuse existing array?
        COSArray a = COSArray.create();
        cosSetField(key, a);

        for (int i = 0; i < array.length; i++) {
            a.add(COSFixed.create(array[i]));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        String result = "[]";
        if (cosGetObject().isIndirect()) {
            result = "[" + cosGetObject().getIndirectObject().toString() + "]";
        }
        return result;
    }
}
