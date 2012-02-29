package uk.ac.susx.inf.ianw.shareManagement;

import java.io.Serializable;

public class Signable implements Serializable {

	private static final long serialVersionUID = 5363139415056721446L;
	private byte[] signature;
	private long id;
	private long nonce;
	

	public Signable(long id, long nonce) {
		super();
		this.id = id;
		this.nonce = nonce;
	}
	
	public long getNonce() {
		return nonce;
	}


	public long getId() {
		return id;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}
	
}
