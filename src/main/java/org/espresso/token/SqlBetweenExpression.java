package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;
import org.espresso.eval.SqlComparisonEvaluator;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import static org.espresso.eval.SqlComparisonEvaluator.pickEvaluator;

/**
 * Represents the SQL 'between' predicate, i.e., something BETWEEN this AND that.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlBetweenExpression<E>
        extends SqlExpression<E> {
    private SqlComparisonEvaluator leftComparator = null;
    private SqlComparisonEvaluator rightComparator = null;
    public final static String BETWEEN_OPERATOR = "BETWEEN";

    public SqlBetweenExpression() {
    }

    @Override
    public String getOperator() {
        return BETWEEN_OPERATOR;
    }

    /**
     * Overrides the parent toString method to properly represent the BETWEEN predicate
     * @return the predicate representation, as "(identifier BETWEEN expression AND expression)"
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("(");
        final Iterator<SqlExpressionNode> iterator = operands.iterator();
        builder.append(iterator.next().toString()).append(" ").append(BETWEEN_OPERATOR).append(" ");
        builder.append(iterator.next().toString()).append(" AND ");
        builder.append(iterator.next().toString()).append(")");
        return builder.toString();
    }

    /**
     * Evaluates what BETWEEN lower AND higher
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return true or false depending on whether the condition was satisfied
     * @throws SQLException wraps all types of errors that may happen
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        if (3 != operands.size())
            throw new SQLException("BETWEEN requires 3 operands");
        final Iterator<SqlExpressionNode> iterator = operands.iterator();
        try {
            final Object what = iterator.next().eval(row, functions);
            if (null == what)
                return false;
            final Object lower = iterator.next().eval(row, functions);
            if (null == lower)
                return false;
            if (null == leftComparator)
                leftComparator = pickEvaluator(what, lower);
            if (-1 == leftComparator.compare(what, lower))
                return false;
            final Object right = iterator.next().eval(row, functions);
            if (null == right)
                return false;
            if (null == rightComparator)
                rightComparator = pickEvaluator(what, right);
            return 1 != rightComparator.compare(what, right);
        } catch (ClassCastException e) {
            throw new SQLException("BETWEEN operand could not be cast to Comparable", e);
        }
    }
    /**
     * Accept method for the visitor pattern, turn around and call visit on the visitor.
     * Pretty standard, nothing new here...
     *
     * @param visitor the visitor to this class
     */
    @Override
    public void accept(final SqlNodeVisitor<E> visitor) throws SQLException {
        visitor.visit(this);
    }
}
