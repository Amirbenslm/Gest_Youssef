package models;

public class Client {

	private String code;
	private String name;
	private String lastName;
	private String address;
	
	
	public Client(String code, String name, String lastName, String address) {
		super();
		this.code = code;
		this.name = name;
		this.lastName = lastName;
		this.address = address;
	}


	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}


	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
}
