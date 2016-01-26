package util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;

public class H2DBUtil {
	private static volatile H2DBUtil _instance = new H2DBUtil();
	private JdbcConnectionPool connPool;
	private H2DBUtil() {
		try {
			Class.forName("org.h2.Driver");
			connPool = JdbcConnectionPool.create("jdbc:h2:~/test", "", "");
			initTables();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initTables() throws SQLException {
		Connection conn  = connPool.getConnection();
		if(null != conn) {
			Statement stat = conn.createStatement();
			stat.execute("runscript from 'db/init.sql'");
			stat.close();
			conn.close();
		}
	}
	
	public Connection getConnection() throws SQLException {
		return connPool.getConnection();
	}
	
	public static H2DBUtil getInstance() {
		return _instance;
	}
	
	public static void closeDBConnection(Connection conn) {
		if(null == conn) return;
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
