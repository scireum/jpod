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

import de.intarsys.pdf.encoding.PDFDocEncoding;
import de.intarsys.tools.hex.HexTools;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * The string representation for a pdf document
 */
public class COSString extends COSPrimitiveObject implements Comparable {
    private static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

    /**
     * Create a {@link COSString} from {@code bytes}.
     *
     * @param bytes
     * @return The new {@link COSString}
     */
    public static COSString create(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        return new COSString(bytes, null);
    }

    /**
     * Create a {@link COSString} from {@code bytes}.
     *
     * @param bytes
     * @return The new {@link COSString}
     */
    public static COSString create(byte[] bytes, String encoding) {
        if (bytes == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        return new COSString(bytes, encoding);
    }

    /**
     * Create a {@link COSString} from {@code string}.
     *
     * @param string
     * @return The new {@link COSString}
     */
    public static COSString create(String string) {
        if (string == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        if (PDFDocEncoding.UNIQUE.isEncodable(string)) {
            return new COSString(string, null);
        } else {
            return new COSString(string, "UTF-16BE");
        }
    }

    /**
     * Create a {@link COSString} from {@code string}.
     *
     * @param string
     * @return The new {@link COSString}
     */
    public static COSString create(String string, String encoding) {
        if (string == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        return new COSString(string, encoding);
    }

    /**
     * Create a {@link COSString} from {@code bytes} in hex representation.
     *
     * @param bytes
     * @return The new {@link COSString}
     */
    public static COSString createHex(byte[] bytes) {
        if (bytes == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        COSString result = new COSString(bytes, null);
        result.setHexMode(true);
        return result;
    }

    /**
     * Create a {@link COSString} from {@code bytes} in hex representation.
     *
     * @param bytes
     * @return The new {@link COSString}
     */
    public static COSString createHex(byte[] bytes, String encoding) {
        if (bytes == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        COSString result = new COSString(bytes, encoding);
        result.setHexMode(true);
        return result;
    }

    /**
     * Create a {@link COSString} from {@code string} in hex representation.
     *
     * @param string
     * @return The new {@link COSString}
     */
    public static COSString createHex(String string) {
        if (string == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        if (PDFDocEncoding.UNIQUE.isEncodable(string)) {
            COSString result = new COSString(string, null);
            result.setHexMode(true);
            return result;
        } else {
            COSString result = new COSString(string, "UTF-16BE");
            result.setHexMode(true);
            return result;
        }
    }

    /**
     * Create a {@link COSString} from {@code string} in hex representation.
     *
     * @param string
     * @return The new {@link COSString}
     */
    public static COSString createHex(String string, String encoding) {
        if (string == null) {
            throw new NullPointerException("COSString value can't be null"); //$NON-NLS-1$
        }
        COSString result = new COSString(string, encoding);
        result.setHexMode(true);
        return result;
    }

    /**
     * Create a {@link COSString} from {@code string}, escaping all newlines.
     *
     * @param string
     * @return The new {@link COSString}
     */
    public static COSString createMultiLine(String string) {
        return create(toPDFString(string));
    }

    public static String toJavaString(String value) {
        StringBuilder result = new StringBuilder();
        int length = value.length();
        boolean crFound = false;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '\r') {
                if (crFound) {
                    result.append(LINE_SEPARATOR);
                }
                crFound = true;
            } else if (c == '\n') {
                result.append(LINE_SEPARATOR);
                crFound = false;
            } else {
                if (crFound) {
                    result.append(LINE_SEPARATOR);
                }
                result.append(c);
                crFound = false;
            }
        }
        if (crFound) {
            result.append(LINE_SEPARATOR);
        }
        return result.toString();
    }

    public static String toPDFString(String value) {
        // transform line delimiter (CR, CR/LN, LN) to CR
        StringBuilder result = new StringBuilder();
        int length = value.length();
        boolean ignoreLn = false;
        for (int i = 0; i < length; i++) {
            char c = value.charAt(i);
            if (c == '\n') {
                if (!ignoreLn) {
                    result.append('\r');
                }
            } else {
                result.append(c);
            }
            ignoreLn = c == '\r';
        }
        return result.toString();
    }

    private String encoding;

    // the bytes that make up the string
    private byte[] bytes;

    // cached hash code
    private int hash = 0;

    // flag if this string has to be written in hex representation
    private boolean hexMode = false;

    private byte offset = 0;

    // the decoded string representation of the bytes (lazy)
    private String string;

    protected COSString() {
        super();
    }

    protected COSString(byte[] bytes, String encoding) {
        super();
        this.bytes = bytes;
        this.encoding = encoding;
    }

    protected COSString(String string, String encoding) {
        super();
        this.string = string;
        this.encoding = encoding;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#accept(de.intarsys.pdf.cos.ICOSObjectVisitor )
     */
    @Override
    public java.lang.Object accept(ICOSObjectVisitor visitor) throws COSVisitorException {
        return visitor.visitFromString(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#asString()
     */
    @Override
    public COSString asString() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#basicToString()
     */
    @Override
    protected String basicToString() {
        if (isHexMode()) {
            return "<" + hexStringValue() + ">"; //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            return "(" + stringValue() + ")"; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * The bytes that make up this string.
     *
     * @return The bytes that make up this string.
     */
    public byte[] byteValue() {
        if (bytes == null) {
            bytes = encode();
        }
        return bytes;
    }

    /**
     * Reset the bytes for this {@link COSString}. You may need this if the
     * encoding is changed during the {@link COSString} lifetime.
     */
    public void clearBytes() {
        if (bytes == null) {
            return;
        }
        decode();
        bytes = null;
    }

    /**
     * Reset the string value for this {@link COSString}. You may need this if
     * the encoding is changed during the {@link COSString} lifetime.
     */
    public void clearString() {
        if (string == null) {
            return;
        }
        encode();
        string = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof COSString)) {
            throw new ClassCastException("must compare with a COSString"); //$NON-NLS-1$
        }
        byte[] thisBytes = byteValue();
        byte[] otherBytes = ((COSString) o).byteValue();
        for (int i = 0; (i < thisBytes.length) && (i < otherBytes.length); i++) {
            if (thisBytes[i] < otherBytes[i]) {
                return -1;
            }
            if (thisBytes[i] > otherBytes[i]) {
                return 1;
            }
        }
        if (thisBytes.length < otherBytes.length) {
            return -1;
        }
        if (thisBytes.length > otherBytes.length) {
            return 1;
        }
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#copyBasic()
     */
    @Override
    protected COSObject copyBasic() {
        COSString result = new COSString();
        result.bytes = this.bytes;
        result.hash = this.hash;
        result.hexMode = this.hexMode;
        result.string = this.string;
        result.encoding = this.encoding;
        result.offset = this.offset;
        return result;
    }

    /**
     * decode the string from the byte value in the given encoding
     *
     * @return the decoded string
     */
    protected String decode() {
        // test for well known internal encodings
        testBuiltinEncoding();
        if (encoding != null) {
            try {
                return new String(bytes, offset, bytes.length - offset, encoding);
            } catch (UnsupportedEncodingException ignored) {
                return PDFDocEncoding.UNIQUE.decode(bytes);
            }
        }
        return PDFDocEncoding.UNIQUE.decode(bytes);
    }

    /**
     * encode the string in byte representation using the correct encoding
     *
     * @return the encoded string
     */
    protected byte[] encode() {
        if (encoding != null) {
            if ("UTF-16BE".equals(encoding)) {
                byte[] tempBytes;
                try {
                    tempBytes = string.getBytes("UTF-16BE"); //$NON-NLS-1$
                } catch (UnsupportedEncodingException e) {
                    // strange... :-)
                    return PDFDocEncoding.UNIQUE.encode(string);
                }
                byte[] resultBytes = new byte[tempBytes.length + 2];
                resultBytes[0] = (byte) 0xfe;
                resultBytes[1] = (byte) 0xff;
                System.arraycopy(tempBytes, 0, resultBytes, 2, tempBytes.length);
                return resultBytes;
            } else if ("UTF-16LE".equals(encoding)) {
                byte[] tempBytes;
                try {
                    tempBytes = string.getBytes("UTF-16LE"); //$NON-NLS-1$
                } catch (UnsupportedEncodingException e) {
                    // strange... :-)
                    return PDFDocEncoding.UNIQUE.encode(string);
                }
                byte[] resultBytes = new byte[tempBytes.length + 2];
                resultBytes[0] = (byte) 0xff;
                resultBytes[1] = (byte) 0xfe;
                System.arraycopy(tempBytes, 0, resultBytes, 2, tempBytes.length);
                return resultBytes;
            } else {
                try {
                    return string.getBytes(encoding);
                } catch (UnsupportedEncodingException ignored) {
                    return PDFDocEncoding.UNIQUE.encode(string);
                }
            }
        }
        return PDFDocEncoding.UNIQUE.encode(string);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof COSString)) {
            return false;
        }
        return Arrays.equals(byteValue(), ((COSString) o).byteValue());
    }

    /**
     * Lookup the custom encoding used by this string. This method returns null
     * if the default PDF conventions are used.
     * <p>
     * Be warned: If you use a custom encoding, there is no way for standard PDF
     * tools to recover this information!
     *
     * @return Lookup the custom encoding used by this string
     */
    public String getEncoding() {
        return encoding;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (bytes == null) {
            return super.hashCode();
        }
        int h = hash;
        if (h == 0) {
            for (int i = 0; i < bytes.length; i++) {
                h = (31 * h) + bytes[i];
            }
            hash = h;
        }
        return h;
    }

    /**
     * Show a hex encoded representation of the strings content.
     *
     * @return Show a hex encoded representation of the strings content.
     */
    public String hexStringValue() {
        return HexTools.bytesToHexString(byteValue());
    }

    /**
     * {@code true} if this string has to be saved as hex representation
     *
     * @return {@code true} if this string has to be saved as hex
     * representation
     */
    public boolean isHexMode() {
        return hexMode;
    }

    /**
     * A Java {@link String} with correctly expanded newlines.
     *
     * @return A Java {@link String} with correctly expanded newlines.
     */
    public String multiLineStringValue() {
        return toJavaString(stringValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.COSObject#restoreState(java.lang.Object)
     */
    @Override
    public void restoreState(Object object) {
        super.restoreState(object);
        COSString cosString = (COSString) object;
        this.bytes = cosString.bytes;
        this.hash = cosString.hash;
        this.hexMode = cosString.hexMode;
        this.string = cosString.string;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.tools.objectsession.ISaveStateSupport#saveState()
     */
    @Override
    public Object saveState() {
        COSString result = new COSString();
        result.bytes = this.bytes;
        result.hash = this.hash;
        result.hexMode = this.hexMode;
        result.string = this.string;
        result.encoding = this.encoding;
        result.offset = this.offset;
        result.container = this.container.saveStateContainer();
        return result;
    }

    /**
     * Set the {@link Charset} name to be used when decoding the
     * {@link COSString} bytes. If no encoding is present, the default PDF
     * conventions are used.
     * <p>
     * Be warned: If you use a custom encoding, there is no way for standard PDF
     * tools to recover this information!
     *
     * @param encoding
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * Set the flag if this is written in hex representation
     *
     * @param newHexMode {@code true} if this is written in hex representation
     */
    public void setHexMode(boolean newHexMode) {
        hexMode = newHexMode;
    }

    /**
     * The Java {@link String} representation of the receiver
     *
     * @return The Java {@link String} representation of the receiver
     */
    @Override
    public String stringValue() {
        if (string == null) {
            string = decode();
        }
        return string;
    }

    protected void testBuiltinEncoding() {
        if (bytes.length < 2) {
            return;
        }
        if ((bytes[0] == (byte) 0xfe) && (bytes[1] == (byte) 0xff)) {
            offset = 2;
            encoding = "UTF-16BE"; //$NON-NLS-1$
        } else if ((bytes[0] == (byte) 0xff) && (bytes[1] == (byte) 0xfe)) {
            // extend test to undocumented format used by some tools
            offset = 2;
            encoding = "UTF-16LE"; //$NON-NLS-1$
        }
    }
}
