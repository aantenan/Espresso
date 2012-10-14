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
package org.espresso;

import java.util.Date;

/**
 * {@code SimpleTestNode} is a trivial "element" for testing {@link org.espresso.SqlEngine}.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
class SimpleTestNode {
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
