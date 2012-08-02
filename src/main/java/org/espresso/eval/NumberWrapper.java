package org.espresso.eval;

import java.sql.SQLException;

/**
 * Wraps a number as a double or long, handling conversions whenever required by arithmetic
 * operations.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public final class NumberWrapper extends Number {
    private double doubleValue;
    private long longValue;
    private boolean isDouble;

    public NumberWrapper(final double value) {
        this.doubleValue = value;
        this.isDouble = true;
    }

    public NumberWrapper(final long value) {
        this.longValue = value;
        this.isDouble = false;
    }

    public NumberWrapper(final Object value) throws SQLException {
        final Class clazz = value.getClass();
        if (ClassUtil.isFloatPrecision(clazz)) {
            this.doubleValue = ((Number) value).doubleValue();
            this.isDouble = true;
        } else try {
            this.longValue = ((Number) value).longValue();
            this.isDouble = false;
        } catch (final ClassCastException e) {
            throw new SQLException("Number expected, got " + clazz + " instead");
        }
    }

    public boolean isDouble() {
        return isDouble;
    }

    public NumberWrapper setDouble(final double doubleValue) {
        this.isDouble = true;
        this.doubleValue = doubleValue;
        return this;
    }

    public NumberWrapper setLong(final long longValue) {
        this.isDouble = false;
        this.longValue = longValue;
        return this;
    }

    public void add(final NumberWrapper value) throws SQLException {
        if (this.isDouble) {
            if (value.isDouble)
                this.doubleValue += value.doubleValue;
            else
                this.doubleValue += value.longValue;
        } else if (value.isDouble)
            this.doubleValue = this.longValue + value.doubleValue;
                    else this.longValue += value.longValue;
    }

    public void subtract(final NumberWrapper value) throws SQLException {
        if (this.isDouble) {
            if (value.isDouble)
                this.doubleValue -= value.doubleValue;
            else
                this.doubleValue -= value.longValue;
        } else if (value.isDouble)
            this.doubleValue = this.longValue - value.doubleValue;
                    else this.longValue -= value.longValue;
    }

    public void multiply(final NumberWrapper value) throws SQLException {
        if (this.isDouble) {
            if (value.isDouble)
                this.doubleValue *= value.doubleValue;
            else
                this.doubleValue *= value.longValue;
        } else if (value.isDouble)
            this.doubleValue = this.longValue * value.doubleValue;
                    else this.longValue *= value.longValue;
    }

    public void divide(final NumberWrapper value) throws SQLException {
        if (this.isDouble) {
            if (value.isDouble)
                this.doubleValue /= value.doubleValue;
            else
                this.doubleValue /= value.longValue;
        } else if (value.isDouble)
            this.doubleValue = this.longValue / value.doubleValue;
                    else this.longValue /= value.longValue;
    }

    public double asDouble() {
        if (isDouble)
            return doubleValue;
        return longValue;
    }

    public long asLong() {
        if (isDouble)
            return (long) doubleValue;
        return longValue;
    }

    @Override
    public String toString() {
        if (isDouble)
            return Double.toString(doubleValue);
        else
            return Long.toString(longValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof NumberWrapper)) return false;

        NumberWrapper that = (NumberWrapper) o;

        if (this.isDouble) {
            if (that.isDouble)
                return this.doubleValue == that.doubleValue;
        } else {
            if (!that.isDouble)
                return this.longValue == that.longValue;
        }
        return false;
    }

    @Override
    public int hashCode() {
        long temp;

        if (isDouble)
            temp = doubleValue != +0.0d ? Double.doubleToLongBits(doubleValue) : 0L;
        else
            temp = longValue;
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public int intValue() {
        return (int) asLong();
    }

    @Override
    public long longValue() {
        return asLong();
    }

    @Override
    public float floatValue() {
        return (float) asDouble();
    }

    @Override
    public double doubleValue() {
        return asDouble();
    }
}
