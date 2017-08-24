package models;

public class ProductPrice {

	private double prixAchatTTC;
	private double tva;
	private double prixVenteHT;
	private double prixVenteTTC;


	public ProductPrice(double prixAchatTTC, double tva, double prixVenteHT, double prixVenteTTC) {
		this.prixAchatTTC = prixAchatTTC;
		this.tva = tva;
		this.prixVenteHT = prixVenteHT;
		this.prixVenteTTC = prixVenteTTC;
	}


	public double getPrixAchatTTC() {
		return prixAchatTTC;
	}
	public void setPrixAchatTTC(double prixAchatTTC) {
		this.prixAchatTTC = prixAchatTTC;
	}


	public double getTva() {
		return tva;
	}
	public void setTva(double tva) {
		this.tva = tva;
	}


	public double getPrixVenteHT() {
		return prixVenteHT;
	}
	public void setPrixVenteHT(double prixVenteHT) {
		this.prixVenteHT = prixVenteHT;
	}


	public double getPrixVenteTTC() {
		return prixVenteTTC;
	}
	public void setPrixVenteTTC(double prixVenteTTC) {
		this.prixVenteTTC = prixVenteTTC;
	}

}
