/*
 * Handles interactions with the ShareBroker service. Separate from the PortfolioBean
 * because whilst they do very similar operations, they send them to different endpoints
 * (one to a web service, the other to a database) and I didn't want to get them confused.
 */
package org.tomhume.webapps;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import uk.ac.susx.inf.ianw.shareManagement.PurchaseResult;

/**
 *
 * @author twhume
 */
@Named(value = "shareBroker")
@RequestScoped
public class ShareBrokerBean {
    @EJB PortfolioControllerLocal portfolio; 
    @EJB ShareBrokerControllerLocal broker;
    
    /* Fields used for adding a new ShareTransaction */

    private String amount;
    private String company;
    private String transactionType;
    private String submit;
    
    
    public ShareBrokerBean() {
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
    
    public String[] getCompanies() {
        return ShareTransaction.COMPANIES;
    }
        
    /**
     * Make a single PurchaseRequest. Passes this through to the ShareBrokerController.
     * 
     * @return 
     */

    public String save() {
        double amountD = Double.parseDouble(this.amount);
        if (this.transactionType.equalsIgnoreCase("Sell")) amountD = amountD * -1;

        try {
            PurchaseResult res = broker.makePurchase(company, amountD);
            if (res!=null) {
                ShareTransaction st = new ShareTransaction();
                st.setAmount(amountD);
                st.setPricePaid((int)(res.getPrice()*100));
                st.setCompany(company);
                portfolio.add(st);
            }
        } catch (Exception e) {
            /* Generally it's bad practice to catch Exception, but there's so many it 
             * could be and I don't think we should be floating messages about
             * KeyStores or RemoteExceptions to the UI
             */
            e.printStackTrace();
        }
        cleanParameters();
        return "index";
    }
    
    /**
     * When we've saved a new transaction, we want to clean out all the parameters
     * so that the addition form isn't pre-filled with the details of the transaction
     * we just created...
     */
    
    private void cleanParameters() {
        this.amount = "";
        this.company = "";
        this.transactionType = "";
    }
    
    /**
     * Takes a string of the form "100" or "100.01" and returns the number of
     * cents as an integer. So "100" would become "10000" and "100.01" "10001".
     * @param p
     * @return 
     */

    private int centsFromDollars(String p) {
        if (!p.contains(".")) {
            return (Integer.parseInt(p)*100);
        }
        String[] fields = p.split("\\.");
        System.err.println("Have " + fields.length + " from " +p);
        //TODO add proper error handling here
        return (Integer.parseInt(fields[0])*100) + Integer.parseInt(fields[1]);
    }
}
