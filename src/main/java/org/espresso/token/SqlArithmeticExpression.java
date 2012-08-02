package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;
import org.espresso.eval.NumberWrapper;

import java.sql.SQLException;
import java.util.Map;

/**
 * Implements an arithmetic expression, i.e., one that involves +, -, * or /
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlArithmeticExpression<E> extends SqlExpression<E> {
    private final SqlArithmeticOperator operator;

    public SqlArithmeticExpression(final SqlArithmeticOperator operator) {
        this.operator = operator;
    }

    @Override
    public String getOperator() {
        return operator.toString();
    }

    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        try {
            boolean first = true;
            NumberWrapper result = null;
            for (SqlExpressionNode node : operands)
                if (first) {
                    result = new NumberWrapper(node.eval(row, functions));
                    first = false;
                } else
                    operator.eval(result, (NumberWrapper) node.eval(row, functions));
            return result;
        } catch (final ClassCastException e) {
            throw new SQLException("Numeric type expected", e);
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
