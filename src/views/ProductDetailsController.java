package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
import models.AlertError;
import models.Depot;
import models.Stock;

public class ProductDetailsController implements Initializable{


	@FXML TextField txtCode;
	@FXML TextField txtLibelle;

	@FXML TextField txtPrixDachatTTC;
	@FXML TextField txtTVA;
	@FXML TextField txtPrixDeVentHT;
	@FXML TextField txtPrixDeVentTTC;

	@FXML TableView<Stock> tableViewDepots;
	@FXML TableColumn<Stock, String> columnDepotName;
	@FXML TableColumn<Stock, String> columnStock;


	private ObservableList<Stock> depotsData = FXCollections.observableArrayList();

	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnDepotName.setCellValueFactory(new PropertyValueFactory<>("DepotName"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("QntAsString"));

		columnStock.setCellFactory(TextFieldTableCell.forTableColumn());
		
		TableColumnChangeEventHandler tableColumnChangeEventHandler = new TableColumnChangeEventHandler();
		columnStock.setOnEditCommit(tableColumnChangeEventHandler);
		
		
		tableViewDepots.setItems(depotsData);
		
		try {
			loadDepotsData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			AlertError alert = new AlertError("ERROR ERR0002", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}


	private void loadDepotsData() throws SQLException{
		ArrayList<Depot> depotsList = AppDataBaseManager.shared.getAllDepots();
		for (int i=0;i<depotsList.size();i++){
			depotsData.add(new Stock(depotsList.get(i), null, 0));
		}
	}
	
	
	
	
	private class TableColumnChangeEventHandler implements EventHandler<CellEditEvent<Stock, String>> {

		@Override
		public void handle(CellEditEvent<Stock, String> event) {
			StringsManager stringsManager = new StringsManager();
			
			String newValue = stringsManager.getOnlyNumbers(event.getNewValue());
			
			if (newValue.equals("")) {
				newValue = stringsManager.getOnlyNumbers(event.getOldValue());
			}
			
			if (newValue.equals("")) {
				newValue = "0";
			}
			
			int newQNT = Integer.parseInt(newValue);
			depotsData.get(event.getTablePosition().getRow()).setQnt(newQNT);;
			
			tableViewDepots.refresh();
		}
		
		
	}

}



