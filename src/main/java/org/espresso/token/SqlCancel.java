package org.espresso.token;

/**
 * Represents a cancel statement, in the form of CANCEL query_id_string. Eventually
 * this will be used when we add support for continuous queries.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlCancel implements SqlStatement{
    private final String queryId;

    /**
     * Creates a Cancel given the queryId
     * @param queryId the query id
     * @throws IllegalArgumentException if the query id is null
     */
    public SqlCancel(final String queryId) {
        if (null == queryId)
            throw new IllegalArgumentException("SqlCancel: queryId cannot be null");
        this.queryId = queryId;
    }

    /**
     * Accessor to the underlying query id
     * @return the query id
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * String representation of this statement
     * @return the string representation, as "CANCEL queryId;"
     */
    @Override
    public String toString() {
        return "CANCEL " + queryId + ';';
    }
}
