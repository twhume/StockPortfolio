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

    @Test
    public void testMakePurchase() throws RemoteException, NotBoundException, KeyStoreException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, CertificateException, InvalidKeyException, SignatureException, InvalidRequestException {
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("rmi:/ShareBroker");
        PurchaseRequest req = new PurchaseRequest(-5469711340414773356L, System.currentTimeMillis(), "Microsoft", 20);
        req.setSignature(null);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(req);
        oout.close();
        
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        
        PrivateKey key = (PrivateKey) ks.getKey("selfsigned", new String("password").toCharArray());
        Signature signature = Signature.getInstance("SHA1withRSA");

        signature.initSign(key);
        signature.update(bout.toByteArray());
        req.setSignature(signature.sign());
        
        sb.buyShares(req);
    }

    public void testLoadKeyStore() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, InvalidRequestException {
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        System.err.println("Got " + is);
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        System.err.println(ks.getCertificate("selfsigned"));
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("rmi:/ShareBroker");
        CustomerIdentifier ci = sb.register(ks.getCertificate("selfsigned"), "Tom Hume");
        System.err.println(ci);
        
        /**
         * Output of running this test was:
         * 
         * CustomerIdentifier [id=-5469711340414773356, name=Tom Hume, dealerCertificate=[
[
  Version: V3
  Subject: CN=Ian Wakeman, OU=Informatics, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
  Signature Algorithm: SHA1withDSA, OID = 1.2.840.10040.4.3

  Key:  Sun DSA Public Key
    Parameters:DSA
	p:     fd7f5381 1d751229 52df4a9c 2eece4e7 f611b752 3cef4400 c31e3f80 b6512669
    455d4022 51fb593d 8d58fabf c5f5ba30 f6cb9b55 6cd7813b 801d346f f26660b7
    6b9950a5 a49f9fe8 047b1022 c24fbba9 d7feb7c6 1bf83b57 e7c6a8a6 150f04fb
    83f6d3c5 1ec30235 54135a16 9132f675 f3ae2b61 d72aeff2 2203199d d14801c7
	q:     9760508f 15230bcc b292b982 a2eb840b f0581cf5
	g:     f7e1a085 d69b3dde cbbcab5c 36b857b9 7994afbb fa3aea82 f9574c0b 3d078267
    5159578e bad4594f e6710710 8180b449 167123e8 4c281613 b7cf0932 8cc8a6e1
    3c167a8b 547c8d28 e0a3ae1e 2bb3a675 916ea37f 0bfa2135 62f1fb62 7a01243b
    cca4f1be a8519089 a883dfe1 5ae59f06 928b665e 807b5525 64014c3b fecf492a

  y:
    61b018a3 611983e5 5b96eb64 e11e7d54 c8fd8f88 802503d4 bee86432 003ba0ca
    0d987430 cf91463c 18d2efb2 e81b4509 a139d99c 4ca03690 2012bf1b 2e0bfb5a
    fcf16c91 25f3d97c 8fae4c8c 054ee12b 85861f1d e79b3e73 37a4dd12 7f11a0c5
    69408df5 0e2af2b8 427cbb8b b8bb3d6c 73b6e29e 66dee53b 23bd5017 c57b6888

  Validity: [From: Sat Feb 19 08:16:28 GMT 2011,
               To: Fri May 20 09:16:28 BST 2011]
  Issuer: CN=Ian Wakeman, OU=Informatics, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
  SerialNumber: [    4d5f7c5c]

]
  Algorithm: [SHA1withDSA]
  Signature:
0000: 30 2C 02 14 0D 62 D1 B5   CB F0 8E D8 3F E7 EE FE  0,...b......?...
0010: 54 06 A0 BA FE 32 54 E8   02 14 71 ED 50 8A CE 97  T....2T...q.P...
0020: CC BD 7E FE 5E E0 03 DF   48 6B 30 BA 58 C1        ....^...Hk0.X.

]]
]]
* 
         */
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
