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
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
        PurchaseRequest req = new PurchaseRequest(-3772258020141942224L, System.currentTimeMillis(), "Microsoft", 20);
        req.setSignature(null);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oout = new ObjectOutputStream(bout);
        oout.writeObject(req);
        oout.close();
        
        InputStream is = getClass().getResourceAsStream("/keystore.jks");
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(is, null);
        
        Key key = ks.getKey("selfsigned", new String("password").toCharArray());
        System.err.println(key);
        PrivateKey privateKey = (PrivateKey) key;
        Signature signature = Signature.getInstance("SHA1withDSA");

        signature.initSign(privateKey);
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
        
        ShareBroker sb = (ShareBroker) LocateRegistry.getRegistry("localhost", 40090).lookup("ShareBroker");
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
        
        /* Output of this on second run:
         * 
         * Got java.io.BufferedInputStream@58f39b3a
[
[
  Version: V3
  Subject: CN=Tom Hume, OU=Department of Informatics, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
  Signature Algorithm: SHA1withRSA, OID = 1.2.840.113549.1.1.5

  Key:  Sun RSA public key, 2048 bits
  modulus: 19397973928994531409103562591168463157000201189520750409478091396966935779808261894001502271125452760702744400765049102470685344952183268870499779974656996601509876376889907238037482784220226244603601953415566041605720054156082947433928100536229789694405142491168542483054454811369032496438260193205633859812438842673781214271624421887061573296843108921832772204190244648258436569520553945576291200078906943767684392812183032952208230591706654247798895851586510375453420857050847188781681383987443878351693864348374976420564164458853590295916157126526359665085211657842835306426434647936413151698101052871861815034931
  public exponent: 65537
  Validity: [From: Fri Mar 09 10:13:31 GMT 2012,
               To: Mon Mar 04 10:13:31 GMT 2013]
  Issuer: CN=Tom Hume, OU=Department of Informatics, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
  SerialNumber: [    4f59d7cb]

]
  Algorithm: [SHA1withRSA]
  Signature:
0000: 6A FE 33 A3 0B DE 59 73   23 56 EA 37 AE 6B 15 89  j.3...Ys#V.7.k..
0010: A8 3D 7A 95 00 9C 04 73   24 7D F4 C9 D5 A6 47 50  .=z....s$.....GP
0020: 60 09 DB DF B8 AA 49 EF   6F FE 56 1B F0 2D DF 71  `.....I.o.V..-.q
0030: D0 E6 20 2A 99 45 3E 45   A0 C2 2F 3B 0B 41 DE 50  .. *.E>E../;.A.P
0040: EA 6E 03 AA 58 48 38 5A   94 94 E7 7B 87 B7 BD 3C  .n..XH8Z.......<
0050: 87 2B 2B E7 8B 07 8B 99   01 2D F1 83 6E 79 13 F3  .++......-..ny..
0060: 59 05 CE 8D 11 A9 61 A0   90 CE 20 1E 6E 37 2B EA  Y.....a... .n7+.
0070: 60 38 8D EB 7B 8B 3B 0E   CC 7C 63 C8 F6 69 62 E6  `8....;...c..ib.
0080: 89 70 A6 5E CD 8A 0B 75   E0 C7 90 12 E4 3F 71 CD  .p.^...u.....?q.
0090: E7 0B C6 05 11 D2 15 BC   3B 71 87 67 0E 99 07 7E  ........;q.g....
00A0: A9 4A 93 28 BC 85 95 D7   AC 44 33 F8 EC 40 F0 8A  .J.(.....D3..@..
00B0: E2 40 42 1F 93 B4 DC 57   49 16 98 12 AD 91 BB 64  .@B....WI......d
00C0: 7D A1 15 4B 84 E1 68 FC   A4 AB CE 57 E7 F4 24 AF  ...K..h....W..$.
00D0: DA E7 6C 1E A0 1D 89 D3   52 4B A0 A4 8C 47 7A CD  ..l.....RK...Gz.
00E0: 53 34 39 CE 58 81 4F 98   A5 B7 59 6F B3 E7 7F 4B  S49.X.O...Yo...K
00F0: B6 4D E5 94 E9 CE C7 ED   0B C2 F7 7A 1D 29 57 02  .M.........z.)W.

]
CustomerIdentifier [id=9040539406979066810, name=Tom Hume, dealerCertificate=[
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

         */
        
        /* And with DSA:
         * Got java.io.BufferedInputStream@19632847
[
[
  Version: V3
  Subject: CN=Tom Hume, OU=Informatics Department, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
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
    e76acb20 7148e790 51c9c1ef bc9eb844 3d96b5f8 1a921f75 38a5180d f9a8dc77
    50bbc201 9d373a5a b3b18365 6c4b0cfc 3fe02eae aaff78a3 484f8426 be98da75
    cf1d9142 2ac2357c 1398505c 5146ceb9 8a7a23bc cf2a04df 850028f4 27a14899
    8b238b30 2b4d3e1e 0ddcf164 4d0fbc1f 9fe0784c 3b5ebbfa c2f5fcaa 1dfd4487

  Validity: [From: Thu Mar 15 16:17:06 GMT 2012,
               To: Wed Jun 13 17:17:06 BST 2012]
  Issuer: CN=Tom Hume, OU=Informatics Department, O=University of Sussex, L=Brighton, ST=East Sussex, C=GB
  SerialNumber: [    4f621602]

]
  Algorithm: [SHA1withDSA]
  Signature:
0000: 30 2C 02 14 20 58 50 E6   22 BA 86 14 61 9C 6B 1B  0,.. XP."...a.k.
0010: 78 E0 AB BA 4F C9 0B C3   02 14 22 E3 9A 75 30 18  x...O....."..u0.
0020: A5 E9 B4 A5 F5 B1 AC 4E   DC EE F9 F9 25 07        .......N....%.

]
CustomerIdentifier [id=-3772258020141942224, name=Tom Hume, dealerCertificate=[
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
