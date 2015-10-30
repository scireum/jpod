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
package de.intarsys.pdf.st;

import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadError;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.parser.COSLoadWarning;
import de.intarsys.tools.randomaccess.IRandomAccess;

import java.io.IOException;

/**
 * An abstract parser for the PDF XRef information.
 */
public abstract class AbstractXRefParser {
    private STDocument doc;

    private COSDocumentParser parser;

    protected AbstractXRefParser(STDocument doc, COSDocumentParser parser) {
        this.doc = doc;
        this.parser = parser;
    }

    protected STDocument getDoc() {
        return doc;
    }

    protected COSDocumentParser getParser() {
        return parser;
    }

    /**
     * Parser the {@link STXRefSection} from the {@code randomAccess}.
     *
     * @param randomAcces
     * @return The parsed {@link STXRefSection}
     * @throws IOException
     * @throws COSLoadException
     */
    public abstract STXRefSection parse(IRandomAccess randomAcces) throws IOException, COSLoadException;

    protected void handleWarning(COSLoadWarning warning) throws COSLoadException {
        parser.handleWarning(warning);
    }

    protected void handleError(COSLoadError error) throws COSLoadException {
        parser.handleError(error);
    }
}
