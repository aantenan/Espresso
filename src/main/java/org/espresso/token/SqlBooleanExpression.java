package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.util.Map;

/**
 * Encapsulates a boolean operation, i.e., AND, OR or NOT.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlBooleanExpression<E> extends SqlExpression<E> {
    private final SqlBooleanOperator operator;

    public SqlBooleanExpression(final SqlBooleanOperator operator) {
        this.operator = operator;
    }

    @Override
    public String getOperator() {
        return operator.toString();
    }

    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        return operator.eval(row, functions, operands);
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
