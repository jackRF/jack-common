package org.jack.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.util.DBUtils;
import org.junit.Test;

public class DBTest extends BaseTest{
	private static DBUtils.ConnectionInfo DEV_BMS;
	private static DBUtils.ConnectionInfo DEV_CREDIT_ZX;
	static{
		DEV_BMS=new DBUtils.ConnectionInfo();
		DEV_BMS.setUrl("jdbc:mysql://172.16.230.122:3306/bms_cyb");
		DEV_BMS.setUser("bms");
		DEV_BMS.setPassword("bms");
		
		DEV_CREDIT_ZX=new DBUtils.ConnectionInfo();
		DEV_CREDIT_ZX.setUrl("jdbc:oracle:thin:@172.16.230.90:1521:stupor");
		DEV_CREDIT_ZX.setUser("xd_zx");
		DEV_CREDIT_ZX.setPassword("123456");
	}
	@Test
	public void testColumns(){
		DBUtils.ConnectionInfo connectionInfo=DEV_CREDIT_ZX;
		String tableName="bms_loan_base";
		
		tableName="T_PBCCRC_REPORT";
		try {
			List<String> columns=getTableColumns(tableName, connectionInfo);
			log("ColumnCount:"+columns.size());
			log(columns);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private List<String> getTableColumns(String tableName,DBUtils.ConnectionInfo connectionInfo) throws SQLException{
		Connection connection=DBUtils.getConnection(connectionInfo);
		DatabaseMetaData metaData=connection.getMetaData();
		String dbType=metaData.getDatabaseProductName();
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM  ");
		sql.append(tableName);
		if(dbType.toLowerCase().contains("mysql")){
			sql.append(" limit 0");
		}else if(dbType.toLowerCase().contains("oracle")){
			sql.append(" where rownum<1");
		}else if(dbType.toLowerCase().contains("db2")){
			sql.append(" fetch   first  1 rows  only");
		}
		List<String> columns=new ArrayList<String>();
		Statement statement=connection.createStatement();
		ResultSet rs=statement.executeQuery(sql.toString());
		ResultSetMetaData rsetaData=rs.getMetaData();
		int count=rsetaData.getColumnCount();
		
		for(int i=1;i<=count;i++){
			columns.add(rsetaData.getColumnName(i));
		}
		return columns;
		
	}
	@Test
	public void testMySQL() {
		testConnection(DEV_BMS);
	}
	@Test
	public void testOracel() {
		testConnection(DEV_CREDIT_ZX);
	}
	private void testConnection(DBUtils.ConnectionInfo connectionInfo) {
		try {
			Connection connection=DBUtils.getConnection(connectionInfo);
			DatabaseMetaData metaData=connection.getMetaData();
			log(metaData.getDatabaseProductName());
			log(metaData.getDatabaseProductVersion());
			log("Connection success");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
