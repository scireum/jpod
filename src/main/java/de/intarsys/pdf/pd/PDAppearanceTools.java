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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

import java.util.HashSet;
import java.util.Iterator;

/**
 * A tool class for common tasks with {@link PDAppearance} instances.
 */
public class PDAppearanceTools {
    static protected PDForm createAppearanceForm() {
        PDForm form = (PDForm) PDForm.META.createNew();
        return form;
    }

    public static boolean createState(PDAppearance appearance, String state) {
        boolean result = false;
        COSName cosState = COSName.create(state);

        //
        COSName[] keys = new COSName[]{PDAppearance.DK_N, PDAppearance.DK_R, PDAppearance.DK_D};
        for (COSName key : keys) {
            COSDictionary dict = appearance.cosGetDict().get(key).asDictionary();
            if (dict != null) {
                COSObject form = dict.get(cosState);
                if (form.isNull()) {
                    dict.put(cosState, createAppearanceForm().cosGetObject());
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Given a PDAppearance, return the /D (down) appearance for the state
     * "state".
     *
     * @param appearance The PDAppearance structure containing the appearance
     *                   descriptions
     * @return Given a PDAppearance, return the /D (down) appearance for the
     * state "state".
     */
    public static PDForm getDownAppearance(PDAppearance appearance, COSName state) {
        PDForm form = appearance.getDownAppearance(state);
        if (form == null) {
            form = createAppearanceForm();
            appearance.setDownAppearance(state, form);
        }
        return form;
    }

    /**
     * Given a PDAppearance, return the /N (normal) appearance for the state
     * "state".
     *
     * @param appearance The PDAppearance structure containing the appearance
     *                   descriptions
     * @return Given a PDAppearance, return the /N (normal) appearance for the
     * state "state".
     */
    public static PDForm getNormalAppearance(PDAppearance appearance, COSName state) {
        PDForm form = appearance.getNormalAppearance(state);
        if (form == null) {
            form = createAppearanceForm();
            appearance.setNormalAppearance(state, form);
        }
        return form;
    }

    /**
     * Given a PDAppearance, return the /R (rollover) appearance for the state
     * "state".
     *
     * @param appearance The PDAppearance structure containing the appearance
     *                   descriptions
     * @return Given a PDAppearance, return the /R (rollover) appearance for the
     * state "state".
     */
    public static PDForm getRolloverAppearance(PDAppearance appearance, COSName state) {
        PDForm form = appearance.getRolloverAppearance(state);
        if (form == null) {
            form = createAppearanceForm();
            appearance.setRolloverAppearance(state, form);
        }
        return form;
    }

    public static void renameState(PDAppearance appearance, String oldState, String newState) {
        COSName cosOldState = COSName.create(oldState);
        COSName cosNewState = COSName.create(newState);

        COSName[] keys = new COSName[]{PDAppearance.DK_N, PDAppearance.DK_R, PDAppearance.DK_D};
        for (COSName key : keys) {
            COSDictionary dict = appearance.cosGetDict().get(key).asDictionary();
            if (dict != null) {
                COSObject form = dict.get(cosOldState);
                if (!form.isNull()) {
                    dict.remove(cosOldState);
                    dict.put(cosNewState, form);
                }
            }
        }
    }

    public static void resetAppearance(COSDictionary appearanceDict) {
        if (appearanceDict == null) {
            return;
        }
        for (Iterator i = new HashSet(appearanceDict.keySet()).iterator(); i.hasNext(); ) {
            COSName appKey = (COSName) i.next();
            COSObject appValue = appearanceDict.get(appKey);
            if (appValue instanceof COSDictionary) {
                COSDictionary appValueDict = (COSDictionary) appValue;
                for (Iterator j = new HashSet(appValueDict.keySet()).iterator(); j.hasNext(); ) {
                    COSName stateKey = (COSName) j.next();
                    appValueDict.put(stateKey, PDForm.META.createNew().cosGetObject());
                }
            } else {
                appearanceDict.put(appKey, PDForm.META.createNew().cosGetObject());
            }
        }
    }

    public static void resetAppearance(PDAppearance appearance) {
        resetAppearance(appearance.cosGetDict());
    }
}
