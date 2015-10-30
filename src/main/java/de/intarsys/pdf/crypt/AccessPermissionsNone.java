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
 * Provide no access to PDF features.
 */
public final class AccessPermissionsNone implements IAccessPermissions {

    private static final AccessPermissionsNone active = new AccessPermissionsNone();

    public static AccessPermissionsNone get() {
        return active;
    }

    private AccessPermissionsNone() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayAssemble()
     */
    @Override
    public boolean mayAssemble() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayCopy()
     */
    @Override
    public boolean mayCopy() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayExtract()
     */
    @Override
    public boolean mayExtract() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayFillForm()
     */
    @Override
    public boolean mayFillForm() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayModify()
     */
    @Override
    public boolean mayModify() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayModifyAnnotation()
     */
    @Override
    public boolean mayModifyAnnotation() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayPrint()
     */
    @Override
    public boolean mayPrint() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.encryption.IAccessPermissions#mayPrintHighQuality()
     */
    @Override
    public boolean mayPrintHighQuality() {
        return false;
    }
}
