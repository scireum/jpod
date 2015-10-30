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
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

import java.util.Iterator;
import java.util.Set;

/**
 * The annotation representing a field in an AcroForm.
 */
public class PDWidgetAnnotation extends PDAnnotation {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDAnnotation.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDWidgetAnnotation(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    /**
     * Highlighting Mode
     */
    public static final COSName DK_H = COSName.constant("H");

    /**
     * highlighting: N: None
     */
    public static final COSName CN_H_N = COSName.constant("N");

    /**
     * highlighting: I: Invert
     */
    public static final COSName CN_H_I = COSName.constant("I");

    /**
     * highlighting: O: Outline
     */
    public static final COSName CN_H_O = COSName.constant("O");

    /**
     * highlighting: P: Push
     */
    public static final COSName CN_H_P = COSName.constant("P");

    /**
     * highlighting: T: Toggle
     */
    public static final COSName CN_H_T = COSName.constant("T");

    public static final COSName DK_MK = COSName.constant("MK");

    public static final COSName CN_Subtype_Widget = COSName.constant("Widget");

    public static final COSName CN_State_Off = COSName.constant("Off");

    protected PDWidgetAnnotation(COSObject object) {
        super(object);
    }

    @Override
    public boolean canReceiveFocus() {
        if (super.canReceiveFocus()) {
            PDAcroFormField field = getAcroFormField();
            return !field.isReadOnly() && (field.isTypeTx() || field.isTypeBtn() || field.isTypeCh());
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedSubtype()
     */
    @Override
    protected COSName cosGetExpectedSubtype() {
        return CN_Subtype_Widget;
    }

    @Override
    public void dispose() {
        // remove the annotation
        super.dispose();
        // dispose the field if there are no more annotations associated with it
        getAcroFormField().dispose(true);
    }

    public PDAcroFormField getAcroFormField() {
        return (PDAcroFormField) PDAcroFormField.META.createFromCos(cosGetDict());
    }

    public COSName getAltAppearanceState() {
        COSName newState = null;
        COSName oldState = getAppearanceState();
        Set states = getAppearanceStates();
        states.add(COSName.create("Off"));
        for (Iterator it = states.iterator(); it.hasNext(); ) {
            COSName state = (COSName) it.next();
            if (!state.equals(oldState)) {
                newState = state;
                break;
            }
        }
        if (getAcroFormField().isTypeBtn()) {
            PDAFButtonField field = (PDAFButtonField) getAcroFormField().getLogicalRoot();
            COSArray opts = field.cosGetOptions();
            if (opts != null && !PDWidgetAnnotation.CN_State_Off.equals(newState)) {
                COSString optionValue = opts.get(Integer.parseInt(newState.stringValue())).asString();
                newState = COSName.create(optionValue.stringValue());
            }
        }
        return newState;
    }

    public PDAppearanceCharacteristics getAppearanceCharacteristics() {
        return (PDAppearanceCharacteristics) PDAppearanceCharacteristics.META.createFromCos(cosGetField(DK_MK));
    }

    public COSName getHighlightingMode() {
        COSName mode = cosGetField(DK_H).asName();
        if (mode != null) {
            return mode;
        }
        return CN_H_I; // default
    }

    @Override
    public String getSubtypeLabel() {
        return "Widget";
    }

    public boolean isOff() {
        COSName state = getAppearanceState();
        return (state == null) || state.equals(CN_State_Off);
    }

    @Override
    public boolean isWidgetAnnotation() {
        return true;
    }

    public void setAppearanceCharacteristics(PDAppearanceCharacteristics newAppearanceCharacteristics) {
        setFieldObject(DK_MK, newAppearanceCharacteristics);
    }

    public void setHighlightingMode(COSName newHighlightingMode) {
        if ((newHighlightingMode == null) || CN_H_I.equals(newHighlightingMode)) {
            cosRemoveField(DK_H);
        } else {
            cosSetField(DK_H, newHighlightingMode);
        }
    }

    /**
     * Change a given rectangle and matrix (belonging to a form) according to
     * the annotations defined rotation.
     *
     * @param rect   The rectangle to transform
     * @param matrix The matrix to change
     */
    public void transform(CDSRectangle rect, CDSMatrix matrix) {
        int rotation = 0;
        PDAppearanceCharacteristics ac = getAppearanceCharacteristics();
        if (ac != null) {
            rotation = ac.getRotation();
        }
        PDAnnotationTools.transform(rect, matrix, rotation);
    }
}
