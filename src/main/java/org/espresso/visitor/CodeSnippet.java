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
    public void setJvmType(final JvmType jvmType) {
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

    @Override
    public Iterator<Instruction> iterator() {
        return code.iterator();
    }
}
