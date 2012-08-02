package org.espresso.util;

/**
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 *         Date: 8/2/12
 *         Time: 10:44 AM
 *         TODO: Document!
 */
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@code Sets} holds common static methods on sets.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 * @see java.util.Collections Java Collections
 */
public class Sets {
    /**
     * Creates a new modifiable concurrent set backed by a concurrent hash map.
     * <p/>
     * Rather than throw {@code NullPointerException} for {@code null} elements, operations return
     * {@code false} where relevant.
     *
     * @param <T> the set element type
     *
     * @return the new set, never missing
     *
     * @see java.util.concurrent.ConcurrentSkipListSet An alternative
     */
    public static <T> Set<T> newConcurrentSet() {
        return new AbstractSet<T>() {
            private final ConcurrentMap<T, T> delegate = new ConcurrentHashMap<T, T>();


            @Override
            public Iterator<T> iterator() {
                return delegate.keySet().iterator();
            }

            @Override
            public int size() {
                return delegate.size();
            }

            /**
             * {@inheritDoc}
             *
             * Returns {@code false} for null elements.
             */
            @Override
            public boolean add(final T t) {
                return null != t && null == delegate.put(t, t);
            }

            /**
             * {@inheritDoc}
             *
             * Returns {@code false} for null elements.
             */
            @Override
            public boolean remove(final Object o) {
                return null != o && null != delegate.remove(o);
            }
        };
    }

    public static <T> Set<T> newHashSet(final Iterator<T> iterator) {
        final Set<T> set = new HashSet<T>();
        while (iterator.hasNext())
            set.add(iterator.next());
        return set;
    }

    public static <T> Set<T> newHashSet(final T... elements) {
        final Set<T> set = new HashSet<T>(elements.length, 1.0F);
        for (final T element : elements)
            set.add(element);
        return set;
    }

    /**
     * Checks if the given collection <var>target</var> contains any element from the collection
     * <var>c</var>.  Complements {@link Collection#containsAll(java.util.Collection)}.
     * <p/>
     * For best performance provide the smaller of the two collections as <var>c</var> provided the
     * larger has an optimized {@link Collection#contains(Object)}.
     *
     * @param target the target collection to check membership of, never missing
     * @param c the test collection, never missing
     * @param <T> the common element type
     *
     * @return {@code true} if any elements of <var>c</var> are in <var>target</var>
     */
    public static <T> boolean containsAny(final Collection<? super T> target,
            final Collection<? extends T> c) {
        for (final T element : c)
            if (target.contains(element))
                return true;

        return false;
    }
}
