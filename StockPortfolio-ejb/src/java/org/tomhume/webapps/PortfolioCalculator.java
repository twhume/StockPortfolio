/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Calculates the total values of shares held in various companies
 * 
 * @author twhume
 */
@Stateless
public class PortfolioCalculator implements PortfolioCalculatorLocal {

    @PersistenceContext(unitName = "StockPortfolio-ejbPU")
    private EntityManager em;
        
    @Override
    public List<ShareValue> getPortfolioValues() {
        
        /* Get the total number of shares we own in each company */

        List<ShareValue> values = new ArrayList<ShareValue>();
        Query query = em.createQuery("select s.company, sum(s.amount) FROM ShareTransaction s group by s.company");
        List<Object[]> results = query.getResultList();
        for(Object[] o: results) {
            ShareValue v = new ShareValue((String) o[0], (Double) o[1]);
            values.add(v);
        }
        
        /* Populate that list using share prices looked up from the service */
        
        return populatePrices(values);
    }
    
    private List<ShareValue> populatePrices(List<ShareValue> l) {
        return l;
    }
    
    
}
