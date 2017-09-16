package views;

import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import managers.AppDataBaseManager;
import managers.StringsManager;
import models.Bill;
import models.Client;
import models.Depot;
import models.Payment;
import models.Product;
import models.ProductBill;
import models.ProductPrice;
import models.ProductStock;
import models.ui.AddPaymentDelegate;
import models.ui.AlertError;
import models.ui.ClientSearchPickedClientDelegate;
import models.ui.StringConverterLocalDate;
import models.ui.ProductSearchPickedProductDelegate;
import models.ui.StringConverterDouble;
import models.ui.StringConverterInteger;
import models.ui.StringConverterTimestamp;
import models.ui.TimesSpinerConfigurator;

public class AddEditBillController implements Initializable, ClientSearchPickedClientDelegate, 
ProductSearchPickedProductDelegate, AddPaymentDelegate{


	@FXML Button btnAdd;
	@FXML Button btnAddProduct;
	@FXML Button btnAddPayment;
	@FXML Button btnCancel;

	@FXML Label lblBillNumber;
	@FXML Label lblMoneyTotal;
	@FXML Label lblMoneyPayed;
	@FXML Label lblMoneyToPay;

	@FXML TextField txtClientCode;
	@FXML TextField txtClientName;
	@FXML TextField txtProductCode;
	@FXML TextField txtProductName;
	@FXML TextField txtProductBuyedPrice;
	@FXML TextField txtProductSelledPrice;
	@FXML TextField txtProductQnt;
	@FXML TextField txtTotal;
	@FXML TextField txtDiscount;
	@FXML TextField txtFinalToPay;


	@FXML TableView<ProductBill> tableViewProducts;
	@FXML TableColumn<ProductBill, String> columnCode;
	@FXML TableColumn<ProductBill, String> columnName;
	@FXML TableColumn<ProductBill, Double> columnBuyedPrice;
	@FXML TableColumn<ProductBill, Double> columnSelledPrice;
	@FXML TableColumn<ProductBill, Integer> columnQNT;
	@FXML TableColumn<ProductBill, Double> columnTotal;

	@FXML TableView<Payment> tableViewPayments;
	@FXML TableColumn<Payment, Timestamp> columnPaymentDate;
	@FXML TableColumn<Payment, String> columnPaymentType;
	@FXML TableColumn<Payment, Double> columnPaymentAmount;

	@FXML DatePicker date;

	@FXML ComboBox<Depot> comboBoxDepot;

	@FXML Spinner<Integer> spinerHoures;
	@FXML Spinner<Integer> spinerMinutes;

	@FXML FlowPane paneAddProduct;
	@FXML FlowPane paneButtons;
	@FXML AnchorPane panePayement;

	private boolean isAddingNewBill = true;
	private StringsManager stringsManager = new StringsManager();

	private ObservableList<Depot> depotsList = FXCollections.observableArrayList();

	private ObservableList<ProductBill> productsData = FXCollections.observableArrayList();

	private ObservableList<Payment> paymentsData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		configureTimeView();

		comboBoxDepot.setItems(depotsList);

		columnCode.setCellValueFactory(new PropertyValueFactory<>("Code"));
		columnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		columnBuyedPrice.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ProductBill,Double>, ObservableValue<Double>>() {
			@Override
			public ObservableValue<Double> call(CellDataFeatures<ProductBill, Double> param) {
				return new SimpleDoubleProperty(param.getValue().getPrice().getPrixAchatTTC()).asObject();
			}
		});
		columnSelledPrice.setCellValueFactory(new PropertyValueFactory<>("priceSelled"));
		columnQNT.setCellValueFactory(new PropertyValueFactory<>("Qnt"));
		columnTotal.setCellValueFactory(new PropertyValueFactory<>("TotalPrice"));

		columnQNT.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterInteger()));
		columnQNT.setOnEditCommit(new CellIntegerEditEventHandler());
		columnSelledPrice.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterDouble()));
		columnSelledPrice.setOnEditCommit(new CellDoubleEditEventHandler());

		tableViewProducts.setOnMousePressed(new MouseEventHandler());

		tableViewProducts.setRowFactory(new TableViewRowFactory());
		
		columnPaymentDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		columnPaymentType.setCellValueFactory(new PropertyValueFactory<>("type"));
		columnPaymentAmount.setCellValueFactory(new PropertyValueFactory<>("ammount"));
		
		columnPaymentDate.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverterTimestamp()));

		ActionEventHandler actionEventHandler = new ActionEventHandler();
		btnAdd.setOnAction(actionEventHandler);
		btnCancel.setOnAction(actionEventHandler);
		txtClientCode.setOnAction(actionEventHandler);
		txtProductCode.setOnAction(actionEventHandler);
		txtProductSelledPrice.setOnAction(actionEventHandler);
		txtProductQnt.setOnAction(actionEventHandler);
		btnAddProduct.setOnAction(actionEventHandler);
		btnAddPayment.setOnAction(actionEventHandler);


		TextFieldChangeListener textFieldChangeListener = new TextFieldChangeListener();
		txtClientCode.textProperty().addListener(textFieldChangeListener);
		txtProductCode.textProperty().addListener(textFieldChangeListener);
		txtProductQnt.textProperty().addListener(textFieldChangeListener);
		txtDiscount.textProperty().addListener(textFieldChangeListener);

		KeyEventHandler keyEventHandler = new KeyEventHandler();
		txtClientCode.setOnKeyPressed(keyEventHandler);
		txtProductCode.setOnKeyPressed(keyEventHandler);

		txtDiscount.setText("0");

		try {
			depotsList.addAll(AppDataBaseManager.shared.getAllDepots());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		

		tableViewProducts.setItems(productsData);
		tableViewPayments.setItems(paymentsData);

		configureView();
	}

	private void configureView(){
		if (isAddingNewBill) {
			lblBillNumber.setVisible(false);
			panePayement.setVisible(false);

		}else{
			lblBillNumber.setVisible(true);
			panePayement.setVisible(true);
			paneAddProduct.setVisible(false);
			paneButtons.setVisible(false);

			tableViewProducts.setRowFactory(null);
		}
	}

	private void configureTimeView(){
		TimesSpinerConfigurator timesSpinerConfigurator = new TimesSpinerConfigurator(spinerHoures, spinerMinutes);
		timesSpinerConfigurator.configure();

		date.setConverter(new StringConverterLocalDate());

		date.setValue(LocalDate.now());
	}

	public void showBillDetails(String billCode){
		isAddingNewBill = false;
		configureView();

		try {
			Bill bill = AppDataBaseManager.shared.getBillByCode(billCode);

			lblBillNumber.setText(billCode);

			date.setValue(bill.getDate().toLocalDateTime().toLocalDate());
			spinerHoures.getValueFactory().setValue(bill.getDate().toLocalDateTime().getHour());
			spinerMinutes.getValueFactory().setValue(bill.getDate().toLocalDateTime().getMinute());

			txtClientCode.setText(bill.getClientCode());
			Client client = AppDataBaseManager.shared.getClientByCode(txtClientCode.getText());
			setUpClientDetails(client);

			Depot depot = AppDataBaseManager.shared.getDepotByCode(bill.getDepotCode());
			comboBoxDepot.setValue(depot);

			productsData.clear();
			productsData.addAll(bill.getProducts());

			refreshBillTotal();

			// payementDetails

			ArrayList<Payment> allPayments = AppDataBaseManager.shared.getAllPaymentForBillByBillCode(billCode);
			paymentsData.addAll(allPayments);
			refreshPaymentsDetails();
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0012", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}

	private void setUpClientDetails(Client client){
		txtClientCode.setText(client.getCode());
		txtClientName.setText(client.getName()+" "+client.getLastName());
		comboBoxDepot.requestFocus();
	}
	
	private void refreshPaymentsDetails(){
		double total = calculateTotalWithDiscount();
		double totalMoneyPayed = 0;
		
		for (int i=0; i<paymentsData.size(); i++) {
			totalMoneyPayed += paymentsData.get(i).getAmmount();
		}
		
		lblMoneyTotal.setText(Double.toString(total));
		lblMoneyPayed.setText(Double.toString(totalMoneyPayed));
		lblMoneyToPay.setText(Double.toString(total-totalMoneyPayed));
		
		if (total-totalMoneyPayed <= 0) {
			btnAddPayment.setDisable(true);
		}
	}

	private void refreshBillTotal(){
		txtTotal.setText(Double.toString(calculateTotal()));
		txtFinalToPay.setText(Double.toString(calculateTotalWithDiscount()));
	}

	private double calculateTotal(){
		double total = 0;

		for (int i=0; i<productsData.size(); i++) {
			total += productsData.get(i).getTotalPrice();
		}
		
		return total;
	}

	private double calculateTotalWithDiscount(){
		double total = calculateTotal();
		double discount = Double.parseDouble(txtDiscount.getText());
		
		return total-total*(discount/100);
	}

	private void createTheBill() {

		if (txtClientCode.getText().equals("") || txtClientName.getText().equals("")) {
			AlertError alert = new AlertError("Entrer un client", null, "Vous devez entrer un client !");
			alert.showAndWait();
			txtClientCode.requestFocus();
			return;
		}


		if (comboBoxDepot.getValue() == null) {
			AlertError alert = new AlertError("Sélectionner un dépôt", null, "Vous devez sélectionner un dépôt !");
			alert.showAndWait();
			comboBoxDepot.requestFocus();
			return;
		}

		if (productsData.size() == 0) {
			AlertError alert = new AlertError("Entrer au moins une article", null, "Vous devez entrer au moins une article !");
			alert.showAndWait();
			txtProductCode.requestFocus();
			return;
		}


		LocalDateTime localDateTime = 
				date.getValue().atTime(spinerHoures.getValue(), spinerMinutes.getValue());
		Timestamp timestamp = Timestamp.valueOf(localDateTime);

		ArrayList<ProductBill> billsProducts = new ArrayList<>();

		for (int i=0; i<productsData.size(); i++) {
			billsProducts.add(productsData.get(i));
		}

		double discount = Double.parseDouble(txtDiscount.getText());

		Bill bill = new Bill(null, txtClientCode.getText(), comboBoxDepot.getValue().getCode(), timestamp, 
				discount, billsProducts);

		try {
			String billCode = AppDataBaseManager.shared.createNewBill(bill);
			showBillDetails(billCode);
		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0011", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}

	}

	private void closeStage(){
		((Stage)tableViewProducts.getScene().getWindow()).close();
	}

	private class ActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {

			if (event.getSource() == btnAdd) {
				createTheBill();
			}else if (event.getSource() == btnCancel) {
				closeStage();
			}else if(event.getSource() == btnAddPayment){
				double totalMoneyPayed = 0;
				
				for (int i=0; i<paymentsData.size(); i++) {
					totalMoneyPayed += paymentsData.get(i).getAmmount();
				}
				
				RootViewController.selfRef.presentAddPaymentView(lblBillNumber.getText(), 
						calculateTotalWithDiscount(), totalMoneyPayed, AddEditBillController.this);
			}else if (event.getSource() == txtClientCode) {
				try {
					Client client = AppDataBaseManager.shared.getClientByCode(txtClientCode.getText());

					if (client != null) {
						setUpClientDetails(client);
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (event.getSource() == txtProductCode){
				try {

					if (AppDataBaseManager.shared.isProductCodeExist(txtProductCode.getText())) {
						setupAddProductViews(txtProductCode.getText());
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else if (event.getSource() == txtProductSelledPrice) {
				txtProductQnt.requestFocus();
			}else if (event.getSource() == txtProductQnt || event.getSource() == btnAddProduct) {

				try {
					Product product = AppDataBaseManager.shared.getProductByCode(txtProductCode.getText());

					if (product != null) {

						int qntToAdd = Integer.parseInt(txtProductQnt.getText());

						int i = 0;

						while (i<productsData.size() 
								&& !productsData.get(i).getCode().equals(txtProductCode.getText())) {
							i++;
						}

						if (i<productsData.size()){
							productsData.get(i).setQnt(productsData.get(i).getQnt()+qntToAdd);
							productsData.get(i).setPriceSelled(Double.parseDouble(txtProductSelledPrice.getText()));
							tableViewProducts.refresh();
						}else{
							LocalDateTime localDateTime = 
									date.getValue().atTime(spinerHoures.getValue(), spinerMinutes.getValue());
							Timestamp timestamp = Timestamp.valueOf(localDateTime);

							ProductPrice price = AppDataBaseManager.shared.getProductPrice(txtProductCode.getText(), timestamp);

							product.setPrice(price);

							ProductBill productBill = new ProductBill(product, 
									Double.parseDouble(txtProductSelledPrice.getText()), qntToAdd);
							productsData.add(productBill);
						}

						txtProductCode.setText("");
						txtProductName.setText("");
						txtProductBuyedPrice.setText("");
						txtProductSelledPrice.setText("");
						txtProductQnt.setText("");
						txtProductCode.requestFocus();

						refreshBillTotal();

					}

				} catch (SQLException e) {
					AlertError alert = new AlertError("ERROR ERR0010", "SQL error code : "+e.getErrorCode(),e.getMessage());
					alert.showAndWait();
				} catch (NumberFormatException e){

				}

			}

		}
	}



	private class TextFieldChangeListener implements ChangeListener<String> {

		@Override
		public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

			TextField txt = (TextField) ((StringProperty)observable).getBean();

			if (txt == txtClientCode || txt == txtProductCode) {
				String ch = txt.getText().replaceAll(" ", "");
				ch = stringsManager.getOnlyLettersAndNumbers(ch);
				txt.setText(ch.toUpperCase());

				if (txt == txtClientCode) {
					txtClientName.setText("");
				}else if (txt == txtProductCode) {
					txtProductQnt.setEditable(false);
					txtProductSelledPrice.setEditable(false);
					txtProductName.setText("");
					txtProductBuyedPrice.setText("");
					txtProductSelledPrice.setText("");
					txtProductQnt.setText("");
				} 
			}else if (txt == txtProductQnt) {
				txt.setText(stringsManager.getOnlyNumbers(txt.getText()));
			}else if (txt == txtDiscount) {
				String onlyDouble = stringsManager.getDoubleFormat(newValue);

				if (onlyDouble.equals("")) {
					onlyDouble = "0";
				}

				if (Double.parseDouble(onlyDouble) > 100 || Double.parseDouble(onlyDouble) < 0) {
					txt.setText(oldValue);
				}else{
					txt.setText(onlyDouble);
				}

				refreshBillTotal();
			}

		}

	}


	private class KeyEventHandler implements EventHandler<KeyEvent>{

		@Override
		public void handle(KeyEvent event) {
			if (event.getSource() == txtClientCode && event.getCode() == KeyCode.SPACE) {
				RootViewController.selfRef.presentClientSearchView(txtClientCode.getText(), AddEditBillController.this);
			}else if (event.getSource() == txtProductCode && event.getCode() == KeyCode.SPACE) {
				RootViewController.selfRef.presentProductSearchView(comboBoxDepot.getSelectionModel().getSelectedItem()
						,AddEditBillController.this, txtProductCode.getText(), "");
			}

		}

	}

	private class CellIntegerEditEventHandler implements EventHandler<CellEditEvent<ProductBill, Integer>>{

		@Override
		public void handle(CellEditEvent<ProductBill, Integer> event) {
			if (event.getNewValue() == 0) {
				productsData.remove(event.getTablePosition().getRow());
			}else{
				ProductBill productBill = productsData.get(event.getTablePosition().getRow());
				productBill.setQnt(event.getNewValue());
				tableViewProducts.refresh();
			}
		}

	}

	private class CellDoubleEditEventHandler implements EventHandler<CellEditEvent<ProductBill, Double>>{

		@Override
		public void handle(CellEditEvent<ProductBill, Double> event) {
			ProductBill productBill = productsData.get(event.getTablePosition().getRow());
			productBill.setPriceSelled(event.getNewValue());
			tableViewProducts.refresh();
		}

	}

	private class MouseEventHandler implements EventHandler<MouseEvent>{

		@Override
		public void handle(MouseEvent event) {

			if (event.isSecondaryButtonDown()  
					&& tableViewProducts.getSelectionModel().getSelectedItem() != null)  {

				Product product = tableViewProducts.getSelectionModel().getSelectedItem();
				RootViewController.selfRef.presentProductDetailsView(product);

			}

		}

	}


	private class TableViewRowFactory implements Callback<TableView<ProductBill>, TableRow<ProductBill>> {

		@Override
		public TableRow<ProductBill> call(TableView<ProductBill> param) {
			return new TableRow<ProductBill>() {
				@Override
				protected void updateItem(ProductBill item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || comboBoxDepot.getValue() == null) {
						setStyle("");
					}else {
						try {
							int availableStock = AppDataBaseManager.shared.getProductsStock(item.getCode(), 
									comboBoxDepot.getValue().getCode());

							if (availableStock >= item.getQnt()) {
								setStyle("");
							}else{
								setStyle("-fx-background-color: red;");
							}

						} catch (SQLException e) {
							setStyle("");
							AlertError alert = new AlertError("ERROR ERR0008", "SQL error code : "+e.getErrorCode(),e.getMessage());
							alert.showAndWait();
						}

					}


				}
			};
		}

	}


	@Override
	public void clientSearchPickedClient(Client client) {
		setUpClientDetails(client);
	}

	@Override
	public void productSearchPickedProduct(Product product) {
		setupAddProductViews(product.getCode());
	}

	private void setupAddProductViews(String productCode){
		try {
			LocalDateTime localDateTime = 
					date.getValue().atTime(spinerHoures.getValue(), spinerMinutes.getValue());
			Timestamp timestamp = Timestamp.valueOf(localDateTime);

			Product product = AppDataBaseManager.shared.getProductByCode(productCode);
			ProductPrice price = AppDataBaseManager.shared.getProductPrice(productCode, timestamp);

			txtProductCode.setText(product.getCode());
			txtProductName.setText(product.getName());
			txtProductBuyedPrice.setText(Double.toString(price.getPrixAchatTTC()));
			txtProductSelledPrice.setText(Double.toString(price.getPrixVenteTTC()));

			txtProductSelledPrice.setEditable(true);
			txtProductQnt.setEditable(true);
			txtProductSelledPrice.requestFocus();

		} catch (SQLException e) {
			AlertError alert = new AlertError("ERROR ERR0009", "SQL error code : "+e.getErrorCode(),e.getMessage());
			alert.showAndWait();
		}
	}

	@Override
	public void didAddNewPayment(Payment payment) {
		paymentsData.add(payment);
		refreshPaymentsDetails();
	}


}