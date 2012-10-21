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

    public static boolean evalIsNull(final Object test, final boolean checkIfNull) {
        return test == null ? checkIfNull : !checkIfNull;
    }

}
