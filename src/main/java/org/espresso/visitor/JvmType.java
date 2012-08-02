package org.espresso.visitor;

import org.apache.bcel.generic.L2D;

import java.sql.SQLException;

/**
 * Enum representing the possible types handled by the JVM, along with several helper methods
 * to crete the different string representations for those types (e.g., as parameters, as
 * characters, etc.).
 * <p>
 * Also supplies a series of helper methods to handle conversions to different representations
 * required by the JVM, as well as some snippet manipulation.
 * 
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public enum JvmType {
    BOOLEAN('Z'),
    BYTE('B'),
    CHAR('C'),
    DOUBLE('D'),
    FLOAT('F'),
    INTEGER('I'),
    LONG('J'),
    OBJECT('L'),
    SHORT('S'),
    VOID('V'),
    ARRAY('[');

    private final char jvmType;

    /**
     * Constructor, uses a char to represent the type as per the JVM standard.
     * @param jvmType Char representing the type.
     */
    JvmType(final char jvmType) {
        this.jvmType = jvmType;
    }

    /**
     * This enum as the corresponding JVM char
     * @return the char
     */
    public char asJvmTypeChar() {
        return jvmType;
    }

    /**
     * Booleans, bytes, characters and shorts are represented as integers in the JVM stack. 
     * @return the type as it is shows up in the stack.
     */
    public JvmType asStackType() {
        if (jvmType == 'Z' || jvmType == 'B' || jvmType == 'C' || jvmType == 'S')
            return INTEGER;
        return this;
    }

    /**
     * Converts a class to the JVM type
     * @param clazz class to be converted
     * @return the corresponding JVM type
     */
    public static JvmType classToJvmType(final Class clazz) {
        if (clazz == boolean.class)
            return BOOLEAN;
        if (clazz == byte.class)
            return BYTE;
        if (clazz == char.class)
            return CHAR;
        if (clazz == double.class)
            return DOUBLE;
        if (clazz == float.class)
            return FLOAT;
        if (clazz == int.class)
            return INTEGER;
        if (clazz == long.class)
            return LONG;
        if (clazz == short.class)
            return SHORT;
        if (clazz == void.class)
            return VOID;
        return OBJECT;
    }

    /**
     * Converts a class name to the JVM representation. For example, com.foo.TheClass becomes
     * Lcom/foo/TheClass;
     * @param clazz class to convert
     * @return the JVM representation of the class
     */
    public static String classToJvmStringType(final Class clazz) {
        return "L" + clazz.getCanonicalName().replace('.', '/') + ";";
    }

    /**
     * Given a type and and a class, generates the corresponding JVM representation for the
     * signature of the corresponding getter. For example, com.foo.Bar getBar() becomes
     * ()Lcom/foo/Bar;, and int getInt() becomes ()I
     * @param returnType the type the getter returns 
     * @param clazz class name, used when the return type is an object
     * @return
     */
    public static String getterSignature(final JvmType returnType, final Class clazz) {
        if (OBJECT == returnType)
            return "()" + classToJvmStringType(clazz);
        return "()" + returnType.asJvmTypeChar();
    }

    /**
     * Given the return type of a method, and a list of code snippets that leave a type on the
     * stack, generate the signature of a method that will consume the stack and return the type
     * passed as parameter. For example, int doIt(int, long, double) will generate (IJD)I, while
     * String 
     * @param returnType
     * @param parameters
     * @return
     */
    public static String buildSignature(final JvmType returnType, final CodeSnippet... parameters) {
        final StringBuilder builder = new StringBuilder("(");
        for (final CodeSnippet parameter : parameters)
            if (OBJECT == parameter.getJvmType())
                builder.append(classToJvmStringType(parameter.getClazz()));
            else
                builder.append(parameter.getJvmType().asJvmTypeChar());
        builder.append(')').append(returnType.asJvmTypeChar());
        return builder.toString();
    }

    /**
     * Given a set of types on the stack, picks a type that can hold all of them. For example,
     * int, long, double picks double; int, long picks long. For numeric types, always chooses
     * long or double to simplify processing.
     * <br>
     * This is used to "normalize" different code snippets to a common type, so the appropriate
     * conversion instruction can be appended where needed before the snippets can be combined.
     * 
     * @param snippets list of snippets
     * @return the all encompassing type.
     * @throws SQLException when there is no all encompassing type.
     */
    public static JvmType targetType(final CodeSnippet... snippets) throws SQLException {
        int target = 0;
        for (final CodeSnippet snippet : snippets)
            switch (snippet.getJvmType()) {
                case BYTE:
                case CHAR:
                case INTEGER:
                case LONG:
                case SHORT:
                    target |= 1;
                    break;
                case FLOAT:
                case DOUBLE:
                    target |= 2;
                    break;
                default:
                    target |= 4;
            }
        if (1 == target)
            return LONG;
        if (2 == target || 3 == target)
            return DOUBLE;
        if (4 == target)
            return OBJECT;
        throw new SQLException("Invalid combination of types in expression (INT:1/FLT:2/*:4): " + target);
    }

    /**
     * If the target type is double and the snippet places a long on the stack, appends the 
     * instruction that converts from long to double.
     * 
     * @param snippet code snippet
     * @param target target type for that snippet
     */
    public static void normalizeTopOfStackType(final CodeSnippet snippet, final JvmType target) {
        if (DOUBLE == target && LONG == snippet.getJvmType())
            snippet.append(new L2D());
    }

}
