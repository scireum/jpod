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

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.IContentStreamProvider;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * A tile used to fill the shape.
 */
public class PDTilingPattern extends PDPattern implements IContentStreamProvider {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDPattern.MetaClass {
        protected MetaClass(Class<?> paramInstanceClass) {
            super(paramInstanceClass);
        }

        @Override
        protected COSObject doCreateCOSObject() {
            return COSStream.create(null);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDTilingPattern(object);
        }
    }

    public static final COSName DK_PaintType = COSName.constant("PaintType"); //$NON-NLS-1$
    public static final COSName DK_BBox = COSName.constant("BBox"); //$NON-NLS-1$
    public static final COSName DK_Resources = COSName.constant("Resources"); //$NON-NLS-1$
    public static final COSName DK_XStep = COSName.constant("XStep"); //$NON-NLS-1$
    public static final COSName DK_YStep = COSName.constant("YStep"); //$NON-NLS-1$
    public static final COSName DK_TilingType = COSName.constant("TilingType"); //$NON-NLS-1$

    public static final int PAINT_TYPE_COLORED = 1;
    public static final int PAINT_TYPE_UNCOLORED = 2;

    public static final int TILING_TYPE_CONSTANT_SPACING = 1;
    public static final int TILING_TYPE_NO_DISTORTION = 2;
    public static final int TILING_TYPE_CONSTANT_SPACING_AND_FASTER_TILING = 3;

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * Cached content of the pattern.
     * <p>
     * <p>
     * Requesting the content is expensive.
     * </p>
     */
    private CSContent cachedContent;

    protected PDTilingPattern(COSObject object) {
        super(object);
    }

    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();

        setFieldInt(DK_PatternType, PATTERN_TYPE_TILING);
        setPaintType(PAINT_TYPE_COLORED);
        setTilingType(TILING_TYPE_CONSTANT_SPACING);
        setBoundingBox(new CDSRectangle());
        cosSetField(DK_Resources, COSDictionary.create());
        setBytes(new byte[0]);
    }

    @Override
    public COSDictionary cosGetDict() {
        return cosGetStream().getDict();
    }

    @Override
    public int getPatternType() {
        return PATTERN_TYPE_TILING;
    }

    public int getPaintType() {
        return getFieldInt(DK_PaintType, PAINT_TYPE_COLORED);
    }

    public void setPaintType(int paintType) {
        setFieldInt(DK_PaintType, paintType);
    }

    public int getTilingType() {
        return getFieldInt(DK_TilingType, TILING_TYPE_CONSTANT_SPACING);
    }

    public void setTilingType(int tilingType) {
        setFieldInt(DK_TilingType, tilingType);
    }

    public float getXStep() {
        return getFieldFixed(DK_XStep, 1.0f);
    }

    public void setXStep(float step) {
        setFieldFixed(DK_XStep, step);
    }

    public float getYStep() {
        return getFieldFixed(DK_YStep, 1.0f);
    }

    public void setYStep(float step) {
        setFieldFixed(DK_YStep, step);
    }

    public CDSRectangle getBoundingBox() {
        COSArray array = cosGetField(DK_BBox).asArray();
        if (array == null) {
            return null;
        }
        return CDSRectangle.createFromCOS(array);
    }

    public void setBoundingBox(CDSRectangle rect) {
        setFieldObject(DK_BBox, rect);
    }

    @Override
    public CSContent getContentStream() {
        if (cachedContent == null) {
            cachedContent = CSContent.createFromCos(cosGetStream());
        }
        return cachedContent;
    }

    @Override
    public void setContentStream(CSContent content) {
        setBytes(content.toByteArray());
    }

    public void setBytes(byte[] bytes) {
        cosGetStream().setDecodedBytes(bytes);
    }

    /**
     * The resource dictionary of this pattern. This method can return
     * null if no resource dictionary is available. Spec lists resource
     * dictionary as required however.
     *
     * @return The resource dictionary of the receiver pattern.
     */
    @Override
    public PDResources getResources() {
        COSDictionary r = cosGetField(DK_Resources).asDictionary();
        return (PDResources) PDResources.META.createFromCos(r);
    }

    @Override
    public void setResources(PDResources resources) {
        setFieldObject(DK_Resources, resources);
    }
}
