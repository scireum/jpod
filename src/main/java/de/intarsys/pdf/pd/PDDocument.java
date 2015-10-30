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
package de.intarsys.pdf.pd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cds.CDSNameTreeEntry;
import de.intarsys.pdf.cds.CDSNameTreeNode;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSCatalog;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSDocument;
import de.intarsys.pdf.cos.COSInfoDict;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSRuntimeException;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.crypt.AccessPermissionsTools;
import de.intarsys.pdf.crypt.IAccessPermissions;
import de.intarsys.pdf.crypt.ISystemSecurityHandler;
import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.st.EnumWriteMode;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.pdf.writer.COSWriter;
import de.intarsys.tools.attribute.IAttributeSupport;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.ILocatorSupport;

/**
 * The PD Level representation of a PDF Document. PD Level objects provide the
 * PDF semantics on top of the COS objects.
 * 
 * <p>
 * A PDDocument object should not be shared between threads without thought.
 * Some clients may store information along with the PDDocument extensions in a
 * non thread safe manner.
 * </p>
 * 
 */
public class PDDocument implements IAdditionalActionSupport, IAttributeSupport,
		ILocatorSupport {
	// supported additional action triggers
	public static final Set CATALOG_ACTION_TRIGGERS;

	public static final COSName CN_Perms_DocMDP = COSName.constant("DocMDP"); //$NON-NLS-1$

	public static final COSName CN_Perms_UR = COSName.constant("UR"); //$NON-NLS-1$

	public static final COSName DK_OpenAction = COSName.constant("OpenAction"); //$NON-NLS-1$

	public static final COSName DK_Perms = COSName.constant("Perms"); //$NON-NLS-1$

	public static final COSName DK_Legal = COSName.constant("Legal"); //$NON-NLS-1$

	static {
		CATALOG_ACTION_TRIGGERS = new HashSet(6);
		CATALOG_ACTION_TRIGGERS.add("DC"); //$NON-NLS-1$
		CATALOG_ACTION_TRIGGERS.add("WS"); //$NON-NLS-1$
		CATALOG_ACTION_TRIGGERS.add("DS"); //$NON-NLS-1$
		CATALOG_ACTION_TRIGGERS.add("WP"); //$NON-NLS-1$
		CATALOG_ACTION_TRIGGERS.add("DP"); //$NON-NLS-1$
	}

	/**
	 * create a pd document based on a cos level object
	 * 
	 * @param doc
	 *            COSDocument to base this PDDocument on
	 * 
	 * @return A new PDDocument object.
	 */
	public static PDDocument createFromCos(COSDocument doc) {
		PDDocument result = (PDDocument) doc.getAttribute(PDDocument.class);
		if (result == null) {
			result = new PDDocument(doc);
			result.initializeFromCos();
			result.checkConsistency();
			doc.setAttribute(PDDocument.class, result);
		}
		return result;
	}

	public static PDDocument createFromLocator(ILocator locator)
			throws IOException, COSLoadException {
		return createFromLocator(locator, null);
	}

	public static PDDocument createFromLocator(ILocator locator, Map options)
			throws IOException, COSLoadException {
		return createFromCos(COSDocument.createFromLocator(locator, options));
	}

	/**
	 * create a PDDocument from scratch
	 * 
	 * @return A new PDDocument.
	 */
	public static PDDocument createNew() {
		PDDocument result = new PDDocument();
		result.initializeFromScratch();
		// setAttribute is forwarded to COS level
		result.setAttribute(PDDocument.class, result);
		return result;
	}

	private IAccessPermissions accessPermissions;

	// some low level cos representations
	// the underlying COSDocument object
	private final COSDocument cosDoc;

	protected PDDocument() {
		this(COSDocument.createNew());
	}

	/**
	 * PDDocument constructor.
	 * 
	 * @param newDoc
	 *            The COS document representing the receiver.
	 */
	protected PDDocument(COSDocument newDoc) {
		super();
		cosDoc = newDoc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.pd.IActionContainer#addAction(de.intarsys.pdf.pd.PDAction
	 * )
	 */
	public void addAction(PDAction action) {
		addOpenAction(action);
	}

	public void addDestination(String name, COSObject destination) {
		COSCatalog catalog = getCatalog();
		//
		COSDictionary destsDict = catalog.cosGetDests();
		if (destsDict == null) {
			destsDict = COSDictionary.create();
			catalog.cosSetDests(destsDict);
		}
		destsDict.put(COSName.createUTF8(name), destination);
	}

	/**
	 * Add a {@link PDAction} to be exceuted when this is opened.
	 * 
	 * @param newAction
	 *            The new {@link PDAction}
	 */
	public void addOpenAction(PDAction newAction) {
		if (newAction == null) {
			return;
		}
		if (getOpenAction() != null) {
			getOpenAction().addNext(newAction);
		} else {
			setOpenAction(newAction);
		}
	}

	/**
	 * Add a page object to this documents root page tree.
	 * 
	 * @param newNode
	 *            The page to be added
	 */
	public void addPageNode(PDPageNode newNode) {
		getPageTree().addNode(newNode);
	}

	/**
	 * Add a page object to this document after the designated page.
	 * 
	 * @param newNode
	 *            the page to be added
	 * @param destination
	 *            The page after the new one is inserted
	 */
	public void addPageNodeAfter(PDPageNode newNode, PDPageNode destination) {
		if (destination == null) {
			getPageTree().addNode(newNode);
		} else {
			destination.getParent().addNodeAfter(newNode, destination);
		}
	}

	protected void checkConsistency() throws COSRuntimeException {
		if (getCatalog() == null) {
			throw new COSRuntimeException("Catalog missing"); //$NON-NLS-1$
		}
		if (getPageTree() == null) {
			throw new COSRuntimeException("Page tree missing"); //$NON-NLS-1$
		}
	}

	/**
	 * Close the document. Accessing a documents content is undefined after
	 * <code>close</code>.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		cosDoc.close();
	}

	protected void collectAnnotations(List result) {
		getPageTree().collectAnnotations(result);
	}

	/**
	 * Make a deep copy of the receiver.
	 * 
	 * @return the object copied recursively
	 */
	public PDDocument copyDeep() {
		return PDDocument.createFromCos(cosGetDoc().copyDeep());
	}

	/**
	 * The COS level implementation of the document
	 * 
	 * @return The underlying COSDocument
	 */
	public COSDocument cosGetDoc() {
		return cosDoc;
	}

	/**
	 * The permissions dictionary of the document.
	 * 
	 * @return The permissions dictionary of the document.
	 */
	public COSDictionary cosGetPermissionsDict() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		return catalog.cosGetField(DK_Perms).asDictionary();
	}

	/**
	 * The piece info dictionary of the document.
	 * 
	 * @return The piece info dictionary of the document.
	 */
	public COSDictionary cosGetPieceInfoDict() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		return catalog.cosGetField(COSCatalog.DK_PieceInfo).asDictionary();
	}

	/**
	 * The uri dictionary of the document.
	 * 
	 * @return The uri dictionary of the document.
	 */
	public COSDictionary cosGetURI() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		return catalog.cosGetField(COSCatalog.DK_URI).asDictionary();
	}

	/**
	 * Set the permissions dictionary of the document.
	 * 
	 * @param permsDict
	 *            the new permission dictionary
	 */
	public void cosSetPermissionsDict(COSDictionary permsDict) {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return;
		}
		permsDict.beIndirect();
		catalog.cosSetField(DK_Perms, permsDict);
	}

	/**
	 * Set the piece info dictionary of the document.
	 * 
	 * @param dict
	 *            the new piece info dictionary
	 */
	public void cosSetPieceInfoDict(COSDictionary dict) {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return;
		}
		dict.beIndirect();
		catalog.cosSetField(COSCatalog.DK_PieceInfo, dict);
	}

	/**
	 * create an AcroForm in a PDF Document.
	 * 
	 * @return the acro form just created
	 * 
	 * @throws IllegalStateException
	 */
	public PDAcroForm createAcroForm() {
		if (getAcroForm() != null) {
			throw new IllegalStateException("AcroForm already available"); //$NON-NLS-1$
		}
		PDAcroForm acroForm = (PDAcroForm) PDAcroForm.META.createNew();
		PDResources formResources = (PDResources) PDResources.META.createNew();
		acroForm.setDefaultResources(formResources);
		setAcroForm(acroForm);
		return acroForm;
	}

	/**
	 * create a new page tree for this document. tha page tree must still be
	 * added to the document or a page tree to get visible.
	 * 
	 * @return the PDPageTree created
	 */
	public PDPageTree createPageTree() {
		PDPageTree result = (PDPageTree) PDPageTree.META.createNew();
		return result;
	}

	/**
	 * Get a permissions object. The permission objects reflects the limitations
	 * implied by the storage layer security handlers as well as those defined
	 * in the permissions dict.
	 * 
	 * @return document access permissions
	 */
	public IAccessPermissions getAccessPermissions() {
		if (accessPermissions == null) {
			accessPermissions = AccessPermissionsTools.createPermissions(this);
		}
		return accessPermissions;
	}

	/**
	 * read an AcroForm from a PDF Document. return null if no AcroForm is
	 * available.
	 * 
	 * @return the acro form of the document or null
	 */
	public PDAcroForm getAcroForm() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		return (PDAcroForm) PDAcroForm.META.createFromCos(catalog
				.cosGetField(COSCatalog.DK_AcroForm));
	}

	public PDAdditionalActions getAdditionalActions() {
		COSDictionary aa = null;
		COSCatalog catalog = getCatalog();
		if (catalog != null) {
			aa = catalog.cosGetField(DK_AA).asDictionary();
		}
		return (PDAdditionalActions) PDAdditionalActions.META.createFromCos(aa);
	}

	public List getAnnotations() {
		List result = new ArrayList();
		collectAnnotations(result);
		return result;
	}

	public PDApplicationData getApplicationData(String name) {
		COSDictionary pid = cosGetPieceInfoDict();
		if (pid == null) {
			return null;
		}
		COSName cosName = COSName.createUTF8(name);
		COSDictionary pi = pid.get(cosName).asDictionary();
		if (pi == null) {
			return null;
		}
		return (PDApplicationData) PDApplicationData.META.createFromCos(pi);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.component.IAttributeSupport#getAttribute(java.lang.
	 * Object)
	 */
	final public Object getAttribute(Object key) {
		return cosDoc.getAttribute(key);
	}

	public String getAuthor() {
		return getDocumentInfoString(COSInfoDict.DK_Author);
	}

	/**
	 * lookup the catalog dictionary in a document
	 * 
	 * @return the document catalog object
	 */
	public COSCatalog getCatalog() {
		return cosGetDoc().getCatalog();
	}

	public PDCollection getCollection() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		return (PDCollection) PDCollection.META.createFromCos(catalog
				.cosGetField(COSCatalog.DK_Collection));
	}

	public CDSDate getCreationDate() {
		if (getInfoDict() == null) {
			return null;
		}
		return getInfoDict().getCreationDate();
	}

	public String getCreationDateString() {
		return getDocumentInfoString(COSInfoDict.DK_CreationDate);
	}

	public String getCreator() {
		return getDocumentInfoString(COSInfoDict.DK_Creator);
	}

	public List<String> getDestinationNames() {
		List<String> destinationNames = new ArrayList<String>();
		COSDictionary dests = getCatalog().cosGetDests();
		if (dests != null) {
			for (Iterator i = dests.keySet().iterator(); i.hasNext();) {
				COSName key = (COSName) i.next();
				destinationNames.add(key.stringValue());
			}
		}
		COSDictionary names = getCatalog().cosGetNames();
		if (names != null) {
			COSDictionary destsDict = names.get(COSCatalog.DK_Dests)
					.asDictionary();
			if (destsDict != null) {
				CDSNameTreeNode destsTree = CDSNameTreeNode
						.createFromCos(destsDict);
				for (Iterator i = destsTree.iterator(); i.hasNext();) {
					CDSNameTreeEntry entry = (CDSNameTreeEntry) i.next();
					destinationNames.add(entry.getName().stringValue());
				}
			}
		}
		return destinationNames;
	}

	protected String getDocumentInfoString(COSName name) {
		COSInfoDict infoDict = cosGetDoc().getInfoDict();
		if (infoDict == null) {
			return null;
		}
		COSObject obj = infoDict.cosGetField(name);
		return obj.isNull() ? null : obj.stringValue();
	}

	/**
	 * Get the info dictionary containing metadata.
	 * 
	 * @return The info dictionary containing metadata.
	 */
	public COSInfoDict getInfoDict() {
		return cosGetDoc().getInfoDict();
	}

	public String getKeywords() {
		return getDocumentInfoString(COSInfoDict.DK_Keywords);
	}

	public ILocator getLocator() {
		return cosDoc.getLocator();
	}

	public String getMetadata() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}
		COSStream metadata = catalog.cosGetField(COSCatalog.DK_Metadata)
				.asStream();
		if (metadata == null) {
			return null;
		}
		byte[] bytes = metadata.getEncodedBytes();
		// TODO 2 review encoding result, should be UTF-8 in jpod context
		return new String(bytes);
	}

	public CDSDate getModDate() {
		if (getInfoDict() == null) {
			return null;
		}
		return getInfoDict().getModDate();
	}

	public String getModDateString() {
		return getDocumentInfoString(COSInfoDict.DK_ModDate);
	}

	public String getName() {
		return cosDoc.getName();
	}

	public PDAction getOpenAction() {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return null;
		}

		COSObject openAction = catalog.cosGetField(DK_OpenAction);
		if (openAction.isNull()) {
			return null;
		}

		return (PDAction) PDAction.META.createFromCos(openAction);
	}

	public PDOutline getOutline() {
		return (PDOutline) PDOutline.META.createFromCos(getCatalog()
				.cosGetOutline());
	}

	public PDOutputIntent getOutputIntent(COSName oiName) {
		List outputIntents = getOutputIntents();
		if (outputIntents != null) {
			for (Iterator iter = outputIntents.iterator(); iter.hasNext();) {
				PDOutputIntent element = (PDOutputIntent) iter.next();
				COSObject keys = element.cosGetField(PDOutputIntent.DK_S);
				if (keys != null) {
					if (keys.equals(oiName)) {
						return element;
					}
				}
			}
		}
		return null;
	}

	public List getOutputIntents() {
		COSArray oi = null;
		COSCatalog catalog = getCatalog();
		if (catalog != null) {
			oi = catalog.cosGetField(COSCatalog.DK_OutputIntents).asArray();
			if (oi != null) {
				List result = new ArrayList();
				Iterator i = oi.iterator();
				while (i.hasNext()) {
					COSBasedObject pdObject = PDOutputIntent.META
							.createFromCos((COSObject) i.next());
					if (pdObject != null) {
						result.add(pdObject);
					}
				}
				return result;
			}
		}
		return null;
	}

	/**
	 * return the documents root page tree
	 * 
	 * @return the root page tree of the document
	 */
	public PDPageTree getPageTree() {
		return (PDPageTree) PDPageNode.META.createFromCos(getCatalog()
				.cosGetField(COSCatalog.DK_Pages));
	}

	/**
	 * Shortcut to a signature dictionary in the document permissions
	 * dictionary. Valid keys are "DocMDP" and "UR". If the permissions
	 * dictionary doesn't exist, then null is returned. If no signature
	 * dictionary under the specified key exists, null is returned.
	 * 
	 * @param key
	 *            key which should be ether "DocMDP" or "UR"
	 * 
	 * @return returns a signature dictionary referenced by the specified key or
	 *         null if ether no permissions dictionary exists or no signature
	 *         dictionary to the key exists.
	 */
	public PDSignature getPermissions(COSName key) {
		COSDictionary perms = cosGetPermissionsDict();
		if (perms == null) {
			return null;
		}
		COSDictionary sigDict = perms.get(key).asDictionary();
		if (sigDict == null) {
			return null;
		}
		return (PDSignature) PDSignature.META.createFromCos(sigDict);
	}

	public String getProducer() {
		return getDocumentInfoString(COSInfoDict.DK_Producer);
	}

	public String getSubject() {
		return getDocumentInfoString(COSInfoDict.DK_Subject);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.pd.IAdditionalActionSupport#getSupportedTriggerEvents()
	 */
	public Set getSupportedTriggerEvents() {
		return CATALOG_ACTION_TRIGGERS;
	}

	public String getTitle() {
		return getDocumentInfoString(COSInfoDict.DK_Title);
	}

	public String getTrapped() {
		if (getInfoDict() == null) {
			return null;
		}
		return getInfoDict().getTrapped();
	}

	/**
	 * The write mode to be used when the document is written the next time. If
	 * defined this overrides any hint that is used when saving the document.
	 * The write mode is reset after each "save".
	 * 
	 * @return The write mode to be used when the document is written.
	 */
	public EnumWriteMode getWriteModeHint() {
		return cosDoc.getWriteModeHint();
	}

	/**
	 * initialize the object when created based on its cos representation
	 */
	protected void initializeFromCos() {
		//
	}

	/**
	 * initialize the object when created from scratch (in memory)
	 */
	protected void initializeFromScratch() {
		PDPageTree pageTree = (PDPageTree) PDPageTree.META.createNew();
		cosDoc.getCatalog().cosSetField(COSCatalog.DK_Pages,
				pageTree.cosGetObject());
	}

	/**
	 * <code>true</code> if the document has a flag to prefere incremental save.
	 * 
	 * @return <code>true</code> if the document should be saved in an
	 *         incremental way only.
	 */
	public boolean isAppendOnly() {
		if (getAcroForm() == null) {
			return false;
		}
		return getAcroForm().getSigFlags().isAppendOnly();
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
		return cosDoc.isAutoUpdate();
	}

	public boolean isDirty() {
		return cosDoc.isDirty();
	}

	/**
	 * Lookup if this document is encrypted, means it has a security handler.
	 * 
	 * @return true if the document has an {@link ISystemSecurityHandler}
	 */
	public boolean isEncrypted() {
		return cosDoc.isEncrypted();
	}

	public boolean isNew() {
		return cosDoc.isNew();
	}

	/**
	 * <code>true</code> if the document should be saved in an incremental way
	 * only. This is for example the case when the document contains digital
	 * signatures.
	 * 
	 * @return <code>true</code> if the document should be saved in an
	 *         incremental way only.
	 */
	public boolean isPreferIncrementalSave() {
		// we don't like full save of encrypted documents
		if (isEncrypted()) {
			return true;
		}
		// there is a hint with the form not to do a full save
		if (isAppendOnly()) {
			return true;
		}
		// sometimes the hint is not set - check harder
		return isSigned();
	}

	public boolean isReadOnly() {
		return cosDoc.isReadOnly();
	}

	/**
	 * <code>true</code> if the document contains digital signatures. This
	 * should be reflected in the SigFlags entry of the form - but who knows...
	 * 
	 * @return <code>true</code> if the document contains digital signatures.
	 * 
	 */
	public boolean isSigned() {
		if (getAcroForm() == null) {
			return false;
		}
		return getAcroForm().isSigned();
	}

	public COSObject lookupDestination(String name) {
		COSCatalog catalog = getCatalog();
		COSObject destination = null;
		COSDictionary dests = catalog.cosGetDests();
		if (dests != null) {
			destination = dests.get(COSName.createUTF8(name));
		}
		if (destination == null) {
			COSDictionary names = catalog.cosGetNames();
			if (names != null) {
				COSDictionary destsDict = names.get(COSCatalog.DK_Dests)
						.asDictionary();
				if (destsDict != null) {
					CDSNameTreeNode destsTree = CDSNameTreeNode
							.createFromCos(destsDict);
					destination = destsTree.get(COSString.create(name));
				}
			}
		}
		if (destination == null || destination.isNull()) {
			return null;
		}
		return destination;
	}

	public void removeApplicationData(String name) {
		COSDictionary pid = cosGetPieceInfoDict();
		if (pid == null) {
			return;
		}
		COSName cosName = COSName.createUTF8(name);
		pid.remove(cosName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.component.IAttributeSupport#removeAttribute(java.lang
	 * .Object)
	 */
	final public Object removeAttribute(Object key) {
		return cosDoc.removeAttribute(key);
	}

	public void restore(ILocator locator) throws IOException, COSLoadException {
		cosGetDoc().restore(locator);
	}

	public void save() throws IOException {
		save(getLocator(), null);
	}

	public void save(ILocator locator) throws IOException {
		save(locator, null);
	}

	public void save(ILocator locator, Map options) throws IOException {
		Map actualOptions = options;
		if (actualOptions == null) {
			actualOptions = new HashMap();
		}
		if (isPreferIncrementalSave()) {
			// request an incremental write if possible
			actualOptions.put(STDocument.OPTION_WRITEMODEHINT,
					EnumWriteMode.INCREMENTAL);
		} else {
			EnumWriteMode mode = (EnumWriteMode) actualOptions
					.get(STDocument.OPTION_WRITEMODEHINT);
			if (mode == null || mode.isUndefined()) {
				// we have no write mode predefined
				if ((locator != null) && (locator != getLocator())) {
					// this is "save as..."
					actualOptions.put(STDocument.OPTION_WRITEMODEHINT,
							EnumWriteMode.FULL);
				}
			}
		}
		cosDoc.save(locator, actualOptions);
	}

	/**
	 * Set AcroForm in PDF Document.
	 * 
	 */
	public void setAcroForm(PDAcroForm form) {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return;
		}
		catalog.setFieldObject(COSCatalog.DK_AcroForm, form);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.pd.IAdditionalActionSupport#setActions(de.intarsys.pdf
	 * .pd.PDAdditionalActions)
	 */
	public void setAdditionalActions(PDAdditionalActions actions) {
		COSCatalog catalog = getCatalog();
		if (catalog != null) {
			catalog.setFieldObject(DK_AA, actions);
		}
	}

	public void setApplicationData(String name, PDApplicationData data) {
		COSDictionary pid = cosGetPieceInfoDict();
		if (pid == null) {
			pid = COSDictionary.create();
			cosSetPieceInfoDict(pid);
		}
		COSName cosName = COSName.createUTF8(name);
		if (data == null) {
			pid.remove(cosName);
		} else {
			pid.put(cosName, data.cosGetDict());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.tools.component.IAttributeSupport#setAttribute(java.lang.
	 * Object, java.lang.Object)
	 */
	final public Object setAttribute(Object key, Object o) {
		return cosDoc.setAttribute(key, o);
	}

	public void setAuthor(String value) {
		setDocumentInfo(COSInfoDict.DK_Author, value);
	}

	/**
	 * @see PDDocument#isAutoUpdate()
	 * 
	 * @param autoUpdate
	 */
	public void setAutoUpdate(boolean autoUpdate) {
		cosDoc.setAutoUpdate(autoUpdate);
	}

	public void setCollection(PDCollection collection) {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return;
		}
		catalog.setFieldObject(COSCatalog.DK_Collection, collection);
	}

	public void setCreationDateString(String value) {
		setDocumentInfo(COSInfoDict.DK_CreationDate, value);
	}

	public void setCreator(String value) {
		setDocumentInfo(COSInfoDict.DK_Creator, value);
	}

	public void setDocumentInfo(COSName name, String value) {
		COSInfoDict infoDict = cosGetDoc().getInfoDict();
		if (infoDict == null) {
			infoDict = (COSInfoDict) COSInfoDict.META.createNew();
			cosGetDoc().setInfoDict(infoDict);
		}
		infoDict.setFieldString(name, value);
	}

	/**
	 * Set the info dictionary containing metadata.
	 * 
	 * @param infoDict
	 *            The info dictionary containing metadata.
	 */
	public void setInfoDict(COSInfoDict infoDict) {
		cosGetDoc().setInfoDict(infoDict);
	}

	public void setKeywords(String value) {
		setDocumentInfo(COSInfoDict.DK_Keywords, value);
	}

	public void setModDateString(String value) {
		setDocumentInfo(COSInfoDict.DK_ModDate, value);
	}

	public void setName(String name) {
		cosDoc.setName(name);
	}

	public void setOpenAction(PDAction newAction) {
		COSCatalog catalog = getCatalog();
		if (catalog == null) {
			return;
		}
		catalog.setFieldObject(DK_OpenAction, newAction);
	}

	public void setOutline(PDOutline outline) {
		getCatalog().cosSetOutline(outline.cosGetDict());
	}

	public void setPageTree(PDPageTree newTree) {
		getCatalog().setFieldObject(COSCatalog.DK_Pages, newTree);
	}

	public void setPermissions(COSName name, PDSignature signature) {
		COSDictionary perms = cosGetPermissionsDict();
		if (perms == null) {
			perms = COSDictionary.create();
			cosSetPermissionsDict(perms);
		}
		COSDictionary sigDict = signature.cosGetDict();
		sigDict.beIndirect();
		perms.put(name, sigDict);
	}

	public void setProducer(String value) {
		setDocumentInfo(COSInfoDict.DK_Producer, value);
	}

	public void setSubject(String value) {
		setDocumentInfo(COSInfoDict.DK_Subject, value);
	}

	public void setTitle(String value) {
		setDocumentInfo(COSInfoDict.DK_Title, value);
	}

	public void setTrapped(String value) {
		if (getInfoDict() == null) {
			return;
		}
		getInfoDict().setTrapped(value);
	}

	/**
	 * The write mode to be used when the document is written the next time. If
	 * defined this overrides any hint that is used when saving the document.
	 * The write mode is reset after each "save".
	 * 
	 * @param writeMode
	 *            The write mode to be used when the document is written.
	 */
	public void setWriteModeHint(EnumWriteMode writeMode) {
		cosDoc.setWriteModeHint(writeMode);
	}

}
