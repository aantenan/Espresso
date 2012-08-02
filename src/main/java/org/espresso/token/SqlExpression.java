package org.espresso.token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Implements the SqlExpressionNode interface, and it represents a node in a SQL condition.
 * SqlExpression is the building block that represents conditions as a tree that can be
 * traversed.
 * </p>
 * SqlExpression has an operator (e.g., "+" or "and") and a list of
 * operands, which in turn can also be SqlExpressionNodes.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public abstract class SqlExpression<E>
        implements SqlExpressionNode<E>, Iterable<SqlExpressionNode> {

    final List<SqlExpressionNode> operands = new ArrayList<SqlExpressionNode>();

    /**
     * Builds the base expression
     */
    public SqlExpression() {}

    /**
     * Accessor to the operator
     *
     * @return the operator
     */
    public abstract String getOperator();

    /**
     * Adds a new operand to the end of the operand list
     *
     * @param operand the operand to add, never null
     *
     * @throws IllegalArgumentException if a null operand is supplied
     */
    public void addOperand(final SqlExpressionNode operand) {
        if (null == operand)
            throw new IllegalArgumentException("SqlExpression: operand cannot be null");
        operands.add(operand);
    }

    /**
     * Iterator over the list of operands
     *
     * @return the iterator
     */
    @Override
    public ListIterator<SqlExpressionNode> iterator() {
        return operands.listIterator();
    }

    /**
     * String representation of this expression. Note that we always surround the representation
     * with parenthesis, so the precedence is clear. The general format is (operand1 operator
     * operand2 operator ...), which is suitable for most cases. Derived classes can override this
     * method when the default case does not match the SQL92 syntax.
     *
     * @return the string representation of this expression, surrounded by parenthesis
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("(");

        if (operands.isEmpty()) {
            builder.append(getOperator()).append(')');
            return builder.toString();
        }

        if (1 == operands.size()) {
            builder.append(getOperator()).append(operands.get(0)).append(')');
            return builder.toString();
        }

        final Iterator<SqlExpressionNode> iterator = operands.iterator();
        if (iterator.hasNext())
            builder.append(iterator.next().toString());
        while (iterator.hasNext())
            builder.append(' ').append(getOperator()).append(' ')
                    .append(iterator.next().toString());
        builder.append(')');
        return builder.toString();
    }

}
