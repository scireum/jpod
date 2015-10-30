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
import de.intarsys.pdf.cos.COSStream;
import org.jpedal.jbig2.JBIG2Decoder;
import org.jpedal.jbig2.JBIG2Exception;

import java.io.IOException;

/**
 *
 */
public class JBIG2Filter extends Filter {
    public static final COSName DK_JBIG2Globals = COSName.constant("JBIG2Globals"); //$NON-NLS-1$

    /**
     *
     */
    public JBIG2Filter(COSDictionary options) {
        super(options);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#decode(byte[])
     */
    @Override
    protected byte[] decode(byte[] source) throws IOException {
        JBIG2Decoder decoder;

        decoder = new JBIG2Decoder();
        try {
            if (getOptions() != null) {
                COSStream globals;

                globals = getOptions().get(DK_JBIG2Globals).asStream();
                if (globals != null) {
                    decoder.setGlobalData(globals.getDecodedBytes());
                }
            }
            decoder.decodeJBIG2(source);
        } catch (JBIG2Exception ex) {
            IOException ioException;

            ioException = new IOException();
            ioException.initCause(ex);
            throw ioException;
        }

        return decoder.getPageAsJBIG2Bitmap(0).getData(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#encode(byte[])
     */
    @Override
    protected byte[] encode(byte[] source) throws IOException {
        return null;
    }
}
