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
package org.espresso.visitor;

import org.espresso.SqlParser;
import org.espresso.TestDeal;
import org.espresso.eval.Evaluator;
import org.espresso.token.SqlSelect;
import org.espresso.token.SqlStatement;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class CompilerVisitorTest {
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testIsNull() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where deal_type is null;");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();
        deal.setDealType(null);
        assertTrue(evaluator.matches(deal));
        deal.setDealType("Not null");
        assertFalse(evaluator.matches(deal));
    }

    @Test
    public void testIsNotNull() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where deal_type is not null;");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();
        deal.setDealType(null);
        assertFalse(evaluator.matches(deal));
        deal.setDealType("Not null");
        assertTrue(evaluator.matches(deal));
    }

    @Test
    public void testBetween() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child between 1 and 5;");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();

        deal.setChild(3);
        assertTrue(evaluator.matches(deal));
        deal.setChild(10);
        assertFalse(evaluator.matches(deal));
        deal.setChild(0);
        assertFalse(evaluator.matches(deal));
    }

    @Test
    public void testGreaterThanNumeric() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child > 2;");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();

        deal.setChild(3);
        assertTrue(evaluator.matches(deal));

        deal.setChild(1);
        assertFalse(evaluator.matches(deal));
    }


    @Test
    public void testGreaterThanObject() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where book > 'book1';");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();

        deal.setBook("book2");
        assertTrue(evaluator.matches(deal));

        deal.setBook("book0");
        assertFalse(evaluator.matches(deal));
    }

    @Test
    public void testLessThanFloat() throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child < 2.0;");
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        final Evaluator evaluator = visitor.compile();
        final TestDeal deal = new TestDeal();

        deal.setChild(1);
        assertTrue(evaluator.matches(deal));

        deal.setChild(2);
        assertFalse(evaluator.matches(deal));
    }

    @Test
    public void testArithmeticOperator() throws SQLException {
        final TestDeal deal = new TestDeal();
        deal.setChild(1);

        SqlSelect statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child < 1.0 + 1;");
        CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        Evaluator evaluator = visitor.compile();
        assertTrue(evaluator.matches(deal));

        statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child < 3 - 1.0;");
        visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        evaluator = visitor.compile();
        assertTrue(evaluator.matches(deal));

        statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child < 2 * 3;");
        visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        evaluator = visitor.compile();
        assertTrue(evaluator.matches(deal));

        statement = (SqlSelect) SqlParser.parse
                ("select * from TestDeals where child < 4.0 / 2;");
        visitor = new CompilerVisitor<TestDeal>(TestDeal.class, statement.getWhereClause());
        evaluator = visitor.compile();
        assertTrue(evaluator.matches(deal));
    }
}
