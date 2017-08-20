package views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class ProductDetailsController implements Initializable{

	
	@FXML TextField txtCode;
	@FXML TextField txtLibelle;
	
	@FXML TextField txtPrixDachatTTC;
	@FXML TextField txtTVA;
	@FXML TextField txtPrixDeVentHT;
	@FXML TextField txtPrixDeVentTTC;
	

	public ProductDetailsController() {
		System.out.println("$0X");
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		System.out.println("$1X");
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		System.out.println("$2X");
	}
	
}
