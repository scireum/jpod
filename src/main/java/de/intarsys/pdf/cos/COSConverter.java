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

import de.intarsys.tools.stream.StreamTools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class COSConverter {

    protected static Object basicToJava(COSObject object, Map visited) {
        if (object instanceof COSArray) {
            List result = new ArrayList();
            for (COSObject anObject : object) {
                result.add(toJava(anObject, visited));
            }
            return result;
        } else if (object instanceof COSDictionary) {
            Map result = new HashMap();
            Iterator<Map.Entry<COSName, COSObject>> it = ((COSDictionary) object).entryIterator();
            while (it.hasNext()) {
                Map.Entry<COSName, COSObject> entry = it.next();
                result.put(entry.getKey().stringValue(), toJava(entry.getValue(), visited));
            }
            return result;
        } else if (object instanceof COSObjectProxy) {
            return toJava(((COSObjectProxy) object).cosGetObject());
        } else if (object instanceof COSStream) {
            return ((COSStream) object).getDecodedBytes();
        } else if (object instanceof COSBoolean) {
            return ((COSBoolean) object).booleanValue();
        } else if (object instanceof COSName) {
            return ((COSName) object).byteValue();
        } else if (object instanceof COSNull) {
            return null;
        } else if (object instanceof COSFixed) {
            return ((COSFixed) object).floatValue();
        } else if (object instanceof COSInteger) {
            return ((COSInteger) object).intValue();
        } else if (object instanceof COSString) {
            return object.stringValue();
        } else {
            // ... we did a complete enumeration...
            throw new IllegalStateException("can not happen"); //$NON-NLS-1$
        }
    }

    /**
     * Try the best in marshalling java objects directly to {@link COSObject}.
     * Collections will be marshalled recursively.
     * <p>
     * todo this is not yet recursion safe
     *
     * @param javaObject the java object to be marshalled
     * @return The resulting {@link COSObject}
     */
    public static COSObject toCos(Object javaObject) {
        COSObject result = null;
        if (javaObject instanceof String) {
            result = COSString.create((String) javaObject);
        } else if (javaObject instanceof Integer) {
            result = COSInteger.create(((Integer) javaObject).intValue());
        } else if (javaObject instanceof Float) {
            result = COSFixed.create(((Float) javaObject).floatValue());
        } else if (javaObject instanceof Double) {
            result = COSFixed.create(((Double) javaObject).floatValue());
        } else if (javaObject instanceof Boolean) {
            result = COSBoolean.create(((Boolean) javaObject).booleanValue());
        } else if (javaObject == null) {
            result = COSNull.create();
        } else if (javaObject instanceof Map) {
            result = COSDictionary.create(((Map) javaObject).size());
            for (Iterator it = ((Map) javaObject).entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                String key = String.valueOf(entry.getKey());
                Object value = entry.getValue();
                ((COSDictionary) result).put(COSName.create(key), toCos(value));
            }
        } else if (javaObject instanceof List) {
            result = COSArray.create(((List) javaObject).size());
            for (Iterator it = ((List) javaObject).iterator(); it.hasNext(); ) {
                Object value = it.next();
                ((COSArray) result).add(toCos(value));
            }
        } else if (javaObject instanceof byte[]) {
            result = COSName.create((byte[]) javaObject);
        } else if (javaObject instanceof char[]) {
            result = COSString.create(new String((char[]) javaObject));
        } else if (javaObject instanceof float[]) {
            float[] floats = (float[]) javaObject;
            result = COSArray.create(floats.length);
            for (int i = 0; i < floats.length; i++) {
                ((COSArray) result).add(COSFixed.create(floats[i]));
            }
        } else if (javaObject instanceof double[]) {
            double[] doubles = (double[]) javaObject;
            result = COSArray.create(doubles.length);
            for (int i = 0; i < doubles.length; i++) {
                ((COSArray) result).add(COSFixed.create(doubles[i]));
            }
        } else if (javaObject instanceof int[]) {
            int[] ints = (int[]) javaObject;
            result = COSArray.create(ints.length);
            for (int i = 0; i < ints.length; i++) {
                ((COSArray) result).add(COSInteger.create(ints[i]));
            }
        } else if (javaObject instanceof Byte) {
            result = COSInteger.create(((Byte) javaObject).intValue());
        } else if (javaObject instanceof Short) {
            result = COSInteger.create(((Short) javaObject).intValue());
        } else if (javaObject instanceof Long) {
            result = COSInteger.create(((Long) javaObject).intValue());
        } else if (javaObject instanceof InputStream) {
            result = COSStream.create(null);
            byte[] bytes;
            try {
                bytes = StreamTools.toByteArray((InputStream) javaObject);
            } catch (IOException ignored) {
                bytes = new byte[]{};
            }
            ((COSStream) result).setDecodedBytes(bytes);
        } else if (javaObject instanceof COSObject) {
            result = (COSObject) javaObject;
        } else {
            throw new IllegalArgumentException(javaObject + " can not be marshalled to a cos object"); //$NON-NLS-1$
        }
        return result;
    }

    public static Object toJava(COSObject object) {
        return toJava(object, new HashMap());
    }

    public static Object toJava(COSObject object, Map visited) {
        Object result = visited.get(object);
        if (result != null) {
            return result;
        }
        result = basicToJava(object, visited);
        visited.put(object, result);
        return result;
    }

    private COSConverter() {
        super();
    }
}
