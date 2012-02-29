package uk.ac.susx.inf.ianw.shareManagement;

import java.io.Serializable;
import java.util.Arrays;

public class PurchaseRequest extends Signable implements Serializable {

	private static final long serialVersionUID = -1778243341910610906L;

	@Override
	public String toString() {
		return "PurchaseRequest [company=" + company + ", amount=" + amount
				+ ", getId()=" + getId() + ", getSignature()="
				+ Arrays.toString(getSignature()) + "]";
	}

	
	private String company;
	private double amount;
	
	
	public PurchaseRequest(long id, long nonce, String company, double amount) {
		super(id, nonce);
		this.company = company;
		this.amount = amount;
	}

	public String getCompany() {
		return company;
	}

	public double getAmount() {
		return amount;
	}

	
	
}
