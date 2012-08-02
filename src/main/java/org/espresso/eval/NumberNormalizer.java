package org.espresso.eval;

/**
 * Converts numbers to a normalized representation - a NumberWrapper - with a floating
 * or a fixed representation. Also used to memoize the conversion operation, so we
 * don't have to test for what conversion to apply over and over again.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public enum NumberNormalizer {
    TO_DOUBLE {
        @Override
        public Object convertIfNeeded(final Object source, NumberWrapper wrapper,
                final NumberWrapperSetter setter) {
            if (null == source)
                return null;
            if (null == wrapper) {
                wrapper = new NumberWrapper(((Number) source).doubleValue());
                setter.setNumberWrapper(wrapper);
            }
            return wrapper.setDouble(((Number) source).doubleValue());
        }
    },
    TO_LONG {
        @Override
        public Object convertIfNeeded(final Object source, NumberWrapper wrapper,
                final NumberWrapperSetter setter) {
            if (null == source)
                return null;
            if (null == wrapper) {
                wrapper = new NumberWrapper(((Number) source).longValue());
                setter.setNumberWrapper(wrapper);
            }
            return wrapper.setLong(((Number) source).longValue());
        }
    },
    NO_OP {
        @Override
        public Object convertIfNeeded(final Object source, NumberWrapper wrapper,
                final NumberWrapperSetter setter) {
            return source;
        }
    };

    /**
     * Converts the source object if required, or performs a noop if the value is not
     * numeric. Sets the NumberWrapper created, so it can be memoized, avoiding the
     * creation of too many temporary objects.
     *
     * @param source Object to be converted (if necessary)
     * @param wrapper If the source is numeric, a number wrapper to use to set the value
     * @param setter If the source is numeric and no number wrapper was provided, create
     * one and use this call to set it on the object holding the wrapper.
     * @return the converted object (or the original one if no conversion was required)
     */
    public abstract Object convertIfNeeded(final Object source, final NumberWrapper wrapper,
            final NumberWrapperSetter setter);

    /**
     * Identifies the appropriate normalized for a given object (based on the class)
     * @param value the value we want to normalize
     * @return the appropriate normalizer
     */
    public static NumberNormalizer getNormalizer(final Object value) {
        if (null == value)
            return null;
        final Class clazz = value.getClass();
        if (clazz == NumberWrapper.class)
            return NO_OP;
        if (ClassUtil.isNumber(clazz))
            if (ClassUtil.isFloatPrecision(clazz))
                return TO_DOUBLE;
            else return TO_LONG;
        return NO_OP;
    }
}
