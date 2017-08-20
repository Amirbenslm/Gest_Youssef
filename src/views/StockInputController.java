package views;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class StockInputController implements Initializable {

	@FXML Button btnAdd;
	@FXML Button btnCancel;
	@FXML Button btnDetails;
	
	@FXML TextField txtCode;
	@FXML TextField txtName;
	@FXML TextField txtQnt;
	
	@FXML DatePicker date;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
