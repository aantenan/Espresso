package org.espresso.index;

import java.util.Calendar;
import java.util.Date;

/**
 * Index suitable for dates. The dates are arranged following an order, with all dates that are <=
 * 1990 going to the first bucket, and the last bucket holding all dates beyond the last date we can
 * fit. The buckets on this index follow a natural order, so less than and greater than are
 * supported.
 * </p>
 * This is suitable for MTS-related applications, where we can specify the earliest date as 01/01/1990.
 * If wider date ranges are required, we should change this class to pass the ranges as part of the
 * constructor.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class DateIndex<T>
        extends OrderedIndex<T, Date> {
    private static final int earliestYear = 1990;

    public static <T> Index<T, Date> newIndex(final String name, final Getter<T, Date> column,
            final int totalBuckets) {
        return new DateIndex<T>(name, column, totalBuckets);
    }

    public static <T> Index<T, Date> newIndex(final String name, final Getter<T, Date> column) {
        return new DateIndex<T>(name, column);
    }

    private DateIndex(final String name, final Getter<T, Date> column, final int totalBuckets) {
        super(Date.class, name, column, totalBuckets);
    }

    DateIndex(final String name, final Getter<T, Date> column) {
        super(Date.class, name, column);
    }

    @Override
    public Index<T, Date> newIndex() {
        return newIndex(getName(), column, totalBuckets);
    }

    @Override
    protected int whichBucket(final Date other) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(other);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH); // Month is 0 based
        final int index = (year - earliestYear) * 12 + month;
        // Note that we bundle everything that is < earlierst date into bucket 0, and everything
        // that is > latest date into the last bucket. We do this so we can preserve the ordering
        // of the buckets and find, for example, all dates that are greater than a certain date.
        // If we use hash or something similar, then we lose the ability to do ordering.
        if (0 > index)
            return 0;
        if (index > totalBuckets - 1)
            return totalBuckets - 1;
        return index;
    }
}
