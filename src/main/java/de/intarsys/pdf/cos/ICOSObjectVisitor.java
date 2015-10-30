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

/**
 * An interface for visiting a COS object structure.
 */
public interface ICOSObjectVisitor {
    /**
     * Notification of visit to {@link COSArray} object.
     *
     * @param array The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromArray(COSArray array) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSBoolean} object.
     *
     * @param bool The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromBoolean(COSBoolean bool) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSDictionary} object.
     *
     * @param dict The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromDictionary(COSDictionary dict) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSFixed} object.
     *
     * @param fixed The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromFixed(COSFixed fixed) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSInteger} object.
     *
     * @param integer The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromInteger(COSInteger integer) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSName} object.
     *
     * @param name The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromName(COSName name) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSNull} object.
     *
     * @param nullObj The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromNull(COSNull nullObj) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSStream} object.
     *
     * @param stream The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromStream(COSStream stream) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSString} object.
     *
     * @param string The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromString(COSString string) throws COSVisitorException;

    /**
     * Notification of visit to {@link COSIndirectObject} object.
     *
     * @param indirect The Object that is being visited.
     * @return any Object depending on the visitor implementation, or null
     * @throws COSVisitorException If there is an error while visiting this object.
     */
    Object visitFromIndirectObject(COSIndirectObject indirect) throws COSVisitorException;
}
