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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;

/**
 * The URI action.
 * <p>
 * When executed the action focuses a viewer to a new destination.
 */
public class PDActionURI extends PDAction {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDAction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDActionURI(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static final COSName CN_ActionType_URI = COSName.constant("URI"); //$NON-NLS-1$

    public static final COSName DK_URI = COSName.constant("URI"); //$NON-NLS-1$

    public static final COSName DK_IsMap = COSName.constant("IsMap"); //$NON-NLS-1$

    public static PDActionURI createNew(String uri) {
        PDActionURI result = (PDActionURI) PDActionURI.META.createNew();
        result.setURI(uri);
        return result;
    }

    protected PDActionURI(COSObject object) {
        super(object);
    }

    @Override
    public COSName cosGetExpectedActionType() {
        return CN_ActionType_URI;
    }

    public String getURI() {
        COSString cosURI = cosGetField(DK_URI).asString();
        if (cosURI == null) {
            return null;
        }
        return cosURI.stringValue();
    }

    public boolean isMap() {
        COSBoolean cosMap = cosGetField(DK_IsMap).asBoolean();
        if (cosMap == null) {
            return false;
        }
        return cosMap.booleanValue();
    }

    public void setMap(boolean map) {
        if (!map) {
            // the default
            cosRemoveField(DK_IsMap);
        } else {
            cosSetField(DK_IsMap, COSBoolean.create(map));
        }
    }

    public void setURI(String uri) {
        setFieldString(DK_URI, uri);
    }
}
