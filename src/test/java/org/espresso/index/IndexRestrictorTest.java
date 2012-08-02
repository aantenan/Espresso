package org.espresso.index;

import org.espresso.SqlParser;
import org.espresso.TestDeal;
import org.espresso.token.SqlSelect;
import org.espresso.util.Sets;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.Date;
import java.util.Set;

import static org.espresso.extension.DateFormat.toDate;
import static org.espresso.extension.StandardDateExtension.STANDARD_DATE_EXTENSION;
import static org.espresso.util.Sets.newConcurrentSet;
import static org.espresso.util.Sets.newHashSet;
import static java.util.Collections.singleton;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author <a href="mailto:Alberto.Antenangeli@tbd.com">Alberto Antenangeli</a>
 */
public class IndexRestrictorTest {
    private TestDeal deal1;
    private static final String dealNumber1 = "HH_GoodBoy1";
    private static final int child1 = 1;
    private static final String dealType1 = "BOB'S BURGERS";
    private static final String label1 = "OVERNIGHT";

    private TestDeal deal2;
    private static final String dealNumber2 = "HH_GoodBoy2";
    private static final int child2 = 2;
    private static final String dealType2 = "TWILIGHT OF THE DOGS";
    private static final String label2 = "TWILIGHT";

    private TestDeal deal3;
    private static final String dealNumber3 = "HH_GoodBoy3";
    private static final int child3 = 3;
    private static final String dealType3 = "GOODNIGHT MOON";
    private static final String label3 = "NOLIGHT";

    private TestDeal deal4;
    private static final String dealNumber4 = "HH_null_maturity_date";
    private static final int child4 = 3;
    private static final String dealType4 = "GOODNIGHT MOON";
    private static final String label4 = "OVERNIGHT";

    private static final Date past;
    private static final Date future;
    private static final String book = "The Trial";

    private final Set<TestDeal> deals = newConcurrentSet();
    private final DealCacheIndices indices = new DealCacheIndices();

    private IndexRestrictor<TestDeal> restrictor;

    static {
        try {
            past = toDate("01/03/2010");
            future = toDate("01/03/2012");
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    @Before
    public void setUp()
            throws Exception {
        deal1 = new TestDeal();
        deal1.setDealNumber(dealNumber1);
        deal1.setChild(child1);
        deal1.setDealType(dealType1);
        deal1.setLabel(label1);
        deal1.setDealDate(past);
        deal1.setMaturityDate(future);
        deal1.setBook(book);
        deal1.setDatabaseName("database");
        deal1.setDatabaseServer("server");

        deal2 = new TestDeal();
        deal2.setDealNumber(dealNumber2);
        deal2.setChild(child2);
        deal2.setDealType(dealType2);
        deal2.setLabel(label2);
        deal2.setDealDate(past);
        deal2.setMaturityDate(past);
        deal2.setBook(book);
        deal2.setDatabaseName("database");
        deal2.setDatabaseServer("server");

        deal3 = new TestDeal();
        deal3.setDealNumber(dealNumber3);
        deal3.setChild(child3);
        deal3.setDealType(dealType3);
        deal3.setLabel(label3);
        deal3.setDatabaseName("database");
        deal3.setDatabaseServer("server");

        deal4 = new TestDeal();
        deal4.setMaturityDate(null);
        deal4.setMaturityType('E');
        deal4.setDealNumber(dealNumber4);
        deal4.setChild(child4);
        deal4.setBook("Friday");
        deal4.setDealType(dealType4);
        deal4.setLabel(label4);
        deal4.setDatabaseName("database");
        deal4.setDatabaseServer("server");

        deals.add(deal1);
        deals.add(deal2);
        deals.add(deal3);
        deals.add(deal4);
        indices.addToIndices(deal1);
        indices.addToIndices(deal2);
        indices.addToIndices(deal3);
        indices.addToIndices(deal4);

        restrictor = new IndexRestrictor<TestDeal>(STANDARD_DATE_EXTENSION, indices);
    }

    @Test
    public void shouldFindDealByEqual()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where deal_number = '" + dealNumber1 + "';");

        assertThat(Sets.newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldFindDealByReversedEqual()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where '" + dealNumber1 + "' = deal_number;");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldFindDealByLessThan()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where maturity_date<= '26/05/2011';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal2))));
    }

    @Test
    public void shouldFindDealByReversedLessThan()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where '26/05/2011' > maturity_date;");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal2))));
    }

    @Test
    public void shouldFindDealByGreaterThan()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where maturity_date >= '26/05/2011';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldFindDealByReversedGreaterThan()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where '26/05/2011' < maturity_date;");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldFindDealByBetween()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where maturity_date between '01/02/2012' and '01/04/2012';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldFindDealByIn()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where deal_number in ('" + dealNumber2 + "');");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal2))));
    }

    @Test
    public void shouldNotFindDealByLike()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where deal_number like '%2';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())).size(),
                is(equalTo(4)));
    }

    @Test
    public void shouldAndIncompatibleConditions()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where book = '" + book + "' and deal_number = '"
                        + dealNumber1 + "';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldNotOrIncompatibleConditions()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where book = '" + book + "' or deal_number = '"
                        + dealNumber1 + "';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())).size(),
                is(equalTo(4)));
    }

    @Test
    public void shouldOrCompatibleConditions()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where deal_number = '" + dealNumber1
                        + "' or deal_number = '" + dealNumber1 + "';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal1))));
    }

    @Test
    public void shouldHandleIsNull()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where book is null;");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal3))));
    }

    @Test
    public void shouldHandleEverlastingQuery()
            throws SQLException {
        final SqlSelect select = (SqlSelect) SqlParser.
                parse("select * from EnrichedDeal where not (maturity_date >= '26/05/2011') and maturity_type = 'E';");

        assertThat(newHashSet(restrictor.restrict(select, deals.iterator())),
                is(equalTo(singleElement(deal4))));
    }

    private static <T> Set<T> singleElement(final T element) {
        return singleton(element);
    }

    private static class DealCacheIndices
            extends Indices<TestDeal> {
        public DealCacheIndices() {
            super(DateIndex.newIndex("deal_date", new Getter<TestDeal, Date>() {
                @Override
                public Date get(final TestDeal object) {
                    return object.getDealDate();
                }
            }), DateIndex.newIndex("maturity_date", new Getter<TestDeal, Date>() {
                @Override
                public Date get(final TestDeal object) {
                    return object.getMaturityDate();
                }
            }), HashIndex.newIndex(String.class, "deal_number", new Getter<TestDeal, String>() {
                @Override
                public String get(final TestDeal object) {
                    return object.getDealNumber();
                }
            }), HashIndex.newIndex(String.class, "book", new Getter<TestDeal, String>() {
                @Override
                public String get(final TestDeal object) {
                    return object.getBook();
                }
            }), HashIndex.newIndex(String.class, "maturity_type", new Getter<TestDeal, String>() {
                @Override
                public String get(final TestDeal object) {
                    final Character maturityType = object.getMaturityType();
                    if (null == maturityType)
                        return null;
                    return maturityType.toString();
                }
            }));
        }
    }
}
