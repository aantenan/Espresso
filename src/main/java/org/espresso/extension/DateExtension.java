package org.espresso.extension;

import java.sql.SQLException;
import java.util.Date;

/**
 * Interface to support extensions that handle the different date formats.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 *
 */
public interface DateExtension {
    Date toDate(final String dateString)
            throws SQLException;
}
