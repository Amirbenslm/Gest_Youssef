package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

public class AddEditDepotDetailsController implements Initializable {

	@FXML Button btnSave;
	@FXML Button btnCancel;
	
	@FXML FlowPane containerFP;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
		loadDepoDetailsView();
	}

	private void loadDepoDetailsView(){
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/DepotDetails.fxml"));
			Pane p = loader.load();
			containerFP.getChildren().add(p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
