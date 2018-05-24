package org.jack.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.util.ClassScaner;
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
		testColumns(DEV_BMS, "bms_loan_ext");
//		testColumns(DEV_BMS, "bms_loan_base");
//		testColumns(DEV_CREDIT_ZX, "T_PBCCRC_REPORT");
	}
	private void testColumns(DBUtils.ConnectionInfo connectionInfo,String tableName){
		try {
			List<String> columns=getTableColumns(tableName, connectionInfo);
			log("column count:"+columns.size());
			log(columns);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private List<String> getTableColumns(String tableName,DBUtils.ConnectionInfo connectionInfo) throws SQLException{
		ResultSetMetaData rsMetaData=queryResultSetMetaData(tableName, connectionInfo);
		List<String> columns=new ArrayList<String>();
		int count=rsMetaData.getColumnCount();
		for(int i=1;i<=count;i++){
			columns.add(rsMetaData.getColumnName(i));
		}
		return columns;
	}
	private  ResultSetMetaData queryResultSetMetaData(String tableName,DBUtils.ConnectionInfo connectionInfo) throws SQLException{
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM  ");
		sql.append(tableName);
		sql.append(" where 0=1");
		ResultSet rs=query(sql, connectionInfo);
		log("result count:"+count(rs));
		return rs.getMetaData();
	}
	private int count(ResultSet rs) throws SQLException{
		int count=0;
		while(rs.next()){
			count++;
		}
		return count;
	}
	private ResultSet query(StringBuilder sql,DBUtils.ConnectionInfo connectionInfo) throws SQLException{
		Connection connection=DBUtils.getConnection(connectionInfo);
//		DatabaseMetaData databaseMetaData=connection.getMetaData();
//		String dbType=databaseMetaData.getDatabaseProductName();
//		if(dbType.toLowerCase().contains("mysql")){
//			sql.append(" limit 0");
//		}else if(dbType.toLowerCase().contains("oracle")){
//			sql.append(" where rownum<1");
//		}else if(dbType.toLowerCase().contains("db2")){
//			sql.append(" fetch   first  1 rows  only");
//		}
		Statement statement=connection.createStatement();
		return statement.executeQuery(sql.toString());
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
