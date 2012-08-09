/*
 * Copyright 2012 Alberto Antenangeli
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

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles the upside down japanese date style
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
