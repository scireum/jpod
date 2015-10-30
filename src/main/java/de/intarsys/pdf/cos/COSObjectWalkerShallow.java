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
package de.intarsys.pdf.cos;

import java.util.Iterator;

/**
 * Adapter implementation for visiting a COS object structure without navigating
 * the indirect references.
 * <p>
 * More precise, this one will visit the directed acyclic data structure
 * originating from a COS object.
 */
public class COSObjectWalkerShallow implements ICOSObjectVisitor {

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromArray(de.intarsys.pdf.cos.COSArray)
     */
    @Override
    public Object visitFromArray(COSArray array) throws COSVisitorException {
        if (visitFromArrayBefore(array)) {
            for (Iterator i = array.basicIterator(); i.hasNext(); ) {
                ((COSDocumentElement) i.next()).accept(this);
            }
        }
        return visitFromArrayAfter(array);
    }

    protected Object visitFromArrayAfter(COSArray array) {
        return null;
    }

    /**
     * @param array
     */
    protected boolean visitFromArrayBefore(COSArray array) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromBoolean(de.intarsys.pdf.cos.COSBoolean)
     */
    @Override
    public Object visitFromBoolean(COSBoolean bool) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromDictionary(de.intarsys.pdf.cos.COSDictionary)
     */
    @Override
    public Object visitFromDictionary(COSDictionary dict) throws COSVisitorException {
        if (visitFromDictionaryBefore(dict)) {
            for (Iterator i = dict.basicIterator(); i.hasNext(); ) {
                ((COSDocumentElement) i.next()).accept(this);
            }
        }
        return visitFromDictionaryAfter(dict);
    }

    protected Object visitFromDictionaryAfter(COSDictionary dict) {
        return null;
    }

    protected boolean visitFromDictionaryBefore(COSDictionary dict) {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromFixed(de.intarsys.pdf.cos.COSFixed)
     */
    @Override
    public Object visitFromFixed(COSFixed fixed) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromInteger(de.intarsys.pdf.cos.COSInteger)
     */
    @Override
    public Object visitFromInteger(COSInteger integer) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromName(de.intarsys.pdf.cos.COSName)
     */
    @Override
    public Object visitFromName(COSName name) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromNull(de.intarsys.pdf.cos.COSNull)
     */
    @Override
    public Object visitFromNull(COSNull nullObj) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromStream(de.intarsys.pdf.cos.COSStream)
     */
    @Override
    public Object visitFromStream(COSStream stream) throws COSVisitorException {
        stream.getDict().accept(this);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromString(de.intarsys.pdf.cos.COSString)
     */
    @Override
    public Object visitFromString(COSString string) throws COSVisitorException {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSObjectVisitor#visitFromIndirectObject(de.intarsys.pdf.cos.COSIndirectObject)
     */
    @Override
    public Object visitFromIndirectObject(COSIndirectObject io) throws COSVisitorException {
        return null;
    }
}
