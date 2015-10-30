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
package de.intarsys.pdf.crypt;

/**
 * A document permissions set based on the "user access permissions" defined in
 * a /Standard encryption dictionary.
 */
public interface IAccessPermissions {
    /**
     * Assemble the document: insert, rotate or delete pages and create
     * bookmarks or thumbnail images.
     *
     * @return if the document may be assembled
     */
    public abstract boolean mayAssemble();

    /**
     * Copy or otherwise extract text and graphics from the document in support
     * of accessibility to disabled users or for other purposes.
     *
     * @return if parts of the document may be copied
     */
    public abstract boolean mayCopy();

    /**
     * Extract text and graphics (in support of accessibility to disabled users
     * or for other purposes)
     *
     * @return if parts of the document may be extracted
     */
    public abstract boolean mayExtract();

    /**
     * Fill in existing interactive form fields (including signature fields)
     *
     * @return if form fields may be filled
     */
    public abstract boolean mayFillForm();

    /**
     * Modify the contents of the document by operations other than those
     * controlled by mayModifyAnnotation and mayFillForm
     *
     * @return if document may be modified
     */
    public abstract boolean mayModify();

    /**
     * Add or modify text annotations, fill in interactice form fields, and if
     * mayModify is set, create or modify interactive form fields (including
     * signature fields)
     *
     * @return if annotations may be modified
     */
    public abstract boolean mayModifyAnnotation();

    /**
     * Print the document
     *
     * @return if the document may be printed
     */
    public abstract boolean mayPrint();

    /**
     * Print the document to a representation from which a faithful digital copy
     * of the PDF content could be generated. When this premission is not set
     * and mayPrint is set, printing is limited to a low-level representation of
     * the appearance, possibly of degraded quality.
     *
     * @return if the document may be high quality printed
     */
    public abstract boolean mayPrintHighQuality();
}
