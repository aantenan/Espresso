/*
 * Copyright 2012 Alberto Antenangeli
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
