package org.jack.common;

import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.jack.common.util.ClassScaner;
import org.jack.common.util.DBUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Utils;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

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
	public void propertyToColumn(){
		String propertyName=Utils.propertyToColumn("review_remark", new StringBuilder());
		log(propertyName);
	}
	@Test
	public void compareScan(){
		@SuppressWarnings("unchecked")
		Set<Class<?>> clazz=ClassScaner.scan("com.ymkj.bms.domain", Entity.class);
		for(Class<?> entityClass:clazz){
			try {
				compareEntity(entityClass);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private void compareEntity(Class<?> entityClass) throws SQLException{
		
		Table table=entityClass.getAnnotation(Table.class);
		Map<String, ColumnInfo> columnInfos=getTableColumnInfos(table.name(), DEV_BMS);
		BeanWrapperImpl wrapper=new BeanWrapperImpl(entityClass);
		StringBuilder sb=new StringBuilder();
		PropertyDescriptor[] propertyDescriptors=wrapper.getPropertyDescriptors();
		Set<String> prpperties=new HashSet<String>();
		for(PropertyDescriptor propertyDescriptor:propertyDescriptors){
			String propertyName=propertyDescriptor.getName();			
			Method method=propertyDescriptor.getReadMethod();
			if(method==null){
				log("miss getter " +entityClass.getSimpleName()+"."+propertyName);
				continue;
			}
			if(Object.class.equals(propertyDescriptor.getReadMethod().getDeclaringClass())){
				continue;
			}
			if(propertyDescriptor.getWriteMethod()==null){
				log("miss setter " +entityClass.getSimpleName()+"."+propertyName);
				continue;
			}
			Column column=wrapper.getPropertyTypeDescriptor(propertyName).getAnnotation(Column.class);
			String columnName=null;
			if(column!=null&&StringUtils.hasText(column.name())){
				columnName=column.name().toUpperCase();
			}else{
				columnName=Utils.propertyToColumn(propertyName, sb);
			}
			if(!columnInfos.containsKey(columnName)){
				log(table.name()+" miss "+columnName+"  "+propertyName);
				continue;
			}
			prpperties.add(columnName);
			ColumnInfo columnInfo=columnInfos.get(columnName);
			String columnClassName=columnInfo.columnClassName;
			Class<?> propertyType=propertyDescriptor.getPropertyType();
			if(columnInfo.precision>=21845){
				log(String.format("%s %s %s", table.name(),columnName,columnClassName));
			}
			if(!columnClassName.equals(propertyType.getName())){
				if(("java.sql.Timestamp".equals(columnClassName)||"java.sql.Date".equals(columnClassName))&&Date.class.equals(propertyType)){
					continue;
				}
				if(Character.class.equals(propertyType)&&columnInfo.precision==1&&String.class.getName().equals(columnClassName)){
					continue;
				}
				if(Byte.class.equals(propertyType)&&columnInfo.precision<8&&Integer.class.getName().equals(columnClassName)){
					continue;
				}
				if(Integer.class.equals(propertyType)&&columnInfo.precision<32&&Long.class.getName().equals(columnClassName)){
					continue;
				}
				log(table.name()+" "+propertyDescriptor.getName()+"  "+columnClassName+" not match "+propertyType);
			}
		}
		List<String> columns=new ArrayList<String>(columnInfos.keySet());
		columns.removeAll(prpperties);
		if(columns.size()>0){
			log(entityClass+" miss columns mapping:[");
			for(String column:columns){
				log(column+" "+columnInfos.get(column).columnClassName);
			}
			log("]");
			
		}
		
		
	}
	
	@Test
	public void testColumns(){
		testColumns(DEV_BMS, "bms_loan_review");
//		testColumns(DEV_BMS, "bms_loan_base");
//		testColumns(DEV_CREDIT_ZX, "T_PBCCRC_REPORT");
	}
	@Test
	public void testGenerateEntity() {
		generateEntity(DEV_BMS, "bms_review_log");
	}
	private void generateEntity(DBUtils.ConnectionInfo connectionInfo,String tableName){
		Map<String, ColumnInfo> columnInfos;
		try {
			columnInfos = getTableColumnInfos(tableName, connectionInfo);
			List<ColumnInfo> list=new ArrayList<ColumnInfo>(columnInfos.values());
			Collections.sort(list,new Comparator<ColumnInfo>() {
				@Override
				public int compare(ColumnInfo o1, ColumnInfo o2) {
					if(o1.index==o2.index){
						return 0;
					}
					return o1.index<o2.index?-1:1;
				}
			});
			String file="D:\\data\\generate\\entity\\"+tableName+".java";
			FileOutputStream out=new FileOutputStream(file);
			PrintStream p = new PrintStream(out);
			StringBuilder sb=new StringBuilder();
			for(ColumnInfo columnInfo:list){
				p.println();
				p.print('\t');
				Class<?> clazz=getJavaType(columnInfo);
				p.print(String.format("private %s %s;", clazz!=null?clazz.getSimpleName():null,Utils.columnToProperty(columnInfo.columnName, sb)));
			}
			out.close();
			p.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	private Class<?> getJavaType(ColumnInfo columnInfo){
		try {
			Class<?> clazz=Class.forName(columnInfo.getColumnClassName());
			return clazz;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	private void testColumns(DBUtils.ConnectionInfo connectionInfo,String tableName){
		try {
			Map<String, ColumnInfo> columnInfos=getTableColumnInfos(tableName, connectionInfo);
			List<ColumnInfo> list=new ArrayList<ColumnInfo>(columnInfos.values());
			Collections.sort(list,new Comparator<ColumnInfo>() {
				@Override
				public int compare(ColumnInfo o1, ColumnInfo o2) {
					if(o1.index==o2.index){
						return 0;
					}
					return o1.index<o2.index?-1:1;
				}
			});
			List<String> columns=new ArrayList<String>();
			for(ColumnInfo columnInfo:list){
				columns.add(columnInfo.getColumnName());
			}
			log(tableName+" column count:"+columns.size());
			log(columns);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static class ColumnInfo{
		private int index;
		private String catalogName;
		private String columnClassName;
		private int columnDisplaySize;
		private String columnLabel;
		private String columnName;
		private int columnType;
		private String columnTypeName;
		private int precision;
		private int scale;
		private String schemaName;
		private String tableName;
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		public String getCatalogName() {
			return catalogName;
		}
		public void setCatalogName(String catalogName) {
			this.catalogName = catalogName;
		}
		public String getColumnClassName() {
			return columnClassName;
		}
		public void setColumnClassName(String columnClassName) {
			this.columnClassName = columnClassName;
		}
		public int getColumnDisplaySize() {
			return columnDisplaySize;
		}
		public void setColumnDisplaySize(int columnDisplaySize) {
			this.columnDisplaySize = columnDisplaySize;
		}
		public String getColumnLabel() {
			return columnLabel;
		}
		public void setColumnLabel(String columnLabel) {
			this.columnLabel = columnLabel;
		}
		public String getColumnName() {
			return columnName;
		}
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
		public int getColumnType() {
			return columnType;
		}
		public void setColumnType(int columnType) {
			this.columnType = columnType;
		}
		public String getColumnTypeName() {
			return columnTypeName;
		}
		public void setColumnTypeName(String columnTypeName) {
			this.columnTypeName = columnTypeName;
		}
		public int getPrecision() {
			return precision;
		}
		public void setPrecision(int precision) {
			this.precision = precision;
		}
		public int getScale() {
			return scale;
		}
		public void setScale(int scale) {
			this.scale = scale;
		}
		public String getSchemaName() {
			return schemaName;
		}
		public void setSchemaName(String schemaName) {
			this.schemaName = schemaName;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
	}
	private Map<String, ColumnInfo> getTableColumnInfos(String tableName,DBUtils.ConnectionInfo connectionInfo) throws SQLException{
		ResultSetMetaData rsMetaData=queryResultSetMetaData(tableName, connectionInfo);
		int count=rsMetaData.getColumnCount();
		Map<String,ColumnInfo> columnInfoMap=new HashMap<String,ColumnInfo>();
		for(int column=1;column<=count;column++){
			ColumnInfo columnInfo=new ColumnInfo();
			columnInfo.setIndex(column);
			columnInfo.catalogName=rsMetaData.getCatalogName(column);
			columnInfo.columnClassName=rsMetaData.getColumnClassName(column);
			columnInfo.columnDisplaySize=rsMetaData.getColumnDisplaySize(column);
			columnInfo.columnLabel=rsMetaData.getColumnLabel(column);
			columnInfo.columnName=rsMetaData.getColumnName(column);
			columnInfo.columnType=rsMetaData.getColumnType(column);
			columnInfo.columnTypeName=rsMetaData.getColumnTypeName(column);
			columnInfo.precision=rsMetaData.getPrecision(column);
			columnInfo.scale=rsMetaData.getScale(column);
			columnInfo.schemaName=rsMetaData.getSchemaName(column);
			columnInfo.tableName=rsMetaData.getTableName(column);
			columnInfoMap.put(rsMetaData.getColumnName(column).toUpperCase(), columnInfo);
		}
		return columnInfoMap;
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
