package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Depot;
import models.ProductStockTableViewModel;
import models.ui.AlertError;
import models.ui.StringConverterInteger;

public class ProductDetailsController implements Initializable{


	@FXML TextField txtCode;
	@FXML TextField txtLibelle;

	@FXML TextField txtPrixDachatTTC;
	@FXML TextField txtTVA;
	@FXML TextField txtPrixDeVentHT;
	@FXML TextField txtPrixDeVentTTC;

	@FXML TableView<ProductStockTableViewModel> tableViewDepots;
	@FXML TableColumn<ProductStockTableViewModel, String> columnDepotName;
	@FXML TableColumn<ProductStockTableViewModel, Integer> columnStock;


	private StringsManager stringsManager = new StringsManager();
	
	
	private ObservableList<ProductStockTableViewModel> depotsData = FXCollections.observableArrayList();



	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnDepotName.setCellValueFactory(new PropertyValueFactory<>("DepotName"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));

		columnStock.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterInteger()));
		columnStock.setOnEditCommit(new TableColumnChangeEventHandler());

		tableViewDepots.setItems(depotsData);
		
		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		
		txtCode.textProperty().addListener(textFieldChangeListener);
		txtLibelle.textProperty().addListener(textFieldChangeListener);
		txtPrixDachatTTC.textProperty().addListener(textFieldChangeListener);
		txtTVA.textProperty().addListener(textFieldChangeListener);
		txtPrixDeVentHT.textProperty().addListener(textFieldChangeListener);
		txtPrixDeVentTTC.textProperty().addListener(textFieldChangeListener);

		try {
			loadDepotsData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			AlertError alert = new AlertError("ERROR ERR0002", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}

	public ArrayList<ProductStockTableViewModel> getDepotsStocks(){
		ArrayList<ProductStockTableViewModel> depotsStocks = new ArrayList<>();
		
		for (int i=0; i< depotsData.size(); i++) {
			depotsStocks.add(depotsData.get(i));
		}
		
		return depotsStocks;
	}

	private void loadDepotsData() throws SQLException{
		ArrayList<Depot> depotsList = AppDataBaseManager.shared.getAllDepots();
		for (int i=0;i<depotsList.size();i++){
			depotsData.add(new ProductStockTableViewModel(depotsList.get(i), null, 0));
		}
	}


	private class TableColumnChangeEventHandler implements EventHandler<CellEditEvent<ProductStockTableViewModel, Integer>> {

		@Override
		public void handle(CellEditEvent<ProductStockTableViewModel, Integer> event) {
			depotsData.get(event.getTablePosition().getRow()).setQnt(event.getNewValue());
		}

	}
	
	
	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			TextField txt = (TextField) ((StringProperty)observable).getBean();
			
			if (txt == txtCode) {
				String ch = txt.getText().replaceAll(" ", "");
				ch = stringsManager.getOnlyLettersAndNumbers(ch);
				txt.setText(ch.toUpperCase());
			}
			else if (txt == txtLibelle) {
				txt.setText(stringsManager.removeUnwantedSpaces(txt.getText()));
			}
			else if (txt == txtPrixDachatTTC) {
				txt.setText(stringsManager.getDoubleFormat(txt.getText()));
			}
			else if (txt == txtTVA) {
				txt.setText(stringsManager.getDoubleFormat(txt.getText()));
			}
			else if (txt == txtPrixDeVentHT) {
				txt.setText(stringsManager.getDoubleFormat(txt.getText()));
			}
			else if (txt == txtPrixDeVentTTC) {
				txt.setText(stringsManager.getDoubleFormat(txt.getText()));
			}

		}
		
	}

}



