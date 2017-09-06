package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import managers.AppDataBaseManager;
import models.Depot;
import models.Product;
import models.ProductStock;
import models.ui.ProductSearchPickedProductDelegate;

public class ProductSearchController implements Initializable {

	@FXML TextField txtCode;
	@FXML TextField txtName;

	@FXML Button btnSearch;

	@FXML Label lblDepotName;

	@FXML TableView<ProductStock> tableViewProductsStocks;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Integer> columnStock;

	public ProductSearchPickedProductDelegate delegate;

	private Depot depot;
	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));

		ActionEventHandler actionEventHandler = new ActionEventHandler();


		btnSearch.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtName.setOnAction(actionEventHandler);

		tableViewProductsStocks.setOnMousePressed(new TableViewMousePressedHandler());

		tableViewProductsStocks.setItems(productsStockData);
	}

	// Pass "" Empty strings if not want searchConstraint
	public void forceSearch(String productCode, String productName){
		txtCode.setText(productCode);
		txtName.setText(productName);
		refrechTableViewData();
	}
	
	public void configureDepot (Depot depot){
		this.depot = depot;
		lblDepotName.setText(depot.getName());
	}

	private void refrechTableViewData(){

		productsStockData.clear();

		ArrayList<String> productsCodes;

		try {
			productsCodes = AppDataBaseManager.shared.getAllProductsCodes(txtCode.getText(), txtName.getText());

			for (int i=0;i<productsCodes.size();i++){

				Product product = AppDataBaseManager.shared.getProductByCode(productsCodes.get(i));

				Integer stock = null;

				if (depot == null) {
					stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i));
				}else{
					stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i), depot.getCode());
				}


				productsStockData.add(new ProductStock(product, stock));
			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void closeStage(){
		((Stage) tableViewProductsStocks.getScene().getWindow()).close();
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch || event.getSource() == txtCode 
					|| event.getSource() == txtName) {
				refrechTableViewData();
			}
		}
	}


	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewProductsStocks.getSelectionModel().getSelectedItem() != null )  {
				delegate.productSearchPickedProduct(tableViewProductsStocks.getSelectionModel().getSelectedItem());
				closeStage();
			}else if (event.isSecondaryButtonDown() 
					&& tableViewProductsStocks.getSelectionModel().getSelectedItem() != null) {
				Product product = tableViewProductsStocks.getSelectionModel().getSelectedItem();

				RootViewController.selfRef.presentProductsEditsView(product);
			}

		}

	}
}