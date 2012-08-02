package org.espresso;

import org.espresso.eval.Evaluator;
import org.espresso.extension.JapaneseDateExtension;
import org.espresso.token.SqlSelect;
import org.espresso.visitor.CompilerVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Runs different types of queries against a collection of 2,000,000 elements and capture
 * the times. Used to test performance of the SQL Engine.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlEnginePerformance {

    private static void test(final String query, final List<TestDeal> store) throws SQLException {
        System.out.println("Query using " + (query.length() < 200 ? query : (query.substring(0,199) + " ...")));
        long start = System.currentTimeMillis();
        SqlEngine<TestDeal> engine = new SqlEngine<TestDeal>(TestDeal.class, query, new JapaneseDateExtension());
        List<TestDeal> result = engine.execute(store.iterator());
        long end = System.currentTimeMillis();
        System.out.println("Returned " + result.size() + " in " + (end - start) + "ms.");
    }
    
    private static void testCompiled(final String query, final List<TestDeal> store) throws SQLException {
        final SqlSelect statement = (SqlSelect) SqlParser.parse(query);
        final CompilerVisitor<TestDeal> visitor = new CompilerVisitor<TestDeal>(TestDeal.class);
        final Evaluator evaluator = visitor.compile(statement.getWhereClause());
        long start = System.currentTimeMillis();
        for (final TestDeal deal : store)
            evaluator.matches(deal);
        long end = System.currentTimeMillis();
        System.out.println("Returned in " + (end - start) + "ms.");

    }

    public static void main(final String... args) throws SQLException {
        final int size = args.length == 1 ? Integer.parseInt(args[0]) : 2000000;
        final ArrayList<TestDeal> store = new ArrayList<TestDeal>(size);
        System.out.println("Running test with " + size + " deals");
        System.out.print("Populating... ");
        long start = System.currentTimeMillis();
        final Date today = new GregorianCalendar(2001, 0, 2).getTime();
        final Date yesterday = new GregorianCalendar(2001, 0, 1).getTime();
        final Date tomorrow = new GregorianCalendar(2001, 0, 3).getTime();
        for (int i = 0; i < size ; i++) {
            final TestDeal deal = new TestDeal();
            deal.setDealNumber("HH_" + i);
            deal.setBook("book_" + (i % 10)); // 0..9 distribution
            deal.setChild(i % 100); // 0..99 distribution
            deal.setDatabaseName("database_" + (i % 2)); // 2 distribution
            deal.setMaturityDate(i < size / 4 ? yesterday : i > 3 * size / 4 ? tomorrow : today); // half today
            deal.setDealDate(today);
            store.add(deal);
        }
        long end = System.currentTimeMillis();
        System.out.println(" populated in " + (end-start) + " ms.");

        testCompiled("select * from TestDeals where child > 2;", store);
        testCompiled("select * from TestDeals where child between 50 and 59", store);

        System.out.println("Warming up...");
        new SqlEngine<TestDeal>(TestDeal.class,
                "select * from TestDeals where child = 47").execute(store.iterator());

        test("select * from TestDeals where child in (0);", store);
        test("select * from TestDeals where child = 0;", store);
        test("select * from TestDeals where child in (50);", store);
        test("select * from TestDeals where child in (50, 60);", store);
        test("select * from TestDeals where maturity_date in ('2001/01/02');", store);
        test("select * from TestDeals where book in ('book_0', 'book_1');", store);
        test("select * from TestDeals where book in ('book_0');", store);

        test("select * from TestDeals where child = 47", store);
        test("select * from TestDeals where book = 'book_0';", store);
        test("select * from TestDeals where maturity_date >= '2001/01/02'", store);
        test("select * from TestDeals where child = 47 and maturity_date = '2001/01/02'", store);

        final StringBuilder builder = new StringBuilder();
        for (int i = 9; i < 508; i++)
            builder.append("'book_").append(i).append("', ");
        builder.append("'book_509');");
        test("select * from TestDeals where book in (" + builder, store);

        test("select * from TestDeals where maturity_date >= '2001/01/02' and book = 'book_5' and not child in (0, 1, 2, 3, 4, 5, 6, 7, 8, 9);", store);
        test("select * from TestDeals where book like 'b%k_0';", store);

    }
}
