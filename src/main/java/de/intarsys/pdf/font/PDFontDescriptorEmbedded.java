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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.filter.Filter;
import de.intarsys.tools.collection.ByteArrayTools;

/**
 * The PDFontDescriptor that is used when an explicit font descriptor is
 * available in the pdf file. This will happen most of the time, only the
 * builtin fonts MAY discard this object.
 */
public class PDFontDescriptorEmbedded extends PDFontDescriptor {
    /**
     * The meta class implementation
     */
	public static class MetaClass extends PDFontDescriptor.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDFontDescriptorEmbedded(object);
        }
    }

    public static final COSName CN_SubType_Type1C = COSName.constant("Type1C"); //$NON-NLS-1$

    public static final COSName CN_SubType_CIDFontType0C = COSName.constant("CIDFontType0C"); //$NON-NLS-1$

    public static final COSName CN_SubType_OpenType = COSName.constant("OpenType"); //$NON-NLS-1$

    public static final COSName DK_Ascent = COSName.constant("Ascent");

    public static final COSName DK_AvgWidth = COSName.constant("AvgWidth");

    public static final COSName DK_CapHeight = COSName.constant("CapHeight");

    public static final COSName DK_CharSet = COSName.constant("CharSet");

    public static final COSName DK_CIDSet = COSName.constant("CIDSet");

    public static final COSName DK_Descent = COSName.constant("Descent");

    public static final COSName DK_FD = COSName.constant("FD");

    public static final COSName DK_Flags = COSName.constant("Flags");

    public static final COSName DK_FontBBox = COSName.constant("FontBBox");

    public static final COSName DK_FontFamily = COSName.constant("FontFamily");

    public static final COSName DK_FontFile = COSName.constant("FontFile");

    public static final COSName DK_FontFile2 = COSName.constant("FontFile2");

    public static final COSName DK_FontFile3 = COSName.constant("FontFile3");

    public static final COSName DK_FontName = COSName.constant("FontName");

    public static final COSName DK_FontStretch = COSName.constant("FontStretch");

    public static final COSName DK_FontWeight = COSName.constant("FontWeight");

    public static final COSName DK_ItalicAngle = COSName.constant("ItalicAngle");

    public static final COSName DK_Lang = COSName.constant("Lang");

    public static final COSName DK_Leading = COSName.constant("Leading");

    public static final COSName DK_Length1 = COSName.constant("Length1");

    public static final COSName DK_Length2 = COSName.constant("Length2");

    public static final COSName DK_Length3 = COSName.constant("Length3");

    public static final COSName DK_MaxWidth = COSName.constant("MaxWidth");

    public static final COSName DK_MissingWidth = COSName.constant("MissingWidth");

    public static final COSName DK_StemH = COSName.constant("StemH");

    public static final COSName DK_StemV = COSName.constant("StemV");

    public static final COSName DK_Style = COSName.constant("Style");

    public static final COSName DK_XHeight = COSName.constant("XHeight");

    /**
     * The meta class instance
     */
	public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * Create the receiver class from an already defined {@link COSDictionary}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDFontDescriptorEmbedded(COSObject object) {
        super(object);
    }

    @Override
    public COSStream cosGetFontFile() {
        return cosGetField(DK_FontFile).asStream();
    }

    @Override
    public COSStream cosGetFontFile2() {
        return cosGetField(DK_FontFile2).asStream();
    }

    @Override
    public COSStream cosGetFontFile3() {
        return cosGetField(DK_FontFile3).asStream();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getAscent()
     */
    @Override
    public float getAscent() {
        // TODO 3 wrong default, but this field is required
        return getFieldFixed(DK_Ascent, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getAvgWidth()
     */
    @Override
    public float getAvgWidth() {
        return getFieldFixed(DK_AvgWidth, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getCapHeight()
     */
    @Override
    public float getCapHeight() {
        // TODO 3 wrong default, but this field is required
        return getFieldFixed(DK_CapHeight, 0);
    }

    @Override
    public String getCharSet() {
        return getFieldString(DK_CharSet, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getDescent()
     */
    @Override
    public float getDescent() {
        // TODO 3 wrong default, but this field is required
        return getFieldFixed(DK_Descent, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFlags()
     */
    @Override
    public int getFlagsValue() {
        // TODO 3 wrong default, but this field is required
        return getFieldInt(DK_Flags, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getRectangle()
     */
    @Override
    public CDSRectangle getFontBB() {
        COSArray array = cosGetField(DK_FontBBox).asArray();
        if (array == null) {
            return null;
        }
        return CDSRectangle.createFromCOS(array);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFontFamily()
     */
    @Override
    public String getFontFamily() {
        COSString ff = cosGetField(DK_FontFamily).asString();
        return (ff == null) ? null : ff.stringValue();
    }

    @Override
    public byte[] getFontFile() {
        COSStream stream = cosGetField(DK_FontFile).asStream();
        if (stream == null) {
            return null;
        }
        return stream.getDecodedBytes();
    }

    @Override
    public byte[] getFontFile2() {
        COSStream stream = cosGetField(DK_FontFile2).asStream();
        if (stream == null) {
            return null;
        }
        return stream.getDecodedBytes();
    }

    @Override
    public byte[] getFontFile3() {
        COSStream stream = cosGetField(DK_FontFile3).asStream();
        if (stream == null) {
            return null;
        }
        return stream.getDecodedBytes();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getFontName()
     */
    @Override
    public String getFontName() {
        COSName fn = cosGetField(DK_FontName).asName();
        return (fn == null) ? null : fn.stringValue();
    }

    @Override
    public String getFontStretch() {
        return getFieldString(DK_FontStretch, null);
    }

    @Override
    public int getFontWeight() {
        return getFieldInt(DK_FontWeight, 400);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getItalicAngle()
     */
    @Override
    public float getItalicAngle() {
        // TODO 3 wrong default, but this field is required
        return getFieldFixed(DK_ItalicAngle, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getLeading()
     */
    @Override
    public int getLeading() {
        return getFieldInt(DK_Leading, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getMaxWidth()
     */
    @Override
    public int getMaxWidth() {
        return getFieldInt(DK_MaxWidth, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getMissingWidth()
     */
    @Override
    public int getMissingWidth() {
        return getFieldInt(DK_MissingWidth, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getStemH()
     */
    @Override
    public int getStemH() {
        return getFieldInt(DK_StemH, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getStemV()
     */
    @Override
    public int getStemV() {
        // TODO 3 wrong default, but this field is required
        return getFieldInt(DK_StemV, 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#getXHeight()
     */
    @Override
    public float getXHeight() {
        return getFieldInt(DK_XHeight, 0);
    }

    public void removeFontFile2() {
        cosGetDict().remove(DK_FontFile2);
    }

    public void setAscent(int value) {
        setFieldInt(DK_Ascent, value);
    }

    public void setAvgWidth(int value) {
        setFieldInt(DK_AvgWidth, value);
    }

    public void setCapHeight(int value) {
        setFieldInt(DK_CapHeight, value);
    }

    @Override
    public void setCharSet(String charset) {
        setFieldString(DK_CharSet, charset);
    }

    public void setDescent(int value) {
        setFieldInt(DK_Descent, value);
    }

    public void setFlags(int value) {
        setFieldInt(DK_Flags, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.font.PDFontDescriptor#setFlagsValue(int)
     */
    @Override
    public void setFlagsValue(int value) {
        setFieldInt(DK_Flags, value);
    }

    public void setFontBB(CDSRectangle rect) {
        setFieldObject(DK_FontBBox, rect);
    }

    @Override
    public void setFontFamily(String value) {
        setFieldString(DK_FontFamily, value);
    }

    @Override
    public void setFontFile(byte[] data) {
        COSStream cosStream = COSStream.create(null);
        setLengths(cosStream, data);
        cosStream.setDecodedBytes(data);
        cosStream.addFilter(Filter.CN_Filter_FlateDecode);
        cosSetField(DK_FontFile, cosStream);
    }

    @Override
    public void setFontFile2(byte[] data) {
        COSStream cosStream = COSStream.create(null);
        cosStream.getDict().put(DK_Length1, COSInteger.create(data.length));
        cosStream.addFilter(Filter.CN_Filter_FlateDecode);
        cosStream.setDecodedBytes(data);
        cosSetField(DK_FontFile2, cosStream);
    }

    @Override
    public void setFontFile3(byte[] data) {
        // todo 3 not supported
        COSStream cosStream = COSStream.create(null);
        cosStream.setDecodedBytes(data);
        cosSetField(DK_FontFile3, cosStream);
    }

    @Override
    public void setFontName(String value) {
        setFieldName(DK_FontName, value);
    }

    @Override
    public void setFontStretch(String stretch) {
        setFieldString(DK_FontStretch, stretch);
    }

    @Override
    public void setFontWeight(int weight) {
        setFieldInt(DK_FontWeight, weight);
    }

    public void setItalicAngle(float value) {
        setFieldFixed(DK_ItalicAngle, value);
    }

    public void setLeading(int value) {
        setFieldInt(DK_Leading, value);
    }

    /**
     * Simple determination of required length fields.
     *
     * @param cosStream
     * @param data
     */
    protected void setLengths(COSStream cosStream, byte[] data) {
        byte[] pattern;
        int length1 = 0;
        int length2 = 0;
        int length3 = 0;

        pattern = "currentfile eexec".getBytes();
        int index1 = ByteArrayTools.indexOf(data, 0, data.length, pattern, 0, pattern.length, 0);
        if (index1 != -1) {
            length1 = index1 + pattern.length;
        }
        // find 512 zero array
        pattern = "0000000000000000000000000000000000000000000000000000000000000000".getBytes();
        int index2 = ByteArrayTools.indexOf(data, 0, data.length, pattern, 0, pattern.length, 0);
        if (index2 == -1) {
            length2 = data.length - length1;
        } else {
            length2 = index2 - length1;
            length3 = data.length - index2;
        }
        cosStream.getDict().put(DK_Length1, COSInteger.create(length1));
        cosStream.getDict().put(DK_Length2, COSInteger.create(length2));
        cosStream.getDict().put(DK_Length3, COSInteger.create(length3));
    }

    public void setMaxWidth(int value) {
        setFieldInt(DK_MaxWidth, value);
    }

    public void setMissingWidth(int value) {
        setFieldInt(DK_MissingWidth, value);
    }

    public void setStemH(int value) {
        setFieldInt(DK_StemH, value);
    }

    public void setStemV(int value) {
        setFieldInt(DK_StemV, value);
    }

    public void setXHeight(int value) {
        setFieldInt(DK_XHeight, value);
    }
}
