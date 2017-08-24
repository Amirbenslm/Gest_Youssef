package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import models.AlertError;
import models.AlertWarning;

public class AddEditDepotDetailsController implements Initializable {

	@FXML Button btnSave;
	@FXML Button btnCancel;

	@FXML FlowPane containerFP;

	Pane paneDepotDetails;
	DepotDetailsController depotDetailsController;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

		loadDepoDetailsView();

		ButtonActionEventHandler buttonActionEventHandler = new ButtonActionEventHandler();
		btnSave.setOnAction(buttonActionEventHandler);
		btnCancel.setOnAction(buttonActionEventHandler);
		
		updateBtnSaveView();
		
		depotDetailsController.txtName.textProperty().addListener(new TextFieldChangeListener());
	}

	private void loadDepoDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/DepotDetails.fxml"));
			paneDepotDetails = loader.load();
			depotDetailsController = loader.getController();
			containerFP.getChildren().add(paneDepotDetails);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	private void clearTxtNameFromSpaces(){
		depotDetailsController.txtName.setText(depotDetailsController.txtName.getText().replaceAll(" ", ""));
	}

	private void updateBtnSaveView(){
		if (depotDetailsController.txtName.getText().equals("")) {
			btnSave.setDisable(true);
		}else{
			btnSave.setDisable(false);
		}
	}
	
	private void closeStage(){
		((Stage) containerFP.getScene().getWindow()).close();
	}
	
	
	private class ButtonActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSave) {
				

				String name = depotDetailsController.txtName.getText();
				String comments = depotDetailsController.txtObservations.getText();

				try {
					
					if (AppDataBaseManager.shared.isDepotNameExist(name)) {
						AlertWarning alert = new AlertWarning("Nom existe déjà", 
								"Le nom du dépôt ' "+name+" ' existe déjà !", 
								"Réessayer avec un autre nom");
						alert.showAndWait();
					}else{
						AppDataBaseManager.shared.addNewDepot(name, comments);
						
						closeStage();
					}
				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0001", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
				}
				
			}
			else if (event.getSource() == btnCancel) {
				closeStage();
			}
			
		}
	}
	
	private class TextFieldChangeListener implements ChangeListener<String>{

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
			clearTxtNameFromSpaces();
			updateBtnSaveView();
		}
	}
}
