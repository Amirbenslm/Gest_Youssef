package managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import models.DataBaseDriverLoadFailed;

public class AppDataBase {

	static public AppDataBase shared = new AppDataBase();
	
	private Connection con;
	private Statement st;
	
	public void prepare() throws DataBaseDriverLoadFailed{
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:file:Database/AppDB;shutdown=true;hsqldb.write_delay=false", "Xr@o/o!t#_?", "Y1T#6uNa?+O$c42@");
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			throw new DataBaseDriverLoadFailed();
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
	
	public void createDatabaseTablesIfNeedeed(){
		try {
			
			st.executeUpdate(
					"CREATE TABLE IF NOT EXISTS salarydetails ("
							  +"EmpID varchar(6) PRIMARY KEY,"
							  +"Salary INT NOT NULL,"
							  +"Bonus INT NOT NULL,"
							  +"Increment INT NOT NULL"
							  +");"
					);
			st.executeUpdate("INSERT INTO salarydetails VALUES ('54601A', 10000, 5000, 2000);");
			st.executeUpdate("INSERT INTO salarydetails VALUES ('54602A', 12000, 500, 2000);");
			st.executeUpdate("INSERT INTO salarydetails VALUES ('54603A', 11000, 500, 2000);");
			//st.close();
			//con.close();
			System.out.println("./");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void select(){
		try {
			ResultSet rs = st.executeQuery("SELECT * FROM SALARYDETAILS;");
			int nb = 0;
			while (rs.next()){
				nb++;
			}
			System.out.println("NB : "+nb);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
