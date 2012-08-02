package org.espresso.token;

import org.espresso.eval.NumberWrapper;

import java.sql.SQLException;

/**
 * Encapsulates the different arithmetic operators and how to perform the operation on
 * a supplied accumulator.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public enum SqlArithmeticOperator {
    PLUS {
        @Override
        public String toString() {
            return "+";
        }

        @Override
        public void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException {
            result.add(operand);
        }
    },
    MINUS {
        @Override
        public String toString() {
            return "-";
        }

        @Override
        public void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException {
            result.subtract(operand);
        }
    },
    TIMES {
        @Override
        public String toString() {
            return "*";
        }

        @Override
        public void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException {
            result.multiply(operand);
        }
    },
    DIV {
        @Override
        public String toString() {
            return "/";
        }

        @Override
        public void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException {
            result.divide(operand);
        }
    };

    /**
     * Accumulates the result of the underlying operation.
     * @param result where to accumulate
     * @param operand the other operand
     * @throws SQLException in case of any error conditions
     */
    public abstract void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException;
}
