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
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
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


	@FXML FlowPane containerFP;


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
			System.out.println("hhh");
		}

		else if (event.getSource() == btnAddArticle) {
			showAddNewProductView();
		}

		else if (event.getSource() == btnEntryStock) {
			System.out.println("hhh");
		}

		else if (event.getSource() == btnTransferStock) {
			System.out.println("hhh");
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

	private void showAddNewProductView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/AddNewProduct.fxml"));
			Pane p = loader.load();
			//containerBP.setCenter(p);
			containerFP.getChildren().clear();
			containerFP.getChildren().add(p);
			System.gc();
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
			//containerBP.setCenter(p);
			containerFP.getChildren().clear();
			containerFP.getChildren().add(p);
			System.gc();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
