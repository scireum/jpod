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
package de.intarsys.pdf.fd;

import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.st.EnumWriteMode;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.tools.locator.ILocator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FDDocument {
    /**
     * the underlying COSDocument object
     */
    private COSDocument cosDoc;

    private FDFDF fdf;

    protected FDDocument(COSDocument newDoc) {
        super();
        cosDoc = newDoc;
    }

    protected FDDocument() {
        super();
        cosDoc = COSDocument.createNew(STDocument.DOCTYPE_FDF);
    }

    /**
     * create a fd document based on a cos level object
     *
     * @param doc COSDocument to base this FDDoc on
     * @return A new FDDoc object.
     */
    public static FDDocument createFromCOS(COSDocument doc) {
        FDDocument result = new FDDocument(doc);
        result.initializeFromCOS();
        return result;
    }

    /**
     * create a FDDoc from scratch
     *
     * @return A new FDDoc.
     */
    public static FDDocument createNew() {
        FDDocument result = new FDDocument();
        result.initializeFromScratch();
        return result;
    }

    /**
     * initialize the object when created based on its cos representation
     */
    protected void initializeFromCOS() {
        fdf = (FDFDF) FDFDF.META.createFromCos(getCatalog().cosGetFDF());
    }

    /**
     * initialize the object when created from scratch (in memory)
     */
    protected void initializeFromScratch() {
        fdf = (FDFDF) FDFDF.META.createNew();
        getCatalog().cosSetFDF(fdf.cosGetDict());
    }

    /**
     * lookup the catalog dictionary in a document
     *
     * @return the document catalog object
     */
    public COSCatalog getCatalog() {
        return cosGetDoc().getCatalog();
    }

    /**
     * get the COS level implementation of the document
     *
     * @return the underlying COSDocument
     */
    public COSDocument cosGetDoc() {
        return cosDoc;
    }

    public FDFDF getFdf() {
        return fdf;
    }

    public void save() throws IOException {
        save(getLocator(), null);
    }

    public ILocator getLocator() {
        return cosDoc.getLocator();
    }

    public void save(ILocator locator, Map options) throws IOException {
        if (options == null) {
            options = new HashMap();
        }
        // we MUST have a full write...
        cosDoc.setWriteModeHint(EnumWriteMode.FULL);
        cosDoc.save(locator, options);
    }

    public void save(ILocator locator) throws IOException {
        save(locator, null);
    }
}
