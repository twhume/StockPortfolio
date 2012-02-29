package uk.ac.susx.inf.ianw.shareManagement;

import java.io.Serializable;
import java.util.Arrays;

public class PurchaseResult extends Signable implements Serializable {

	private static final long serialVersionUID = 1931582022265826675L;
	private double price;
	private String company;
	private double amount;
	
	public PurchaseResult(long id, long nonce, double price, String company,
			double amount) {
		super(id, nonce);
		this.price = price;
		this.company = company;
		this.amount = amount;
	}
	public double getPrice() {
		return price;
	}
	public String getCompany() {
		return company;
	}
	public double getAmount() {
		return amount;
	}
	
	@Override
	public String toString() {
		return "PurchaseResult [price=" + price + ", company=" + company
				+ ", amount=" + amount + ", getId()=" + getId()
				+ ", getSignature()=" + Arrays.toString(getSignature()) + "]";
	}
	
}
