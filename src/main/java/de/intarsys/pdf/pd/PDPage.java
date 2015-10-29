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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.IContentStreamProvider;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * A single concrete page in a PDF document.
 */
public class PDPage extends PDPageNode implements IAdditionalActionSupport,
		IContentStreamProvider {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDPageNode.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		protected COSBasedObject doCreateCOSBasedObject(COSObject object) {
			return new PDPage(object);
		}
	}

	public static String COPY_SUFFIX = "_copy"; //$NON-NLS-1$

	public static final COSName DK_Annots = COSName.constant("Annots"); //$NON-NLS-1$

	public static final COSName DK_Contents = COSName.constant("Contents"); //$NON-NLS-1$

	public static final COSName DK_CropBox = COSName.constant("CropBox"); //$NON-NLS-1$

	public static final COSName DK_MediaBox = COSName.constant("MediaBox"); //$NON-NLS-1$

	public static final COSName DK_Metadata = COSName.constant("Metadata"); //$NON-NLS-1$

	public static final COSName DK_PieceInfo = COSName.constant("PieceInfo"); //$NON-NLS-1$

	public static final COSName DK_Resources = COSName.constant("Resources"); //$NON-NLS-1$

	public static final COSName DK_TemplateInstantiated = COSName
			.constant("TemplateInstantiated"); //$NON-NLS-1$

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	private static final List NULL = new ArrayList();

	/** supported additional action triggers */
	public static final Set PAGE_ACTION_TRIGGERS;

	static {
		PAGE_ACTION_TRIGGERS = new HashSet(3);
		PAGE_ACTION_TRIGGERS.add("O"); //$NON-NLS-1$
		PAGE_ACTION_TRIGGERS.add("C"); //$NON-NLS-1$
	}

	private SoftReference cachedAnnotations = null;

	private SoftReference cachedContentStream = null;

	/**
	 * Create the receiver class from an already defined {@link COSDictionary}.
	 * NEVER use the constructor directly.
	 * 
	 * @param object
	 *            the PDDocument containing the new object
	 */
	protected PDPage(COSObject object) {
		super(object);
	}

	/**
	 * Add a {@link PDAnnotation} to the collection of annotations on the
	 * receiver page.
	 * 
	 * @param annot
	 *            The PDAnnotation to add to the page.
	 */
	public void addAnnotation(PDAnnotation annot) {
		COSArray cosAnnots = cosGetField(DK_Annots).asArray();
		if (cosAnnots == null) {
			cosAnnots = COSArray.create();
			cosAnnots.beIndirect();
			cosSetField(DK_Annots, cosAnnots);
			cachedAnnotations = null;
		}
		cosAnnots.add(annot.cosGetDict());
		annot.setPage(this);
	}

	/**
	 * Add a {@link CSContent} stream to this.
	 * 
	 * @param contentStream
	 *            The new {@link CSContent}
	 */
	public void addContentStream(CSContent contentStream) {
		cosAddContents(contentStream.createStream());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#collectAnnotations(java.util.List)
	 */
	@Override
	protected void collectAnnotations(List annotations) {
		if (getAnnotations() != null) {
			annotations.addAll(getAnnotations());
		}
	}

	/**
	 * Append {@link COSStream} to the pages content
	 * 
	 * @param content
	 *            The {@link COSStream} to add to the page
	 */
	public void cosAddContents(COSStream content) {
		COSObject contents = cosGetField(DK_Contents);
		if (contents.isNull()) {
			cosSetField(DK_Contents, content);
		}
		if (contents instanceof COSStream) {
			COSArray array = COSArray.create(2);
			array.add(contents);
			array.add(content);
			cosSetField(DK_Contents, array);
		}
		if (contents instanceof COSArray) {
			COSArray array = (COSArray) contents;
			array.add(content);
		}
	}

	/**
	 * The /Contents entry
	 * 
	 * @return The /Contents entry
	 */
	public COSObject cosGetContents() {
		return cosGetField(DK_Contents);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Page;
	}

	/**
	 * The piece info dictionary of the document.
	 * 
	 * @return The piece info dictionary of the document.
	 */
	public COSDictionary cosGetPieceInfo() {
		return cosGetField(DK_PieceInfo).asDictionary();
	}

	public COSName cosGetTemplateInstantiated() {
		return cosGetField(DK_TemplateInstantiated).asName();
	}

	/**
	 * Prepend contents to the pages content.
	 * 
	 * @param content
	 *            The {@link COSStream} to add to the page
	 */
	public void cosPrependContents(COSStream content) {
		COSObject contents = cosGetField(DK_Contents);
		if (contents.isNull()) {
			cosSetField(DK_Contents, content);
		}
		if (contents instanceof COSStream) {
			COSArray array = COSArray.create(2);
			array.add(content);
			array.add(contents);
			cosSetField(DK_Contents, array);
		}
		if (contents instanceof COSArray) {
			COSArray array = (COSArray) contents;
			array.add(0, content);
		}
	}

	/**
	 * Set the /Contents for the page
	 * 
	 * @param content
	 *            the stream defining the page content
	 * 
	 * @return The /Contents entry previously associated with this.
	 */
	public COSObject cosSetContents(COSObject content) {
		return cosSetField(DK_Contents, content);
	}

	/**
	 * Set the piece info dictionary of the document.
	 * 
	 * @param dict
	 *            The piece info dictionary of the document.
	 * 
	 * @return The /PieceInfo entry previously associated with this.
	 */
	public COSDictionary cosSetPieceInfo(COSDictionary dict) {
		if (dict != null) {
			dict.beIndirect();
		}
		return cosSetField(DK_PieceInfo, dict).asDictionary();
	}

	public void cosSetTemplateInstantiated(COSName templateName) {
		cosSetField(DK_TemplateInstantiated, templateName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#dispose()
	 */
	@Override
	public void dispose() {
		// todo 1 too much logic here...
		if (getAnnotations() != null) {
			Iterator iter = getAnnotations().iterator();
			while (iter.hasNext()) {
				PDAnnotation annotation = (PDAnnotation) iter.next();
				annotation.dispose();
			}
		}
		super.dispose();
	}

	/**
	 * A collection of all {@link PDAcroFormField}s that have
	 * {@link PDAnnotation}s on the receiver that are children of
	 * <code>parent</code>.
	 * 
	 * @param parent
	 *            The parent {@link PDAcroForm} or {@link PDAcroFormField}.
	 * @param result
	 *            The collection of {@link PDAnnotation}s collected so far.
	 */
	protected void getAcroFormFields(PDObject parent, List result) {
		List elements = parent.getGenericChildren();
		List annotations = getAnnotations();
		if (elements == null) {
			return;
		}
		for (Iterator i = elements.iterator(); i.hasNext();) {
			PDObject object = (PDObject) i.next();
			if (annotations.contains(object)) {
				result.add(object);
			}
			getAcroFormFields(object, result);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.IAdditionalActionSupport#getAdditionalActions()
	 */
	public PDAdditionalActions getAdditionalActions() {
		COSDictionary field = cosGetField(DK_AA).asDictionary();
		return (PDAdditionalActions) PDAdditionalActions.META
				.createFromCos(field);
	}

	/**
	 * Get a list of all {@link PDAnnotation} objects that are referenced in
	 * this page.
	 * 
	 * @return A list of all {@link PDAnnotation} objects that are referenced in
	 *         this page or null if none exist.
	 */
	public List<PDAnnotation> getAnnotations() {
		List annotations = null;
		if (cachedAnnotations != null) {
			annotations = (List) cachedAnnotations.get();
		}
		if (annotations == null) {
			annotations = getPDObjects(DK_Annots, PDAnnotation.META, true);
			if (annotations == null) {
				// avoid continued lookup when no annotations found
				annotations = NULL;
			}
			cachedAnnotations = new SoftReference(annotations);
		}
		if (annotations == NULL) {
			return null;
		}
		return annotations;
	}

	/**
	 * The {@link PDApplicationData} associated with <code>name</code> on the
	 * page.
	 * 
	 * @param name
	 *            The name of the {@link PDApplicationData} to lookup.
	 * @return The {@link PDApplicationData} associated with <code>name</code>
	 *         on the page.
	 */
	public PDApplicationData getApplicationData(String name) {
		COSDictionary pid = cosGetPieceInfo();
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

	protected int getContentsSize() {
		COSObject contents = cosGetContents();
		if (contents.isNull()) {
			return 0;
		}
		if (contents instanceof COSStream) {
			return 1;
		}
		return ((COSArray) contents).size();
	}

	/**
	 * The {@link CSContent} defining the visual content of the page.
	 * 
	 * @return The {@link CSContent} defining the visual content of the page.
	 */
	public CSContent getContentStream() {
		CSContent contentStream = null;
		if (cachedContentStream != null) {
			contentStream = (CSContent) cachedContentStream.get();
		}
		if (contentStream == null) {
			COSObject contents = cosGetContents();
			if (!contents.isNull()) {
				if (contents instanceof COSStream) {
					contentStream = CSContent
							.createFromCos(contents.asStream());
				} else {
					contentStream = CSContent.createFromCos(contents.asArray());
				}
				// just to be sure we are not registered before (soft ref!)
				contents.removeObjectListener(this);
				contents.addObjectListener(this);
				// todo add listener to content streams in array...
				cachedContentStream = new SoftReference(contentStream);
			}
		}
		return contentStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getCount()
	 */
	@Override
	public int getCount() {
		return 1;
	}

	/**
	 * @return The first {@link PDAnnotation} on the page or null
	 */
	@Override
	public PDAnnotation getFirstAnnotation() {
		if (getAnnotations() == null) {
			return null;
		}
		if (getAnnotations().size() == 0) {
			return null;
		}
		return getAnnotations().get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getFirst()
	 */
	@Override
	public PDPageNode getFirstNode() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getFirstPage()
	 */
	@Override
	public PDPage getFirstPage() {
		return this;
	}

	/**
	 * @return The last {@link PDAnnotation} on the page or null
	 */
	@Override
	public PDAnnotation getLastAnnotation() {
		if (getAnnotations() == null) {
			return null;
		}
		int size = getAnnotations().size();
		if (size == 0) {
			return null;
		}
		return getAnnotations().get(size - 1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getLast()
	 */
	@Override
	public PDPageNode getLastNode() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getLastPage()
	 */
	@Override
	public PDPage getLastPage() {
		return this;
	}

	/**
	 * The {@link PDAnnotation} following the given {@link PDAnnotation} annot
	 * or null, if <code>annot</code> was the last one in the list or does't
	 * exist on this page.
	 * 
	 * @param annot
	 *            a PDAnnotation
	 * @return a PDAnnotation or null
	 */
	public PDAnnotation getNextAnnotation(PDAnnotation annot) {
		if (getAnnotations() == null) {
			return null;
		}
		int resultIndex = getAnnotations().indexOf(annot);
		if (resultIndex == -1) {
			return null;
		}
		if ((resultIndex + 1) < getAnnotations().size()) {
			return getAnnotations().get(resultIndex + 1);
		}
		return null;
	}

	/**
	 * The next page after the receiver.
	 * 
	 * @return The next page after the receiver.
	 */
	public PDPage getNextPage() {
		PDPageTree tmpParent = getParent();
		if (tmpParent == null) {
			return null;
		}
		return tmpParent.getNextPage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#getPageAt(int)
	 */
	@Override
	public PDPage getPageAt(int index) {
		if (index == 0) {
			return this;
		}
		return super.getPageAt(index);
	}

	/**
	 * Returns the {@link PDAnnotation} preceding the given {@link PDAnnotation}
	 * annot or null, if annot was the first one in the list or does't exist on
	 * this page.
	 * 
	 * @param annot
	 *            a PDAnnotation
	 * @return a PDAnnotation or null
	 */
	public PDAnnotation getPreviousAnnotation(PDAnnotation annot) {
		if (getAnnotations() == null) {
			return null;
		}
		int resultIndex = getAnnotations().indexOf(annot);
		if (resultIndex == -1) {
			return null;
		}
		if ((resultIndex - 1) >= 0) {
			return getAnnotations().get(resultIndex - 1);
		}
		return null;
	}

	/**
	 * Get the previous page before the receiver.
	 * 
	 * @return Get the previous page before the receiver.
	 */
	public PDPage getPreviousPage() {
		PDPageTree tmpParent = getParent();
		if (tmpParent == null) {
			return null;
		}
		return tmpParent.getPreviousPage(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.IResourcesProvider#getResources()
	 */
	public PDResources getResources() {
		COSDictionary dict = cosGetFieldInheritable(DK_Resources)
				.asDictionary();
		return (PDResources) PDResources.META.createFromCos(dict);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.pd.IAdditionalActionSupport#getSupportedTriggerEvents()
	 */
	public Set getSupportedTriggerEvents() {
		return PAGE_ACTION_TRIGGERS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDComplexBase#initializeFromScratch()
	 */
	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		// todo 3 get default paper size from environment
		setMediaBox(new CDSRectangle(CDSRectangle.SIZE_A4));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#invalidateCaches()
	 */
	@Override
	public void invalidateCaches() {
		super.invalidateCaches();
		cachedAnnotations = null;
		cachedContentStream = null;
		COSObject cosAnnotations = cosGetField(DK_Annots);
		cosAnnotations.removeObjectListener(this);
		COSObject cosContents = cosGetField(DK_Contents);
		cosContents.removeObjectListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#isPage()
	 */
	@Override
	public boolean isPage() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDPageNode#isValid()
	 */
	@Override
	public boolean isValid() {
		PDPageTree tempParent = getParent();
		if (tempParent == null) {
			return false;
		}
		return tempParent.isValid();
	}

	/**
	 * Prepend a {@link CSContent} stream to this.
	 * 
	 * @param contentStream
	 *            The new {@link CSContent}
	 */
	public void prependContentStream(CSContent contentStream) {
		cosPrependContents(contentStream.createStream());
	}

	/**
	 * Remove a {@link PDAnnotation} from the page.
	 * 
	 * @param annot
	 *            The {@link PDAnnotation} to remove from the page.
	 */
	public void removeAnnotation(PDAnnotation annot) {
		COSArray cosAnnots = cosGetField(DK_Annots).asArray();
		if (cosAnnots != null) {
			cosAnnots.remove(annot.cosGetDict());
			if (cosAnnots.isEmpty()) {
				cosRemoveField(DK_Annots);
			}
		}
	}

	/**
	 * Remove the {@link PDApplicationData} associated with <code>name</code>
	 * from this page.
	 * 
	 * @param name
	 *            The name of the application data object to be removed.
	 */
	public void removeApplicationData(String name) {
		COSDictionary pid = cosGetPieceInfo();
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
	 * de.intarsys.pdf.pd.IAdditionalActionSupport#setActions(de.intarsys.pdf
	 * .pd.PDAdditionalActions)
	 */
	public void setAdditionalActions(PDAdditionalActions actions) {
		setFieldObject(DK_AA, actions);
	}

	/**
	 * Associate a {@link PDApplicationData} instance with this using
	 * <code>name</code>.
	 * 
	 * @param name
	 *            The name for the {@link PDApplicationData} instance within
	 *            this.
	 * @param data
	 *            The {@link PDApplicationData} instance.
	 */
	public void setApplicationData(String name, PDApplicationData data) {
		COSDictionary pid = cosGetPieceInfo();
		if (pid == null) {
			pid = COSDictionary.create();
			cosSetPieceInfo(pid);
		}
		COSName cosName = COSName.createUTF8(name);
		pid.put(cosName, data.cosGetDict());
	}

	/**
	 * Assign a new visual appearance to the page.
	 * 
	 * @param contentStream
	 *            The new visual appearance.
	 */
	public void setContentStream(CSContent contentStream) {
		if (contentStream != null) {
			cosSetContents(contentStream.createStream());
		} else {
			cosSetContents(null);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.intarsys.pdf.pd.IResourcesProvider#setResources(de.intarsys.pdf.pd
	 * .PDResources)
	 */
	public void setResources(PDResources resources) {
		cosSetField(DK_Resources, resources.cosGetDict());
	}
}
