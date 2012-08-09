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
import java.util.Date;

/**
 * Hanldes american style date format
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
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
