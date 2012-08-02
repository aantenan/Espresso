package org.espresso.token;

import org.espresso.FunctionExtension;
import org.espresso.SqlNodeVisitor;

import java.sql.SQLException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Map;
import java.util.regex.Pattern;

import static java.text.CharacterIterator.DONE;
import static java.util.regex.Pattern.compile;

/**
 * Represents the SQL clause column LIKE pattern.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlLikeExpression<E> extends SqlExpression<E> {
    private static final Pattern PERCENT = Pattern.compile("%");
    /** @see java.util.regex.Pattern */
    private static final String META = "!$()*+.<>?[\\]^{|}";

    private Pattern compiledPattern = null;

    @Override
    public String getOperator() {
        return "LIKE";
    }

    @Override
    public Object eval(E row, Map<String, FunctionExtension> functions) throws SQLException {
        try {
            final String left = (String) operands.get(0).eval(row, functions);
            final String right = (String) operands.get(1).eval(row, functions);
            return matches(left, right);
        } catch (ClassCastException e) {
            throw new SQLException("LIKE requires a string expression");
        } catch (final IndexOutOfBoundsException e) {
            throw new SQLException("LIKE requires two operands");
        }
    }


    private boolean matches(final String s, final String pattern) {
        if (null == s)
            return false;
        /*
         * 1. Escape regex metacharacters in LIKE pattern.
         * 2. Replace all '%' characters with regex '.*' pattern.
         * 3. Match regex pattern against input string.
         */
        if (null == compiledPattern)
            compiledPattern = compile(PERCENT.matcher(escape(pattern)).replaceAll(".*"));
        return compiledPattern.matcher(s).matches();
    }

    private static String escape(final String s) {
        final StringBuilder builder = new StringBuilder(s.length());
        final CharacterIterator it = new StringCharacterIterator(s);
        for (char ch = it.first(); DONE != ch; ch = it.next()) {
            if (-1 != META.indexOf(ch))
                builder.append('\\');
            builder.append(ch);
        }
        return builder.toString();
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
