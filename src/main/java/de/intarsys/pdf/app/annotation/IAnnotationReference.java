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
package de.intarsys.pdf.app.annotation;

import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDDocument;

import java.awt.geom.Point2D;

/**
 * An object designating an annotation within a document.
 * <p>
 * This can be for example simply a name, referencing an existing object or a
 * full set of descriptions how to create a new one.
 */
public interface IAnnotationReference {

    /**
     * The {@link PDAnnotation} referenced by this.
     * <p>
     * Evaluating this method may create a new {@link PDAnnotation} lazy and
     * modify the {@link PDDocument}!
     *
     * @return The {@link PDAnnotation} referenced by this.
     */
    PDAnnotation getAnnotation();

    /**
     * The {@link PDDocument} hosting the {@link PDAnnotation}.
     *
     * @return The {@link PDDocument} hosting the {@link PDAnnotation}.
     */
    PDDocument getDocument();

    /**
     * The position of the bounding box of the {@link PDAnnotation}. This may
     * be evaluated even if the {@link PDAnnotation} not yet exists without
     * modifying the {@link PDDocument}.
     *
     * @return The position of the bounding box of the {@link PDAnnotation}.
     */
    Point2D getPosition();

    /**
     * The size of the bounding box of the {@link PDAnnotation}. This may be
     * evaluated even if the {@link PDAnnotation} not yet exists without
     * modifying the {@link PDDocument}.
     *
     * @return The size of the bounding box of the {@link PDAnnotation}.
     */
    Point2D getSize();

    boolean isNew();

    boolean isVisible();
}
