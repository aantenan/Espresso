package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;
import org.espresso.eval.SqlComparisonEvaluator;

import java.sql.SQLException;
import java.util.Map;

/**
 * Represents the possible comparisons (less than, greater than, etc.)
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlComparisonExpression<E> extends SqlExpression<E> {
    private final SqlComparisonOperator operator;
    private SqlComparisonEvaluator evaluator; // Memoize how to evaluate the comparison
    /**
     */
    public SqlComparisonExpression(final SqlComparisonOperator operator) {
        this.operator = operator;
    }

    public SqlComparisonOperator getRawOperator() {
        return operator;
    }

    @Override
    public String getOperator() {
        return operator.toString();
    }

    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        try {
            final Object left = operands.get(0).eval(row, functions);
            final Object right = operands.get(1).eval(row, functions);
            if (null == evaluator)
                evaluator = SqlComparisonEvaluator.pickEvaluator(left, right);
            return operator.eval(evaluator.compare(left, right));
        } catch (ClassCastException e) {
            throw new SQLException("At least one comparison side was not a comparable");
        } catch (final IndexOutOfBoundsException e) {
            throw new SQLException("Comparison requires two operators");
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
