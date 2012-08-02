package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class ColorFunction {
    @SqlExtension
    public boolean matches_color(final String color, final SimpleTestNode test) {
        return color.equals(test.color);
    }
}
