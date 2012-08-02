package org.espresso.index;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 *         <p/>
 *         This class can be used as base class for all indices that support the concept of
 *         ordering. Implementations of the "whichBucket" method should place elements into buckets
 *         in a way that if element A < element B, then whichBucket(A) < whichBucket(B), and if A >
 *         B, then whichBucket(A) >= whichBucket(B). By following these rules, we can support the
 *         lessThan() and greaterThan() operations. See DateIndex for an example implementation.
 */
public abstract class OrderedIndex<T, V>
        extends Index<T, V> {
    protected OrderedIndex(final Class<V> type, final String name, final Getter<T, V> column,
            final int totalBuckets) {
        super(type, name, column, totalBuckets);
    }

    protected OrderedIndex(final Class<V> type, final String name, final Getter<T, V> column) {
        super(type, name, column);
    }

    @Override
    public Index<T, V> lessThan(final V what) {
        if (null == what)
            return null;
        final int bucket = whichBucket(what);
        final Index<T, V> index = newIndex();
        for (int i = 0; i <= bucket; i++)
            copySetAt(index, i);
        return index;
    }

    @Override
    public Index<T, V> greaterThan(final V what) {
        if (null == what)
            return null;
        final int bucket = whichBucket(what);
        final Index<T, V> index = newIndex();
        for (int i = bucket; i < totalBuckets; i++)
            copySetAt(index, i);
        return index;
    }
}
