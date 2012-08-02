package org.espresso;

import org.espresso.extension.SqlExtension;

import java.util.Date;

/**
 * {@code WhenFunction} is an extension function over {@link SimpleTestNode} for testing {@link
 * org.espresso.SqlEngine}.
 *
 * @author <a href="mailto:Brian.Oxley@tbd.com">Brian Oxley</a>
 */
public class WhenFunction {
    /**
     * Matches the "when" field of {@code SimpleTestNode}.
     *
     * @param when the test value to match
     * @param test the test node to match, never missing
     *
     * @return {@code true} if matching
     */
    @SqlExtension
    public boolean matches_when(final Date when, final SimpleTestNode test) {
        return when.equals(test.when);
    }
}
