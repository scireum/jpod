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
package de.intarsys.pdf.content;

import java.io.IOException;
import java.util.Iterator;

import de.intarsys.pdf.content.common.CSCreator;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.filter.Filter;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.parser.CSContentParser;
import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;

/**
 * Represents the tokenized content of a PDF rendering program which is called a
 * "content stream".
 * 
 * <p>
 * A PDF rendering program is a sequence of operations, each build by a list of
 * operands followed by the operator.
 * </p>
 * 
 * <p>
 * Any visual appearance in a PDF document is build on a content stream. For
 * example a PDPage hosts a content stream (or a list of content streams) to
 * define its appearance.
 * </p>
 * 
 * <p>
 * A content stream has no access to indirect objects, this means object
 * references are not valid operands for operations. Complex objects are used
 * within a content stream via an indirection defined in a resource dictionary.
 * </p>
 * <p>
 * The {@link CSContent} itself is decoupled from its source of creation, this
 * means after manipulating the content stream you have to apply it where you
 * want to (for example adding it to a page).
 * <p>
 * You can work on the content stream directly, adding and removing operations.
 * A more elegant way to manipulate this is using a {@link CSCreator}, providing
 * high level methods for the different content stream operations.
 */
public class CSContent {
	/**
	 * Create {@link CSContent} from a byte array containing a PDF content
	 * stream.
	 * 
	 * @param data
	 *            The bytes defining the PDF content stream.
	 * 
	 * @return The new {@link CSContent}
	 */
	static public CSContent createFromBytes(byte[] data) {
		try {
			CSContentParser parser = new CSContentParser();
			return parser.parseStream(data);
		} catch (IOException e) {
			throw new COSRuntimeException(e);
		} catch (COSLoadException e) {
			throw new COSRuntimeException(e);
		}
	}

	/**
	 * Create {@link CSContent} from an array of {@link COSStream}, together
	 * defining a PDF content stream.
	 * 
	 * @param streams
	 *            An array of {@link COSStream} objects containing each a chunk
	 *            of the content stream.
	 * @return The new {@link CSContent}.
	 */
	static public CSContent createFromCos(COSArray streams) {
		RandomAccessByteArray data = new RandomAccessByteArray(null);
		try {
			for (Iterator it = streams.iterator(); it.hasNext();) {
				COSStream stream = ((COSObject) it.next()).asStream();
				if (stream != null) {
					data.write(stream.getDecodedBytes());
					// force at least a single space between streams
					data.write(32);
				}
			}
			CSContentParser parser = new CSContentParser();
			data.seek(0);
			return parser.parseStream(data);
		} catch (IOException e) {
			throw new COSRuntimeException(e);
		} catch (COSLoadException e) {
			throw new COSRuntimeException(e);
		}
	}

	/**
	 * Create {@link CSContent} from a {@link COSStream} containing a PDF
	 * content stream.
	 * 
	 * @param stream
	 *            The stream defining containing the PDF content stream.
	 * 
	 * @return The new {@link CSContent}
	 */
	static public CSContent createFromCos(COSStream stream) {
		return createFromBytes(stream.getDecodedBytes());
	}

	/**
	 * Create a new {@link CSContent}.
	 * 
	 * @return The new {@link CSContent}.
	 */
	static public CSContent createNew() {
		CSContent result = new CSContent();
		return result;
	}

	/**
	 * the tokenized elements of the content stream. The elements of this list
	 * are COSStreamOperation objects.
	 */
	private CSOperation[] operations = new CSOperation[100];

	private int size = 0;

	/**
	 * Create a new PDCContentStream.
	 *
	 * @param resourceDict
	 *            The dictionary defining the external references of the content
	 *            stream.
	 */
	protected CSContent() {
		super();
	}

	/**
	 * Add "content" at the end of the "marked content" portion in the content
	 * stream of this.
	 * 
	 * @param mark
	 *            The type of marked content we search
	 * @param content
	 *            The content we want to use.
	 */
	public void addMarkedContent(COSName mark, byte[] content) {
		int i = 0;
		for (; i < size; i++) {
			CSOperation operation = operations[i];
			if (operation.isOpBeginMarkedContent(mark)) {
				i++;
				break;
			}
		}
		if (i < size) {
			// found
			int nesting = 0;
			for (i++; i < size; i++) {
				CSOperation operation = operations[i];
				if (operation.isOpBeginMarkedContent(null)) {
					nesting++;
				}
				if (operation.isOpEndMarkedContent()) {
					if (nesting == 0) {
						addOperation(i, new CSLiteral(content));
						break;
					}
					nesting--;
				}
			}
		} else {
			CSOperation operation;
			operation = new CSOperation(CSOperators.CSO_BMC);
			operation.addOperand(CSOperation.OPERAND_Tx);
			addOperation(operation);
			addOperation(new CSLiteral(content));
			operation = new CSOperation(CSOperators.CSO_EMC);
			addOperation(operation);
		}
	}

	/**
	 * Add another operation to the rendering program.
	 * 
	 * @param op
	 *            The new operation to append.
	 */
	public void addOperation(CSOperation op) {
		if (size >= operations.length) {
			CSOperation[] newOperations = new CSOperation[size * 2];
			System.arraycopy(operations, 0, newOperations, 0, size);
			operations = newOperations;
		}
		operations[size++] = op;
	}

	/**
	 * Add another operation to the rendering program.
	 * 
	 * @param op
	 *            The new operation to append.
	 */
	public void addOperation(int index, CSOperation op) {
		if (size >= operations.length) {
			CSOperation[] newOperations = new CSOperation[size * 2];
			System.arraycopy(operations, 0, newOperations, 0, size);
			operations = newOperations;
		}
		System.arraycopy(operations, index, operations, index + 1, size - index);
		size++;
		operations[index] = op;
	}

	public COSStream createStream() {
		COSStream result = COSStream.create(null);
		result.setDecodedBytes(toByteArray());
		return result;
	}

	public COSStream createStreamFlate() {
		COSStream result = COSStream.create(null);
		byte[] bytes = toByteArray();
		result.setDecodedBytes(bytes);
		if (bytes.length > 10) {
			// deflate large streams
			result.addFilter(Filter.CN_Filter_FlateDecode);
		}
		return result;
	}

	/**
	 * remove last operation from the rendering program.
	 * 
	 * @return the last operation, or null of no operations left
	 */
	public CSOperation getLastOperation() {
		if (size == 0) {
			return null;
		}
		return operations[size - 1];
	}

	public CSOperation getOperation(int index) {
		return operations[index];
	}

	public CSOperation[] getOperations() {
		CSOperation[] copy = new CSOperation[size];
		System.arraycopy(operations, 0, copy, 0, size());
		return copy;
	}

	/**
	 * remove last operation from the rendering program.
	 */
	public void removeLastOperation() {
		removeOperation(size - 1);
	}

	public void removeOperation(int index) {
		System.arraycopy(operations, index + 1, operations, index, size - index
				- 1);
		size--;
	}

	/**
	 * Set the "marked content" portion in the content stream of this. Marked
	 * content is enclosed between "BMC" and "EMC", the begin operation has an
	 * operand identifying the type of marked content.
	 * 
	 * <p>
	 * The portion between the marks is replaced with <code>content</code>.If no
	 * marks are found, the new content is appended as a marked content section.
	 * </p>
	 * 
	 * @param mark
	 *            The type of marked content we search
	 * @param content
	 *            The content we want to use.
	 */
	public void setMarkedContent(COSName mark, byte[] content) {
		int i = 0;
		for (; i < size; i++) {
			CSOperation operation = operations[i];
			if (operation.isOpBeginMarkedContent(mark)) {
				i++;
				break;
			}
		}
		if (i < size) {
			// found
			addOperation(i, new CSLiteral(content));
			int nesting = 0;
			for (i++; i < size;) {
				CSOperation operation = operations[i];
				if (operation.isOpBeginMarkedContent(null)) {
					nesting++;
				}
				if (operation.isOpEndMarkedContent()) {
					if (nesting == 0) {
						break;
					}
					nesting--;
				}
				removeOperation(i);
			}
		} else {
			CSOperation operation;
			operation = new CSOperation(CSOperators.CSO_BMC);
			operation.addOperand(CSOperation.OPERAND_Tx);
			addOperation(operation);
			addOperation(new CSLiteral(content));
			operation = new CSOperation(CSOperators.CSO_EMC);
			addOperation(operation);
		}
	}

	/**
	 * The number of operations in the content stream.
	 * 
	 * @return The number of operations in the content stream.
	 */
	public int size() {
		return size;
	}

	/**
	 * Create the byte representation from the list of operations.
	 * 
	 * @return The byte representation from the list of operations.
	 */
	public byte[] toByteArray() {
		byte[] buffer = new byte[size() * 10];
		RandomAccessByteArray randomAccess = new RandomAccessByteArray(buffer, 0);
		COSWriter writer = new COSWriter(randomAccess, null);
		try {
			writer.writeContentStream(this);
		} catch (IOException e) {
			// this should not happen
		}
		return randomAccess.toByteArray();
	}

	@Override
	public String toString() {
		return new String(toByteArray());
	}
}
