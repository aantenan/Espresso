package org.espresso.extension;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles the upside down japanese date style
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 * */
public class JapaneseDateExtension
        implements DateExtension {
    public static final JapaneseDateExtension JAPANESE_DATE_EXTENSION = new JapaneseDateExtension();

    private static final String FORMAT = "yyyy/MM/dd";

    @Override
    @SqlExtension
    public Date toDate(final String dateString)
            throws SQLException {
        try {
            return null == dateString ? null : new SimpleDateFormat(FORMAT).parse(dateString);
        } catch (final ParseException e) {
            throw new SQLException(e);
        }
    }
}
