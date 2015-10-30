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
package de.intarsys.pdf.app.acroform;

import de.intarsys.pdf.app.action.ActionTools;
import de.intarsys.pdf.app.action.TriggerEvent;
import de.intarsys.pdf.app.appearance.AppearanceCreatorTools;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.pd.PDAFButtonField;
import de.intarsys.pdf.pd.PDAFSignatureField;
import de.intarsys.pdf.pd.PDAcroFormField;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDSignature;
import de.intarsys.tools.string.Converter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The standard implementation of an {@link IFormHandler}. This one delegates
 * its tasks to some other PDF library components, like appearance creation and
 * action processing.
 */
public class StandardFormHandler extends CommonFormHandler {
    protected StandardFormHandler(PDDocument doc) {
        super(doc);
    }

    @Override
    protected void basicRecalculate(PDAcroFormField source) {
        if (!isCalculate()) {
            return;
        }
        try {
            setCalculate(false);
            List co = getAcroForm().getCalculationOrder();
            if (co == null) {
                return;
            }
            for (Iterator it = co.iterator(); it.hasNext(); ) {
                PDAcroFormField field = (PDAcroFormField) it.next();
                recalculateField(source, field);
            }
        } finally {
            setCalculate(true);
        }
    }

    @Override
    protected void basicSetFieldValue(PDAcroFormField field, List value) {
        if (field == null) {
            return;
        }
        PDAcroFormField rootField = field.getLogicalRoot();
        COSObject oldValue = rootField.cosGetValue();
        rootField.setValueStrings(value);
        COSObject newValue = rootField.cosGetValue();
        if (changed(oldValue, newValue)) {
            createAppearance(rootField);
            recalculate(rootField);
        }
    }

    @Override
    protected void basicSetFieldValue(PDAcroFormField field, PDSignature value) {
        if (field == null) {
            return;
        }
        PDAcroFormField rootField = field.getLogicalRoot();
        if (!rootField.isTypeSig()) {
            throw new IllegalArgumentException("signature field expected"); //$NON-NLS-1$
        }
        PDAFSignatureField sigField = (PDAFSignatureField) rootField;
        COSObject oldValue = sigField.cosGetValue();
        sigField.setSignature(value);
        COSObject newValue = sigField.cosGetValue();
        if (changed(oldValue, newValue)) {
            recalculate(sigField);
        }
    }

    @Override
    protected void basicSetFieldValue(PDAcroFormField field, String value) {
        if (field == null) {
            return;
        }
        PDAcroFormField rootField = field.getLogicalRoot();
        if (rootField.isTypeBtn() && ((PDAFButtonField) rootField).isPushbutton()) {
            basicSetFieldValuePushbutton(rootField, value);
        } else {
            basicSetFieldValueDefault(rootField, value);
        }
    }

    protected void basicSetFieldValueDefault(PDAcroFormField rootField, String value) {
        if (isValidate()) {
            TriggerEvent trigger = triggerValidate(rootField, value);
            if (!trigger.getRc()) {
                return;
            }
            value = trigger.getValueString();
        }
        COSObject oldValue = rootField.cosGetValue();
        rootField.setValueString(value);
        COSObject newValue = rootField.cosGetValue();
        if (changed(oldValue, newValue)) {
            createAppearance(rootField);
            recalculate(rootField);
        }
    }

    protected void basicSetFieldValuePushbutton(PDAcroFormField rootField, String value) {
        Map properties = null;
        if (value == null) {
            properties = new HashMap();
        } else {
            properties = Converter.asMap(value);
        }
        String label = (String) properties.get("label");
        String icon = (String) properties.get("icon");
        // ...
    }

    protected boolean changed(Object oldValue, Object newValue) {
        boolean changed = false;
        if (oldValue == null) {
            if (newValue != null) {
                changed = true;
            }
        } else {
            changed = !oldValue.equals(newValue);
        }
        return changed;
    }

    protected void createAppearance(PDAcroFormField field) {
        if (field.isTypeBtn()) {
            return;
        }
        AppearanceCreatorTools.createAppearance(field.getLogicalRoot());
    }

    @Override
    protected void doResetFields(List fields) {
        for (Iterator i = fields.iterator(); i.hasNext(); ) {
            PDAcroFormField field = (PDAcroFormField) i.next();
            field.reset();
            AppearanceCreatorTools.setAppearanceCreator(field.getLogicalRoot(), null);
            createAppearance(field);
        }
        recalculate();
    }

    protected void recalculateField(PDAcroFormField source, PDAcroFormField field) {
        String fieldValue = field.getValueString();
        TriggerEvent trigger = triggerCalculate(field, fieldValue, source);
        if (trigger.getRc()) {
            String newFieldValue = trigger.getValueString();
            if ((newFieldValue != null) && !newFieldValue.equals(fieldValue)) {
                field.setValueString(trigger.getValueString());
                createAppearance(field);
            }
        }
    }

    protected TriggerEvent triggerCalculate(PDAcroFormField field, String value, PDAcroFormField source) {
        return ActionTools.fieldTriggerCalculate(field, value, source);
    }

    protected TriggerEvent triggerValidate(PDAcroFormField field, String value) {
        return ActionTools.fieldTriggerValidate(field, value);
    }
}
