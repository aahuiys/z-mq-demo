package util.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectionUtil {

	private final static String SQL_SERVER_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	
	private final static String MF_SERVER_IP;
	
	private final static String MF_DB_NAME;
	
	private final static String MF_DB_USER;
	
	private final static String MF_DB_PWD;
	
	private final static String SQL_SERVER_URL;
	
	private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
	
	static {
		SetEnv setEnv = SetEnv.getInstance();
		Environments env = setEnv.getEnvironments();
		MF_SERVER_IP = env.getMF_SERVER_IP();
		MF_DB_NAME = env.getMF_DB_NAME();
		MF_DB_USER = env.getMF_DB_USER();
		MF_DB_PWD = env.getMF_DB_PWD();
		SQL_SERVER_URL = "jdbc:sqlserver://" + MF_SERVER_IP + ";databasename=" + MF_DB_NAME;
		try {
			Class.forName(SQL_SERVER_DRIVER);
		} catch (ClassNotFoundException e) {
			throw new DAOException("JDBC driver class not found.", e);
		}
	}
	
	public static Connection getConnection() {
		Connection conn = tl.get();
		if(conn == null) {
			try {
				conn = DriverManager.getConnection(SQL_SERVER_URL, MF_DB_USER, MF_DB_PWD);
				tl.set(conn);
			} catch (SQLException e) {
				throw new DAOException("Unable to get JDBC connection.", e);
			}
		}
		return conn;
	}
	
	public static void close() {
		Connection conn = tl.get();
		if(conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.err.println("JDBC Connection close failed!");
			} finally {
				tl.remove();
			}
		}
	}
}
