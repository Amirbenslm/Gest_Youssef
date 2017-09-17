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


	public void setBillCode(String billCode) {
		this.billCode = billCode;
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
	
	public double calculateTotal(){
		double total = 0;
		
		for (int i=0;i<products.size();i++) {
			total += products.get(i).getTotalPrice();
		}
		
		return total;
	}
	
	public double calculatTotalWithDiscount(){
		double total = calculateTotal();
		return total-(total*(discount/100));
	}
	
}
