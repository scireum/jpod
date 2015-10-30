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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.util.IdentityHashMap;

import de.intarsys.pdf.pd.PDColorSpace;
import de.intarsys.pdf.pd.PDExtGState;
import de.intarsys.tools.attribute.Attribute;
import de.intarsys.tools.attribute.AttributeMap;
import de.intarsys.tools.attribute.IAttributeSupport;

/**
 * The state information for performing graphic operations.
 * 
 */
final public class GraphicsState implements IAttributeSupport {

	final private AttributeMap attributes = new AttributeMap();

	/**
	 * A code defining the shape of a lines endpoint
	 * 
	 * <p>
	 * initial value: 0 (square butt)
	 * </p>
	 */
	public int capStyle;

	/**
	 * all graphics operations are clipped at this boundary
	 * 
	 * <p>
	 * initial value: entire page
	 * </p>
	 */
	public Shape clip;

	/**
	 * The dash pattern used for stroking
	 * 
	 * <p>
	 * initial value: solid line
	 * </p>
	 */
	public float[] dashPattern;

	/**
	 * The phase of the dash pattern
	 * 
	 * <p>
	 * initial value: 0
	 * </p>
	 */
	public float dashPhase;

	/**
	 * A code defining the shape of line joins
	 * 
	 * <p>
	 * initial value: 0 (miter)
	 * </p>
	 */
	public int joinStyle;

	/**
	 * The thickness of stroked lines
	 * 
	 * <p>
	 * initial value: 1
	 * </p>
	 */
	public float lineWidth;

	/**
	 * The maximum length of mitered line joins
	 * 
	 * <p>
	 * initial value: 10
	 * </p>
	 */
	public float miterLimit;

	/**
	 * The alpha (transparency) value for non stroking operations.
	 * <p>
	 * This is contained in the ExtGState but is cached as it is heavily uesd.
	 */
	public float nonStrokeAlphaValue;

	public PDColorSpace nonStrokeColorSpace;

	public float[] nonStrokeColorValues;

	/**
	 * The alpha (transparency) value for stroking operations
	 * <p>
	 * This is contained in the ExtGState but is cached as it is heavily uesd.
	 */
	public float strokeAlphaValue;

	public PDColorSpace strokeColorSpace;

	public PDExtGState extState;

	public float[] strokeColorValues;

	/** The parameters used for rendering text operations. */
	public TextState textState;

	public AffineTransform transform;

	/**
	 * Create a new graphic state for the renderer
	 */
	public GraphicsState() {
		dashPattern = new float[0];
		dashPhase = 0;
		capStyle = 0;
		joinStyle = 0;
		lineWidth = 1f;
		miterLimit = 10f;
		nonStrokeAlphaValue = 1;
		strokeAlphaValue = 1;
		textState = new TextState();
		extState = null;
		transform = new AffineTransform();
	}

	/**
	 * Copy constructor for GraphicsState
	 * 
	 * @param originalState
	 *            The state to copy.
	 */
	protected GraphicsState(GraphicsState originalState) {
		capStyle = originalState.capStyle;
		dashPattern = originalState.dashPattern;
		dashPhase = originalState.dashPhase;
		if (originalState.extState != null) {
			extState = (PDExtGState) PDExtGState.META
					.createFromCos(originalState.extState.cosGetObject()
							.copyShallow());
		}
		joinStyle = originalState.joinStyle;
		lineWidth = originalState.lineWidth;
		miterLimit = originalState.miterLimit;
		nonStrokeAlphaValue = originalState.nonStrokeAlphaValue;
		nonStrokeColorSpace = originalState.nonStrokeColorSpace;
		nonStrokeColorValues = originalState.nonStrokeColorValues;
		strokeAlphaValue = originalState.strokeAlphaValue;
		strokeColorSpace = originalState.strokeColorSpace;
		strokeColorValues = originalState.strokeColorValues;
		textState = originalState.textState.copy();
		transform = originalState.transform;
		// clip may be tied to transform!
		clip = originalState.clip;
	}

	public GraphicsState copy() {
		return new GraphicsState(this);
	}

	/**
	 * Get a generic attribute value.
	 * <p>
	 * ATTENTION: to speed up a little, an {@link IdentityHashMap} is used for
	 * implementing {@link IAttributeSupport}. Be sure to use appropriate keys
	 * (for example {@link Attribute}), not {@link String}.
	 * 
	 * @see de.intarsys.tools.attribute.IAttributeSupport#getAttribute(java.lang.Object)
	 */
	final public Object getAttribute(Object key) {
		return attributes.getAttribute(key);
	}

	/**
	 * Clear a generic attribute value.
	 * <p>
	 * ATTENTION: to speed up a little, an {@link IdentityHashMap} is used for
	 * implementing {@link IAttributeSupport}. Be sure to use appropriate keys
	 * (for example {@link Attribute}), not {@link String}.
	 * 
	 * @see de.intarsys.tools.attribute.IAttributeSupport#removeAttribute(java.lang.Object)
	 */
	final public Object removeAttribute(Object key) {
		return attributes.removeAttribute(key);
	}

	/**
	 * Set a generic attribute value.
	 * <p>
	 * ATTENTION: to speed up a little, an {@link IdentityHashMap} is used for
	 * implementing {@link IAttributeSupport}. Be sure to use appropriate keys
	 * (for example {@link Attribute}), not {@link String}.
	 * 
	 * @see de.intarsys.tools.attribute.IAttributeSupport#setAttribute(java.lang.Object,
	 *      java.lang.Object)
	 */
	final public Object setAttribute(Object key, Object value) {
		return attributes.setAttribute(key, value);
	}

}
