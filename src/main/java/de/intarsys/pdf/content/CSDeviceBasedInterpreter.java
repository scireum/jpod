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
package de.intarsys.pdf.content;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontTools;
import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.pdf.pd.PDImage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDResources;
import de.intarsys.pdf.pd.PDShading;
import de.intarsys.pdf.pd.PDXObject;

import java.util.Iterator;
import java.util.Map;

/**
 * A content stream interpreter for PDF graphics primitives that delegates
 * execution to an {@link ICSDevice} object.
 */
public class CSDeviceBasedInterpreter extends CSInterpreter {

    private float advanceFactor = 0;

    protected final ICSDevice device;

    private float fontSize = 0;

    private float horizontalScalingFactor = 1;

    private int nesting = 0;

    private boolean supportsColorSpace = true;

    private boolean supportsExtendedState = true;

    private boolean supportsFont = true;

    private boolean supportsInlineImage = true;

    private boolean supportsPattern = true;

    private boolean supportsProperties = true;

    private boolean supportsShading = true;

    private boolean supportsXObject = true;

    public CSDeviceBasedInterpreter(Map paramOptions, ICSDevice device) {
        super(paramOptions);
        this.device = device;
        if (device instanceof ICSDeviceFeatures) {
            supportsColorSpace = ((ICSDeviceFeatures) device).supportsColorSpace();
            supportsExtendedState = ((ICSDeviceFeatures) device).supportsExtendedState();
            supportsFont = ((ICSDeviceFeatures) device).supportsFont();
            supportsInlineImage = ((ICSDeviceFeatures) device).supportsInlineImage();
            supportsPattern = ((ICSDeviceFeatures) device).supportsPattern();
            supportsProperties = ((ICSDeviceFeatures) device).supportsProperties();
            supportsShading = ((ICSDeviceFeatures) device).supportsShading();
            supportsXObject = ((ICSDeviceFeatures) device).supportsXObject();
        }
    }

    /**
     * The {@link ICSDevice} currently associated with the interpreter.
     *
     * @return The {@link ICSDevice} currently associated with the interpreter.
     */
    public ICSDevice getDevice() {
        return device;
    }

    protected PDColorSpace lookupColorSpace(COSName name) {
        if (!supportsColorSpace) {
            return null;
        }
        PDColorSpace result = PDColorSpace.getNamed(name);
        if (result == null) {
            if (getResources() == null) {
                throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
            }
            return getResources().getColorSpaceResource(name);
        }
        return result;
    }

    protected PDExtGState lookupExtGState(COSName name) {
        if (!supportsExtendedState) {
            return null;
        }
        if (getResources() == null) {
            throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
        }
        return getResources().getExtGStateResource(name);
    }

    protected PDFont lookupFont(COSName fontname) {
        if (!supportsFont) {
            return null;
        }
        return PDFontTools.getFont(getDoc(), getResources(), fontname);
    }

    protected PDPattern lookupPattern(COSName name) {
        if (!supportsPattern) {
            return null;
        }
        if (getResources() == null) {
            throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
        }
        return getResources().getPatternResource(name);
    }

    protected COSDictionary lookupProperties(COSName name) {
        if (!supportsProperties) {
            return null;
        }
        if (getResources() == null) {
            throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
        }
        return getResources().cosGetResource(PDResources.CN_RT_Properties, name).asDictionary();
    }

    protected PDShading lookupShading(COSName name) {
        if (!supportsShading) {
            return null;
        }
        if (getResources() == null) {
            throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
        }
        return getResources().getShadingResource(name);
    }

    protected PDXObject lookupXObject(COSName name) {
        if (!supportsXObject) {
            return null;
        }
        if (getResources() == null) {
            throw new IllegalStateException("resource dictionary missing"); //$NON-NLS-1$
        }
        return getResources().getXObjectResource(name);
    }

    @Override
    public void process(CSContent pContent, PDResources resourceDict) {
        try {
            if (nesting == 0) {
                device.open(this);
            }
            nesting++;
            super.process(pContent, resourceDict);
        } finally {
            nesting--;
            if (nesting == 0) {
                device.close();
            }
        }
    }

    @Override
    protected void render_b(CSOperation operation) {
        try {
            device.pathCloseFillStrokeNonZero();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_B(CSOperation operation) {
        try {
            device.pathFillStrokeNonZero();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_BDC(CSOperation operation) {
        COSName tag = operation.getOperand(0).asName();
        COSObject op1 = operation.getOperand(1);
        COSName resourceName;
        COSDictionary properties = null;
        if (op1 instanceof COSName) {
            resourceName = (COSName) op1;
            properties = lookupProperties(resourceName);
        } else {
            resourceName = null;
            properties = (COSDictionary) op1;
        }
        device.markedContentBeginProperties(tag, resourceName, properties);
    }

    @Override
    protected void render_BMC(CSOperation operation) {
        COSName tag = operation.getOperand(0).asName();
        device.markedContentBegin(tag);
    }

    @Override
    protected void render_bstar(CSOperation operation) {
        try {
            device.pathCloseFillStrokeEvenOdd();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_Bstar(CSOperation operation) {
        try {
            device.pathFillStrokeEvenOdd();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_BT(CSOperation operation) {
        frame.graphicsObjectState = TextObject;
        device.textBegin();
    }

    @Override
    protected void render_c(CSOperation operation) {
        float x1 = ((COSNumber) operation.getOperand(0)).floatValue();
        float y1 = ((COSNumber) operation.getOperand(1)).floatValue();
        float x2 = ((COSNumber) operation.getOperand(2)).floatValue();
        float y2 = ((COSNumber) operation.getOperand(3)).floatValue();
        float x3 = ((COSNumber) operation.getOperand(4)).floatValue();
        float y3 = ((COSNumber) operation.getOperand(5)).floatValue();
        device.penCurveToC(x1, y1, x2, y2, x3, y3);
    }

    @Override
    protected void render_cm(CSOperation operation) {
        // if there's an "open" path simply keep it
        if (frame.graphicsObjectState != PageLevel
            && frame.graphicsObjectState != PathObject
            && frame.graphicsObjectState != TextObject) {
            // We accept cm operations within BT...ET because of some non-conformant documents created by our clients (see JIRA PSL-100).
            // Other viewers are rendering this, too...
            throw new CSWarning("'cm' not allowed");
        }
        float a = ((COSNumber) operation.getOperand(0)).floatValue();
        float b = ((COSNumber) operation.getOperand(1)).floatValue();
        float c = ((COSNumber) operation.getOperand(2)).floatValue();
        float d = ((COSNumber) operation.getOperand(3)).floatValue();
        float e = ((COSNumber) operation.getOperand(4)).floatValue();
        float f = ((COSNumber) operation.getOperand(5)).floatValue();
        device.transform(a, b, c, d, e, f);
    }

    @Override
    protected void render_cs(CSOperation operation) {
        COSName name = operation.getOperand(0).asName();
        PDColorSpace colorSpace = selectColorSpace(name);
        device.setNonStrokeColorSpace(name, colorSpace);
    }

    @Override
    protected void render_CS(CSOperation operation) {
        COSName name = operation.getOperand(0).asName();
        PDColorSpace colorSpace = selectColorSpace(name);
        device.setStrokeColorSpace(name, colorSpace);
    }

    @Override
    protected void render_d(CSOperation operation) {
        COSArray cosDashArray = (COSArray) operation.getOperand(0);
        float[] pattern = new float[cosDashArray.size()];
        int i = 0;
        for (Iterator<COSObject> it = cosDashArray.iterator(); it.hasNext(); ) {
            COSNumber element = it.next().asNumber();
            if (element != null) {
                pattern[i] = element.floatValue();
                i++;
            }
        }
        float phase = ((COSNumber) operation.getOperand(1)).floatValue();
        device.setLineDash(pattern, phase);
    }

    @Override
    protected void render_Do(CSOperation operation) {
        COSName name = (COSName) operation.getOperand(0);
        PDXObject xobject = lookupXObject(name);
        device.doXObject(name, xobject);
    }

    @Override
    protected void render_DoubleQuote(CSOperation operation) {
        float ws = ((COSNumber) operation.getOperand(0)).floatValue();
        device.textSetWordSpacing(ws);
        float cs = ((COSNumber) operation.getOperand(1)).floatValue();
        device.textSetCharSpacing(cs);
        device.textLineNew();
        byte[] value = ((COSString) operation.getOperand(2)).byteValue();
        device.textShow(value, 0, value.length);
    }

    @Override
    protected void render_DP(CSOperation operation) {
        COSName tag = operation.getOperand(0).asName();
        COSObject op1 = operation.getOperand(1);
        COSName resourceName;
        COSDictionary properties = null;
        if (op1 instanceof COSName) {
            resourceName = (COSName) op1;
            properties = lookupProperties(resourceName);
        } else {
            resourceName = null;
            properties = (COSDictionary) op1;
        }
        device.markedContentPointProperties(tag, resourceName, properties);
    }

    @Override
    protected void render_EI(CSOperation operation) {
        frame.graphicsObjectState = InLineImageObject;
        try {
            PDImage image = (PDImage) operation.getCache();
            if (image == null && supportsInlineImage) {
                COSStream cosStream = operation.getOperand(0).asStream();
                image = (PDImage) PDImage.META.createFromCos(cosStream);
                COSObject cs = image.cosGetColorSpace();
                if (cs instanceof COSName) {
                    if (getResources() != null) {
                        /*
                         * will be null if cs is one of the predefined names. pd
                         * image will resolve lazily then
                         */
                        PDColorSpace pdCS = getResources().getColorSpaceResource((COSName) cs);
                        if (pdCS != null) {
                            image.setColorSpace(pdCS);
                        }
                    }
                }
                operation.setCache(image);
            }
            device.inlineImage(image);
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_EMC(CSOperation operation) {
        device.markedContentEnd();
    }

    @Override
    protected void render_ET(CSOperation operation) {
        try {
            device.textEnd();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_f(CSOperation operation) {
        try {
            device.pathFillNonZero();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_F(CSOperation operation) {
        try {
            device.pathFillNonZero();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_fstar(CSOperation operation) {
        try {
            device.pathFillEvenOdd();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_g(CSOperation operation) {
        if (frame.defaultGray != null) {
            device.setNonStrokeColorSpace(PDColorSpace.CN_CS_DefaultGray, frame.defaultGray);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue()};
            device.setNonStrokeColorValues(values);
        } else {
            device.setNonStrokeColorGray(((COSNumber) operation.getOperand(0)).floatValue());
        }
    }

    @Override
    protected void render_G(CSOperation operation) {
        if (frame.defaultGray != null) {
            device.setStrokeColorSpace(PDColorSpace.CN_CS_DefaultGray, frame.defaultGray);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue()};
            device.setStrokeColorValues(values);
        } else {
            device.setStrokeColorGray(((COSNumber) operation.getOperand(0)).floatValue());
        }
    }

    @Override
    protected void render_gs(CSOperation operation) {
        COSName name = operation.getOperand(0).asName();
        PDExtGState gstate = lookupExtGState(name);
        device.setExtendedState(name, gstate);
    }

    @Override
    protected void render_h(CSOperation operation) {
        device.pathClose();
    }

    @Override
    protected void render_i(CSOperation operation) {
        float flatness = ((COSNumber) operation.getOperand(0)).floatValue();
        device.setFlatnessTolerance(flatness);
    }

    @Override
    protected void render_j(CSOperation operation) {
        int joinStyle = ((COSNumber) operation.getOperand(0)).intValue();
        device.setLineJoin(joinStyle);
    }

    @Override
    protected void render_J(CSOperation operation) {
        int cap = ((COSNumber) operation.getOperand(0)).intValue();
        device.setLineCap(cap);
    }

    @Override
    protected void render_k(CSOperation operation) {
        if (frame.defaultCMYK != null) {
            device.setNonStrokeColorSpace(PDColorSpace.CN_CS_DefaultCMYK, frame.defaultCMYK);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue(),
                              ((COSNumber) operation.getOperand(1)).floatValue(),
                              ((COSNumber) operation.getOperand(2)).floatValue(),
                              ((COSNumber) operation.getOperand(3)).floatValue()};
            device.setNonStrokeColorValues(values);
        } else {
            device.setNonStrokeColorCMYK(((COSNumber) operation.getOperand(0)).floatValue(),
                                         ((COSNumber) operation.getOperand(1)).floatValue(),
                                         ((COSNumber) operation.getOperand(2)).floatValue(),
                                         ((COSNumber) operation.getOperand(3)).floatValue());
        }
    }

    @Override
    protected void render_K(CSOperation operation) {
        if (frame.defaultCMYK != null) {
            device.setStrokeColorSpace(PDColorSpace.CN_CS_DefaultCMYK, frame.defaultCMYK);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue(),
                              ((COSNumber) operation.getOperand(1)).floatValue(),
                              ((COSNumber) operation.getOperand(2)).floatValue(),
                              ((COSNumber) operation.getOperand(3)).floatValue()};
            device.setStrokeColorValues(values);
        } else {
            device.setStrokeColorCMYK(((COSNumber) operation.getOperand(0)).floatValue(),
                                      ((COSNumber) operation.getOperand(1)).floatValue(),
                                      ((COSNumber) operation.getOperand(2)).floatValue(),
                                      ((COSNumber) operation.getOperand(3)).floatValue());
        }
    }

    @Override
    protected void render_l(CSOperation operation) {
        device.penLineTo(((COSNumber) operation.getOperand(0)).floatValue(),
                         ((COSNumber) operation.getOperand(1)).floatValue());
    }

    @Override
    protected void render_m(CSOperation operation) {
        frame.graphicsObjectState = PathObject;
        device.penMoveTo(((COSNumber) operation.getOperand(0)).floatValue(),
                         ((COSNumber) operation.getOperand(1)).floatValue());
    }

    @Override
    protected void render_M(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        device.setMiterLimit(value);
    }

    @Override
    protected void render_MP(CSOperation operation) {
        COSName tag = operation.getOperand(0).asName();
        device.markedContentPoint(tag);
    }

    @Override
    protected void render_n(CSOperation operation) {
        try {
            device.pathEnd();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_q(CSOperation operation) {
        // if there's an "open" path simply keep it
        if (frame.graphicsObjectState != PageLevel && frame.graphicsObjectState != PathObject) {
            throw new CSWarning("'q' not allowed");
        }
        device.saveState();
    }

    @Override
    protected void render_Q(CSOperation operation) {
        // if there's an "open" path simply keep it
        if (frame.graphicsObjectState != PageLevel && frame.graphicsObjectState != PathObject) {
            throw new CSWarning("'Q' not allowed");
        }
        device.restoreState();
    }

    @Override
    protected void render_Quote(CSOperation operation) {
        device.textLineNew();
        byte[] value = ((COSString) operation.getOperand(0)).byteValue();
        device.textShow(value, 0, value.length);
    }

    @Override
    protected void render_re(CSOperation operation) {
        float x = ((COSNumber) operation.getOperand(0)).floatValue();
        float y = ((COSNumber) operation.getOperand(1)).floatValue();
        float width = ((COSNumber) operation.getOperand(2)).floatValue();
        float height = ((COSNumber) operation.getOperand(3)).floatValue();
        device.penRectangle(x, y, width, height);
    }

    @Override
    protected void render_rg(CSOperation operation) {
        if (frame.defaultRGB != null) {
            device.setNonStrokeColorSpace(PDColorSpace.CN_CS_DefaultRGB, frame.defaultRGB);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue(),
                              ((COSNumber) operation.getOperand(1)).floatValue(),
                              ((COSNumber) operation.getOperand(2)).floatValue()};
            device.setNonStrokeColorValues(values);
        } else {
            device.setNonStrokeColorRGB(((COSNumber) operation.getOperand(0)).floatValue(),
                                        ((COSNumber) operation.getOperand(1)).floatValue(),
                                        ((COSNumber) operation.getOperand(2)).floatValue());
        }
    }

    @Override
    protected void render_RG(CSOperation operation) {
        if (frame.defaultRGB != null) {
            device.setStrokeColorSpace(PDColorSpace.CN_CS_DefaultRGB, frame.defaultRGB);
            float[] values = {((COSNumber) operation.getOperand(0)).floatValue(),
                              ((COSNumber) operation.getOperand(1)).floatValue(),
                              ((COSNumber) operation.getOperand(2)).floatValue()};
            device.setStrokeColorValues(values);
        } else {
            device.setStrokeColorRGB(((COSNumber) operation.getOperand(0)).floatValue(),
                                     ((COSNumber) operation.getOperand(1)).floatValue(),
                                     ((COSNumber) operation.getOperand(2)).floatValue());
        }
    }

    @Override
    protected void render_ri(CSOperation operation) {
        COSName intent = operation.getOperand(0).asName();
        device.setRenderingIntent(intent);
    }

    @Override
    protected void render_s(CSOperation operation) {
        try {
            device.pathCloseStroke();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_S(CSOperation operation) {
        try {
            device.pathStroke();
        } finally {
            frame.graphicsObjectState = PageLevel;
        }
    }

    @Override
    protected void render_sc(CSOperation operation) {
        // set color for non stroking
        int totalSize = operation.operandSize();
        float[] values = new float[totalSize];
        for (int i = 0; i < totalSize; i++) {
            values[i] = ((COSNumber) operation.getOperand(i)).floatValue();
        }
        device.setNonStrokeColorValues(values);
    }

    @Override
    protected void render_SC(CSOperation operation) {
        // set color for stroking
        int totalSize = operation.operandSize();
        float[] values = new float[totalSize];
        for (int i = 0; i < totalSize; i++) {
            values[i] = ((COSNumber) operation.getOperand(i)).floatValue();
        }
        device.setStrokeColorValues(values);
    }

    @Override
    protected void render_scn(CSOperation operation) {
        // set color for non stroking (ICCBased, special color spaces)
        int totalSize = operation.operandSize();
        int numberSize = totalSize;
        COSName patternName = null;
        if ((totalSize > 0) && (operation.getOperand(totalSize - 1) instanceof COSName)) {
            patternName = (COSName) operation.getOperand(totalSize - 1);
            numberSize--;
        }
        float[] values = new float[numberSize];
        for (int i = 0; i < numberSize; i++) {
            values[i] = ((COSNumber) operation.getOperand(i)).floatValue();
        }
        if (patternName == null) {
            device.setNonStrokeColorValues(values);
        } else {
            PDPattern pattern = lookupPattern(patternName);
            device.setNonStrokeColorValues(values, patternName, pattern);
        }
    }

    @Override
    protected void render_SCN(CSOperation operation) {
        int totalSize = operation.operandSize();
        int numberSize = totalSize;
        COSName patternName = null;
        if ((totalSize > 0) && (operation.getOperand(totalSize - 1) instanceof COSName)) {
            patternName = (COSName) operation.getOperand(totalSize - 1);
            numberSize--;
        }
        float[] values = new float[numberSize];
        for (int i = 0; i < numberSize; i++) {
            values[i] = ((COSNumber) operation.getOperand(i)).floatValue();
        }
        if (patternName == null) {
            device.setStrokeColorValues(values);
        } else {
            PDPattern pattern = lookupPattern(patternName);
            device.setStrokeColorValues(values, patternName, pattern);
        }
    }

    @Override
    protected void render_sh(CSOperation operation) {
        COSName name = operation.getOperand(0).asName();
        PDShading shading = lookupShading(name);
        device.doShading(name, shading);
    }

    @Override
    protected void render_Tc(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        device.textSetCharSpacing(value);
    }

    @Override
    protected void render_Td(CSOperation operation) {
        float x = ((COSNumber) operation.getOperand(0)).floatValue();
        float y = ((COSNumber) operation.getOperand(1)).floatValue();
        device.textLineMove(x, y);
    }

    @Override
    protected void render_TD(CSOperation operation) {
        float x = ((COSNumber) operation.getOperand(0)).floatValue();
        float y = ((COSNumber) operation.getOperand(1)).floatValue();
        device.textSetLeading(y);
        device.textLineMove(x, y);
    }

    @Override
    protected void render_Tf(CSOperation operation) {
        COSName fontname = operation.getOperand(0).asName();
        PDFont pdFont = lookupFont(fontname);
        fontSize = operation.getOperand(1).asNumber().floatValue();
        advanceFactor = -1f * horizontalScalingFactor * fontSize / 1000;
        device.textSetFont(fontname, pdFont, fontSize);
    }

    @Override
    protected void render_Tj(CSOperation operation) {
        byte[] value = ((COSString) operation.getOperand(0)).byteValue();
        device.textShow(value, 0, value.length);
    }

    @Override
    protected void render_TJ(CSOperation operation) {
        // optimization: access plain array, only direct objects are contained!
        COSDocumentElement[] array = operation.getOperand(0).asArray().toArray();
        int length = array.length;
        for (int i = 0; i < length; i++) {
            COSObject cosObj = (COSObject) array[i];
            if (cosObj instanceof COSNumber) {
                float offset = ((COSNumber) cosObj).floatValue();
                device.textMove(offset * advanceFactor, 0);
            } else if (cosObj instanceof COSString) {
                byte[] value = ((COSString) cosObj).byteValue();
                device.textShow(value, 0, value.length);
            } else {
                // todo 2 report warning
                continue;
            }
        }
    }

    @Override
    protected void render_TL(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        device.textSetLeading(-value);
    }

    @Override
    protected void render_Tm(CSOperation operation) {
        float a = ((COSNumber) operation.getOperand(0)).floatValue();
        float b = ((COSNumber) operation.getOperand(1)).floatValue();
        float c = ((COSNumber) operation.getOperand(2)).floatValue();
        float d = ((COSNumber) operation.getOperand(3)).floatValue();
        float e = ((COSNumber) operation.getOperand(4)).floatValue();
        float f = ((COSNumber) operation.getOperand(5)).floatValue();
        device.textSetTransform(a, b, c, d, e, f);
    }

    @Override
    protected void render_Tr(CSOperation operation) {
        int value = ((COSNumber) operation.getOperand(0)).intValue();
        device.textSetRenderingMode(value);
    }

    @Override
    protected void render_Ts(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        device.textSetRise(value);
    }

    @Override
    protected void render_Tstar(CSOperation operation) {
        device.textLineNew();
    }

    @Override
    protected void render_Tw(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        device.textSetWordSpacing(value);
    }

    @Override
    protected void render_Tz(CSOperation operation) {
        float value = ((COSNumber) operation.getOperand(0)).floatValue();
        horizontalScalingFactor = value / 100;
        advanceFactor = -1f * horizontalScalingFactor * fontSize / 1000;
        device.textSetHorizontalScaling(value);
    }

    @Override
    protected void render_v(CSOperation operation) {
        float x2 = ((COSNumber) operation.getOperand(0)).floatValue();
        float y2 = ((COSNumber) operation.getOperand(1)).floatValue();
        float x3 = ((COSNumber) operation.getOperand(2)).floatValue();
        float y3 = ((COSNumber) operation.getOperand(3)).floatValue();
        device.penCurveToV(x2, y2, x3, y3);
    }

    @Override
    protected void render_w(CSOperation operation) {
        device.setLineWidth(((COSNumber) operation.getOperand(0)).floatValue());
    }

    @Override
    protected void render_W(CSOperation operation) {
        frame.graphicsObjectState = ClippingObject;
        device.pathClipNonZero();
    }

    @Override
    protected void render_Wstar(CSOperation operation) {
        frame.graphicsObjectState = ClippingObject;
        device.pathClipEvenOdd();
    }

    @Override
    protected void render_y(CSOperation operation) {
        float x1 = ((COSNumber) operation.getOperand(0)).floatValue();
        float y1 = ((COSNumber) operation.getOperand(1)).floatValue();
        float x2 = ((COSNumber) operation.getOperand(2)).floatValue();
        float y2 = ((COSNumber) operation.getOperand(3)).floatValue();
        device.penCurveToY(x1, y1, x2, y2);
    }

    protected PDColorSpace selectColorSpace(COSName name) {
        PDColorSpace result = null;
        if (PDColorSpace.CN_CS_DeviceCMYK.equals(name)) {
            result = frame.defaultCMYK;
        } else if (PDColorSpace.CN_CS_DeviceRGB.equals(name)) {
            result = frame.defaultRGB;
        } else if (PDColorSpace.CN_CS_DeviceGray.equals(name)) {
            result = frame.defaultGray;
        }
        if (result == null) {
            return lookupColorSpace(name);
        }
        return result;
    }
}
