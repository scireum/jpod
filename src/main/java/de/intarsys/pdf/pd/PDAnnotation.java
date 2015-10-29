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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.intarsys.pdf.cds.CDSDate;
import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.cos.COSArray;
import de.intarsys.pdf.cos.COSBasedObject;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSStream;

/**
 * An Annotation is an abstract object that allows a PDF document to be changed
 * and extended in a variety of ways. Annotations are recorded with a page in
 * the PDF document. The behavior of an annotation depends on the subtype.
 */
public abstract class PDAnnotation extends PDObject implements
		IAdditionalActionSupport {
	/**
	 * The meta class implementation
	 */
	public static class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.intarsys.pdf.cos.COSBasedObject.MetaClass#doDetermineClass(de.intarsys.pdf.cos.COSObject)
		 */
		@Override
		protected COSBasedObject.MetaClass doDetermineClass(COSObject object) {
			if (!(object instanceof COSDictionary)) {
				return null;
			}
			COSName type = ((COSDictionary) object).get(DK_Subtype).asName();
			if (PDWidgetAnnotation.CN_Subtype_Widget.equals(type)) {
				return PDWidgetAnnotation.META;
			}
			if (PDLinkAnnotation.CN_Subtype_Link.equals(type)) {
				return PDLinkAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_Ink.equals(type)) {
				return PDInkAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_Square.equals(type)) {
				return PDSquareAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_Circle.equals(type)) {
				return PDCircleAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_Line.equals(type)) {
				return PDLineAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_Polygon.equals(type)) {
				return PDPolygonAnnotation.META;
			}
			if (PDMarkupAnnotation.CN_Subtype_PolyLine.equals(type)) {
				return PDPolylineAnnotation.META;
			}
			if (PDTextMarkupAnnotation.CN_Subtype_Highlight.equals(type)) {
				return PDHighlightAnnotation.META;
			}
			if (PDTextMarkupAnnotation.CN_Subtype_Underline.equals(type)) {
				return PDUnderlineAnnotation.META;
			}
			if (PDTextMarkupAnnotation.CN_Subtype_Squiggly.equals(type)) {
				return PDSquigglyAnnotation.META;
			}
			if (PDTextMarkupAnnotation.CN_Subtype_StrikeOut.equals(type)) {
				return PDStrikeOutAnnotation.META;
			}
			if (PDPopupAnnotation.CN_Subtype_Popup.equals(type)) {
				return PDPopupAnnotation.META;
			}
			if (PDTextAnnotation.CN_Subtype_Text.equals(type)) {
				return PDTextAnnotation.META;
			}
			if (PDFileAttachmentAnnotation.CN_Subtype_FileAttachment
					.equals(type)) {
				return PDFileAttachmentAnnotation.META;
			}
			if (PDStampAnnotation.CN_Subtype_Stamp.equals(type)) {
				return PDStampAnnotation.META;
			}
			return PDAnyAnnotation.META;
		}

		@Override
		public Class getRootClass() {
			return PDAnnotation.class;
		}
	}

	/** The meta class instance */
	public static final MetaClass META = new MetaClass(MetaClass.class
			.getDeclaringClass());

	public static final COSName DK_Contents = COSName.constant("Contents"); //$NON-NLS-1$

	public static final COSName DK_P = COSName.constant("P"); //$NON-NLS-1$

	public static final COSName DK_Rect = COSName.constant("Rect"); //$NON-NLS-1$

	public static final COSName DK_NM = COSName.constant("NM"); //$NON-NLS-1$

	public static final COSName DK_M = COSName.constant("M"); //$NON-NLS-1$

	/**
	 * The name for the F entry.
	 * <p>
	 * Currently:
	 * </p>
	 * <ul>
	 * <li>Bit 1: Invisible
	 * <li>Bit 2: Hidden
	 * <li>Bit 3: Print
	 * <li>Bit 4: NoZoom
	 * <li>Bit 5: NoRotate
	 * <li>Bit 6: NoView
	 * <li>Bit 7: ReadOnly
	 * <li>Bit 8: Locked
	 * <li>Bit 9: ToggleNoView
	 * </ul>
	 */
	public static final COSName DK_F = COSName.constant("F"); //$NON-NLS-1$

	public static final COSName DK_BS = COSName.constant("BS"); //$NON-NLS-1$

	public static final COSName DK_Border = COSName.constant("Border"); //$NON-NLS-1$

	public static final COSName DK_AP = COSName.constant("AP"); //$NON-NLS-1$

	public static final COSName DK_AS = COSName.constant("AS"); //$NON-NLS-1$

	public static final COSName DK_C = COSName.constant("C"); //$NON-NLS-1$

	public static final COSName DK_CA = COSName.constant("CA"); //$NON-NLS-1$//PDF v1.4 

	public static final COSName DK_IC = COSName.constant("IC"); //$NON-NLS-1$//PDF v1.4

	public static final COSName DK_A = COSName.constant("A"); //$NON-NLS-1$

	public static final COSName DK_StructParent = COSName
			.constant("StructParent"); //$NON-NLS-1$

	public static final COSName DK_OC = COSName.constant("OC"); //$NON-NLS-1$

	public static final COSName CN_Type_Annot = COSName.constant("Annot"); //$NON-NLS-1$

	/** supported additional action triggers */
	public static final Set ANNOTATION_ACTION_TRIGGERS;

	static {
		ANNOTATION_ACTION_TRIGGERS = new HashSet(11);
		ANNOTATION_ACTION_TRIGGERS.add("E"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("X"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("D"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("U"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("Fo"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("BI"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("PO"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("PC"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("PV"); //$NON-NLS-1$
		ANNOTATION_ACTION_TRIGGERS.add("PI"); //$NON-NLS-1$
	}

	/**
	 * Copy the receivers appearance dictionary.
	 * 
	 * @param appDict
	 *            THe appearance dictionary.
	 * 
	 * @return A copy of the appearance dictionary.
	 */
	protected static COSDictionary copyAppearanceDict(COSDictionary appDict) {
		if (appDict == null) {
			return null;
		}
		COSDictionary result = COSDictionary.create();
		for (Iterator i = appDict.entryIterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			COSName key = (COSName) entry.getKey();
			COSObject value = (COSObject) entry.getValue();
			if (value instanceof COSDictionary) {
				COSDictionary oldDict = (COSDictionary) value;
				value = copyAppearanceDictInner(oldDict);
			} else {
				if (value instanceof COSStream) {
					value = value.copyShallow();
				}
			}
			result.put(key, value);
		}
		return result;
	}

	/**
	 * When the appearance dictionary has multiple states, this methos copies
	 * the inner appearance dictionaries.
	 * 
	 * @param stateDict
	 *            An inner appearance dictionary.
	 * 
	 * @return A copy of the inner appearance dict.
	 */
	protected static COSDictionary copyAppearanceDictInner(
			COSDictionary stateDict) {
		COSDictionary result = COSDictionary.create();
		for (Iterator i = stateDict.entryIterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			COSName key = (COSName) entry.getKey();
			COSObject value = (COSObject) entry.getValue();
			if (value instanceof COSStream) {
				value = value.copyShallow();
			}
			result.put(key, value);
		}
		return result;
	}

	private AnnotationFlags flags;

	private PDBorderStyle cachedBorderStyle;

	private CDSRectangle cachedRectangle;

	/**
	 * Create the receiver class from an already defined {@link COSDictionary}.
	 * NEVER use the constructor directly.
	 * 
	 * @param object
	 *            the PDDocument containing the new object
	 */
	protected PDAnnotation(COSObject object) {
		super(object);
	}

	/**
	 * Add a {@link PDAction} to be executed when this is "activated".
	 * 
	 * @param newAction
	 *            The {@link PDAction} to be executed.
	 */
	public void addAction(PDAction newAction) {
		if (newAction != null) {
			if (getAction() == null) {
				setAction(newAction);
			} else {
				getAction().addNext(newAction);
			}
		}
	}

	/**
	 * The annotation flags in its integer representation.
	 * 
	 * @return The annotation flags in its integer representation.
	 */
	public int basicGetFlags() {
		return getFieldInt(DK_F, 0);
	}

	/**
	 * Assign the annotation flags in integer representation.
	 * 
	 * @param newFlags
	 *            The new annotation flags.
	 */
	public void basicSetFlags(int newFlags) {
		if (newFlags != 0) { // default
			cosSetField(DK_F, COSInteger.create(newFlags));
		} else {
			cosRemoveField(DK_F);
		}
	}

	/**
	 * <code>true</code> if this can receive the focus in an interactive
	 * viewer.
	 * 
	 * @return <code>true</code> if this can receive the focus in an
	 *         interactive viewer.
	 */
	public boolean canReceiveFocus() {
		return !isReadOnly() && !isHidden() && !isNoView() && !isInvisible()
				&& !isLocked();
	}

	/**
	 * Check the annotations internal constraints.
	 * 
	 */
	protected void checkRectangle(CDSRectangle rect) {
		float newWidth = rect.getWidth();
		float newHeight = rect.getHeight();
		if (newWidth <= 0 && newHeight <= 0) {
			newWidth = getDefaultWidth();
			newHeight = getDefaultHeight();
		}
		if (newWidth < getMinWidth()) {
			newWidth = getMinWidth();
		}
		if (newHeight < getMinHeight()) {
			newHeight = getMinHeight();
		}
		rect.resizeTo(newWidth, newHeight);
	}

	/**
	 * Create a copy of the receiver.
	 * 
	 * @return A copy of the receiver.
	 */
	public PDAnnotation copy() {
		// can not use copyNet as there are backward references.
		COSDictionary dict = (COSDictionary) cosGetDict().copyShallow();
		PDAnnotation copy = (PDAnnotation) PDAnnotation.META
				.createFromCos(dict);

		// kill page parent
		copy.cosRemoveField(DK_P);
		// kill form parent
		copy.cosRemoveField(PDAcroFormField.DK_Parent);
		// 
		copy.cosRemoveField(PDAcroFormField.DK_T);
		// 
		copy.cosRemoveField(PDAcroFormField.DK_TU);
		// kill modification
		copy.cosRemoveField(DK_M);
		// kill value from form field
		copy.cosRemoveField(PDAcroFormField.DK_V);

		// create new appearance
		COSDictionary newAppearance = copyAppearanceDict(cosGetField(DK_AP)
				.asDictionary());
		copy.cosSetField(DK_AP, newAppearance);
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#cosGetExpectedType()
	 */
	@Override
	protected COSName cosGetExpectedType() {
		return CN_Type_Annot;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
	public void dispose() {
		PDPage page = PDAnnotationTools.getPage(this);
		if (page != null) {
			page.removeAnnotation(this);
		}
	}

	/**
	 * The {@link PDAction} to be executed when this is "activated".
	 * 
	 * @return The {@link PDAction} to be executed when this is "activated".
	 */
	public PDAction getAction() {
		COSObject cosObject = cosGetField(DK_A);
		if (cosObject.isNull()) {
			return null;
		}
		return (PDAction) PDAction.META.createFromCos(cosObject);
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
	 * The visual appearance of this.
	 * 
	 * @return The {@link PDAction} to be executed when this is "activated".
	 */
	public PDAppearance getAppearance() {
		COSObject cosObject = cosGetField(DK_AP);
		if (cosObject.isNull()) {
			return null;
		}
		return (PDAppearance) PDAppearance.META.createFromCos(cosObject);
	}

	/**
	 * The visual appearance state of this. This state selects an appearance
	 * within its {@link PDAppearance}.
	 * 
	 * @return The visual appearance state of this.
	 */
	public COSName getAppearanceState() {
		return cosGetField(DK_AS).asName();
	}

	/**
	 * All defined appearance states in this.
	 * 
	 * @return All defined appearance states in this.
	 */
	public Set getAppearanceStates() {
		Set result = new HashSet();
		COSDictionary ap = cosGetField(DK_AP).asDictionary();
		if (ap != null) {
			COSDictionary states;
			states = ap.get(PDAppearance.DK_N).asDictionary();
			if (states != null) {
				result.addAll(states.keySet());
			}
			states = ap.get(PDAppearance.DK_D).asDictionary();
			if (states != null) {
				result.addAll(states.keySet());
			}
			states = ap.get(PDAppearance.DK_N).asDictionary();
			if (states != null) {
				result.addAll(states.keySet());
			}
		}
		return result;
	}

	/**
	 * The annotations border style.
	 * 
	 * @return The annotations border style.
	 */
	public PDBorderStyle getBorderStyle() {
		if (cachedBorderStyle == null) {
			COSObject cosObject = cosGetField(DK_BS);
			if (!cosObject.isNull()) {
				cachedBorderStyle = (PDBorderStyle) PDBorderStyle.META
						.createFromCos(cosObject);
			}
		}
		return cachedBorderStyle;
	}

	/**
	 * The name of the border style.
	 * 
	 * @return The name of the border style.
	 */
	public COSName getBorderStyleName() {
		PDBorderStyle bs = getBorderStyle();
		if (bs == null) {
			return PDBorderStyle.DK_S;
		}
		return bs.getStyle();
	}

	/**
	 * The width of the annotations border.
	 * 
	 * @return The width of the annotations border.
	 */
	public float getBorderStyleWidth() {
		PDBorderStyle bs = getBorderStyle();
		if (bs == null) {
			return 1;
		}
		return bs.getWidth();
	}

	/**
	 * The color values for this.
	 * 
	 * @return The color values for this.
	 */
	public float[] getColor() {
		return getFieldFixedArray(DK_C, null);
	}

	/**
	 * The contents {@link String} for this.
	 * 
	 * @return The contents {@link String} for this.
	 */
	public String getContents() {
		return getFieldMLString(DK_Contents, ""); //$NON-NLS-1$
	}

	/**
	 * The default height this annotation likes to have.
	 * 
	 * @return The default height this annotation likes to have.
	 */
	public float getDefaultHeight() {
		return 1;
	}

	/**
	 * The default width this annotation likes to have.
	 * 
	 * @return The default width this annotation likes to have.
	 */
	public float getDefaultWidth() {
		return 1;
	}

	/**
	 * The annotation flags.
	 * 
	 * @return The annotation flags.
	 */
	public AnnotationFlags getFlags() {
		if (flags == null) {
			flags = new AnnotationFlags(this);
		}
		return flags;
	}

	/**
	 * The minimum height this annotation allows.
	 * 
	 * @return The minimum height this annotation allows.
	 */
	public float getMinHeight() {
		// assume we have a rectangle appearance
		float width = getBorderStyleWidth();
		return 2 * width;
	}

	/**
	 * The minimum width this annotation allows.
	 * 
	 * @return The minimum width this annotation allows.
	 */
	public float getMinWidth() {
		// assume we have a rectangle appearance
		float width = getBorderStyleWidth();
		return 2 * width;
	}

	/**
	 * The last modification date.
	 * 
	 * @return The last modification date.
	 */
	public CDSDate getModified() {
		return CDSDate.createFromCOS(cosGetField(DK_M).asString());
	}

	/**
	 * The annotations name.
	 * 
	 * @return The annotations name.
	 */
	public String getName() {
		return getFieldString(DK_NM, null);
	}

	/**
	 * Convenience method to access a normalized copy of the receivers
	 * rectangle.
	 * 
	 */
	public CDSRectangle getNormalizedRectangle() {
		CDSRectangle tempRect = getRectangle();
		if (tempRect == null) {
			return null;
		}
		return tempRect.copy().normalize();
	}

	/**
	 * Try to access the {@link PDPage} we are on. This may return null, as this
	 * reference is not required by the spec.
	 * 
	 * @return Try to access the {@link PDPage} we are on.
	 */
	public PDPage getPage() {
		COSObject cosObject = cosGetField(DK_P);
		return (PDPage) PDPageNode.META.createFromCos(cosObject);
	}

	/**
	 * The {@link CDSRectangle} for the visual appearance of this.
	 * 
	 * @return The {@link CDSRectangle} for the visual appearance of this.
	 */
	public CDSRectangle getRectangle() {
		if (cachedRectangle == null) {
			COSArray array = cosGetField(DK_Rect).asArray();
			if (array == null) {
				return null;
			}
			cachedRectangle = CDSRectangle.createFromCOS(array);
		}
		return cachedRectangle;
	}

	protected Integer getStructParent() {
		COSInteger cosObject = cosGetField(DK_StructParent).asInteger();
		if (cosObject != null) {
			new Integer(cosObject.intValue());
		}
		return null;
	}

	/**
	 * A {@link String} representation of this.
	 * 
	 * @return A {@link String} representation of this.
	 */
	abstract public String getSubtypeLabel();

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.IAdditionalActionSupport#getSupportedTriggerEvents()
	 */
	public Set getSupportedTriggerEvents() {
		return ANNOTATION_ACTION_TRIGGERS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#initializeFromScratch()
	 */
	@Override
	protected void initializeFromScratch() {
		super.initializeFromScratch();
		CDSRectangle initialRect = new CDSRectangle(0, 0, 0, 0);
		setRectangle(initialRect);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
	 */
	@Override
	public void invalidateCaches() {
		super.invalidateCaches();
		cachedBorderStyle = null;
		cachedRectangle = null;
	}

	/**
	 * Convenience method to access "Hidden" flag.
	 */
	public boolean isHidden() {
		return getFlags().isHidden();
	}

	/**
	 * Convenience method to access "Invisible" flag.
	 */
	public boolean isInvisible() {
		return getFlags().isInvisible();
	}

	/**
	 * Convenience method to access "Locked" flag.
	 */
	public boolean isLocked() {
		return getFlags().isLocked();
	}

	/**
	 * <code>true</code> if this is a {@link PDMarkupAnnotation}.
	 * 
	 * @return <code>true</code> if this is a {@link PDMarkupAnnotation}.
	 */
	public boolean isMarkupAnnotation() {
		return false;
	}

	/**
	 * Convenience method to access "NoRotate" flag.
	 */
	public boolean isNoRotate() {
		return getFlags().isNoRotate();
	}

	/**
	 * Convenience method to access "NoView" flag.
	 */
	public boolean isNoView() {
		return getFlags().isNoView();
	}

	/**
	 * Convenience method to access "NoZoom" flag.
	 */
	public boolean isNoZoom() {
		return getFlags().isNoZoom();
	}

	/**
	 * Convenience method to access "Print" flag.
	 */
	public boolean isPrint() {
		return getFlags().isPrint();
	}

	/**
	 * Convenience method to access "ReadOnly" flag.
	 */
	public boolean isReadOnly() {
		return getFlags().isReadOnly();
	}

	/**
	 * Convenience method to access "ToggleNoView" flag.
	 */
	public boolean isToggleNoView() {
		return getFlags().isToggleNoView();
	}

	/**
	 * <code>true</code> if this is a {@link PDWidgetAnnotation}.
	 * 
	 * @return <code>true</code> if this is a {@link PDWidgetAnnotation}.
	 */
	public boolean isWidgetAnnotation() {
		return false;
	}

	/**
	 * Set the {@link PDAction} for the "activation" of this.
	 * 
	 * @param newAction
	 *            Set the {@link PDAction} for the "activation" of this.
	 */
	public void setAction(PDAction newAction) {
		setFieldObject(DK_A, newAction);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.IAdditionalActionSupport#setActions(de.intarsys.pdf.pd.PDAdditionalActions)
	 */
	public void setAdditionalActions(PDAdditionalActions actions) {
		setFieldObject(DK_AA, actions);
	}

	/**
	 * Assign a {@link PDAppearance} to this.
	 * 
	 * @param newAppearance
	 *            The new {@link PDAppearance}.
	 */
	public void setAppearance(PDAppearance newAppearance) {
		setFieldObject(DK_AP, newAppearance);
	}

	/**
	 * Select a concrete {@link PDForm} from the {@link PDAppearance}.
	 * 
	 * @param newAppearanceState
	 *            The new state name.
	 */
	public void setAppearanceState(COSName newAppearanceState) {
		cosSetField(DK_AS, newAppearanceState);
	}

	/**
	 * Assign a {@link PDBorderStyle}.
	 * 
	 * @param newBorderStyle
	 *            The new {@link PDBorderStyle}
	 */
	public void setBorderStyle(PDBorderStyle newBorderStyle) {
		setFieldObject(DK_BS, newBorderStyle);
	}

	/**
	 * Assign a border style name.
	 * 
	 * @param newStyle
	 *            The new border style name.
	 */
	public void setBorderStyleName(COSName newStyle) {
		PDBorderStyle bs = getBorderStyle();
		if (bs == null) {
			bs = (PDBorderStyle) PDBorderStyle.META.createNew();
			setBorderStyle(bs);
		}
		bs.setStyle(newStyle);
	}

	/**
	 * Assign a border width.
	 * 
	 * @param newWidth
	 *            The new Border width.
	 */
	public void setBorderStyleWidth(float newWidth) {
		PDBorderStyle bs = getBorderStyle();
		if (bs == null) {
			bs = (PDBorderStyle) PDBorderStyle.META.createNew();
			setBorderStyle(bs);
		}
		bs.setWidth(newWidth);
	}

	/**
	 * Assign a color.
	 * 
	 * @param color
	 *            The new color values.
	 */
	public void setColor(float[] color) {
		setFieldFixedArray(DK_C, color);
	}

	/**
	 * Assign a content {@link String}.
	 * 
	 * @param newContents
	 *            The new content {@link String}.
	 */
	public void setContents(String newContents) {
		setFieldString(DK_Contents, newContents);
	}

	/**
	 * Convenience method to access "Hidden" flag.
	 */
	public void setHidden(boolean f) {
		getFlags().setHidden(f);
	}

	/**
	 * Convenience method to access "Invisible" flag.
	 */
	public void setInvisible(boolean f) {
		getFlags().setInvisible(f);
	}

	/**
	 * Convenience method to access "Locked" flag.
	 */
	public void setLocked(boolean f) {
		getFlags().setLocked(f);
	}

	protected void setModified(String newModified) {
		setFieldString(DK_M, newModified);
	}

	/**
	 * Assign a name.
	 * 
	 * @param newName
	 *            The new name.
	 */
	public void setName(String newName) {
		setFieldString(DK_NM, newName);
	}

	/**
	 * Convenience method to access "NoRotate" flag.
	 */
	public void setNoRotate(boolean f) {
		getFlags().setNoRotate(f);
	}

	/**
	 * Convenience method to access "NoView" flag.
	 */
	public void setNoView(boolean f) {
		getFlags().setNoView(f);
	}

	/**
	 * Convenience method to access "NoZoom" flag.
	 */
	public void setNoZoom(boolean f) {
		getFlags().setNoZoom(f);
	}

	protected void setPage(PDPage newPage) {
		setFieldObject(DK_P, newPage);
	}

	/**
	 * Convenience method to access "Print" flag.
	 */
	public void setPrint(boolean f) {
		getFlags().setPrint(f);
	}

	/**
	 * Convenience method to access "ReadOnly" flag.
	 */
	public void setReadOnly(boolean readOnly) {
		getFlags().setReadOnly(readOnly);
	}

	/**
	 * Assign a {@link CDSRectangle} for the visual appearance of this.
	 * 
	 * @param newRectangle
	 *            Assign a {@link CDSRectangle} for the visual appearance of
	 *            this.
	 */
	public void setRectangle(CDSRectangle newRectangle) {
		checkRectangle(newRectangle);
		COSObject oldValue = setFieldObject(DK_Rect, newRectangle);
		CDSRectangle oldRectangle = CDSRectangle.createFromCOS(oldValue
				.asArray());
		if (oldRectangle != null) {
			updateStateRectangle(oldRectangle, newRectangle);
		}
	}

	protected void setStructParent(Integer newStructParent) {
		if (newStructParent != null) {
			setFieldInt(DK_StructParent, newStructParent.intValue());
		} else {
			cosRemoveField(DK_StructParent);
		}
	}

	/**
	 * Convenience method to access "ToggleNoView" flag.
	 */
	public void setToggleNoView(boolean f) {
		getFlags().setToggleNoView(f);
	}

	/**
	 * <code>true</code> if print flag can be selected/changed for this kind
	 * of annotation.
	 * 
	 * @return <code>true</code> if print flag can be selected/changed for
	 *         this kind of annotation.
	 */
	public boolean supportsPrint() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.pd.PDObject#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Annotation: "); //$NON-NLS-1$
		sb.append(cosGetSubtype());
		sb.append(" "); //$NON-NLS-1$
		if (isWidgetAnnotation()) {
			PDAcroFormField field = (PDAcroFormField) PDAcroFormField.META
					.createFromCos(cosGetDict());
			sb.append(field.getQualifiedName());
			sb.append(" "); //$NON-NLS-1$
		}
		sb.append(super.toString());
		return sb.toString();
	}

	/**
	 * Assign the current date as the date of last modification.
	 */
	public void touch() {
		setFieldObject(PDAnnotation.DK_M, new CDSDate());
	}

	/**
	 * Some annotations need to update internal state after changing the
	 * rectangle.
	 */
	protected void updateStateRectangle(CDSRectangle oldRectangle,
			CDSRectangle newRectangle) {
		// 
	}
}
