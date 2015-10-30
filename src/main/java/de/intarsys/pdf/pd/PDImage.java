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
package de.intarsys.pdf.pd;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSBoolean;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

import java.io.IOException;
import java.lang.ref.SoftReference;

/**
 * The representation of an image.
 */
public class PDImage extends PDXObject {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDXObject.MetaClass {
        protected MetaClass(Class paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDImage(object);
        }
    }

    /**
     * the valid rendering intents *
     */
    public static final String C_RENDERING_VALID1 = "RelativeColorimetric"; //$NON-NLS-1$

    public static final String C_RENDERING_VALID2 = "AbsoluteColorimetric"; //$NON-NLS-1$

    public static final String C_RENDERING_VALID3 = "Perceptual"; //$NON-NLS-1$

    public static final String C_RENDERING_VALID4 = "Saturation"; //$NON-NLS-1$

    public static final COSName CN_Subtype_Image = COSName.constant("Image"); //$NON-NLS-1$

    public static final COSName DK_Alternates = COSName.constant("Alternates"); //$NON-NLS-1$

    public static final COSName DK_BitsPerComponent = COSName.constant("BitsPerComponent"); //$NON-NLS-1$

    public static final COSName DK_BPC = COSName.constant("BPC"); //$NON-NLS-1$

    public static final COSName DK_ColorSpace = COSName.constant("ColorSpace"); //$NON-NLS-1$

    public static final COSName DK_CS = COSName.constant("CS"); //$NON-NLS-1$

    public static final COSName DK_D = COSName.constant("D"); //$NON-NLS-1$

    public static final COSName DK_Decode = COSName.constant("Decode"); //$NON-NLS-1$

    public static final COSName DK_DecodeParms = COSName.constant("DecodeParms"); //$NON-NLS-1$

    public static final COSName DK_DP = COSName.constant("DP"); //$NON-NLS-1$

    public static final COSName DK_H = COSName.constant("H"); //$NON-NLS-1$

    public static final COSName DK_Height = COSName.constant("Height"); //$NON-NLS-1$

    public static final COSName DK_I = COSName.constant("I"); //$NON-NLS-1$

    public static final COSName DK_ID = COSName.constant("ID"); //$NON-NLS-1$

    public static final COSName DK_IM = COSName.constant("IM"); //$NON-NLS-1$

    public static final COSName DK_ImageMask = COSName.constant("ImageMask"); //$NON-NLS-1$

    public static final COSName DK_Intent = COSName.constant("Intent"); //$NON-NLS-1$

    public static final COSName DK_Interpolate = COSName.constant("Interpolate"); //$NON-NLS-1$

    public static final COSName DK_Mask = COSName.constant("Mask"); //$NON-NLS-1$

    public static final COSName DK_Metadata = COSName.constant("Metadata"); //$NON-NLS-1$

    public static final COSName DK_Name = COSName.constant("Name"); //$NON-NLS-1$

    public static final COSName DK_OC = COSName.constant("OC"); //$NON-NLS-1$

    public static final COSName DK_OPI = COSName.constant("OPI"); //$NON-NLS-1$

    public static final COSName DK_SMask = COSName.constant("SMask"); //$NON-NLS-1$

    public static final COSName DK_SMaskInData = COSName.constant("SMaskInData"); //$NON-NLS-1$

    public static final COSName DK_StructParent = COSName.constant("StructParent"); //$NON-NLS-1$

    public static final COSName DK_W = COSName.constant("W"); //$NON-NLS-1$

    public static final COSName DK_Width = COSName.constant("Width"); //$NON-NLS-1$

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    private PDColorSpace cachedColorSpace = null;

    private int cachedHeight = -1;

    private int cachedIsImageMask = -1;

    private int cachedIsInterpolate = -1;

    private int cachedWidth = -1;

    private SoftReference<byte[]> cachedBytes;

    /**
     * Create the receiver class from an already defined {@link COSStream}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDImage(COSObject object) {
        super(object);
    }

    public COSStream cosExtractJPEGStream() throws IOException {
        COSStream cosStream;

        cosStream = cosGetStream();
        while (!"DCTDecode".equals(cosStream.getFirstFilter().stringValue()) //$NON-NLS-1$
               && !"DCT".equals(cosStream.getFirstFilter().stringValue()) //$NON-NLS-1$
               && !"JPXDecode".equals(cosStream.getFirstFilter().stringValue())) { //$NON-NLS-1$
            cosStream = cosStream.copyDecodeFirst();
        }
        return cosStream;
    }

    public COSObject cosGetColorSpace() {
        COSObject object = cosGetField(DK_CS);
        if (!object.isNull()) {
            return object;
        }
        return cosGetField(DK_ColorSpace);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
     */
    @Override
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_Image;
    }

    public COSObject cosGetMask() {
        return cosGetField(DK_Mask);
    }

    public COSArray cosSetColorSpace(COSArray colorspace) {
        return cosSetField(DK_ColorSpace, colorspace).asArray();
    }

    public COSName cosSetColorSpace(COSName colorspace) {
        return cosSetField(DK_ColorSpace, colorspace).asName();
    }

    private byte[] getCachedBytes() {
        if (cachedBytes == null) {
            return null;
        }
        return cachedBytes.get();
    }

    private void setCachedBytes(byte[] bytes) {
        if (bytes == null) {
            cachedBytes = null;
        } else {
            cachedBytes = new SoftReference<byte[]>(bytes);
        }
    }

    /**
     * Returns the raw image bytes. After decoding the cos stream a length check
     * is performed and decode parameters (if present) applied.
     *
     * @return the image bytes
     */
    public byte[] getAdjustedBytes(int numComponents) {
        int bitsPerPixel;
        int scanlinePad;
        int expectedSize;
        int[] decode;

        byte[] bytes = getCachedBytes();
        if (bytes != null) {
            return bytes;
        }

        // todo 1 @ehk check color space num components
        bitsPerPixel = getBitsPerComponent() * numComponents;
        scanlinePad = (bitsPerPixel + 7) / 8;
        decode = getDecode();
        if (decode != null && decode.length == 2 && decode[0] == 1 && decode[1] == 0) {
            bytes = cosGetStream().getDecodedBytesWritable();
            for (int index = 0; index < bytes.length; index++) {
                bytes[index] = (byte) ~bytes[index];
            }
        } else {
            bytes = cosGetStream().getDecodedBytes();
        }
        expectedSize =
                getHeight() * (((((getWidth() * bitsPerPixel) + 7) / 8) + scanlinePad - 1) / scanlinePad * scanlinePad);
        if (bytes.length != expectedSize) {
            byte[] newBytes;

            newBytes = new byte[expectedSize];
            System.arraycopy(bytes, 0, newBytes, 0, Math.min(bytes.length, expectedSize));
            bytes = newBytes;
        }
        setCachedBytes(bytes);
        return bytes;
    }

    /**
     * The number of bits per component.
     * <p>
     * <p>
     * This information is stored in different attributes depending if the image
     * is inlined or explicit.
     * </p>
     *
     * @return The number of bits per component.
     */
    public int getBitsPerComponent() {
        COSInteger bpc = cosGetField(DK_BPC).asInteger();
        if (bpc != null) {
            return bpc.intValue();
        }
        return getFieldInt(DK_BitsPerComponent, 1);
    }

    public byte[][] getColorKeyMask(int colors) {
        COSArray colorKeyMask;
        byte[][] bytes;

        colorKeyMask = cosGetMask().asArray();
        if (colorKeyMask == null) {
            return null;
        }

        bytes = new byte[colorKeyMask.size() / colors][colors];
        for (int keyIndex = 0; keyIndex < bytes.length; keyIndex++) {
            for (int colorIndex = 0; colorIndex < colors; colorIndex++) {
                bytes[keyIndex][colorIndex] =
                        (byte) ((COSNumber) colorKeyMask.get((keyIndex * colors) + colorIndex)).intValue();
            }
        }
        return bytes;
    }

    /**
     * The color space used by the image.
     * <p>
     * <p>
     * Color space information is stored in different attributes in inlined and
     * explicit images.
     * </p>
     *
     * @return The color space used by the image.
     */
    public PDColorSpace getColorSpace() {
        if (cachedColorSpace == null) {
            COSObject object = cosGetField(DK_CS);
            if (!object.isNull()) {
                cachedColorSpace = (PDColorSpace) PDColorSpace.META.createFromCos(object);
            } else {
                cachedColorSpace = (PDColorSpace) PDColorSpace.META.createFromCos(cosGetField(DK_ColorSpace));
            }
            if ((cachedColorSpace == null) && isImageMask()) {
                cachedColorSpace = PDCSDeviceGray.SINGLETON;
            }
        }
        return cachedColorSpace;
    }

    public int[] getDecode() {
        COSArray cosArray;
        int[] decode;

        cosArray = cosGetField(DK_Decode).asArray();
        if (cosArray == null) {
            cosArray = cosGetField(DK_D).asArray();
        }
        if (cosArray == null) {
            return null;
        }
        decode = new int[cosArray.size()];
        for (int index = 0; index < decode.length; index++) {
            decode[index] = cosArray.get(index).asNumber().intValue();
        }
        return decode;
    }

    /**
     * get the height of the raster image
     *
     * @return the height
     */
    public int getHeight() {
        if (cachedHeight == -1) {
            cachedHeight = getFieldInt(DK_Height, -1);
            if (cachedHeight == -1) {
                cachedHeight = getFieldInt(DK_H, 0);
            }
        }
        return cachedHeight;
    }

    public PDImage getMaskImage() {
        COSObject mask;

        mask = cosGetMask();
        if (mask.isNull()) {
            return null;
        }

        try {
            return (PDImage) PDXObject.META.createFromCos(mask);
        } catch (ClassCastException ignored) {
            return null;
        }
    }

    public PDXObject getSMask() {
        return (PDXObject) PDXObject.META.createFromCos(cosGetField(DK_SMask));
    }

    /**
     * get the width of the raster image
     *
     * @return the width
     */
    public int getWidth() {
        if (cachedWidth == -1) {
            cachedWidth = getFieldInt(DK_Width, -1);
            if (cachedWidth == -1) {
                cachedWidth = getFieldInt(DK_W, 0);
            }
        }
        return cachedWidth;
    }

    public boolean hasTransparency() {
        return !cosGetMask().isNull() || (getSMask() != null);
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedColorSpace = null;
        cachedHeight = -1;
        cachedWidth = -1;
        cachedBytes = null;
        cachedIsInterpolate = -1;
        cachedIsImageMask = -1;
    }

    @Override
    public boolean isImage() {
        return true;
    }

    public boolean isImageMask() {
        if (cachedIsImageMask == -1) {
            COSBoolean result = cosGetField(DK_ImageMask).asBoolean();
            if (result == null) {
                result = cosGetField(DK_IM).asBoolean();
            }
            if ((result != null) && result.booleanValue()) {
                cachedIsImageMask = 1;
            } else {
                cachedIsImageMask = 0;
            }
        }
        return cachedIsImageMask == 1;
    }

    /**
     * @return interpolation flag (used if a image is scaled)
     */
    public boolean isInterpolate() {
        if (cachedIsInterpolate == -1) {
            if (getFieldBoolean(DK_Interpolate, false)) {
                cachedIsInterpolate = 1;
            } else {
                cachedIsInterpolate = 0;
            }
        }
        return cachedIsInterpolate == 1;
    }

    public void setBitsPerComponent(int bits) {
        setFieldInt(DK_BitsPerComponent, bits);
    }

    /**
     * In inline images the color space may reference the resource dictionary.
     * In this case the color space is resolved and assigned externaly.
     *
     * @param paramCachedColorSpace The color space to use.
     */
    public void setColorSpace(PDColorSpace paramCachedColorSpace) {
        // todo 1 review usage, EI
        cachedColorSpace = paramCachedColorSpace;
    }

    public void setDecode(int[] decode) {
        COSArray cosArray;

        cosArray = COSArray.create(decode.length);
        for (int index = 0; index < decode.length; index++) {
            cosArray.add(COSInteger.create(decode[index]));
        }
        cosSetField(DK_Decode, cosArray);
    }

    /**
     * set the height of the raster image
     *
     * @param height the height of the raster image
     */
    public void setHeight(int height) {
        setFieldInt(DK_Height, height);
    }

    public void setImageMask(boolean flag) {
        setFieldBoolean(DK_ImageMask, flag);
    }

    public void setMask(PDXObject object) {
        setFieldObject(DK_Mask, object);
    }

    public void setSMask(PDXObject object) {
        setFieldObject(DK_SMask, object);
    }

    /**
     * set the width of the raster image
     *
     * @param width the width of the raster image
     */
    public void setWidth(int width) {
        setFieldInt(DK_Width, width);
    }
}
