package org.espresso.extension;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

/**
 * Hanldes american style date format
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 * */
public class AmericanDateExtension
        implements DateExtension {
    public static final AmericanDateExtension AMERICAN_DATE_EXTENSION = new AmericanDateExtension();

    @Override
    @SqlExtension
    public Date toDate(final String dateString)
            throws SQLException {
        try {
            return null == dateString ? null : DateFormat.toDateAmericanFormat(dateString);
        } catch (final ParseException e) {
            throw new SQLException(e);
        }
    }
}
