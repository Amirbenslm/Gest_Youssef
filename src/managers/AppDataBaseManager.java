package managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import exceptions.DataBaseDriverLoadFailedException;
import models.Depot;
import models.Product;
import models.ProductPrice;

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
						+ "code VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "name VARCHAR_IGNORECASE(45) NOT NULL, "
						+ "PRIMARY KEY (code), "
						+ "UNIQUE (name) );");




				st.executeUpdate("CREATE TABLE Price ( "
						+ "code_product VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "date_start DATETIME NOT NULL, "
						+ "date_end DATETIME NULL, "
						+ "PrixAchatTTC DOUBLE NOT NULL, "
						+ "TVA DOUBLE NOT NULL, "
						+ "PrixVenteHT DOUBLE NOT NULL, "
						+ "PrixVenteTTC DOUBLE NOT NULL, "
						+ "CHECK( date_start < date_end), "
						+ "CHECK( PrixAchatTTC >= 0), "
						+ "CHECK( TVA >= 0), "
						+ "CHECK( PrixVenteHT >= 0), "
						+ "CHECK( PrixVenteTTC >= 0), "
						+ "PRIMARY KEY (code_product, date_start), "
						+ "CONSTRAINT fkProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE );");




				st.executeUpdate("CREATE TABLE Depot ("
						+ "CODE INTEGER NOT NULL IDENTITY, "
						+ "NAME VARCHAR_IGNORECASE(40) NOT NULL, "
						+ "COMMENTS VARCHAR_IGNORECASE(600), "
						+ "PRIMARY KEY (CODE), "
						+ "UNIQUE (NAME));");


				st.executeUpdate("CREATE TABLE Stock ( "
						+ "code_depot INTEGER NOT NULL, "
						+ "code_product VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "QNT INTEGER NOT NULL, "
						+ "PRIMARY KEY (code_depot, code_product), "
						+ "CONSTRAINT fkStockProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkStockDepot FOREIGN KEY (code_depot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("CREATE TABLE TransferStock ( "
						+ "code_depot INTEGER NOT NULL, "
						+ "code_product VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "date DATETIME NOT NULL, "
						+ "QNT INTEGER NOT NULL, "
						+ "fromDepot INTEGER NOT NULL, "
						+ "PRIMARY KEY (code_depot, date, code_product),"
						+ " CONSTRAINT fkTransferStockProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkTransferStockDepot FOREIGN KEY (code_depot) "
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


	//if don't want to search with constraint pass "" not null and for stockMax pass null
	public ArrayList<String> getAllProductsCodes(String codeLike, String nameLike, Integer stockMax) throws SQLException{
		ArrayList<String> allProductsCodes = new ArrayList<>();


		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM PRODUCT "
				+ "WHERE Code like ? and name like ? and "
				+ "( "
				+ "(? = false) or "
				+ "((? = true) and ((select sum(QNT) from STOCK S where S.CODE_PRODUCT = code) <= ?)) "
				+ ") ;");

		pst.setString(1, "%"+codeLike+"%");
		pst.setString(2, "%"+nameLike+"%");
		pst.setBoolean(3, stockMax != null);
		pst.setBoolean(4, stockMax != null);

		if (stockMax != null) {
			pst.setInt(5, stockMax);
		}else{
			pst.setInt(5, 0); // Will not execute, only to avoid SQLException -> Checked with  (stockMax != null = true)
		}

		ResultSet rs = pst.executeQuery();


		while (rs.next()) {
			allProductsCodes.add(rs.getString(1));
		}

		return allProductsCodes;
	}


	public boolean isProductCodeExist(String code) throws SQLException{
		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM PRODUCT where CODE = ? ;");
		pst.setString(1, code);
		return pst.executeQuery().next();
	}

	public boolean isProductNameExist(String name) throws SQLException{
		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM PRODUCT where name = ? ;");
		pst.setString(1, name);
		return pst.executeQuery().next();
	}

	public void addNewProduct(String code, String name, ProductPrice price) throws SQLException{
		PreparedStatement pst = con.prepareStatement("INSERT INTO PRODUCT (CODE, NAME) VALUES (?, ?);");
		pst.setString(1, code);
		pst.setString(2, name);

		pst.executeUpdate();

		addPriceForProduct(code, price);
		initStockforProduct(code);
	}


	public Product getProductByCode(String code) throws SQLException{
		Product product = null;

		ResultSet rs = st.executeQuery("SELECT CODE, NAME FROM PRODUCT WHERE CODE = '"+code+"';");

		if (rs.next()) {
			product = new Product(rs.getString(1), rs.getString(2));
		}

		return product;
	}

	//Price


	public void addPriceForProduct (String productCode, ProductPrice price) throws SQLException{

		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

		PreparedStatement pst1 = con.prepareStatement("UPDATE PRICE SET DATE_END = ? WHERE CODE_PRODUCT = ? and DATE_END is NULL;");
		pst1.setTimestamp(1, currentTimestamp);
		pst1.setString(2, productCode);

		PreparedStatement pst2 = con.prepareStatement("INSERT INTO PRICE (CODE_PRODUCT, "
				+ "DATE_START, PRIXACHATTTC, TVA, PRIXVENTEHT, PRIXVENTETTC) "
				+ "VALUES (?, ?, ?, ?, ?, ?);");
		pst2.setString(1, productCode);
		pst2.setTimestamp(2, currentTimestamp);
		pst2.setDouble(3, price.getPrixAchatTTC());
		pst2.setDouble(4, price.getTva());
		pst2.setDouble(5, price.getPrixVenteHT());
		pst2.setDouble(6, price.getPrixVenteTTC());


		pst1.executeUpdate();
		pst2.executeUpdate();
	}


	public ProductPrice getProductPrice (String productCode) throws SQLException{
		return getProductPrice(productCode, new Timestamp(System.currentTimeMillis()));
	}


	public ProductPrice getProductPrice (String productCode,Timestamp timestamp) throws SQLException{

		ProductPrice productPrice = null;

		PreparedStatement pst = con.prepareStatement("SELECT "
				+ "PRIXACHATTTC, TVA, PRIXVENTEHT, PRIXVENTETTC "
				+ "FROM PRICE "
				+ "WHERE (CODE_PRODUCT = ?) "
				+ "and ( (? >= DATE_START and DATE_END is NULL) or (? >= DATE_START and ? <= DATE_END ) ) "
				+ "ORDER BY DATE_END ;");

		pst.setString(1, productCode);
		pst.setTimestamp(2, timestamp);
		pst.setTimestamp(3, timestamp);
		pst.setTimestamp(4, timestamp);

		ResultSet rs = pst.executeQuery();

		if (rs.next()) {
			productPrice = new ProductPrice(rs.getDouble(1), rs.getDouble(2), rs.getDouble(3), rs.getDouble(4));
		}

		return productPrice;
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


		// init stock
		ResultSet rs = st.executeQuery("SELECT CODE FROM DEPOT WHERE NAME = '"+name+"';");
		rs.next();
		initStockForDepot(rs.getInt(1));
	}


	public ArrayList<Depot> getAllDepots() throws SQLException{

		ArrayList<Depot> allDepots = new ArrayList<>();

		ResultSet rs = st.executeQuery("SELECT CODE, NAME, COMMENTS FROM DEPOT WHERE (CODE <> 0);");

		while (rs.next()){
			allDepots.add(new Depot(rs.getInt(1), rs.getString(2), rs.getString(3)));
		}

		return allDepots;
	}


	//Stocks




	private void initStockforProduct(String productCode) throws SQLException{

		ResultSet rs = st.executeQuery("SELECT CODE FROM DEPOT;");

		PreparedStatement pst = con.prepareStatement("INSERT INTO STOCK (CODE_DEPOT, CODE_PRODUCT, QNT)"
				+ " VALUES (?, ?, 0);");

		while (rs.next()) {
			if (rs.getInt(1) == 0) {
				continue;
			}
			pst.setInt(1, rs.getInt(1));
			pst.setString(2, productCode);

			pst.executeUpdate();
		}

	}

	private void initStockForDepot(int depotCode) throws SQLException{

		ResultSet rs = st.executeQuery("SELECT CODE FROM PRODUCT;");

		PreparedStatement pst = con.prepareStatement("INSERT INTO STOCK (CODE_DEPOT, CODE_PRODUCT, QNT)"
				+ " VALUES (?, ?, 0);");

		while (rs.next()) {
			pst.setInt(1, depotCode);
			pst.setString(2, rs.getString(1));

			pst.executeUpdate();
		}

	}

	public void transferStock(int fromDepotCode, int toDdepotCode, String productCode, int qnt) throws SQLException{

		PreparedStatement pst1 = null;

		if (fromDepotCode != 0) {
			pst1 = con.prepareStatement("UPDATE STOCK SET QNT = QNT - ? "
					+ "WHERE CODE_DEPOT = ? and CODE_PRODUCT = ?;");

			pst1.setInt(1, qnt);
			pst1.setInt(2, fromDepotCode);
			pst1.setString(3, productCode);
		}
		
		PreparedStatement pst2 = con.prepareStatement("UPDATE STOCK SET QNT = QNT + ? "
				+ "WHERE CODE_DEPOT = ? and CODE_PRODUCT = ?;");

		pst2.setInt(1, qnt);
		pst2.setInt(2, toDdepotCode);
		pst2.setString(3, productCode);



		PreparedStatement pst3 = con.prepareStatement("INSERT INTO TransferStock (CODE_DEPOT, CODE_PRODUCT, "
				+ "DATE, QNT, FROMDEPOT) VALUES (?, ?, ?, ?, ?);");

		pst3.setInt(1, toDdepotCode);
		pst3.setString(2, productCode);
		pst3.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
		pst3.setInt(4, qnt);
		pst3.setInt(5, fromDepotCode);



		if (pst1 != null) {
			pst1.executeUpdate();
		}
		pst2.executeUpdate();
		pst3.executeUpdate();
	}

	public Integer getProductsStock(String productCode) throws SQLException{

		ResultSet rs = st.executeQuery("SELECT SUM(QNT) FROM STOCK WHERE CODE_PRODUCT = '"+productCode+"' ;");

		if (rs.next()) {
			return rs.getInt(1);
		}else{
			return null;
		}

	}

	public Integer getProductsStockInDepot(String productCode, String depotCode) throws SQLException{
		ResultSet rs = st.executeQuery("SELECT QNT FROM STOCK WHERE CODE_PRODUCT = '"+productCode+"' "
				+ "and CODE_DEPOT = '"+depotCode+"' ;");

		if (rs.next()) {
			return rs.getInt(1);
		}else{
			return null;
		}
	}

}
