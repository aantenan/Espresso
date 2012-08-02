package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:Brian.Oxley@tbd.com">Brian Oxley</a>
 */
public class ColorFunction {
    @SqlExtension
    public boolean matches_color(final String color, final SimpleTestNode test) {
        return color.equals(test.color);
    }
}
