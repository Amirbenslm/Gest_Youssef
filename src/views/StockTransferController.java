package views;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Depot;
import models.Product;
import models.ProductStock;
import models.ui.ProductSearchPickedProductDelegate;
import models.ui.StringConverterInteger;

public class StockTransferController implements Initializable, ProductSearchPickedProductDelegate{


	@FXML Button btnTransfere;
	@FXML Button btnCancel;

	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtQnt;

	@FXML DatePicker date;

	@FXML ComboBox<Depot> comboBoxFromDepot;
	@FXML ComboBox<Depot> comboBoxToDepot;

	@FXML TableView<ProductStock> tableViewProductsStocksTransfert;
	@FXML TableColumn<ProductStock, String> columnCode;
	@FXML TableColumn<ProductStock, String> columnName;
	@FXML TableColumn<ProductStock, Integer> columnQNT;

	private StringsManager stringsManager = new StringsManager();

	private ArrayList<Depot> allDepots;
	private ObservableList<Depot> toDepotsList = FXCollections.observableArrayList();
	private ObservableList<Depot> fromDepotsList = FXCollections.observableArrayList();

	private ObservableList<ProductStock> productsStockData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		comboBoxFromDepot.setItems(fromDepotsList);
		comboBoxToDepot.setItems(toDepotsList);
		

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnQNT.setCellValueFactory(new PropertyValueFactory<>("Qnt"));


		columnQNT.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterInteger()));
		columnQNT.setOnEditCommit(new TableColumnChangeEventHandler());

		tableViewProductsStocksTransfert.setOnMousePressed(new TableViewMousePressedHandler());

		tableViewProductsStocksTransfert.setItems(productsStockData);


		ActionEventHandler actionEventHandler = new ActionEventHandler();
		txtCode.setOnAction(actionEventHandler);
		txtQnt.setOnAction(actionEventHandler);

		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();

		txtCode.textProperty().addListener(textFieldChangeListener);
		txtQnt.textProperty().addListener(textFieldChangeListener);

		txtCode.setOnKeyPressed(new KeyEventHandler());

		try {
			allDepots = AppDataBaseManager.shared.getAllDepots();
			fromDepotsList.addAll(allDepots);
			toDepotsList.addAll(allDepots);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			closeStage();
		}
	}

	private void closeStage(){
		//((Stage) containerFP.getScene().getWindow()).close();
	}
	
	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {

			if (event.getSource() == txtCode) {

				try {
					Product product = AppDataBaseManager.shared.getProductByCode(txtCode.getText());

					if (product != null) {
						txtName.setText(product.getName());
						txtQnt.setEditable(true);
						txtQnt.requestFocus();
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}else if (event.getSource() == txtQnt) {
				try {
					Product product = AppDataBaseManager.shared.getProductByCode(txtCode.getText());

					if (product != null) {

						int qntToAdd = Integer.parseInt(txtQnt.getText());

						int i = 0;

						while (i<productsStockData.size() 
								&& !productsStockData.get(i).getCode().equals(txtCode.getText())) {
							i++;
						}

						if (i<productsStockData.size()){
							productsStockData.get(i).setQnt(productsStockData.get(i).getQnt()+qntToAdd);
							tableViewProductsStocksTransfert.refresh();
						}else{
							productsStockData.add(new ProductStock(product, qntToAdd));
						}

						txtCode.setText("");
						txtName.setText("");
						txtQnt.setText("");
						txtCode.requestFocus();
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NumberFormatException e){

				}
			}

		}
	}


	private class KeyEventHandler implements EventHandler<KeyEvent>{

		@Override
		public void handle(KeyEvent event) {
			if (event.getSource() == txtCode && event.getCode() == KeyCode.SPACE) {
				
				RootViewController.selfRef.presentProductSearchView(comboBoxFromDepot.getSelectionModel().getSelectedItem()
						,StockTransferController.this, txtCode.getText(), "");
			}
		}

	}


	private class TableViewMousePressedHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isSecondaryButtonDown()  
					&& tableViewProductsStocksTransfert.getSelectionModel().getSelectedItem() != null)  {

				Product product = tableViewProductsStocksTransfert.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentProductsEditsView(product);

			}

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
				txtQnt.setEditable(false);
				txtName.setText("");
			} else if (txt == txtQnt) {
				txt.setText(stringsManager.getOnlyNumbers(txt.getText()));
			}

		}

	}

	private class TableColumnChangeEventHandler implements EventHandler<CellEditEvent<ProductStock, Integer>> {

		@Override
		public void handle(CellEditEvent<ProductStock, Integer> event) {
			if (event.getNewValue() == 0) {
				productsStockData.remove(event.getTablePosition().getRow());
			}else{
				ProductStock productStock = productsStockData.get(event.getTablePosition().getRow());
				productStock.setQnt(event.getNewValue());
			}
		}

	}

	@Override
	public void productSearchPickedProduct(Product product) {
		txtCode.setText(product.getCode());
		txtName.setText(product.getName());
		txtQnt.setEditable(true);
		txtQnt.requestFocus();
	}
}
