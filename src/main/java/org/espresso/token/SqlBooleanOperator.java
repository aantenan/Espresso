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

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Represents the possible boolean operators.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public enum SqlBooleanOperator {
    AND {
        @Override
        public String toString() {
            return "AND";
        }
        @Override
        public Object eval(final Object row, final Map<String, FunctionExtension> functions,
                final List<SqlExpressionNode> operands) throws SQLException {
            try {
                boolean result = true;
                for (SqlExpressionNode node : operands) {
                    result &= (Boolean) node.eval(row, functions);
                    if (!result)
                        return false;
                }
                return result;
            } catch (final ClassCastException e) {
                throw new SQLException("Boolean type expected", e);
            }
        }
    },
    OR {
        @Override
        public String toString() {
            return "OR";
        }
        @Override
        public Object eval(final Object row, final Map<String, FunctionExtension> functions,
                final List<SqlExpressionNode> operands) throws SQLException {
            try {
                boolean result = true;
                for (SqlExpressionNode node : operands) {
                    result |= (Boolean) node.eval(row, functions);
                    if (result)
                        return true;
                }
                return result;
            } catch (final ClassCastException e) {
                throw new SQLException("Boolean type expected", e);
            }
        }
    },
    NOT {
        @Override
        public String toString() {
            return "NOT";
        }
        @Override
        public Object eval(final Object row, final Map<String, FunctionExtension> functions,
                final List<SqlExpressionNode> operands) throws SQLException {
            try {
                final Boolean operand = (Boolean) operands.get(0).eval(row, functions);
                return !operand;
            } catch (final ClassCastException e) {
                throw new SQLException("Boolean type expected", e);
            } catch (final IndexOutOfBoundsException e) {
                throw new SQLException("NOT requires one and only one operand");
            }
        }
    };

    public abstract Object eval(final Object row, final Map<String, FunctionExtension> functions,
            final List<SqlExpressionNode> operands) throws SQLException;
}
