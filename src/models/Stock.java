package models;

public class Stock {

	private Depot depot;
	private Product product;
	private int qnt;
	
	public Stock(Depot depot, Product product, int qnt) {
		this.depot = depot;
		this.product = product;
		this.qnt = qnt;
	}
	
	public String getDepotName(){
		return depot.getName();
	}
	
	public String getProductName(){
		return product.getName();
	}
	
	public int getQnt() {
		return qnt;
	}

	public void setQnt(int qnt) {
		this.qnt = qnt;
	}
	
	public String getQntAsString() {
		return Integer.toString(qnt);
	}
	
	
}
