package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import models.Product;
import models.ProductStock;
import models.ui.AlertError;

public class AllProductsInDepotController implements Initializable{

	@FXML Button btnPrint;

	@FXML TableView<ProductStock> tableViewProducts;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Double> columnStock;

	@FXML TabPane mainTabPane;
	@FXML Tab tabAvailableProducts;
	@FXML Tab tabOutOfStockProducts;
	@FXML Tab tabMissingProducts;

	private Integer depotCode = null;
	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));

		tableViewProducts.setRowFactory(new TableViewRowFactory());

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnPrint.setOnAction(actionEventHandler);

		tableViewProducts.setItems(productsStockData);
		
		tableViewProducts.setOnMousePressed(new TableViewMousePressedHandler());

		mainTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListenerTab());
	}

	public void setDepotCode(int depotCode){
		this.depotCode = depotCode;
		updateTableViewData();
	}

	private void updateTableViewData(){
		Tab selectedTab = mainTabPane.getSelectionModel().getSelectedItem();

		productsStockData.clear();

		if (selectedTab == null || depotCode == null) {
			return;
		}

		try {
			ArrayList<String> productsCodes = new ArrayList<>();
			
			if (selectedTab == tabAvailableProducts) {
				productsCodes = AppDataBaseManager.shared.getAllAvailableProductsCodesByDepotCode(depotCode);
			}else if (selectedTab == tabOutOfStockProducts) {
				productsCodes = AppDataBaseManager.shared.getAllOutOfStockProductsCodesByDepotCode(depotCode);
			}else if (selectedTab == tabMissingProducts) {
				productsCodes = AppDataBaseManager.shared.getAllMissingProductsCodesByDepotCode(depotCode);
			}
			
			for (int i=0;i<productsCodes.size();i++){
				Product product = AppDataBaseManager.shared.getProductByCode(productsCodes.get(i));

				Double stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i), depotCode);

				productsStockData.add(new ProductStock(product, stock));
			}
			
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0043", "Fatal Error", e.getMessage());
			alert.showAndWait();
		}

	}


	private class ChangeListenerTab implements ChangeListener<Tab>{

		@Override
		public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
			updateTableViewData();
		}

	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnPrint) {
				int x;
			}
		}
	}
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isSecondaryButtonDown()  
					&& tableViewProducts.getSelectionModel().getSelectedItem() != null)  {

				Product product = tableViewProducts.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentProductDetailsView(product);
			}

		}

	}
	
	private class TableViewRowFactory implements Callback<TableView<ProductStock>, TableRow<ProductStock>> {

		@Override
		public TableRow<ProductStock> call(TableView<ProductStock> param) {
			return new TableRow<ProductStock>() {
				@Override
				protected void updateItem(ProductStock item, boolean empty) {
					super.updateItem(item, empty);
					
					if (item == null || mainTabPane.getSelectionModel().getSelectedItem() == tabMissingProducts) {
						setStyle("");
					}else {
						if (item.getQnt() >= 0) {
							setStyle("");
						}else{
							setStyle("-fx-background-color: red;");
						}
					}
					
				}
			};
		}
		
	}

}
