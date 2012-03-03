/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;
import org.junit.*;
import static org.junit.Assert.*;
import uk.ac.susx.inf.ianw.shareManagement.UnknownCompanyException;

/**
 *
 * @author twhume
 */
public class PortfolioCalculatorTest {
    
    public PortfolioCalculatorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testReadingFromPortfolio() throws NamingException, RemoteException, NotBoundException, UnknownCompanyException {
        ShareValue sv = new ShareValue("Microsoft", 2.0);
        List<ShareValue> sl = new ArrayList<ShareValue>();
        sl.add(new ShareValue("Microsoft", 2.0));
        sl.add(new ShareValue("Oracle", 3.0));
        
        PortfolioCalculator pc = new PortfolioCalculator();
        pc.populatePrices(sl);
        
        assertTrue(sl.get(0).getValue()>0);
        assertTrue(sl.get(1).getValue()>0);
        
    }
}
