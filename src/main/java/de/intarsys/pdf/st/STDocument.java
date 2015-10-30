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
package de.intarsys.pdf.st;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSIndirectObject;
import de.intarsys.pdf.cos.COSInfoDict;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSNull;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectKey;
import de.intarsys.pdf.cos.COSObjectWalkerDeep;
import de.intarsys.pdf.cos.COSTrailer;
import de.intarsys.pdf.cos.COSVisitorException;
import de.intarsys.pdf.crypt.AccessPermissionsFull;
import de.intarsys.pdf.crypt.COSSecurityException;
import de.intarsys.pdf.crypt.IAccessPermissions;
import de.intarsys.pdf.crypt.IAccessPermissionsSupport;
import de.intarsys.pdf.crypt.ISecurityHandler;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.crypt.SystemSecurityHandler;
import de.intarsys.pdf.parser.COSDocumentParser;
import de.intarsys.pdf.parser.COSLoadError;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.pd.PDObject;
import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.EventDispatcher;
import de.intarsys.tools.event.EventType;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.event.INotificationSupport;
import de.intarsys.tools.exception.ExceptionTools;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;
import de.intarsys.tools.locator.TransientLocator;
import de.intarsys.tools.message.MessageBundle;
import de.intarsys.tools.randomaccess.BufferedRandomAccess;
import de.intarsys.tools.randomaccess.IRandomAccess;
import de.intarsys.tools.stream.StreamTools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The most physical abstraction of a PDF document. This object handles the
 * random access representation of the PDF file.
 * <p>
 * An STDocument manages the cross ref access to data stream positions from COS
 * level objects. As such the ST and the COS package are highly interdependent.
 */
public class STDocument implements INotificationSupport, IAttributeSupport, ILocatorSupport {
    public static final String ATTR_LOCATOR = "locator";

    public static final String ATTR_XREF_SECTION = "xRefSection";

    public static final String ATTR_SYSTEM_SECURITY_HANDLER = "systemSecurityHandler";

    /**
     * A counter for naming new documents
     */
    private static int COUNTER = 0;

    /**
     * our current fdf version number *
     */
    public static final STDocType DOCTYPE_FDF = new STDocType("FDF", "1.2"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * our current pdf version number *
     */
    public static final STDocType DOCTYPE_PDF = new STDocType("PDF", "1.4"); //$NON-NLS-1$ //$NON-NLS-2$

    /**
     * The logger to be used in this package
     */
    private static Logger Log = PACKAGE.Log;

    /**
     * NLS
     */
    private static final MessageBundle Msg = PACKAGE.Messages;

    public static final String OPTION_WRITEMODEHINT = "writeModeHint"; //$NON-NLS-1$

    /**
     * Create a new document representing the data referenced by locator.
     *
     * @param locator The locator to the documents data
     * @return A new document representing the data referenced by locator.
     * @throws IOException
     * @throws COSLoadException
     */
    public static STDocument createFromLocator(ILocator locator) throws IOException, COSLoadException {
        return createFromLocator(locator, null);
    }

    /**
     * Create a new document representing the data referenced by locator using
     * {@code options} to fine tune creation.
     * <p>
     * All options given are copied to the {@link STDocument} attributes and
     * accessible via the {@link IAttributeSupport} interface.
     *
     * @param locator The locator to the documents data
     * @param options A collection of options
     * @return A new document representing the data referenced by locator.
     * @throws IOException
     * @throws COSLoadException
     */
    public static STDocument createFromLocator(ILocator locator, Map options) throws IOException, COSLoadException {
        if (!locator.exists()) {
            throw new FileNotFoundException("'" + locator.getFullName() //$NON-NLS-1$
                                            + "' not found"); //$NON-NLS-1$
        }
        STDocument result = new STDocument(locator);
        if (options != null) {
            for (Iterator it = options.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry entry = (Map.Entry) it.next();
                result.setAttribute(entry.getKey(), entry.getValue());
            }
        }
        result.initializeFromLocator();
        return result;
    }

    protected static String createName(String typeName) {
        COUNTER++;
        return Msg.getString("STDocument.documentName.new", typeName, Integer.valueOf(COUNTER)); //$NON-NLS-1$
    }

    /**
     * create a new empty pdf document.
     *
     * @return A new empty pdf document
     */
    public static STDocument createNew() {
        return createNew(DOCTYPE_PDF);
    }

    /**
     * create a new empty document.
     *
     * @return A new empty document
     */
    public static STDocument createNew(STDocType docType) {
        STDocument doc = new STDocument();
        doc.initializeFromScratch(docType);
        return doc;
    }

    private EventDispatcher dispatcher = new EventDispatcher(this);

    private Object accessLock = new Object();

    /**
     * Generic attribute support
     */
	private final AttributeMap attributes = new AttributeMap();

    /**
     * The collection of changed objects within the document since last save
     */
	private final Set<COSIndirectObject> changes = new HashSet<COSIndirectObject>();

    private boolean closed = false;

    /**
     * Flag if this document is changed
     */
    private boolean dirty = false;

    private COSDocument doc;

    /**
     * The document's doc type.
     * <p>
     * <p>
     * This value is read from the file document header.
     * </p>
     */
    private STDocType docType;

    /**
     * The registered {@link COSIndirectObject} instances.
     */
    private COSIndirectObject[] objects = new COSIndirectObject[100];

    /**
     * The locator for the document physics
     */
    private ILocator locator;

    /**
     * The next free COSObjectKey to use for a new indirect object
     */
    private COSObjectKey nextKey;

    /**
     * The parser used for this document
     */
    private COSDocumentParser parser;

    /**
     * The random access stream to read the documents data
     */
    private IRandomAccess randomAccess;

    /**
     * The security handler used for decrypting this documents content
     */
    private ISystemSecurityHandler readSecurityHandler;

    private EnumWriteMode writeModeHint = (EnumWriteMode) EnumWriteMode.META.getDefault();

    /**
     * The security handler used for encrypting this documents content
     */
    private ISystemSecurityHandler writeSecurityHandler;

    /**
     * The most recent x reference section.
     * <p>
     * When a new document is created or initialized from a data stream, a new
     * empty XRef Section is always created for holding the changes to come.
     */
    private STXRefSection xRefSection;

    private boolean autoUpdate = true;

    /**
     * A new empty document.
     * <p>
     * Use always the factory method, this is not completely initialized.
     */
    protected STDocument() {
        //
    }

    /**
     * A new document bound to a locator.
     *
     * @param locator The locator to the documents data.
     */
    protected STDocument(ILocator locator) {
        setLocator(locator);
    }

    /**
     * Mark object as changed within this document.
     *
     * @param object The object that is new or changed
     */
    public void addChangedReference(COSIndirectObject object) {
        synchronized (changes) {
            setDirty(true);
            changes.add(object);
        }
    }

    @Override
    public void addNotificationListener(EventType type, INotificationListener listener) {
        dispatcher.addNotificationListener(type, listener);
    }

    /**
     * Add another indirect object to the document.
     *
     * @param newRef The new indirect object.
     */
    public void addObjectReference(COSIndirectObject newRef) {
        synchronized (objects) {
            int index = newRef.getObjectNumber();
            ensureLength(index);
            objects[index] = newRef;
        }
    }

    protected void checkConsistency() throws COSLoadError {
        if (getDocType() == null) {
            throw new COSLoadError("unknown document type"); //$NON-NLS-1$
        }
        if (getDocType().isPDF()) {
            if (getXRefSection() == null) {
                throw new COSLoadError("x ref section missing"); //$NON-NLS-1$
            }
            if (getXRefSection().cosGetDict() == null) {
                throw new COSLoadError("trailer missing"); //$NON-NLS-1$
            }
            // check catalog
            COSDictionary dict = getTrailer().getRoot().cosGetDict();
            if (dict == null || !COSCatalog.CN_Type_Catalog.equals(dict.get(PDObject.DK_Type))) {
                throw new COSLoadError("catalog invalid"); //$NON-NLS-1$
            }
        }
    }

    /**
     * Close the document. Accessing a documents content is undefined after
     * {@code close}.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        synchronized (getAccessLock()) {
            if (isClosed()) {
                return;
            }
            if (getRandomAccess() != null) {
                getRandomAccess().close();
                setClosed(true);
                setRandomAccess(null);
            }
        }
    }

    /**
     * Return a deep copy of the document. This will create a copy of the
     * documents content. The new documents location (random access) is
     * undefined. The objects will not preserve their key values.
     *
     * @return A deep copy of this.
     */
    public STDocument copyDeep() {
        STDocument result = STDocument.createNew();
        COSDictionary newTrailer = (COSDictionary) cosGetTrailer().copyDeep();
        newTrailer.remove(COSTrailer.DK_Prev);
        newTrailer.remove(COSTrailer.DK_Size);
        newTrailer.remove(STXRefSection.DK_XRefStm);
        ((STTrailerXRefSection) result.getXRefSection()).cosSetDict(newTrailer);
        result.readSecurityHandler = readSecurityHandler;
        result.writeSecurityHandler = writeSecurityHandler;
        String name = Msg.getString("STDocument.documentName.copyOf", getName()); //$NON-NLS-1$
        result.locator = new TransientLocator(name, getDocType().getTypeName());
        return result;
    }

    /**
     * The documents trailer dictionary
     *
     * @return The documents trailer dictionary
     */
    public COSDictionary cosGetTrailer() {
        return getXRefSection().cosGetDict();
    }

    public STXRefSection createNewXRefSection() {
        if (getXRefSection().getOffset() != -1) {
            // create a new empty xref section for changes...
            return getXRefSection().createSuccessor();
        }
        return getXRefSection();
    }

    /**
     * Create a new valid key for use in the document.
     *
     * @return A new valid key for use in the document.
     */
    public COSObjectKey createObjectKey() {
        synchronized (nextKey) {
            nextKey = nextKey.createNextKey();
            return nextKey;
        }
    }

    /**
     * Create a new random access object for the document data.
     *
     * @param pLocator The locator to the document data.
     * @return Create a new random access object for the document data.
     * @throws IOException
     */
    protected IRandomAccess createRandomAccess(ILocator pLocator) throws IOException {
        if (pLocator == null) {
            return null;
        }
        IRandomAccess baseAccess = pLocator.getRandomAccess();
        if (baseAccess.isReadOnly()) {
            pLocator.setReadOnly();
        }
        return new BufferedRandomAccess(baseAccess, 4096);
    }

    protected void ensureLength(int index) {
        if (index >= objects.length) {
            int newLength = objects.length + 100;
            if (index >= newLength) {
                newLength = index + 100;
            }
            COSIndirectObject[] tempObjects = new COSIndirectObject[newLength];
            System.arraycopy(objects, 0, tempObjects, 0, objects.length);
            objects = tempObjects;
        }
    }

    /**
     * Start a garbage collection for the receiver. In a garbage collection
     * every indirect object currently unused (unreachable from the catalog) is
     * removed.
     */
    public void garbageCollect() {
        // ignore all changes so far - we will include all anyway
        synchronized (changes) {
            changes.clear();
        }
        COSObjectWalkerDeep walker = new COSObjectWalkerDeep() {
            @Override
            public Object visitFromIndirectObject(COSIndirectObject io) throws COSVisitorException {
                if (getVisited().contains(io)) {
                    return null;
                }
                super.visitFromIndirectObject(io);
                // read and hold to enable "hardening"
                COSObject tempObject = io.dereference();
                // add reachable object to list of changes
                io.setDirty(true);
                return null;
            }
        };
        try {
            cosGetTrailer().accept(walker);
        } catch (COSVisitorException e) {
            // won't happen
        }

        // prepare new empty x ref section
        STTrailerXRefSection emptyXRefSection = new STTrailerXRefSection(this);
        COSDictionary emptyTrailer = emptyXRefSection.cosGetDict();
        emptyTrailer.addAll(cosGetTrailer());
        emptyTrailer.remove(COSTrailer.DK_Prev);
        emptyTrailer.remove(COSTrailer.DK_Size);
        emptyTrailer.remove(STXRefSection.DK_XRefStm);
        setXRefSection(emptyXRefSection);
        // prepare new object collection
        synchronized (objects) {
            Arrays.fill(objects, null);
        }
        synchronized (nextKey) {
            nextKey = new COSObjectKey(0, 0);
        }
        for (Iterator i = walker.getVisited().iterator(); i.hasNext(); ) {
            COSIndirectObject o = (COSIndirectObject) i.next();
            // force new key
            o.setKey(null);
            addObjectReference(o);
        }
    }

    public Object getAccessLock() {
        return accessLock;
    }

    /**
     * If a document contains a permissions dictionary, it is "pushed" to this
     * by the parser. Otherwise the document will have full permissions set.
     *
     * @return The document access permissions
     */
    public IAccessPermissions getAccessPermissions() {
        if (getReadSecurityHandler() != null) {
            ISecurityHandler basicSecurityHandler = getReadSecurityHandler().getSecurityHandler();
            if (basicSecurityHandler instanceof IAccessPermissionsSupport) {
                return ((IAccessPermissionsSupport) basicSecurityHandler).getAccessPermissions();
            }
        }
        return AccessPermissionsFull.get();
    }

    @Override
	public final synchronized Object getAttribute(Object key) {
        return attributes.get(key);
    }

    public Collection<COSIndirectObject> getChanges() {
        synchronized (changes) {
            return changes;
        }
    }

    public COSDocument getDoc() {
        return doc;
    }

    public STDocType getDocType() {
        return docType;
    }

    public int getIncrementalCount() {
        return getXRefSection().getIncrementalCount();
    }

    /**
     * The /Linearized dictionary of the document. The /Linearized dictionary is
     * represented by the first entry in the (logically) first XRef section.
     * <p>
     * Note that this method may NOT return a dictionary even if the document
     * contains a /Linearized dictionary as the first object. This is the case
     * when the document was linearized and was written with an incremental
     * change so that the linearization is obsolete.
     *
     * @return The valid /Linearized dictionary of the document.
     */
    public COSDictionary getLinearizedDict() {
        int objectNumber = 0;
        Iterator it = getXRefSection().entryIterator();
        while (it.hasNext()) {
            STXRefEntry entry = (STXRefEntry) it.next();
            if (entry.getObjectNumber() != 0) {
                objectNumber = entry.getObjectNumber();
                break;
            }
        }
        try {
            COSObject result = load(objectNumber);
            if (result != null && result.asDictionary() != null) {
                COSObject version = result.asDictionary().get(COSName.constant("Linearized"));
                if (!version.isNull()) {
                    return result.asDictionary();
                }
            }
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    /**
     * THe locator for the document data.
     *
     * @return THe locator for the document data.
     */
    @Override
    public ILocator getLocator() {
        return locator;
    }

    /**
     * A name for the document.
     * <p>
     * This is either a "local" name or the name of the locator reference if
     * present.
     *
     * @return A name for the document
     */
    public String getName() {
        return getLocator().getLocalName();
    }

    /**
     * The indirect object with object number objNum and generation number
     * genNum is looked up in the document. If the indirect object is not yet
     * available, it is created and registered.
     *
     * @param key
     * @return The indirect object with object number objNum and generation
     * number genNum
     */
    public COSIndirectObject getObjectReference(COSObjectKey key) {
        return getObjectReference(key.getObjectNumber(), key.getGenerationNumber());
    }

    /**
     * The indirect object with object number objNum and generation number
     * genNum is looked up in the document. If the indirect object is not yet
     * available, it is created and registered.
     *
     * @param objectNumber
     * @param generationNumber
     * @return The indirect object with object number objNum and generation
     * number genNum
     */
    public COSIndirectObject getObjectReference(int objectNumber, int generationNumber) {
        synchronized (objects) {
            COSIndirectObject result = null;
            if (objectNumber < objects.length) {
                result = objects[objectNumber];
            }
            if (result == null) {
                result = COSIndirectObject.create(this, objectNumber, generationNumber);
            }
            return result;
        }
    }

    /**
     * The parser used for decoding the document data stream.
     *
     * @return The parser used for decoding the document data stream.
     */
    public COSDocumentParser getParser() {
        return parser;
    }

    /**
     * The random access object for the documents data. Be aware that using the
     * IRandomAccess after it is closed will throw an IOException.
     *
     * @return The random access object for the documents data.
     */
    public IRandomAccess getRandomAccess() {
        return randomAccess;
    }

    /**
     * The documents security handler for decrypting.
     *
     * @return The documents security handler for decrypting.
     */
    public ISystemSecurityHandler getReadSecurityHandler() {
        return readSecurityHandler;
    }

    public COSTrailer getTrailer() {
        return (COSTrailer) COSTrailer.META.createFromCos(cosGetTrailer());
    }

    /**
     * The version of the PDF spec for this document
     *
     * @return The version of the PDF spec for this document
     */
    public String getVersion() {
        // todo 1 @mit fix version
        return getDocType().toString();
    }

    /**
     * The write mode to be used when the document is written the next time. If
     * defined this overrides any hint that is used when saving the document.
     * The write mode is reset after each "save".
     *
     * @return The write mode to be used when the document is written.
     */
    public EnumWriteMode getWriteModeHint() {
        return writeModeHint;
    }

    /**
     * The documents security handler for encrypting.
     *
     * @return The documents security handler for encrypting.
     */
    public ISystemSecurityHandler getWriteSecurityHandler() {
        return writeSecurityHandler;
    }

    /**
     * The most recent STXrefSection of the document.
     *
     * @return The most recent STXrefSection of the document.
     */
    public STXRefSection getXRefSection() {
        return xRefSection;
    }

    public void incrementalGarbageCollect() {
        final Set unknown;
        synchronized (changes) {
            unknown = new HashSet(changes);
        }
        COSObjectWalkerDeep stripper = new COSObjectWalkerDeep(true, false) {
            @Override
            public Object visitFromIndirectObject(COSIndirectObject io) throws COSVisitorException {
                unknown.remove(io);
                return super.visitFromIndirectObject(io);
            }
        };
        try {
            cosGetTrailer().accept(stripper);
        } catch (COSVisitorException e) {
            // won't happen
        }
        synchronized (changes) {
            changes.removeAll(unknown);
        }
    }

    /**
     * Initialize the security handler context.
     *
     * @throws IOException
     */
    protected void initEncryption() throws IOException {
        readSecurityHandler = null;
        writeSecurityHandler = null;
        try {
            readSecurityHandler = SystemSecurityHandler.createFromSt(this);
            if (readSecurityHandler != null) {
                // force authentication early, /AuthEvent not yet supported
                readSecurityHandler.authenticate();
            }
        } catch (COSSecurityException e) {
            IOException ioe = new IOException(e.getMessage());
            ioe.initCause(e);
            throw ioe;
        }
        writeSecurityHandler = readSecurityHandler;
    }

    /**
     * Initialize the document from its data.
     *
     * @throws IOException
     * @throws COSLoadException
     */
    protected void initializeFromLocator() throws IOException, COSLoadException {
        parser = new COSDocumentParser(this);
        streamLoad();
    }

    /**
     * Initialize a new empty document
     */
    protected void initializeFromScratch(STDocType pDocType) {
        setDocType(pDocType);
        String name = createName(getDocType().getTypeName());
        locator = new TransientLocator(name, pDocType.getTypeName());
        parser = new COSDocumentParser(this);
        setXRefSection(new STTrailerXRefSection(this));
        nextKey = new COSObjectKey(0, 0);
        cosGetTrailer().put(COSTrailer.DK_Root, COSCatalog.META.createNew().cosGetDict());
        setDirty(true);
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

    public boolean isClosed() {
        return closed;
    }

    /**
     * {@code true} if this has been changed.
     *
     * @return {@code true} if this has been changed.
     */
    public boolean isDirty() {
        return dirty;
    }

    /**
     * @return if the document has an {@link ISystemSecurityHandler}
     */
    public boolean isEncrypted() {
        return getReadSecurityHandler() != null;
    }

    /**
     * {@code true} if this document is linearized.
     * <p>
     * When linearized reading is truly implemented, this check should be made
     * using the document length instead for performance reasons.
     *
     * @return {@code true} if this document is linearized.
     */
    public boolean isLinearized() {
        return getLinearizedDict() != null;
    }

    public boolean isNew() {
        return (getXRefSection().getOffset() == -1) && (getXRefSection().getPrevious() == null);
    }

    /**
     * {@code true} if this is read only.
     *
     * @return {@code true} if this is read only.
     */
    public boolean isReadOnly() {
        return (getRandomAccess() == null) || getRandomAccess().isReadOnly();
    }

    /**
     * {@code true} if this has only streamed xref sections.
     *
     * @return {@code true} if this has only streamed xref sections.
     */
    public boolean isStreamed() {
        if (getXRefSection() != null) {
            return getXRefSection().isStreamed();
        }
        return false;
    }

    /**
     * Load a COSObject from the documents data.
     *
     * @param ref The object reference to be loaded.
     * @throws IOException
     * @throws COSLoadException
     */
    public COSObject load(COSIndirectObject ref) throws IOException, COSLoadException {
        int objectNumber = ref.getKey().getObjectNumber();
        return load(objectNumber);
    }

    protected COSObject load(int objectNumber) throws IOException, COSLoadException {
        synchronized (getAccessLock()) {
            if (isClosed()) {
                return COSNull.NULL;
            }
            return getXRefSection().load(objectNumber, getReadSecurityHandler());
        }
    }

    public void loadAll() throws IOException, COSLoadException {
        synchronized (getAccessLock()) {
            if (isClosed()) {
                return;
            }
            for (int i = 0; i < getXRefSection().getSize(); i++) {
                getXRefSection().load(i, getReadSecurityHandler());
            }
        }
    }

    /**
     * The number of objects currently loaded.
     *
     * @return The number of objects currently loaded.
     */
    public int loadedSize() {
        int result = 0;
        synchronized (objects) {
            for (COSIndirectObject io : objects) {
                if (!io.isSwapped()) {
                    result++;
                }
            }
        }
        return result;
    }

    /**
     * An iterator on the indirect objects of the storage layer document. This
     * includes garbage and purely technical objects like x ref streams.
     *
     * @return An iterator on the indirect objects of the storage layer
     * document. This includes garbage and purely technical objects like
     * x ref streams.
     */
    public Iterator objects() {
        return new Iterator() {
            int i = 1;
            int size = getXRefSection().getSize();

            @Override
            public boolean hasNext() {
                return i < size;
            }

            @Override
            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException(""); //$NON-NLS-1$
                }
                return getObjectReference(i++, 0);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove not supported"); //$NON-NLS-1$
            }
        };
    }

    /**
     * @throws IOException
     */
    protected void open() throws IOException {
        synchronized (getAccessLock()) {
            if ((randomAccess != null) && !isClosed()) {
                throw new IllegalStateException("can't open an open document"); //$NON-NLS-1$
            }
            setRandomAccess(createRandomAccess(getLocator()));
        }
    }

    @Override
	public final synchronized Object removeAttribute(Object key) {
        Object oldValue = attributes.remove(key);
        triggerChanged(key, oldValue, null);
        return oldValue;
    }

    @Override
    public void removeNotificationListener(EventType type, INotificationListener listener) {
        dispatcher.removeNotificationListener(type, listener);
    }

    /**
     * Reparses the XREF sections without actually instantiating. Used for
     * collecting errors on XREF level
     *
     * @throws IOException
     * @throws COSLoadException
     */
    public void reparseFromLocator() throws IOException, COSLoadException {
        synchronized (getAccessLock()) {
            int offset = getParser().searchLastStartXRef(getRandomAccess());
            AbstractXRefParser xRefParser;
            if (getParser().isTokenXRefAt(getRandomAccess(), offset)) {
                xRefParser = new XRefTrailerParser(this, getParser());
            } else {
                xRefParser = new XRefStreamParser(this, getParser());
            }
            getRandomAccess().seek(offset);
            xRefParser.parse(getRandomAccess());
        }
    }

    /**
     * Assign a new locator to the document.
     * <p>
     * The documents data is completely copied to the new location.
     *
     * @param newLocator The new locator for the documents data.
     * @throws IOException
     */
    protected void replaceLocator(ILocator newLocator) throws IOException {
        ILocator oldLocator = getLocator();
        if (newLocator.equals(oldLocator)) {
            return;
        }
        synchronized (getAccessLock()) {
            IRandomAccess oldRandomAccess = getRandomAccess();
            try {
                setLocator(newLocator);
                setRandomAccess(null);
                open();
                IRandomAccess newRandomAccess = getRandomAccess();
                if (newRandomAccess.isReadOnly()) {
                    throw new FileNotFoundException();
                }
                if (oldRandomAccess != null) {
                    newRandomAccess.setLength(oldRandomAccess.getLength());
                    InputStream is = oldRandomAccess.asInputStream();
                    OutputStream os = newRandomAccess.asOutputStream();
                    oldRandomAccess.seek(0);
                    StreamTools.copyStream(is, false, os, false);
                } else {
                    newRandomAccess.setLength(0);
                }
                StreamTools.close(oldRandomAccess);
            } catch (Exception e) {
                // undo changes
                StreamTools.close(getRandomAccess());
                setLocator(oldLocator);
                setRandomAccess(oldRandomAccess);
                if (e instanceof IOException) {
                    throw (IOException) e;
                }
                throw ExceptionTools.createIOException("unexpected exception saving '" + newLocator.getFullName() + "'",
                                                       e);
            }
        }
        triggerChanged(ATTR_LOCATOR, oldLocator, newLocator);
    }

    public void restore(ILocator newLocator) throws IOException, COSLoadException {
        synchronized (getAccessLock()) {
            ILocator oldLocator = getLocator();
            if (newLocator.equals(oldLocator)) {
                return;
            }
            IRandomAccess oldRandomAccess = getRandomAccess();
            StreamTools.close(oldRandomAccess);
            setRandomAccess(null);
            setLocator(newLocator);
            synchronized (changes) {
                changes.clear();
            }
            synchronized (objects) {
                Arrays.fill(objects, null);
            }
            closed = false;
            dirty = false;
            streamLoad();
        }
        getDoc().triggerChangedAll();
    }

    public void save() throws IOException {
        save(getLocator(), null);
    }

    public void save(ILocator pLocator) throws IOException {
        save(pLocator, null);
    }

    public void save(ILocator pLocator, Map options) throws IOException {
        // options could be null, when called from save(), even if a locator
        // exists.
        if (options == null) {
            options = new HashMap();
        }
        if ((pLocator != null) && (pLocator != getLocator())) {
            replaceLocator(pLocator);
        }
        boolean incremental = true;
        EnumWriteMode writeMode = doc.getWriteModeHint();
        // reset write mode
        doc.setWriteModeHint(EnumWriteMode.UNDEFINED);
        if (writeMode.isUndefined()) {
            Object tempHint = options.get(OPTION_WRITEMODEHINT);
            if (tempHint instanceof EnumWriteMode) {
                writeMode = (EnumWriteMode) tempHint;
            }
        }
        if (writeMode.isFull()) {
            incremental = false;
        }
        IRandomAccess tempRandomAccess = getRandomAccess();
        if (tempRandomAccess == null) {
            throw new IOException("nowhere to write to"); //$NON-NLS-1$
        }
        if (tempRandomAccess.isReadOnly()) {
            throw new FileNotFoundException("destination is read only"); //$NON-NLS-1$
        }
        COSWriter writer = new COSWriter(tempRandomAccess, getWriteSecurityHandler());
        writer.setAutoUpdate(isAutoUpdate());
        writer.setIncremental(incremental);
        writer.writeDocument(this);
        readSecurityHandler = writeSecurityHandler;
    }

    @Override
	public final synchronized Object setAttribute(Object key, Object value) {
        Object oldValue = attributes.put(key, value);
        triggerChanged(key, oldValue, value);
        return oldValue;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    protected void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * Set the change flag of this.
     *
     * @param dirty {@code true} if this should be marked as changed
     */
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        if (!dirty) {
            synchronized (changes) {
                changes.clear();
            }
        }
    }

    public void setDoc(COSDocument doc) {
        this.doc = doc;
        getXRefSection().setCOSDoc(getDoc());
    }

    protected void setDocType(STDocType docType) {
        this.docType = docType;
    }

    protected void setLocator(ILocator pLocator) {
        locator = pLocator;
    }

    /**
     * Rename the document locally.
     * <p>
     * This has no effect if a locator is present.
     *
     * @param name The new local name of this
     */
    public void setName(String name) {
        if (getLocator() instanceof TransientLocator) {
            ((TransientLocator) getLocator()).setLocalName(name);
        }
    }

    /**
     * Assign the {@link IRandomAccess} to the raw data.
     *
     * @param randomAccess the {@link IRandomAccess} to the raw data.
     */
    protected void setRandomAccess(IRandomAccess randomAccess) {
        this.randomAccess = randomAccess;
    }

    /**
     * Set the ISystemSecurityHandler in order to change document's encryption.
     *
     * @param handler the ISystemSecurityHandler to the documents data.
     * @throws COSSecurityException
     */
    public void setSystemSecurityHandler(ISystemSecurityHandler handler) throws COSSecurityException {
        Object oldValue = writeSecurityHandler;
        if (writeSecurityHandler != null) {
            writeSecurityHandler.detach(this);
        }
        this.writeSecurityHandler = handler;
        if (writeSecurityHandler != null) {
            writeSecurityHandler.attach(this);
        }
        triggerChanged(ATTR_SYSTEM_SECURITY_HANDLER, oldValue, writeSecurityHandler);
    }

    /**
     * The write mode to be used when the document is written the next time. If
     * defined this overrides any hint that is used when saving the document.
     * The write mode is reset after each "save".
     *
     * @param writeMode The write mode to be used when the document is written.
     */
    public void setWriteModeHint(EnumWriteMode writeMode) {
        if (writeMode == null) {
            throw new IllegalArgumentException("write mode can't be null"); //$NON-NLS-1$
        }
        this.writeModeHint = writeMode;
    }

    /**
     * Attach the most recent x ref section to the document.
     *
     * @param pXRefSection The x ref section representing the most recent document
     *                     changes.
     */
    public void setXRefSection(STXRefSection pXRefSection) {
        Object oldValue = xRefSection;
        xRefSection = pXRefSection;
        if (getDoc() != null) {
            xRefSection.setCOSDoc(getDoc());
        }
        triggerChanged(ATTR_XREF_SECTION, oldValue, xRefSection);
    }

    protected void streamLoad() throws IOException, COSLoadException {
        try {
            open();
            STXRefSection initialXRefSection;
            setDocType(getParser().parseHeader(getRandomAccess()));
            try {
                int offset = getParser().searchLastStartXRef(getRandomAccess());
                AbstractXRefParser xRefParser;
                if (getParser().isTokenXRefAt(getRandomAccess(), offset)) {
                    xRefParser = new XRefTrailerParser(this, getParser());
                } else {
                    xRefParser = new XRefStreamParser(this, getParser());
                }
                getRandomAccess().seek(offset);
                initialXRefSection = xRefParser.parse(getRandomAccess());
                setXRefSection(initialXRefSection);
                checkConsistency();
            } catch (Exception ex) {
                Log.log(Level.FINEST, "error parsing " //$NON-NLS-1$
                                      + getLocator().getFullName(), ex);
                synchronized (objects) {
                    // must reset objects, catalog may already be read
                    Arrays.fill(objects, null);
                }
                // TODO 2 log warning, trailer can't be parsed
                initialXRefSection = new XRefFallbackParser(this, getParser()).parse(getRandomAccess());
                setXRefSection(initialXRefSection);
                checkConsistency();
            }
            int size = initialXRefSection.getSize();
            nextKey = new COSObjectKey(size - 1, 0);
            ensureLength(size);
            initEncryption();
        } catch (IOException | COSLoadException e) {
            try {
                close();
            } catch (IOException ce) {
                // ignore
            }
            throw e;
        }
	}

    protected void triggerChanged(Object attribute, Object oldValue, Object newValue) {
        Event event = new AttributeChangedEvent(this, attribute, oldValue, newValue);
        triggerEvent(event);
    }

    protected void triggerEvent(Event event) {
        dispatcher.triggerEvent(event);
    }

    public void updateModificationDate() {
        COSDictionary infoDict = cosGetTrailer().get(COSTrailer.DK_Info).asDictionary();
        if (infoDict == null) {
            return;
        }
        infoDict.put(COSInfoDict.DK_ModDate, new CDSDate().cosGetObject());
    }
}
