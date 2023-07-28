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
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;

/**
 *
 */
public class CCITTFaxFilter extends Filter {
    public static final COSName DK_BlackIs1 = COSName.constant("BlackIs1"); //$NON-NLS-1$

    public static final COSName DK_Columns = COSName.constant("Columns"); //$NON-NLS-1$

    public static final COSName DK_DamagedRowsBeforeError = COSName.constant("DamagedRowsBeforeError"); //$NON-NLS-1$

    public static final COSName DK_EncodedByteAlign = COSName.constant("EncodedByteAlign"); //$NON-NLS-1$

    public static final COSName DK_EndOfBlock = COSName.constant("EndOfBlock"); //$NON-NLS-1$

    public static final COSName DK_EndOfLine = COSName.constant("EndOfLine"); //$NON-NLS-1$

    public static final COSName DK_H = COSName.constant("H"); //$NON-NLS-1$

    public static final COSName DK_Height = COSName.constant("Height"); //$NON-NLS-1$

    public static final COSName DK_K = COSName.constant("K"); //$NON-NLS-1$

    public static final COSName DK_Rows = COSName.constant("Rows"); //$NON-NLS-1$

    public static final COSName DK_W = COSName.constant("W"); //$NON-NLS-1$

    public static final COSName DK_Width = COSName.constant("Width"); //$NON-NLS-1$

    /**
     *
     */
    public CCITTFaxFilter(COSDictionary options) {
        super(options);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#decode(byte[])
     */
    @Override
    protected byte[] decode(byte[] source) {
        int width = 1728;
        COSInteger widthDef = getStream().getDict().get(DK_Width).asInteger();
        if (widthDef == null) {
            widthDef = getStream().getDict().get(DK_W).asInteger();
        }
        if (widthDef != null) {
            width = widthDef.intValue();
        }
        int height = 0;
        COSInteger heightDef = getStream().getDict().get(DK_Height).asInteger();
        if (heightDef == null) {
            heightDef = getStream().getDict().get(DK_H).asInteger();
        }
        if (heightDef != null) {
            height = heightDef.intValue();
        }

        //
        int columns = getOption(DK_Columns).getValueInteger(width);
        int rows = getOption(DK_Rows).getValueInteger(height);
        int k = getOption(DK_K).getValueInteger(0);
        int size = rows * ((columns + 7) >> 3);
        byte[] destination = new byte[size];

//        boolean align = getOption(DK_EncodedByteAlign).getValueBoolean(false);
//
//        CCITTFaxDecoder decoder = new CCITTFaxDecoder(1, columns, rows);
//        decoder.setAlign(align);
//        if (k == 0) {
//            decoder.decodeT41D(destination, source, 0, rows);
//        } else if (k > 0) {
//            decoder.decodeT42D(destination, source, 0, rows);
//        } else if (k < 0) {
//            decoder.decodeT6(destination, source, 0, rows);
//        }
//        if (!getOption(DK_BlackIs1).getValueBoolean(false)) {
//            for (int i = 0; i < destination.length; i++) {
//                // bitwise not
//                destination[i] = (byte) ~destination[i];
//            }
//        }
        return destination;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.filter.IFilter#encode(byte[])
     */
    @Override
    protected byte[] encode(byte[] source) {
        return null;
    }
}
