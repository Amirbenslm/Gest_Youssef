package models;

public class DepotStock extends Depot{

	private int qnt;

	public DepotStock(Depot depot, int qnt){
		super(depot.getCode(), depot.getName(), depot.getComments());
		this.qnt = qnt;
	}

	
	public int getQnt() {
		return qnt;
	}

	public void setQnt(int qnt) {
		this.qnt = qnt;
	}


}
