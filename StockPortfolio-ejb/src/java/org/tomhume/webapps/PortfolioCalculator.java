/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import uk.ac.susx.inf.ianw.shareManagement.ShareBroker;

/**
 * Calculates the total values of shares held in various companies
 * 
 * @author twhume
 */
@Stateless
public class PortfolioCalculator implements PortfolioCalculatorLocal {

    @PersistenceContext(unitName = "StockPortfolio-ejbPU")
    private EntityManager em;
        
    /**
     * Reads in the whole Portfolio of shares owned from the database.
     * 
     * @return a list of ShareValue objects corresponding to the Portfolio
     */
    
    @Override
    public List<ShareValue> getPortfolioValues() {
        
        /* Get the total number of shares we own in each company; we use the
         * database to tot up these values before they come to us.
         */

        List<ShareValue> values = new ArrayList<ShareValue>();
        Query query = em.createQuery("select s.company, sum(s.amount) FROM ShareTransaction s group by s.company");
        List<Object[]> results = query.getResultList();
        for(Object[] o: results) {
            values.add(new ShareValue((String) o[0], (Double) o[1]));
        }
        
        /* Populate that list using share prices looked up from the service */
        
        return populatePrices(values);
    }
    
    /**
     * Takes a list of ShareValues and, for each one which doesn't have a total
     * price set, looks up the current price for the company using the ShareBroker
     * service and uses said price to populate the value.
     * 
     * @param l List of 
     * @return 
     */
    
    protected List<ShareValue> populatePrices(List<ShareValue> l) {

        /*
         * Some mucky exception  handling here. Because we already have a list of
         * ShareValues, if there's a problem filling one or any of them out, we
         * can return this list and have the higher layers notice that a ShareValue
         * with a total of NOT_SET implies we had trouble looking up the result.
         * 
         * We catch 3 exceptions the same way. I was tempted to catch all of them,
         * but if there's some other strangeness I'd like that to float up and out.
         * 
         * I also wonder if I should have some separate exception-catching in the inner
         * loop, so that if a single getPrice() call fails, others can later succeed.
         * Or is it better to have a single failure prevent a following set of failures?
         * Probably an application-specific call (i.e. either approach is justifiable).
         */
        
        try {
            if (System.getSecurityManager() == null) System.setSecurityManager(new RMISecurityManager());
            ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("rmi:/ShareBroker");

            for (ShareValue v: l) {
                if (v.getValue()==ShareValue.NOT_SET) {
                    System.err.println("Looking up " + v.getCompany());
                    v.setValue(sb.getPrice(v.getCompany()) * v.getNumShares());
                }
            }
        } catch (RemoteException n) {
            n.printStackTrace();
        } catch (NotBoundException n) {
            n.printStackTrace();
        } finally {
            return l;
        }
    }
    
    
}
