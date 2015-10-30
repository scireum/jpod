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

import de.intarsys.pdf.content.CSOperator;

/**
 * A classs enumerating the operators for a cmap program.
 */
public class CMapOperator {
    public static final CSOperator CMO_beginbfchar = CSOperator.create("beginbfchar"); //$NON-NLS-1$

    public static final CSOperator CMO_beginbfrange = CSOperator.create("beginbfrange"); //$NON-NLS-1$

    public static final CSOperator CMO_begincidchar = CSOperator.create("begincidchar"); //$NON-NLS-1$

    public static final CSOperator CMO_begincidrange = CSOperator.create("begincidrange"); //$NON-NLS-1$

    // list of well known operators
    public static final CSOperator CMO_begincmap = CSOperator.create("begincmap"); //$NON-NLS-1$

    public static final CSOperator CMO_begincodespacerange = CSOperator.create("begincodespacerange"); //$NON-NLS-1$

    public static final CSOperator CMO_beginnotdefchar = CSOperator.create("beginnotdefchar"); //$NON-NLS-1$

    public static final CSOperator CMO_beginnotdefrange = CSOperator.create("beginnotdefrange"); //$NON-NLS-1$

    public static final CSOperator CMO_def = CSOperator.create("def"); //$NON-NLS-1$

    public static final CSOperator CMO_endbfchar = CSOperator.create("endbfchar"); //$NON-NLS-1$

    public static final CSOperator CMO_endbfrange = CSOperator.create("endbfrange"); //$NON-NLS-1$

    public static final CSOperator CMO_endcidchar = CSOperator.create("endcidchar"); //$NON-NLS-1$

    public static final CSOperator CMO_endcidrange = CSOperator.create("endcidrange"); //$NON-NLS-1$

    public static final CSOperator CMO_endcmap = CSOperator.create("endcmap"); //$NON-NLS-1$

    public static final CSOperator CMO_endcodespacerange = CSOperator.create("endcodespacerange"); //$NON-NLS-1$

    public static final CSOperator CMO_endnotdefchar = CSOperator.create("endnotdefchar"); //$NON-NLS-1$

    public static final CSOperator CMO_endnotdefrange = CSOperator.create("endnotdefrange"); //$NON-NLS-1$

    public static final CSOperator CMO_usecmap = CSOperator.create("usecmap"); //$NON-NLS-1$

    public static final CSOperator CMO_usefont = CSOperator.create("usefont"); //$NON-NLS-1$

    /**
     * Enumeration class
     */
    private CMapOperator() {
        // this is a tool class
    }
}
