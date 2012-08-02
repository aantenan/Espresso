package org.espresso.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * {@code Indices} are a collection of element cache indices.  Defining class factory methods on the
 * cache element for generaing indexes is best practice, supporting both Java and Spring wiring:
 * <pre>
 * &#64;Component
 * public final class EgIndices extends Indices&lt;EgElement&gt; {
 *     public EgIndices() {
 *         super(nameIndex(), whenIndex());
 *     }
 * }</pre>
 * <pre>
 * &lt;bean id="indices" class="org.espresso.index.Indices"&gt;
 *     &lt;constructor-arg&gt;
 *         &lt;array&gt;
 *             &lt;bean class="org.espresso.eg.EgElement"
 *                   factory-method="nameIndex"/&gt;
 *             &lt;bean class="org.espresso.eg.EgElement"
 *                   factory-method="whenIndex"/&gt;
 *         &lt;/array&gt;
 *     &lt;/constructor-arg&gt;
 * &lt;/bean&gt;</pre>
 *
 * For caches without indices use {@link #none()}.
 *
 * @param <T> the cache element type
 *
 * @author <a href="mailto:Brian.Oxley@tbd.com">Brian Oxley</a>
 * @todo Replace <T> with E extends Element<E> once deal cache uses generic cache
 */
public class Indices<T> {
    private static final Indices<?> NONE = new Indices<Object>();

    private final Map<String, Index<T, ?>> indexMap;

    /**
     * Gets the empty indices.
     *
     * @param <T> the cache element type
     *
     * @return the empty indices, never missing
     */
    public static <T> Indices<T> none() {
        return (Indices<T>) NONE;
    }

    /** Constructs a new, empty set of indices for unindexed caches. */
    public Indices() {
        indexMap = emptyMap();
    }

    /**
     * Constructs a new set of indices for the given <var>indexes</var>.
     *
     * @param indexes the indexes, never missing
     */
    public Indices(final Collection<Index<T, ?>> indexes) {
        indexMap = newHashMapWithExpectedSize(indexes.isEmpty() ? 1 : indexes.size());
        for (final Index<T, ?> index : indexes)
            indexMap.put(index.getName(), index);
    }

    /**
     * Constructs a new set of indices for the given <var>indexes</var>.
     *
     * @param indexes the indexes, never missing
     */
    public Indices(final Index<T, ?>... indexes) {
        indexMap = newHashMapWithExpectedSize(0 == indexes.length ? 1 : indexes.length);
        for (final Index<T, ?> index : indexes)
            indexMap.put(index.getName(), index);
    }

    /**
     * Adds the given <var>element</var> to all cache indices.
     *
     * @param element the cache element, never missing
     */
    public final void addToIndices(final T element) {
        for (final Index<T, ?> index : indexMap.values())
            index.add(element);
    }

    /**
     * Removes the given <var>element</var> from all cache indices
     *
     * @param element the cache element, never missing
     */
    public final void removeFromIndices(final T element) {
        if (null == element)
            return;
        for (final Index<T, ?> index : indexMap.values())
            index.remove(element);
    }

    /**
     * Finds the cache index for the given <var>column</var> or {@code null} if <var>column</var> is
     * unindexed.  <var>column</var> is case-independent.
     *
     * @param column the column, never missing
     *
     * @return the corresponding index or {@code null}
     */
    public final <V> Index<T, V> indexFor(final String column) {
        return (Index<T, V>) indexMap.get(column.toLowerCase());
    }
    
    private HashMap newHashMapWithExpectedSize(final int size) {
        return new HashMap(size, 1.0F);
    } 
}
