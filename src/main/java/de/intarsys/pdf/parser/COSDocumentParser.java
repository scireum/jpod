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

import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.tools.randomaccess.IRandomAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A parser for PDF data streams.
 * <p>
 * <p>
 * The parser will create a object representation of the pdf document using COS
 * level objects.
 * </p>
 * <p>
 * <p>
 * The parser is a one pass, read everything implementation.
 * </p>
 */
public class COSDocumentParser extends PDFParser {

    /**
     * use a buffer large than specified by the spec. we already had documents
     * with whitespace padding > 1024 bytes!
     */
    public static final int SEARCH_BUFFER_SIZE = 2048;

    private STDocument doc;

    public COSDocumentParser(STDocument doc) {
        this.doc = doc;
    }

    /*
     * create a COS reference out of the objects in the parsers lookahead see
     * PDF Reference v1.4, chapter 3.2.9 Indirect objects COSReference ::=
     * objNum genNum "R"
     *
     * @return the reference object created
     *
     * @throws IOException @throws COSLoadException
     */
    @Override
    protected COSIndirectObject createObjectReference(IRandomAccess input) throws IOException, COSLoadException {
        COSObject obj1 = lookaheadPop();
        COSObject obj2 = lookaheadPop();
        if (!(obj1 instanceof COSInteger) || !(obj2 instanceof COSInteger)) {
            COSLoadError e = new COSLoadError("illegal reference at character index " + input.getOffset());
            handleError(e);
        }
        int objectNumber = ((COSInteger) obj1).intValue();
        int generationNumber = ((COSInteger) obj2).intValue();
        return getDoc().getObjectReference(objectNumber, generationNumber);
    }

    public STDocument getDoc() {
        return doc;
    }

    public boolean isTokenXRefAt(IRandomAccess input, int offset) throws IOException {
        input.seek(offset);
        readSpaces(input);
        byte[] token = new byte[4];
        input.read(token);
        return Arrays.equals(token, PDFParser.TOKEN_xref);
    }

    /**
     * read a pdf style object from the input. see PDF Reference v1.4, chapter
     * 3.2.9 Indirect Objects COSIndirectObject ::= ObjNum GenNum "obj" Object
     * "endobj"
     *
     * @return The parsed object.
     * @throws IOException
     * @throws COSLoadException
     */
    public COSObject parseIndirectObject(IRandomAccess input, ISystemSecurityHandler securityHandler)
            throws IOException, COSLoadException {
        COSObjectKey key = parseIndirectObjectKey(input);

        // this may be called recursive in a stream for its length
        COSObjectKey oldObjectKey = getObjectKey();
        ISystemSecurityHandler oldSecurityHandler = getSecurityHandler();
        try {
            setObjectKey(key);
            setSecurityHandler(securityHandler);
            COSObject object = parseIndirectObjectBody(input);
            if (object == null) {
                object = COSNull.NULL;
            }
            return object;
        } finally {
            setObjectKey(oldObjectKey);
            setSecurityHandler(oldSecurityHandler);
        }
    }

    protected COSObject parseIndirectObjectBody(IRandomAccess input) throws IOException, COSLoadException {
        byte[] token;
        COSDocumentElement element = parseObject(input);
        if (element == null) {
            COSLoadError e = new COSLoadError("object expected at character index " + input.getOffset());
            handleError(e);
        } else {
            if (element.isReference()) {
                COSLoadError e =
                        new COSLoadError("object reference not allowed in a indirect object at character index " + input
                                .getOffset());
                handleError(e);
            }
            if (check) {
                List messages = new ArrayList();
                token = readToken(input, messages, false);
                // todo 2 kkr add check for additional whitespace after
                // endstream
                // before endobj
                if (messages.size() > 0
                    && !(messages.size() == 1 && messages.contains(C_TOKEN_ADDWSB))
                    && !(messages.size() == 1 && messages.contains(C_TOKEN_ADDWSA2))) {
                    COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLEEOL_OBJ);
                    pwarn.setHint(new Long(input.getOffset()));
                    handleWarning(pwarn);
                }
            } else {
                token = readToken(input);
            }
            if (token == null) {
                COSLoadError e = new COSLoadError("unexpected end of file");
                handleError(e);
            }
            if (!Arrays.equals(token, TOKEN_endobj)) {
                input.seekBy(-token.length);
                COSLoadWarning w = new COSLoadWarning(C_WARN_ENDOBJ_MISSING);
                handleWarning(w);
            }
        }
        return (COSObject) element;
    }

    protected COSObjectKey parseIndirectObjectKey(IRandomAccess input) throws IOException, COSLoadException {
        byte[] token;

        // pdfa compliance here, must verify the existence of a single space
        // between
        // object number, generation and obj keyword
        List messages = new ArrayList();

        // object number
        if (check) {
            token = readToken(input, messages, true);
        } else {
            token = readToken(input);
        }
        if (token == null) {
            COSLoadError e = new COSLoadError("unexpected end of file");
            handleError(e);
        }
        int objNumber = 0;
        try {
            objNumber = Integer.parseInt(new String(token));
            if (messages.size() > 1) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLESPACE_OBJ);
                pwarn.setHint(new Long(input.getOffset()));
                handleWarning(pwarn);
            }
        } catch (NumberFormatException ex) {
            COSLoadError e = new COSLoadError("invalid object number at character index " + input.getOffset());
            handleError(e);
        }

        // generation number
        messages.clear();
        if (check) {
            token = readToken(input, messages, true);
        } else {
            token = readToken(input);
        }

        if (token == null) {
            COSLoadError e = new COSLoadError("unexpected end of file");
            handleError(e);
        }
        int genNumber = 0;
        try {
            genNumber = Integer.parseInt(new String(token));
            if (messages.size() > 1) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLESPACE_OBJ);
                pwarn.setHint(new Long(input.getOffset()));
                handleWarning(pwarn);
            }
        } catch (NumberFormatException ex) {
            COSLoadError e = new COSLoadError("invalid generation number at character index " + input.getOffset());
            handleError(e);
        }

        // obj keyword
        messages.clear();
        if (check) {
            token = readToken(input, messages, true);
        } else {
            token = readToken(input);
        }
        if (token == null) {
            COSLoadError e = new COSLoadError("unexpected end of file");
            handleError(e);
        }
        if (!Arrays.equals(token, TOKEN_obj)) {
            input.seekBy(-token.length);
            COSLoadError e =
                    new COSLoadError("file format error, obj expected at character index " + input.getOffset());
            handleError(e);
        }
        if (check) {
            if (messages.size() > 0) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLESPACE_OBJ);
                pwarn.setHint(new Long(input.getOffset()));
                handleWarning(pwarn);
            }

            if (readEOL(input) > 1) {
                COSLoadWarning pwarn = new COSLoadWarning(C_WARN_SINGLEEOL_OBJ);
                pwarn.setHint(new Long(input.getOffset()));
                handleWarning(pwarn);
            }
        } else {
            readSpaces(input);
        }
        return new COSObjectKey(objNumber, genNumber);
    }

    /**
     * the startxref value.
     *
     * @return the startxref value
     * @throws IOException
     * @throws COSLoadException
     */
    public int parseStartXRef(IRandomAccess input) throws IOException, COSLoadException {
        readSpaces(input);
        byte[] token = new byte[9];
        input.read(token);
        if (!Arrays.equals(token, PDFParser.TOKEN_startxref)) {
            COSLoadError e =
                    new COSLoadError("file format error. 'startxref' expected at offset:" + (input.getOffset() - 9));
            handleError(e);
        }
        return readInteger(input, true);
    }

    /**
     * parse the trailer section from the current stream position. see PDF
     * Reference v1.4, chapter 3.4.4 File Trailer DocumentTrailer ::= "trailer"
     * COSDict "startxref" COSNumber
     *
     * @return the trailer dictionary
     * @throws IOException
     * @throws COSLoadException
     */
    public COSDictionary parseTrailer(IRandomAccess input) throws IOException, COSLoadException {
        byte[] token = new byte[7];
        int bytesRead = input.read(token);
        if (!Arrays.equals(token, TOKEN_trailer)) {
            if (bytesRead > 0) {
                input.seekBy(-bytesRead);
            }
            COSLoadError e =
                    new COSLoadError("file format error. 'trailer' expected at character index " + input.getOffset());
            handleError(e);
        }
        readSpaces(input);
        COSDictionary trailerDict = (COSDictionary) parseObjectDictionary(input);
        readSpaces(input);
        return trailerDict;
    }

    /**
     * Searches the offset to the first trailer in the last SEARCH_BUFFER_SIZE
     * bytes of the document. The search goes backwards starting with the last
     * byte.
     *
     * @return the offset to the first trailer found
     * @throws IOException
     * @throws COSLoadException
     */
    public int searchLastStartXRef(IRandomAccess input) throws IOException, COSLoadException {
        long startOffset = input.getLength() - SEARCH_BUFFER_SIZE;
        if (startOffset < 0) {
            startOffset = 0;
        }
        input.seek(startOffset);
        byte[] buffer = new byte[SEARCH_BUFFER_SIZE];
        int bytesRead = input.read(buffer);

        boolean found = false;
        int bufferOffset;
        for (bufferOffset = bytesRead - TOKEN_startxref.length; bufferOffset > 0; bufferOffset--) {
            for (int j = 0; j < TOKEN_startxref.length; j++) {
                if (buffer[bufferOffset + j] == TOKEN_startxref[j]) {
                    found = true;
                } else {
                    found = false;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        if (found) {
            long startXRefOffset = startOffset + bufferOffset;
            input.seek(startXRefOffset);
            return parseStartXRef(input);
        }
        COSLoadError e =
                new COSLoadError("no startxref found in the last " + SEARCH_BUFFER_SIZE + " bytes of the document");
        handleError(e);
        return -1;
    }

    /**
     * @param input
     * @return Returns the offset of the dictionary with linearization
     * parameters if any.
     * @throws IOException
     * @throws COSLoadException
     * @deprecated Don't use this anymore
     * <p>
     * Returns the offset of the dictionary with linearization
     * parameters if any. Returns -1 otherwise.
     */
    @Deprecated
    public int searchLinearized(IRandomAccess input) throws IOException, COSLoadException {
        long oldOffset = input.getOffset();
        int result = -1;
        input.seek(0);
        parseComment(input); // file header

        int next = input.read();
        while (true) {
            if (next == -1) {
                break;
            }
            if ((next == ' ') || isWhitespace(next)) { // performance shortcut
                next = input.read();
                continue;
            }
            input.seekBy(-1);
            if (isDigit(next)) {
                result = (int) input.getOffset();
                COSDocumentElement cosobj = parseIndirectObject(input, null);
                COSName linearized = COSName.constant("Linearized");
                if (cosobj instanceof COSDictionary && ((COSDictionary) cosobj).containsKey(linearized)) {
                    return result;
                }
                result = -1;
                break;
            } else if (next == '%') {
                parseComment(input); // this is the binary comment
            } else {
                break;
            }
            next = input.read();
        }

        // reset randomaccess
        input.seek(oldOffset);
        return result;
    }
}
