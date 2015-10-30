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
package de.intarsys.pdf.app.appearance;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.pd.PDAcroFormField;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDAppearance;
import de.intarsys.pdf.pd.PDObject;
import de.intarsys.tools.attribute.Attribute;

import java.util.Iterator;

/**
 * Tool class for tasks related to appearance creation.
 */
public class AppearanceCreatorTools {

    private static final Attribute ATTR_APPEARANCECREATOR = new Attribute("appearanceCreator");

    private static final Attribute ATTR_PREVIOUSAPPEARANCECREATOR = new Attribute("previousAppearanceCreator");

    private static final IAppearanceCreator APPEARANCECREATOR_IDENTITY = new IdentityAppearanceCreator();

    private static final IAppearanceCreator APPEARANCECREATOR_NULL = new NullAppearanceCreator();

	private AppearanceCreatorTools() {
	}

	public static void createAppearance(PDAcroFormField field) {
        IAppearanceCreator appearanceCreator = getAppearanceCreator(field);
        for (Iterator i = field.getLogicalRoot().getAnnotations().iterator(); i.hasNext(); ) {
            PDAnnotation annot = (PDAnnotation) i.next();
            createAppearance(annot, appearanceCreator);
        }
    }

    public static PDAppearance createAppearance(PDAnnotation annotation) {
        IAppearanceCreator appearanceCreator = getAppearanceCreator(annotation);
        return createAppearance(annotation, appearanceCreator);
    }

    public static PDAppearance createAppearance(PDAnnotation annotation, IAppearanceCreator appearanceCreator) {
        if (appearanceCreator == null) {
            COSName type = annotation.cosGetSubtype();
            appearanceCreator = AppearanceCreatorRegistry.get().lookupAppearanceCreator(type);
            if (appearanceCreator == null) {
                appearanceCreator = APPEARANCECREATOR_NULL;
            }
        }
        PDAppearance appearance = appearanceCreator.createAppearance(annotation, null);
        if (appearance != annotation.getAppearance()) {
            annotation.setAppearance(appearance);
        }
        return appearance;
    }

    public static IAppearanceCreator getAppearanceCreator(PDObject fieldOrAnntotation) {
        return (IAppearanceCreator) fieldOrAnntotation.getAttribute(ATTR_APPEARANCECREATOR);
    }

    public static void resumeAppearanceCreation(PDObject fieldOrAnntotation) {
        IAppearanceCreator previousAppearanceCreator =
                (IAppearanceCreator) fieldOrAnntotation.removeAttribute(ATTR_PREVIOUSAPPEARANCECREATOR);
        setAppearanceCreator(fieldOrAnntotation, previousAppearanceCreator);
    }

    public static void setAppearanceCreator(PDObject fieldOrAnntotation, IAppearanceCreator appearanceCreator) {
        fieldOrAnntotation.setAttribute(ATTR_APPEARANCECREATOR, appearanceCreator);
    }

    public static void suspendAppearanceCreation(PDObject fieldOrAnntotation) {
        IAppearanceCreator currentAppearanceCreator = getAppearanceCreator(fieldOrAnntotation);
        fieldOrAnntotation.setAttribute(ATTR_PREVIOUSAPPEARANCECREATOR, currentAppearanceCreator);
        setAppearanceCreator(fieldOrAnntotation, APPEARANCECREATOR_IDENTITY);
    }
}
