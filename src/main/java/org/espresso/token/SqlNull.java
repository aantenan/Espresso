package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.util.Map;

/**
 * Represents the SQL NULL element
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlNull<E> implements SqlExpressionNode<E>, Comparable {
    public SqlNull() {}

    @Override
    public String toString() {
        return "NULL";
    }

    /**
     * Returns a null as the value for a SQL NULL
     * @param row Object from where to get the data (similar to a DB row)
     * @param functions Function extensions, passed down the expression tree
     * @return null
     * @throws SQLException in case of any errors
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        return null;
    }

    /**
     * Null is always less than any other value
     * @param target value to compare against
     * @return 0 if object is null, -1 otherwise
     */
    @Override
    public int compareTo(final Object target) {
        if (null == target)
            return 0;
        return -1;
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
