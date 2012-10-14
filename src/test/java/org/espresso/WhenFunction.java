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

import org.espresso.extension.SqlExtension;

import java.util.Date;

/**
 * {@code WhenFunction} is an extension function over {@link SimpleTestNode} for testing {@link
 * org.espresso.SqlEngine}.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
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
