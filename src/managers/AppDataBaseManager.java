package managers;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;

import exceptions.DataBaseDriverLoadFailedException;
import models.Bill;
import models.Client;
import models.Depot;
import models.Payment;
import models.Product;
import models.ProductBill;
import models.ProductPrice;
import models.ProductStock;

public class AppDataBaseManager {

	static private String PAYMENT_TYPE_CHECK = "CHECK";
	static private String PAYMENT_TYPE_CASH = "CASH";

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
						+ "QNT DOUBLE NOT NULL, "
						+ "PRIMARY KEY (code_depot, code_product), "
						+ "CONSTRAINT fkStockProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkStockDepot FOREIGN KEY (code_depot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("CREATE TABLE TransferStock ( "
						+ "code INTEGER NOT NULL IDENTITY, "
						+ "date DATETIME NOT NULL, "
						+ "toDepot INTEGER NOT NULL, "
						+ "fromDepot INTEGER NOT NULL, "
						+ "PRIMARY KEY (code), "
						+ "CONSTRAINT fkTransferStockToDepot FOREIGN KEY (toDepot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkTransferStockFromDepot FOREIGN KEY (fromDepot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("CREATE TABLE TransferStockProducts ( "
						+ "code_TransferStock INTEGER NOT NULL, "
						+ "code_product VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "QNT DOUBLE NOT NULL, "
						+ "PRIMARY KEY (code_TransferStock, code_product), "
						+ "CONSTRAINT fkTransferStockProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkTransferStockCode FOREIGN KEY (code_TransferStock) "
						+ "REFERENCES TransferStock (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("INSERT INTO Depot (NAME) VALUES ('ADMIN');");



				st.executeUpdate("CREATE TABLE Client ("
						+ "CODE VARCHAR_IGNORECASE(30) NOT NULL, "
						+ "NAME VARCHAR_IGNORECASE(25) NOT NULL, "
						+ "LASTNAME VARCHAR_IGNORECASE(25) NOT NULL, "
						+ "ADRESSE VARCHAR_IGNORECASE(400) DEFAULT '', "
						+ "PRIMARY KEY (CODE) );");


				st.executeUpdate("CREATE TABLE PhoneNumber ("
						+ "number VARCHAR_IGNORECASE(30) NOT NULL, "
						+ "type VARCHAR_IGNORECASE(5) NOT NULL, "
						+ "code_client VARCHAR_IGNORECASE(30) NOT NULL, "
						+ "CHECK(type IN ('fax','phone')), "
						+ "PRIMARY KEY (number, type,code_client), "
						+ "CONSTRAINT fkClientPhoneNumber FOREIGN KEY (code_client) "
						+ "REFERENCES Client (CODE) ON DELETE CASCADE ON UPDATE CASCADE );");


				st.executeUpdate("CREATE TABLE BILL ( "
						+ "code VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "date DATETIME NOT NULL, "
						+ "discount DOUBLE NOT NULL, "
						+ "code_client VARCHAR_IGNORECASE(30) NOT NULL, "
						+ "code_depot INTEGER NOT NULL, "
						+ "CHECK( discount >= 0 and discount <= 100), "
						+ "PRIMARY KEY (code), "
						+ "CONSTRAINT fkBillClient FOREIGN KEY (code_client) "
						+ "REFERENCES Client (CODE) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkBillDepot FOREIGN KEY (code_depot) "
						+ "REFERENCES DEPOT (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("CREATE TABLE BillProducts ( "
						+ "code_bill VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "code_product VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "QNT DOUBLE NOT NULL, "
						+ "price DOUBLE NOT NULL, "
						+ "PRIMARY KEY (code_bill, code_product), "
						+ "CONSTRAINT fkBillProduct FOREIGN KEY (code_product) "
						+ "REFERENCES Product (code) ON DELETE CASCADE ON UPDATE CASCADE, "
						+ "CONSTRAINT fkBillCode FOREIGN KEY (code_bill) "
						+ "REFERENCES BILL (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");


				st.executeUpdate("CREATE TABLE PAYMENT ( "
						+ "id INTEGER NOT NULL IDENTITY, "
						+ "date DATETIME NOT NULL, "
						+ "type VARCHAR_IGNORECASE(5) NOT NULL, "
						+ "amount DOUBLE NOT NULL, "
						+ "code_bill VARCHAR_IGNORECASE(20) NOT NULL, "
						+ "CHECK( type in ('cash','check') ), "
						+ "PRIMARY KEY (id), "
						+ "CONSTRAINT fkPaymentBillCode FOREIGN KEY (code_bill) "
						+ "REFERENCES BILL (CODE) ON DELETE CASCADE ON UPDATE CASCADE);");

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


	//Clients


	//if don't want to search with constraint pass "" not null
	public ArrayList<String> getAllClientsCodes(String codeLike, String nameLike, String lastNameLike
			, String addressLike) throws SQLException{
		ArrayList<String> allClientsCodes = new ArrayList<>();

		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM CLIENT "
				+ "WHERE CODE LIKE ? and NAME LIKE ? and LASTNAME LIKE ? and ADRESSE LIKE ? ;");

		pst.setString(1, "%"+codeLike+"%");
		pst.setString(2, "%"+nameLike+"%");
		pst.setString(3, "%"+lastNameLike+"%");
		pst.setString(4, "%"+addressLike+"%");

		ResultSet rs = pst.executeQuery();


		while (rs.next()) {
			allClientsCodes.add(rs.getString(1));
		}

		return allClientsCodes;
	}

	public ArrayList<String> getAllClientsCodes() throws SQLException{
		return getAllClientsCodes("", "", "", "");
	}


	public Client getClientByCode(String code) throws SQLException{
		Client client = null;

		ResultSet rs = st.executeQuery("SELECT CODE, NAME, LASTNAME, ADRESSE FROM CLIENT "
				+ "WHERE CODE = '"+code+"';");

		if (rs.next()) {
			client = new Client(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
		}

		return client;
	}



	//Bills

	private String createNewBillCode() throws SQLException{

		LocalDate date = LocalDate.now();

		String year = date.getYear()+"";
		String month = date.getMonthValue()+"";

		if (month.length() == 1) {
			month = "0"+month;
		}

		PreparedStatement ps = con.prepareStatement("SELECT Count(CODE) FROM BILL WHERE DATE >= ? and DATE <= ? ;");

		ps.setString(1, year+"-"+month+"-"+"01");
		ps.setString(2, year+"-"+month+"-"+date.getMonth().maxLength());

		ResultSet rs = ps.executeQuery();
		rs.next();
		int billsCount = rs.getInt(1);
		billsCount++;

		String billsCountString = Integer.toString(billsCount);

		while (billsCountString.length() < 5) {
			billsCountString = "0" + billsCountString;
		}

		return year.substring(1)+month+billsCountString;
	}

	public String createNewBill(Bill bill) throws SQLException{
		String billCode = createNewBillCode();

		PreparedStatement psCreateNewBill = con.prepareStatement("INSERT INTO BILL "
				+ "(CODE, DATE, DISCOUNT, CODE_CLIENT, CODE_DEPOT) VALUES (?, ?, ?, ?, ?);");
		psCreateNewBill.setString(1, billCode);
		psCreateNewBill.setTimestamp(2, bill.getDate());
		psCreateNewBill.setDouble(3, bill.getDiscount());
		psCreateNewBill.setString(4, bill.getClientCode());
		psCreateNewBill.setInt(5, bill.getDepotCode());

		psCreateNewBill.executeUpdate();

		PreparedStatement psAddProductToBill = con.prepareStatement("INSERT INTO BILLPRODUCTS "
				+ "(CODE_BILL, CODE_PRODUCT, QNT, PRICE) "
				+ "VALUES (?, ?, ?, ?);");
		PreparedStatement psUpdateStock = con.prepareStatement("UPDATE STOCK SET QNT = QNT - ? "
				+ "WHERE CODE_DEPOT = ? and CODE_PRODUCT = ?;");

		psAddProductToBill.setString(1, billCode);

		ArrayList<ProductBill> billsProducts = bill.getProducts();

		for (int i=0; i<billsProducts.size(); i++) {
			ProductBill product = billsProducts.get(i);
			psAddProductToBill.setString(2, product.getCode());
			psAddProductToBill.setDouble(3, product.getQnt());
			psAddProductToBill.setDouble(4, product.getPriceSelled());

			psUpdateStock.setDouble(1, product.getQnt());
			psUpdateStock.setInt(2, bill.getDepotCode());
			psUpdateStock.setString(3, product.getCode());

			psAddProductToBill.executeUpdate();
			psUpdateStock.executeUpdate();
		}


		return billCode;
	}

	public Bill getBillByCode(String billCode) throws SQLException{

		ResultSet rsBillDetails = st.executeQuery("SELECT CODE, DATE, DISCOUNT, CODE_CLIENT, CODE_DEPOT "
				+ "FROM BILL WHERE code = '"+billCode+"' ;");
		if (!rsBillDetails.next()) {
			return null;
		}

		ArrayList<ProductBill> products = new ArrayList<>();

		ResultSet rsBillProducts = st.executeQuery("SELECT CODE_PRODUCT, QNT, PRICE "
				+ "FROM BILLPRODUCTS WHERE CODE_BILL = '"+billCode+"' ;");

		while (rsBillProducts.next()) {
			Product product = getProductByCode(rsBillProducts.getString(1));
			product.setPrice(getProductPrice(product.getCode(), rsBillDetails.getTimestamp(2)));
			products.add(new ProductBill(product, rsBillProducts.getDouble(3), rsBillProducts.getDouble(2)));
		}

		return new Bill(billCode, rsBillDetails.getString(4), rsBillDetails.getInt(5), 
				rsBillDetails.getTimestamp(2), rsBillDetails.getDouble(3), products) ;
	}

	//if don't want to search with constraint pass "" not null and for date pass null
	public ArrayList<String> getAllBillsCodes(String codeLike, LocalDate date) throws SQLException{
		ArrayList<String> allBillsCodes = new ArrayList<>();

		PreparedStatement pst = con.prepareStatement("SELECT CODE FROM BILL "
				+ "WHERE Code like ? and "
				+ "( "
				+ "(? = false) or "
				+ "((? = true) and (CAST(DATE AS DATE) = ?)) "
				+ ") ;");

		pst.setString(1, codeLike+"%");
		pst.setBoolean(2, date != null);
		pst.setBoolean(3, date != null);

		if (date != null) {
			pst.setDate(4, Date.valueOf(date));
		}else{
			pst.setDate(4, new Date(System.currentTimeMillis()));// Will not execute, only to avoid SQLException -> Checked with  (date != null = true in the SQL request)
		}

		ResultSet rs = pst.executeQuery();

		while (rs.next()) {
			allBillsCodes.add(rs.getString(1));
		}

		return allBillsCodes;
	}

	//Payments

	public void addPaymentToBill(String billCode, Payment payment) throws SQLException{
		String paymentType = "";

		if (payment.getType().toUpperCase().equals(Payment.TYPE_CASH.toUpperCase())) {
			paymentType = PAYMENT_TYPE_CASH;
		}else if (payment.getType().toUpperCase().equals(Payment.TYPE_CHECK.toUpperCase())) {
			paymentType = PAYMENT_TYPE_CHECK;
		}

		PreparedStatement ps = con.prepareStatement("INSERT INTO PAYMENT (DATE, TYPE, AMOUNT, CODE_BILL) "
				+ "VALUES (?, ?, ?, ?);");
		ps.setTimestamp(1, payment.getDate());
		ps.setString(2, paymentType);
		ps.setDouble(3, payment.getAmmount());
		ps.setString(4, billCode);
		ps.executeUpdate();
	}

	public ArrayList<Payment> getAllPaymentForBillByBillCode(String billCode) throws SQLException{

		ArrayList<Payment> allPayments = new ArrayList<>();

		ResultSet rs = st.executeQuery("SELECT DATE, TYPE, AMOUNT FROM PAYMENT WHERE"
				+ " CODE_BILL = '"+billCode+"' ;");

		while (rs.next()) {
			String paymentType = "";

			if (rs.getString(2).toUpperCase().equals(PAYMENT_TYPE_CASH.toUpperCase())){
				paymentType = Payment.TYPE_CASH;
			}else if (rs.getString(2).toUpperCase().equals(PAYMENT_TYPE_CHECK.toUpperCase())){
				paymentType = Payment.TYPE_CHECK;
			}

			allPayments.add(new Payment(rs.getDouble(3), paymentType, rs.getTimestamp(1)));
		}

		return allPayments;
	}

	public double getPayedAmountForBillByBillCode(String billCode) throws SQLException{
		ResultSet rs = st.executeQuery("SELECT SUM(AMOUNT) FROM PAYMENT WHERE CODE_BILL = '"
				+billCode+"' ;");
		rs.next();
		return rs.getDouble(1);
	}

	//Products


	//if don't want to search with constraint pass "" not null and for stockMax pass null
	public ArrayList<String> getAllProductsCodes(String codeLike, String nameLike, Double stockMax) throws SQLException{
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
			pst.setDouble(5, stockMax);
		}else{
			pst.setInt(5, 0); // Will not execute, only to avoid SQLException -> Checked with  (stockMax != null = true in the SQL request)
		}

		ResultSet rs = pst.executeQuery();


		while (rs.next()) {
			allProductsCodes.add(rs.getString(1));
		}

		return allProductsCodes;
	}

	public ArrayList<String> getAllProductsCodes(String codeLike, String nameLike) throws SQLException{
		return getAllProductsCodes(codeLike, nameLike, null);
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

	public void addNewProduct(Product product) throws SQLException{
		PreparedStatement pst = con.prepareStatement("INSERT INTO PRODUCT (CODE, NAME) VALUES (?, ?);");
		pst.setString(1, product.getCode());
		pst.setString(2, product.getName());

		pst.executeUpdate();

		addPriceForProduct(product.getCode(), product.getPrice());
		initStockforProduct(product.getCode());
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


	public Depot getAdminDepot() throws SQLException{

		Depot depot = null;

		ResultSet rs = st.executeQuery("SELECT CODE, NAME, COMMENTS FROM DEPOT WHERE CODE = 0 ;");

		if (rs.next()) {
			depot = new Depot(rs.getInt(1), rs.getString(2), rs.getString(3));
		}

		return depot;
	}

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

	public Depot getDepotByCode(int depotCode) throws SQLException{
		Depot depot = null;

		ResultSet rs = st.executeQuery("SELECT CODE, NAME, COMMENTS FROM DEPOT WHERE CODE = '"+depotCode+"' ;");

		if (rs.next()) {
			depot = new Depot(depotCode, rs.getString(2), rs.getString(3));
		}

		return depot;
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



	public void transferStock(int fromDepotCode, int toDepotCode, ArrayList<ProductStock> productsWithStocks, Timestamp timestamp) throws SQLException{

		if (productsWithStocks.size() == 0) {
			return;
		}

		PreparedStatement pstInsertIntoTransfertStock = con.prepareStatement("INSERT INTO TRANSFERSTOCK "
				+ "(DATE, TODEPOT, FROMDEPOT) VALUES (?, ?, ?);");

		PreparedStatement pstInsertIntoTransferStockProducts = con.prepareStatement("INSERT INTO "
				+ "TRANSFERSTOCKPRODUCTS (CODE_TRANSFERSTOCK, CODE_PRODUCT, QNT) VALUES (?, ?, ?);");

		PreparedStatement pstUpdateStock = con.prepareStatement("UPDATE STOCK SET QNT = QNT + ? "
				+ "WHERE CODE_DEPOT = ? and CODE_PRODUCT = ?;");


		pstInsertIntoTransfertStock.setTimestamp(1, timestamp);
		pstInsertIntoTransfertStock.setInt(2, toDepotCode);
		pstInsertIntoTransfertStock.setInt(3, fromDepotCode);

		pstInsertIntoTransfertStock.executeUpdate();

		ResultSet rs = st.executeQuery("SELECT MAX(CODE) FROM TRANSFERSTOCK;");
		rs.next();
		int transferStockCode = rs.getInt(1);


		pstInsertIntoTransferStockProducts.setInt(1, transferStockCode);


		for (int i=0; i<productsWithStocks.size(); i++) {

			pstInsertIntoTransferStockProducts.setString(2, productsWithStocks.get(i).getCode());
			pstInsertIntoTransferStockProducts.setDouble(3, productsWithStocks.get(i).getQnt());

			pstInsertIntoTransferStockProducts.executeUpdate();

			pstUpdateStock.setDouble(1, productsWithStocks.get(i).getQnt());
			pstUpdateStock.setInt(2, toDepotCode);
			pstUpdateStock.setString(3, productsWithStocks.get(i).getCode());

			pstUpdateStock.executeUpdate();


			if (fromDepotCode != 0) {
				pstUpdateStock.setDouble(1, -productsWithStocks.get(i).getQnt());
				pstUpdateStock.setInt(2, fromDepotCode);
				pstUpdateStock.executeUpdate();
			}
		}

	}


	public void transferStock(int fromDepotCode, int toDepotCode, ArrayList<ProductStock> productsWithStocks) throws SQLException{
		transferStock(fromDepotCode, toDepotCode, productsWithStocks, new Timestamp(System.currentTimeMillis()));
	}


	public Double getProductsStock(String productCode) throws SQLException{

		ResultSet rs = st.executeQuery("SELECT SUM(QNT) FROM STOCK WHERE CODE_PRODUCT = '"+productCode+"' ;");

		if (rs.next()) {
			return rs.getDouble(1);
		}else{
			return null;
		}

	}

	public Double getProductsStock(String productCode, int depotCode) throws SQLException{
		ResultSet rs = st.executeQuery("SELECT QNT FROM STOCK WHERE CODE_PRODUCT = '"+productCode+"' "
				+ "and CODE_DEPOT = "+depotCode+" ;");

		if (rs.next()) {
			return rs.getDouble(1);
		}else{
			return null;
		}
	}

}
