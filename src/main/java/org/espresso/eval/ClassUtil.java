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
package org.espresso.eval;

/**
 * Several helper methods related to manipulating classes and conversions.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class ClassUtil {
    /**
     * Check if the class corresponds to a floating precision number
     * @param clazz the class
     * @return whether this is a double or a float
     */
    public static boolean isFloatPrecision(final Class clazz) {
        return clazz == Double.class || clazz == Float.class;
    }

    /**
     * Check if the class corresponds to a fixex precision number
     * @param clazz the class
     * @return whether this is a long, integer, short or a byte
     */
    public static boolean isFixedPrecision(final Class clazz) {
        return clazz == Long.class || clazz == Integer.class || clazz == Short.class || clazz == Byte.class;
    }

    /**
     * Check if the class represents a number
     * @param clazz the class
     * @return whether this is a number wrapper, a fixed precision, or a float precision
     */
    public static boolean isNumber(final Class clazz) {
        return clazz == NumberWrapper.class || isFixedPrecision(clazz) || isFloatPrecision(clazz);
    }

    /**
     * If the value is numeric, convert it to the expected class passed as parameter. This is used
     * to convert numeric types to the appropriate representation before a reflective method calll.
     * @param value the value to be potentially converted
     * @param expectedClass the expected class
     * @return the coverted object or the original value if the object is not numeric.
     */
    public static Object asExpectedClass(final Object value, final Class<?> expectedClass) {
        if (expectedClass == float.class || expectedClass == double.class ||
                expectedClass == long.class || expectedClass == int.class ||
                expectedClass == short.class || expectedClass == byte.class) {

            final NumberWrapper number = (NumberWrapper) value;
            if (expectedClass == double.class)
                return number.asDouble();
            if (expectedClass == float.class)
                return (float) number.asDouble();
            if (expectedClass == long.class)
                return number.asLong();
            if (expectedClass == int.class)
                return (int) number.asLong();
            if (expectedClass == short.class)
                return (short) number.asLong();
            if (expectedClass == byte.class)
                return (byte) number.asLong();
        }
        return value;
    }

}
