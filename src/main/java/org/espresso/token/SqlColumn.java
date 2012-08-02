package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;
import org.espresso.eval.NumberNormalizer;
import org.espresso.eval.NumberWrapper;
import org.espresso.eval.NumberWrapperSetter;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.Character.toLowerCase;
import static java.lang.Character.toUpperCase;

/**
 * Represents a database column, which, in turn, is associated witha field.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlColumn<E> implements SqlExpressionNode<E>, NumberWrapperSetter {
    private final static Map<Class, Map<String, Field>> fieldRowTypes = new ConcurrentHashMap<Class, Map<String, Field>>();
    private final String name;
    private NumberNormalizer normalizer = null;
    private NumberWrapper wrapper = null;

    /**
     * Given the column name, creates the column
     * @param name the name of the column
     * @throws IllegalArgumentException if the column name is null
     */
    public SqlColumn(final String name) {
        if (null == name)
            throw new IllegalArgumentException("SqlColumn: column name cannot be null");
        this.name = name;
    }

    /**
     * Accessor to the column name
     * @return the column name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the column name
     */
    @Override
    public String toString() {
        return name;
    }

    @Override
    public void setNumberWrapper(final NumberWrapper wrapper) {
        this.wrapper = wrapper;
    }

    /**
     * Evaluates the column object - converts the column name to the appropriate accessor,
     * and then calls the corresponding method on the current object. Tries both getXXX()
     * and isXXX().
     * </br>
     * If the column is numeric, convert the result to a BigDecimal before returning it,
     * so we have a standard representation for numeric types that can be used for the
     * basic arithmetic operations, comparisons, etc. If we don't do this, then we are
     * left with a double dispatch problem, since the left and right hand side of an
     * operator may be any of multiple numeric types.
     * </br>
     * This method also caches the references to methods associated with a particular class,
     * so we don't have to fetch them every time they are referred to.
     *
     * @param row Reference to the current object
     * @param functions Function extensions, passed down the expression tree
     * @return the value that corresponds to the required column
     * @throws SQLException wraps all types of errors that may happen
     */
    @Override
    public Object eval(final E row, final Map<String, FunctionExtension> functions) throws SQLException {
        final Field field = getField(row);
        try {
            field.setAccessible(true);
            final Object result = field.get(row);
            if (null == result)
                return result;
            if (null == normalizer)
                normalizer = NumberNormalizer.getNormalizer(result);
            return normalizer.convertIfNeeded(result, wrapper, this);
        } catch (Exception e) {
            throw new SQLException("Error invoking getter for column: " + name, e);
        }
    }
    

    /**
     * Accept method for the visitor pattern, turn around and call visit on the visitor.
     * Pretty standard, nothing new here...
     *
     * @param visitor the visitor to this class
     */
    @Override
    public void accept(final SqlNodeVisitor<E> visitor) throws SQLException {
        visitor.visit(this);
    }

    private Field getField(final E row) throws SQLException {
        final Class clazz = row.getClass();
        Map<String, Field> fields = fieldRowTypes.get(clazz);
        if (null == fields) {
            fields = new ConcurrentHashMap<String, Field>();
            fieldRowTypes.put(clazz, fields);
        }
        Field field = fields.get(name);
        if (null == field) {
            final String fieldName = convertToCamelBackGetter("", name);
            try {
                field = clazz.getDeclaredField(fieldName);
                fields.put(name, field);
            } catch (final NoSuchFieldException e) {
                throw new SQLException("Could not find field: " + fieldName, e); 
            }
        }
        return field;
    }


    private static String convertToCamelBackGetter(final String prefix, final String name) {
        final StringBuilder builder = new StringBuilder(prefix);
        boolean toUpper = false;
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if ('_' == c)
                toUpper = true;
            else {
                builder.append(toUpper ? toUpperCase(c) : toLowerCase(c));
                toUpper = false;
            }
        }
        return builder.toString();
    }
}
