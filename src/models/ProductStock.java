package models;

public class ProductStock extends Product{

	private int qnt;

	public ProductStock(Product product, int qnt) {
		super(product.getCode(), product.getName(), product.getPrice());
		this.qnt = qnt;
	}

	public int getQnt() {
		return qnt;
	}

	public void setQnt(int qnt) {
		this.qnt = qnt;
	}

}
