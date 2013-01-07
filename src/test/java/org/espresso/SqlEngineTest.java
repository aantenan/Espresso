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
import org.espresso.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Set;

import static org.espresso.extension.AmericanDateExtension.AMERICAN_DATE_EXTENSION;
import static org.espresso.extension.JapaneseDateExtension.JAPANESE_DATE_EXTENSION;
import static org.espresso.extension.StandardDateExtension.STANDARD_DATE_EXTENSION;
import static java.util.Calendar.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@code SqlEngineTest} tests {@link SqlEngine}.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class SqlEngineTest {
    private static final String DEAL_TYPE = "Over the hill and far away";
    private static final String LABEL = "a label";

    private TestDeal deal1;
    private TestDeal deal2;
    private TestDeal deal3;

    @Before
    public void setUp()
            throws Exception {
        deal1 = new TestDeal();
        deal1.setDealNumber("HH_Titanic");
        deal1.setChild(1);
        deal1.setParent(13);
        deal1.setDealType(DEAL_TYPE);
        deal1.setLabel(LABEL);
        deal1.setBook("bob_the_builder");
        deal1.setDealDate(newDateFormat().parse("01/03/2011"));
        deal1.setMaturityDate(newDateFormat().parse("11/03/2011"));
        deal1.setDatabaseName("database");
        deal1.setDatabaseServer("server");

        deal2 = new TestDeal();
        deal2.setDealNumber("HH_Mayflower");
        deal2.setChild(2);
        deal2.setDealType(DEAL_TYPE + 'A');
        deal2.setLabel("other label");
        deal2.setDealDate(newDateFormat().parse("05/03/2011"));
        deal2.setMaturityDate(newDateFormat().parse("10/03/2011"));
        deal2.setDatabaseName("other database");
        deal2.setDatabaseServer("other server");
        deal2.setBook("book");

        deal3 = deal1.clone();
        deal3.setDealNumber("Null Book");
        deal3.setBook(null);
    }

    @Test(expected = SQLException.class)
    public void shouldThrowOnBadSQL()
            throws ParseException, SQLException {
        restrict("this is not sql;", deal1, deal2);
    }

    @Test
    public void shouldIncludeByIsNullMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book is null;", deal1, deal2, deal3),
                is(equalTo(singleDeal(deal3))));
    }

    @Test
    public void shouldExcludeByIsNullMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book is null;", deal1),
                is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByIsNotNullMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book is not null;", deal1, deal3),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByIsNotNullMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book is not null;", deal3),
                is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByExactSimpleMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book = 'bob_the_builder';", deal1,
                deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByExactSimpleMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book != 'bob_the_builder';", deal1,
                deal2), is(equalTo(singleDeal(deal2))));
    }

    @Test
    public void shouldIncludeByExactDateMatch()
            throws SQLException {
        assertThat(restrictWithDate("select * from EnrichedDeal where deal_date = '01/03/2011';",
                deal1, deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldIncludeByDateRange()
            throws SQLException, ParseException {
        assertThat(restrictWithDate(
                "select * from EnrichedDeal where deal_date BETWEEN '01/03/2011' AND '03/03/2011';",
                deal1, deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByDateRange()
            throws SQLException, ParseException {
        assertThat(restrictWithDate(
                "select * from EnrichedDeal where deal_date BETWEEN '02/03/2011' AND '03/03/2011';",
                deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByDateUpperBound()
            throws SQLException, ParseException {
        assertThat(restrictWithDate("select * from EnrichedDeal where deal_date < '02/03/2011';",
                deal1, deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByDateUpperBound()
            throws SQLException, ParseException {
        assertThat(restrictWithDate("select * from EnrichedDeal where deal_date < '01/03/2010';",
                deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByDateLowerBound()
            throws SQLException, ParseException {
        assertThat(restrictWithDate("select * from EnrichedDeal where deal_date > '01/03/2011';",
                deal1, deal2), is(equalTo(singleDeal(deal2))));
    }

    @Test
    public void shouldExcludeByDateLowerBound()
            throws SQLException, ParseException {
        assertThat(restrictWithDate("select * from EnrichedDeal where deal_date > '01/03/2012';",
                deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByLikeMatch()
            throws ParseException, SQLException {
        assertThat(restrict("select * from EnrichedDeal where book LIKE '%_the_%';", deal1, deal2),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldIncludeByDealType()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where deal_type = '" + DEAL_TYPE + "\';",
                deal1, deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByDealType()
            throws SQLException {
        assertThat(
                restrict("select * from EnrichedDeal where deal_type = '" + DEAL_TYPE + 'Z' + "\';",
                        deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldNotIncludePastMaturityDate()
            throws SQLException {
        // Query uses American format!
        assertThat(
                restrictWithDate("select * from EnrichedDeal where maturity_date >= '16/03/2011';",
                        deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeWithNotFunction()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where not(child = 2);", deal1, deal2),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeNotFunction()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where not(child = 1) and not(child = 2);",
                deal1, deal2), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByOverlappingRestrictionsOnDate()
            throws SQLException {
        assertThat(restrictWithDate(
                "select * from EnrichedDeal where deal_date >= '01/03/2011' and deal_date < '02/03/2011';",
                deal1, deal2), is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByDisjointRestrictionsOnDate()
            throws SQLException {
        assertThat(restrictWithDate(
                "select * from EnrichedDeal where deal_date >= '01/03/2011' and deal_date < '01/03/2011';",
                deal1, deal2), is(equalTo(noDeals())));
    }

    @Test(expected = SQLException.class)
    public void shouldThrowOnUnknownColumnsForMtsLoad()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where unknown_column = 3;", deal1, deal2),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldIncludeByDatabaseName()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where database_name = 'database';", deal1),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByDatabaseName()
            throws SQLException {
        assertThat(
                restrict("select * from EnrichedDeal where database_name = 'yes_he_can';", deal1),
                is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByServerName()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where database_server = 'server';", deal1),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByServerName()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where database_server = 'bob_the_builder';",
                deal1), is(equalTo(noDeals())));
    }

    @Test
    public void shouldIncludeByLike()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where deal_number like 'HH_T%';", deal1, deal2, deal3),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldExcludeByLike()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where deal_number like 'wont_match_T%';", deal1, deal2, deal3),
                is(equalTo(noDeals())));
    }

    @Test
    public void shouldExcludeWithNullValues()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where book like 'bob%';", deal1, deal2, deal3),
                is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void shouldIncludeWithInList()
            throws SQLException {
        assertThat(restrict("select * from EnrichedDeal where child in (2, 3, 4, 5);", deal1, deal2, deal3),
                is(equalTo(singleDeal(deal2))));
    }

    @Test
    public void shouldProcessNotTwoFalseOrs()
            throws SQLException {
        Collection<TestDeal> ds1 = restrict("select * from EnrichedDeal where not(label='label' or book = 'bob_the');", deal1);
        assertThat(ds1, is(equalTo(singleDeal(deal1))));
    }

    @Test
    public void deMorgansTest() throws Exception {
        Collection<TestDeal> ds1 = restrict("select * from EnrichedDeal where deal_number='HH_Titanic' and not (label='label' or book='bob_the');", deal1);
        Collection<TestDeal> ds2 = restrict("select * from EnrichedDeal where deal_number='HH_Titanic' and label<>'label' and book<>'bob_the');", deal1);

        assertThat(ds1, is(equalTo(ds2)));
    }

    @Test
    public void testExtensions()
            throws SQLException {
        final SqlEngine<SimpleTestNode> sqlEngine = new SqlEngine<SimpleTestNode>(
                SimpleTestNode.class,
                "select * from Test where age = 40 and lie_about_age() = 10 and " +
                        "matches_color('blue') and is_bob() and age_between(30, 50) and "  +
                        "not(matches_color('red')) and '1991/05/15' < when;",
                new NameFunction("Bob"), new ColorFunction(), new AgeFunction(), new AgeBetween(),
                new WhenFunction(), JAPANESE_DATE_EXTENSION);
        final ArrayList<SimpleTestNode> cache = new ArrayList<SimpleTestNode>(3);
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(YEAR, 1991);
        calendar.set(MONTH, 4); // 0-based
        calendar.set(DATE, 15);

        final SimpleTestNode node1 = new SimpleTestNode("Bob", 40, "blue", calendar.getTime());
        calendar.add(DATE, 1);
        final SimpleTestNode node2 = new SimpleTestNode("Bob", 10, "red", calendar.getTime());
        calendar.add(DATE, 1);
        final SimpleTestNode node3 = new SimpleTestNode("Mary", 40, "white", calendar.getTime());
        calendar.add(DATE, 1);
        final SimpleTestNode node4 = new SimpleTestNode("Bob", 40, "blue", calendar.getTime());

        cache.add(node1);
        cache.add(node2);
        cache.add(node3);
        cache.add(node4);

        assertThat(sqlEngine.execute(cache.iterator()), is(only(node4)));
    }

    @Test(expected = SQLException.class)
    public void testExtensionsShouldBePublic()
            throws SQLException {
        new SqlEngine<SimpleTestNode>(SimpleTestNode.class, "select * from test where 1 = 2;",
                new Object() {
                    @SqlExtension
                    private int cannotAccessThisDude() {
                        return 0;
                    }
                });
    }

    @Test(expected = SQLException.class)
    public void shouldNotAllowDuplicateFunctions()
            throws SQLException {
        new SqlEngine<SimpleTestNode>(SimpleTestNode.class, "select * from test where 1 = 2;",
                AMERICAN_DATE_EXTENSION, STANDARD_DATE_EXTENSION);
    }

    private static Collection<TestDeal> noDeals() {
        return emptyList();
    }

    private static Collection<TestDeal> singleDeal(final TestDeal deal) {
        return singletonList(deal);
    }

    private static Collection<TestDeal> restrict(final String select, final TestDeal... deals)
            throws SQLException {
        final SqlEngine<TestDeal> engine = new SqlEngine<TestDeal>(TestDeal.class, select);
        return engine.execute(asSet(deals).iterator());
    }

    private static Collection<TestDeal> restrictWithDate(final String select,
            final TestDeal... deals)
            throws SQLException {
        final SqlEngine<TestDeal> engine = new SqlEngine<TestDeal>(TestDeal.class, select,
                STANDARD_DATE_EXTENSION);
        return engine.execute(asSet(deals).iterator());
    }

    private static Set<TestDeal> asSet(final TestDeal... list) {
        return Sets.newHashSet(list);
    }

    private static SimpleDateFormat newDateFormat() {
        return new SimpleDateFormat("dd/MM/yyyy");
    }

    private static Collection<SimpleTestNode> only(final SimpleTestNode node4) {
        return singletonList(node4);
    }
}
