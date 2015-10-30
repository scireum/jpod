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

import de.intarsys.pdf.filter.FilterFactory;
import de.intarsys.pdf.filter.IFilter;
import de.intarsys.tools.collection.SingleObjectIterator;
import de.intarsys.tools.file.FileTools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * An object representing stream data in a PDF document. Unlike a string, stream
 * data is not restricted in length. Stream data may be encoded with the filter
 * implementation.
 */
public class COSStream extends COSCompositeObject {
    public static final COSName DK_DecodeParms = COSName.constant("DecodeParms"); //$NON-NLS-1$

    public static final COSName DK_DP = COSName.constant("DP"); //$NON-NLS-1$
    public static final COSName DK_F = COSName.constant("F"); //$NON-NLS-1$
    public static final COSName DK_FDecodeParms = COSName.constant("FDecodeParms"); //$NON-NLS-1$
    public static final COSName DK_FFilter = COSName.constant("FFilter"); //$NON-NLS-1$
    public static final COSName DK_Filter = COSName.constant("Filter"); //$NON-NLS-1$
    public static final COSName DK_Length = COSName.constant("Length"); //$NON-NLS-1$
    public static final COSName DK_Resources = COSName.constant("Resources"); //$NON-NLS-1$

    public static final Object SLOT_BYTES = new Object();

    /**
     * Create a new {@link COSStream}.
     *
     * @param dict An optional dictionary to be used as the streams dictionary.
     * @return Create a new {@link COSStream}.
     */
    public static COSStream create(COSDictionary dict) {
        COSStream result = new COSStream(dict);
        result.beIndirect();
        return result;
    }

    /**
     * The options or an array of options for filtering.
     *
     * @return The options or an array of options for filtering.
     */
    static public COSObject getDecodeParams(COSDictionary dict) {
        if (isExternal(dict)) {
            return dict.get(DK_FDecodeParms);
        }
        COSObject result = dict.get(DK_DP);
        if (!result.isNull()) {
            return result;
        }
        return dict.get(DK_DecodeParms);
    }

    /**
     * The options corresponding to the first occurence of the filter
     * <code>name</code>.
     *
     * @return The options corresponding to the first occurence of the filter
     * <code>name</code>.
     */
    static public COSDictionary getDecodeParams(COSDictionary dict, COSName name) {
        COSObject basicFilters = getFilters(dict);
        if (basicFilters instanceof COSName) {
            COSName filter = basicFilters.asName();
            if (filter.equals(name)) {
                return dict.get(COSStream.DK_DecodeParms).asDictionary();
            }
        } else if (basicFilters instanceof COSArray) {
            COSArray filters = basicFilters.asArray();
            int i = 0;
            Iterator it = filters.iterator();
            while (it.hasNext()) {
                COSName filter = ((COSObject) it.next()).asName();
                if (filter != null && filter.equals(name)) {
                    COSArray decodeParamsArray = dict.get(COSStream.DK_DecodeParms).asArray();
                    if (decodeParamsArray != null) {
                        return decodeParamsArray.get(i).asDictionary();
                    }
                }
                i++;
            }
        }
        return null;
    }

    /**
     * Return the filter or the collection of filters for the stream.
     *
     * @return The filter or the collection of filters for the stream.
     */
    static public COSObject getFilters(COSDictionary dict) {
        if (isExternal(dict)) {
            return dict.get(DK_FFilter);
        }
        COSObject result = dict.get(DK_F);
        if (!result.isNull()) {
            return result;
        }
        return dict.get(DK_Filter);
    }

    /**
     * <code>true</code> if the given stream dictionary has declared a filter
     * <code>name</code>.
     *
     * @param dict a stream dictionary
     * @param name a filter name
     * @return <code>true</code> if the given stream dictionary has declared a
     * filter <code>name</code>.
     */
    static public boolean hasFilter(COSDictionary dict, COSName name) {
        COSObject filters = getFilters(dict);
        if (filters.isNull()) {
            return false;
        }
        if (filters instanceof COSName) {
            return filters.equals(name);
        }
        if (filters instanceof COSArray) {
            for (Iterator i = ((COSArray) filters).iterator(); i.hasNext(); ) {
                COSName filterName = ((COSObject) i.next()).asName();
                if (filterName != null && filterName.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * <code>true</code> if the stream dictionary contains the F key.
     *
     * @return <code>true</code> if the stream dictionary contains the F key.
     */
    static public boolean isExternal(COSDictionary dict) {
        // check for F key
        // if it is a Array or Name, it is used as an abbreviation for Filter
        COSObject result = dict.get(DK_F);
        if (!result.isNull()) {
            if (result instanceof COSName || result instanceof COSArray) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * The logical byte stream
     */
    private byte[] decodedBytes;

    /**
     * the dictionary describing the stream
     */
    private COSDictionary dict;

    /**
     * The physical byte stream
     */
    private byte[] encodedBytes;

    protected COSStream() {
        super();
    }

    /**
     * COSStream constructor.
     *
     * @param newDict The stream dictionary for the new stream. Can be null, a new
     *                dictionary will be created.
     */
    protected COSStream(COSDictionary newDict) {
        super();
        if (newDict == null) {
            newDict = COSDictionary.create();
        }
        setDict(newDict);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor
     * )
     */
    @Override
    public java.lang.Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        return visitor.visitFromStream(this);
    }

    /**
     * Add a new filter declaration to the filters collection. If necessary
     * convert the Filter entry to a collection first.
     *
     * @param name The logical name of the filter.
     */
    public void addFilter(COSName name) {
        addFilter(getFilterSize(), name, null);
    }

    /**
     * Add a new filter declaration to the filters collection. If necessary
     * convert the Filter entry to a collection first.
     *
     * @param name       The logical name of the filter.
     * @param dictionary The corresponding decode parameters
     */
    public void addFilter(COSName name, COSDictionary dictionary) {
        addFilter(getFilterSize(), name, dictionary);
    }

    /**
     * Add a new filter declaration to the filters collection at the specified
     * index. If necessary convert the Filter entry to a collection first.
     *
     * @param index The index to add the filter at.
     * @param name  The logical name of the filter.
     */
    public void addFilter(int index, COSName name) {
        addFilter(index, name, null);
    }

    /**
     * Add a new filter declaration to the filters collection at the specified
     * index. If necessary convert the Filter entry to a collection first.
     *
     * @param index      The index to add the filter at.
     * @param name       The logical name of the filter.
     * @param dictionary The corresponding decode parameters
     */
    public void addFilter(int index, COSName name, COSDictionary dictionary) {
        // be sure decoded stream is available
        getDecodedBytes();
        encodedBytes = null;
        COSObject filters = getFilters();
        if (filters.isNull()) {
            getDict().put(DK_Filter, name);
            getDict().put(DK_DecodeParms, dictionary);
        } else {
            COSName filterName;
            COSArray newFilterArray;
            COSObject decodeParms;
            COSArray newDecodeParmsArray;

            filterName = filters.asName();
            if (filterName != null) {
                getDict().remove(DK_Filter);
                newFilterArray = COSArray.create(2);
                newFilterArray.add(filterName);
            } else {
                newFilterArray = filters.asArray();
            }
            newFilterArray.add(index, name);
            getDict().put(DK_Filter, newFilterArray);

            decodeParms = getDecodeParams();
            newDecodeParmsArray = null;
            if (decodeParms.isNull() && dictionary != null) {
                newDecodeParmsArray = COSArray.create(newFilterArray.size());
                // add one less
                for (int count = 0; count < newFilterArray.size() - 1; count++) {
                    newDecodeParmsArray.add(COSNull.NULL);
                }
            } else {
                COSDictionary decodeParmsDictionary;

                decodeParmsDictionary = decodeParms.asDictionary();
                if (decodeParmsDictionary != null) {
                    getDict().remove(DK_DecodeParms);
                    newDecodeParmsArray = COSArray.create(newFilterArray.size());
                    newDecodeParmsArray.add(decodeParmsDictionary);
                } else {
                    newDecodeParmsArray = decodeParms.asArray();
                }
            }
            if (newDecodeParmsArray != null) {
                if (dictionary == null) {
                    newDecodeParmsArray.add(index, COSNull.NULL);
                } else {
                    newDecodeParmsArray.add(index, dictionary);
                }
            }
            getDict().put(DK_DecodeParms, newDecodeParmsArray);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.COSCompositeObject#addObjectListener(de.intarsys.
     * pdf.cos.ICOSObjectListener)
     */
    @Override
    public void addObjectListener(ICOSObjectListener listener) {
        super.addObjectListener(listener);
        if (dict != null) {
            dict.addObjectListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#asStream()
     */
    @Override
    public COSStream asStream() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicIterator()
     */
    @Override
    public Iterator basicIterator() {
        return new SingleObjectIterator(getDict());
    }

    /**
     * Set the streams logical content.
     *
     * @param newBytes the logical content for the stream
     */
    public void basicSetDecodedBytes(byte[] newBytes) {
        decodedBytes = newBytes;
        encodedBytes = null;
    }

    /**
     * Set the streams physical content.
     *
     * @param newBytes the physical content for the stream
     */
    public void basicSetEncodedBytes(byte[] newBytes) {
        encodedBytes = newBytes;
        decodedBytes = null;
        int length = (encodedBytes == null) ? 0 : encodedBytes.length;
        // no update propagation, please!!
        getDict().basicPutSilent(DK_Length, COSInteger.create(length));
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        byte[] decoded = getDecodedBytes();
        if (decoded == null) {
            return null;
        }
        return new String(decoded);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyBasic()
     */
    @Override
    protected COSObject copyBasic() {
        COSStream result = new COSStream();
        result.beIndirect();
        // aggregated dictionary takes care of itself
        result.encodedBytes = this.encodedBytes;
        result.decodedBytes = this.decodedBytes;
        return result;
    }

    /**
     * A copy of this, bytes decoded.
     *
     * @return A copy of this, bytes decoded.
     * @throws IOException
     */
    public COSStream copyDecodeFirst() throws IOException {
        COSStream newStream;
        COSName filter;
        COSArray filters;
        COSArray decodeParmss;
        byte[] bytes;

        // prepare new stream
        newStream = (COSStream) copyShallow();
        if (getFilters().isNull()) {
            return newStream;
        }
        newStream.basicSetDecodedBytes(null);
        newStream.getDict().remove(DK_DecodeParms);
        newStream.getDict().remove(DK_Filter);
        newStream.getDict().remove(DK_Length);
        // prepare content
        bytes = getEncodedBytes();
        if ((filter = getFirstFilter()) != null) {
            bytes = doDecode(filter, getFirstDecodeParam(), bytes, 0, getAnyLength());
        }
        filters = getFilters().asArray();
        if (filters != null) {
            decodeParmss = getDecodeParams().asArray();
            for (int index = filters.size() - 1; index > 0; index--) {
                if (decodeParmss != null) {
                    COSDictionary dictionary;

                    dictionary = decodeParmss.get(index).asDictionary();
                    if (dictionary != null) {
                        dictionary = (COSDictionary) dictionary.copyShallow();
                    }
                    newStream.filter((COSName) filters.get(index).copyShallow(), dictionary);
                } else {
                    newStream.filter((COSName) filters.get(index).copyShallow());
                }
            }
        }
        newStream.setEncodedBytes(bytes);
        return newStream;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSCompositeObject#copyDeep(java.util.Map)
     */
    @Override
    public COSObject copyDeep(Map copied) {
        COSStream result = (COSStream) super.copyDeep(copied);
        result.setDict((COSDictionary) getDict().copyDeep(copied));
        if (encodedBytes != null) {
            result.setEncodedBytes(encodedBytes);
        } else {
            result.setDecodedBytes(decodedBytes);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyShallow()
     */
    @Override
    public COSObject copyShallow() {
        COSStream result = (COSStream) super.copyShallow();
        result.setDict((COSDictionary) getDict().copyShallow());
        if (encodedBytes != null) {
            result.setEncodedBytes(encodedBytes);
        } else {
            result.setDecodedBytes(decodedBytes);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copySubGraph(java.util.Map)
     */
    @Override
    protected COSObject copySubGraph(Map copied) {
        COSStream result = (COSStream) super.copySubGraph(copied);
        result.setDict((COSDictionary) getDict().copySubGraph(copied));
        if (encodedBytes != null) {
            result.setEncodedBytes(encodedBytes);
        } else {
            result.setDecodedBytes(decodedBytes);
        }
        return result;
    }

    /**
     * Decode the filtered stream content using the filters defined in the
     * /Filter entry in the stream dictionary
     *
     * @return The decoded bytes.
     * @throws IOException
     */
    protected byte[] doDecode() throws IOException {
        byte[] newBytes;

        if (isExternal() && isBytesArrayEmpty(encodedBytes)) {
            // reset the encodedbytes because of later null checks
            encodedBytes = null;
            parseFKeyedFile();
        }
        if (encodedBytes == null) {
            return null;
        }

        // get the filters
        COSObject filters = getFilters();
        if (filters.isNull()) {
            int length = getLength();
            if ((length != -1) && (encodedBytes.length > length)) {
                newBytes = new byte[length];
                System.arraycopy(encodedBytes, 0, newBytes, 0, length);
            } else {
                newBytes = encodedBytes;
            }
            return newBytes;
        }

        // get the options
        COSObject options = getDecodeParams();

        // decode
        if (filters instanceof COSName) {
            newBytes = doDecode((COSName) filters, options.asDictionary(), encodedBytes, 0, getAnyLength());
        } else {
            byte[] temp = encodedBytes;
            int length = getAnyLength();
            for (int i = 0; i < ((COSArray) filters).size(); i++) {
                COSObject option = COSNull.NULL;
                if (!options.isNull()) {
                    option = ((COSArray) options).get(i);
                }
                COSName filter = ((COSArray) filters).get(i).asName();
                temp = doDecode(filter, option.asDictionary(), temp, 0, length);
                if (temp != null) {
                    length = temp.length;
                }
            }
            newBytes = temp;
        }
        return newBytes;
    }

    /**
     * Perform the decoding process of the underlying byte stream.
     *
     * @param filterName The name of a filter to use for this step.
     * @param options    The options to use for the filter.
     * @param bytes      The bytes to decode.
     * @param offset     The offset to start.
     * @param length     The length to be decoded.
     * @return The decoded bytes.
     * @throws IOException
     */
    protected byte[] doDecode(COSName filterName, COSDictionary options, byte[] bytes, int offset, int length)
            throws IOException {
        if (bytes == null) {
            return new byte[0];
        }
        IFilter filter = FilterFactory.get().createFilter(filterName, options);
        filter.setStream(this);
        return filter.decode(bytes, offset, length);
    }

    /**
     * encode the filtered stream content using the filters defined in the
     * /Filter entry in the stream dictionary in reverse order
     *
     * @throws IOException
     */
    protected void doEncode() throws IOException {
        if (decodedBytes == null) {
            return;
        }

        // get the filters
        COSObject filters = getFilters();
        if (filters.isNull()) {
            encodedBytes = decodedBytes;
            return;
        }

        // get the options
        COSObject options = getDecodeParams();

        // encode
        if (filters instanceof COSName) {
            encodedBytes = doEncode((COSName) filters, options.asDictionary(), decodedBytes, 0, decodedBytes.length);
        } else {
            byte[] temp = decodedBytes;
            int length = decodedBytes.length;
            for (int i = ((COSArray) filters).size() - 1; i >= 0; i--) {
                COSDictionary option = null;
                if (!options.isNull()) {
                    option = ((COSArray) options).get(i).asDictionary();
                }

                COSName filter = ((COSArray) filters).get(i).asName();
                temp = doEncode(filter, option, temp, 0, length);
                length = temp.length;
            }
            encodedBytes = temp;
        }
    }

    /**
     * Perform the encoding process of the underlying byte stream.
     *
     * @param filterName The name of a filter to use for this step.
     * @param options    The options to use for the filter.
     * @param bytes      The bytes to encode .
     * @param offset     The offset to start.
     * @param length     The length to be encoded.
     * @return The encoded bytes.
     * @throws IOException
     */
    protected byte[] doEncode(COSName filterName, COSDictionary options, byte[] bytes, int offset, int length)
            throws IOException {
        if (bytes == null) {
            return new byte[0];
        }
        IFilter filter = FilterFactory.get().createFilter(filterName, options);
        filter.setStream(this);
        return filter.encode(bytes, offset, length);
    }

    @Override
    public boolean equals(Object o) {
        return this.equals(o, new PairRegister());
    }

    @Override
    protected boolean equals(Object o, PairRegister visited) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        COSStream other = (COSStream) o;
        if (visited.check(this, other)) {
            // We've already seen this pair.
            return true;
        }

        return this.getDict().equals(other.getDict(), visited) && Arrays.equals(this.getEncodedBytes(),
                                                                                other.getEncodedBytes());
    }

    /**
     * Add a new filter. If the stream is already filtered, the new filter will
     * be applied to the current encoded bytes.
     *
     * @param name The logical name of the filter.
     */
    public void filter(COSName name) {
        addFilter(0, name, null);
    }

    /**
     * Add a new filter. If the stream is already filtered, the new filter will
     * be applied to the current encoded bytes.
     *
     * @param name       The logical name of the filter.
     * @param dictionary The corresponding decode parameters
     */
    public void filter(COSName name, COSDictionary dictionary) {
        addFilter(0, name, dictionary);
    }

    /**
     * The declared or real length for this.
     *
     * @return The declared or real length for this.
     */
    public int getAnyLength() {
        int result = getLength();
        if (result == -1) {
            return encodedBytes.length;
        }
        return result;
    }

    /**
     * The unfiltered (logical) stream content. It is not intended to manipulate
     * the byte array directly.
     *
     * @return The unfiltered (logical) stream content
     * @throws IOException
     */
    public byte[] getDecodedBytes() {
        if (decodedBytes == null) {
            try {
                decodedBytes = doDecode();
            } catch (IOException e) {
                handleException(new COSRuntimeException("error decoding stream", e)); //$NON-NLS-1$
            }
        }
        return decodedBytes;
    }

    /**
     * The unfiltered content as in getDecodedBytes, but allow the caller to
     * manipulate the result by copying/not caching the returned bytes
     *
     * @return The unfiltered content as in getDecodedBytes
     */
    public byte[] getDecodedBytesWritable() {
        byte[] bytes;
        byte[] copiedBytes;

        try {
            bytes = doDecode();
            // take care; doDecode does not always create a new array
            if (bytes != encodedBytes) {
                return bytes;
            }
        } catch (IOException e) {
            handleException(new COSRuntimeException("error decoding stream", e)); //$NON-NLS-1$
            return new byte[0];
        }
        copiedBytes = new byte[bytes.length];
        System.arraycopy(bytes, 0, copiedBytes, 0, bytes.length);
        return copiedBytes;
    }

    /**
     * The options or an array of options for filtering.
     *
     * @return The options or an array of options for filtering.
     */
    public COSObject getDecodeParams() {
        return getDecodeParams(getDict());
    }

    /**
     * The options corresponding to the first occurrence of the filter
     * <code>name</code>.
     *
     * @return The options corresponding to the first occurrence of the filter
     * <code>name</code>.
     */
    public COSDictionary getDecodeParams(COSName name) {
        return getDecodeParams(getDict(), name);
    }

    /**
     * The stream dictionary
     *
     * @return The stream dictionary
     */
    public COSDictionary getDict() {
        return dict;
    }

    /**
     * The filtered (physical) stream content. If it must be generated first,
     * then the content length is adjusted as a side effect. It is not intended
     * to manipulate the byte array directly.
     *
     * @return The filtered (physical) stream content
     */
    public byte[] getEncodedBytes() {
        if (encodedBytes == null) {
            try {
                doEncode();
            } catch (IOException e) {
                handleException(new COSRuntimeException("error encoding stream", e)); //$NON-NLS-1$
            }
            int length = (encodedBytes == null) ? 0 : encodedBytes.length;
            getDict().basicPutSilent(DK_Length, COSInteger.create(length));
        }
        return encodedBytes;
    }

    /**
     * Return the filter or the collection of filters for the stream.
     *
     * @return The filter or the collection of filters for the stream.
     */
    public COSObject getFilters() {
        return getFilters(getDict());
    }

    /**
     * Return the number of filters.
     *
     * @return The number of filters.
     */
    public int getFilterSize() {
        COSObject filter;

        filter = getFilters();
        if (filter.isNull()) {
            return 0;
        }
        if (filter.asName() != null) {
            return 1;
        }
        return filter.asArray().size();
    }

    /**
     * A dictionary with filter options or the first element of an array of such
     * dictionaries for each filter.
     *
     * @return A dictionary with filter options or the first element of an array
     * of such dictionaries for each filter.
     */
    public COSDictionary getFirstDecodeParam() {
        COSObject dictionaryOrArray;

        dictionaryOrArray = getDecodeParams();
        if (dictionaryOrArray.isNull()) {
            return null;
        }
        if (dictionaryOrArray instanceof COSDictionary) {
            return (COSDictionary) dictionaryOrArray;
        }
        if (dictionaryOrArray instanceof COSArray) {
            return ((COSArray) dictionaryOrArray).get(0).asDictionary();
        }
        return null;
    }

    /**
     * The filter or the first element of the collection of filters for the
     * stream.
     *
     * @return The filter or the first element of the collection of filters for
     * the stream.
     */
    public COSName getFirstFilter() {
        COSObject nameOrArray;

        nameOrArray = getFilters();
        if (nameOrArray.isNull()) {
            return null;
        }
        if (nameOrArray instanceof COSName) {
            return (COSName) nameOrArray;
        }
        if (nameOrArray instanceof COSArray) {
            return ((COSArray) nameOrArray).get(0).asName();
        }
        return null;
    }

    /**
     * The length of the encoded content. Be aware that this is the /Length
     * written in the stream dictionary, which is not necessarily a meaningful
     * value...
     *
     * @return The length of the encoded content
     */
    public int getLength() {
        COSNumber length = dict.get(DK_Length).asInteger();
        if (length != null) {
            return length.intValue();
        }
        return -1;
    }

    /**
     * <code>true</code> if this stream has declared a filter <code>name</code>.
     *
     * @param name a filter name
     * @return <code>true</code> if the stream has declared a filter
     * <code>name</code>.
     */
    public boolean hasFilter(COSName name) {
        return hasFilter(getDict(), name);
    }

    /**
     * tests a byte array for null, length=0 or crlf emptiness
     *
     * @param toTest
     * @return
     */
    private boolean isBytesArrayEmpty(byte[] toTest) {
        if ((toTest == null) || (toTest.length == 0)) {
            return true;
        }
        if ((toTest.length == 2) && (toTest[0] == 13) && (toTest[1] == 10)) {
            return true;
        }
        return false;
    }

    /**
     * <code>true</code> if the stream dictionary contains the F key.
     *
     * @return <code>true</code> if the stream dictionary contains the F key.
     */
    public boolean isExternal() {
        return isExternal(getDict());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#iterator()
     */
    @Override
    public Iterator<COSObject> iterator() {
        return new SingleObjectIterator(getDict());
    }

    /**
     * Parse the file referenced by the F key in this stream and set as the
     * filtered content.
     */
    protected void parseFKeyedFile() {
        COSObject fileSpec = dict.get(DK_F);
        String filepath = ""; //$NON-NLS-1$
        if (fileSpec instanceof COSString) {
            filepath = ((COSString) fileSpec).stringValue();
        } else {
            // todo 2 implement PDF fileSpecification logic
            return;
        }
        File externalFile = new File(filepath);
        if (!externalFile.exists()) {
            return;
        }
        byte[] content;
        try {
            content = FileTools.toBytes(externalFile);
        } catch (IOException e) {
            return;
        }
        if (content != null) {
            encodedBytes = content;
        }
    }

    /**
     * Remove all filters from this.
     */
    public void removeFilters() {
        // be sure decoded stream is available
        getDecodedBytes();
        encodedBytes = null;
        getDict().remove(DK_Filter);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.cos.COSCompositeObject#removeObjectListener(de.intarsys
     * .pdf.cos.ICOSObjectListener)
     */
    @Override
    public void removeObjectListener(ICOSObjectListener listener) {
        super.removeObjectListener(listener);
        if (dict != null) {
            dict.removeObjectListener(listener);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#restoreState(java.lang.Object)
     */
    @Override
    public void restoreState(Object object) {
        super.restoreState(object);
        COSStream stream = (COSStream) object;
        encodedBytes = stream.encodedBytes;
        decodedBytes = stream.decodedBytes;
        triggerChanged(null, null, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    public Object saveState() {
        COSStream result = new COSStream();
        // aggregated dictionary takes care of itself
        result.encodedBytes = this.encodedBytes;
        result.decodedBytes = this.decodedBytes;
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * Set the streams logical content
     *
     * @param newBytes The logical content for the stream
     */
    public void setDecodedBytes(byte[] newBytes) {
        willChange(this);
        basicSetDecodedBytes(newBytes);
        getDict().remove(DK_Length);
        if (objectListeners != null) {
            triggerChanged(SLOT_BYTES, null, null);
        }
    }

    /**
     * Give private access to dictionary to ease copying.
     *
     * @param dictionary dictionary part of the stream
     * @throws IllegalArgumentException if the stream is indirect
     */
    private void setDict(COSDictionary dictionary) {
        if (dictionary.isIndirect()) {
            throw new IllegalArgumentException("stream dictionary cannot be indirect"); //$NON-NLS-1$
        }
        dict = dictionary;
        dict.addContainer(this);
    }

    /**
     * Set the stream physical content.
     *
     * @param newBytes the physical content for the stream
     */
    public void setEncodedBytes(byte[] newBytes) {
        willChange(this);
        basicSetEncodedBytes(newBytes);
        if (objectListeners != null) {
            triggerChanged(SLOT_BYTES, null, null);
        }
    }
}
