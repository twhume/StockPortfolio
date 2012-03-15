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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.naming.NamingException;
import org.junit.*;
import static org.junit.Assert.*;
import uk.ac.susx.inf.ianw.shareManagement.*;

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

    public void testMakePurchase() throws RemoteException, NotBoundException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InvalidKeyException, SignatureException, InvalidRequestException {

        // Register with the ShareBroker
        
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        CustomerIdentifier ci = sb.register(ks.getCertificate("selfsigned"), "Tom Hume");
        java.security.cert.Certificate dealerCert = ci.getDealerCertificate();
        
        
        // Make a purchase
        
        PurchaseRequest req = new PurchaseRequest(ci.getId(), System.currentTimeMillis(), "Microsoft", 20);
        req.setSignature(null);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(req);
        oout.close();
        
        
        Key key = ks.getKey("selfsigned", new String("password").toCharArray());
        PrivateKey privateKey = (PrivateKey) key;
        Signature signature = Signature.getInstance("SHA1withDSA");

        signature.initSign(privateKey);
        signature.update(bout.toByteArray());
        req.setSignature(signature.sign());
        
        PurchaseResult pr = sb.buyShares(req);
        
        // Validate the signature of the response
        
        
        byte[] signatureReceived = pr.getSignature();
        
        pr.setSignature(null);
        bout = new ByteArrayOutputStream();
        oout = new ObjectOutputStream(bout);
        oout.writeObject(pr);
        oout.close();

        signature = Signature.getInstance("SHA1withDSA");

        signature.initVerify(dealerCert);
        signature.update(bout.toByteArray());
        System.err.println("Received="+signatureReceived.length);
        for (int i=0; i<signatureReceived.length; i++) {
            System.err.print(signatureReceived[i] + ",");
        }
        System.err.println();
        
        byte[] signatureGenerated = signature.sign();
        System.err.println("Generated="+signatureGenerated.length);
        for (int i=0; i<signatureGenerated.length; i++) {
            System.err.print(signatureGenerated[i] + ",");
        }
        System.err.println();

        assertTrue(Arrays.equals(signatureReceived, signatureGenerated));

        
    }

    @Test
    public void testLoadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, InvalidRequestException {
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        System.err.println("Got " + is);
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        System.err.println(ks.getCertificate("selfsigned"));
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        CustomerIdentifier ci = sb.register(ks.getCertificate("selfsigned"), "Tom Hume");
        System.err.println(ci);
        ci.getDealerCertificate();
    }
    
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
