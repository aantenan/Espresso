/*
 * Copyright 2012 Espresso Team
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;
import org.espresso.eval.NumberWrapper;

import java.sql.SQLException;
import java.util.Map;

/**
 * Implements an arithmetic expression, i.e., one that involves +, -, * or /
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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

    public SqlArithmeticOperator getRawOperator() {
        return operator;
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
