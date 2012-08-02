package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class AgeBetween {
    /**
     * Matches the "when" field of {@code SimpleTestNode}.
     *
     * @param low the test value to match
     * @param high the test value to match
     * @param test the test node to match, never missing
     *
     * @return {@code true} if matching
     */
    @SqlExtension
    public boolean age_between(final double low, final double high, final SimpleTestNode test) {
        return test.age >= low && test.age <= high;
    }
}
