package views;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import managers.AppDataBaseManager;
import models.ui.AlertError;
import models.ui.AlertWarning;

public class AllProductsController implements Initializable{

	
	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtStockLessThan;
	
	@FXML Button btnSearch;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		btnSearch.setOnAction(new ButtonActionEventHandler());
		
	}
	
	
	
	
	public void refrechTableViewData(){
		
		Integer stockMax = null;
		
		try {
			stockMax = Integer.parseInt(txtStockLessThan.getText());
		}catch (NumberFormatException e){}
		
		
		ArrayList<String> productsCodes;
		
		try {
			productsCodes = AppDataBaseManager.shared.getAllProductsCodes(txtCode.getText(), txtName.getText(), stockMax);
			
			for (int i=0;i<productsCodes.size();i++){
				
				int x;
				
				
			}
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	private class ButtonActionEventHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			if (event.getSource() == btnSearch) {
				refrechTableViewData();
			}
		}
	}
}
