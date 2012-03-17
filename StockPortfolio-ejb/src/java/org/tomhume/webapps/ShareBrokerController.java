/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import uk.ac.susx.inf.ianw.shareManagement.InvalidRequestException;
import uk.ac.susx.inf.ianw.shareManagement.PurchaseRequest;
import uk.ac.susx.inf.ianw.shareManagement.PurchaseResult;
import uk.ac.susx.inf.ianw.shareManagement.ShareBroker;

/**
 * Calculates the total values of shares held in various companies
 * 
 * @author twhume
 */
@Stateless
public class ShareBrokerController implements ShareBrokerControllerLocal {

    private static final long CUSTOMER_ID = 1762632200919605846L;
    private static final String PASSWORD = "password";  // Ow. I know.

    @PersistenceContext(unitName = "StockPortfolio-ejbPU")
    private EntityManager em;
        
    /**
     * Makes a single PurchaseRequest via the ShareBroker service, signs it,
     * sends it off, validates the signature of the response, and returns it
     * if all went well.
     */
    
    public PurchaseResult makePurchase(String company, double amount)  throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidRequestException {

        /* Load the keystore */
        
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);

        /* Create a PurchaseRequest, sign it with our private key, and send it to the ShareBrokerController */
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        PurchaseRequest req = new PurchaseRequest(CUSTOMER_ID, System.currentTimeMillis(), company, amount);
        req.setSignature(null);

        Key key = ks.getKey("selfsigned", PASSWORD.toCharArray());
        PrivateKey privateKey = (PrivateKey) key;
        
        Signature signature = Signature.getInstance("SHA1withDSA");
        byte[] objBytes = objectToBytes(req);
        signature.initSign(privateKey);
        signature.update(objBytes);
        req.setSignature(signature.sign());
        
        PurchaseResult res = sb.buyShares(req);
        
        /* Validate the signature of the response is valid, and return the PurchaseResult if so */
        
        byte[] signatureReceived = res.getSignature();
        res.setSignature(null);
        byte[] signatureGenerated = objectToBytes(res);
        boolean sigOK = signatureOK(signatureGenerated, signatureReceived);
        if (sigOK) return res;
        return null;
    }
    
    /**
     * Helper method. Validates that the signature generated from signing one
     * byte array matches the signature passed in in another.
     * 
     * @param input         byte array from which to generate a signature
     * @param comparison    comparison byte array for signature
     * @return              true if the signature is valid, false if not
     * 
     * @throws CertificateException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException 
     */
    
    private boolean signatureOK(byte[] input, byte[] comparison) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");  
        java.security.cert.Certificate dealerCert = cf.generateCertificate(getClass().getResourceAsStream("/server.crt"));  
        Signature sig = Signature.getInstance("SHA1withDSA");
        sig.initVerify(dealerCert);
        sig.update(input);
        return sig.verify(comparison);
    }

    /**
     * Helper method. Serialises the Object passed in into a byte array
     * 
     * @param o Object to serialise
     * @return byte array of serialised object
     * 
     * @throws IOException 
     */
    
    private byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(o);
        oout.close();
        return bout.toByteArray();
    }
    
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
     * price set, looks up the current price for the company using the ShareBrokerController
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
            ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");

            for (ShareValue v: l) {
                if (v.getValue()==ShareValue.NOT_SET) {
                    System.err.println("Looking up " + v.getCompany());
                    System.err.println("price="+sb.getPrice(v.getCompany()));
                    v.setValue(sb.getPrice(v.getCompany()) * v.getNumShares());
                }
            }
        } catch (RemoteException n) {
            System.err.println(n);
            n.printStackTrace();
        } catch (NotBoundException n) {
            System.err.println(n);
            n.printStackTrace();
        } finally {
            return l;
        }
    }
    
    
}
