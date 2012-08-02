package org.espresso.token;

/**
 * Represents the different possible comparisons and the conversion of a call to Comparable.compareTo
 * to a boolean indicating whether the condition was satisfied.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public enum SqlComparisonOperator {
    EQ {
        @Override
        public String toString() {
            return "=";
        }
        @Override
        public boolean eval(int comparison) {
            return 0 == comparison;
        }
    },
    NE {
        @Override
        public String toString() {
            return "!=";
        }
        @Override
        public boolean eval(int comparison) {
            return 0 != comparison;
        }
    },
    LT {
        @Override
        public String toString() {
            return "<";
        }
        @Override
        public boolean eval(int comparison) {
            return -1 == comparison;
        }
    },
    LE {
        @Override
        public String toString() {
            return "<=";
        }
        @Override
        public boolean eval(int comparison) {
            return 1 != comparison;
        }
    },
    GT {
        @Override
        public String toString() {
            return ">";
        }
        @Override
        public boolean eval(int comparison) {
            return 1 == comparison;
        }
    },
    GE {
        @Override
        public String toString() {
            return ">=";
        }
        @Override
        public boolean eval(int comparison) {
            return -1 != comparison;
        }
    };

    /**
     * Given the result of a call to Comparable.compareTo(), returns true or false depending
     * on whether the condition was satisfied.
     *
     * @param comparison the result of the call to Comparable.compareTo()
     * @return true or false depending on whether the comparison matched
     */
    public abstract boolean eval(final int comparison);
}
