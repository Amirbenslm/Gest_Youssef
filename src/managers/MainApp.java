package managers;

import java.io.IOException;

import exceptions.DataBaseDriverLoadFailedException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;


	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Gestion Skander");

		loadRootView();
	}


	private void loadRootView(){

		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("/views/RootView.fxml"));
			Scene scence = new Scene(loader.load(),1300,750);
			primaryStage.setScene(scence);
			primaryStage.setMinWidth(1000);
			primaryStage.setMinHeight(600);
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		try {
			AppDataBaseManager.shared.prepare();
			
			launch(args);
		} catch (DataBaseDriverLoadFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
