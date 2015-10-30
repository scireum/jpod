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

import java.io.IOException;

/**
 * Factory to create pdf filtering streams by name.
 */
public class StandardFilterFactory implements IFilterFactory {
    public StandardFilterFactory() {
    }

    /**
     * Create an {@link IFilter} that can deliver decoded bytes.
     *
     * @param filterName Filter name to lookup
     * @param options    The options to use for the filter.
     * @return An {@link IFilter}
     * @throws IOException
     */
    public IFilter createFilter(COSName filterName, COSDictionary options) throws IOException {
        IFilter result = null;
        if (filterName.equals(Filter.CN_Filter_FlateDecode)) {
            result = new FlateFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_Fl)) {
            result = new FlateFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_ASCIIHexDecode)) {
            result = new ASCIIHexFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_AHx)) {
            result = new ASCIIHexFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_ASCII85Decode)) {
            result = new ASCII85Filter(options);
        } else if (filterName.equals(Filter.CN_Filter_A85)) {
            result = new ASCII85Filter(options);
        } else if (filterName.equals(Filter.CN_Filter_LZWDecode)) {
            result = new LZWFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_LZW)) {
            result = new LZWFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_RunLengthDecode)) {
            result = new RunLengthFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_RL)) {
            result = new RunLengthFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_CCITTFaxDecode)) {
            result = new CCITTFaxFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_CCF)) {
            result = new CCITTFaxFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_JBIG2Decode)) {
            result = new JBIG2Filter(options);
        } else if (filterName.equals(Filter.CN_Filter_DCTDecode)) {
            result = new DCTFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_DCT)) {
            result = new DCTFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_JPXDecode)) {
            result = new JPXFilter(options);
        } else if (filterName.equals(Filter.CN_Filter_Crypt)) {
            result = new CryptFilter(options);
        }
        if (result == null) {
            throw new IOException("unknown filter:" + filterName);
        }
        return result;
    }
}
