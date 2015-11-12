package com.wd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.wd.config.DBConf;

public class DBUtil {
	private static Logger logger = Logger.getLogger(DBUtil.class);
	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(DBConf.url, DBConf.user, DBConf.password);
	}

	/**
	 * @param sql
	 * @param params
	 * @return true代表添加成功,false代表添加失败
	 */
	public static boolean add(String sql, List<Object> params) {
		return executeADU(sql, params);
	}
	
	/**
	 * @param sql sql语句中无'?'
	 * @return true代表删除成功,false代表删除失败
	 */
	public static boolean delete(String sql) {
		return executeADU(sql, null);
	}
	
	/**
	 * @param sql
	 * @param params
	 * @return true代表删除成功,false代表删除失败
	 */
	public static boolean delete(String sql, List<Object> params) {
		return executeADU(sql, params);
	}
	
	/**
	 * @param sql
	 * @param params
	 * @return true代表更新成功,false代表更新失败
	 */
	public static boolean update(String sql, List<Object> params) {
		return executeADU(sql, params);
	}
	
	/**
	 * @param sql sql语句中无'?'
	 * @return true代表更新成功,false代表更新失败
	 */
	public static List<Object[]> query(String sql) {
		return executeQuery(sql, null);
	}
	
	public static List<Object[]> query(String sql, List<Object> params) {
		return executeQuery(sql, params);
	}
	
	/**
	 * 执行数据库增删改操作
	 * @param sql 可能包含一个或多个'?'的sql语句
	 * @param params 对应sql语句中'?'的参数列表，sql语句没有'?'时为null
	 * @return true代表操作成功,false代表操作失败
	 */
	private static boolean executeADU(String sql, List<Object> params) {
		boolean isExecute = false;
		Connection con = null;
		PreparedStatement ps = null;
		try {
			try {
				con = getConnection();
				ps = con.prepareStatement(sql);
			} catch (SQLException e) {
				logger.error("cannot access the database");
				e.printStackTrace();
				throw e;
			}
			
			if(params != null) {
				for (int i = 0; i < params.size(); i++) {
					try {
						ps.setObject(i + 1, params.get(i));
					} catch (SQLException e) {
						logger.error("cannot access the database or parameterIndex does not correspond to a parameter marker in the SQL statement");
						throw e;
					}
				}
			}
			
			try {
				ps.executeUpdate();
			} catch (SQLException e) {
				//异常1访问不到数据库2更新的数据不符合表结构,如插入数据时主键重复等
				logger.error("cannot access the database or the update data does not meet the table structure");
				throw e;
			}
			
			isExecute = true;
		} catch (SQLException e) {
			
		} finally {
			close(null, ps, con);
		}
		return isExecute;
	}
	
	/**
	 * 执行数据库查询操作
	 * @param sql
	 * @param params
	 * @return 用于存储查询成功与否和查询的结果
	 */
	private static List<Object[]> executeQuery(String sql, List<Object> params) {
		List<Object[]> queryResult = null;
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			try {
				con = getConnection();
				ps = con.prepareStatement(sql);
			} catch (SQLException e) {
				logger.error("cannot access the database");
				e.printStackTrace();
				throw e;
			}
			
			if(params != null) {
				for (int i = 0; i < params.size(); i++) {
					try {
						ps.setObject(i + 1, params.get(i));
					} catch (SQLException e) {
						logger.error("cannot access the database or parameterIndex does not correspond to a parameter marker in the SQL statement");
						throw e;
					}
				}
			}
			
			try {
				rs = ps.executeQuery();
				if(rs != null) {
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnNumber = rsmd.getColumnCount();
					queryResult = new ArrayList<Object[]>();
					while(rs.next()) {
						Object[] row = new Object[columnNumber];
						for (int i = 0; i < row.length; i++) {
							row[i] = rs.getObject(i + 1);
						}
						queryResult.add(row);	
					}
				}
			} catch (SQLException e) {
				logger.error("cannot access the database");
				queryResult = null;
				e.printStackTrace();
				throw e;
			}
			
		} catch (SQLException e) {
			
		} finally {
			close(rs, ps, con);
		}
		return queryResult;
	}
	
	public static void executeBatch(List<String> sqlList) throws SQLException{
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

	    try {
	        con = getConnection();
	        con.setAutoCommit(false);
	        st = con.createStatement();
	        for (String s : sqlList) {  
                st.addBatch(s);  
            }  
	        st.executeBatch();
	        con.commit();
	    } catch (SQLException e) {
	    	try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error("error:roll back error");
			}
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
	
	public static Object[] executeSelectSql(String sql, Callback cb) {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		Object[] objs = null;
		try {
			con = getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if (cb != null) {
				objs = cb.CallbackHandler(rs);
			}
		} catch (SQLException ex) {
			logger.equals("cannot access the database");
		} finally {
			close(rs, st, con);
		}
		return objs;
	}
}
