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

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * Primitive COS datatypes. These objects are "immutable" in their PDF
 * semantics. The container may change!
 */
abstract public class COSPrimitiveObject extends COSObject {
    protected COSPrimitiveObject() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#addObjectListener(de.intarsys.pdf.cos.ICOSObjectListener)
     */
    @Override
    public void addObjectListener(ICOSObjectListener listener) {
        // no op for immutable primitives
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicIterator()
     */
    @Override
    public java.util.Iterator basicIterator() {
        return Collections.emptyIterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyDeep()
     */
    @Override
    public COSObject copyDeep() {
        return copyShallow();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSDocumentElement#copyDeep(java.util.Map)
     */
    @Override
    public COSObject copyDeep(Map copied) {
        return copyShallow();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#isDangling()
     */
    @Override
    public boolean isDangling() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#isObjectListenerAvailable()
     */
    @Override
    public boolean isObjectListenerAvailable() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#isPrimitive()
     */
    @Override
    public boolean isPrimitive() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#iterator()
     */
    @Override
    public Iterator<COSObject> iterator() {
        return Collections.emptyIterator();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSDocumentElement#register(de.intarsys.pdf.cos.COSDocument)
     */
    @Override
    protected void registerWith(COSDocument doc) {
        // nothing to do
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#removeObjectListener(de.intarsys.pdf.cos.ICOSObjectListener)
     */
    @Override
    public void removeObjectListener(ICOSObjectListener listener) {
        // no op for immutable primitives
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#triggerChanged(java.lang.Object,
     *      de.intarsys.pdf.cos.COSObject, de.intarsys.pdf.cos.COSObject)
     */
    @Override
    protected void triggerChanged(Object slot, Object oldValue, Object newValue) {
        // ignore
    }
}
