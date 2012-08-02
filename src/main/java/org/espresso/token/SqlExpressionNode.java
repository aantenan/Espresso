package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.util.Map;

/**
 * This is an empty interface used to represent a "generic" node in a condition.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public interface SqlExpressionNode<E> {
    /**
     * Evaluates the expression against the parameter
     *
     * @param row Object from where to get the data (similar to a DB row)
     * @param functions Function extensions, passed down the expression tree
     * @return the result of evaluating the node against the object
     * @throws SQLException in case of errors (e.g., type mismatch: 1 and 2)
     */
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException;

    public void accept(final SqlNodeVisitor<E> visitor) throws SQLException;
}
