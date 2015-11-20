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

import de.intarsys.pdf.crypt.IAccessPermissions;
import de.intarsys.pdf.crypt.ISecurityHandler;
import de.intarsys.pdf.parser.COSLoadError;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.st.EnumWriteMode;
import de.intarsys.pdf.st.STDocType;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.pdf.st.STXRefSection;
import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.event.AttributeChangedEvent;
import de.intarsys.tools.event.Event;
import de.intarsys.tools.event.INotificationListener;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * This is a COS level representation of a pdf document. A COS document is made
 * up of a collection of {@link COSObject} instances. These objects are arranged
 * according to the PDF file format specification.
 * <p>
 * <p>
 * See PDF File Format Specification [PDF].
 * </p>
 */
public class COSDocument implements ICOSContainer, ICOSExceptionHandler, IAttributeSupport, ILocatorSupport {

    /**
     * A dummy object indicating a unconstrained change
     */
    public static final Object SLOT_ALL = new Attribute("_all_");

    public static final Object SLOT_DIRTY = new Attribute("dirty");

    public static final Object SLOT_LOCATOR = new Attribute("locator");

    public static final Object SLOT_TRAILER = new Attribute("trailer");

    public static COSDocument createFromLocator(ILocator locator) throws IOException, COSLoadException {
        return createFromLocator(locator, null);
    }

    /**
     * Create a COSDocument based on a Locator.
     *
     * @param locator The ILocater referencing the documents data stream.
     * @return A new COSDocument.
     * @throws COSLoadException
     * @throws IOException
     */
    public static COSDocument createFromLocator(ILocator locator, Map options) throws IOException, COSLoadException {
        STDocument stDoc = STDocument.createFromLocator(locator, options);
        try {
            return createFromST(stDoc);
        } catch (COSLoadException e) {
            if (stDoc != null) {
                try {
                    stDoc.close();
                } catch (IOException ex) {
                    // ignore
                }
            }
            throw e;
        }
    }

    /**
     * Create a COSDocument based on a STDocument.
     *
     * @param doc The storage layer document.
     * @return A new COSDocument.
     * @throws COSLoadException
     */
    public static COSDocument createFromST(STDocument doc) throws COSLoadException {
        COSDocument result = new COSDocument(doc);
        result.initializeFromST();
        result.checkConsistency();
        return result;
    }

    /**
     * Create a new empty PDF COSDocument.
     *
     * @return A new empty PDF COSDocument
     */
    public static COSDocument createNew() {
        return createNew(STDocument.DOCTYPE_PDF);
    }

    /**
     * Create a new empty COSDocument.
     *
     * @return A new empty COSDocument
     */
    public static COSDocument createNew(STDocType docType) {
        STDocument stDoc = STDocument.createNew(docType);
        COSDocument doc = new COSDocument(stDoc);
        doc.initializeFromScratch();
        return doc;
    }

    /**
     * The list of listeners interested in document change events.
     */
    private List<ICOSDocumentListener> documentListeners;

    private ICOSExceptionHandler exceptionHandler;

    private INotificationListener listenSTChange = new INotificationListener() {

        @Override
        public void handleEvent(Event event) {
            onStDocumentChange((AttributeChangedEvent) event);
        }
    };

    /**
     * The list of listeners interested in session events.
     */
    private List<ICOSMonitor> monitors;

    /**
     * The abstraction of the document storage layer.
     */
    private STDocument stDoc;

    /**
     * Create a new empty COSDocument.
     * <p>
     * This one does no initialization, use the factory method.
     */
    protected COSDocument() {
        this(STDocument.createNew());
    }

    /**
     * Create a new COSDocument based on a STDocument.
     * <p>
     * This one does no initialization, use the factory method.
     *
     * @param pStDoc The storage level document
     */
    protected COSDocument(STDocument pStDoc) {
        stDoc = pStDoc;
        stDoc.setDoc(this);
        stDoc.addNotificationListener(AttributeChangedEvent.ID, listenSTChange);
    }

    /**
     * This should not be used by the application programmer.
     * {@code public} for package visibility reasons.
     *
     * @param element
     */
    public void add(COSDocumentElement element) {
        COSDocumentElement containable = element.containable();
        containable.addContainer(this);
    }

    /**
     * Add an {@link ICOSDocumentListener} to be informed about the documents
     * events.
     *
     * @param listener THe new listener
     */
    public void addDocumentListener(ICOSDocumentListener listener) {
        List<ICOSDocumentListener> newListeners;
        if (documentListeners == null) {
            newListeners = new ArrayList<ICOSDocumentListener>();
        } else {
            newListeners = new ArrayList<ICOSDocumentListener>(documentListeners);
        }
        newListeners.add(listener);
        documentListeners = newListeners;
    }

    public void addMonitor(ICOSMonitor listener) {
        List<ICOSMonitor> newMonitors;
        if (monitors == null) {
            newMonitors = new ArrayList<ICOSMonitor>();
        } else {
            newMonitors = new ArrayList<ICOSMonitor>(monitors);
        }
        newMonitors.add(listener);
        monitors = newMonitors;
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public ICOSContainer associate(ICOSContainer newContainer, COSObject object) {
        if (newContainer == this) {
            // error ?
            return this;
        }

        // sorry, this is an error
        throw new IllegalStateException("object may only be contained once (use indirect object)"); //$NON-NLS-1$
    }

    protected void checkConsistency() throws COSLoadError {
        if (getCatalog() == null) {
            throw new COSLoadError("Catalog missing"); //$NON-NLS-1$
        }
    }

    /**
     * Close the document. Accessing a documents content is undefined after
     * {@code close}.
     *
     * @throws IOException
     */
    public void close() throws IOException {
        stDoc.close();
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public COSDocumentElement containable(COSObject object) {
        return object;
    }

    /**
     * Make a deep copy of the receiver. The newly created document has the same
     * content as this, but does not share any object. The structure of the ST
     * level is built from scratch.
     *
     * @return A deep copy of this.
     */
    public COSDocument copyDeep() {
        try {
            return COSDocument.createFromST(stGetDoc().copyDeep());
        } catch (COSLoadException e) {
            throw new COSRuntimeException(e);
        }
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public ICOSContainer disassociate(ICOSContainer oldContainer, COSObject object) {
        if (oldContainer == this) {
            // object removed from container
            object.basicSetContainer(COSObject.NULL_CONTAINER);
            return COSObject.NULL_CONTAINER;
        }

        // sorry, this is an error
        throw new IllegalStateException("association inconsistent"); //$NON-NLS-1$
    }

    /**
     * The currently active access permissions if supported by the
     * {@link ISecurityHandler}.
     *
     * @return The currently active access permissions if supported by the
     * {@link ISecurityHandler}.
     */
    public IAccessPermissions getAccessPermissions() {
        return stGetDoc().getAccessPermissions();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#getAttribute(java.lang.
     * Object)
     */
    @Override
    public synchronized Object getAttribute(Object key) {
        return stDoc.getAttribute(key);
    }

    /**
     * Get the root object (the catalog) for the document.
     *
     * @return The root object (the catalog) for the document.
     */
    public COSCatalog getCatalog() {
        return getTrailer().getRoot();
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public COSDocument getDoc() {
        return this;
    }

    public ICOSExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * The number of versions created for this document so far.
     *
     * @return The number of versions created for this document so far.
     */
    public int getIncrementalCount() {
        return stGetDoc().getIncrementalCount();
    }

    /**
     * Get the info dictionary containing metadata.
     *
     * @return The info dictionary containing metadata.
     */
    public COSInfoDict getInfoDict() {
        return getTrailer().getInfoDict();
    }

    /**
     * The {@link ILocator} for this document. The {@link ILocator} designates
     * the physical storage for the PDF data.
     *
     * @return The {@link ILocator} for this document.
     */
    @Override
    public ILocator getLocator() {
        return stDoc.getLocator();
    }

    /**
     * The document name. This is derived from the associated {@link ILocator}.
     *
     * @return The document name.
     */
    public String getName() {
        return stDoc.getName();
    }

    /**
     * The trailer for the document.
     *
     * @return The trailer for the document.
     */
    public COSTrailer getTrailer() {
        return stGetDoc().getTrailer();
    }

    /**
     * The write mode to be used when the document is written the next time. If
     * defined this overrides any hint that is used when saving the document.
     * The write mode is reset after each "save".
     *
     * @return The write mode to be used when the document is written.
     */
    public EnumWriteMode getWriteModeHint() {
        return stDoc.getWriteModeHint();
    }

    /*
     * (non-Javadoc)
     *
     * @seede.intarsys.pdf.cos.ICOSExceptionHandler#error(de.intarsys.pdf.cos.
     * COSRuntimeException)
     */
    @Override
    public void handleException(COSRuntimeException ex) {
        if (exceptionHandler != null) {
            exceptionHandler.handleException(ex);
        } else {
            throw ex;
        }
    }

    @Override
    public void harden(COSObject object) {
        // ignore
    }

    protected void initializeFromScratch() {
        //
    }

    protected void initializeFromST() {
        //
    }

    /**
     * When auto update is true, the {@link COSWriter} will automatically create
     * new values for the file modification date in the info dictionary and the
     * file id in the trailer. When false, these values are under client code
     * control.
     *
     * @return Answer {@code true} if the document has auto update enabled.
     */
    public boolean isAutoUpdate() {
        return stDoc.isAutoUpdate();
    }

    /**
     * Answer {@code true} if the document has changes to be committed.
     *
     * @return Answer {@code true} if the document has changes to be
     * committed.
     */
    public boolean isDirty() {
        return stGetDoc().isDirty();
    }

    /**
     * Answer {@code true} if the document is encrypted.
     *
     * @return Answer {@code true} if the document is encrypted.
     */
    public boolean isEncrypted() {
        return stGetDoc().isEncrypted();
    }

    /**
     * Answer {@code true} if the document is new, i.e. not yet written.
     *
     * @return Answer {@code true} if the document is new, i.e. not yet
     * written.
     */
    public boolean isNew() {
        return stDoc.isNew();
    }

    /**
     * Answer {@code true} if the document is read only. To save the
     * document and its changes you have to define another {@link ILocator} when
     * saving.
     *
     * @return Answer {@code true} if the document is read only.
     */
    public boolean isReadOnly() {
        return stDoc.isReadOnly();
    }

    /**
     * An iterator on all COSObject instances of this that are managed as
     * indirect objects in the storage layer.
     * <p>
     * ATTENTION: This iterator may (and on incremental documents most often
     * will) return objects that are no longer used (referenced) in the
     * document.
     *
     * @return An iterator on all COSObject instances of this document that are
     * managed as indirect objects in the storage layer.
     */
    public Iterator<COSObject> objects() {
        Iterator iteratorObjects = new Iterator() {
            /**
             * The iterator on all indirect objects in the storage layer. This
             * also includes garbage or purely structural objects like x ref
             * streams.
             */
            private Iterator indirectObjects = stGetDoc().objects();

            COSIndirectObject io = null;

            @Override
            public boolean hasNext() {
                if (io != null) {
                    return true;
                }
                while (indirectObjects.hasNext()) {
                    COSIndirectObject current = (COSIndirectObject) indirectObjects.next();
                    if (!current.dereference().isDangling()) {
                        io = current;
                        break;
                    }
                }
                return io != null;
            }

            @Override
            public Object next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                COSIndirectObject result = io;
                io = null;
                return result.dereference();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
        return iteratorObjects;
    }

    protected void onStDocumentChange(AttributeChangedEvent event) {
        if (STDocument.ATTR_XREF_SECTION.equals(event.getAttribute())) {
            STXRefSection oldXRef = (STXRefSection) event.getOldValue();
            STXRefSection newXRef = (STXRefSection) event.getNewValue();
            COSDictionary oldTrailer = oldXRef == null ? null : oldXRef.cosGetDict();
            COSDictionary newTrailer = newXRef == null ? null : newXRef.cosGetDict();
            triggerChanged(SLOT_TRAILER, oldTrailer, newTrailer);
        }
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public int referenceCount() {
        return 1;
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public COSIndirectObject referenceIndirect(COSObject object) {
        // i contain the trailer - that will never be indirect
        throw new IllegalStateException("document can not have indirect references"); //$NON-NLS-1$
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public void register(COSDocumentElement object) {
        object.registerWith(this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.component.IAttributeSupport#removeAttribute(java.lang
     * .Object)
     */
    @Override
    public synchronized Object removeAttribute(Object key) {
        return stDoc.removeAttribute(key);
    }

    /**
     * Remove an {@link ICOSDocumentListener}.
     *
     * @param listener The listener to be removed
     */
    public void removeDocumentListener(ICOSDocumentListener listener) {
        if (documentListeners == null) {
            return;
        }
        List<ICOSDocumentListener> newListeners = new ArrayList<ICOSDocumentListener>(documentListeners);
        newListeners.remove(listener);
        documentListeners = newListeners;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.objectsession.IChangeListenerSupport#removeChangeListener
     * (de.intarsys.tools.objectsession.IChangeListener)
     */
    public void removeMonitor(ICOSMonitor monitor) {
        if (monitors == null) {
            return;
        }
        List<ICOSMonitor> newMonitors = new ArrayList<ICOSMonitor>(monitors);
        newMonitors.remove(monitor);
        monitors = newMonitors;
    }

    /**
     * Restore this from a locator. The {@link ILocator} must reference a data
     * stream that was previously used to parse the document.
     *
     * @param locator The {@link ILocator} defining the new physical content.
     * @throws IOException
     * @throws COSLoadException
     */
    public void restore(ILocator locator) throws IOException, COSLoadException {
        stDoc.restore(locator);
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public ICOSContainer restoreStateContainer(ICOSContainer container) {
        return container;
    }

    /**
     * Save the document to its current {@link ILocator}.
     *
     * @throws IOException
     */
    public void save() throws IOException {
        save(getLocator(), null);
    }

    /**
     * Save the document nto a new {@link ILocator}.
     *
     * @param locator The {@link ILocator} defining the new data location.
     * @throws IOException
     */
    public void save(ILocator locator) throws IOException {
        save(locator, null);
    }

    /**
     * Save the document to an optional new {@link ILocator} using the
     * {@code options} to control specific serializing behavior such as
     * "incremental writing".
     *
     * @param locator
     * @param options
     * @throws IOException
     */
    public void save(ILocator locator, Map options) throws IOException {
        Object oldValue = getLocator();
        stDoc.save(locator, options);
        triggerChangedLocator(oldValue, locator);
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public ICOSContainer saveStateContainer() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * de.intarsys.tools.attribute.IAttributeSupport#setAttribute(java.lang.
     * Object, java.lang.Object)
     */
    @Override
    public synchronized Object setAttribute(Object key, Object value) {
        return stDoc.setAttribute(key, value);
    }

    public void setAutoUpdate(boolean autoUpdate) {
        stDoc.setAutoUpdate(autoUpdate);
    }

    /**
     * Assign a new catalog (/Root entry in the trailer). Use with care - this
     * "swaps" the whole document.
     *
     * @param catalog The new catalog.
     */
    public void setCatalog(COSCatalog catalog) {
        getTrailer().setRoot(catalog);
    }

    protected void setDirty(boolean b) {
        boolean oldValue = stGetDoc().isDirty();
        stGetDoc().setDirty(b);
        if (oldValue != b) {
            triggerChangedDirty();
        }
    }

    public void setExceptionHandler(ICOSExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Set the info dictionary containing metadata.
     *
     * @param infoDict The info dictionary containing metadata.
     */
    public void setInfoDict(COSInfoDict infoDict) {
        getTrailer().setInfoDict(infoDict);
    }

    /**
     * Assign a new name to the document.
     *
     * @param name The new name.
     */
    public void setName(String name) {
        stDoc.setName(name);
        triggerChangedLocator(getLocator(), getLocator());
    }

    /**
     * The write mode to be used when the document is written the next time. If
     * defined this overrides any hint that is used when saving the document.
     * The write mode is reset after each "save".
     *
     * @param writeMode The write mode to be used when the document is written.
     */
    public void setWriteModeHint(EnumWriteMode writeMode) {
        stDoc.setWriteModeHint(writeMode);
    }

    @Override
    public void soften(COSObject object) {
        // ignore
    }

    /**
     * The storage layer document.
     *
     * @return The storage layer document.
     */
    public STDocument stGetDoc() {
        return stDoc;
    }

    protected void triggerChanged(Object slot, Object oldValue, Object newValue) {
        if (documentListeners == null) {
            return;
        }
        for (Iterator<ICOSDocumentListener> it = documentListeners.iterator(); it.hasNext(); ) {
            ICOSDocumentListener listener = it.next();
            listener.changed(this, slot, oldValue, newValue);
        }
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    public void triggerChangedAll() {
        triggerChanged(SLOT_ALL, null, null);
    }

    protected void triggerChangedDirty() {
        Boolean newValue = Boolean.valueOf(isDirty());
        Boolean oldValue = Boolean.valueOf(!isDirty());
        triggerChanged(SLOT_DIRTY, oldValue, newValue);
    }

    protected void triggerChangedLocator(Object oldValue, Object newValue) {
        triggerChanged(SLOT_LOCATOR, oldValue, newValue);
    }

    /**
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    @Override
    public void willChange(COSObject change) {
        setDirty(true);
        if (monitors == null) {
            return;
        }
        for (Iterator<ICOSMonitor> iter = monitors.iterator(); iter.hasNext(); ) {
            ICOSMonitor monitor = iter.next();
            monitor.willChange(change);
        }
    }
}
