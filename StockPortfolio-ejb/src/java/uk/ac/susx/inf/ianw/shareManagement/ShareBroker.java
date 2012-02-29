package uk.ac.susx.inf.ianw.shareManagement;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.cert.Certificate;

public interface ShareBroker extends Remote {
	public static final String SHAREBROKER = "ShareBroker";
	
	double getPrice(String company) throws RemoteException, UnknownCompanyException;
	
	public CustomerIdentifier register(Certificate publicKey, String name) 
			throws RemoteException, InvalidRequestException; 
	
	public PurchaseResult buyShares(PurchaseRequest req) throws RemoteException, InvalidRequestException;
}
