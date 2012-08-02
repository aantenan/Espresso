package org.espresso;

import java.util.Date;

/**
 * {@code SimpleTestNode} is a trivial "element" for testing {@link org.espresso.SqlEngine}.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SimpleTestNode {
    public final String name;
    public final int age;
    public final String color;
     public final Date when;

    public SimpleTestNode(final String name, final int age, final String color, final Date when) {
        this.name = name;
        this.age = age;
        this.color = color;
        this.when = when;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getColor() {
        return color;
    }

    public Date getWhen() {
        return when;
    }
}
