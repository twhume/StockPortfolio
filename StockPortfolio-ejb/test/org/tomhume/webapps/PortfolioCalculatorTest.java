/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.security.*;
import java.security.cert.*;
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
    
    private static final long CUSTOMER_ID = 1762632200919605846L;
    
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
    public void doTransactionWithStoredCertificate() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidRequestException {
        assertNotNull(makePurchaseRequest("Microsoft", 20));
    }
    
    private PurchaseResult makePurchaseRequest(String stock, double amount) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidRequestException {

        /* Load the keystore */
        
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);

        /* Create a PurchaseRequest, sign it with our private key, and send it to the ShareBroker */
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        PurchaseRequest req = new PurchaseRequest(CUSTOMER_ID, System.currentTimeMillis(), stock, amount);
        req.setSignature(null);

        Key key = ks.getKey("selfsigned", new String("password").toCharArray());
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
    
    private boolean signatureOK(byte[] input, byte[] comparison) throws CertificateException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");  
        java.security.cert.Certificate dealerCert = cf.generateCertificate(getClass().getResourceAsStream("/server.crt"));  
        Signature sig = Signature.getInstance("SHA1withDSA");
        sig.initVerify(dealerCert);
        sig.update(input);
        return sig.verify(comparison);
    }

    private byte[] objectToBytes(Object o) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(o);
        oout.close();
        return bout.toByteArray();
    }
    public void testLoadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, InvalidRequestException {
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        System.err.println("Got " + is);
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        System.err.println(ks.getCertificate("selfsigned"));
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        CustomerIdentifier ci = sb.register(ks.getCertificate("selfsigned"), "Tom Hume");
        
        System.err.println(ci.getId()); // 1762632200919605846
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
