package org.espresso.visitor;

import org.espresso.SqlParser;
import org.espresso.TestDeal;
import org.espresso.eval.Evaluator;
import org.espresso.token.SqlSelect;
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
