package views;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import models.ui.TimesSpinerConfigurator;

public class StockInputController implements Initializable {

	
	@FXML Spinner<Integer> spinerHoures;
	@FXML Spinner<Integer> spinerMinutes;
	@FXML DatePicker date;
	@FXML TextField txtCode;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		TimesSpinerConfigurator timesSpinerConfigurator = new TimesSpinerConfigurator(spinerHoures, spinerMinutes);
		timesSpinerConfigurator.configure();
		
	}
	


}
