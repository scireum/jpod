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
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;

/**
 * A destination within a document defined using a named destination. The
 * destination is looked up via the /Dests entry in the catalog.
 */
public class PDNamedDestination extends PDDestination {
    /**
     * The meta class implementation
     */
    public static class MetaClass extends PDDestination.MetaClass {
        protected MetaClass(Class instanceClass) {
            super(instanceClass);
        }

        @Override
        protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
            return new PDNamedDestination(object);
        }
    }

    /**
     * The meta class instance
     */
    public static final MetaClass META = new MetaClass(MetaClass.class.getDeclaringClass());

    protected PDNamedDestination(COSObject object) {
        super(object);
    }

    public String getName() {
        return cosGetObject().stringValue();
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
        COSObject resolvedDest = doc.lookupDestination(getName());
        if (resolvedDest instanceof COSDictionary) {
            COSObject destDict = ((COSDictionary) resolvedDest).get(COSName.create("D"));
            PDDestination newDest = (PDDestination) PDDestination.META.createFromCos(destDict);
            return newDest.getResolvedDestination(doc);
        }
        if (resolvedDest instanceof COSArray) {
            return (PDExplicitDestination) PDExplicitDestination.META.createFromCos(resolvedDest);
        }
        return null;
    }
}
