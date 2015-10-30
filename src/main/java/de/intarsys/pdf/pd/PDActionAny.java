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
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * A generic action implementation to be used when no specific implementation
 * needed or around.
 */
public class PDActionAny extends PDAction {

    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDAction.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDActionAny(object);
        }
    }

    /**
     * all currently undefined or not implemented action types
     */
    public static final COSName CN_ActionType_GoToE = COSName.constant("GoToE");
    public static final COSName CN_ActionType_Thread = COSName.constant("Thread");
    public static final COSName CN_ActionType_Sound = COSName.constant("Sound");
    public static final COSName CN_ActionType_Movie = COSName.constant("Movie");
    public static final COSName CN_ActionType_Hide = COSName.constant("Hide");
    public static final COSName CN_ActionType_ImportData = COSName.constant("ImportData");
    public static final COSName CN_ActionType_SetOCGState = COSName.constant("SetOCGState");
    public static final COSName CN_ActionType_Rendition = COSName.constant("Rendition");
    public static final COSName CN_ActionType_Trans = COSName.constant("Trans");

    public static final COSName CN_ActionType_GoTo3DView = COSName.constant("GoTo3DView");
    // obsolete per 1.6
    public static final COSName CN_ActionType_set_state = COSName.constant("set-state");

    public static final COSName CN_ActionType_no_op = COSName.constant("no-op");

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    public static PDActionAny createNew(COSName actionType) {
        PDActionAny result = (PDActionAny) PDActionAny.META.createNew();
        result.cosSetActionType(actionType);
        return result;
    }

    protected PDActionAny(COSObject object) {
        super(object);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.pd.PDAction#cosGetExpectedActionType()
     */
    @Override
    public COSName cosGetExpectedActionType() {
        return null;
    }

    protected COSObject cosSetActionType(COSName newActionType) {
        return cosSetField(DK_S, newActionType);
    }
}
