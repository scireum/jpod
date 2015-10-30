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

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is a cos level representation of the elements that may be contained in a
 * cos container. An element may be either a {@link
 * de.intarsys.pdf.cos.COSObject} or a
 * {@link de.intarsys.pdf.cos.COSIndirectObject} to a
 * {@link de.intarsys.pdf.cos.COSObject}. A
 * {@link de.intarsys.pdf.cos.COSIndirectObject} is never seen by an application
 * level programmer, this is an internal construct only.
 */
abstract public class COSDocumentElement implements ICOSExceptionHandler {
    protected COSDocumentElement() {
        //
    }

    /**
     * Accept a visitor object. The receiver selects the correct implementation
     * in the <code>visitor</code> by "double dispatching".
     *
     * @param visitor The object visiting the receiver.
     * @return Object An object depending on the visitor semantics.
     * @throws COSVisitorException An exception depending on the visitor semantics.
     */
    abstract public Object accept(ICOSObjectVisitor visitor) throws COSVisitorException;

    /**
     * Add a backward reference to the container when the receiver is added to a
     * container object. The implementation depends on the type of containment
     * for the object so far (direct/indirect), so we delegate to the old
     * container.
     *
     * @param newContainer the new container embedding the object
     * @return The new {@link ICOSContainer} associated with this.
     */
    abstract protected ICOSContainer addContainer(ICOSContainer newContainer);

    /**
     * The {@link COSDocumentElement} suitable for use in an
     * {@link ICOSContainer}. This may be a {@link COSIndirectObject} or the
     * {@link COSObject} itself if not indirect.
     * <p>
     * This method should not be used by the application programmer. This is
     * called in the {@link COSObject} lifecycle to ensure internal consistency.
     */
    abstract public COSDocumentElement containable();

    /**
     * see copyDeep()
     * <p>
     * <p>
     * This method keeps track of already copied objects to deal with cyclic
     * references.
     * </p>
     *
     * @see de.intarsys.pdf.cos.COSObject#copyDeep()
     */
    abstract protected COSObject copyDeep(Map copied);

    abstract protected COSDocumentElement copyShallowNested();

    /**
     * Return the real object. This is either the object itself or the object
     * referenced by a reference object ({@link COSIndirectObject}).
     *
     * @return The real object.
     */
    abstract public COSObject dereference();

    /**
     * Structurally compares another object with this one while keeping track of
     * the visited objects. The default implementation simply delegates to
     * {@link #equals(Object)}. Subclassers may override to register pairs in
     * order to prevent infinite loops while walking the COS structure and
     * to avoid expensive comparisons of objects that have already been
     * compared.
     *
     * @param o       the object with which to compare
     * @param visited a register of pairs of {@link COSObject}s that have
     *                already been visited in the comparison
     * @return {@code true} if this object is the same as {@code} o
     */
    protected boolean equals(Object o, PairRegister visited) {
        return this.equals(o);
    }

    /**
     * A register for unordered pairs of {@link COSObject}s that have been
     * visited in a comparison of a COS structure.
     */
    protected static class PairRegister {
        /**
         * <p>A mapping of {@code a}s to sets of {@code b}s that together form
         * pairs {@code (a, b)}.</p>
         */
        private Map<COSObject, Set<COSObject>> register = new IdentityHashMap<>();

        public PairRegister() {
            super();
        }

        /**
         * Checks whether this register contains the unordered pair of
         * {@link COSObject}s {@code (a, b)} -- that is either {@code (a, b)} or
         * {@code (b, a)} has already been registered. If this register doesn't
         * contain the pair and both, {@code a} and {@code b} are indirect
         * objects, then the pair is added to this registry. Note that this
         * implies that the result is always {@code false} if one of the objects
         * is direct.
         *
         * @param a one element of the pair to check
         * @param b the other element of the pair to check
         * @return {@code true} if both {@code a} and {@code b} are direct
         * objects and this registry already contains the unordered pair
         * {@code (a, b)}; {@code false} otherwise
         */
        public boolean check(COSObject a, COSObject b) {
            if (!a.isIndirect() || !b.isIndirect()) {
                return false;
            }

            if (contains(a, b) || contains(b, a)) {
                return true;
            }

            add(a, b);
            return false;
        }

        private void add(COSObject a, COSObject b) {
            Set<COSObject> set = register.get(a);
            if (set == null) {
                set = newSet();
                register.put(a, set);
            }

            set.add(b);
        }

        private <E> Set<E> newSet() {
            return Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
        }

        private boolean contains(COSObject a, COSObject b) {
            Set<COSObject> set = register.get(a);
            return set != null && set.contains(b);
        }
    }

    /**
     * The document where this is contained or null. A
     * {@link COSDocumentElement} is at most contained in a single
     * {@link COSDocument}.
     *
     * @return The document where this is contained.
     */
    abstract public COSDocument getDoc();

    /*
     * (non-Javadoc)
     *
     * @see de.intarsys.pdf.cos.ICOSExceptionHandler#handleException(de.intarsys.pdf.cos.COSRuntimeException)
     */
    public void handleException(COSRuntimeException ex) throws COSRuntimeException {
        COSDocument doc = getDoc();
        if (doc != null) {
            doc.handleException(ex);
        } else {
            throw ex;
        }
    }

    /**
     * Answer <code>true</code> if this element is a reference (a
     * {@link COSIndirectObject}.
     *
     * @return Answer <code>true</code> if this element is a reference.
     */
    public boolean isReference() {
        return false;
    }

    /**
     * Answer <code>true</code> if this elements content is swapped to a
     * persistent store.
     *
     * @return Answer <code>true</code> if this elements content is swapped to
     * a persistent store.
     */
    public boolean isSwapped() {
        return false;
    }

    /**
     * Register the all indirect objects that can be reached from this with doc
     *
     * @param doc The container document
     */
    abstract protected void registerWith(COSDocument doc);

    /**
     * Remove a backward reference to the container when the receiver is removed
     * from a container object. The implementation depends on the type of
     * containment for the object so far (direct/indirect), so we delegate to
     * the old container.
     *
     * @param oldContainer the container that no longer embeds the receiver
     * @return The new {@link ICOSContainer} associated with this.
     */
    abstract protected ICOSContainer removeContainer(ICOSContainer oldContainer);
}
