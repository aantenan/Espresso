package org.espresso;

import java.util.Date;

/**
 * Simplified enriched deal, so we can test queries that closely match the deal cache without
 * introducing a dependency on the deal cache module.
 *
 * @author <a href="mailto:antenangeli@yahoo.com">Alberto Antenangeli</a>
 */
public class TestDeal
        implements Cloneable {
    private String dealNumber;
    private int child;
    private int parent;
    private String label;
    private String book;
    private Date dealDate;
    private Date maturityDate;
    private char maturityType;
    private String dealType;
    private String databaseName;
    private String databaseServer;

    public String getDatabaseServer() {
        return databaseServer;
    }

    public void setDatabaseServer(final String databaseServer) {
        this.databaseServer = databaseServer;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(final String dealType) {
        this.dealType = dealType;
    }

    public String getDealNumber() {
        return dealNumber;
    }

    public void setDealNumber(final String dealNumber) {
        this.dealNumber = dealNumber;
    }

    public int getChild() {
        return child;
    }

    public void setChild(final int child) {
        this.child = child;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(final int parent) {
        this.parent = parent;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public String getBook() {
        return book;
    }

    public void setBook(final String book) {
        this.book = book;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(final Date dealDate) {
        this.dealDate = dealDate;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(final Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    @Override
    public final TestDeal clone() {
        try {
            return TestDeal.class.cast(super.clone());
        } catch (final CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public char getMaturityType() {
        return maturityType;
    }

    public void setMaturityType(final char maturityType) {
        this.maturityType = maturityType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TestDeal))
            return false;

        final TestDeal that = (TestDeal) o;

        if (child != that.child)
            return false;
        if (!databaseName.equals(that.databaseName))
            return false;
        if (!dealNumber.equals(that.dealNumber))
            return false;
        if (!label.equals(that.label))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = dealNumber.hashCode();
        result = 31 * result + child;
        result = 31 * result + label.hashCode();
        result = 31 * result + databaseName.hashCode();
        return result;
    }
}
