package org.espresso.visitor;

import org.espresso.SqlNodeVisitor;
import org.espresso.eval.Evaluator;
import org.espresso.eval.EvaluatorHelper;
import org.espresso.token.*;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.*;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

import static org.espresso.visitor.JvmType.*;
import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;
import static org.apache.bcel.Constants.ACC_PUBLIC;


/**
 * Visitor that generates an instance of a class that executes the given SQL Statement
 * <br/>
 * This class is NOT thread safe. It is intended to be used once and only once.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 * TODO: See if we can make the visitor reusable.
 */
public class CompilerVisitor<E> extends SqlNodeVisitor<E> {
    private final Class<E> rowType;
    private final SqlExpressionNode root;

    private final ConstantPoolGen constPoolGen;
    private final ClassGen classGen;
    private final Deque<CodeSnippetList> codeStack = new ArrayDeque<CodeSnippetList>();

    // Used to generate unique Evaluator class names: Evaluator1, Evaluator2, etc.
    private static final String evaluatorFullName = Evaluator.class.getCanonicalName();
    private final String generatedClassName;
    private static final AtomicInteger generatedClassNumber = new AtomicInteger(0);
    
    private static final EvaluatorLoader evaluatorLoader = new EvaluatorLoader();

    /**
     * Constructs a compiler for a given node tyope and where clause
     * @param nodeType Type of node used to evaluate the where clause, needed for proper casting
     * @param root the parsed where clause as a tree that returns a boolean
     */
    public CompilerVisitor(final Class<E> nodeType, final SqlExpressionNode root) {
        this.rowType = nodeType;
        this.root = root;
        constPoolGen = new ConstantPoolGen();
        
        generatedClassName = evaluatorFullName + generatedClassNumber.incrementAndGet();
        this.classGen = new ClassGen(generatedClassName, "java/lang/Object", generatedClassName + ".java",
                ACC_PUBLIC, new String[] {evaluatorFullName}, constPoolGen);
    }

    /**
     * Compiles the where clause and returns an instance of a class that can evaluate it
     * @return Instance of a class that implements the Evaluator interface
     * @throws SQLException in case anything goes wrong.
     */
    public Evaluator compile()
            throws SQLException {

        codeStack.add(new CodeSnippetList());
        root.accept(this);
        final InstructionList instructionList = codeStack.getFirst().asInstructionList();
        instructionList.append(new IRETURN());
        
        // Create the "matches" method matching the Evaluator.matches declaration
        final MethodGen methodGen = new MethodGen(ACC_PUBLIC,
                org.apache.bcel.generic.Type.BOOLEAN,
                new Type[]{org.apache.bcel.generic.Type.OBJECT},
                null, "matches", generatedClassName, instructionList, constPoolGen);
        methodGen.setMaxLocals();
        methodGen.setMaxStack();
        // Adds the method to the class
        classGen.addMethod(methodGen.getMethod());

        // Create the constructor
        classGen.addEmptyConstructor(ACC_PUBLIC);

        // Done, print the class definition so we can sanity check what we just did
        final JavaClass javaClass = classGen.getJavaClass();
        System.out.println(javaClass);
        for (final Method method : javaClass.getMethods())
            System.out.println(method.getCode().toString(true));

        // Get the bytecode representing the class
        final byte[] bytecode = javaClass.getBytes();

        // Create a special class loader that knows only how to do one thing: create an instance
        // of the class we just created. First, define the class, then create a new instance.
        // Since we know our class implements the Evalator interface, we can safely cast it.
        try {
            return evaluatorLoader.getEvaluator(bytecode, generatedClassName);
        } catch (final Exception e) {
            throw new SQLException("Could not create compiled class", e);
        }
    }

    /**
     * Pushes the value of a column to the top of the stack - this is accomplished by
     * calling the corresponding getter. Note that because Evaluator.matches takes an
     * object as parameter, but we are assuming the actual type is rowType, we need to
     * check if the object can be, indeed, cast to rowType's class.
     * 
     * @param node Column node whose value we want to push to the stack
     * @throws SQLException if anything goes wrong
     */
    @Override
    public void visit(final SqlColumn node) throws SQLException {
        // Get the name of the getter for this column
        final String getter = convertToCamelBackGetter(node.getName());
        try {
            // Get the JVM signature for the getter, i.e., int getBlah() becomes ()I 
            final Class returnClassType = getGetterReturnType(rowType, getter);
            final JvmType signatureType = classToJvmType(returnClassType);

            // Push constants to the poll gen: class to cast to, and reference to the getter method
            final int castIndex = constPoolGen.addClass(rowType.getCanonicalName());
            final int getterIndex = constPoolGen.addMethodref(rowType.getCanonicalName(),
                    getter, getterSignature(signatureType, returnClassType));
            
            // Push reference to first parameter of Evaluator.matches(Object row) - the row
            final JvmType stackType = signatureType.asStackType();
            final CodeSnippet snippet = new CodeSnippet(stackType, returnClassType);
            snippet.append(new ALOAD(1));
            // Cast to rowType
            snippet.append(new CHECKCAST(castIndex));
            // Call the getter
            snippet.append(new INVOKEVIRTUAL(getterIndex));

            // We support long or double arithmetic - if the stack has int or float, convert
            snippet.extendType();

            // And make the top of the stack reflect what we now have.
            codeStack.peekFirst().append(snippet);
        } catch (NoSuchMethodException e) {
            throw new SQLException("Missing getter " + getter + " for column " + node.getName(), e);
        }
    }

    /**
     * Processes expressions of type column between this and that. We push a new list on the code stack
     * before calling super.visit(), so we are left three snippets: the call to the getter that
     * corresponds to the column, and the evaluation of two expressions that corresponds to "this" and
     * "that". We then consolidate the snippets, push the call to EvaluatorHelper.evalBetween to the
     * end, and add this to the snippet list on the top of the code stack.
     * @param node node to evaluate
     * @throws SQLException if something goes wrong
     */
    @Override
    public void visit(final SqlBetweenExpression node) throws SQLException {
        codeStack.addFirst(new CodeSnippetList());
        super.visit(node);
        
        final CodeSnippetList snippets = codeStack.removeFirst();
        // Stack has column value at the bottom, lower limit, and upper limit at the top
        // make helper method call  
        final CodeSnippet top = snippets.getSnippetAt(0);
        final CodeSnippet middle = snippets.getSnippetAt(1);
        final CodeSnippet bottom = snippets.getSnippetAt(2);
        
        final String signature = buildSignature(JvmType.BOOLEAN, top, middle, bottom);
        
        // Add reference to the method to the pool gen and make the call
        final int helperIndex = constPoolGen.addMethodref(EvaluatorHelper.class.getCanonicalName(),
                "evalBetween", signature);
        final CodeSnippet snippet = new CodeSnippet(JvmType.BOOLEAN, top, middle, bottom);
        snippet.append(new INVOKESTATIC(helperIndex));
        codeStack.peekFirst().append(snippet);
    }

    @Override
    public void visit(final SqlDate node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(final SqlFunction node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(final SqlInExpression node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(final SqlIsNullExpression node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(final SqlLikeExpression node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visit(final SqlNull node) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * For numbers, we add a snippet to the top of the code stack that pushes the
     * constant to the top of the JVM stack.
     * @param node
     */
    @Override
    public void visit(final SqlNumber node) {
        int index;
        CodeSnippet snippet;
        // Add a double or a long reference to a constant, make our stack reflect what it is
        if (node.getNumber().isDouble()) {
            index = constPoolGen.addDouble(node.getNumber().asDouble());
            snippet = new CodeSnippet(DOUBLE);
        } else {
            index = constPoolGen.addLong(node.getNumber().asLong());
            snippet = new CodeSnippet(LONG);
        }
        // Load the constant to the top of the stack
        snippet.append(new LDC2_W(index));
        codeStack.peekFirst().append(snippet);
    }

    /**
     * For strings, we add a snippet to the top of the stack that pushes the
     * reference to the string to the top of the JVM stack.
     * @param node The string to push
     */
    @Override
    public void visit(final SqlString node) {
        // This one is easy: add the string constant to the pool gen; add the
        // instruction to push it to the JVM stack
        final int index = constPoolGen.addString(node.getString());
        final CodeSnippet snippet = new CodeSnippet(OBJECT, String.class);
        snippet.append(new LDC(index));
        codeStack.peekFirst().append(snippet);
    }

    /**
     * Pushes a new list of snippet to the top of the code stack, then call
     * super.visit(). This will create a series of snippets for each element of the
     * expression. For example a + b + c + d generates four snippets. Then evaluate
     * the target type depending on the snippet (long or double), append the appropriate
     * conversions where required, and combine the snippets and the corresponding
     * arithmetic expression.
     * @param node node with the expression
     * @throws SQLException if something goes wrong
     */
    @Override
    public void visit(final SqlArithmeticExpression node) throws SQLException {
        codeStack.addFirst(new CodeSnippetList());
        super.visit(node);
        final CodeSnippetList snippets = codeStack.removeFirst();

        // Adjust for the target type, it will be long or double; then append the 
        // appropriate instruction at the end of the snippets - except the first,
        // as we need two elements in the stack before we can operate on them
        final JvmType targetType = targetType(snippets);
        Class targetClazz = null;
        boolean isFirst = true;
        for (final CodeSnippet snippet : snippets) {
            normalizeTopOfStackType(snippet, targetType);
            if (isFirst) {
                isFirst = false;
                targetClazz = snippet.getClazz();
            } else
                snippet.append(node.getRawOperator().getInstruction(targetType == DOUBLE));
        }

        codeStack.peekFirst().append(snippets.asSnippet(targetType, targetClazz));
        
    }

    @Override
    public void visit(final SqlBooleanExpression node) throws SQLException {
        super.visit(node);
    }

    /**
     * Comparisons have a left and a righ hand side. We push a new list to the top of
     * the code stack, then combine them along with a call to EvaluatorHelper.evalCompare.
     * @param node the comparison node
     * @throws SQLException if something goes wrong
     */
    @Override
    public void visit(final SqlComparisonExpression node) throws SQLException {
        codeStack.addFirst(new CodeSnippetList());
        super.visit(node);
        
        final CodeSnippetList snippets = codeStack.removeFirst();

        // Objects are handled as Comparable, regardless of their type
        final CodeSnippet lhs = snippets.getSnippetAt(0);
        if (lhs.getJvmType() == OBJECT)
            lhs.setClazz(Comparable.class);
        final CodeSnippet rhs = snippets.getSnippetAt(1);
        if (rhs.getJvmType() == OBJECT)
            rhs.setClazz(Comparable.class);
        final CodeSnippet comparison = new CodeSnippet(OBJECT, SqlComparisonOperator.class);

        // We will make a static call to EvaluatorHelper.evalBetween, figure out the signature
        // It will be something like (org/espresso/token/SqlComparisonOperator;)Z or
        // (Ljava/lang/Comparable;Ljava/lang/Comparable;Lorg/espresso/token/SqlComparisonOperator;)Z
        final String signature = buildSignature(BOOLEAN, lhs, rhs, comparison);

        // Add reference to the method to the pool gen and make the call
        final int enumIndex = constPoolGen.addFieldref(
                SqlComparisonOperator.class.getCanonicalName().replace('.', '/'),
                node.getRawOperator().name(), classToJvmStringType(SqlComparisonOperator.class));
        final int helperIndex = constPoolGen.addMethodref(EvaluatorHelper.class.getCanonicalName(),
                "evalCompare", signature);

        final CodeSnippet snippet = new CodeSnippet(BOOLEAN);
        snippet.append(new GETSTATIC(enumIndex));
        snippet.append(new INVOKESTATIC(helperIndex));

        // Top of stack now has a boolean
        final CodeSnippetList tos = codeStack.peekFirst();
        tos.append(lhs);
        tos.append(rhs);
        tos.append(snippet);
    }

    /**
     * Helper to convert a column NAME_LIKE_THIS to getNameLikeThis.
     * @param name column name
     * @return getter name
     */
    private static String convertToCamelBackGetter(final String name) {
        final StringBuilder builder = new StringBuilder("get");
        boolean toUpper = true;
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if ('_' == c)
                toUpper = true;
            else {
                builder.append(toUpper ? toUpperCase(c) : toLowerCase(c));
                toUpper = false;
            }
        }
        return builder.toString();
    }

    /**
     * Determines the return type of a given getter.
     * @param clazz class where the getter is called
     * @param getter name of the getter
     * @return class returned by the getter
     * @throws NoSuchMethodException if there is no such getter
     */
    private static Class getGetterReturnType(final Class clazz, final String getter)
            throws NoSuchMethodException {
        return clazz.getMethod(getter).getReturnType();
    }

    /**
     * Helper class that creates an instance of an Evaluator based on the supplied bytecode
     */
    private static class EvaluatorLoader extends ClassLoader {
        public final Evaluator getEvaluator(final byte[] bytecode, final String className) 
                throws IllegalAccessException, InstantiationException {
            return (Evaluator) (defineClass(className, bytecode, 0, bytecode.length)).
                    newInstance();
        }
    }


}
