package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class AgeFunction {
    @SqlExtension
    public int lie_about_age(final SimpleTestNode test) {
        return 10;
    }
}
