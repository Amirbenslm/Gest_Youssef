package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.rmi.CORBA.PortableRemoteObjectDelegate;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import models.AlertError;

public class AddNewProductController implements Initializable {


	@FXML Button btnSave;
	@FXML Button btnCancel;
	@FXML FlowPane containerFP;

	Pane paneProductDetails;
	ProductDetailsController productDetailsController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		loadProductDetailsView();
		updateBtnSaveView();

	}

	private void loadProductDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/ProductDetails.fxml"));
			paneProductDetails = loader.load();
			productDetailsController = loader.getController();
			containerFP.getChildren().add(paneProductDetails);
		} catch (IOException e) {
			AlertError alert = new AlertError("ERROR ERR0003", "Fatal Error", e.getMessage());
			alert.showAndWait();
			closeStage();
		}
	}

	private void updateBtnSaveView(){
		if ( (productDetailsController.txtCode.getText().equals("")) || 
				(productDetailsController.txtLibelle.getText().equals("")) ||
				(productDetailsController.txtPrixDachatTTC.getText().equals("")) ||
				(productDetailsController.txtTVA.getText().equals("")) ||
				(productDetailsController.txtPrixDeVentHT.getText().equals("")) ||
				(productDetailsController.txtPrixDeVentTTC.getText().equals("")) ){
			btnSave.setDisable(true);
		}else{
			btnSave.setDisable(false);
		}
	}

	private void closeStage(){
		((Stage) containerFP.getScene().getWindow()).close();
	}
}
