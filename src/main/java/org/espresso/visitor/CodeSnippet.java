/*
 * Copyright 2012 Espresso Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.espresso.visitor;

import org.apache.bcel.generic.*;

import java.util.Iterator;

import static org.espresso.visitor.JvmType.*;

/**
 * Represents a snippet of JVM code. It holds a list of JVM instructions, and the type it
 * leaves on the top of the stack when the code is executed. If type is object, it also
 * holds the name of the class it leaves on the to of the stack.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class CodeSnippet implements Iterable<Instruction> {
    private final InstructionList code = new InstructionList();
    private JvmType jvmType;
    private Class clazz = null;

    /**
     * Builds a snippet that leaves jvmType/Class on top of the stack, and combines the several
     * snippets into one large snippet. Those are appended in the same order.
     *
     * @param jvmType Type left on the top of stack
     * @param clazz class name left on the top of the stack (used if jvmType == OBJECT)
     * @param snippets (optional) list of snippets to combine
     */
    public CodeSnippet(final JvmType jvmType, final Class clazz, final CodeSnippet... snippets) {
        this.jvmType = jvmType;
        this.clazz = clazz;
        for (final CodeSnippet snippet : snippets)
            code.append(snippet.getCode());
    }

    /**
     * Builds a snippet that leaves jvmType on top of the stack, and combines the several
     * snippets in to one larger snipped. Snippets are appended in teh same order.
     *
     * @param jvmType Type left on top of stack
     * @param snippets (optional) list of snippets
     */
    public CodeSnippet(final JvmType jvmType, final CodeSnippet... snippets) {
        this(jvmType, null, snippets);
    }
    
    /**
     * Appends a new instruction to the end of the list.
     *
     * @param instruction The instruction to appen
     * @return reference to the handle of the appended instruction.
     */
    public InstructionHandle append(final Instruction instruction) {
        return code.append(instruction);
    }
    
    public void append(final CodeSnippet snippet) {
        code.append(snippet.getCode());
    }

    /**
     * Given the snippet's target stack type, extend the snippet to make it long or double (if required)
     */
    public void extendType() {
        if (jvmType == INTEGER) {
            append(new I2L());
            setJvmType(LONG);
        }
        if (jvmType == FLOAT) {
            append(new F2D());
            setJvmType(DOUBLE);
        }
    }


    /**
     * Getter for the jvm type
     * @return the type
     */
    public JvmType getJvmType() {
        return jvmType;
    }

    /**
     * Setter for the jvm type
     * @param jvmType the type
     */
    void setJvmType(final JvmType jvmType) {
        this.jvmType = jvmType;
    }

    /**
     * Accessor to the code.
     * @return the code
     */
    public InstructionList getCode() {
        return code;
    }

    /**
     * Accessor to the class on the top of the stack.
     * @return the class
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * Setter for the class on the top of the stack
     * @param clazz the class
     */
    public void setClazz(final Class clazz) {
        this.clazz = clazz;
    }

    /**
     * Allows us to iterate over the instructions of this snippet.
     * @return the iterator
     */
    @Override
    public Iterator<Instruction> iterator() {
        return code.iterator();
    }
}
