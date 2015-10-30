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
package de.intarsys.pdf.cos;

import de.intarsys.pdf.parser.COSLoadException;
import de.intarsys.pdf.parser.PDFParser;
import de.intarsys.pdf.st.STDocument;
import de.intarsys.pdf.st.STXRefSection;
import de.intarsys.tools.locator.ILocator;
import de.intarsys.tools.locator.LocatorViewport;
import de.intarsys.tools.randomaccess.IRandomAccess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Some tools to ease life with COS.
 */
public class COSTools {

    public static class Revision {
        private STXRefSection xRefSection;
        private long length;

        public ILocator createLocator() {
            LocatorViewport locator = new LocatorViewport(getXRefSection().getDoc().getLocator());
            locator.setName(getXRefSection().getDoc().getLocator().getLocalName()
                            + "_v"
                            + getXRefSection().getIncrementalCount()
                            + ".pdf"); //$NON-NLS-1$ //$NON-NLS-2$
            locator.setStart(0);
            locator.setEnd(getLength());
            locator.setReadOnly();
            return locator;
        }

        public long getLength() {
            return length;
        }

        public STXRefSection getXRefSection() {
            return xRefSection;
        }

        public void setLength(long length) {
            this.length = length;
        }

        public void setXRefSection(STXRefSection refSection) {
            xRefSection = refSection;
        }
    }

    /**
     * Try the best in marshalling java objects directly to {@link COSObject}.
     * Collections will be marshalled recursively.
     *
     * @param javaObject the java object to be marshalled
     * @return The resulting {@link COSObject}
     * @deprecated use {@link COSConverter}
     */
	@Deprecated
	public static COSObject createObject(Object javaObject) {
        return COSConverter.toCos(javaObject);
    }

    public static List<Revision> getRevisions(COSDocument doc) throws IOException {
        List<Revision> result = new ArrayList<Revision>();
        STDocument stDoc = doc.stGetDoc();
        STXRefSection xRef = stDoc.getXRefSection();
        IRandomAccess randomAccess = stDoc.getLocator().getRandomAccess();
        if (randomAccess == null) {
            return result;
        }
        try {
            List<STXRefSection> offsetSortedXRefs = new ArrayList<STXRefSection>();
            while (xRef != null) {
                offsetSortedXRefs.add(xRef);
                xRef = xRef.getPrevious();
            }
            Collections.sort(offsetSortedXRefs, new Comparator<STXRefSection>() {
                @Override
                public int compare(STXRefSection o1, STXRefSection o2) {
                    if (o1.getOffset() < o2.getOffset()) {
                        return 1;
                    }
					if (o1.getOffset() > o2.getOffset()) {
                        return -1;
                    }
					return 0;
                }
            });

            long lastEnd = -1;
            for (STXRefSection section : offsetSortedXRefs) {
                COSDictionary cosDict = section.cosGetDict();
                if (!cosDict.get(COSTrailer.DK_Root).isNull()) {
                    // the dummy trailer in a linearized pdf does not need /Root
                    long limit = lastEnd;
                    if (limit == -1) {
                        limit = randomAccess.getLength();
                    }
                    long end = searchNextEOF(randomAccess, section.getOffset(), limit);
                    // if (end != -1 && lastEnd != end) {
                    if (end != -1) {
                        Revision revision = new Revision();
                        revision.setXRefSection(section);
                        revision.setLength(end);
                        result.add(revision);
                        lastEnd = end;
                    }
                }
            }
        } finally {
            randomAccess.close();
        }
        Collections.reverse(result);
        return result;
    }

    public static List<Revision> getSubsequentRevisions(COSDocument doc, STXRefSection base)
            throws IOException {
        List<Revision> revisions = getRevisions(doc);
        List<Revision> result = new ArrayList<Revision>();
        boolean include = false;
        for (Revision revision : revisions) {
            if (include) {
                result.add(revision);
            } else {
                if (revision.getXRefSection() == base) {
                    include = true;
                }
            }
        }
        return result;
    }

    /**
     * A collection of {@link ILocator} instances, representing the versions
     * created when writing incrementally.
     *
     * @param doc The original document.
     * @return A collection of {@link ILocator} instances, representing the
     * versions created when writing incrementally.
     * @throws IOException
     * @throws COSLoadException
     */
	public static List<ILocator> getVersions(COSDocument doc) throws IOException {
        List<Revision> revisions = getRevisions(doc);
        List<ILocator> result = new ArrayList<ILocator>();
        for (Revision revision : revisions) {
            result.add(revision.createLocator());
        }
        return result;
    }

    protected static boolean readUptoNewLine(IRandomAccess input) throws IOException {
        int i;
        while (true) {
            i = input.read();
            if (i == -1) {
                return false;
            }
            if (PDFParser.isEOL(i)) {
                if (i == PDFParser.CHAR_CR) {
                    i = input.read();
                    if (i != PDFParser.CHAR_LF) {
                        input.seekBy(-1);
                    }
                }
                return true;
            }
        }
    }

    protected static long searchNextEOF(IRandomAccess input, long start, long end)
            throws IOException {
        input.seek(start);
        int comparisonIndex = 0;
        for (int i = input.read(); (i != -1) && (input.getOffset() < end); i = input.read()) {
            if (i == PDFParser.TOKEN_EOF[comparisonIndex]) {
                comparisonIndex++;
                if (comparisonIndex == PDFParser.TOKEN_EOF.length) {
                    readUptoNewLine(input);
                    return input.getOffset();
                }
            } else {
                comparisonIndex = 0;
            }
        }
        return -1;
    }

    /**
     * Tries to force a dictionary out of the COSObject.
     *
     * @param object The object to be cast to a {@link COSDictionary}
     * @return {@link COSDictionary} or null
     */
    public static COSDictionary toDictionary(COSObject object) {
        COSDictionary dict = null;
        if (object instanceof COSDictionary) {
            dict = (COSDictionary) object;
        } else if (object instanceof COSStream) {
            dict = ((COSStream) object).getDict();
        }
        return dict;
    }

    private COSTools() {
        super();
    }
}
