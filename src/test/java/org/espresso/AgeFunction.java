package org.espresso;

import org.espresso.extension.SqlExtension;

/**
 * @author <a href="mailto:Brian.Oxley@tbd.com">Brian Oxley</a>
 */
public class AgeFunction {
    @SqlExtension
    public int lie_about_age(final SimpleTestNode test) {
        return 10;
    }
}
