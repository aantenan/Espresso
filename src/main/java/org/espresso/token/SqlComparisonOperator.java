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
