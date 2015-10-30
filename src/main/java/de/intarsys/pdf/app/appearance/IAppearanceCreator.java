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
package de.intarsys.pdf.app.appearance;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDAppearance;

/**
 * A strategy for creating the appearances (PDForm objects) for an annotation.
 * Depending on the annotations state the PDAppearance should be manipulated to
 * provide the correct visual feedback.
 */
public interface IAppearanceCreator {
    /**
     * Create a PDAppearance suitable to display annotation in its current
     * state. The implementation should use "appearance" when set, if not should
     * try to use the annotations PDAppearance. If none of both is set, it
     * should create a new one. The method should return the appearance it
     * worked upon.
     * <p>
     * The code should NOT manipulate (write to) the annotation. If a new
     * PDAppearance is to be created, it should be created and returned as the
     * methods result without connecting to the annotation.
     * <p>
     * To allow for "daisy chaining" calls, a appearance parameter is provided.
     * If it is null, the code should lookup the appearance in the annotation.
     * If nothing there, it should create a new one (again: without connecting
     * to the annotation).
     *
     * @param annotation
     * @param appearance
     * @return the created appearance
     */
    PDAppearance createAppearance(PDAnnotation annotation, PDAppearance appearance);

    /**
     * The type of annotations this handler can process.
     * <p>
     * This is for example /Ink or /Circle.
     *
     * @return The type of annotations this handler can process.
     */
    COSName getAnnotationType();
}
