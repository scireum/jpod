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
 * Represents the boolean value "true"
 */
public class COSTrue extends COSBoolean {
    static public COSTrue create() {
        return new COSTrue();
    }

    protected COSTrue() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        return "true"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBoolean#booleanValue()
     */
    @Override
    public boolean booleanValue() {
        return true;
    }

    @Override
    protected COSObject copyBasic() {
        return new COSTrue();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        return o instanceof COSTrue;
    }

    /**
     * Returns an arbitrary number to avoid collisions
     *
     * @return 19
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        // arbitrary number to avoid collisions
        return 19;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    public Object saveState() {
        COSObject result = new COSTrue();
        result.container = this.container.saveStateContainer();
        return result;
    }
}
