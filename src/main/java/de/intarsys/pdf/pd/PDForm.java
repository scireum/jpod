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

import de.intarsys.pdf.cds.CDSMatrix;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.content.IContentStreamProvider;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

import java.awt.geom.Rectangle2D;

/**
 * A form object. A form object specifies a reusable graphical object (not an
 * AcroForm).
 */
public class PDForm extends PDXObject implements IContentStreamProvider {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDXObject.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDForm(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    //
    public static final COSName CN_Subtype_Form = COSName.constant("Form"); //$NON-NLS-1$

    public static final COSName DK_BBox = COSName.constant("BBox"); //$NON-NLS-1$

    public static final COSName DK_Matrix = COSName.constant("Matrix"); //$NON-NLS-1$

    public static final COSName DK_PieceInfo = COSName.constant("PieceInfo"); //$NON-NLS-1$

    public static final COSName DK_FormType = COSName.constant("FormType"); //$NON-NLS-1$

    public static final COSName DK_OPI = COSName.constant("OPI"); //$NON-NLS-1$

    public static final COSName DK_PS = COSName.constant("PS"); //$NON-NLS-1$

    public static final COSName DK_Ref = COSName.constant("Ref"); //$NON-NLS-1$

    public static final COSName DK_Subtype2 = COSName.constant("Subtype2"); //$NON-NLS-1$

    public static final COSName DK_Group = COSName.constant("Group"); //$NON-NLS-1$

    /**
     * Cached content of the form.
     * <p>
     * <p>
     * Requesting the content is frequent and expensive.
     * </p>
     */
    private CSContent cachedContent;

    /**
     * Cached transformed rectangle.
     */
    private Rectangle2D cachedTransformedBBox;

    /**
     * Create the receiver class from an already defined {@link COSStream}.
     * NEVER use the constructor directly.
     *
     * @param object the PDDocument containing the new object
     */
    protected PDForm(COSObject object) {
        super(object);
    }

    public void setApplicationData(COSName name, PDApplicationData data) {
        COSDictionary pid = cosGetPieceInfo();
        if (pid == null) {
            pid = COSDictionary.create();
            cosSetPieceInfo(pid);
        }
        pid.put(name, data.cosGetDict());
    }

    public PDApplicationData getApplicationData(COSName name) {
        COSDictionary pid = cosGetPieceInfo();
        if (pid == null) {
            return null;
        }
        COSDictionary pi = pid.get(name).asDictionary();
        if (pi == null) {
            return null;
        }
        return (PDApplicationData) PDApplicationData.META.createFromCos(pi);
    }

    /**
     * Set the bounding box of the receiver.
     *
     * @param rect The new bounding box of the receiver.
     */
    public void setBoundingBox(CDSRectangle rect) {
        setFieldObject(DK_BBox, rect);
    }

    /**
     * The bounding box of the receiver form.
     *
     * @return The bounding box of the receiver form.
     */
    public CDSRectangle getBoundingBox() {
        COSArray array = cosGetField(DK_BBox).asArray();
        if (array == null) {
            return null;
        }
        return CDSRectangle.createFromCOS(array);
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

    @Override
    public boolean isForm() {
        return true;
    }

    /**
     * Set the new variable content of the stream.
     *
     * @param content The new variable content of the stream.
     */
    public void setMarkedContent(byte[] content) {
        CSContent contentStream;
        byte[] bytes = getBytes();
        if (bytes == null) {
            bytes = new byte[0];
        }
        contentStream = CSContent.createFromBytes(bytes);
        contentStream.setMarkedContent(CSOperation.OPERAND_Tx, content);
        setBytes(contentStream.toByteArray());
    }

    /**
     * Set the matrix of the receiver.
     *
     * @param matrix The new matrix of the receiver.
     */
    public void setMatrix(CDSMatrix matrix) {
        setFieldObject(DK_Matrix, matrix);
    }

    /**
     * The form matrix of the receiver form.
     *
     * @return The form matrix of the receiver form.
     */
    public CDSMatrix getMatrix() {
        return CDSMatrix.createFromCOS(cosGetField(DK_Matrix).asArray());
    }

    /**
     * The resource dictionary of the receiver form. This method can return null
     * if no resource dictionary is available.
     *
     * @return The resource dictionary of the receiver form.
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

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
     */
    @Override
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_Form;
    }

    /**
     * Compute a box according to the PDF specification that completely
     * encompasses the transformed bounding box of the form.
     *
     * @return a box according to the PDF specification that completely
     * encompasses the transformed bounding box of the form.
     */
    public Rectangle2D getTransformedBBox() {
        if (cachedTransformedBBox == null) {
            Rectangle2D.Float bbox = (Rectangle2D.Float) getBoundingBox().toRectangle();
            CDSMatrix matrix = getMatrix();

            // transform all corners and lookup extremes
            float minX;
            float minY;
            float maxX;
            float maxY;
            float[] vec;
            vec = new float[]{bbox.x,
                              bbox.y,
                              bbox.x + bbox.width,
                              bbox.y,
                              bbox.x + bbox.width,
                              bbox.y + bbox.height,
                              bbox.x,
                              bbox.y + bbox.height};
            float[] tvec = vec;
            if (matrix != null) {
                tvec = matrix.transform(vec);
            }
            minX = tvec[0];
            minY = tvec[1];
            maxX = tvec[0];
            maxY = tvec[1];
            minX = Math.min(minX, tvec[2]);
            minY = Math.min(minY, tvec[3]);
            maxX = Math.max(maxX, tvec[2]);
            maxY = Math.max(maxY, tvec[3]);
            minX = Math.min(minX, tvec[4]);
            minY = Math.min(minY, tvec[5]);
            maxX = Math.max(maxX, tvec[4]);
            maxY = Math.max(maxY, tvec[5]);
            minX = Math.min(minX, tvec[6]);
            minY = Math.min(minY, tvec[7]);
            maxX = Math.max(maxX, tvec[6]);
            maxY = Math.max(maxY, tvec[7]);
            //
            cachedTransformedBBox = new Rectangle2D.Float(minX, minY, maxX - minX, maxY - minY);
        }
        return cachedTransformedBBox;
    }

    /**
     * Add new marked content to the stream.
     *
     * @param content The new variable content of the stream.
     */
    public void addMarkedContent(byte[] content) {
        CSContent contentStream;
        byte[] bytes = getBytes();
        if (bytes == null) {
            bytes = new byte[0];
        }
        contentStream = CSContent.createFromBytes(bytes);
        contentStream.addMarkedContent(CSOperation.OPERAND_Tx, content);
        setBytes(contentStream.toByteArray());
    }

    /**
     * The piece info dictionary of the document.
     *
     * @return The piece info dictionary of the document.
     */
    public COSDictionary cosGetPieceInfo() {
        return cosGetField(DK_PieceInfo).asDictionary();
    }

    /**
     * Set the piece info dictionary of the document.
     *
     * @param dict The piece info dictionary of the document.
     * @return The /PieceInfo entry previously associated with this.
     */
    public COSDictionary cosSetPieceInfo(COSDictionary dict) {
        if (dict != null) {
            dict.beIndirect();
        }
        return cosSetField(DK_PieceInfo, dict).asDictionary();
    }

    @Override
    public void invalidateCaches() {
        super.invalidateCaches();
        cachedContent = null;
        cachedTransformedBBox = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#initializeFromScratch()
     */
    @Override
    protected void initializeFromScratch() {
        super.initializeFromScratch();
        setBoundingBox(new CDSRectangle());
        setMatrix(new CDSMatrix());
        cosSetField(DK_Resources, COSDictionary.create());
        cosSetField(DK_FormType, COSInteger.create(1));
        setBytes(new byte[0]);
    }
}
