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
 * Represents a cancel statement, in the form of CANCEL query_id_string. Eventually
 * this will be used when we add support for continuous queries.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
