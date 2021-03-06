package org.jack.common.orm;

import java.beans.PropertyDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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

import org.jack.common.BaseTest;
import org.jack.common.util.ClassScaner;
import org.jack.common.util.DBUtils;
import org.jack.common.util.DBUtils.ConnectionInfo;
import org.jack.common.util.net.AuthenticatePair;
import org.jack.common.util.net.ConnectionPair;
import org.jack.common.util.net.DBConnectionPair;
import org.jack.common.util.net.DBPair;
import org.jack.common.util.net.NetAddressPair;
import org.jack.common.util.Utils;
import org.junit.Test;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class DBTest extends BaseTest{
	private static interface MatchFilter{
		boolean match(String tableName,String tableType,ColumnInfo columnInfo);
		void accept(String tableName,String tableType,ColumnInfo columnInfo);
	}
	public static Table getTable(DBConnectionPair connectionPair,String name){
		return null;
		
	}
	@Test
	public void testTables() {
		try {
			List<String> list=getAllTables(getDatabase("DEV_CRM"));
			for(String tableName:list){
				log(tableName);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected List<String> getAllTables(DBConnectionPair dbConnectionPair) throws SQLException{
		Connection sqlConnection=getConnection(dbConnectionPair);
		return getAllTables(sqlConnection);
	}
	
	protected List<String> getAllTables(Connection sqlConnection) throws SQLException{
		StringBuilder sql=new StringBuilder();
		DatabaseMetaData dmd=sqlConnection.getMetaData();
		ResultSet rs=dmd.getSchemas();
		List<String> schemas=new ArrayList<>();
		while(rs.next()) {
			schemas.add(rs.getString(1));
		}
		rs.close();
		String productName=dmd.getDatabaseProductName();
		if("Mysql".equals(productName)) {
			sql.append("select table_name from information_schema.tables where table_schema='"+schemas.get(0)+"' and table_type='BASE TABLE'");
		}else if("Oracle".equals(productName)){
			sql.append(" select table_name from user_tables");
		}
		
		rs=query(sql.toString(), sqlConnection);
		List<String> list=new ArrayList<String>();
		while(rs.next()){
			list.add(rs.getString("table_name"));
		}
		rs.close();
		return list;
	}
	@Test
	public void testConn() {
		Connection sqlConnection=getConnection(getDatabase("DEV_CRM"));
		try {
			DatabaseMetaData dmd=sqlConnection.getMetaData();
			viewDatabaseMetaData(dmd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log(sqlConnection.toString());
	}
	private void viewDatabaseMetaData(DatabaseMetaData dmd) throws SQLException {
		log("getCatalogs start");
		ResultSet rs=dmd.getCatalogs();
		while(rs.next()) {
			log(rs.getString(1));
		}
		log("getCatalogs end");
		log(dmd.getUserName());
		log("getSchemas start");
		rs=dmd.getSchemas();
		while(rs.next()) {
			log(rs.getString(1));
		}
		log("getSchemas end");
		log(dmd.getDatabaseProductName());
		log(dmd.getDatabaseMajorVersion());
		log(dmd.getDatabaseMinorVersion());
		log(dmd.getDatabaseProductVersion());
	}
	@Test
	public void propertyToColumn(){
		String propertyName=Utils.propertyToColumn("review_remark", new StringBuilder());
		log(propertyName);
	}
	@Test
	public void testMatchColumn() {
		final List<ColumnInfo> matched=new ArrayList<ColumnInfo>();
		matchColumn(getConnection(getDatabase("DEV_BMS")), new MatchFilter() {
			@Override
			public boolean match(String tableName, String tableType,
					ColumnInfo columnInfo) {
				String columnLabel=columnInfo.getColumnLabel().toUpperCase();
				return columnLabel.contains("NAME")||columnLabel.contains("ID_NO")
						||columnLabel.contains("ID_CARD")
						||columnLabel.contains("IDCARD")
						||columnLabel.contains("BANK")
						||columnLabel.contains("PHONE");
			}
			
			@Override
			public void accept(String tableName, String tableType, ColumnInfo columnInfo) {
				matched.add(columnInfo);
			}
		});
		
		for(ColumnInfo columnInfo:matched){
			log(String.format("%s.%s   %s", columnInfo.getTableName(),columnInfo.getColumnName(),columnInfo.getColumnDetail().getColumnComment()));
		}
	}
	private Connection getConnection(ConnectionInfo connectionInfo){
		try {
			return DBUtils.getConnection(connectionInfo);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	protected Connection getConnection(DBConnectionPair dbConnectionPair){
		try {
			return DBUtils.getConnection(dbConnectionPair);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void matchColumn(Connection connection,MatchFilter matchFilter){
		List<String[]> tableInfos=new ArrayList<String[]>();
		try {
			ResultSet rs=query("select table_name,table_type from information_schema.tables where table_schema='bms_cyb'", connection);
			while(rs.next()){
				String tableName=rs.getString(1);
				String tableType=rs.getString(2);
				String[] tableInfo={tableName,tableType};
				tableInfos.add(tableInfo);
			}
			rs.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for(String[] tableInfo:tableInfos){
			try {
				Map<String, ColumnInfo> columnInfoMap = getTableColumnInfos(tableInfo[0], connection);
				processComment(columnInfoMap, connection);
				for(Map.Entry<String,ColumnInfo> entry:columnInfoMap.entrySet()){
					if(matchFilter.match(tableInfo[0], tableInfo[1], entry.getValue())){
						matchFilter.accept(tableInfo[0], tableInfo[1], entry.getValue());
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
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
	@Test
	public void testColumns(){
//		testColumns(DEV_BMS, "bms_social_insurance_info");
		testColumns(getConnection(getDatabase("DEV_CRM")), "bms_app_person_info_ext");
//		testColumns(DEV_BMS, "bms_loan_base");
//		testColumns(DEV_CREDIT_ZX, "T_PBCCRC_REPORT");
//		testColumns(TEST_MYCAT, "company");
	}
	@Test
	public void testQuery(){
		StringBuilder sql=new StringBuilder();
		sql.append("select id,name,QUANTITY,price from test_product");
		sql.append(" where id>2 and id <7  limit 4");
		try {
			ResultSet rs=query(sql.toString(), getConnection(getDatabase("DEV_MYCAT")));
			int columnCount=rs.getMetaData().getColumnCount();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<columnCount;i++){
				sb.append("\t"+rs.getMetaData().getColumnLabel(i+1));
			}
			log(sb);
			while(rs.next()){
				sb=new StringBuilder();
				for(int i=0;i<columnCount;i++){
					sb.append("\t"+rs.getObject(i+1));
				}
				log(sb);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Test
	public void testResultMapping(){
		Set<String> columns=new HashSet<String>();
		StringBuilder sb=new StringBuilder();
		sb.append("BANK_PHONE, APPLY_BANK_CARD_NO, APPLY_BANK_BRANCH, APPLY_BANK_NAME, APPLY_BANK_BRANCH_ID");
		sb.append(", APPLY_BANK_NAME_ID, CONTRACT_NUM, CONTRACT_SOURCE,CONTRACT_LMT, CONTRACT_TREM");
		sb.append(", LOAN_BANK_ID_BORROW, LOAN_BANK_ID_STILL, contract_type, auth_type, SIGN_TARGET, APPLY_PURPOSE");
		String[] columna=sb.toString().toUpperCase().split("\\s*,\\s*");
		for(String column:columna){
			columns.add(column);
		}
		StringBuilder sb0=new StringBuilder();
		for(String column: columna){
			String result=String.format("<result property=\"%s\" column=\"%s\" />",Utils.columnToProperty(column, sb0), column);
			log(result);
		}
	}
	@Test
	public void testGenerateEntity() {
		Set<String> columns=new HashSet<String>();
		StringBuilder sb=new StringBuilder();
		sb.append("BANK_PHONE, APPLY_BANK_CARD_NO, APPLY_BANK_BRANCH, APPLY_BANK_NAME, APPLY_BANK_BRANCH_ID");
		sb.append(", APPLY_BANK_NAME_ID, CONTRACT_NUM, CONTRACT_SOURCE,CONTRACT_LMT, CONTRACT_TREM");
		sb.append(", LOAN_BANK_ID_BORROW, LOAN_BANK_ID_STILL, contract_type, auth_type, SIGN_TARGET, APPLY_PURPOSE");
		String[] columna=sb.toString().toUpperCase().split("\\s*,\\s*");
		for(String column:columna){
			columns.add(column);
		}
		generateEntity(getConnection(getDatabase("DEV_BMS")), "BMS_LOAN_PRODUCT",columns);
	}
	@Test
	public void testSqlEntity() {
		StringBuilder sql=new StringBuilder();
		sql.append("select lb.owning_branch_id,lb.id loan_base_id,lb.loan_no,ap.name,ap.id_no,ap.id person_id,lp.apply_term,lp.product_cd,lp.product_name,");
		sql.append("la.refuse_date,le.primary_reason,lb.branch_manager_code,lb.branch_manager_name,");
		sql.append("la.creator,la.created_time,la.creator_id,la.modifier,la.modified_time,la.modifier_id,le.blacklist_id,le.secode_reason,le.first_levle_reasons_code as PRIMARY_REASON_CODE,");
		sql.append("le.two_levle_reasons_code as TWO_REASON_CODE,lb.enter_branch,la.created_time as submit_xs_date,");
		sql.append("(SELECT ll.OPERATOR FROM bms_Loan_log ll WHERE ll.loan_No = lb.loan_no  AND ll.OPERATION_TYPE in('115','131') ORDER BY ll.OPERATION_TIME DESC LIMIT 1) reject_person_name,");
		sql.append("(SELECT ll.OPERATOR_CODE FROM bms_Loan_log ll WHERE ll.loan_No = lb.loan_no  AND ll.OPERATION_TYPE in('115','131') ORDER BY ll.OPERATION_TIME DESC LIMIT 1) reject_person_code");
		sql.append(" from bms_loan_base lb ");
		sql.append(" left join bms_loan_audit la on lb.id = la.loan_base_id ");
		sql.append(" left join bms_loan_product lp on lb.id = lp.loan_base_id ");
		sql.append(" left join bms_product p on lp.product_cd = p.code ");
		sql.append(" left join bms_app_person ap on lb.person_id = ap.id ");
		sql.append(" left join bms_loan_ext le on lb.id = le.loan_base_id ");
//		sql.append(" where la.refuse_date &gt; date_add(#{endTime},interval -10 day) ");
		sql.append(" where 0=1 ");
		sql.append(" and ((lb.rtf_node_state = 'XSCS-REJECT' AND lb.rtf_state='XSCS' ) OR (lb.rtf_node_state = 'XSZS-REJECT' AND lb.rtf_state='XSZS')OR (lb.rtf_node_state = 'CSFP-REJECT' AND lb.rtf_state='CSFP')OR (lb.rtf_node_state = 'SQJWH-REJECT')) ");
		sql.append(" and not exists (select id from bms_loan_review lr where lb.loan_no = lr.loan_no) ");
//		try {
//			compareSqlEntity(sql, LoanReviewEntity.class, DEV_BMS);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}
	private void generateEntity(Connection connection,String tableName,Set<String> columns){
		Map<String, ColumnInfo> columnInfos;
		try {
			columnInfos = getTableColumnInfos(tableName, connection);
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
				if(CollectionUtils.isEmpty(columns)||columns.contains(columnInfo.columnName)){
					p.println();
					p.print('\t');
					Class<?> clazz=getJavaType(columnInfo);
					p.print(String.format("private %s %s;", clazz!=null?clazz.getSimpleName():null,Utils.columnToProperty(columnInfo.columnName, sb)));
				}
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
	private void compareSqlEntity(StringBuilder sql,Class<?> entityClass,Connection connection) throws SQLException{
		compareEntity(getSqlColumnInfos(sql, connection), entityClass);
	}
	
	private void compareEntity(Class<?> entityClass) throws SQLException{
		Table table=entityClass.getAnnotation(Table.class);
		compareEntity(entityClass, table.name(), getDatabase("DEV_BMS"));
	}
	protected void compareEntity(Class<?> entityClass,String tableName,DBConnectionPair dbConnectionPair) throws SQLException {
		Map<String, ColumnInfo> columnInfos=getTableColumnInfos(tableName, getConnection(dbConnectionPair));
		compareEntity(columnInfos, entityClass);
	}
	protected void compareEntity(Class<?> entityClass,String tableName,Connection connection) throws SQLException {
		Map<String, ColumnInfo> columnInfos=getTableColumnInfos(tableName,connection);
		compareEntity(columnInfos, entityClass);
	}
	protected void compareEntity(Map<String, ColumnInfo> columnInfos,Class<?> entityClass) {
		BeanWrapperImpl wrapper=new BeanWrapperImpl(entityClass);
		StringBuilder sb=new StringBuilder();
		PropertyDescriptor[] propertyDescriptors=wrapper.getPropertyDescriptors();
		Set<String> prpperties=new HashSet<String>();
		String entityName=entityClass.getSimpleName();
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
				log(entityName+" miss "+columnName+"  "+propertyName);
				continue;
			}
			prpperties.add(columnName);
			ColumnInfo columnInfo=columnInfos.get(columnName);
			String columnClassName=columnInfo.columnClassName;
			Class<?> propertyType=propertyDescriptor.getPropertyType();
			if(columnInfo.precision>=21845){
				log(String.format("%s %s %s", entityName,columnName,columnClassName));
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
				log(entityName+" "+propertyDescriptor.getName()+"  "+columnClassName+" not match "+propertyType);
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
	
	
	private void testColumns(Connection connection,String tableName){
		try {
			Map<String, ColumnInfo> columnInfos=getTableColumnInfos(tableName, connection);
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
		private ColumnDetail columnDetail;
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
		public ColumnDetail getColumnDetail() {
			return columnDetail;
		}
		public void setColumnDetail(ColumnDetail columnDetail) {
			this.columnDetail = columnDetail;
		}
		
	}
	protected Map<String, ColumnInfo> getTableColumnInfos(String tableName,Connection connection) throws SQLException{
		StringBuilder sql=new StringBuilder();
		sql.append("SELECT * FROM  ");
		sql.append(tableName);
		sql.append(" where 0=1");
		return getSqlColumnInfos(sql,connection);
	}
	private Map<String, ColumnInfo> getSqlColumnInfos(StringBuilder sql,Connection connection) throws SQLException{
		ResultSet rs=query(sql.toString(), connection);
		log("result count:"+count(rs));
		return getSqlColumnInfos(rs);
	}
	private Map<String, ColumnInfo> getSqlColumnInfos(ResultSet rs) throws SQLException{
		ResultSetMetaData rsMetaData=rs.getMetaData();
		Map<String, ColumnInfo>  columnInfos=convertToColumnInfos(rsMetaData);
		return columnInfos;
	}
	private static class ColumnDetail{
		private String columnComment;
		private String columnName;
		private String tableName;
		public String getColumnComment() {
			return columnComment;
		}
		public void setColumnComment(String columnComment) {
			this.columnComment = columnComment;
		}
		public String getColumnName() {
			return columnName;
		}
		public void setColumnName(String columnName) {
			this.columnName = columnName;
		}
		public String getTableName() {
			return tableName;
		}
		public void setTableName(String tableName) {
			this.tableName = tableName;
		}
		
		
	}
	private void processComment(Map<String, ColumnInfo>  columnInfos,Connection connection){
		Set<String> tableNames=new HashSet<String>();
		String tableSchema="";
		for(Map.Entry<String, ColumnInfo> entry:columnInfos.entrySet()){
			ColumnInfo columnInfo=entry.getValue();
			tableNames.add(columnInfo.getTableName());
			tableSchema=columnInfo.getCatalogName();
		}
		StringBuilder sb=new StringBuilder();
		sb.append("select COLUMN_COMMENT,COLUMN_NAME,TABLE_NAME,TABLE_SCHEMA from information_schema.columns ");
		sb.append(" where table_schema='"+tableSchema+"' and table_name in("+StringUtils.collectionToDelimitedString(tableNames, ",", "'", "'")+")");
		log(sb);
		Map<String,ColumnDetail> columnDetails=new HashMap<>();
		try {
			ResultSet rs=query(sb.toString(), connection);
			while(rs.next()){
				ColumnDetail columnDetail=new ColumnDetail();
				columnDetail.setColumnComment(rs.getString(1));
				columnDetail.setColumnName(rs.getString(2));
				columnDetail.setTableName(rs.getString(3));
				columnDetails.put(columnDetail.getTableName()+"."+columnDetail.getColumnName(),columnDetail);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log("columnDetails.size:"+columnDetails.size());
		for(Map.Entry<String, ColumnInfo> entry:columnInfos.entrySet()){
			ColumnInfo columnInfo=entry.getValue();
			ColumnDetail columnDetail=columnDetails.get(columnInfo.getTableName()+"."+columnInfo.getColumnName());
			columnInfo.setColumnDetail(columnDetail);
		}
	}
	private Map<String, ColumnInfo> convertToColumnInfos(ResultSetMetaData rsMetaData) throws SQLException{
		int count=rsMetaData.getColumnCount();
		Map<String,ColumnInfo> columnInfoMap=new HashMap<String,ColumnInfo>();
		for(int column=1;column<=count;column++){
			ColumnInfo columnInfo=new ColumnInfo();
			columnInfo.index=column;
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
			columnInfoMap.put(rsMetaData.getColumnLabel(column).toUpperCase(), columnInfo);
		}
		return columnInfoMap;
	}
	private int count(ResultSet rs) throws SQLException{
		int count=0;
		while(rs.next()){
			count++;
		}
		return count;
	}
	private ResultSet query(String sql,Connection connection) throws SQLException{
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
		return statement.executeQuery(sql);
	}
	@Test
	public void testMySQL() {
		testConnection(getDatabase("DEV_BMS"));
	}
	@Test
	public void testOracel() {
		testConnection(getDatabase("DEV_CREDIT_ZX"));
	}
	private void testConnection(DBConnectionPair connectionInfo) {
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
