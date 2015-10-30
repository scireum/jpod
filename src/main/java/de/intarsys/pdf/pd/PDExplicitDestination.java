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
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;

/**
 * The explicit reference to a destination in a PDF document, consisting of a
 * page and a definition of the rectangle to be displayed.
 */
public class PDExplicitDestination extends PDDestination {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDDestination.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDExplicitDestination(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_DISPLAY_MODE_XYZ = COSName.constant("XYZ");

    public static final COSName CN_DISPLAY_MODE_Fit = COSName.constant("Fit");

    public static final COSName CN_DISPLAY_MODE_FitH = COSName.constant("FitH");

    public static final COSName CN_DISPLAY_MODE_FitV = COSName.constant("FitV");

    public static final COSName CN_DISPLAY_MODE_FitR = COSName.constant("FitR");

    public static final COSName CN_DISPLAY_MODE_FitB = COSName.constant("FitB");

    public static final COSName CN_DISPLAY_MODE_FitBH = COSName.constant("FitBH");

    public static final COSName CN_DISPLAY_MODE_FitBV = COSName.constant("FitBV");

    protected PDExplicitDestination(COSObject object) {
        super(object);
    }

    public COSName getDisplayMode() {
        COSArray definition = cosGetArray();
        if (definition.size() < 2) {
            return null;
        }
        return definition.get(1).asName();
    }

    /**
     * The destination page. ATTENTION: it is common have dangling destinations
     * to invalid (null) pages around!
     *
     * @return The destination page. Be sure to handle null return values.
     */
    public PDPage getPage(PDDocument doc) {
        COSArray definition = cosGetArray();
        COSObject page = definition.get(0);
        if (page.asNumber() != null) {
            int pageIndex = page.asNumber().intValue();
            return doc.getPageTree().getPageAt(pageIndex);
        }
        if (page.asDictionary() != null) {
            return (PDPage) PDPageNode.META.createFromCos(page.asDictionary());
        }
        return null;
    }

    public float[] getParameters() {
        COSArray definition = cosGetArray();
        int size = definition.size() - 2;
        if (size < 0) {
            return new float[0];
        }
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            COSNumber param = definition.get(i + 2).asNumber();
            if (param == null) {
                result[i] = 0;
            } else {
                result[i] = param.floatValue();
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.pdf.pd.PDDestination#getResolvedDestination(de.intarsys.pdf
     * .pd.PDDoc)
     */
    @Override
    public PDExplicitDestination getResolvedDestination(PDDocument doc) {
        return this;
    }

    public void setDisplayMode(COSName mode) {
        COSArray definition = cosGetArray();
        if (definition.size() == 0) {
            definition.add(COSNull.NULL);
            definition.add(mode);
        } else if (definition.size() == 1) {
            definition.add(mode);
        } else {
            definition.set(1, mode);
        }
    }

    public void setPage(PDPage page) {
        COSArray definition = cosGetArray();
        if (definition.size() == 0) {
            definition.add(page.cosGetObject());
        } else {
            definition.set(0, page.cosGetObject());
        }
    }

    public void setParameters(double[] parameters) {
        COSArray definition = cosGetArray();
        while (definition.size() < (2 + parameters.length)) {
            definition.add(COSNull.NULL);
        }
        for (int i = 0; i < parameters.length; i++) {
            definition.set(i + 2, COSFixed.create(parameters[i]));
        }
    }

    public void setParameters(float[] parameters) {
        COSArray definition = cosGetArray();
        while (definition.size() < (2 + parameters.length)) {
            definition.add(COSNull.NULL);
        }
        for (int i = 2; i < (2 + parameters.length); i++) {
            definition.set(i, COSFixed.create(parameters[i]));
        }
    }
}
