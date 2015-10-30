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
package de.intarsys.pdf.filter;

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

import java.io.IOException;

/**
 * The abstract superclass for the implementation of IFilter.
 */
public abstract class Filter implements IFilter {
    public static final COSName CN_Filter_A85 = COSName.constant("A85"); //$NON-NLS-1$

    public static final COSName CN_Filter_AHx = COSName.constant("AHx"); //$NON-NLS-1$

    public static final COSName CN_Filter_ASCII85Decode = COSName.constant("ASCII85Decode"); //$NON-NLS-1$

    public static final COSName CN_Filter_ASCIIHexDecode = COSName.constant("ASCIIHexDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_CCF = COSName.constant("CCF"); //$NON-NLS-1$

    public static final COSName CN_Filter_CCITTFaxDecode = COSName.constant("CCITTFaxDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_Crypt = COSName.constant("Crypt"); //$NON-NLS-1$

    public static final COSName CN_Filter_DCT = COSName.constant("DCT"); //$NON-NLS-1$

    public static final COSName CN_Filter_DCTDecode = COSName.constant("DCTDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_Fl = COSName.constant("Fl"); //$NON-NLS-1$

    //
    public static final COSName CN_Filter_FlateDecode = COSName.constant("FlateDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_JBIG2Decode = COSName.constant("JBIG2Decode"); //$NON-NLS-1$

    public static final COSName CN_Filter_JPXDecode = COSName.constant("JPXDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_LZW = COSName.constant("LZW"); //$NON-NLS-1$

    public static final COSName CN_Filter_LZWDecode = COSName.constant("LZWDecode"); //$NON-NLS-1$

    public static final COSName CN_Filter_RL = COSName.constant("RL"); //$NON-NLS-1$

    public static final COSName CN_Filter_RunLengthDecode = COSName.constant("RunLengthDecode"); //$NON-NLS-1$

    /**
     * The options defining additional parameters to the filters algorithm
     */
    private COSDictionary options;

    /**
     * This is the stream where the filter is used.
     * <p>
     * <p>
     * Normally we should not need this one, but there are subtle implementation
     * problems (see CCITT) where the available libraries need information that
     * only can be found in this context.
     * </p>
     * <p>
     * <p>
     * Look at this as if already deprecated.... :-)
     * </p>
     */
    private COSStream stream;

    /**
     *
     */
    protected Filter(COSDictionary paramOptions) {
        super();
        options = paramOptions;
    }

    protected abstract byte[] decode(byte[] source) throws IOException;

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#decode(byte[], int, int)
     */
    @Override
    public byte[] decode(byte[] source, int offset, int length) throws IOException {
        if ((offset != 0) || (length != source.length)) {
            int minLength = Math.min(length, source.length);
            byte[] temp = new byte[minLength];
            System.arraycopy(source, offset, temp, 0, minLength);
            return decode(temp);
        }
        return decode(source);
    }

    protected abstract byte[] encode(byte[] source) throws IOException;

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#encode(byte[], int, int)
     */
    @Override
    public byte[] encode(byte[] source, int offset, int length) throws IOException {
        if ((offset != 0) || (length != source.length)) {
            byte[] temp = new byte[length];
            System.arraycopy(source, offset, temp, 0, length);
            return encode(temp);
        }
        return encode(source);
    }

    /**
     * The decode option declared for the key {@code name} or
     * {@link COSNull}.
     *
     * @param name
     * @return The decode option declared for the key {@code name} or
     * {@link COSNull}.
     */
    public COSObject getOption(COSName name) {
        if (getOptions() == null) {
            return COSNull.NULL;
        }
        return getOptions().get(name);
    }

    public COSDictionary getOptions() {
        return options;
    }

    @Override
    public COSStream getStream() {
        return stream;
    }

    @Override
    public void setStream(COSStream stream) {
        this.stream = stream;
    }
}
