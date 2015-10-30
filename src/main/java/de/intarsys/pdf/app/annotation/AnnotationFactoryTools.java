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

import java.awt.geom.Rectangle2D;
import java.util.Map;

import de.intarsys.pdf.cos.COSName;
import de.intarsys.pdf.pd.PDAnnotation;
import de.intarsys.pdf.pd.PDPage;
import de.intarsys.tools.reflect.ObjectCreationException;

/**
 * A tool class to ease life with annotation creation handling.
 */
public class AnnotationFactoryTools {

	/**
	 * Create a new {@link PDAnnotation} from scratch.
	 * 
	 * @param type
	 *            The COSName indicating the annotations type.
	 * @param page
	 *            The page where the annotation is created.
	 * @param rect
	 *            The rectangle bounds of the annotation.
	 * @param attributes
	 *            More attributes that are required for annotation creation in
	 *            their special {@link IAnnotationFactory} implementation.
	 * @return The newly created {@link PDAnnotation} or <code>null</code>
	 * @throws ObjectCreationException
	 */
	static public PDAnnotation createAnnotation(COSName type, PDPage page,
			Rectangle2D rect, Map attributes) throws ObjectCreationException {
		IAnnotationFactory factory = AnnotationOutlet.get()
				.lookupAnnotationFactory(type);
		if (factory != null) {
			return factory.createAnnotation(page, rect, attributes);
		}
		return null;
	}

}
