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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * A name object.
 */
public class COSName extends COSPrimitiveObject {

    public static COSName constant(String name) {
        return (COSName) COSName.create(name).beConstant();
    }

    public static COSName constantUTF8(String name) {
        return (COSName) COSName.createUTF8(name).beConstant();
    }

    public static COSName create(byte[] bytes) {
        return new COSName(bytes, true);
    }

    public static COSName create(String name) {
        return new COSName(name.getBytes(), true);
    }

    public static COSName createUTF8(String name) {
        try {
            return new COSName(name.getBytes("UTF-8"), true); //$NON-NLS-1$
        } catch (UnsupportedEncodingException e) {
            // ...?
            return new COSName(name.getBytes(), true);
        }
    }

    /**
     * if string representation is needed it is lazy computed on base of UTF 8
     */
    private String string;

    /**
     * the underlying byte representation
     */
    private final byte[] bytes;

    /**
     * cached hash value
     */
    private int hash;

    /**
     * COSName constructor.
     *
     * @param newName The name value for the object.
     */
    protected COSName(byte[] newName) {
        super();
        bytes = newName;
    }

    /**
     * COSName constructor. Dummy constructor to include hash computation
     *
     * @param newName The name value for the object.
     */
    protected COSName(byte[] newName, boolean computeHash) {
        super();
        bytes = newName;
        computeHash();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor)
     */
    @Override
    public java.lang.Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        return visitor.visitFromName(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#asName()
     */
    @Override
    public COSName asName() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        return "/" + stringValue(); //$NON-NLS-1$
    }

    /**
     * The bytes that make up this name (without "/")
     *
     * @return The bytes that make up this name (without "/")
     */
    public byte[] byteValue() {
        return bytes;
    }

    private void computeHash() {
        int h = 0;
        for (int i = 0; i < bytes.length; i++) {
            h = (31 * h) + bytes[i];
        }
        hash = h;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyBasic(de.intarsys.pdf.cos.COSDocument)
     */
    @Override
    protected COSObject copyBasic() {
        COSName result = new COSName(bytes);
        result.string = this.string;
        result.hash = this.hash;
        return result;
    }

    protected String decode() {
        /*
         * UTF-8 Test: a UTF-8 encoded character starts with 110xxxxx or
         * 1110xxxx or 11110xxx followed by 1 to 3 bytes in form: 10xxxxxx. This
         * algorithm looks for the UTF-8 start and 1 followup character. If one
         * sequence is found, UTF-8 is assumed, otherwise not. F8: 1111 1000 F0:
         * 1111 0000 E0: 1110 0000 C0: 1100 0000 08: 1000 0000
         */
        boolean utf8 = false;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] < 0) {
                int test = bytes[i] & 0x00F8;
                if ((test == 0x00C0) || (test == 0x00E0) || (test == 0x00F0)) {
                    if ((i + 1) == bytes.length) {
                        break;
                    }
                    i++;
                    if ((bytes[i] & 0x00C0) == 0x0080) {
                        utf8 = true;
                    }
                    break;
                }
            }
        }

        if (utf8) {
            try {
                return new String(bytes, "UTF-8"); //$NON-NLS-1$
            } catch (UnsupportedEncodingException ignored) {
                return new String(bytes);
            }
        } else {
            return new String(bytes);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof COSName)) {
            return false;
        }
        return Arrays.equals(bytes, ((COSName) o).byteValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return hash;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#restoreState(java.lang.Object)
     */
    @Override
    public void restoreState(Object object) {
        super.restoreState(object);
        COSName name = (COSName) object;
        this.string = name.string;
        this.hash = name.hash;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    @Override
    public Object saveState() {
        COSName result = new COSName(bytes);
        result.string = this.string;
        result.hash = this.hash;
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * The string value that makes up this name (without "/")
     *
     * @return The string value that makes up this name (without "/")
     */
    @Override
    public java.lang.String stringValue() {
        if (string == null) {
            string = decode();
        }
        return string;
    }
}
