package views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ListView.EditEvent;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.Stage;
import managers.StringsManager;
import models.ui.AlertError;

public class AddClientController implements Initializable{

	static private String STRING_TAP_TO_ADD_NEW_CLIENT = "Ajouter (+)";

	@FXML Button btnSave;
	@FXML Button btnCancel;

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtLastName;
	@FXML TextArea txtAdress;

	@FXML ListView<String> listViewPhones;
	@FXML ListView<String> listViewFaxes;

	private StringsManager stringsManager = new StringsManager();

	private ObservableList<String> phonesNumbersData = FXCollections.observableArrayList();
	private ObservableList<String> faxesNumbersData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		listViewPhones.setItems(phonesNumbersData);
		listViewPhones.setCellFactory(TextFieldListCell.forListView());
		listViewPhones.setOnEditCommit(new StringEditEventHandler(listViewPhones));

		listViewFaxes.setItems(faxesNumbersData);
		listViewFaxes.setCellFactory(TextFieldListCell.forListView());
		listViewFaxes.setOnEditCommit(new StringEditEventHandler(listViewFaxes));

		phonesNumbersData.add(STRING_TAP_TO_ADD_NEW_CLIENT);
		faxesNumbersData.add(STRING_TAP_TO_ADD_NEW_CLIENT);
		
		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnSave.setOnAction(actionEventHandler);
		btnCancel.setOnAction(actionEventHandler);
	}
	
	
	private void closeStage(){
		((Stage)btnCancel.getScene().getWindow()).close();
	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent>{

		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnCancel) {
				closeStage();
			}else if (event.getSource() == btnSave) {
				
			}
		}
		
	}

	private class StringEditEventHandler implements EventHandler<EditEvent<String>>{

		private ListView<String> listView;

		public StringEditEventHandler(ListView<String> listView) {
			this.listView = listView;
		}

		@Override
		public void handle(EditEvent<String> event) {

			boolean editMode = false;

			if (listView == listViewPhones) {
				editMode = event.getIndex() < phonesNumbersData.size()-1;
			}else if (listView == listViewFaxes){
				editMode = event.getIndex() < faxesNumbersData.size()-1;
			}

			String firstChar = "";

			if (event.getNewValue().startsWith("+")) {
				firstChar = "+";
			}

			String newNumber = stringsManager.getOnlyNumbers(event.getNewValue());

			if (newNumber.equals("") && editMode) {

				if (listView == listViewPhones) {
					phonesNumbersData.remove(event.getIndex());
				}else if (listView == listViewFaxes){
					faxesNumbersData.remove(event.getIndex());
				}

			}else if (!newNumber.equals("")){

				newNumber = firstChar+newNumber;

				if (editMode){

					if (listView == listViewPhones) {
						phonesNumbersData.set(event.getIndex(), newNumber);
					}else if (listView == listViewFaxes){
						faxesNumbersData.set(event.getIndex(), newNumber);
					}

				}else{

					if (listView == listViewPhones) {
						if (phonesNumbersData.contains(newNumber)) {
							AlertError alert = new AlertError("Numéro déja ajouté", null, "Numéro déja ajouté");
							alert.showAndWait();
						}else{
							phonesNumbersData.add(phonesNumbersData.size()-1, newNumber);	
						}
					}else if (listView == listViewFaxes){
						if (faxesNumbersData.contains(newNumber)) {
							AlertError alert = new AlertError("Numéro déja ajouté", null, "Numéro déja ajouté");
							alert.showAndWait();
						}else{
							faxesNumbersData.add(faxesNumbersData.size()-1, newNumber);	
						}
					}

				}

			}

		}

	}
}
