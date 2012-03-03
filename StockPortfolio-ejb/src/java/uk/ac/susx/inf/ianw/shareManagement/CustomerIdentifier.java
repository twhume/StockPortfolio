package uk.ac.susx.inf.ianw.shareManagement;

import java.io.Serializable;
import java.security.cert.Certificate;

public class CustomerIdentifier implements Serializable {
	private long id;
	private String name;
	private Certificate dealerCertificate;
	
	@Override
	public String toString() {
		return "CustomerIdentifier [id=" + id + ", name=" + name
				+ ", dealerCertificate=" + dealerCertificate + "]";
	}

	public Certificate getDealerCertificate() {
		return dealerCertificate;
	}


	public CustomerIdentifier(long id, String name, Certificate dealerCertificate) {
		super();
		this.id = id;
		this.name = name;
		this.dealerCertificate = dealerCertificate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
