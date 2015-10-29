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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSString;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontTools;
import de.intarsys.pdf.font.PDFontType1;

/**
 * Abstract superclass to factor out commons between {@link PDAcroForm} and
 * {@link PDAcroFormField}.
 * 
 */
abstract public class PDAcroFormNode extends PDObject {
	/**
	 * The meta class implementation
	 */
	static public abstract class MetaClass extends PDObject.MetaClass {
		protected MetaClass(Class instanceClass) {
			super(instanceClass);
		}

		@Override
		public Class getRootClass() {
			return PDAcroFormNode.class;
		}
	}

	/** The name for the Encoding. */
	static public final COSName DK_Encoding = COSName.constant("Encoding");

	/** The name for the default resources. */
	static public final COSName DK_DR = COSName.constant("DR");

	/** The name for the fonts. */
	static public final COSName DK_Font = COSName.constant("Font");

	/** The name for the DefaultAppereance entry. */
	static public final COSName DK_DA = COSName.constant("DA");

	/** The name for the Quadding entry. */
	static public final COSName DK_Q = COSName.constant("Q");

	/**
	 * Create a canonical form of the field name.
	 * 
	 * @param name
	 *            The field name to be converted.
	 * @return The canonical for of the field name.
	 */
	static public String canonicalize(String name) {
		return name.toLowerCase();
	}

	private CSContent defaultAppearanceContent;

	private DefaultAppearance defaultAppearance;

	protected PDAcroFormNode(COSObject object) {
		super(object);
	}

	/**
	 * Insert a {@link PDAcroFormField} in the receiver node.
	 * 
	 * @param field
	 *            The field to be inserted.
	 */
	abstract public void addField(PDAcroFormField field);

	protected PDAcroFormField basicGetField(String name, boolean canonical) {
		List children = getGenericChildren();
		if ((children == null) || (name == null)) {
			return null;
		}
		String childName = null;
		String childPath = null;
		int separatorIndex = name.indexOf('.');
		if (separatorIndex < 0) {
			childName = name;
		} else {
			childName = name.substring(0, separatorIndex);
			childPath = name.substring(separatorIndex + 1);
		}
		for (Iterator i = children.iterator(); i.hasNext();) {
			PDAcroFormField field = (PDAcroFormField) i.next();
			String fieldName = field.getLocalName(canonical);
			if (childName.equals(fieldName)) {
				if (childPath == null) {
					return field;
				} else {
					return field.basicGetField(childPath, canonical);
				}
			} else if (fieldName == null) {
				// must descend when /T is undefined ?
				PDAcroFormField child = field.basicGetField(name, canonical);
				if (child != null) {
					return child;
				}
			}
		}
		return null;
	}

	/**
	 * The list of all final nodes (fields) within this node.
	 * 
	 * @return The list of all final nodes (fields) within this node.
	 */
	public List collectLeafFields() {
		return collectLeafFields(new ArrayList());
	}

	protected List collectLeafFields(List result) {
		for (Iterator i = getGenericChildren().iterator(); i.hasNext();) {
			PDAcroFormField child = (PDAcroFormField) i.next();
			child.collectLeafFields(result);
		}
		return result;
	}

	/**
	 * The {@link PDAcroForm} for this node.
	 * 
	 * @return The {@link PDAcroForm} for this node.
	 */
	abstract public PDAcroForm getAcroForm();

	/**
	 * Get an annotation from the tree beyond <code>this</code> identified by
	 * <code>index</code>.
	 * 
	 * @return An annotation from the tree beyond <code>this</code> identified
	 *         by <code>index</code>.
	 */
	public PDAnnotation getAnnotation(int index) {
		return (PDAnnotation) getAnnotations().get(index);
	}

	/**
	 * A list of all annotations in the tree beyond <code>this</code>.
	 * 
	 * @return A list of all annotations in the tree beyond <code>this</code>.
	 */
	public List getAnnotations() {
		// todo 2 caching?
		return getAnnotations(new ArrayList());
	}

	/**
	 * Recursively collect all annotations in the tree beyond <code>this</code>.
	 * 
	 * @param annotations
	 *            The annotations collected so far.
	 * 
	 * @return The enhanced list of annotations for this subtree.
	 */
	protected List getAnnotations(List annotations) {
		for (Iterator i = getGenericChildren().iterator(); i.hasNext();) {
			PDAcroFormNode kid = (PDAcroFormNode) i.next();
			kid.getAnnotations(annotations);
		}
		return annotations;
	}

	protected DefaultAppearance getDefaultAppearance() {
		if (defaultAppearance == null) {
			defaultAppearance = new DefaultAppearance(this);
		}
		return defaultAppearance;
	}

	/**
	 * The {@link CSContent} fragment defining the default appearance to be used
	 * for variable text fields.
	 * 
	 * @return The {@link CSContent} fragment defining the default appearance to
	 *         be used for variable text fields.
	 */
	public CSContent getDefaultAppearanceContent() {
		if (defaultAppearanceContent == null) {
			COSString cosObject = cosGetFieldInheritable(DK_DA).asString();
			if (cosObject != null) {
				defaultAppearanceContent = CSContent.createFromBytes(cosObject
						.byteValue());
			}
		}
		return defaultAppearanceContent;
	}

	/**
	 * The font object defined by the default appearance.
	 * 
	 * @return The font object defined by the default appearance.
	 */
	public PDFont getDefaultAppearanceFont() {
		PDFont result = getDefaultAppearance().getFont();
		if (result == null) {
			// this is an error in the document, the font should exist!
			// still take a substitute
			if (getAcroForm() != this) {
				result = getAcroForm().getDefaultAppearanceFont();
			}
			if (result == null) {
				result = PDFontTools
						.createBuiltinFont(PDFontType1.FONT_Helvetica);
			}
		}
		return result;
	}

	/**
	 * The font color defined by the default appearance.
	 * 
	 * @return The font color defined by the default appearance.
	 */
	public float[] getDefaultAppearanceFontColor() {
		return getDefaultAppearance().getFontColorValues();
	}

	/**
	 * The font name used by the default appearance to select a font from the
	 * resources.
	 * 
	 * @return The font name used by the default appearance to select a font
	 *         from the resources.
	 */
	public COSName getDefaultAppearanceFontName() {
		return getDefaultAppearance().getFontName();
	}

	/**
	 * The font size defined by the default appearance,
	 * 
	 * @return The font size defined by the default appearance,
	 */
	public float getDefaultAppearanceFontSize() {
		return getDefaultAppearance().getFontSize();
	}

	/**
	 * The sub-node identified by <code>path</code>. The <code>path</code> may
	 * be a navigation path containing multiple segments separated by a ".",
	 * each segment identifying a sub node in the node found so far.
	 * <p>
	 * The navigation starts at this and the first path segment is matched
	 * against the node's children. Example:<br>
	 * 
	 * <code>
	 * AcroForm
	 * |
	 * + Field1
	 * |
	 * + Group1
	 *   |
	 *   + FieldA
	 *   |
	 *   + FieldB
	 * 
	 * When requesting the form itself, Field1 is addressed
	 * 
	 *   form.getField("Field1");
	 * 
	 * FieldA can be looked up
	 * 
	 *   form.getField("Group1.FieldA");
	 * 
	 * or
	 * 
	 *   group = form.getField("Group1");
	 *   group.getField("FieldA");
	 * </code>
	 * 
	 * @param path
	 *            The navigation path to the field.
	 * @return The sub-node identified by <code>path</code>.
	 */
	public PDAcroFormField getField(String path) {
		return getField(path, false);
	}

	/**
	 * The sub-node identified by <code>path</code>. The <code>path</code> may
	 * be a navigation path containing multiple segments separated by a ".",
	 * each segment identifying a sub node in the node found so far. If
	 * <code>canonicalName</code> is <code>true</code>, the name will be
	 * transformed to a canonical format before lookup.
	 * 
	 * @param name
	 *            The navigation path to the field.
	 * @param canonicalName
	 *            Flag if lookup uses canonical form.
	 * @return The sub-node identified by <code>path</code>
	 */
	public PDAcroFormField getField(String name, boolean canonicalName) {
		String basicname = canonicalName ? canonicalize(name) : name;
		return basicGetField(basicname, canonicalName);
	}

	/**
	 * The justification of variable text within the field.
	 * <ul>
	 * <li>0</li>
	 * left (default)
	 * <li>1</li>
	 * centered
	 * <li>2</li>
	 * right
	 * </ul>
	 * 
	 * @return An int representing the intended quadding for this field
	 */
	public int getQuadding() {
		COSInteger cosObject = cosGetFieldInheritable(DK_Q).asInteger();
		if (cosObject == null) {
			return 0;
		}
		return cosObject.intValue();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.intarsys.pdf.cos.COSBasedObject#invalidateCaches()
	 */
	@Override
	public void invalidateCaches() {
		super.invalidateCaches();
		defaultAppearanceContent = null;
		defaultAppearance = null;
	}

	/**
	 * Remove a {@link PDAcroFormField} from the receiver.
	 * 
	 * @param field
	 *            The field to remove.
	 * @return <code>true</code> if <code>field</code> was removed.
	 */
	abstract public boolean removeField(PDAcroFormField field);

	protected void setDefaultAppearance(
			DefaultAppearance defaultAppearanceParser) {
		this.defaultAppearance = defaultAppearanceParser;
	}

	/**
	 * Set the content stream fragment to be used as the default appearance with
	 * variable text fields.
	 * 
	 * @param pContent
	 *            The new default appearance content.
	 */
	public void setDefaultAppearanceContent(CSContent pContent) {
		if (pContent != null) {
			cosSetField(DK_DA, COSString.create(pContent.toByteArray()));
		} else {
			cosRemoveField(DK_DA);
		}
	}

	/**
	 * Set the font to be used as the default font in variable text fields.
	 * 
	 * @param font
	 *            The font to be used as the default font in variable text
	 *            fields.
	 */
	public void setDefaultAppearanceFont(PDFont font) {
		getDefaultAppearance().setFont(font);
	}

	/**
	 * Set the font color to be used as the default font color in variable text
	 * fields.
	 * 
	 * @param color
	 *            The font color to be used as the default font color in
	 *            variable text fields.
	 */
	public void setDefaultAppearanceFontColor(float[] color) {
		getDefaultAppearance().setFontColorValues(color);
	}

	/**
	 * Set the font size to be used as the default font size in variable text
	 * fields.
	 * 
	 * @param size
	 *            The font size to be used as the default font size in variable
	 *            text fields.
	 */
	public void setDefaultAppearanceFontSize(float size) {
		getDefaultAppearance().setFontSize(size);
	}

	/**
	 * Set the justification of variable text within the field.
	 * 
	 * @param quadding
	 *            THe new quadding value
	 */
	public void setQuadding(int quadding) {
		cosSetFieldInheritable(DK_Q, COSInteger.create(quadding));
	}
}
