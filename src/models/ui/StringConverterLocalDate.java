package models.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.util.StringConverter;

public class StringConverterLocalDate extends StringConverter<LocalDate> {

	private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Override
	public String toString(LocalDate localDate)
	{
		return dateTimeFormatter.format(localDate);
	}

	@Override
	public LocalDate fromString(String dateString)
	{
		return LocalDate.parse(dateString,dateTimeFormatter);
	}

}
