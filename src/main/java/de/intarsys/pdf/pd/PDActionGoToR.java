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

import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSBoolean;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.tools.kernel.PDFFileTools;

import java.io.File;

/**
 * The GoToR action.
 * <p>
 * When executed the action focuses a viewer to a new destination.
 */
public class PDActionGoToR extends PDAction {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDAction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDActionGoToR(object);
        }

        @Override
        protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
            return PDActionGoToR.META;
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_ActionType_GoToR = COSName.constant("GoToR");

    public static final COSName DK_D = COSName.constant("D");

    public static final COSName DK_F = COSName.constant("F");

    public static final COSName DK_NewWindow = COSName.constant("NewWindow"); //$NON-NLS-1$

    private PDDestination destination;

    protected PDActionGoToR(COSObject object) {
        super(object);
    }

    @Override
    public COSName cosGetExpectedActionType() {
        return CN_ActionType_GoToR;
    }

    public PDDestination getDestination() {
        if (destination == null) {
            COSObject destObject;
            if (cosGetObject() instanceof COSDictionary) {
                destObject = cosGetField(DK_D);
            } else {
                destObject = cosGetObject();
            }
            destination = (PDDestination) PDDestination.META.createFromCos(destObject);
        }
        return destination;
    }

    public File getFile() {
        File file = null;
        COSObject cosFileSpec = cosGetField(DK_F);
        if (cosFileSpec instanceof COSString) {
            String fileSpec = cosFileSpec.stringValue();
            file = new File(PDFFileTools.toOSPath(fileSpec));
        } else if (cosFileSpec instanceof COSDictionary) {
            PDFileSpecification fileSpec = (PDFileSpecification) PDFileSpecification.META.createFromCos(cosFileSpec);
            String fileString = fileSpec.getFile();
            if (fileString != null) {
                file = new File(PDFFileTools.toOSPath(fileString));
            }
        }
        return file;
    }

    @Override
    protected void initializeFromCos() {
        super.initializeFromCos();
    }

    @Override
    public void invalidateCaches() {
        destination = null;
        super.invalidateCaches();
    }

    public boolean isNewWindow() {
        COSBoolean value = cosGetField(DK_NewWindow).asBoolean();
        if (value == null) {
            return false;
        }
        return value.booleanValue();
    }
}
