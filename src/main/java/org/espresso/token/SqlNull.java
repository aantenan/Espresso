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

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.util.Map;

/**
 * Represents the SQL NULL element
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
