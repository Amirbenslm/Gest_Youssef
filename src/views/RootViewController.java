package views;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.ViewController;

public class RootViewController implements Initializable, EventHandler<ActionEvent>{

	private ArrayList<ViewController> viewsTab = new ArrayList<>();

	@FXML Button btnDashboard;

	@FXML Button btnClients;
	@FXML Button btnAddClient;
	@FXML Button btnFactures;
	@FXML Button btnAddFacture;

	@FXML Button btnArticles;
	@FXML Button btnAddArticle;
	@FXML Button btnEntryStock;
	@FXML Button btnTransferStock;
	@FXML Button btnAddDepot;
	@FXML Button btnDepot;
	@FXML Button btnInventaires;
	@FXML Button btnAddInventaire;


	@FXML AnchorPane containerAP;


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnDashboard.setOnAction(this);

		btnClients.setOnAction(this);
		btnAddClient.setOnAction(this);
		btnFactures.setOnAction(this);
		btnAddFacture.setOnAction(this);

		btnArticles.setOnAction(this);
		btnAddArticle.setOnAction(this);
		btnEntryStock.setOnAction(this);
		btnTransferStock.setOnAction(this);
		btnAddDepot.setOnAction(this);
		btnDepot.setOnAction(this);
		btnInventaires.setOnAction(this);
		btnAddInventaire.setOnAction(this);

	}


	private void showPaneInContainer(Pane p) {
		
		AnchorPane.setTopAnchor(p, 0.0);
		AnchorPane.setBottomAnchor(p, 0.0);
		AnchorPane.setLeftAnchor(p, 0.0);
		AnchorPane.setRightAnchor(p, 0.0);
		
		containerAP.getChildren().clear();
		containerAP.getChildren().add(p);
		System.gc();
		
	}
	
	private void showPaneInAlertMode(String title,Pane p,double width,double height) {
		
		Scene scene = new Scene(p, width, height);
		Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setTitle(title);

        stage.setScene(scene);
        stage.show();
	}


	@Override
	public void handle(ActionEvent event) {

		if (event.getSource() == btnDashboard) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnClients) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnAddClient) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnFactures) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnAddFacture) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnArticles) {
			showAllProductView();
		}

		else if (event.getSource() == btnAddArticle) {
			showAddNewProductView();
		}

		else if (event.getSource() == btnEntryStock) {
			showStockInputView();
		}

		else if (event.getSource() == btnTransferStock) {
			showStockTransferView();
		}

		else if (event.getSource() == btnAddDepot) {
			showAddNewDepotView();
		}

		else if (event.getSource() == btnDepot) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnInventaires) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnAddInventaire) {
			System.out.println("mmm");

		}

	}

	
	private void showAllProductView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AllProducts.fxml"));
			Pane p = loader.load();
			showPaneInContainer(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void showAddNewProductView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddNewProduct.fxml"));
			Pane p = loader.load();
			
			showPaneInAlertMode("BLA", p, 850, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void showAddNewDepotView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddEditDepotDetails.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("BLABLA", p, 500, 300);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	private void showStockInputView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockInput.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("BLABLA", p, 850, 400);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	private void showStockTransferView() {

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/StockTransfer.fxml"));
			Pane p = loader.load();

			showPaneInAlertMode("BLABLA", p, 830, 730);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	


}
