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
package org.espresso;

import org.espresso.extension.DateExtension;
import org.espresso.extension.NotExtension;
import org.espresso.extension.SqlExtension;
import org.espresso.extension.StandardDateExtension;
import org.espresso.index.IndexRestrictor;
import org.espresso.index.Indices;
import org.espresso.token.SqlSelect;
import org.espresso.token.SqlStatement;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class encapsulates a SQL Engine. It supports parsing the tree and converting it
 * to a tree representation that can then be evaluated against a collection of objects:
 * the objects matching the tree are returned as a list. The engine also supports the
 * concept of extensions - those are objects with annotated methods that can be referred
 * to by the SQL code and called as part of the evaluation process.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlEngine<E> {
    private static final NotExtension NOT = new NotExtension();
    private static final DateExtension DATE_EXTENSION = new StandardDateExtension();

    private final Class<E> nodeType;
    private final SqlSelect select;
    private HashMap<String, FunctionExtension> functions;
    private DateExtension dateExtension;

    /**
     * Constructs and initializes an engine,
     *
     * @param nodeType the node type to match, never missing
     * @param selectStatement The select statement we want to run
     * @param extensions Objects containing extension functions. Those should be annotated with the
     * SqlExtension annotation.
     *
     * @throws SQLException if the SQL cannot be parsed, or the engine cannot be properly
     * initialzed
     */
    public SqlEngine(final Class<E> nodeType, final String selectStatement,
            final Object... extensions)
            throws SQLException {
        this.nodeType = nodeType;
        select = parse(terminate(selectStatement));
        select.setFromAlias(nodeType.getName());
        processExtensions(extensions);

    }

    /**
     * Runs the query over the supplied iterator, using the indices to reduce the universe that
     * needs to be traversed whenever possible.
     *
     * @param iterator iterator over the collection we want to restrict
     * @param indices the cache indices, never missing
     *
     * @return a list with the elements matching the restriction
     *
     * @throws SQLException in case of any error
     */
    public List<E> execute(final Iterator<E> iterator, final Indices<E> indices)
            throws SQLException {
        final IndexRestrictor<E> restrictor = new IndexRestrictor<E>(dateExtension, indices);
        return execute(restrictor.restrict(select, iterator));
    }

    /**
     * Runs the query over the supplied iterable, with no indices to support it. This will always
     * translate to a full scan over the iterable.
     *
     * @param iterator iterator ver the collection we want to restrict
     *
     * @return a list with the elements matching the restriction
     *
     * @throws SQLException in case of any errors
     */
    public List<E> execute(final Iterator<E> iterator)
            throws SQLException {
        final ArrayList<E> results = new ArrayList<E>();
        while (iterator.hasNext()) {
            final E row = against(iterator.next());
            if (null != row)
                results.add(row);
        }
        return results;
    }

    /**
     * Checks whether the supplied element matches the restriction
     *
     * @param element the element to check
     *
     * @return the element, or null if the element does not match the restriction
     *
     * @throws SQLException in case of errrors
     */
    public E against(final E element)
            throws SQLException {
        try {
            if ((Boolean) select.getWhereClause().eval(element, functions))
                return element;
            return null;
        } catch (final ClassCastException e) {
            throw new SQLException("WHERE clause did not evaluate to boolean", e);
        }
    }

    private static SqlSelect parse(final String selectStatement)
            throws SQLException {
        final SqlStatement statement = SqlParser.parse(selectStatement);
        if (!(statement instanceof SqlSelect))
            throw new SQLException("SqlEngine requires a SELECT statement.");
        return (SqlSelect) statement;
    }

    private void processExtensions(final Object... extensions)
            throws SQLException {
        if (null == extensions)
            return;
        buildFunctions(extensions);
        if (null == dateExtension)
            dateExtension = DATE_EXTENSION;
    }

    private void buildFunctions(final Object... extensions)
            throws SQLException {
        functions = new HashMap<String, FunctionExtension>(extensions.length);
        for (final Object extension : extensions) {
            for (final Method method : extension.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(SqlExtension.class)) {
                    if (Modifier.isPublic(method.getModifiers())) {
                        if (functions.containsKey(method.getName()))
                            throw new SQLException("Extension function " + method.getName()
                                    + "() defined in more than one extension object. FIX YOUR CODE!");
                        else
                            functions.put(method.getName(), new FunctionExtension(extension, method));
                    } else
                        throw new SQLException("Method " + method.getName()
                                + " is annotated as SqlExtension, but it is not public. FIX YOUR CODE!");
                }
            }
            if (DateExtension.class.isInstance(extension))
                dateExtension = (DateExtension) extension;
        }

        try {
            if (!functions.containsKey("NOT"))
                functions.put("NOT", new FunctionExtension(NOT, NOT.getClass().getDeclaredMethod("NOT", boolean.class)));
        } catch (final NoSuchMethodException e) {
            // Should never happen
            throw new SQLException(
                    "NotFunction disappeared from SqlEngine class. SHOULD NEVER HAPPEN!", e);
        }
    }

    private static String terminate(final String rawStatement) {
        final String statement = rawStatement.trim();
        return statement.endsWith(";") ? statement : statement + ';';
    }
}
