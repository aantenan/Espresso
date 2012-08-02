package org.espresso.token;

/**
 * Represents a describe statement, as in "describe tableName". Not currently used,
 * but it should return a description of the class matching the table name, which
 * should include field names and types.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlDescribe implements SqlStatement {
    private final String tableName;

    /**
     * Creates a SqlDescribe statement
     * @param tableName the name of the table to be described
     * @throws IllegalArgumentException if the table name is null
     */
    public SqlDescribe(final String tableName) {
        if (null == tableName)
            throw new IllegalArgumentException("SqlDescribe: table name cannot be null");
        this.tableName = tableName;
    }

    /**
     * Accessor to the underlying table name
     * @return the table name
     */
    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return "DESCRIBE " + tableName + ';';
    }
}
