package com.wd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.wd.config.DB2Conf;

public class DBUtil2 {
	private static Logger logger = Logger.getLogger(DBUtil2.class);
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DB2Conf.url, DB2Conf.user, DB2Conf.password);
	}
	
	public static void executeSql(List<String> sqlList) throws SQLException{
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	    try {
	        con = getConnection();
	        st = con.createStatement();
	        for (String s : sqlList) {  
                st.addBatch(s);  
            }  
	        st.executeBatch();
	    } catch (SQLException e) {
	    	logger.error("error:" + e.getMessage());
	    	throw e;
	    } finally {
	    	close(rs, st, con);
	    }
	}
	
	public static void executeSql(String sql) throws SQLException{
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	    try {
	        con = getConnection();
	        st = con.createStatement();
	        st.executeUpdate(sql);
	
	    } catch (SQLException e) {
	    	logger.error("sql:" + sql + ",error:" + e.getMessage());
	    	throw e;
	    } finally {
	    	close(rs, st, con);
	    }
	}
	
	private static void close(ResultSet rs, Statement st, Connection con) {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error("close resultSet error"); 
			}
		}
		
		if(st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				logger.error("close statement error");
			}
		}
		
		if(con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.error("close connection error");
			}
		}
	} 
}
