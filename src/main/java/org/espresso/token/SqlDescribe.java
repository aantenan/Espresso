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

/**
 * Represents a describe statement, as in "describe tableName". Not currently used,
 * but it should return a description of the class matching the table name, which
 * should include field names and types.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
