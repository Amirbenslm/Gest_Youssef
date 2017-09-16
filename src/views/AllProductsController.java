package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import models.Product;
import models.ProductPrice;
import models.ProductStock;

public class AllProductsController implements Initializable{


	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtStockLessThan;

	@FXML Button btnSearch;


	@FXML TableView<ProductStock> tableViewProductsStocks;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Double> columnTVA;
	@FXML TableColumn<ProductStock, Double> columnPrixDeVentHT;
	@FXML TableColumn<ProductStock, Double> columnPrixDeVentTTC;
	@FXML TableColumn<ProductStock, Integer> columnStock;


	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnStock.setCellValueFactory(new PropertyValueFactory<>("Qnt"));
		columnTVA.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {

			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getTva()).asObject();
			}
		});
		columnPrixDeVentHT.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {

			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getPrixVenteHT()).asObject();
			}
		});
		columnPrixDeVentTTC.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductStock,Double>, ObservableValue<Double>>() {

			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductStock, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getPrixVenteTTC()).asObject();
			}
		});

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		
		
		btnSearch.setOnAction(actionEventHandler);
		txtCode.setOnAction(actionEventHandler);
		txtName.setOnAction(actionEventHandler);
		txtStockLessThan.setOnAction(actionEventHandler);

		tableViewProductsStocks.setOnMousePressed(new TableViewMousePressedHandler());
		
		tableViewProductsStocks.setItems(productsStockData);
		
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtCode.requestFocus();
			}
		});
	}


	public void refrechTableViewData(){

		productsStockData.clear();


		Double stockMax = null;

		try {
			stockMax = Double.parseDouble(txtStockLessThan.getText());
		}catch (NumberFormatException e){}


		ArrayList<String> productsCodes;

		try {
			productsCodes = AppDataBaseManager.shared.getAllProductsCodes(txtCode.getText(), txtName.getText(), stockMax);

			for (int i=0;i<productsCodes.size();i++){

				Product product = AppDataBaseManager.shared.getProductByCode(productsCodes.get(i));

				ProductPrice productPrice = AppDataBaseManager.shared.getProductPrice(productsCodes.get(i));

				product.setPrice(productPrice);

				Double stock = AppDataBaseManager.shared.getProductsStock(productsCodes.get(i));

				productsStockData.add(new ProductStock(product, stock));

			}



		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch || event.getSource() == txtCode 
					|| event.getSource() == txtName || event.getSource() == txtStockLessThan) {
				refrechTableViewData();
			}
		}
	}
	
	
	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {
			
			if (event.isPrimaryButtonDown() && event.getClickCount() == 2 
					&& tableViewProductsStocks.getSelectionModel().getSelectedItem() != null)  {
				
				Product product = tableViewProductsStocks.getSelectionModel().getSelectedItem();
				
				RootViewController.selfRef.presentProductDetailsView(product);

			}
			
		}
		
	}
}
