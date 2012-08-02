package org.espresso.token;

/**
 * Representation of a subset of a SQL select statement. It does not support a list of columns to
 * select, and you can select from a single table only.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlSelect
        implements SqlStatement {
    private final String from;
    private String fromAlias;
    private final SqlExpressionNode whereClause;

    /**
     * Builds a select statement given the table name and the where clause
     *
     * @param from the table name from where to select
     * @param whereClause the restriction
     */
    public SqlSelect(final String from, final SqlExpressionNode whereClause) {
        this.from = from;
        this.whereClause = whereClause;
    }

    /**
     * Accessor to the table from where to select
     *
     * @return the table name
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets an alias for the table name. If one is set, it will be used in the toString method
     *
     * @param fromAlias the table alias
     */
    public void setFromAlias(final String fromAlias) {
        this.fromAlias = fromAlias;
    }

    /**
     * Accessor to the table alias
     *
     * @return the table alias (null is one was not set)
     */
    public String getFomAlias() {
        return fromAlias;
    }

    /**
     * Accessor to the where clause
     *
     * @return the expression corresponding to the restriction
     */
    public SqlExpressionNode getWhereClause() {
        return whereClause;
    }

    /**
     * The string representation of teh SQL statement as SELECT * FROM table WHERE ...
     *
     * @return the SQL select statement
     */
    @Override
    public String toString() {
        return "SELECT * FROM " + (null == fromAlias ? from : fromAlias) + " WHERE " + whereClause
                + ';';
    }
}
