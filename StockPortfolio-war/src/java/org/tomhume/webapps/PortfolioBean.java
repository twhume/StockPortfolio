/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

/**
 *
 * @author twhume
 */
@Named(value = "portfolio")
@Dependent
public class PortfolioBean {
    @EJB PortfolioControllerLocal portfolio; 
    @EJB PortfolioCalculatorLocal calculator;
    /**
     * Creates a new instance of PortfolioBean
     */
    
    public PortfolioBean() {
    }
    
    public List<ShareTransaction> getTransactions() {
        return portfolio.list();
    }
    
    public List<ShareTransaction> getTransactionsForCompany(String c) {
        return portfolio.listForCompany(c);
    }
    
    public List<ShareValue> getValues() {
        return calculator.getPortfolioValues();
    }
}
