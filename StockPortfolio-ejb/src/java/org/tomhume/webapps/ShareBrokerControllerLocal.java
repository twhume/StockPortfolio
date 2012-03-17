/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tomhume.webapps;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import javax.ejb.Local;
import uk.ac.susx.inf.ianw.shareManagement.InvalidRequestException;
import uk.ac.susx.inf.ianw.shareManagement.PurchaseResult;

/**
 *
 * @author twhume
 */
@Local
public interface ShareBrokerControllerLocal {

    List<ShareValue> getPortfolioValues();
    
    public PurchaseResult makePurchase(String company, double amount) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, RemoteException, NotBoundException, UnrecoverableKeyException, InvalidKeyException, SignatureException, InvalidRequestException;

    
}
