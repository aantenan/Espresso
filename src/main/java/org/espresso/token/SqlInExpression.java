package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Representation of a SQL "in" predicate - column IN (option1, option2, ...)
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlInExpression<E>
        extends SqlExpression<E> {
    private final SqlColumn column;
    private Set inList = null;
    public final static String IN_OPERATOR = "IN";

    /**
     * Builds an in expression
     *
     * @param column the column that drives the predicate, never null
     *
     * @throws IllegalArgumentException if a null column is supplied
     */
    public SqlInExpression(final SqlColumn column) {
        if (null == column)
            throw new IllegalArgumentException("In: column cannot be null");
        this.column = column;
    }

    /**
     * Accessor to the column object
     *
     * @return the column object
     */
    public SqlColumn getColumn() {
        return column;
    }

    @Override
    public String getOperator() {
        return IN_OPERATOR;
    }

    /**
     * Returns the in predicate as (column IN (option1, option2, ...))
     *
     * @return the string representation of the in predicate
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("(");
        builder.append(column.toString()).append(" " + IN_OPERATOR + " (");
        final Iterator<SqlExpressionNode> iterator = operands.iterator();
        if (iterator.hasNext())
            builder.append(iterator.next().toString());
        while (iterator.hasNext())
            builder.append(", ").append(iterator.next().toString());
        builder.append("))");
        return builder.toString();
    }

    /**
     * Evaluates the current column and check if it belongs to the supplied in list.
     * Note that the operands from the base class are stored as a list; we convert
     * the list to a set the first time eval is called to speed up the check.
     *
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return true or false whether the value of the current column belongs to the in list
     * @throws SQLException wraps any kind of error that may occur
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        if (null == inList) {
            // In list initialized a la singleton style, the first time we access it.
            inList = new HashSet(operands.size());
            for (SqlExpressionNode element : operands)
                inList.add(element.eval(row, functions));
        }
        return inList.contains(column.eval(row, functions));
    }


    /**
     * Accept method for the visitor pattern. Call pre-, then visit, then post-
     * to give the visitor a chance to push/pop state associated with recursion.
     * @param visitor the visitor to this class.
     */
    @Override
    public void accept(final SqlNodeVisitor<E> visitor) throws SQLException {
        visitor.visit(this);
    }
}
