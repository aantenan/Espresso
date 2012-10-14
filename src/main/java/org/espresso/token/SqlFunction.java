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
import org.espresso.eval.NumberNormalizer;
import org.espresso.eval.NumberWrapper;
import org.espresso.eval.NumberWrapperSetter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.espresso.eval.ClassUtil.asExpectedClass;


/**
 * Represents a function call that may take 0 or more parameters.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlFunction<E>
        extends SqlExpression<E>
        implements NumberWrapperSetter {
    private final String name;
    private NumberNormalizer normalizer = null;
    private NumberWrapper wrapper = null;

    /**
     * Creates an instance of a function given its name.
     *
     * @param name the name of the function, never null
     *
     * @throws IllegalArgumentException if the name is null
     */
    public SqlFunction(final String name) {
        if (null == name)
            throw new IllegalArgumentException("SqlFunction: name cannot be null");
        this.name = name;
    }

    /**
     * Accessor to the function name
     *
     * @return the function name
     */
    public String getName() {
        return name;
    }

    @Override
    public String getOperator() {
        return name;
    }

    @Override
    public void setNumberWrapper(final NumberWrapper wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Sets the list of parameters associated with this function.
     *
     * @param parameters new list of parameters, never null. Empty is OK.
     *
     * @throws IllegalArgumentException if null list of parameters is supplied
     */
    public void setParameters(final List<SqlExpressionNode> parameters) {
        if (null == parameters)
            throw new IllegalArgumentException("SqlFunction: name cannot be null");
        // Make a copy to be on the safe side. Don't want external modification to
        // this list.
        operands.clear();
        operands.addAll(parameters);
    }

    /**
     * String representation of a function call, as (name(param1, param2, ...)) or (name()) if no
     * parameters.
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("(");
        builder.append(name).append("(");
        final Iterator<SqlExpressionNode> iterator = operands.iterator();
        if (!iterator.hasNext()) {
            builder.append("))");
            return builder.toString();
        }
        builder.append(iterator.next().toString());
        while (iterator.hasNext())
            builder.append(", ").append(iterator.next().toString());
        builder.append("))");
        return builder.toString();
    }

    /**
     * Performs a reflective call to one of the extensions provided to the engine. Evaluates
     * the operands to populate the parameters required to call the function, appending the
     * current object to the end of the list if the extension so requires it.
     *
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return the result of the function execution; if numeric, converts to BigDecimal
     * @throws SQLException wraps any error that may happen
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        final FunctionExtension function = functions.get(name);
        if (null == function)
            throw new SQLException("Implementation for function [" + name + "] was not supplied as extension");
        final Class<?>[] types = function.getParameterTypes();
        final Object[] parameters = new Object[types.length];
        for (int i = 0; i < operands.size(); i++)
            parameters[i] = asExpectedClass(operands.get(i).eval(row, functions), types[i]);
        if (operands.size() < parameters.length) // Need to append current object
                parameters[parameters.length-1] = row;
        try {
            final Object result = function.invoke(parameters);
            if (null == result)
                return null;
            if (null == normalizer)
                normalizer = NumberNormalizer.getNormalizer(result);
            return normalizer.convertIfNeeded(result, wrapper, this);
        } catch (IllegalAccessException e) {
            throw new SQLException("Implementation of function " + name + " is not public", e);
        } catch (InvocationTargetException e) {
            throw new SQLException("Implementation of function " + name + " could not be called", e);
        }
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
