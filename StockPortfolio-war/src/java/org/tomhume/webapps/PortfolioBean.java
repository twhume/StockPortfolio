/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
}
