package managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import exceptions.DataBaseDriverLoadFailedException;
import models.Depot;
import models.Product;

public class AppDataBaseManager {

	static public AppDataBaseManager shared = new AppDataBaseManager();

	private Connection con;
	private Statement st;

	public void prepare() throws DataBaseDriverLoadFailedException{
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:file:Database/AppDB;shutdown=true;hsqldb.write_delay=false", "Xr@o/o!t#_?", "Y1T#6uNa?+O$c42@");
			st = con.createStatement();
			createDatabaseTablesIfNeedeed();
		} catch (ClassNotFoundException e) {
			throw new DataBaseDriverLoadFailedException();
		} catch (SQLException e) {
			System.out.println("ha?");
		}
	}

	/*
	 public void backupDatabase(){
		try {
			st.execute("BACKUP DATABASE TO 'Backups/' NOT BLOCKING");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 */

	private void createDatabaseTablesIfNeedeed(){

		try {
			if (getDataBasetablesCount() == 0) {

				st.executeUpdate("CREATE TABLE Product ( "
						+ "code VARCHAR(20) NOT NULL, "
						+ "name VARCHAR(45) NOT NULL, "
						+ "PRIMARY KEY (code), "
						+ "UNIQUE (name) );");

				
				
				
				st.executeUpdate("CREATE TABLE Price ( "
						+ "code_product VARCHAR(20) NOT NULL, "
						+ "date_start DATETIME NOT NULL, "
						+ "date_end DATETIME NOT NULL, "
						+ "PrixAchatTTC DOUBLE NOT NULL, "
						+ "TVA DOUBLE NOT NULL, "
						+ "PrixVenteHT DOUBLE NOT NULL, "
						+ "PrixVenteTTC DOUBLE NOT NULL, "
						+ "CHECK( PrixAchatTTC >= 0), "
						+ "CHECK( TVA >= 0), "
						+ "CHECK( PrixVenteHT >= 0), "
						+ "CHECK( PrixVenteTTC >= 0), "
						+ "PRIMARY KEY (code_product, date_start), "
						+ "CONSTRAINT fkProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE );");
				
				
				
				
				st.executeUpdate("CREATE TABLE Depot ("
						+ "CODE INTEGER NOT NULL IDENTITY, "
						+ "NAME VARCHAR(40) NOT NULL, "
						+ "COMMENTS VARCHAR(600), "
						+ "PRIMARY KEY (CODE), "
						+ "UNIQUE (NAME));");
				
				
				
				st.executeUpdate("CREATE TABLE Stock ( "
						+ "code_depot INTEGER NOT NULL, "
						+ "code_product VARCHAR(20) NOT NULL, "
						+ "date DATETIME NOT NULL, "
						+ "QNT INTEGER NOT NULL, "
						+ "fromDepot INTEGER NOT NULL, "
						+ "PRIMARY KEY (code_depot, date, code_product), "
						+ "CONSTRAINT fkStockProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkStockDepot FOREIGN KEY (code_depot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");
				
				
				st.executeUpdate("INSERT INTO Depot (NAME) VALUES ('ADMIN');");
			}
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private int getDataBasetablesCount() throws SQLException{

		ResultSet rs = st.executeQuery("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_TABLES where TABLE_TYPE = 'TABLE';");
		int nb = 0;
		while (rs.next()){
			nb++;
		}

		return nb;
	}

	
	//Products
	
	public boolean isProductCodeExist(String code){
		return false;
	}

	public void addNewProduct(Product product){
		
	}
	
	//Depots
	
	public boolean isDepotNameExist(String name) throws SQLException{
		
		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM DEPOT where NAME = ? ;");
		pst.setString(1, name);
		return pst.executeQuery().next();
	}

	public void addNewDepot(String name, String comments) throws SQLException{
		
		PreparedStatement pst = con.prepareStatement("INSERT INTO DEPOT "
				+ "(NAME, COMMENTS) "
				+ "VALUES (?, ?);");
		pst.setString(1, name);
		pst.setString(2, comments);
		
		pst.executeUpdate();
	}
	
	
	public ArrayList<Depot> getAllDepots() throws SQLException{
		
		ArrayList<Depot> allDepots = new ArrayList<>();
		
		ResultSet rs = st.executeQuery("SELECT CODE, NAME, COMMENTS FROM DEPOT WHERE (CODE <> 0);");
		
		while (rs.next()){
			allDepots.add(new Depot(rs.getInt(1), rs.getString(2), rs.getString(3)));
		}
		
		return allDepots;
	}
}
