package util.connection;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionUtil {

	public static void beginTransaction() {
		Connection conn = DBConnectionUtil.getConnection();
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			throw new DAOException("Transaction begin exception!", e);
		}
	}
	
	public static void commit() {
		Connection conn = DBConnectionUtil.getConnection();
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DAOException("Transaction commit exception!", e);
		}
	}
	
	public static void rollback() {
		Connection conn = DBConnectionUtil.getConnection();
		try {
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DAOException("Transaction rollback exception!", e);
		}
	}
	
	public static void commitAndClose() {
		Connection conn = DBConnectionUtil.getConnection();
		try {
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DAOException("Transaction commit exception!", e);
		} finally {
			DBConnectionUtil.close();
		}
	}
	
	public static void rollbackAndClose() {
		Connection conn = DBConnectionUtil.getConnection();
		try {
			conn.rollback();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			throw new DAOException("Transaction rollback exception!", e);
		} finally {
			DBConnectionUtil.close();
		}
	}
}
