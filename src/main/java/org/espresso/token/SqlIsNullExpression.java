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
 * Represents the "is null" or "is not null" predicate
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlIsNullExpression<E>
        extends SqlExpression<E> {
    private final SqlColumn column;
    private final boolean isNull;
    public static final String IS_NULL_OPERATOR = "IS NULL";
    public static final String IS_NOT_NULL_OPERATOR = "IS NOT NULL";

    /**
     * Builds the predicate given the eval. Only is null or is not null are acceptable.
     *
     * @param isNull whether this is an "IS NULL" or "IS NOT NULL" predicate
     * @param column The column that is or is not null
     */
    public SqlIsNullExpression(final SqlColumn column, final boolean isNull) {
        this.column = column;
        this.isNull = isNull;
    }

    /**
     * Accessor to the column that is or is not null
     *
     * @return the column
     */
    public SqlColumn getColumn() {
        return column;
    }

    @Override
    public String getOperator() {
        return isNull ? IS_NULL_OPERATOR : IS_NOT_NULL_OPERATOR;
    }

    /**
     * String representation as in "IS NULL" or "IS NOT NULL"
     *
     * @return the representation of this predicate
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("(");
        builder.append(column.toString()).append(' ').append(getOperator()).append(')');
        return builder.toString();
    }

    /**
     * Checks if the value in the current corresponding to the column is null.
     *
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return true or false, depending on the test
     * @throws SQLException
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        final Object operand = column.eval(row, functions);
        return isNull ? null == operand : null != operand;
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
