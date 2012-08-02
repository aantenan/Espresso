package org.espresso;

import org.espresso.token.*;

import java.sql.SQLException;

/**
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
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
