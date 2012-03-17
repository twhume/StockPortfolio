package org.tomhume.webapps;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *  Class encapsulating a single transaction of shares.
 * 
 * @author twhume
 */
@Entity
@Table(name="ShareTransaction") 
public class ShareTransaction implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;            /* Unique identifier for this transaction */
    private String company;    /* company in whom shares are being bought */
    private double amount;      /* # of shares bought, allowing for fractions
                                 * and negative if this was a sale */
    private int pricePaid;     /* Amount paid for these shares, in cents */
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date when;          /* Timestamp for when transaction was created */

    public static final String[] COMPANIES =
    {"Google" , "Apple" , "Oracle" , "Microsoft"};

    public ShareTransaction() {
        /* By default, transactions are presumed to be created "now" */
        when = new Date();
        
    }

    public ShareTransaction(long id) {
        this();
        this.id = id;
    }
    
    public Date getWhen() {
        return when;
    }

    public void setWhen(Date when) {
        this.when = when;
    }
    
    /**
     * Get the value of amount
     *
     * @return the value of amount
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Set the value of amount
     *
     * @param amount new value of amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }

    /**
     * Get the value of pricePaid
     *
     * @return the value of pricePaid
     */
    public long getPricePaid() {
        return pricePaid;
    }

    /**
     * Set the value of pricePaid
     *
     * @param pricePaid new value of pricePaid
     */
    public void setPricePaid(int pricePaid) {
        this.pricePaid = pricePaid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    /**
     * Get the value of company
     *
     * @return the value of company
     */
    public String getCompany() {
        return company;
    }

    /**
     * Set the value of company
     *
     * @param company new value of company
     */
    public void setCompany(String Company) {
        this.company = Company;
    }

    /**
     * Helper method to determine whether this was a sale or purchase of shares.
     * @return 
     */
    
    public String getTransactionType() {
        if (this.amount<0) return "Sale";
        else return "Purchase";
    }

    /**
     * Helper method to return the number of shares transacted, whether it was
     * a purchase or sale this should be positive.
     * @return 
     */
    public double getSharesTransacted() {
        return Math.abs(this.amount);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShareTransaction other = (ShareTransaction) obj;
        if (this.id != other.id) {
            return false;
        }
        if ((this.company == null) ? (other.company != null) : !this.company.equals(other.company)) {
            return false;
        }
        if (Double.doubleToLongBits(this.amount) != Double.doubleToLongBits(other.amount)) {
            return false;
        }
        if (this.pricePaid != other.pricePaid) {
            return false;
        }
        if (this.when != other.when && (this.when == null || !this.when.equals(other.when))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 37 * hash + (this.company != null ? this.company.hashCode() : 0);
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.amount) ^ (Double.doubleToLongBits(this.amount) >>> 32));
        hash = 37 * hash + this.pricePaid;
        hash = 37 * hash + (this.when != null ? this.when.hashCode() : 0);
        return hash;
    }
    
}
