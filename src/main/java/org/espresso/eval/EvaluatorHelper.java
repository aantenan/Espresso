package org.espresso.eval;

import org.espresso.token.SqlComparisonOperator;

import static org.espresso.token.SqlComparisonOperator.*;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class
        EvaluatorHelper {
    public static boolean evalBetween(final long column, final long lower, final long upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final long column, final long lower, final double upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final long column, final double lower, final long upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final long column, final double lower, final double upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final double column, final long lower, final long upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final double column, final long lower, final double upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final double column, final double lower, final long upper) {
        return column >= lower && column <= upper;
    }

    public static boolean evalBetween(final double column, final double lower, final double upper) {
        return column >= lower && column <= upper;
    }
    
    
    public static boolean evalCompare(final long lhs, final long rhs, SqlComparisonOperator operator) {
        if (lhs < rhs) return operator == LT;
        if (lhs > rhs) return operator == GT;
        return operator == EQ || operator == GE || operator == LE;
    }

    public static boolean evalCompare(final long lhs, final double rhs, SqlComparisonOperator operator) {
        if (lhs < rhs) return operator == LT;
        if (lhs > rhs) return operator == GT;
        return operator == EQ || operator == GE || operator == LE;
    }

    public static boolean evalCompare(final double lhs, final long rhs, SqlComparisonOperator operator) {
        if (lhs < rhs) return operator == LT;
        if (lhs > rhs) return operator == GT;
        return operator == EQ || operator == GE || operator == LE;
    }

    public static boolean evalCompare(final double lhs, final double rhs, SqlComparisonOperator operator) {
        if (lhs < rhs) return operator == LT;
        if (lhs > rhs) return operator == GT;
        return operator == EQ || operator == GE || operator == LE;
    }

    public static boolean evalCompare(final Comparable lhs, final Comparable rhs, SqlComparisonOperator operator) {
        return operator.eval(lhs.compareTo(rhs));
    }

}
