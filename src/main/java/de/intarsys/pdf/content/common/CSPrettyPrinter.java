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
package de.intarsys.pdf.content.common;

import de.intarsys.pdf.content.CSContent;
import de.intarsys.pdf.content.CSOperation;
import de.intarsys.pdf.content.CSOperators;

/**
 * A simple pretty printer for content streams.
 */
public class CSPrettyPrinter {
    private static final int COL_WIDTH = 50;

    /**
     * The string buffer containing the current serialized rendering program
     */
    private StringBuilder sb;

    private StringBuilder lb;

    private boolean createComment = false;

    private boolean createLineSeparator = true;

    private boolean createIndent = true;

    /**
     * The line separator to use
     */
    private String lineSeparator = System.getProperty("line.separator"); //$NON-NLS-1$

    /**
     * The current indent. The pretty printer indents the program for each
     * save/restore graphic state operation pair.
     */
    private int indent = 0;

    public String getValue() {
        return sb.toString();
    }

    public void print(CSContent content) {
        sb = new StringBuilder();
        lb = new StringBuilder();
        int len = content.size();
        for (int i = 0; i < len; i++) {
            CSOperation operation = content.getOperation(i);
            printOperation(operation);
        }
    }

    protected void printOperation(CSOperation operation) {
        lb.setLength(0);
        if (operation.matchesOperator(CSOperators.CSO_Q)) {
            indent--;
        }
        if (isCreateIndent()) {
            for (int i = 0; i < indent; i++) {
                lb.append("   "); //$NON-NLS-1$
            }
        }
        String opString = operation.toString();
        lb.append(opString);
        if (isCreateComment()) {
            for (int i = lb.length(); i < COL_WIDTH; i++) {
                lb.append(" "); //$NON-NLS-1$
            }
            lb.append("% "); //$NON-NLS-1$
            String opDescription = CSOperators.getDescription(operation.getOperator());
            lb.append(opDescription);
        }
        if (isCreateLineSeparator() || isCreateComment() || isCreateIndent()) {
            lb.append(lineSeparator);
        } else {
            lb.append(" "); //$NON-NLS-1$
        }
        if (operation.matchesOperator(CSOperators.CSO_q)) {
            indent++;
        }
        sb.append(lb);
    }

    public boolean isCreateComment() {
        return createComment;
    }

    public void setCreateComment(boolean createComment) {
        this.createComment = createComment;
    }

    public boolean isCreateLineSeparator() {
        return createLineSeparator;
    }

    public void setCreateLineSeparator(boolean separateLines) {
        this.createLineSeparator = separateLines;
    }

    public boolean isCreateIndent() {
        return createIndent;
    }

    public void setCreateIndent(boolean createIndent) {
        this.createIndent = createIndent;
    }
}
