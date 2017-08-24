package managers;

public class StringsManager {

	
	public String getOnlyNumbers(String text){
		String ch = "";
		
		for (int i = 0;i<text.length();i++){
			if (Character.isDigit(text.charAt(i))){
				ch += text.charAt(i);
			}
		}
		
		return ch;
	}
}
