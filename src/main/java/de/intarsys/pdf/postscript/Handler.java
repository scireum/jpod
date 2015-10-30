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
package de.intarsys.pdf.postscript;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

/**
 * <em>Very</em> simple class to execute postscript code as it it parsed (which
 * means to execute it again you have to parse it again)
 */
public class Handler {

    private PSArray procedure;

    private Stack stack;

    public Handler() {
        super();
        stack = new Stack();
    }

    public Object get(int index) {
        return stack.get(index);
    }

    public Object peek() {
        return stack.peek();
    }

    public Object pop() {
        return stack.pop();
    }

    public List popResult() {
        List result;
        Iterator iterator;

        result = new ArrayList(stack.size());
        iterator = stack.iterator();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        stack.removeAllElements();
        return result;
    }

    public void processEndArray() {
        throw new UnsupportedOperationException();
    }

    public void processEndProcedure() {
        stack.push(procedure);
        procedure = null;
    }

    public void processIdentifier(String string) throws ParseException {
        if (procedure != null) {
            procedure.add(new PSName(string));
            return;
        }

        String name;
        Class clazz;
        IOperator operator;

        name = MessageFormat.format("de.intarsys.pdf.postscript.Operator_{0}", //$NON-NLS-1$
                                    string);
        try {
            clazz = Class.forName(name);
        } catch (Exception ex) {
            throw new ParseException(ex);
        }
        // all operator classes should have an "Instance" field by convention
        try {
            operator = (IOperator) clazz.getField("Instance").get(null); //$NON-NLS-1$
        } catch (Exception ex) {
            throw new ParseException(ex);
        }
        operator.execute(this);
    }

    public void processImmediateIdentifier(String string) {
        throw new UnsupportedOperationException();
    }

    public void processKeyIdentifier(String string) {
        throw new UnsupportedOperationException();
    }

    public void processLiteral(double d) {
        if (procedure != null) {
            procedure.add(new PSPrimitiveObject(d));
            return;
        }
        stack.push(d);
    }

    public void processLiteral(int i) {
        if (procedure != null) {
            procedure.add(new PSPrimitiveObject(i));
            return;
        }
        stack.push(i);
    }

    public void processLiteral(Object object) {
        if (procedure != null) {
            procedure.add(new PSPrimitiveObject(object));
            return;
        }
        stack.push(object);
    }

    public void processStartArray() {
        throw new UnsupportedOperationException();
    }

    public void processStartProcedure() {
        procedure = new PSArray();
    }

    public void push(Object value) {
        stack.push(value);
    }

    public void pushArgs(List args) {
        stack.addAll(args);
    }

    public int size() {
        return stack.size();
    }
}
