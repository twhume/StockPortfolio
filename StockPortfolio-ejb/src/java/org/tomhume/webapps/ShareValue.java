package org.tomhume.webapps;

/**
 * Used to encapsulate the total value of shares for a given company. The
 * PortfolioController will output a list of these.
 *
 * We also store the number of shares here too. This is so a ShareValue class
 * can be created, populated with a number of shares, and passed to a separate
 * class to be populated with their value (thus separating out the reading of a
 * portfolio from its valuation).
 *
 * @author twhume
 */
public class ShareValue {

    private String company;
    private double value;
    private double numShares;
    
    /* We have a magic value of NOT_SET to indicate that the value of any
     * given ShareValue hasn't been looked up from the ShareBroker service.
     */
    public static final double NOT_SET = -1;

    public ShareValue(String c, double n, double v) {
        this.company = c;
        this.numShares = n;
        this.value = v;
    }

    public ShareValue(String c, double n) {
        this(c, n, NOT_SET);
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getNumShares() {
        return numShares;
    }

    public void setNumShares(double numShares) {
        this.numShares = numShares;
    }
}
