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

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * A parser for .pdf type content streams.
 */
public class CSContentParser extends PDFParser {
    private static final COSObject[] EMPTY_OPERANDS = new COSObject[0];

    /**
     * This is an hopefully temporary workaround for identifying valid
     * operations. It is only used when parsing inline images to make some
     * assumptions about the image data end.
     *
     * @param operation the operation to check
     * @return Answer {@code true} if the operation is valid.
     */
    protected static boolean accepts(CSOperation operation) {
        byte[] token = operation.getOperatorToken();
        switch (token[0]) {
            case 'q':
                return token.length == 1;
            case 'Q':
                return token.length == 1;
            case 'T':
                switch (token[1]) {
                    case 'j':
                        return token.length == 2;
                    case 'J':
                        return token.length == 2;
                    case 'f':
                        return token.length == 2;
                    case 'd':
                        return token.length == 2;
                    case 'L':
                        return token.length == 2;
                    case 'D':
                        return token.length == 2;
                    case 'c':
                        return token.length == 2;
                    case 'm':
                        return token.length == 2;
                    case 'r':
                        return token.length == 2;
                    case 's':
                        return token.length == 2;
                    case 'w':
                        return token.length == 2;
                    case 'z':
                        return token.length == 2;
                    case '*':
                        return token.length == 2;
                }
                break;
            case 'n':
                return token.length == 1;
            case 's':
                if (token.length == 1) {
                    return true;
                }
                switch (token[1]) {
                    case 'c':
                        if (token.length == 2) {
                            return true;
                        } else {
                            return token.length == 3;
                        }
                    case 'h':
                        return token.length == 2;
                }
                break;
            case 'g':
                if (token.length == 1) {
                    return true;
                } else {
                    return token.length == 2;
                }
            case 'r':
                switch (token[1]) {
                    case 'e':
                        return token.length == 2;
                    case 'g':
                        return token.length == 2;
                    case 'i':
                        return token.length == 2;
                }
                break;
            case 'R':
                return token.length == 2;
            case 'm':
                return token.length == 1;
            case 'l':
                return token.length == 1;
            case 'f':
                if (token.length == 1) {
                    return true;
                } else {
                    return token.length == 2;
                }
            case 'B':
                if (token.length == 1) {
                    return true;
                }
                switch (token[1]) {
                    case '*':
                        return token.length == 2;
                    case 'T':
                        return token.length == 2;
                    case 'M':
                        return token.length == 3;
                    case 'D':
                        return token.length == 3;
                    case 'I':
                        return token.length == 2;
                    case 'X':
                        return token.length == 2;
                }
                break;
            case 'b':
                if (token.length == 1) {
                    return true;
                } else {
                    return token.length == 2;
                }

            case 'S':
                if (token.length == 1) {
                    return true;
                } else {
                    if (token.length == 2) {
                        return true;
                    } else {
                        return token.length == 3;
                    }
                }
            case 'h':
                return token.length == 1;
            case 'W':
                if (token.length == 1) {
                    return true;
                } else {
                    return token.length == 2;
                }
            case 'c':
                if (token.length == 1) {
                    return true;
                }
                switch (token[1]) {
                    case 'm':
                        return token.length == 2;
                    case 's':
                        return token.length == 2;
                }
                break;
            case 'E':
                switch (token[1]) {
                    case 'T':
                        return token.length == 2;
                    case 'M':
                        return token.length == 3;
                    case 'I':
                        return token.length == 2;
                    case 'X':
                        return token.length == 2;
                }
                break;
            case 'G':
                return token.length == 1;
            case '\'':
                return token.length == 1;
            case '"':
                return token.length == 1;
            case 'C':
                return token.length == 2;
            case 'd':
                if (token.length == 1) {
                    return true;
                }
                switch (token[1]) {
                    case '0':
                        return token.length == 2;
                    case '1':
                        return token.length == 2;
                }
                break;
            case 'D':
                switch (token[1]) {
                    case 'o':
                        return token.length == 2;
                    case 'P':
                        return token.length == 2;
                }
                break;
            case 'F':
                return token.length == 1;
            case 'i':
                return token.length == 1;
            case 'I':
                return token.length == 2;
            case 'j':
                return token.length == 1;
            case 'J':
                return token.length == 1;
            case 'K':
                return token.length == 1;
            case 'k':
                return token.length == 1;
            case 'M':
                if (token.length == 1) {
                    return true;
                } else {
                    return token.length == 2;
                }
            case 'v':
                return token.length == 1;
            case 'w':
                return token.length == 1;
            case 'y':
                return token.length == 1;
            default:
                return false;
        }
        return false;
    }

    private Object[] operands = new Object[10];

    private int size = 0;

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.parser.PDFParser#createObjectReference()
     */
    @Override
    protected COSIndirectObject createObjectReference(IRandomAccess input) throws IOException, COSLoadException {
        COSLoadError e =
                new COSLoadError("indirect objects not allowed in streams at character index " + input.getOffset());
        handleError(e);
        return null;
    }

    protected void parseImageData(IRandomAccess input, COSStream cosStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int next = input.read();
        // no more skipping
        // the space after ID is already consumed!
        // see test doc "CCITTFax G4 inline 1"
        while (next != -1) {
            /*
             * spec is not clear but some internet articles claim that before
             * "EI" a line break is required. spaces and CRs have been seen in
             * real world documents; accept these and LF as possible end and
             * check if valid operation follows. treat any CR followed by a LF
             * as belonging to the image data, because this also has been seen
             * out there.
             */
            if ((next == '\n') || (next == '\r') || (next == ' ')) {
                // remember position
                long mark = input.getOffset();
                try {
                    int tempNext = input.read();
                    if (tempNext == 'E') {
                        tempNext = input.read();
                        if (tempNext == 'I') {
                            // is this followed by a valid operation?
                            CSOperation tempOperation = parseOperation(input);
                            if (tempOperation == null || CSContentParser.accepts(tempOperation)) {
                                // exit image parsing
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    // ignore, parse on image data
                } finally {
                    input.seek(mark);
                }
            }
            bos.write(next);
            next = input.read();
        }
        cosStream.setEncodedBytes(bos.toByteArray());
    }

    /**
     * parse a valid COS object for use in stream context from the current
     * stream position see PDF Reference v1.4, chapter 3.7.1 Content Streams
     *
     * @param input The stream content object that defines the context of the stream.
     * @return The stream operation parsed.
     * @throws IOException
     * @throws COSLoadException
     */
    protected CSOperation parseOperation(IRandomAccess input) throws IOException, COSLoadException {
        do {
            Object element = parseElement(input);
            if (element instanceof byte[]) {
                COSObject[] copyOperands;

                // speed
                if (size == 0) {
                    copyOperands = EMPTY_OPERANDS;
                } else {
                    copyOperands = new COSObject[size];
                    System.arraycopy(operands, 0, copyOperands, 0, size);
                }
                size = 0;
                return new CSOperation((byte[]) element, copyOperands);
            } else if (element == null) {
                int next = input.read();
                // strange document contains a "Ctrl-D" in ToUnicode stream...
                if (next != -1 && next != 4) {
                    input.seekBy(-1);
                    COSLoadError e = new COSLoadError("unexpected char ("
                                                      + (char) next
                                                      + ") at character index "
                                                      + input.getOffset());
                    handleError(e);
                }
                size = 0;
                return null;
            } else {
                if (size >= operands.length) {
                    Object[] newOperands = new Object[size << 2];
                    System.arraycopy(operands, 0, newOperands, 0, size);
                    operands = newOperands;
                }
                operands[size++] = element;
            }
        } while (true);
    }

    protected CSOperation parseOperationEI(IRandomAccess input, COSDictionary parameters)
            throws IOException, COSLoadException {
        COSStream cosStream;
        Object element;
        CSOperation op;

        cosStream = COSStream.create(parameters);
        parseImageData(input, cosStream);
        operands[size++] = cosStream;
        element = parseElement(input);
        if (!(element instanceof byte[])) {
            COSLoadError e = new COSLoadError("EI expected at character index " + input.getOffset());
            handleError(e);
        }
        COSObject[] copy = new COSObject[size];
        System.arraycopy(operands, 0, copy, 0, size);
        op = new CSOperation((byte[]) element, copy);
        size = 0;
        return op;
    }

    /**
     * parse a content stream.
     * <p>
     * <p>
     * See PDF Reference v1.4, chapter 3.7 Content Streams
     * </p>
     *
     * @param data A byte array containing the encoded content stream
     * @return the parsed content
     * @throws IOException
     * @throws COSLoadException
     */
    public CSContent parseStream(byte[] data) throws IOException, COSLoadException {
        return parseStream(new RandomAccessByteArray(data));
    }

    /**
     * parse a content stream.
     * <p>
     * <p>
     * See PDF Reference v1.4, chapter 3.7 Content Streams
     * </p>
     *
     * @param input a open IRandomAccessData positioned at the beginning of the
     *              content stream
     * @return the parsed content
     * @throws IOException
     * @throws COSLoadException
     */
    public CSContent parseStream(IRandomAccess input) throws IOException, COSLoadException {
        CSContent streamContent = CSContent.createNew();
        while (true) {
            CSOperation op = parseOperation(input);
            if (op == null) {
                return streamContent;
            }
            byte[] operatorToken = op.getOperatorToken();
            if ((operatorToken.length == 2) && (operatorToken[0] == 'I') && (operatorToken[1] == 'D')) {
                COSDictionary parameters;

                // after the "ID" tag we expect image data, followed by "EI"
                parameters = COSDictionary.create();
                for (Iterator<COSObject> iterator = op.getOperands(); iterator.hasNext(); ) {
                    parameters.put(iterator.next().asName(), iterator.next().copyShallow());
                }
                op = parseOperationEI(input, parameters);
                if (op != null) {
                    streamContent.addOperation(op);
                }
            } else {
                streamContent.addOperation(op);
            }
        }
    }
}
