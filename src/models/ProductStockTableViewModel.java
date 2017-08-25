package models;

public class ProductStockTableViewModel {

	private Depot depot;
	private Product product;
	private int qnt;
	
	public ProductStockTableViewModel(Depot depot, Product product, int qnt) {
		this.depot = depot;
		this.product = product;
		this.qnt = qnt;
	}
	
	
	public Depot getDepot() {
		return depot;
	}


	public Product getProduct() {
		return product;
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
	
}
