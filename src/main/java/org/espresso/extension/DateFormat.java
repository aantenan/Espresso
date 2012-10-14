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
package org.espresso.extension;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Calendar.*;

/**
 * {@code DateFormat} <strong>needs documentation</strong>.
 *
 * @author <a href="mailto:binkley@alumni.rice.edu">B. K. Oxley (binkley)</a>
 */
public enum DateFormat {
    STANDARD("^(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4,4})$") {
        @Override
        protected Date toDate(final Matcher matcher) {
            return from(matcher.group(3), matcher.group(2), matcher.group(1));
        }
    },
    JAPANESE("^(\\d{4,4})(\\d{2,2})(\\d{2,2})$") {
        @Override
        protected Date toDate(final Matcher matcher) {
            return from(matcher.group(1), matcher.group(2), matcher.group(3));
        }
    },
    AMERICAN("^(\\d{1,2})[/-](\\d{1,2})[/-](\\d{4,4})$") {
        @Override
        protected Date toDate(final Matcher matcher) {
            return from(matcher.group(3), matcher.group(1), matcher.group(2));
        }
    };

    private final Pattern format;

    DateFormat(final String pattern) {
        format = Pattern.compile(pattern);
    }

    protected abstract Date toDate(final Matcher matcher);

    /**
     * Gets the date matching the input string for any format trying first standard then alternate
     * formats.  To parse American dates, use {@link #toDateAmericanFormat(String)}.
     *
     * @param s the input, never missing
     *
     * @return the date matching the input, never missing
     *
     * @throws java.text.ParseException if the input is not a recognizeable date
     */
    public static Date toDate(final String s)
            throws ParseException {
        if (null == s)
            throw new IllegalArgumentException("Missing s");

        for (final DateFormat format : values()) {
            final Matcher matcher = format.format.matcher(s);
            if (!matcher.find())
                continue;
            return format.toDate(matcher);
        }

        throw new ParseException("Not a recognizeable date: " + s, 0);
    }

    /**
     * Gets the date matching the input string for American format, else Japanese format if not
     * matching American.
     *
     * @param s the input, never missing
     *
     * @return the date matching the input, never missing
     *
     * @throws java.text.ParseException if the input is not a recognizeable date
     */
    public static Date toDateAmericanFormat(final String s)
            throws ParseException {
        if (null == s)
            throw new IllegalArgumentException("Missing s");

        final Matcher american = AMERICAN.format.matcher(s);
        if (american.find())
            return AMERICAN.toDate(american);

        final Matcher alternate = JAPANESE.format.matcher(s);
        if (alternate.find())
            return JAPANESE.toDate(alternate);

        throw new ParseException("Date does not match American format: " + s, 0);
    }

    /**
     * Formats the given <var>date</var> in world standard \format (dd/MM/yyyy).
     *
     * @param date the date, never missing
     *
     * @return the formatted date, never missing
     */
    public static String formatStandardDate(final Date date) {
        return newStandardDateFormat().format(date);
    }

    /**
     * Creates a new JDK date formatter for world standard date formats
     * (dd/MM/yyyy).
     *
     * @return the date formatter, never missing
     */
    public static java.text.DateFormat newStandardDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    /**
     * Formats the given <var>date</var> in American format (MM/dd/yyyy).
     *
     * @param date the date, never missing
     *
     * @return the formatted date, never missing
     */
    public static String formatAmericanDate(final Date date) {
        return newAmericanDateFormat().format(date);
    }

    /**
     * Creates a new JDK date formatter for American date formats (MM/dd/yyyy).
     *
     * @return the date formatter, never missing
     */
    public static java.text.DateFormat newAmericanDateFormat() {
        return new SimpleDateFormat("MM/dd/yyyy");
    }

    private static Date from(final String year, final String month, final String day) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        calendar.set(YEAR, Integer.valueOf(year));
        calendar.set(MONTH, Integer.valueOf(month) - 1); // Yes, really
        calendar.set(DATE, Integer.valueOf(day));
        calendar.set(HOUR_OF_DAY, 0); // No, not HOUR
        calendar.set(MINUTE, 0);
        calendar.set(SECOND, 0);
        calendar.set(MILLISECOND, 0);
        return calendar.getTime();
    }
}
