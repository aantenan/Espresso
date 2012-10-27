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

import java.sql.SQLException;
import java.util.Map;

/**
 * Encapsulates a boolean operation, i.e., AND, OR or NOT.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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

    public SqlBooleanOperator getRawOperator() {
        return operator;
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
