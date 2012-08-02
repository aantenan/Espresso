package org.espresso.extension;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

/**
 * Handles the date format most of the world uses.
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a> Date: 8/18/11
 */
public class StandardDateExtension
        implements DateExtension {
    public static final StandardDateExtension STANDARD_DATE_EXTENSION = new StandardDateExtension();

    @Override
    @SqlExtension
    public Date toDate(final String dateString)
            throws SQLException {
        try {
            return null == dateString ? null : DateFormat.toDate(dateString);
        } catch (final ParseException e) {
            throw new SQLException(e);
        }
    }
}
