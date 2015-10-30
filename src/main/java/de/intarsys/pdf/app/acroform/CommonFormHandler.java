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

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.pd.PDAcroForm;
import de.intarsys.pdf.pd.PDAcroFormField;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDSignature;
import de.intarsys.pdf.pd.PDWidgetAnnotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A common superclass for implementing {@link IFormHandler}.
 */
public abstract class CommonFormHandler implements IFormHandler {
    private PDDocument doc;

    private PDAcroForm acroForm;

    private boolean validate = true;

    private boolean calculate = true;

    protected CommonFormHandler(PDDocument doc) {
        super();
        this.doc = doc;
        if (doc.getAcroForm() == null) {
            throw new IllegalArgumentException("doc has no form"); //$NON-NLS-1$
        }
        acroForm = getDoc().getAcroForm();
    }

    abstract protected void basicRecalculate(PDAcroFormField field);

    abstract protected void basicSetFieldValue(PDAcroFormField field, List value);

    abstract protected void basicSetFieldValue(PDAcroFormField field, PDSignature value);

    abstract protected void basicSetFieldValue(PDAcroFormField field, String value);

    protected abstract void doResetFields(List fields);

    protected PDAcroForm getAcroForm() {
        return acroForm;
    }

    public PDDocument getDoc() {
        return doc;
    }

    public String getFieldValue(Object fieldref) {
        PDAcroFormField field = marshalField(fieldref);
        return field.getValueString();
    }

    public boolean isCalculate() {
        return calculate;
    }

    public boolean isValidate() {
        return validate;
    }

    protected PDAcroFormField marshalField(Object fieldRef) {
        if (fieldRef instanceof PDWidgetAnnotation) {
            return ((PDWidgetAnnotation) fieldRef).getAcroFormField();
        }
        if (fieldRef instanceof PDAcroFormField) {
            return (PDAcroFormField) fieldRef;
        }
        if (fieldRef instanceof COSString) {
            fieldRef = ((COSString) fieldRef).stringValue();
        }
        if (fieldRef instanceof COSName) {
            fieldRef = ((COSName) fieldRef).stringValue();
        }
        if (fieldRef instanceof String) {
            return getAcroForm().getField((String) fieldRef);
        }
        throw new IllegalArgumentException("fieldRef of unknown type '" //$NON-NLS-1$
                                           + fieldRef.getClass().getName() + "'"); //$NON-NLS-1$
    }

    public void recalculate() {
        basicRecalculate(null);
    }

    public void recalculate(Object fieldRef) {
        basicRecalculate(marshalField(fieldRef));
    }

    public void resetFields() {
        doResetFields(getAcroForm().collectLeafFields());
    }

    public void resetFields(List fieldNames, boolean invert) {
        List fields = null;
        if (invert) {
            fields = getAcroForm().collectLeafFields();
            for (Iterator i = fieldNames.iterator(); i.hasNext(); ) {
                String fieldName = (String) i.next();
                PDAcroFormField field = getAcroForm().getField(fieldName);
                if (field != null) {
                    fields.removeAll(field.collectLeafFields());
                }
            }
        } else {
            fields = new ArrayList();
            for (Iterator i = fieldNames.iterator(); i.hasNext(); ) {
                String fieldName = (String) i.next();
                PDAcroFormField field = getAcroForm().getField(fieldName);
                if (field != null) {
                    fields.addAll(field.collectLeafFields());
                }
            }
        }
        doResetFields(fields);
    }

    public void setCalculate(boolean calculate) {
        this.calculate = calculate;
    }

    public void setFieldValue(Object fieldRef, Object value) {
        if (value instanceof List) {
            basicSetFieldValue(marshalField(fieldRef), (List) value);
        } else if (value instanceof String) {
            basicSetFieldValue(marshalField(fieldRef), (String) value);
        } else if (value instanceof COSName) {
            basicSetFieldValue(marshalField(fieldRef), ((COSName) value).stringValue());
        } else if (value instanceof COSString) {
            basicSetFieldValue(marshalField(fieldRef), ((COSString) value).stringValue());
        } else if (value instanceof PDSignature) {
            basicSetFieldValue(marshalField(fieldRef), (PDSignature) value);
        } else if (value instanceof COSNull) {
            basicSetFieldValue(marshalField(fieldRef), (String) null);
        } else if (value == null) {
            basicSetFieldValue(marshalField(fieldRef), (String) null);
        } else {
            value = String.valueOf(value);
            basicSetFieldValue(marshalField(fieldRef), (String) value);
        }
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }
}
