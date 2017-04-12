package util.connection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseDAO {

	protected int executeUpdate(String sql) {
		Connection conn = DBConnectionUtil.getConnection();
		int result = 0;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			throw new DAOException("SQL execute update failed.", e);
		} finally {
			close(stmt, null);
		}
		return result;
	}
	
	protected List<Map<String, Object>> executeQuery(String sql) {
		Connection conn = DBConnectionUtil.getConnection();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			convertResultSetToListMap(rs, list);
		} catch (SQLException e) {
			throw new DAOException("SQL execute query failed.", e);
		} finally {
			close(stmt, rs);
		}
		return list;
	}
	
	protected int executeUpdate(String sql, Object... params) {
		Connection conn = DBConnectionUtil.getConnection();
		int result = 0;
		PreparedStatement stmt = null;
		try {
			stmt = getPreparedStatement(conn, sql, params);
			result = stmt.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException("SQL execute update failed.", e);
		} finally {
			close(stmt, null);
		}
		return result;
	}
	
	protected List<Map<String, Object>> executeQuery(String sql, Object... params) {
		Connection conn = DBConnectionUtil.getConnection();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = getPreparedStatement(conn, sql, params);
			rs = stmt.executeQuery();
			convertResultSetToListMap(rs, list);
		} catch (SQLException e) {
			throw new DAOException("SQL execute query failed.", e);
		} finally {
			close(stmt, rs);
		}
		return list;
	}
	
	private PreparedStatement getPreparedStatement(Connection conn, String sql, Object... params) throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(sql);
		if(params != null) {
			for(int i = 0; i < params.length; i++) {
				stmt.setObject(i + 1, params[i]);
			}
		}
		return stmt;
	}
	
	private void convertResultSetToListMap(ResultSet rs, List<Map<String, Object>> list) {
		try {
			ResultSetMetaData metaData = rs.getMetaData();
			int count = metaData.getColumnCount();
			while(rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				for(int i = 1; i <= count; i ++) {
					String key = metaData.getColumnName(i);
					Object value = rs.getObject(key);
					map.put(key, value);
				}
				list.add(map);
			}
		} catch (SQLException e) {
			throw new DAOException("Convert ResultSet failed.", e);
		}
	}
	
	private void close(Statement stmt, ResultSet rs) {
		try {
			if(rs != null) rs.close();
			if(stmt != null) stmt.close();
		} catch (SQLException e) {
			System.err.println("ResultSet or Statement close failed!");
		}
	}
}
