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

/**
 * A low level interface for polymorphic access to an object containing a
 * COSObject.
 * <p>
 * This is implemented for example by {@link COSArray}.
 */
public interface ICOSContainer {
    /**
     * It is the responsibility of the current container to create the
     * association with the new one.
     * <p>
     * The table shows the available transitions
     * <p>
     * {@code
     * | composite  | indirect   |
     * |
     * constant  | n.a.       | n.a.       | (always copied before by &quot;containable&quot;)
     * null      | ok         | ok         |
     * composite | error      | ok         |
     * indirect  | ok         | ok         |
     * }
     *
     * @param newContainer
     * @param object
     * @return The resulting {@link ICOSContainer} for {@code object}
     */
    ICOSContainer associate(ICOSContainer newContainer, COSObject object);

    /**
     * The stand-in to be used when object should be contained in a container.
     * This is either the object itself or the COSIndirectObject to it.
     *
     * @param object THe object whose containable is requested.
     * @return The stand-in to be used when object should be contained in a
     * container.
     */
    COSDocumentElement containable(COSObject object);

    /**
     * It is the responsibility of the current container to remove the
     * association from the old one.
     * <p>
     * The table shows the available transitions.
     * <p>
     * <pre>
     *              | composite  | indirect   |
     *              |
     *    constant  | n.a.       | n.a.       |
     *    null      | n.a.       | n.a.       |
     *    composite | ok         | n.a.       |
     *    indirect  | ok         | n.a.       |
     * </pre>
     *
     * @param oldContainer
     * @param object
     * @return The resulting {@link ICOSContainer} for {@code object}
     */
    ICOSContainer disassociate(ICOSContainer oldContainer, COSObject object);

    /**
     * The COSDocument instance where the ICOSContainer is contained.
     *
     * @return The COSDocument instance where the ICOSContainer is contained.
     */
    COSDocument getDoc();

    /**
     * "Harden" the reference to {@code object}, keeping it from being
     * garbage collected even if (temporarily) not accessed. Otherwise a
     * container may decide to "swap" its descendants out of memory.
     * <p>
     * Hardening uses a counter to decide if a strong reference can be finally
     * released.
     *
     * @param object The object that should be kept in memory.
     */
    void harden(COSObject object);

    /**
     * The number of references to the contained object. This method returns -1
     * when the value can not be determined (as for indirect objects parsed from
     * a file).
     *
     * @return The number of references to the contained object.
     */
    int referenceCount();

    /**
     * Switch a contained object to an indirect one. Update the reference.
     *
     * @param object The object to be indirect
     */
    COSIndirectObject referenceIndirect(COSObject object);

    /**
     * It is the responsibility of the active container to register object in
     * its data structures.
     *
     * @param object The new object to be registered in the hierarchy.
     */
    void register(COSDocumentElement object);

    /**
     * Restore the save state for the container.
     *
     * @param container
     * @return The "before" state of the receiver.
     */
    ICOSContainer restoreStateContainer(ICOSContainer container);

    /**
     * Create a save state for the container when saving the COSObject state.
     *
     * @return The save state for the container.
     */
    ICOSContainer saveStateContainer();

    /**
     * "Soften" the reference to {@code object}, making it accessible
     * for swapping out / garbage collection if the counter for hardening is
     * zero.
     *
     * @param object The object that should be kept in memory.
     */
    void soften(COSObject object);

    /**
     * Propagate a change from a COSObject down in the hierarchy.
     */
    void willChange(COSObject object);
}
