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
 * Represents the boolean value "false".
 */
public class COSFalse extends COSBoolean {
    public static COSFalse create() {
        return new COSFalse();
    }

    protected COSFalse() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        return "false"; //$NON-NLS-1$
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSBoolean#booleanValue()
     */
    @Override
    public boolean booleanValue() {
        return false;
    }

    @Override
    protected COSObject copyBasic() {
        return new COSFalse();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof COSFalse;
    }

    /**
     * Returns an arbitrary number to avoid collisions
     *
     * @return 17
     * @see Object#hashCode()
     */
    @Override
    public int hashCode() {
        // arbitrary number to avoid collisions
        return 17;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    @Override
    public Object saveState() {
        COSObject result = new COSFalse();
        result.container = this.container.saveStateContainer();
        return result;
    }
}
