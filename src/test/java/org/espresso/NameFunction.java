package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public class NameFunction {
    private final String name;

    public NameFunction(final String name) {
        this.name = name;
    }

    @SqlExtension
    public boolean is_bob(final SimpleTestNode test) {
        return name.equals(test.name);
    }
}
