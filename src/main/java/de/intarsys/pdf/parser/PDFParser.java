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
package de.intarsys.pdf.parser;

import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSFalse;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSNumber;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.cos.COSTrue;
import de.intarsys.pdf.crypt.COSSecurityException;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.st.STDocType;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.stream.FastByteArrayOutputStream;
import de.intarsys.tools.string.StringTools;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * An abstract superclass for our two flavours of PDF Parsers.
 */
public abstract class PDFParser {
    public static char CHAR_CR = '\r';

    public static char CHAR_LF = '\n';

    public static char CHAR_HT = '\t';

    public static char CHAR_BS = '\b';

    public static char CHAR_FF = '\f';

    public static final byte[] TOKEN_PDFHEADER = "%PDF".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_FDFHEADER = "%FDF".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_EOF = "%%EOF".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_obj = "obj".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_endobj = "endobj".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_false = "false".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_true = "true".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_null = "null".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_startxref = "startxref".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_trailer = "trailer".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_xref = "xref".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_stream = "stream".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_s_tream = "tream".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_endstream = "endstream".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_ndstream = "ndstream".getBytes(); //$NON-NLS-1$

    public static final byte[] TOKEN_R = "R".getBytes(); //$NON-NLS-1$

    public static final String C_WARN_UNEVENHEX = "616a"; //$NON-NLS-1$

    public static final String C_WARN_ILLEGALHEX = "616b"; //$NON-NLS-1$

    public static final String C_WARN_STRING_TOO_LONG = "ImplLimitString"; //$NON-NLS-1$

    public static final String C_WARN_NAME_TOO_LONG = "ImplLimitName"; //$NON-NLS-1$

    public static final String C_WARN_ARRAYSIZE = "ImplLimitArray"; //$NON-NLS-1$

    public static final String C_WARN_SINGLESPACE = "614a"; //$NON-NLS-1$

    public static final String C_WARN_SINGLEEOL = "614b"; //$NON-NLS-1$

    public static final String C_WARN_STREAMEOL = "617a"; //$NON-NLS-1$

    public static final String C_WARN_ENDSTREAMEOL = "617b"; //$NON-NLS-1$

    public static final String C_WARN_ENDSTREAMCORRUPT = "617c"; //$NON-NLS-1$

    public static final String C_WARN_STREAMEXTERNAL = "617d"; //$NON-NLS-1$

    public static final String C_WARN_STREAMLENGTH = "617e"; //$NON-NLS-1$

    public static final String C_WARN_SINGLESPACE_OBJ = "618a"; //$NON-NLS-1$

    public static final String C_WARN_SINGLEEOL_OBJ = "618b"; //$NON-NLS-1$

    public static final String C_WARN_ENDOBJ_MISSING = "618c"; //$NON-NLS-1$

    public static final String C_WARN_LARGE_INT = "6112a"; //$NON-NLS-1$

    protected static final String C_TOKEN_ADDWSB = "additional whitespace before"; //$NON-NLS-1$

    protected static final String C_TOKEN_WSB = "whitespace before"; //$NON-NLS-1$

    protected static final String C_TOKEN_ADDWSA = "additional whitespace after"; //$NON-NLS-1$

    protected static final String C_TOKEN_ADDWSA2 = "second add whitespace after"; //$NON-NLS-1$

    protected static final String C_TOKEN_COMMENT = "comment"; //$NON-NLS-1$

    protected static final String C_TOKEN_NOWSA = "no whitespace after"; //$NON-NLS-1$

    protected static final byte[] characterClass = new byte[256];

    protected static final byte CHARCLASS_ANY = 0;

    protected static final byte CHARCLASS_DELIMITER = 1;

    protected static final byte CHARCLASS_WHITESPACE = 2;

    protected static final byte CHARCLASS_TOKEN = 3;

    protected static final byte CHARCLASS_DIGIT = 4;

    protected static final byte CHARCLASS_NUMBERSPECIAL = 5;

    public static final byte[] TOKEN_def = "def".getBytes(); //$NON-NLS-1$

    static {
        for (int i = 0; i < 256; i++) {
            characterClass[i] = CHARCLASS_ANY;
        }
        // delimiters
        characterClass['('] = CHARCLASS_DELIMITER;
        characterClass[')'] = CHARCLASS_DELIMITER;
        characterClass['<'] = CHARCLASS_DELIMITER;
        characterClass['>'] = CHARCLASS_DELIMITER;
        characterClass['['] = CHARCLASS_DELIMITER;
        characterClass[']'] = CHARCLASS_DELIMITER;
        characterClass['{'] = CHARCLASS_DELIMITER;
        characterClass['}'] = CHARCLASS_DELIMITER;
        characterClass['/'] = CHARCLASS_DELIMITER;
        characterClass['%'] = CHARCLASS_DELIMITER;

        // whitespace
        characterClass[' '] = CHARCLASS_WHITESPACE;
        characterClass['\t'] = CHARCLASS_WHITESPACE;
        characterClass['\r'] = CHARCLASS_WHITESPACE;
        characterClass['\n'] = CHARCLASS_WHITESPACE;
        characterClass[12] = CHARCLASS_WHITESPACE;
        characterClass[0] = CHARCLASS_WHITESPACE;

        // digits
        characterClass['0'] = CHARCLASS_DIGIT;
        characterClass['1'] = CHARCLASS_DIGIT;
        characterClass['2'] = CHARCLASS_DIGIT;
        characterClass['3'] = CHARCLASS_DIGIT;
        characterClass['4'] = CHARCLASS_DIGIT;
        characterClass['5'] = CHARCLASS_DIGIT;
        characterClass['6'] = CHARCLASS_DIGIT;
        characterClass['7'] = CHARCLASS_DIGIT;
        characterClass['8'] = CHARCLASS_DIGIT;
        characterClass['9'] = CHARCLASS_DIGIT;

        // number special
        characterClass['.'] = CHARCLASS_NUMBERSPECIAL;
        characterClass['-'] = CHARCLASS_NUMBERSPECIAL;
        characterClass['+'] = CHARCLASS_NUMBERSPECIAL;

        // alpha
        for (int i = 'a'; i <= 'z'; i++) {
            characterClass[i] = CHARCLASS_TOKEN;
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            characterClass[i] = CHARCLASS_TOKEN;
        }

        // contentstream allowed token characters
        characterClass['\''] = CHARCLASS_TOKEN;
        characterClass['"'] = CHARCLASS_TOKEN;
    }

    /**
     * evaluate to true if i is a PDF Delimiter char.
     * <p>
     * <p>
     * See pdf spec delimiter characters.
     * </p>
     *
     * @param i i a byte representation
     * @return true if i is a PDF delimiter char
     */
    public static boolean isDelimiter(int i) {
        return characterClass[i] == CHARCLASS_DELIMITER;
    }

    /**
     * evaluate to true if i is a valid digit.
     *
     * @param i i a byte representation
     * @return true if i is a valid digit
     */
    public static boolean isDigit(int i) {
        return characterClass[i] == CHARCLASS_DIGIT;
    }

    /**
     * evaluate to true if i is a valid line terminator.
     *
     * @param i i a byte representation
     * @return true if i is a valid line terminator
     */
    public static boolean isEOL(int i) {
        return (i == CHAR_CR) || (i == CHAR_LF) || (i == 12);
    }

    /**
     * evaluate to true if i is a valid first char for a number token.
     *
     * @param i i a byte representation
     * @return true if i is a valid first char for a number token
     */
    public static boolean isNumberStart(int i) {
        int cc = characterClass[i];
        return (cc == CHARCLASS_DIGIT) || (cc == CHARCLASS_NUMBERSPECIAL);
    }

    /**
     * evaluate to true if i is a valid octal digit.
     *
     * @param i i a byte representation
     * @return true if i is a valid octal digit
     */
    public static boolean isOctalDigit(int i) {
        return ((i >= '0') && (i <= '7'));
    }

    /**
     * evaluate to true if i is a valid string token start.
     *
     * @param i i a byte representation
     * @return true if i is a valid string token start
     */
    public static boolean isTokenStart(int i) {
        return characterClass[i] == CHARCLASS_TOKEN;
    }

    /**
     * evaluate to true if i is a valid whitespace.
     * <p>
     * <p>
     * See pdf spec "white space characters"
     * </p>
     *
     * @param i i a byte representation
     * @return true if i is a valid whitespace
     */
    public static boolean isWhitespace(int i) {
        return characterClass[i] == CHARCLASS_WHITESPACE;
    }

    /**
     * parse the given byte array to a valid COSObject.
     *
     * @param data a byte array containing COS encoded objects
     * @return a COSObject
     * @throws IOException
     * @throws COSLoadException
     */
    public static COSObject toCOSObject(byte[] data) throws IOException, COSLoadException {
        COSDocumentParser docParser = new COSDocumentParser(null);
        return (COSObject) docParser.parseElement(new RandomAccessByteArray(data));
    }

    /**
     * A list for object lookahead (needed with PDF references)
     */
    private COSObject[] lookahead = {null, null, null};

    /**
     * The number of elements currently in the lookahead buffer.
     */
    private int lookaheadCount = 0;

    private ISystemSecurityHandler securityHandler;

    /**
     * A flag indicating we should flush the lookahead
     */
    private boolean flushLookahead = false;

    private FastByteArrayOutputStream localStream = new FastByteArrayOutputStream();

    /**
     * an exception handler for handling messages eg PDFA compliance checks *
     */
    private IPDFParserExceptionHandler exceptionHandler;

    private COSObjectKey objectKey;

    protected boolean check = false;

    protected abstract COSIndirectObject createObjectReference(IRandomAccess input)
            throws IOException, COSLoadException;

    public IPDFParserExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    protected COSObjectKey getObjectKey() {
        return objectKey;
    }

    protected ISystemSecurityHandler getSecurityHandler() {
        return securityHandler;
    }

    /**
     * Handle an error if an exceptionHandler is set.
     *
     * @param error
     * @throws COSLoadException
     */
    public void handleError(COSLoadError error) throws COSLoadException {
        if (exceptionHandler != null) {
            exceptionHandler.error(error);
        } else {
            throw error;
        }
    }

    /**
     * Handle a warning if an exceptionHandler is set.
     *
     * @param warning
     * @throws COSLoadException
     */
    public void handleWarning(COSLoadWarning warning) throws COSLoadException {
        if (exceptionHandler != null) {
            exceptionHandler.warning(warning);
        } else {
            // it is just a warning...
        }
    }

    /**
     * in order to read references we need a two object lookahead for the
     * integer numbers this method pops the first object from the fifo
     * structure.
     *
     * @return The topmost {@link COSObject}in the object lookahead buffer.
     */
    protected COSObject lookaheadPop() {
        COSObject result = lookahead[0];
        lookahead[0] = lookahead[1];
        lookahead[1] = lookahead[2];
        lookahead[2] = null;
        lookaheadCount--;
        if (lookaheadCount <= 0) {
            // everything flushed now
            lookaheadCount = 0;
            this.flushLookahead = false;
        }
        return result;
    }

    /**
     * in order to read references we need a two object lookahead for the
     * integer numbers this method pushes an object in the fifo structure.
     *
     * @param obj The {@link COSObject}to push in the buffer.
     */
    protected void lookaheadPush(COSObject obj) {
        lookahead[lookaheadCount++] = obj;
    }

    /**
     * comment see PDF Reference v1.4, chapter 3.1.2 comments Comment ::= "%"
     * anyChar EOL read until end of line.
     *
     * @throws IOException
     */
    protected void parseComment(IRandomAccess input) throws IOException {
        int next;
        while (true) {
            next = input.read();
            if (next == -1) {
                break;
            }
            if (isEOL(next)) {
                break;
            }
        }
    }

    /**
     * parse the basic elements from the current stream position.
     * <p>
     * <p>
     * see PDF Reference v1.4, chapter 3.2 Objects
     * </p>
     * <p>
     * <p>
     * COSObject ::= COSToken | COSBoolean | COSString | COSNumber | COSName |
     * COSNull | COSArray | COSDictionary | COSStream
     * </p>
     *
     * @return the object parsed
     * @throws IOException
     * @throws COSLoadException
     */
    public Object parseElement(IRandomAccess input) throws IOException, COSLoadException {
        int next;
        do {
            next = input.read();
            if (next == -1) {
                // thats a legal end
                return null;
            }

            // we have found a non-whitespace character
            if (isNumberStart(next)) {
                return parseOnObjectNumber(input, next);
            }
            if (next == '(') {
                return parseOnObjectString(input);
            }
            if (isTokenStart(next)) {
                byte[] token = readTokenElement(input, next);
                if (token.length == 1) {
                    if (token[0] == TOKEN_R[0]) {
                        return TOKEN_R;
                    }
                } else if (token.length == 4) {
                    if ((token[0] == TOKEN_true[0]) && (token[1] == TOKEN_true[1]) && (token[2] == TOKEN_true[2]) && (
                            token[3]
                            == TOKEN_true[3])) {
                        return COSTrue.create();
                    }
                    if ((token[0] == TOKEN_null[0]) && (token[1] == TOKEN_null[1]) && (token[2] == TOKEN_null[2]) && (
                            token[3]
                            == TOKEN_null[3])) {
                        return COSNull.create();
                    }
                } else if (token.length == 5) {
                    if ((token[0] == TOKEN_false[0])
                        && (token[1] == TOKEN_false[1])
                        && (token[2] == TOKEN_false[2])
                        && (token[3] == TOKEN_false[3])
                        && (token[4] == TOKEN_false[4])) {
                        return COSFalse.create();
                    }
                }
                return token;
            }
            if (next == '/') {
                return parseOnObjectName(input);
            }

            // performance shortcut for simple space
            if ((next == ' ') || isWhitespace(next)) {
                continue;
            }
            if (next == '%') {
                parseComment(input);
                continue;
            }

            // before we start parsing a container we must flush lookahead
            if (lookaheadCount > 0) {
                input.seekBy(-1);
                return null;
            }
            if (next == '<') {
                return parseOnObjectStreamOrDictionaryOrHexString(input);
            }
            if (next == '[') {
                return parseOnObjectArray(input);
            }
            // unread, i do not understand...
            // return null if char unexpected, if this is an error depends on
            // context
            input.seekBy(-1);
            return null;
        } while (true);
    }

    /**
     * pdf header see PDF Reference v1.4, chapter 3.4.1 Header COSHEader ::=
     * "%PDF-" version.
     *
     * @throws IOException
     * @throws COSLoadException
     */
    public STDocType parseHeader(IRandomAccess input) throws IOException, COSLoadException {
        int next;
        boolean errHeader = false;
        while (true) {
            next = input.read();
            if (next == -1) {
                break;
            }

            // read up to %
            if ((next != '%')) {
                errHeader = true;
                continue;
            }
            break;
        }
        STDocType docType = new STDocType();
        if (next == -1) {
            COSLoadError e = new COSLoadError("file format error. document must start with %PDF or %FDF");
            handleError(e);
        } else {
            byte[] token = new byte[4];
            token[0] = (byte) next;
            input.read(token, 1, 3);
            if (Arrays.equals(token, TOKEN_PDFHEADER)) {
                docType.setTypeName("PDF");
            } else if (Arrays.equals(token, TOKEN_FDFHEADER)) {
                docType.setTypeName("FDF");
            } else {
                input.seekBy(-token.length);
                COSLoadError e =
                        new COSLoadError("file format error. document must start with %PDF or %FDF at character index "
                                         + input.getOffset());
                handleError(e);
            }
            if (errHeader) {
                COSLoadWarning w = new COSLoadWarning(
                        "file format error. document must start with %PDF or %FDF at character index "
                        + input.getOffset());
                handleWarning(w);
            }
            input.read();
            byte[] version = readToken(input);
            if (version == null) {
                COSLoadError e = new COSLoadError("file format error. no pdf/fdf version info found at character index "
                                                  + input.getOffset());
                handleError(e);
            } else {
                docType.setVersion(StringTools.toString(version));
            }
        }
        return docType;
    }

    /**
     * Parse a valid COS object for use in document context from the current
     * stream position.
     * <p>
     * <p>
     * see PDF Reference v1.4, chapter 3.2 Objects
     * </p>
     * <p>
     * <p>
     * this implementation is a little more complicated, as we hava a two object
     * lookahead to detect references.
     * <p>
     * {@code
     * COSObject ::=   COSReference |
     * COSBoolean |
     * COSString |
     * COSNumber |
     * COSName |
     * COSNull |
     * COSArray |
     * COSDictionary |
     * COSStream
     * <p>
     * }
     * <p>
     * </p>
     *
     * @return the object parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSDocumentElement parseObject(IRandomAccess input) throws IOException, COSLoadException {
        if (flushLookahead) {
            return lookaheadPop();
        }

        // parse another element
        Object parsedElement = parseElement(input);
        if (parsedElement == null) {
            flushLookahead = true;
            return lookaheadPop();
        }

        // try to detect reference "R"
        COSObject resultObject;
        if (parsedElement instanceof byte[]) {
            if (TOKEN_R == parsedElement) {
                // reference detected, clean up lookahed and return
                return createObjectReference(input);
            }
            // we have found a token that has to be re-read in another context
            // take care of consumed whitespace!
            input.seekBy(-1);
            int next = input.read();

            // performance shortcut for simple space
            if ((next == ' ') || isWhitespace(next)) {
                input.seekBy(-1);
            }
            input.seekBy(-((byte[]) parsedElement).length);
            this.flushLookahead = true;
            return lookaheadPop();
        }
        resultObject = (COSObject) parsedElement;

        // build up lookahead stack
        if (resultObject instanceof COSNumber) {
            lookaheadPush(resultObject);
            // return one object if lookahead larger than 2
            if (lookaheadCount > 2) {
                return lookaheadPop();
            }

            // enter parse recursive
            return parseObject(input);
        }

        // shortcut to avoid building entry in lookahead
        if (lookaheadCount > 0) {
            lookaheadPush(resultObject);
            this.flushLookahead = true;
            return lookaheadPop();
        }
        return resultObject;
    }

    protected COSObject parseObjectDictionary(IRandomAccess input) throws IOException, COSLoadException {
        int next;
        next = input.read();
        if (next != '<') {
            input.seekBy(-1);
            COSLoadError e = new COSLoadError("'<' expected at character index " + input.getOffset());
            handleError(e);
        }
        next = input.read();
        if (next != '<') {
            input.seekBy(-1);
            COSLoadError e = new COSLoadError("'<' expected at character index " + input.getOffset());
            handleError(e);
        }
        return parseOnObjectDictionary(input);
    }

    /**
     * parse a COS array from the current stream position. see PDF Reference
     * v1.4, chapter 3.2.5 Array objects COSArray ::= "[" (COSObject) "]"
     *
     * @return the array parsed
     * @throws IOException
     * @throws IOException
     */
    protected COSObject parseOnObjectArray(IRandomAccess input) throws COSLoadException, IOException {
        try {
            int next;
            COSArray result = COSArray.create();
            if (securityHandler != null) {
                securityHandler.pushContextObject(result);
            }
            while (true) {
                COSDocumentElement element = parseObject(input);
                if (element == null) {
                    next = input.read();
                    if (next == -1) {
                        unexpectedEndOfInput(input);
                    }
                    if (next != ']') {
                        byte[] badElement = readTokenElement(input, next);
                        if (check) {
                            COSLoadWarning pwarn =
                                    new COSLoadWarning("bad array element (" + new String(badElement) + ")");
                            pwarn.setHint(result);
                            handleWarning(pwarn);
                        }
                        continue;
                    }
                    break;
                }
                result.basicAddSilent(element);
            }
            if (check && (result.size() > 8191)) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_ARRAYSIZE);
                pwarn.setHint(result);
                handleWarning(pwarn);
            }
            return result;
        } finally {
            if (securityHandler != null) {
                securityHandler.popContextObject();
            }
        }
    }

    /**
     * parse a COS dictionary from the current stream position. see PDF
     * Reference v1.4, chapter 3.2.6 Dictionary objects
     * <p>
     * {@code
     * COSDictionary ::= &quot;&lt;&lt;&quot; (COSName COSObject)* &quot;&gt;&gt;&quot;
     * }
     *
     * @return the dictionary parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectDictionary(IRandomAccess input) throws IOException, COSLoadException {
        try {
            int next;
            COSDictionary dict = COSDictionary.create();
            if (securityHandler != null) {
                securityHandler.pushContextObject(dict);
            }
            try {
                while (true) {
                    COSDocumentElement keyObject = parseObject(input);
                    if (keyObject == null) {
                        // when parsing dictionaries in CMap we may encounter
                        // the
                        // keyword "def" - don't know if this is legal, but
                        // happens...
                        input.mark();
                        Object tempElement = parseElement(input);
                        if (tempElement != null) {
                            // try to detect reference "def"
                            if (tempElement instanceof byte[]) {
                                if (Arrays.equals(TOKEN_def, (byte[]) tempElement)) {
                                    // this is no-op
                                    continue;
                                }
                            }
                        }
                        input.reset();
                        break;
                    }
                    COSName dictKey = (COSName) keyObject;
                    COSDocumentElement value = parseObject(input);
                    if (value == null) {
                        COSLoadError e =
                                new COSLoadError("missing value for key '" + keyObject + "' at character index " + input
                                        .getOffset());
                        handleError(e);
                    } else {
                        dict.basicPutSilent(dictKey, value);
                    }
                }
            } catch (ClassCastException ignored) {
                COSLoadError e = new COSLoadError("name expected at character index " + input.getOffset());
                handleError(e);
            }
            next = input.read();
            if (next != '>') {
                COSLoadError e = new COSLoadError("unexpected character ("
                                                  + (char) next
                                                  + ") at character index "
                                                  + input.getOffset());
                handleError(e);
            }
            next = input.read();
            if (next != '>') {
                COSLoadError e = new COSLoadError("unexpected character ("
                                                  + (char) next
                                                  + ") at character index "
                                                  + input.getOffset());
                handleError(e);
            }
            return dict;
        } finally {
            if (securityHandler != null) {
                securityHandler.popContextObject();
            }
        }
    }

    /**
     * parse a COS string encoded in hex from the current stream position. see
     * PDF Reference v1.4, chapter 3.2.3 String objects
     * <p>
     * {@code
     * COSString ::= COSString | COSHexString
     * COSHexString ::= &quot;&lt;&quot; (hexChar)* &quot;&gt;&quot;
     * }
     *
     * @return the string parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectHexString(IRandomAccess input, int next) throws IOException, COSLoadException {
        localStream.reset();
        boolean secondDigit = false;
        int digitValue = 0;
        int charValue = 0;
        while (true) {
            digitValue = HexTools.hexDigitToInt((char) next);
            if (digitValue == -1) {
                if (next == -1) {
                    break;
                }
                if (next == '>') {
                    break;
                }
                if (!isWhitespace(next)) {
                    IOException ioe = new IOException("<" + next + "> '" + (char) next + "' not a valid hex char");

                    // todo 3 @mit Warning is useless. Due to the IOException, such documents cannot be loaded anyway. Remove it?

                    // a warning for PDF/A related checks will be triggered
                    // exception is handled right on track
                    COSLoadWarning pwarn = new COSLoadWarning(C_WARN_ILLEGALHEX);
                    pwarn.setHint(Long.valueOf(input.getOffset()));
                    handleWarning(pwarn);
                    throw ioe;
                }
            } else {
                if (secondDigit) {
                    charValue = (charValue << 4) + digitValue;
                    localStream.write(charValue);
                    secondDigit = false;
                } else {
                    secondDigit = true;
                    charValue = digitValue;
                }
            }
            next = input.read();
        }
        if (secondDigit) {
            // this is a warning for uneven numbers on hex codes
            if (check) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_UNEVENHEX);
                pwarn.setHint(Long.valueOf(input.getOffset()));
                handleWarning(pwarn);
            }
            // assume trailing "0"
            charValue = charValue << 4;
            localStream.write(charValue);
        }

        COSString result;
        if ((securityHandler == null) || (objectKey == null)) {
            result = COSString.createHex(localStream.toByteArray());
        } else {
            byte[] bytes = localStream.toByteArray();
            try {
                byte[] decrypted = securityHandler.decryptString(objectKey, bytes);
                result = COSString.createHex(decrypted);
            } catch (COSSecurityException e) {
                result = COSString.createHex(bytes);
                COSLoadWarning warning = new COSLoadWarning("error decrypting string " + objectKey, e);
                handleWarning(warning);
            }
        }
        if (check && (result.stringValue().length() > 32767)) {
            COSLoadWarning pwarn = new COSLoadWarning(C_WARN_STRING_TOO_LONG);
            pwarn.setHint(result);
            handleWarning(pwarn);
        }
        return result;
    }

    /**
     * parse a COS name from the current stream position. see PDF Reference
     * v1.4, chapter 3.2.4 Name Objects COSName ::= "/" nameChars
     *
     * @return the name parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectName(IRandomAccess input) throws IOException, COSLoadException {
        int next;
        localStream.reset();
        do {
            next = input.read();
            if (next == -1) {
                break;
            }

            // performance shortcut for simple space
            if ((next == ' ') || isWhitespace(next)) {
                break;
            }
            if (isDelimiter(next)) {
                input.seekBy(-1);
                break;
            }
            if (next == '#') {
                next = input.read();

                int digit1 = HexTools.hexDigitToInt((char) next);
                if (digit1 == -1) {
                    COSLoadError e = new COSLoadError("<"
                                                      + next
                                                      + "> not a valid hex char at character index "
                                                      + input.getOffset());
                    handleError(e);
                }
                next = input.read();

                int digit2 = HexTools.hexDigitToInt((char) next);
                if (digit2 == -1) {
                    COSLoadError e = new COSLoadError("<"
                                                      + next
                                                      + "> not a valid hex char at character index "
                                                      + input.getOffset());
                    handleError(e);
                }
                localStream.write((digit1 << 4) + digit2);
            } else {
                localStream.write(next);
            }
        } while (true);
        byte[] bytes = localStream.toByteArray();
        COSName result = COSName.create(bytes);
        if (check && (result.stringValue().length() > 127)) {
            COSLoadWarning pwarn = new COSLoadWarning(C_WARN_NAME_TOO_LONG);
            pwarn.setHint(result);
            handleWarning(pwarn);
        }
        return result;
    }

    /**
     * parse a COS number from the current stream position. see PDF Reference
     * v1.4, chapter 3.2.2 Numeric objects COSNumber ::= COSFixed | COSInteger
     * COSFixed ::= (+ | -)? (digit) "." (digit) COSInteger ::= (+ | -)? (digit)
     *
     * @return the number parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectNumber(IRandomAccess input, int next) throws IOException, COSLoadException {
        boolean isFixed = false;
        localStream.reset();
        isFixed = next == '.';
        localStream.write((byte) next);
        do {
            next = input.read();
            if (next == -1) {
                break;
            } else if (isDigit(next)) {
                localStream.write((byte) next);
            } else if (next == '.') {
                isFixed = true;
                localStream.write((byte) '.');
            } else if ((next == ' ') || isWhitespace(next)) {
                break;
            } else {
                input.seekBy(-1);
                break;
            }
        } while (true);
        if (isFixed) {
            return COSFixed.create(localStream.getBytes(), 0, localStream.size());
        }
        byte[] streamBytes = localStream.getBytes();
        int streamSize = localStream.size();
        if (exceptionHandler != null) {
            COSInteger result = COSInteger.createStrict(streamBytes, 0, streamSize);
            if (result != null) {
                return result;
            }
            COSLoadWarning warning = new COSLoadWarning(C_WARN_LARGE_INT);
            handleWarning(warning);
        }
        return COSInteger.create(streamBytes, 0, streamSize);
    }

    /**
     * parse a COS stream from the current stream position. see PDF Reference
     * v1.4, chapter 3.2.7 Stream objects COSStream ::= COSDictionary "stream"
     * bytes "endstream"
     *
     * @param dict The object that should be filled with the dictionary entries.
     * @return The stream parsed.
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectStream(IRandomAccess input, COSDictionary dict)
            throws IOException, COSLoadException {
        COSStream stream = COSStream.create(dict);

        byte[] token = new byte[5];
        // read "tream", "s" already consumed
        input.read(token);
        if (!Arrays.equals(token, TOKEN_s_tream)) {
            input.seekBy(-token.length - 1);
            COSLoadError e =
                    new COSLoadError("file format error. 'stream' expected at character index " + input.getOffset());
            handleError(e);
        }

        // allow for at max two separator chars after "stream"
        int next;
        next = input.read();
        if (next == -1) {
            unexpectedEndOfInput(input);
        }
        if (next == CHAR_CR) {
            next = input.read();
        }
        if (next != CHAR_LF) {
            // ?? its legal to have NO separator
            // ?? there are testdocuments that provide only a single CR
            if (check) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_STREAMEOL);
                pwarn.setHint(Long.valueOf(input.getOffset()));
                handleWarning(pwarn);
            }
            input.seekBy(-1);
        }

        long offset = input.getOffset();
        int length = -1;
        COSNumber cosLength = dict.get(COSStream.DK_Length).asInteger();
        if (cosLength == null) {
            // warning for pdfa
            if (check) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_STREAMLENGTH);
                pwarn.setHint(Long.valueOf(input.getOffset()));
                handleWarning(pwarn);
            }
        } else {
            length = cosLength.intValue();
        }
        // may be moved by reading indirect /Length !
        input.seek(offset);

        byte[] bytes = null;

        if (length < 0) {
            bytes = readStream(input);
        } else {
            bytes = new byte[length];
            int count = input.read(bytes);
            if (count < length) {
                if (check) {
                    // get additional warning for pdfa
                    COSLoadWarning pwarn = new COSLoadWarning(C_WARN_STREAMLENGTH);
                    pwarn.setHint(Long.valueOf(input.getOffset()));
                    handleWarning(pwarn);
                }
                unexpectedEndOfInput(input);
            }
        }

        if (check) {
            // pdfa compliance check
            int test = readEOL(input);
            if (test != 1) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_ENDSTREAMEOL);
                pwarn.setHint(Long.valueOf(input.getOffset()));
                handleWarning(pwarn);
            }
        } else {
            // be lazy with pdf spec and accept any whitespace before
            // 'endstream'
            readSpaces(input);
        }

        // read "endstream"
        token = new byte[9];
        input.read(token);
        if (!Arrays.equals(token, TOKEN_endstream)) {
            input.seekBy(-token.length - 1);
            // a warning for PDF/A related checks will be triggered
            COSLoadWarning pwarn = new COSLoadWarning(C_WARN_ENDSTREAMCORRUPT);
            pwarn.setHint(Long.valueOf(input.getOffset()));
            handleWarning(pwarn);

            if (length > 0) {
                // retry from the beginning with undeterminate length
                input.seek(offset);
                bytes = readStream(input);
                // read "endstream"
                token = new byte[9];
                input.read(token);
                if (!Arrays.equals(token, TOKEN_endstream)) {
                    COSLoadError e = new COSLoadError("file format error. 'endstream' expected at character index "
                                                      + input.getOffset());
                    handleError(e);
                }
            } else {
                COSLoadError e = new COSLoadError("file format error. 'endstream' expected at character index "
                                                  + input.getOffset());
                handleError(e);
            }
        }
        if ((securityHandler == null) || (objectKey == null)) {
            stream.basicSetEncodedBytes(bytes);
        } else {
            try {
                byte[] decrypted = securityHandler.decryptStream(objectKey, dict, bytes);
                stream.basicSetEncodedBytes(decrypted);
            } catch (COSSecurityException e) {
                stream.basicSetEncodedBytes(bytes);
                COSLoadWarning warning = new COSLoadWarning("error decrypting stream " + objectKey, e);
                handleWarning(warning);
            }
        }
        return stream;
    }

    /**
     * parse a COS stream or dictionary from the current stream position.
     * COSStreamOrDict ::= COSStream | COSDict
     *
     * @return the object parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectStreamOrDictionary(IRandomAccess input) throws IOException, COSLoadException {
        COSObject dict = parseOnObjectDictionary(input);
        int next;
        boolean lastWasEOL = false;
        while (true) {
            next = input.read();
            if (next == -1) {
                return dict;
            }

            // performance shortcut for simple space
            if ((next == ' ') || isWhitespace(next)) {
                lastWasEOL = next == '\n' || next == '\r';
                continue;
            }
            break;
        }
        if (next == 's') {
            return parseOnObjectStream(input, (COSDictionary) dict);
        }
        if (next == 'e' && check && !lastWasEOL) {
            COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLEEOL_OBJ);
            pwarn.setHint(new Long(input.getOffset()));
            handleWarning(pwarn);
        }
        input.seekBy(-1);
        return dict;
    }

    /**
     * parse a COS stream or dictionary or hex string from the current stream
     * position. COSStreamOrDictOrHex ::= COSStream | COSDict | COSHexString
     *
     * @return the object parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectStreamOrDictionaryOrHexString(IRandomAccess input)
            throws IOException, COSLoadException {
        int next;
        next = input.read();
        if (next == '<') {
            return parseOnObjectStreamOrDictionary(input);
        }
        return parseOnObjectHexString(input, next);
    }

    /**
     * parse a COS string from the current stream position. see PDF Reference
     * v1.4, chapter 3.2.3. String objects COSString ::= "(" stringData ")"
     *
     * @return the string parsed
     * @throws IOException
     * @throws COSLoadException
     */
    protected COSObject parseOnObjectString(IRandomAccess input) throws IOException, COSLoadException {
        int next;
        int paraCount = 0;
        localStream.reset();
        while (true) {
            next = input.read();
            if (next == '\\') {
                int c = readEscape(input);
                if (c != -1) {
                    localStream.write(c);
                }
            } else if (next == ')') {
                if (paraCount > 0) {
                    paraCount--;
                    localStream.write(')');
                } else {
                    break;
                }
            } else if (next == CHAR_CR) {
                // eol is always \n in a string
                next = input.read();
                if (next != -1 && next != CHAR_LF) {
                    input.seekBy(-1);
                }
                localStream.write(CHAR_LF);
            } else if (next == '(') {
                paraCount++;
                localStream.write('(');
            } else if (next == -1) {
                unexpectedEndOfInput(input);
            } else {
                localStream.write(next);
            }
        }
        COSString result;
        if ((securityHandler == null) || (objectKey == null)) {
            result = COSString.create(localStream.toByteArray());
        } else {
            byte[] bytes = localStream.toByteArray();
            try {
                byte[] decrypted = securityHandler.decryptString(objectKey, bytes);
                result = COSString.create(decrypted);
            } catch (COSSecurityException e) {
                result = COSString.create(bytes);
                COSLoadWarning warning = new COSLoadWarning("error decrypting string " + objectKey, e);
                handleWarning(warning);
            }
        }
        if (check && (result.stringValue().length() > 32767)) {
            COSLoadWarning pwarn = new COSLoadWarning(C_WARN_STRING_TOO_LONG);
            pwarn.setHint(result);
            handleWarning(pwarn);
        }
        return result;
    }

    /**
     * determine number of EOL sequences
     *
     * @param input
     * @return {@code number of EOL}
     * @throws IOException
     */
    protected int readEOL(IRandomAccess input) throws IOException {
        int next = input.read();
        if (next == -1) {
            return 0;
        }
        if (next == CHAR_CR) {
            next = input.read();
            if (next == -1) {
                return 1;
            } else if (next == CHAR_LF) {
                next = input.read();
                if (next == -1) {
                    return 1;
                } else if (isWhitespace(next)) {
                    readSpaces(input);
                    return 2;
                } else {
                    input.seekBy(-1);
                    return 1;
                }
            } else if (isWhitespace(next)) {
                readSpaces(input);
                return 2;
            } else {
                input.seekBy(-1);
                return 1;
            }
        }
        if (next == CHAR_LF) {
            next = input.read();
            if (next == -1) {
                return 1;
            } else if (isWhitespace(next)) {
                readSpaces(input);
                return 2;
            } else {
                input.seekBy(-1);
                return 1;
            }
        }
        if (isWhitespace(next)) {
            readSpaces(input);
            return 2;
        }
        input.seekBy(-1);
        return 0;
    }

    /**
     * read an esacped char from the stream.
     *
     * @return the character corresponding to the escape code
     * @throws IOException
     */
    protected int readEscape(IRandomAccess input) throws IOException {
        int next = 0;
        next = input.read();
        if (next == -1) {
            return -1;
        }
        if (isOctalDigit(next)) {
            input.seekBy(-1);
            return readOctalChar(input);
        }
        if (next == CHAR_LF) {
            return -1;
        }
        if (next == CHAR_CR) {
            next = input.read();
            if (next != -1 && next != CHAR_LF) {
                input.seekBy(-1);
            }
            return -1;
        }
        if (next == 'n') {
            return CHAR_LF;
        }
        if (next == 'r') {
            return CHAR_CR;
        }
        if (next == 't') {
            return CHAR_HT;
        }
        if (next == 'b') {
            return CHAR_BS;
        }
        if (next == 'f') {
            return CHAR_FF;
        }
        return next;
    }

    /**
     * reads the next integer on input. consumes one trailing space if
     * consumeSpaceAfter is set to true. Consumes leading spaces and comments.
     *
     * @param input
     * @param consumeSpaceAfter
     * @return The integer read.
     * @throws IOException
     */
    public int readInteger(IRandomAccess input, boolean consumeSpaceAfter) throws IOException {
        int result = 0;
        int next;
        while (true) {
            next = input.read();
            if (next == -1) {
                return result;
            } else if ((next == ' ') || isWhitespace(next)) {
                continue;
            } else if (next == '%') {
                parseComment(input);
            } else {
                break;
            }
        }
        // avoid returning 0 for degenerate case
        boolean digitFound = false;
        while (true) {
            if (isDigit(next)) {
                digitFound = true;
                result = ((result * 10) + next) - '0';
            } else {
                if (!digitFound) {
                    throw new IOException("digit expected at " + input.getOffset());
                }
                input.seekBy(-1);
                break;
            }
            next = input.read();
            if (next == -1) {
                break;
            } else if ((next == ' ') || isWhitespace(next)) {
                if (!consumeSpaceAfter) {
                    input.seekBy(-1);
                }
                break;
            }
        }
        return result;
    }

    /**
     * read an octal character from the stream.
     *
     * @return the integer value of the character read or -1
     * @throws IOException
     */
    protected int readOctalChar(IRandomAccess input) throws IOException {
        int result = -1;
        int c = 0;

        c = input.read();
        if (isOctalDigit(c)) {
            result = c - '0';
            c = input.read();
            if (isOctalDigit(c)) {
                result = ((result << 3) + c) - '0';
                c = input.read();
                if (isOctalDigit(c)) {
                    result = ((result << 3) + c) - '0';
                } else {
                    if (c == -1) {
                        return result;
                    }
                    input.seekBy(-1);
                }
            } else {
                if (c == -1) {
                    return result;
                }
                input.seekBy(-1);
            }
        } else {
            if (c == -1) {
                return result;
            }
            input.seekBy(-1);
        }
        return result;
    }

    /**
     * read all characters until EOF or non space char appears. the first non
     * space char is pushed back so the next char read is the first non space
     * char.
     *
     * @throws IOException
     */
    public void readSpaces(IRandomAccess input) throws IOException {
        int next = 0;
        while (true) {
            next = input.read();
            if (next == -1) {
                break;
            }
            // performance shortcut for simple space
            if ((next == ' ') || isWhitespace(next)) {
                continue;
            }
            input.seekBy(-1);
            break;
        }
    }

    /**
     * Read all characters up to "endstream" and assume them belonging to the
     * stream.
     * <p>
     * ATTENTION this is a heuristic approach as the tag "endstream" may be part
     * of the stream data!
     *
     * @return All characters up to "endstream"
     * @throws IOException
     */
    protected byte[] readStream(IRandomAccess input) throws IOException {
        byte[] token = new byte[8];
        localStream.reset();
        int next;
        while (true) {
            next = input.read();
            if (next == 'e') {
                input.read(token);
                if (Arrays.equals(token, TOKEN_ndstream)) {
                    input.seekBy(-TOKEN_endstream.length);
                    return localStream.toByteArray();
                }
                input.seekBy(-token.length);
            } else if (next == -1) {
                break;
            }
            localStream.write(next);
        }
        if (localStream.size() == 0) {
            return null;
        }
        return localStream.toByteArray();
    }

    /**
     * read a single token.
     *
     * @return the array of characters belonging to the token
     * @throws IOException
     */
    public byte[] readToken(IRandomAccess input) throws IOException {
        //
        int next;
        while (true) {
            next = input.read();
            if (next == -1) {
                return null;
            } else if ((next == ' ') || isWhitespace(next)) {
                continue;
            } else if (next == '%') {
                parseComment(input);
            } else {
                break;
            }
        }
        return readTokenElement(input, next);
    }

    /**
     * derive of readToken, populates the messages list with non-fatal error
     * messages
     *
     * @param input
     * @param messages
     * @return token bytes
     * @throws IOException
     */
    public byte[] readToken(IRandomAccess input, List messages, boolean strict) throws IOException {
        int next;
        int countWS = 0;
        boolean crEol = false;
        while (true) {
            next = input.read();
            if (next == -1) {
                return null;
            } else if ((next == ' ') || isWhitespace(next)) {
                if (!strict && !crEol && (next == ' ')) {
                    // ignore ignorable space
                } else {
                    countWS++;
                }
                if (countWS > 1 && !(crEol && next == '\n')) {
                    messages.add(C_TOKEN_ADDWSB);
                } else if (strict && (next == ' ')) {
                    messages.add(C_TOKEN_WSB);
                } else { // may be CR+EOL
                    crEol = next == '\r';
                }
                continue;
            } else if (next == '%') {
                messages.add(C_TOKEN_COMMENT);
                parseComment(input);
            } else {
                break;
            }
        }
        return readTokenElement(input, next, messages);
    }

    protected byte[] readTokenElement(IRandomAccess input, int next) throws IOException {
        localStream.reset();
        //
        localStream.write(next);
        do {
            next = input.read();
            if ((next == ' ') || isWhitespace(next)) { // performance
                // shortcut
                break;
            }
            if (isDelimiter(next)) {
                input.seekBy(-1);
                break;
            }
            localStream.write(next);
        } while (true);
        return localStream.toByteArray();
    }

    /**
     * derive of readToken, populates the messages list with non-fatal error
     * messages
     *
     * @param input
     * @param next
     * @param messages
     * @return token bytes
     * @throws IOException
     */
    protected byte[] readTokenElement(IRandomAccess input, int next, List messages) throws IOException {
        localStream.reset();
        localStream.write(next);
        do {
            next = input.read();
            if (next == -1) {
                break;
            }
            if ((next == ' ') || isWhitespace(next)) { // performance
                // shortcut
                if (next == ' ') {
                    messages.add(C_TOKEN_ADDWSA);
                }
                next = input.read();
                if ((next == ' ')) { // performance
                    // shortcut
                    messages.add(C_TOKEN_ADDWSA2);
                }
                if (next != -1) {
                    input.seekBy(-1);
                }
                break;
            }
            if (isDelimiter(next)) {
                messages.add(C_TOKEN_NOWSA);
                input.seekBy(-1);
                break;
            }
            localStream.write(next);
        } while (true);
        return localStream.toByteArray();
    }

    public void setExceptionHandler(IPDFParserExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
        check = exceptionHandler != null;
    }

    protected void setObjectKey(COSObjectKey objectKey) {
        this.objectKey = objectKey;
    }

    protected void setSecurityHandler(ISystemSecurityHandler securityHandler) {
        this.securityHandler = securityHandler;
    }

    protected void unexpectedEndOfInput(IRandomAccess input) throws IOException, COSLoadException {
        COSLoadError e =
                new COSLoadError("file format error. unexpected end of input at character index " + input.getOffset());
        handleError(e);
    }
}
