package org.jack.common.util;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {
	static{
		registerDriver(ClassLoaderUtils.loadClass("com.mysql.cj.jdbc.Driver"));
		registerDriver(ClassLoaderUtils.loadClass("oracle.jdbc.OracleDriver"));
	}
	public static void registerDriver(Class<?> cls){
		if(cls==null){
			return;
		}
		try {
			registerDriver((Driver)cls.newInstance());
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	public static void registerDriver(Driver driver){
		try {
			DriverManager.registerDriver(driver);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static class ConnectionInfo{
		private String url;
		private String user;
		private String password;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getUser() {
			return user;
		}
		public void setUser(String user) {
			this.user = user;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
	}
	public static Connection getConnection(ConnectionInfo connectionInfo) throws SQLException{
		return DriverManager.getConnection(connectionInfo.url, connectionInfo.user, connectionInfo.password);
	}
}
