/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author twhume
 */
@Named(value = "transaction")
@RequestScoped
public class TransactionBean {

    @EJB PortfolioControllerLocal portfolio; 
    private String company;
    private String transactionType;
    private String purchasePrice;
    private String submit;
    private String amount;
    private String id;
    
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
