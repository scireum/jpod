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
import java.util.Set;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSDeviceBasedInterpreter;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.content.CSOperators;
import de.intarsys.pdf.content.CSVirtualDevice;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSFixed;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.font.PDFont;
import de.intarsys.pdf.font.PDFontTools;

/**
 * A internal representation of the parsed {@link CSContent} for the default
 * appearance content stream fragment in a {@link PDAcroFormField}.
 * 
 */
public class DefaultAppearance {

	/**
	 * An embedded "device" to gather information from the default appearance
	 * string.
	 */
	public class DefaultAppearanceDevice extends CSVirtualDevice {

		@Override
		protected void basicSetNonStrokeColorSpace(PDColorSpace colorSpace) {
			nonStrokeColorSpace = colorSpace;
		}

		@Override
		protected void basicSetNonStrokeColorValues(float[] values) {
			nonStrokeColorValues = values;
		}

		@Override
		protected void basicSetStrokeColorSpace(PDColorSpace colorSpace) {
			strokeColorSpace = colorSpace;
		}

		@Override
		protected void basicSetStrokeColorValues(float[] values) {
			strokeColorValues = values;
		}

		@Override
		public void textSetFont(COSName name, PDFont paramFont, float size) {
			fontName = name;
			fontSize = size;
		}
	}

	private PDAcroFormNode node;

	protected COSName fontName;

	protected float fontSize;

	protected float[] nonStrokeColorValues;

	protected PDColorSpace nonStrokeColorSpace;

	protected float[] strokeColorValues;

	protected PDColorSpace strokeColorSpace;

	public DefaultAppearance(PDAcroFormNode node) {
		super();
		this.node = node;
		CSDeviceBasedInterpreter interpreter;
		interpreter = new CSDeviceBasedInterpreter(null,
				new DefaultAppearanceDevice());
		interpreter.process(node.getDefaultAppearanceContent(), node
				.getAcroForm().getDefaultResources());
	}

	protected COSName addFontResource(PDAcroForm form, PDFont font) {
		PDResources resources = form.getDefaultResources();
		if (resources == null) {
			resources = (PDResources) PDResources.META.createNew();
			form.setDefaultResources(resources);
		}
		COSDictionary fontResources = resources
				.cosGetResources(PDResources.CN_RT_Font);
		if (fontResources == null) {
			fontResources = resources.cosInitResources(PDResources.CN_RT_Font);
		}
		for (Iterator i = fontResources.keySet().iterator(); i.hasNext();) {
			COSName fontKey = (COSName) i.next();
			COSObject fontValue = fontResources.get(fontKey);
			if (fontValue.equals(font.cosGetObject())) {
				return fontKey;
			}
		}
		return resources.createFontResource(font);
	}

	protected COSName addFontResource(PDFont font) {
		return addFontResource(node.getAcroForm(), font);
	}

	protected void cleanupFontResources(PDAcroForm form) {
		// collect used font keys
		Set referencedKeys = getReferencedFontKeys(form);

		// determine overhead
		Set overhead = new HashSet();
		COSDictionary fontDict = form.getDefaultResources().cosGetResources(
				PDResources.CN_RT_Font);
		if (fontDict != null) {
			for (Iterator i = fontDict.keySet().iterator(); i.hasNext();) {
				COSName key = (COSName) i.next();
				if (!referencedKeys.contains(key)) {
					overhead.add(key);
				}
			}
			// remove overhead
			for (Iterator i = overhead.iterator(); i.hasNext();) {
				COSName key = (COSName) i.next();
				fontDict.remove(key);
			}
		}
	}

	protected void collectDefaultAppearanceFonts(PDAcroFormNode pNode, Set keys) {
		// check usage in default appearance
		COSName fontKey = pNode.getDefaultAppearanceFontName();
		if (fontKey != null) {
			keys.add(fontKey);
		}

		//
		if (pNode.getGenericChildren() == null) {
			return;
		}
		for (Iterator i = pNode.getGenericChildren().iterator(); i.hasNext();) {
			PDAcroFormNode child = (PDAcroFormNode) i.next();
			collectDefaultAppearanceFonts(child, keys);
		}
	}

	protected void contentReplaceColor(float[] color) {
		CSContent appearance = node.getDefaultAppearanceContent();
		CSContent content = CSContent.createNew();
		CSOperation op = null;
		switch (color.length) {
		case 1: {
			op = new CSOperation(CSOperators.CSO_g);
			break;
		}
		case 3: {
			op = new CSOperation(CSOperators.CSO_rg);
			break;
		}
		case 4: {
			op = new CSOperation(CSOperators.CSO_k);
			break;
		}
		}
		for (int index = 0; index < color.length; index++) {
			op.addOperand(COSFixed.create(color[index]));
		}
		if (appearance == null) {
			content.addOperation(op);
		} else {
			// copy / modify stream
			int len = appearance.size();
			boolean replaced = false;
			for (int i = 0; i < len; i++) {
				CSOperation operation = appearance.getOperation(i);
				if (operation.matchesOperator(CSOperators.CSO_g)
						|| operation.matchesOperator(CSOperators.CSO_rg)
						|| operation.matchesOperator(CSOperators.CSO_k)) {
					content.addOperation(op);
					replaced = true;
				} else {
					content.addOperation(operation);
				}
			}
			if (!replaced) {
				content.addOperation(op);
			}
		}
		node.setDefaultAppearanceContent(content);
	}

	protected void contentReplaceFont(COSName pFontName) {
		CSContent appearance = node.getDefaultAppearanceContent();
		CSContent content = CSContent.createNew();
		CSOperation op = new CSOperation(CSOperators.CSO_Tf);
		op.addOperand(pFontName);
		op.addOperand(COSInteger.create(0));
		if (appearance == null) {
			content.addOperation(op);
		} else {
			// copy / modify stream
			int len = appearance.size();
			boolean replaced = false;
			for (int i = 0; i < len; i++) {
				CSOperation operation = appearance.getOperation(i);
				if (operation.matchesOperator(CSOperators.CSO_Tf)) {
					if (operation.operandSize() >= 2) {
						op.setOperand(1, operation.getOperand(1));
					}
					content.addOperation(op);
					replaced = true;
				} else {
					content.addOperation(operation);
				}
			}
			if (!replaced) {
				content.addOperation(op);
			}
		}
		node.setDefaultAppearanceContent(content);
	}

	protected void contentReplaceSize(float pFontSize) {
		CSContent appearance = node.getDefaultAppearanceContent();
		CSContent content = CSContent.createNew();
		CSOperation op = new CSOperation(CSOperators.CSO_Tf);
		op.addOperand(COSName.create("Helv")); //$NON-NLS-1$
		op.addOperand(COSFixed.create(pFontSize));
		if (appearance == null) {
			content.addOperation(op);
		} else {
			// copy / modify stream
			int len = appearance.size();
			boolean replaced = false;
			for (int i = 0; i < len; i++) {
				CSOperation operation = appearance.getOperation(i);
				if (operation.matchesOperator(CSOperators.CSO_Tf)) {
					if (operation.operandSize() >= 1) {
						op.setOperand(0, operation.getOperand(0));
					}
					content.addOperation(op);
					replaced = true;
				} else {
					content.addOperation(operation);
				}
			}
			if (!replaced) {
				content.addOperation(op);
			}
		}
		node.setDefaultAppearanceContent(content);
	}

	public PDFont getFont() {
		PDResources resources = node.getAcroForm().getDefaultResources();
		if (resources == null) {
			return null;
		}
		return PDFontTools.getFont(resources, getFontName());
	}

	/**
	 * parse the requested font color from the default appearance string
	 * 
	 * @return the font color used in the default appearance
	 * 
	 * @throws IllegalStateException
	 */
	public float[] getFontColorValues() {
		return nonStrokeColorValues;
	}

	public COSName getFontName() {
		return fontName;
	}

	public float getFontSize() {
		return fontSize;
	}

	public PDAcroFormNode getNode() {
		return node;
	}

	protected Set getReferencedFontKeys(PDAcroForm form) {
		Set referencedKeys = new HashSet();
		collectDefaultAppearanceFonts(form, referencedKeys);
		return referencedKeys;
	}

	public void setFont(PDFont font) {
		COSName cosFontName = addFontResource(font);
		setFontName(cosFontName);
		cleanupFontResources(node.getAcroForm());
	}

	public void setFontColorValues(float[] color) {
		contentReplaceColor(color);
		nonStrokeColorValues = color;
	}

	public void setFontName(COSName pFontName) {
		contentReplaceFont(pFontName);
		fontName = pFontName;
	}

	public void setFontSize(float pFontSize) {
		contentReplaceSize(pFontSize);
		fontSize = pFontSize;
	}
}
