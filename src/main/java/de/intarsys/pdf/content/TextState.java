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

import de.intarsys.pdf.font.PDFont;

import java.awt.geom.AffineTransform;

/**
 * This class summarizes all state information that is used to render characters
 * in the current context, such as font, font size or the text transformation
 * matrix etc.
 */
public class TextState {

    public static final int RENDERING_MODE_CLIP = 7;

    public static final int RENDERING_MODE_FILL = 0;

    public static final int RENDERING_MODE_FILL_CLIP = 4;

    public static final int RENDERING_MODE_FILL_STROKE = 2;

    public static final int RENDERING_MODE_FILL_STROKE_CLIP = 6;

    public static final int RENDERING_MODE_NONE = 3;

    public static final int RENDERING_MODE_STROKE = 1;

    public static final int RENDERING_MODE_STROKE_CLIP = 5;

    /**
     * Flag if text object (text begin operator) is active.
     */
    public boolean active;

    /**
     * character spacing, unscaled text units
     * <p>
     * <p>
     * initial value: 0
     * </p>
     */
    public float charSpacing;

    /**
     * This is the scaled character spacing.
     * <p>
     * This is a derived value:
     * <p>
     * charSpacingScaled = charSpacing*horizontalScaling
     */
    public float derivedCharSpacingScaled;

    /**
     * The factor to be used to calculate the advance after a glyph.
     * <p>
     * This is a derived value:
     * <p>
     * glyphAdvanceFactor = fontSize/1000*horizontalScalingFactor
     */
    public float derivedGlyphAdvanceFactor;

    public float derivedHorizontalScalingFactor;

    /**
     * This is the scaled word spacing.
     * <p>
     * This is a derived value:
     * <p>
     * wordSpacingScaled = wordSpacing*horizontalScaling
     */
    public float derivedWordSpacingScaled;

    /**
     * the PDFont to use for this text piece
     * <p>
     * <p>
     * initial value: undefined
     * </p>
     */
    public PDFont font;

    /**
     * the font scaling
     * <p>
     * <p>
     * initial value: undefined
     * </p>
     */
    public float fontSize;

    /**
     * percentage of normal width
     * <p>
     * <p>
     * initial value: 100
     * </p>
     */
    public float horizontalScaling;

    public boolean knockout;

    /**
     * text leading, unscaled text units
     * <p>
     * <p>
     * initial value: 0
     * </p>
     */
    public float leading;

    /**
     * The text line matrix.
     * <p>
     * <p>
     * This is a temporary attribute that keeps track of the state of tm at the
     * start of a new line.
     * </p>
     */
    public AffineTransform lineTransform;

    /**
     * rendering mode, enumeration
     * <p>
     * <p>
     * initial value: 0
     * </p>
     */
    public int renderingMode;

    /**
     * text rise, unscaled text units
     * <p>
     * <p>
     * initial value: 0
     * </p>
     */
    public float rise;

    /**
     * The text matrix.
     */
    public AffineTransform transform;

    /**
     * The concatenation of the user space transformation and the text matrix.
     */
    public AffineTransform globalTransform;

    /**
     * word spacing, unscaled text units
     * <p>
     * <p>
     * initial value: 0
     * </p>
     */
    public float wordSpacing;

    public TextState() {
        active = false;
        charSpacing = 0;
        horizontalScaling = 100;
        knockout = true;
        leading = 0;
        renderingMode = 0;
        rise = 0;
        lineTransform = new AffineTransform();
        transform = new AffineTransform();
        globalTransform = new AffineTransform();
        wordSpacing = 0;
        //
        derivedHorizontalScalingFactor = 1;
        derivedCharSpacingScaled = 0;
        derivedGlyphAdvanceFactor = 0;
        derivedWordSpacingScaled = 0;
    }

    protected TextState(TextState other) {
        active = other.active;
        charSpacing = other.charSpacing;
        font = other.font;
        fontSize = other.fontSize;
        horizontalScaling = other.horizontalScaling;
        knockout = other.knockout;
        leading = other.leading;
        renderingMode = other.renderingMode;
        rise = other.rise;
        lineTransform = other.lineTransform;
        transform = other.transform;
        globalTransform = other.globalTransform;
        wordSpacing = other.wordSpacing;
        //
        derivedHorizontalScalingFactor = other.derivedHorizontalScalingFactor;
        derivedCharSpacingScaled = other.derivedCharSpacingScaled;
        derivedGlyphAdvanceFactor = other.derivedGlyphAdvanceFactor;
        derivedWordSpacingScaled = other.derivedWordSpacingScaled;
    }

    public TextState copy() {
        return new TextState(this);
    }
}
