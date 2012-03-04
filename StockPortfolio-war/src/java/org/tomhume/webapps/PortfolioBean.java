/*
 * Encapsulates all the methods for reading in lists of ShareTransactions, as
 * well as adding and deleting transactions.
 * 
 * Originally I had these as two separate classes; I brought them together when
 * I realised that I wanted a delete() or save() operation to invalidate the
 * cache I was maintaining for listing operations.
 */
package org.tomhume.webapps;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author twhume
 */
@Named(value = "portfolio")
@RequestScoped
public class PortfolioBean {
    @EJB PortfolioControllerLocal portfolio; 
    @EJB PortfolioCalculatorLocal calculator;
    
    /* Fields used for adding a new ShareTransaction */
    
    private String company;
    private String transactionType;
    private String purchasePrice;
    private String submit;
    private String amount;
    private String id;
    
    /* List where we store the current portfolio values, so that a JSF page
     * can refer to them many times in the course of a single request without
     * their being re-fetched from the database.
     */
    private List<ShareValue> svList = null;
    
    /* Similarly, here's a place where we can cache lists of transactions.
     * This means that in future we could use this bean in pages where we 
     * had, say, separate Datatables for each company.
     */
    private Map<String, List<ShareTransaction>> txCache = new TreeMap<String, List<ShareTransaction>>();
    
    /* Magic key used to store the transactions for "all companies" in txCache */
    private static final String ALL_COMPANIES_KEY = "___ALL___";
    
    public PortfolioBean() {
    }
    
    public List<ShareTransaction> getTransactions() {
        if (txCache.get(ALL_COMPANIES_KEY)==null) txCache.put(ALL_COMPANIES_KEY, portfolio.list());
        return txCache.get(ALL_COMPANIES_KEY);
    }
    
    public List<ShareTransaction> getTransactionsForCompany(String c) {
        if (txCache.get(c)==null) txCache.put(c, portfolio.listForCompany(c));
        return txCache.get(c);
    }
    
    public List<ShareValue> getValues() {
        if (svList==null) svList = calculator.getPortfolioValues();
        return svList;
    }
    
    private void invalidateCache() {
        txCache = new TreeMap<String, List<ShareTransaction>>();
        svList = null;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }
    
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
    
    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
    
    public String[] getCompanies() {
        return ShareTransaction.COMPANIES;
    }
    
    public String delete() {
        portfolio.delete(new ShareTransaction(Long.parseLong(this.id)));
        invalidateCache();
        return "index";
    }
    
    public String save() {
        double amount = Double.parseDouble(this.amount);
        if (this.transactionType.equalsIgnoreCase("Sell")) amount = amount * -1;

        ShareTransaction st = new ShareTransaction();
        st.setAmount(amount);
        
        int pricePaid = centsFromDollars(this.purchasePrice);
        
        st.setPricePaid(pricePaid);
        st.setCompany(this.company);

        portfolio.add(st);
        invalidateCache();
        return "index";
    }

    private int centsFromDollars(String p) {
        if (!p.contains(".")) {
            return (Integer.parseInt(p)*100);
        }
        String[] fields = p.split(".");
        //TODO add proper error handling here
        return (Integer.parseInt(fields[0])*100) + Integer.parseInt(fields[1]);
    }
}
