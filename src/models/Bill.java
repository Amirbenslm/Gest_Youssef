package models;

import java.sql.Timestamp;
import java.util.ArrayList;

public class Bill {

	private String billCode;
	private String clientCode;
	private int depotCode;
	private Timestamp date;
	private double discount;
	private ArrayList<ProductBill> products = new ArrayList<>();
	
	
	public Bill(String billCode, String clientCode, int depotCode, Timestamp date, double discount,
			ArrayList<ProductBill> products) {
		super();
		this.billCode = billCode;
		this.clientCode = clientCode;
		this.depotCode = depotCode;
		this.date = date;
		this.discount = discount;
		this.products = products;
	}


	public String getBillCode() {
		return billCode;
	}
	

	public String getClientCode() {
		return clientCode;
	}


	public int getDepotCode() {
		return depotCode;
	}


	public Timestamp getDate() {
		return date;
	}


	public double getDiscount() {
		return discount;
	}


	public ArrayList<ProductBill> getProducts() {
		return products;
	}
	
}
