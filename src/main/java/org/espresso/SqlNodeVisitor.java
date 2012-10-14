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
package org.espresso;

import org.espresso.token.*;

import java.sql.SQLException;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public abstract class SqlNodeVisitor<E> {
    public abstract void visit(final SqlColumn<E> node) throws SQLException;
    public abstract void visit(final SqlDate<E> node) throws SQLException;
    public abstract void visit(final SqlFunction<E> node) throws SQLException;
    public abstract void visit(final SqlInExpression<E> node) throws SQLException;
    public abstract void visit(final SqlIsNullExpression<E> node) throws SQLException;
    public abstract void visit(final SqlLikeExpression<E> node) throws SQLException;
    public abstract void visit(final SqlNull<E> node) throws SQLException;
    public abstract void visit(final SqlNumber<E> node) throws SQLException;
    public abstract void visit(final SqlString<E> node) throws SQLException;


    public void visit(final SqlArithmeticExpression<E> node) throws SQLException {
        for (final SqlExpressionNode<E> nested : node)
            nested.accept(this);
    }

    public void visit(final SqlBetweenExpression<E> node) throws SQLException {
        int i = 0;
        for (final SqlExpressionNode<E> nested : node) {
            nested.accept(this);
            i += 1;
        }
        if (3 != i)
            throw new SQLException("BETWEEN requires 3 operands, got " + i);
    }

    public void visit(final SqlComparisonExpression<E> node) throws SQLException {
        for (final SqlExpressionNode<E> nested : node)
            nested.accept(this);
    }

    public void visit(final SqlBooleanExpression<E> node) throws SQLException {
        for (final SqlExpressionNode<E> nested : node)
            nested.accept(this);
    }

}
