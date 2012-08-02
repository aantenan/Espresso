package org.espresso.index;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.espresso.util.Sets.newConcurrentSet;


/**
 * The Index class roughly corresponds to a database index, in the sense it provides faster access
 * to a subset of a larger set. Note that the main goal of our index class is not to support exact
 * matches, but merely to restrict the set that needs to be traversed to find the exact matches.
 * From this perspective, all operations that involve indices err on the side of caution, i.e., by
 * returning a subset that is equal to or larger than the perfect matching subset.
 * <p/>
 * Note that indices were designed having speed and low memory utilization in mind. An index is
 * basically an array of buckets, and each buckets contains a set of EnrichedDeal objects. The
 * number of buckets should remain low, so we do not waste too much memory. There is some support
 * for set operations, but the sets contained in a bucket are never manipulated - only whether a
 * particular set goes or not to a resulting bucket. Note that the sets contained in a bucket are
 * immutable, i.e., they are never replaced. The sets themselves may change, but they are shared
 * across all the different copies of indices over the same column.
 * <p/>
 * Nulls are easily handled with this approach - elements with corresponding null attributes
 * are placed in a special bucket. Although this complicates the processing of indices a bit, it
 * avoids having to take byzantine measures such as wrapping nulls with some special class, or
 * creating special instances of a class to represent nulls.
 * <p/>
 * Finally, note that an index also behaves as a set - it can produce an iterator,
 * thus allowing the traversal of its elements.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 * todo: Replace <T> with E extends Element<E>
 */
public abstract class Index<T, V>
        extends AbstractSet<T>
        implements Cloneable {
    private static final int DEFAULT_BUCKETS = 1024;

    private final Class<V> type;
    private final String name;
    protected final Getter<T, V> column;
    protected final int totalBuckets;
    private final Set<T>[] buckets;
    private Set<T> nullBucket;

    /**
     * Builds an index given the number of buckets and the associated column.  The column name is
     * case-independent.
     *
     * @param type the index type, never missing
     * @param name the column name, never missing
     * @param column the column to which the index refer
     * @param totalBuckets how many buckets over which the data will be partitioned
     */
    protected Index(final Class<V> type, final String name, final Getter<T, V> column,
            final int totalBuckets) {
        this.type = type;
        this.totalBuckets = totalBuckets;
        this.column = column;
        this.name = name.toLowerCase();
        buckets = new Set[totalBuckets];
        for (int i = 0; i < totalBuckets; i++)
            buckets[i] = newSet();
        nullBucket = newSet();
    }

    /**
     * Builds an index with the DEFAULT_BUCKETS bucket size
     *
     * @param type the index type, never missing
     * @param name the column name, never missing
     * @param column the column to which this index refer
     */
    protected Index(final Class<V> type, final String name, final Getter<T, V> column) {
        this(type, name, column, DEFAULT_BUCKETS);
    }

    /**
     * @param deal the deal object on which to eval
     *
     * @return the value of the attribute of the deal object associated with the column of this
     *         index.
     */
    protected V getColumnValue(final T deal) {
        return column.get(deal);
    }

    /**
     * Returns the set corresponding to a bucket.
     *
     * @param bucket the bucket number
     *
     * @return the associated set
     */
    protected Set<T> setAt(final int bucket) {
        return buckets[bucket];
    }

    /**
     * Copies the set contained by the given bucket to the destination
     *
     * @param destination Index to where the set should be copied
     * @param bucket the bucket number
     */
    protected void copySetAt(final Index<T, V> destination, final int bucket) {
        destination.buckets[bucket] = buckets[bucket];
    }

    /**
     * Makes a copy of the null bucket set
     *
     * @param destination the index to where the null bucket should be copied
     * @param source from where to copy
     */
    protected void copyNullSet(final Index<T, V> destination, final Index<T, V> source) {
        destination.nullBucket = source.nullBucket;
    }

    /**
     * Whether this index is compatible with the other, i.e., both can be manipulated together as
     * sets
     *
     * @param other the other index
     *
     * @return true if they are compatible, false otherwise
     */
    public boolean isCompatibleWith(final Index<T, V> other) {
        return column.equals(other.column) && totalBuckets == other.totalBuckets;
    }

    /**
     * Creates an instance of this index. This is required because the Index class is abstract.
     *
     * @return Another (empty) instance of this index.
     */
    public abstract Index<T, V> newIndex();

    /**
     * Gets the column name for this index.
     *
     * @return the column name, never missing
     */
    public String getName() {
        return name;
    }

    /**
     * Calculates the bucket where an object should be placed.
     *
     * @param object The object we need to place (in the end, this will be the attribute in the T
     * object that corresponds to this index)
     *
     * @return the bucket where the object should be placed.
     */
    protected abstract int whichBucket(final V object);

    /**
     * Returns an index that represents a subset of the buckets that are less than or equal to the
     * object that was passed as a parameter
     *
     * @param what the object to compare to
     *
     * @return the index representing the subset, or null if the index does not support the less
     *         than operation.
     */
    public abstract Index<T, V> lessThan(final V what);

    /**
     * Returns an index that represents a subset of the buckets that are greater than or equal to
     * the object that was passed as a parameter
     *
     * @param what the object to compare to
     *
     * @return the index representing the subset, or null if the index does not support the greater
     *         than operation
     */
    public abstract Index<T, V> greaterThan(final V what);

    /**
     * The class over which this index is built
     *
     * @return a reference to the class object
     */
    public final Class<V> getIndexType() {
        return type;
    }

    /**
     * Returns an index with only one bucket populated - the one that contains the object that was
     * passes as a parameter and any other objects in the bucket.  It is a superset of the target
     * object.
     *
     * @param what object to compare
     *
     * @return the index representing the subset
     */
    public Index<T, V> singleBucket(final V what) {
        final Index<T, V> index = newIndex();
        if (null == what)
            copyNullSet(index, this);
        else
            copySetAt(index, whichBucket(what));
        return index;
    }

    /**
     * Calculates the intersection between two indices. If they are not compatible, then return null
     * to indicate. The intersection basically selects the buckets that are populated on both
     * indices
     *
     * @param other the index to which we want to intersect
     *
     * @return the intersection, or null if they are not compatible
     */
    public Index<T, V> intersection(final Index<T, V> other) {
        if (!isCompatibleWith(other))
            return null;
        final Index<T, V> copy = newIndex();
        for (int i = 0; i < totalBuckets; i++)
            if (!buckets[i].isEmpty() && !other.buckets[i].isEmpty())
                copy.buckets[i] = buckets[i];
        if (!nullBucket.isEmpty() && !other.nullBucket.isEmpty())
            copy.nullBucket = nullBucket;
        return copy;
    }

    /**
     * Calculates the union between two indices. If they are not compatible, then return null to
     * indicate. The union basically selects the buckets that are populated in one of the two
     * indices.
     *
     * @param other the index to which we want the union
     *
     * @return the union, or null if they are not compatible
     */
    public Index<T, V> union(final Index<T, V> other) {
        if (!isCompatibleWith(other))
            return null;
        final Index<T, V> copy = newIndex();
        for (int i = 0; i < totalBuckets; i++)
            if (!buckets[i].isEmpty())
                copy.buckets[i] = buckets[i];
            else
                copy.buckets[i] = other.buckets[i];
        if (nullBucket.isEmpty())
            copy.nullBucket = other.nullBucket;
        else
            copy.nullBucket = nullBucket;
        return copy;
    }

    /**
     * Adds an T to the index
     *
     * @param element the deal to add
     *
     * @return true or false, following the Set standard
     */
    @Override
    public boolean add(final T element) {
        final V object = getColumnValue(element);
        if (null == object)
            return nullBucket.add(element);
        return setAt(whichBucket(object)).add(element);
    }

    /**
     * Removes an T from the index
     *
     * @param element the deal to remove
     *
     * @return true or false, according to the Set standard
     */
    @Override
    public boolean remove(final Object element) {
        final V object = getColumnValue((T) element);
        if (null == object)
            return nullBucket.remove(element);
        return setAt(whichBucket(object)).remove(element);
    }

    /** Make this an empty index (supporting the Set standard) */
    @Override
    public void clear() {
        for (int i = 0; i < totalBuckets; i++)
            buckets[i] = newSet();
    }

    /** Creates an iterator to go over all elements of this index */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = -1;
            private Iterator<T> iter;

            @Override
            public boolean hasNext() {
                if (null != iter && iter.hasNext())
                    return true;
                for (; ; ) {
                    index += 1;
                    if (index == totalBuckets) {
                        iter = nullBucket.iterator();
                        return iter.hasNext();
                    }
                    if (index > totalBuckets)
                        return false;
                    iter = buckets[index].iterator();
                    if (iter.hasNext())
                        return true;
                }
            }

            @Override
            public T next()
                    throws NoSuchElementException {
                if (hasNext())
                    return iter.next();
                throw new NoSuchElementException("Attempting to read beyond end of set");
            }

            @Override
            public void remove()
                    throws UnsupportedOperationException {
                throw new UnsupportedOperationException(
                        "Index does not allow removal of elements.");
            }
        };
    }

    /**
     * Computes the number of elements of this index
     *
     * @return the number of elements
     */
    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < totalBuckets; i++)
            size += buckets[i].size();
        return size + nullBucket.size();
    }

    /**
     * Creates a set suitable to be placed in a bucket
     *
     * @return the set
     */
    private static <T> Set<T> newSet() {
        return newConcurrentSet();
    }
}
