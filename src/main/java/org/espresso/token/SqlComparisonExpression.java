/*
 * Copyright 2012 Alberto Antenangeli
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
import org.espresso.eval.SqlComparisonEvaluator;

import java.sql.SQLException;
import java.util.Map;

/**
 * Represents the possible comparisons (less than, greater than, etc.)
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
