package org.espresso;

import org.espresso.token.SqlSelect;
import org.espresso.token.SqlStatement;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.SQLException;

import static org.espresso.SqlParser.parse;
import static junit.framework.Assert.assertEquals;

/**
 * {@code SqlParserTest} tests {@link org.espresso.SqlParser}.
 *
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class SqlParserTest {
    @Test
    public void testDescribe()
            throws SQLException {
        final SqlStatement statement = parse("describe test;");
        assertEquals("DESCRIBE test;", statement.toString());
    }

    @Test
    public void shouldNotThrowForMixedCaseDescribe()
            throws SQLException {
        final SqlStatement statement = parse(
                "DeScRiBe table_should_be_implied_but_grammar_explicit");
        assertEquals("DESCRIBE table_should_be_implied_but_grammar_explicit;",
                statement.toString());
    }

    @Test
    public void testCancel()
            throws SQLException {
        final SqlStatement statement = parse("cancel test;");
        assertEquals("CANCEL test;", statement.toString());
    }

    @Test
    public void shouldNotThrowForMixedCaseCancel()
            throws SQLException {
        final SqlStatement statement = parse("CaNcEl table_should_be_implied_but_grammar_explicit");
        assertEquals("CANCEL table_should_be_implied_but_grammar_explicit;", statement.toString());
    }

    @Test
    public void shouldNotThrowForMixedCaseSelect()
            throws SQLException {
        final SqlStatement statement = parse(
                "SeLeCt * FrOm test WhErE a In (3) Or b Is NoT nUlL AnD x bEtWeEn 3 aNd 4");
        assertEquals(
                "SELECT * FROM test WHERE ((a IN (3)) OR ((b IS NOT NULL) AND (x BETWEEN 3 AND 4)));",
                statement.toString());
    }

    @Test
    @Ignore("Need support in parser/SqlSelect")
    public void shouldSelectAllWithoutWhereClause()
            throws SQLException {
        final SqlStatement statement = parse("SELECT * FROM test");
        assertEquals("SELECT * FROM test;", statement.toString());
    }

    @Test
    public void testSimpleEquality()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where 1 = 1;");
        assertEquals("SELECT * FROM test WHERE (1 = 1);", statement.toString());
    }

    @Test
    public void testAlias()
            throws SQLException {
        final SqlSelect select = (SqlSelect) parse("select * from test where 1 = 1;");
        select.setFromAlias("this.that.test");
        assertEquals("SELECT * FROM this.that.test WHERE (1 = 1);", select.toString());
    }

    @Test
    public void testOrsAndAnds()
            throws SQLException {
        final SqlStatement statement = parse(
                "select * from test where a > 2 and b < 3 or c >= 5 and d <= 6;");
        assertEquals("SELECT * FROM test WHERE (((a > 2) AND (b < 3)) OR ((c >= 5) AND (d <= 6)));",
                statement.toString());
    }

    @Test
    public void testNot()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where not a < b;");
        assertEquals("SELECT * FROM test WHERE (NOT(a < b));", statement.toString());
    }

    @Test
    public void testBetween()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where a between 2 and 5;");
        assertEquals("SELECT * FROM test WHERE (a BETWEEN 2 AND 5);", statement.toString());
    }

    @Test
    public void testIn()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where a in (1, 2, 3);");
        assertEquals("SELECT * FROM test WHERE (a IN (1, 2, 3));", statement.toString());
    }

    @Test
    public void testLike()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where a like '%b';");
        assertEquals("SELECT * FROM test WHERE (a LIKE '%b');", statement.toString());
    }

    @Test
    public void testIsNull()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where a is null;");
        assertEquals("SELECT * FROM test WHERE (a IS NULL);", statement.toString());
    }

    @Test
    public void testIsNotNull()
            throws SQLException {
        final SqlStatement statement = parse("select * from test where a is not null;");
        assertEquals("SELECT * FROM test WHERE (a IS NOT NULL);", statement.toString());
    }

    @Test
    public void testTermsAndFactors()
            throws SQLException {
        final SqlStatement statement = parse(
                "select * from test where a = 1 + 2 * 3 / 4 * ( 6 - 7);");
        assertEquals("SELECT * FROM test WHERE (a = (1 + (((2 * 3) / 4) * (6 - 7))));",
                statement.toString());
    }

    @Test
    public void testFunctionCall()
            throws SQLException {
        final SqlStatement statement = parse(
                "select * from test where function(1, 2, 3) + other(4,5) > 10;");
        assertEquals("SELECT * FROM test WHERE (((function(1, 2, 3)) + (other(4, 5))) > 10);",
                statement.toString());
    }

    @Test
    public void testActiveDealQuery()
            throws SQLException {
        final SqlStatement statement = parse(
                "SELECT * FROM EnrichedDeal WHERE (deal_date <= '08/17/2011') and (close_out ='N' or close_out_date > '08/17/2011') AND deal_type_number = 2500 AND database_name = 'energy_power_rep' AND server_name = 'TACRPT14' AND label = 'OVERNIGHT' AND (not ( maturity_date >= '08/17/2011' )) and ( maturity_type =  'E' );");
        assertEquals(
                "SELECT * FROM EnrichedDeal WHERE ((deal_date <= toDate('08/17/2011')) AND ((close_out = 'N') OR (close_out_date > toDate('08/17/2011'))) AND (deal_type_number = 2500) AND (database_name = 'energy_power_rep') AND (server_name = 'TACRPT14') AND (label = 'OVERNIGHT') AND (NOT(maturity_date >= toDate('08/17/2011'))) AND (maturity_type = 'E'));",
                statement.toString());
    }

}
