/*
 * Copyright 2012 Espresso Team
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
package org.espresso.util;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
        Collections.addAll(set, elements);
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
