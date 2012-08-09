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

import org.apache.bcel.generic.*;
import org.espresso.eval.NumberWrapper;

import java.sql.SQLException;

/**
 * Encapsulates the different arithmetic operators and how to perform the operation on
 * a supplied accumulator.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
        @Override
        public Instruction getInstruction(final boolean isFloatPrecision) {
            return isFloatPrecision ? new DADD() : new LADD();
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
        @Override
        public Instruction getInstruction(final boolean isFloatPrecision) {
            return isFloatPrecision ? new DSUB() : new LSUB();
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
        @Override
        public Instruction getInstruction(final boolean isFloatPrecision) {
            return isFloatPrecision ? new DMUL() : new LMUL();
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
        @Override
        public Instruction getInstruction(final boolean isFloatPrecision) {
            return isFloatPrecision ? new DDIV() : new LDIV();
        }
    };

    /**
     * Accumulates the result of the underlying operation.
     * @param result where to accumulate
     * @param operand the other operand
     * @throws SQLException in case of any error conditions
     */
    public abstract void eval(final NumberWrapper result, final NumberWrapper operand) throws SQLException;

    /**
     * Return the VM instruction that corresponds to this operator
     * @param isFloatPrecision whether the stack contains a floating or fixed point precision
     * @return the corresponding instruction
     */
    public abstract Instruction getInstruction(final boolean isFloatPrecision);
}
