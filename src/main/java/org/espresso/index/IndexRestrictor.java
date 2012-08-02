package org.espresso.index;

import org.espresso.extension.DateExtension;
import org.espresso.token.*;

import java.sql.SQLException;
import java.util.Iterator;

/**
 * This class traverses a SqlExpression representing a where clause looking for opportunities to
 * restrict the V space based on indices.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public final class IndexRestrictor<T> {

    private final DateExtension dateProcessor;
    private final Indices<T> indices;

    public IndexRestrictor(final DateExtension dateProcessor, final Indices<T> indices) {
        this.dateProcessor = dateProcessor;
        this.indices = indices;
    }

    /**
     * Returns the collection that should be used to restrict the where clause - hopefully one that
     * is smaller than the original one passed as parameter. The collection is calculated based on
     * the where clause and the supporting indices. If the indices do not help, then simply return
     * the original collection.
     *
     * @param select the select statement over which to restrict
     * @param original the original (large) collection
     *
     * @return a smaller collection, or the original one if the indices do not help restricting it
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    public <V> Iterator<T> restrict(final SqlSelect select, final Iterator<T> original)
            throws SQLException {
        if (null == indices)
            return original;
        final Index<T, V> restricted = restrictSqlExpressionNode(select.getWhereClause());
        if (null == restricted)
            return original;
        return restricted.iterator();
    }

    /**
     * Traverses the expression recursively attempting to identify a smaller set
     *
     * @param exp the where clause (or part of it, since this is recursive)
     *
     * @return an Index representing a smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> restrictSqlExpressionNode(final SqlExpressionNode exp)
            throws SQLException {
        if (exp instanceof SqlExpression)
            return restrictSqlExpression((SqlExpression) exp);
        return null;
    }

    /**
     * Identifies the operator of an expression, and attempts to use the indices to come up with a
     * smaller set that matches that expression.
     *
     * @param sqlExpression where clause (or part of it)
     *
     * @return and Index representing a smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> restrictSqlExpression(final SqlExpression sqlExpression)
            throws SQLException {
        final String operator = sqlExpression.getOperator();
        final Iterator<SqlExpressionNode> operands = sqlExpression.iterator();
        if (SqlBooleanOperator.AND.name().equals(operator))
            return andRestriction(operands);
        if (SqlBooleanOperator.OR.name().equals(operator))
            return orRestriction(operands);
        if (SqlComparisonOperator.GE.toString().equals(operator) || SqlComparisonOperator.GT.toString().equals(operator))
            return greaterThanRestriction(operands);
        if (SqlComparisonOperator.LE.toString().equals(operator) || SqlComparisonOperator.LT.toString().equals(operator))
            return lessThanRestriction(operands);
        if (SqlComparisonOperator.EQ.toString().equals(operator))
            return equalRestriction(operands);
        if (SqlInExpression.IN_OPERATOR.equals(operator))
            return inRestriction(((SqlInExpression) sqlExpression).getColumn(), operands);
        if (SqlIsNullExpression.IS_NULL_OPERATOR.equals(operator))
            return isNullRestriction(((SqlIsNullExpression) sqlExpression).getColumn());
        if (SqlBetweenExpression.BETWEEN_OPERATOR.equals(operator))
            return isBetweenRestriction(operands);
        return null;
    }

    /**
     * Attempts to AND all operands of an expression using the support indices. Note that even when
     * the indices are not compatible, AND is still OK - just return the smaller set.
     *
     * @param operands the operands of the AND clause (a AND b AND c...)
     *
     * @return the smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> andRestriction(final Iterator<SqlExpressionNode> operands)
            throws SQLException {
        Index<T, V> result = null;
        while (operands.hasNext()) {
            final Index<T, V> temp = restrictSqlExpressionNode(operands.next());
            if (null == result)
                result = temp;
            else if (null != temp) {
                final Index<T, V> intersection = result.intersection(temp);
                if (null != intersection)
                    result = intersection;
                else if (temp.size() < result.size())
                    result = temp;
            }
        }
        return result;
    }

    /**
     * Attempts to OR all operands of an expression using the support indices. Note that OR requires
     * the indices to be compatible with each other - if they are not, just return null.
     *
     * @param operands the operands of the OR clause (a OR b OR c...)
     *
     * @return the smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> orRestriction(final Iterator<SqlExpressionNode> operands)
            throws SQLException {
        Index<T, V> result = null;
        while (operands.hasNext()) {
            final Index<T, V> temp = restrictSqlExpressionNode(operands.next());
            if (null == temp)
                return null;
            if (null == result)
                result = temp;
            else
                result = result.union(temp);
        }
        return result;
    }

    /**
     * Attempts to use the support indices to return a smaller set based on > or >= restriction.
     * Because this is a binary operator, there will always be two elements in operands. One of them
     * should be a column, the other one a value. If that's not the case, simply return null, as we
     * cannot restrict based on indices. Note that we reverse the logic based on which one comes
     * first - the column or the value.
     *
     * @param operands the (single) operand (column > operand or operand < column)
     *
     * @return the smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> greaterThanRestriction(final Iterator<SqlExpressionNode> operands)
            throws SQLException {
        final SqlExpressionNodePair exp = new SqlExpressionNodePair(operands);
        if (exp.left instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.left);
            if (null == index)
                return null;
            final V value = valueFor(exp.right);
            if (null == value)
                return null;
            return index.greaterThan(value);
        } else if (exp.right instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.right);
            if (null == index)
                return null;
            final V value = valueFor(exp.left);
            if (null == value)
                return null;
            return index.lessThan(value);
        }
        return null;
    }

    /**
     * Attempts to use the support indices to return a smaller set based on < or <= restriction.
     *
     * @param operands the (single) operand (column < operand)
     *
     * @return the smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> lessThanRestriction(final Iterator<SqlExpressionNode> operands)
            throws SQLException {
        final SqlExpressionNodePair exp = new SqlExpressionNodePair(operands);
        if (exp.left instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.left);
            if (null == index)
                return null;
            final V value = valueFor(exp.right);
            if (null == value)
                return null;
            return index.lessThan(value);
        } else if (exp.right instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.right);
            if (null == index)
                return null;
            final V value = valueFor(exp.left);
            if (null == value)
                return null;
            return index.greaterThan(value);
        }
        return null;
    }

    /**
     * Attempts to use the support indices to return a smaller set based on = restriction.
     *
     * @param operands the (single) operand (column = operand)
     *
     * @return the smaller collection, or null if the indices do not help
     * @throws java.sql.SQLException if where cause is badly constructed. Shouldn't happen, as the
     * parsing of the SQL statement should catch those cases.
     */
    private <V> Index<T, V> equalRestriction(final Iterator<SqlExpressionNode> operands)
            throws SQLException {
        final SqlExpressionNodePair exp = new SqlExpressionNodePair(operands);
        if (exp.left instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.left);
            if (null == index)
                return null;
            final V value = valueFor(exp.right);
            if (null == value)
                return null;
            return index.singleBucket(value);
        } else if (exp.right instanceof SqlColumn) {
            final Index<T, V> index = indexFor((SqlColumn) exp.right);
            if (null == index)
                return null;
            final V value = valueFor(exp.left);
            if (null == value)
                return null;
            return index.singleBucket(value);
        }
        return null;
    }

    /**
     * Attempts to use the support indices to return a smaller set based on in restriction. This is
     * equivalent to col = operand1 or col = operand2 or col = operand3...
     *
     * @param column the column that drives the IN clause
     * @param operands the operands as in IN (operand1, operand2, operand3...)
     *
     * @return the smaller collection, or null if the indices do not help a
     */
    private <V> Index<T, V> inRestriction(final SqlColumn column,
            final Iterator<SqlExpressionNode> operands) {
        final Index<T, V> index = indexFor(column);
        if (null == index)
            return null;
        Index<T, V> result = index.newIndex();
        while (null != result && operands.hasNext())
            result = result.union(index.singleBucket((V) valueFor(operands.next())));
        return result;
    }

    /**
     * Attempts to use the support indices to return a smaller set based on IS NULL restriction.
     *
     * @param column the colum for which we want to check the condition
     *
     * @return the smaller collection, or null if the indices do not help
     */
    private <V> Index<T, V> isNullRestriction(final SqlColumn column) {
        final Index<T, V> index = indexFor(column);
        if (null == index)
            return null;
        return index.singleBucket(null);
    }

    /**
     * Attempts to use the support indices to return a smaller set based BETWEEN restriction. First
     * operand is, by construction, required to be a column, followed by two values: column between
     * this and that.
     *
     * @param operands the (two) operands (column BETWEEN operand1 AND operand2)
     *
     * @return the smaller collection, or null if the indices do not help
     */
    private <V> Index<T, V> isBetweenRestriction(final Iterator<SqlExpressionNode> operands) {
        if (!operands.hasNext())
            return null;
        final SqlExpressionNode column = operands.next();
        if (!(column instanceof SqlColumn))
            return null;
        final Index<T, V> index = indexFor((SqlColumn) column);
        if (null == index)
            return null;
        if (!operands.hasNext())
            return null;
        final V lower = valueFor(operands.next());
        if (!operands.hasNext())
            return null;
        final V higher = valueFor(operands.next());
        final Index<T, V> lowerIndex = index.lessThan(higher);
        if (null == lowerIndex)
            return null;
        final Index<T, V> other = index.greaterThan(lower);
        return null == other ? null : lowerIndex.intersection(other);
    }

    /**
     * Returns the index associated with a column (if it exists)
     *
     * @param column the column used to lookup the index.
     *
     * @return the associated index, null if there isn't one.
     */
    private <V> Index<T, V> indexFor(final SqlColumn column) {
        return indices.indexFor(column.getName());
    }

    /**
     * Moves the operand iterator one step, and return the object corresponding to the operand.
     *
     * @param node the node for which we want the value
     *
     * @return the current object
     */
    private <V> V valueFor(final SqlExpressionNode node) {
        try {
            if (node instanceof SqlNumber)
                return (V) ((SqlNumber) node).getNumber();
            if (node instanceof SqlString)
                return (V) ((SqlString) node).getString();
            if (node instanceof SqlDate)
                return (V) dateProcessor.toDate(((SqlDate) node).getDateString());
            return null;
        } catch (final Exception e) {
            // Any weirdness with the expressions will be dealt with downstream.
            return null;
        }
    }

    private static final class SqlExpressionNodePair {
        final SqlExpressionNode left;
        final SqlExpressionNode right;

        public SqlExpressionNodePair(final Iterator<SqlExpressionNode> operands)
                throws SQLException {
            if (operands.hasNext())
                left = operands.next();
            else
                throw new SQLException("Binary operation missing left operand");

            if (operands.hasNext())
                right = operands.next();
            else
                throw new SQLException("Binary operation missing right operand");
        }
    }
}

