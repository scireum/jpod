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
package de.intarsys.pdf.writer;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.content.CSOperators;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBoolean;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocumentElement;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectProxy;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.cos.COSVisitorException;
import de.intarsys.pdf.cos.ICOSObjectVisitor;
import de.intarsys.pdf.cos.ICOSProxyVisitor;
import de.intarsys.pdf.crypt.COSSecurityException;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.parser.PDFParser;
import de.intarsys.pdf.st.AbstractXRefWriter;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.pdf.st.STXRefEntryOccupied;
import de.intarsys.pdf.st.STXRefSection;
import de.intarsys.tools.hex.HexTools;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.randomaccess.RandomAccessByteArray;
import de.intarsys.tools.string.StringTools;

/**
 * A writer for PDF related data structures.
 */
public class COSWriter implements ICOSObjectVisitor, ICOSProxyVisitor {
	public static final byte[] ARRAY_CLOSE = "]".getBytes(); //$NON-NLS-1$

	public static final byte[] ARRAY_OPEN = "[".getBytes(); //$NON-NLS-1$

	public static final byte[] COMMENT = "%".getBytes(); //$NON-NLS-1$

	/*
	 * todo 1 @mit break up streams longer than allowed line (255 chars, pp67)
	 */

	/** To be used when 2 byte sequence is enforced. */
	public static final byte[] CRLF = "\r\n".getBytes(); //$NON-NLS-1$

	public static final byte[] DICT_CLOSE = ">>".getBytes(); //$NON-NLS-1$

	public static final byte[] DICT_OPEN = "<<".getBytes(); //$NON-NLS-1$

	/** a fast lookup for serializing digits */
	protected static final char[] DIGITS = new char[] { '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '9' };

	public static final byte[] ENDOBJ = "endobj".getBytes(); //$NON-NLS-1$

	public static final byte[] ENDSTREAM = "endstream".getBytes(); //$NON-NLS-1$

	public static final byte[] EOF = "%%EOF".getBytes(); //$NON-NLS-1$

	/** standard line separator on this platform */
	public static final byte[] EOL = System.getProperty("line.separator") //$NON-NLS-1$
			.getBytes();

	public static final byte[] FALSE = "false".getBytes(); //$NON-NLS-1$

	public static final byte[] GARBAGE = new byte[] { (byte) 0xF6, (byte) 0xE4,
			(byte) 0xFC, (byte) 0xDF };

	/** Line feed character. */
	public static final byte[] LF = "\n".getBytes(); //$NON-NLS-1$

	public static final byte[] LITERAL_ESCAPED_BS = "\\b".getBytes(); //$NON-NLS-1$

	public static final byte[] LITERAL_ESCAPED_CR = "\\r".getBytes(); //$NON-NLS-1$

	public static final byte[] LITERAL_ESCAPED_FF = "\\f".getBytes(); //$NON-NLS-1$

	public static final byte[] LITERAL_ESCAPED_HT = "\\t".getBytes(); //$NON-NLS-1$

	public static final byte[] LITERAL_ESCAPED_LF = "\\n".getBytes(); //$NON-NLS-1$

	public static final byte[] NAME_ESCAPE = "#".getBytes(); //$NON-NLS-1$

	public static final byte[] NAME_PREFIX = "/".getBytes(); //$NON-NLS-1$

	public static final byte[] NULL = "null".getBytes(); //$NON-NLS-1$

	public static final byte[] OBJ = "obj".getBytes(); //$NON-NLS-1$

	public static final byte[] PDF_ESCAPE = "\\".getBytes(); //$NON-NLS-1$

	public static final byte[] REFERENCE = "R".getBytes(); //$NON-NLS-1$

	public static final byte[] SPACE = " ".getBytes(); //$NON-NLS-1$

	public static final byte[] STREAM = "stream".getBytes(); //$NON-NLS-1$

	public static final byte[] STRING_CLOSE = ")".getBytes(); //$NON-NLS-1$

	public static final byte[] STRING_HEX_CLOSE = ">".getBytes(); //$NON-NLS-1$

	public static final byte[] STRING_HEX_OPEN = "<".getBytes(); //$NON-NLS-1$

	public static final byte[] STRING_OPEN = "(".getBytes(); //$NON-NLS-1$

	public static final byte[] TRAILER = "trailer".getBytes(); //$NON-NLS-1$

	public static final byte[] TRUE = "true".getBytes(); //$NON-NLS-1$

	private static final NumberFormat formatFixed = NumberFormat.getIntegerInstance(Locale.US);

	synchronized public static void basicWriteFixed(IRandomAccess randomAccess,
			float value, int precision) throws IOException {
		formatFixed.setGroupingUsed(false);
		formatFixed.setMaximumFractionDigits(precision);
		randomAccess.write(StringTools.toByteArray(formatFixed.format(value)));
	}

	public static void basicWriteInteger(IRandomAccess randomAccess, int value)
			throws IOException {
		randomAccess.write(StringTools.toByteArray(Integer.toString(value)));
	}

	/**
	 * create the byte stream for the representation of a name
	 * 
	 * @param randomAccess
	 *            the randomAccessData to write to
	 * @param name
	 *            the names byte stream
	 * 
	 * @throws IOException
	 */
	public static void basicWriteName(IRandomAccess randomAccess, byte[] name)
			throws IOException {
		randomAccess.write(NAME_PREFIX);
		for (int i = 0; i < name.length; i++) {
			int current = name[i] & 0xff; // convert to unsigned byte
			if (current <= 32 || current >= 127	|| PDFParser.isDelimiter(current) || current == 35) {
				randomAccess.write(NAME_ESCAPE);
				randomAccess.write(HexTools.ByteToHex[current]);
			} else {
				randomAccess.write(current);
			}
		}
	}

	/**
	 * create a hex encoded byte stream representation of string
	 * 
	 * @param randomAccess
	 *            the randomAccessData to write to
	 * @param string
	 *            the string to write
	 * 
	 * @throws IOException
	 */
	public static void basicWriteStringHex(IRandomAccess randomAccess,
			byte[] string) throws IOException {
		randomAccess.write(STRING_HEX_OPEN);
		for (int i = 0; i < string.length; i++) {
			randomAccess.write(HexTools.ByteToHex[string[i] & 0xFF]);
		}
		randomAccess.write(STRING_HEX_CLOSE);
	}

	/**
	 * create the literal byte stream representation of string
	 * 
	 * @param randomAccess
	 *            the randomAccessData to write to
	 * @param string
	 *            the string to write
	 * 
	 * @throws IOException
	 */
	public static void basicWriteStringLiteral(IRandomAccess randomAccess,
			byte[] string) throws IOException {
		randomAccess.write(STRING_OPEN);
		for (int i = 0; i < string.length; i++) {
			int b = string[i];
			if (b == '\n') {
				randomAccess.write(LITERAL_ESCAPED_LF);
			} else if (b == '\r') {
				randomAccess.write(LITERAL_ESCAPED_CR);
			} else if (b == '\t') {
				randomAccess.write(LITERAL_ESCAPED_HT);
			} else if (b == '\f') {
				randomAccess.write(LITERAL_ESCAPED_FF);
			} else if (b == '\b') {
				randomAccess.write(LITERAL_ESCAPED_BS);
			} else if ((b == '(') || (b == ')') || (b == '\\')) {
				randomAccess.write(PDF_ESCAPE);
				randomAccess.write(b);
			} else {
				randomAccess.write(b);
			}
		}
		randomAccess.write(STRING_CLOSE);
	}

	/**
	 * Create a byte array representation from a COSObject.
	 * 
	 * @param object
	 *            The object to be serialized.
	 * 
	 * @return A byte array representation from a COSObject.
	 */
	public static final byte[] toByteArray(COSObject object) {
		RandomAccessByteArray tempRandom = new RandomAccessByteArray(null);
		COSWriter writer = new COSWriter(tempRandom, null);
		try {
			writer.writeObject(object);
		} catch (IOException e) {
			return "*** not printable ***".getBytes(); //$NON-NLS-1$
		}
		return tempRandom.toByteArray();
	}

	private COSIndirectObject currentObject;

	private final ISystemSecurityHandler securityHandler;

	private boolean incremental = true;

	private boolean autoUpdate = true;

	/** flag to prevent generating two newlines in sequence */
	private boolean onNewLine = false;

	private final List proxies = new ArrayList();

	/**
	 * The IRandomAccess we write to.
	 */
	private final IRandomAccess randomAccess;

	public COSWriter(IRandomAccess randomAccess,
			ISystemSecurityHandler securityHandler) {
		this.securityHandler = securityHandler;
		this.randomAccess = randomAccess;
	}

	protected void basicWriteDocument(STDocument doc) throws IOException {
		if (doc.isDirty() && isAutoUpdate()) {
			doc.updateModificationDate();
			doc.getTrailer().updateFileID();
		}
		if (doc.isNew()) {
			// force complete write on new document
			setIncremental(false);
		}
		if (doc.isClosed()) {
			return;
		}
		if (!isIncremental()) {
			doc.garbageCollect();
		} else {
			doc.incrementalGarbageCollect();
		}
		synchronized (doc.getAccessLock()) {
			if (!isIncremental()) {
				getRandomAccess().setLength(0);
				writeHeader(doc);
			}
			Collection changes = doc.getChanges();
			if (changes.size() > 0) {
				seekToEnd();
				STXRefSection xrefSection = doc.createNewXRefSection();
				if (getSecurityHandler() != null) {
					getSecurityHandler()
							.updateTrailer(xrefSection.cosGetDict());
				}
				for (Iterator it = changes.iterator(); it.hasNext();) {
					COSIndirectObject object = (COSIndirectObject) it.next();
					writeEntry(xrefSection, object);
					object.setDirty(false);
				}
				seekToEnd();
				writeXRef(xrefSection);
				writeEOF();
				doc.setXRefSection(xrefSection);
				doc.setDirty(false);
			}
			for (Iterator it = getProxies().iterator(); it.hasNext();) {
				COSObjectProxy proxy = (COSObjectProxy) it.next();
				proxy.ended(this);
			}
			getRandomAccess().flush();
		}
	}

	protected void close(STDocument doc) throws IOException {
		// todo 1 change dirty
	}

	protected byte[] encryptStream(COSDictionary dict, byte[] bytes)
			throws IOException {
		if (getSecurityHandler() != null && getCurrentObject() != null) {
			try {
				return getSecurityHandler().encryptStream(
						getCurrentObject().getKey(), dict, bytes);
			} catch (COSSecurityException e) {
				IOException ioe = new IOException("error encrypting data"); //$NON-NLS-1$
				ioe.initCause(e);
				throw ioe;
			}
		}
		return bytes;
	}

	protected byte[] encryptString(COSString obj) throws IOException {
		byte[] bytes = obj.byteValue();
		if (getSecurityHandler() != null && getCurrentObject() != null) {
			try {
				return getSecurityHandler().encryptString(
						getCurrentObject().getKey(), bytes);
			} catch (COSSecurityException e) {
				IOException ioe = new IOException("error encrypting data"); //$NON-NLS-1$
				ioe.initCause(e);
				throw ioe;
			}
		}
		return bytes;
	}

	protected COSIndirectObject getCurrentObject() {
		return currentObject;
	}

	/**
	 * The collection of proxies to COSObjects visited by the writer.
	 * 
	 * @return The collection of proxies to COSObjects visited by the writer.
	 */
	public List getProxies() {
		return proxies;
	}

	public IRandomAccess getRandomAccess() {
		return randomAccess;
	}

	protected ISystemSecurityHandler getSecurityHandler() {
		return securityHandler;
	}

	/**
	 * When auto update is true, the {@link COSWriter} will automatically create
	 * new values for the file modification date in the info dictionary and the
	 * file id in the trailer. When false, these values are under client code
	 * control.
	 * 
	 * @return
	 */
	public boolean isAutoUpdate() {
		return autoUpdate;
	}

	public boolean isIncremental() {
		return incremental;
	}

	/**
	 * This will tell if we are on a new line.
	 * 
	 * @return true If we are on a new line.
	 */
	protected boolean isOnNewLine() {
		return onNewLine;
	}

	protected void reset() {
		onNewLine = false;
	}

	public void seekToEnd() throws IOException {
		randomAccess.seek(randomAccess.getLength());
		reset();
	}

	public void setAutoUpdate(boolean autoUpdate) {
		this.autoUpdate = autoUpdate;
	}

	protected void setCurrentObject(COSIndirectObject currentObject) {
		this.currentObject = currentObject;
	}

	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}

	/**
	 * visitFromArray.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromArray(COSArray obj) throws COSVisitorException {
		try {
			if (getSecurityHandler() != null) {
				getSecurityHandler().pushContextObject(obj);
			}
			int count = 0;
			write(ARRAY_OPEN);
			for (Iterator i = obj.basicIterator(); i.hasNext();) {
				COSDocumentElement current = (COSDocumentElement) i.next();
				current.accept(this);
				count++;
				if (i.hasNext()) {
					if ((count % 10) == 0) {
						writeEOL();
					} else {
						write(SPACE);
					}
				}
			}
			write(ARRAY_CLOSE);
			writeEOL();
		} catch (IOException e) {
			throw new COSVisitorException(e);
		} finally {
			if (getSecurityHandler() != null) {
				getSecurityHandler().popContextObject();
			}
		}
		return null;
	}

	/**
	 * visitFromBoolean.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromBoolean(COSBoolean obj) throws COSVisitorException {
		try {
			if (obj.booleanValue()) {
				write(TRUE);
			} else {
				write(FALSE);
			}
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * visitFromDictionary.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromDictionary(COSDictionary obj)
			throws COSVisitorException {
		try {
			if (getSecurityHandler() != null) {
				getSecurityHandler().pushContextObject(obj);
			}
			write(DICT_OPEN);
			writeEOL();
			for (Iterator i = obj.basicEntryIterator(); i.hasNext();) {
				Map.Entry entry = (Map.Entry) i.next();
				COSName name = (COSName) entry.getKey();
				COSDocumentElement current = (COSDocumentElement) entry
						.getValue();
				if (current != null) {
					// this is purely defensive, if entry is set to null instead
					// of removed
					basicWriteName(randomAccess, name.byteValue());
					write(SPACE);
					current.accept(this);
					writeEOL();
				}
			}
			write(DICT_CLOSE);
			writeEOL();
		} catch (IOException e) {
			throw new COSVisitorException(e);
		} finally {
			if (getSecurityHandler() != null) {
				getSecurityHandler().popContextObject();
			}
		}
		return null;
	}

	/**
	 * visitFromFixed.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromFixed(COSFixed obj) throws COSVisitorException {
		try {
			basicWriteFixed(randomAccess, obj.floatValue(), obj.getPrecision());
			onNewLine = false;
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * This will write an indirect object reference
	 * 
	 * @param obj
	 *            The indirect object to write.
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromIndirectObject(COSIndirectObject obj)
			throws COSVisitorException {
		reset();
		try {
			write(StringTools.toByteArray(Integer.toString(obj
					.getObjectNumber())));
			write(SPACE);
			write(StringTools.toByteArray(Integer.toString(obj
					.getGenerationNumber())));
			write(SPACE);
			write(REFERENCE);
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * visitFromInteger.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromInteger(COSInteger obj) throws COSVisitorException {
		try {
			basicWriteInteger(randomAccess, obj.intValue());
			onNewLine = false;
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * visitFromName.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromName(COSName obj) throws COSVisitorException {
		try {
			basicWriteName(randomAccess, obj.byteValue());
			onNewLine = false;
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * visitFromNull.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromNull(COSNull obj) throws COSVisitorException {
		try {
			write(NULL);
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	@Override
	public Object visitFromProxy(COSObjectProxy obj) throws COSVisitorException {
		try {
			obj.setPosition(getRandomAccess().getOffset());
			// move forward, don't initialize
			randomAccess.seekBy(obj.getLength());
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		proxies.add(obj);
		return null;
	}

	/**
	 * visitFromStream.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromStream(COSStream obj) throws COSVisitorException {
		try {
			if (getSecurityHandler() != null) {
				getSecurityHandler().pushContextObject(obj);
			}
			int length;
			byte[] bytes = new byte[0];
			if (!obj.isExternal()) {
				// only standard (internal) streams have a writable byte content
				bytes = obj.getEncodedBytes();
			}
			// MUST encrypt before dict is written - length may be changed
			byte[] encrypted = encryptStream(obj.getDict(), bytes);
			// when writing out stream, /Length will determine the number of
			// bytes to be decrypted, use this value
			length = encrypted == null ? 0 : encrypted.length;
			obj.getDict().basicPutSilent(COSStream.DK_Length,
					COSInteger.create(length));
			// stream dictionaries are not indirect
			obj.getDict().accept(this);
			// revert to encoded bytes length available in object memory...
			length = bytes == null ? 0 : bytes.length;
			obj.getDict().basicPutSilent(COSStream.DK_Length,
					COSInteger.create(length));
			writeCRLF();
			// write the stream content
			writeStreamContent(encrypted);
		} catch (IOException e) {
			throw new COSVisitorException(e);
		} finally {
			if (getSecurityHandler() != null) {
				getSecurityHandler().popContextObject();
			}
		}
		return null;
	}

	/**
	 * visitFromString.
	 * 
	 * @param obj
	 *            The object that is being visited.
	 * 
	 * @return unused
	 * 
	 * @throws COSVisitorException
	 *             If there is an exception while visiting this object.
	 */
	@Override
	public Object visitFromString(COSString obj) throws COSVisitorException {
		try {
			if (obj.isHexMode()) {
				writeStringHex(encryptString(obj));
			} else {
				writeStringLiteral(encryptString(obj));
			}
			onNewLine = false;
		} catch (IOException e) {
			throw new COSVisitorException(e);
		}
		return null;
	}

	/**
	 * This will write some byte to the stream.
	 * 
	 * @param b
	 *            The source byte array.
	 * 
	 * @throws IOException
	 *             If the underlying stream throws an exception.
	 */
	public void write(byte[] b) throws IOException {
		onNewLine = false;
		randomAccess.write(b);
	}

	/**
	 * This will write some byte to the stream.
	 * 
	 * @param b
	 *            The source byte array.
	 * @param off
	 *            The offset into the array to start writing.
	 * @param len
	 *            The number of bytes to write.
	 * 
	 * @throws IOException
	 *             If the underlying stream throws an exception.
	 */
	public void write(byte[] b, int off, int len) throws IOException {
		onNewLine = false;
		randomAccess.write(b, off, len);
	}

	/**
	 * This will write a single byte to the stream.
	 * 
	 * @param b
	 *            The byte to write to the stream.
	 * 
	 * @throws IOException
	 *             If there is an error writing to the underlying stream.
	 */
	public void write(int b) throws IOException {
		onNewLine = false;
		randomAccess.write(b);
	}

	public void writeContentStream(CSContent contentStream) throws IOException {
		int len = contentStream.size();
		for (int i = 0; i < len; i++) {
			CSOperation operation = contentStream.getOperation(i);
			try {
				writeOperation(operation);
			} catch (COSVisitorException e) {
				IOException ioe = new IOException(e.getMessage());
				ioe.initCause(e);
				throw ioe;
			}
			write(' ');
		}
	}

	/**
	 * This will write a CRLF to the stream
	 * 
	 * @throws IOException
	 *             If there is an error writing the data to the stream.
	 */
	protected void writeCRLF() throws IOException {
		randomAccess.write(CRLF);
	}

	public void writeDocument(STDocument doc) throws IOException {
		basicWriteDocument(doc);
		close(doc);
	}

	protected void writeEntry(STXRefSection xrefSection,
			COSIndirectObject object) throws IOException {
		STXRefEntryOccupied entry = new STXRefEntryOccupied(
				object.getObjectNumber(), object.getGenerationNumber(),
				getRandomAccess().getOffset());
		xrefSection.addEntry(entry);
		writeIndirectObject(object);
	}

	protected void writeEOF() throws IOException {
		write(COSWriter.EOF);
		writeEOL();
	}

	/**
	 * This will write an EOL to the stream.
	 * 
	 * @throws IOException
	 *             If there is an error writing to the stream
	 */
	public void writeEOL() throws IOException {
		if (!isOnNewLine()) {
			randomAccess.write(EOL);
			onNewLine = true;
		}
	}

	/**
	 * This will write the header to the PDF document.
	 * 
	 * @throws IOException
	 *             If there is an error writing to the stream.
	 */
	protected void writeHeader(STDocument stdoc) throws IOException {
		write(COMMENT);
		write(stdoc.getVersion().getBytes());
		writeEOL();
		write(COMMENT);
		write(GARBAGE);
		writeEOL();
	}

	protected void writeImageData(COSObject imageData) throws IOException {
		if (imageData instanceof COSString) {
			randomAccess.write(((COSString) imageData).byteValue());
		} else if (imageData instanceof COSStream) {
			randomAccess.write(((COSStream) imageData).getDecodedBytes());
		} else if (imageData instanceof COSName) {
			randomAccess.write(((COSName) imageData).byteValue());
		} else {
			// ?
		}
		randomAccess.write('\n');
	}

	public void writeIndirectObject(COSIndirectObject obj) throws IOException {
		setCurrentObject(obj);
		reset();
		write(StringTools.toByteArray(Integer.toString(obj.getObjectNumber())));
		write(SPACE);
		write(StringTools.toByteArray(Integer.toString(obj
				.getGenerationNumber())));
		write(SPACE);
		write(OBJ);
		writeEOL();
		try {
			obj.dereference().accept(this);
		} catch (COSVisitorException e) {
			Throwable cause = (e.getCause() == null) ? e : e.getCause();
			IOException ioe = new IOException(cause.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
		writeEOL();
		write(ENDOBJ);
		writeEOL();
		setCurrentObject(null);
	}

	/**
	 * This will write a cos object to the stream
	 * 
	 * @param object
	 *            the object to write
	 * 
	 * @throws IOException
	 *             If an error occurs while generating the data.
	 */
	public void writeObject(COSObject object) throws IOException {
		try {
			object.accept(this);
		} catch (COSVisitorException e) {
			IOException ioe = new IOException(e.getMessage());
			ioe.initCause(e);
			throw ioe;
		}
	}

	protected void writeOperation(CSOperation obj) throws COSVisitorException,
			IOException {
		if (obj.matchesOperator(CSOperators.CSO_EI) && obj.operandSize() == 1) {
			writeImageData(obj.getOperand(0));
		} else {
			for (Iterator i = obj.getOperands(); i.hasNext();) {
				COSObject operand = (COSObject) i.next();
				operand.accept(this);
				write(' ');
			}
		}
		write(obj.getOperatorToken());
	}

	protected void writeStreamContent(byte[] bytes) throws IOException {
		write(STREAM);
		writeCRLF();
		if (bytes != null) {
			write(bytes);
		}
		writeCRLF();
		write(ENDSTREAM);
		writeEOL();
	}

	protected void writeStringHex(byte[] bytes) throws IOException {
		basicWriteStringHex(randomAccess, bytes);
	}

	protected void writeStringLiteral(byte[] bytes) throws IOException {
		basicWriteStringLiteral(randomAccess, bytes);
	}

	protected void writeXRef(STXRefSection xrefSection) throws IOException {
		AbstractXRefWriter xrefWriter = xrefSection.getWriter(this);
		xrefWriter.writeXRef(xrefSection);
	}
}
