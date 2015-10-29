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
package de.intarsys.pdf.font;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import de.intarsys.pdf.cds.CDSRectangle;
import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSError;
import de.intarsys.pdf.content.CSException;
import de.intarsys.pdf.content.CSInterpreter;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.content.CSWarning;
import de.intarsys.pdf.cos.COSDictionary;
import de.intarsys.pdf.cos.COSInteger;
import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.cos.COSObject;
import de.intarsys.pdf.cos.COSObjectWalkerDeep;
import de.intarsys.pdf.cos.COSStream;
import de.intarsys.pdf.cos.COSTools;
import de.intarsys.pdf.cos.COSVisitorException;
import de.intarsys.pdf.cos.ICOSObjectVisitor;
import de.intarsys.pdf.encoding.Encoding;
import de.intarsys.pdf.encoding.WinAnsiEncoding;
import de.intarsys.pdf.font.outlet.FontFactoryException;
import de.intarsys.pdf.font.outlet.FontOutlet;
import de.intarsys.pdf.font.outlet.FontQuery;
import de.intarsys.pdf.font.outlet.IFontFactory;
import de.intarsys.pdf.font.outlet.IFontOutlet;
import de.intarsys.pdf.font.outlet.IFontQuery;
import de.intarsys.pdf.pd.PDDocument;
import de.intarsys.pdf.pd.PDForm;
import de.intarsys.pdf.pd.PDObject;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.pdf.pd.PDPattern;
import de.intarsys.pdf.pd.PDResources;
import de.intarsys.tools.attribute.Attribute;

/**
 * Tool class for handling PDF fonts.
 * <p>
 * For more sophisticated help, see {@link IFontOutlet}.
 * 
 */
public class PDFontTools {

	private static final Attribute ATTR_CIDFONT = new Attribute("cidfont");

	private static final Attribute ATTR_CREATEDFONTS = new Attribute(
			"createdfonts");

	private static final Attribute ATTR_TYPE0FONT = new Attribute("type0font");

	static protected void collectFonts(Set fonts, CSContent content,
			PDResources resources, final boolean considerTR) {
		final Set fontNames = new HashSet();
		CSInterpreter collector = new CSInterpreter(new HashMap()) {

			private COSName fontName;

			private int renderingMode = 0;

			@Override
			protected void handleError(CSError error) throws CSException {
				//
			}

			protected void handleText() {
				if (renderingMode != 3 || !considerTR) {
					fontNames.add(fontName);
				}
			}

			@Override
			protected void handleWarning(CSWarning warning) throws CSException {
				//
			}

			@Override
			protected void notSupported(CSOperation operation)
					throws CSException {
				//
			}

			@Override
			protected void render_DoubleQuote(CSOperation operation)
					throws CSException {
				handleText();
			}

			@Override
			protected void render_Quote(CSOperation operation)
					throws CSException {
				handleText();
			}

			@Override
			protected void render_Tf(CSOperation operation) throws CSException {
				if (operation.operandSize() == 2) {
					fontName = operation.getOperand(0).asName();
				}
			}

			@Override
			protected void render_Tj(CSOperation operation) throws CSException {
				handleText();
			}

			@Override
			protected void render_TJ(CSOperation operation) throws CSException {
				handleText();
			}

			@Override
			protected void render_Tr(CSOperation operation) throws CSException {
				COSInteger mode = (COSInteger) operation.getOperand(0);
				renderingMode = mode.intValue();
			}
		};
		collector.process(content, resources);
		for (Iterator it = fontNames.iterator(); it.hasNext();) {
			COSName fontName = (COSName) it.next();
			fonts.add(resources.getFontResource(fontName));
		}
	}

	/**
	 * Create a well known Type1 font.
	 * 
	 * @param name
	 *            The font name.
	 * 
	 * @return The font we found or a new instance created on the fly.
	 */
	public static PDFont createBuiltinFont(String name) {
		PDFont font = PDFontType1.createNew(name);
		if (font.getFontDescriptor().isNonsymbolic()) {
			font.setEncoding(WinAnsiEncoding.UNIQUE);
		}
		return font;
	}

	protected static CIDFont createCIDFont(PDFont font) {
		if (font instanceof PDFontTrueType) {
			CIDFontType2 result = (CIDFontType2) CIDFontType2.META.createNew();
			result.cosSetField(PDFont.DK_BaseFont, font.getBaseFont()
					.copyShallow());
			CIDSystemInfo info = (CIDSystemInfo) CIDSystemInfo.META.createNew();
			int width = font.getMissingWidth();
			if (width == 0) {
				width = 1000;
			}
			result.setDefaultGlyphWidth(width);
			result.setCIDSystemInfo(info);
			// PDFontDescriptorEmbedded descriptor =
			// (PDFontDescriptorEmbedded)
			// PDFontDescriptorEmbedded.META
			// .createNew();
			result.setFontDescriptor(font.getFontDescriptor());
			return result;
		} else if (font instanceof PDFontType1) {
			//
		} else {
			//
		}
		return null;
	}

	protected static PDFont createType0Font(PDFont font) {
		PDFont result;
		if (font instanceof PDFontType0) {
			result = font;
		} else {
			CIDFont cidFont = getCIDFont(font);
			if (cidFont == null) {
				result = font;
			} else {
				PDFontType0 type0Font = (PDFontType0) PDFontType0.META
						.createNew();
				type0Font.cosSetField(PDFont.DK_BaseFont, font.getBaseFont()
						.copyShallow());
				// /Identity-H encoding is set by default
				type0Font.setDescendantFont(cidFont);
				result = type0Font;
			}
		}
		return result;
	}

	static protected CIDFont getCIDFont(PDFont font) {
		CIDFont cidFont = (CIDFont) font.getAttribute(ATTR_CIDFONT);
		if (cidFont == null) {
			cidFont = createCIDFont(font);
			font.setAttribute(ATTR_CIDFONT, cidFont);
		}
		return cidFont;
	}

	/**
	 * The font <code>name</code>, looked up in <code>resources</code>.
	 * <p>
	 * When no matching resource is found, a builtin font is created on the fly.
	 * 
	 * @param document
	 * @param resources
	 * @param name
	 * @return The font <code>name</code>, looked up in <code>resources</code>.
	 */
	public static PDFont getFont(PDDocument document, PDResources resources,
			COSName name) {
		if (resources != null) {
			PDFont font = resources.getFontResource(name);
			if (font != null) {
				return font;
			}
		}
		if (name != null) {
			// bad PDF but try to be nice; treat as builtin font anyway
			return lookupFont(document, name.stringValue());
		}
		return null;
	}

	/**
	 * The font <code>name</code>, looked up in <code>resources</code>.
	 * 
	 * @param resources
	 * @param name
	 * @return The font <code>name</code>, looked up in <code>resources</code>.
	 */
	public static PDFont getFont(PDResources resources, COSName name) {
		return getFont(resources.getDoc(), resources, name);
	}

	/**
	 * Determine the fonts contained as objects within the document.
	 * 
	 * @param doc
	 *            The PDDocument to parse
	 * @return Collection of all {@link PDFont} objects in the document.
	 */
	public static List getFonts(PDDocument doc) {
		final List result = new ArrayList();
		try {
			COSDictionary trailer = doc.cosGetDoc().stGetDoc().cosGetTrailer();
			ICOSObjectVisitor visitor = new COSObjectWalkerDeep() {
				@Override
				protected void handleException(RuntimeException e)
						throws COSVisitorException {
					// ignore swapping exceptions
				}

				@Override
				public Object visitFromDictionary(COSDictionary dict)
						throws COSVisitorException {
					try {
						PDFont font = (PDFont) PDFont.META.createFromCos(dict);
						if (font != null) {
							result.add(font);
							return null;
						}
					} catch (Exception e) {
						//
					}
					return super.visitFromDictionary(dict);
				}
			};
			trailer.accept(visitor);
		} catch (Exception e) {
			// ignore any exception (especially COSSwapException)
		}
		return result;
	}

	/**
	 * The font height in user space coordinates.
	 * 
	 * @param font
	 *            The font to be used.
	 * @return The scaled font height in user space coordinates.
	 */
	public static float getGlyphHeight(PDFont font) {
		CDSRectangle rect = font.getFontDescriptor().getFontBB();
		float scaledHeight = rect.getUpperRightY() - rect.getLowerLeftY();
		scaledHeight = scaledHeight / 1000f;
		return scaledHeight;
	}

	/**
	 * The scaled font height in user space coordinates.
	 * 
	 * @param font
	 *            The font to be used.
	 * @param size
	 *            The font size
	 * @return The scaled font height in user space coordinates.
	 */
	public static float getGlyphHeightScaled(PDFont font, float size) {
		CDSRectangle rect = font.getFontDescriptor().getFontBB();
		float scaledHeight = rect.getUpperRightY() - rect.getLowerLeftY();
		scaledHeight = (size * scaledHeight) / 1000f;
		return scaledHeight;
	}

	/**
	 * The sum of the length of all glyphs referenced by <code>length</code>
	 * bytes from <code>codepoints</code> starting at <code>offset</code>.
	 * 
	 * @param font
	 * @param codepoints
	 * @param offset
	 * @param length
	 * @return The sum of the length of all glyphs referenced by
	 *         <code>length</code> bytes from <code>codepoints</code> starting
	 *         at <code>offset</code>.
	 */
	public static int getGlyphWidthEncoded(PDFont font, byte[] codepoints,
			int offset, int length) {
		InputStream is = new ByteArrayInputStream(codepoints, offset, length);
		Encoding encoding = font.getEncoding();
		int result = 0;
		try {
			int codepoint = encoding.getNextEncoded(is);
			while (codepoint != -1) {
				result = result + font.getGlyphWidthEncoded(codepoint);
				codepoint = encoding.getNextEncoded(is);
			}
		} catch (IOException e) {
			// no io exception with byte arrays
		}
		return result;
	}

	/**
	 * The scaled sum of the length of all glyphs referenced by
	 * <code>length</code> bytes from <code>codepoints</code> starting at
	 * <code>offset</code>.
	 * 
	 * @param font
	 * @param size
	 * @param codepoints
	 * @param offset
	 * @param length
	 * @return The scaled sum of the length of all glyphs referenced by
	 *         <code>length</code> bytes from <code>codepoints</code> starting
	 *         at <code>offset</code>.
	 */
	public static float getGlyphWidthEncodedScaled(PDFont font, float size,
			byte[] codepoints, int offset, int length) {
		float width = getGlyphWidthEncoded(font, codepoints, offset, length);
		return (size * width) / 1000f;
	}

	/**
	 * "Scale up" <code>font</code> to a multibyte font.
	 * 
	 * @param font
	 * @return The new font
	 */
	static public PDFont getType0Font(PDFont font) {
		PDFont result = (PDFont) font.getAttribute(ATTR_TYPE0FONT);
		if (result == null) {
			result = createType0Font(font);
			font.setAttribute(ATTR_TYPE0FONT, result);
		}
		return result;
	}

	/**
	 * Tries to determine which fonts are really used within the document
	 * 
	 * The following criteria are used to determine usage of a font.
	 * <ul>
	 * Any glyph of the font is referenced in
	 * <li>the Contents stream of a page object</li>
	 * <li>the stream of a Form XObject</li>
	 * <li>the appearance stream of an annotation, including form fields</li>
	 * <li>the content stream of a Type 3 font glyph</li>
	 * <li>the stream of a tiling pattern</li>
	 * </ul>
	 * 
	 * @param doc
	 *            The PDDocument to parse
	 * @param considerTR
	 *            If true, considers font references with Text rendering mode 3
	 *            as unused
	 * @return Set of all used fonts
	 */
	public static List getUsedFonts(PDDocument doc, boolean considerTR) {
		Set fonts = new HashSet();

		// get the fonts from the page objects / contentstream
		PDPage page = doc.getPageTree().getFirstPage();
		while (true) {
			if (page == null) {
				break;
			}
			CSContent contentstream = page.getContentStream();
			if (contentstream != null) {
				collectFonts(fonts, contentstream, page.getResources(),
						considerTR);
			}
			page = page.getNextPage();
		}

		// get the fonts from all XObjects, also from tiling patterns
		for (Iterator it = doc.cosGetDoc().objects(); it.hasNext();) {
			try {
				COSObject object = (COSObject) it.next();
				COSDictionary dict = COSTools.toDictionary(object);
				if (dict == null) {
					continue;
				}
				// Form XObjects
				if (dict.get(PDObject.DK_Subtype)
						.equals(PDForm.CN_Subtype_Form)) {
					PDForm form = (PDForm) PDForm.META.createFromCos(object);
					if (form != null) {
						collectFonts(fonts, form.getContentStream(),
								form.getResources(), considerTR);
					}
				}

				// Pattern tiling
				if (dict.get(PDPattern.DK_PatternType).equals(
						COSInteger.create(PDPattern.PATTERN_TYPE_TILING))) {
					CSContent patternCS = CSContent
							.createFromCos((COSStream) object);
					COSDictionary r = dict.get(PDForm.DK_Resources)
							.asDictionary();
					PDResources resources = (PDResources) PDResources.META
							.createFromCos(r);
					collectFonts(fonts, patternCS, resources, considerTR);
				}
			} catch (RuntimeException e) {
				// ignore exception from diving into objects, go on...
			}
		}
		return new ArrayList(fonts);
	}

	/**
	 * Lookup a font anywhere in the document. If the font is not found, we
	 * assume a well known Type1 font was requested.
	 * <p>
	 * This method reads and scans the whole document - so this may be to slow
	 * for some scenarios. In this case you should simply create a new one.
	 * 
	 * @param document
	 *            The document where we search.
	 * @param name
	 *            The font name.
	 * @return The font we found or a new instance created on the fly.
	 */
	protected static PDFont lookupFont(PDDocument document, String name) {
		PDFont font = null;
		IFontFactory factory = FontOutlet.get().lookupFontFactory(document);
		IFontQuery query = new FontQuery(name);
		try {
			font = factory.getFont(query);
		} catch (FontFactoryException e) {
			//
		}
		if (font == null) {
			font = createBuiltinFont(name);
			factory.registerFont(font);
		}
		return font;
	}

}
