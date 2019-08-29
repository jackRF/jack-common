package org.jack.common.orm;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Table;

import org.jack.common.core.ClassLoaderFactory;
import org.jack.common.core.ClassLoaderFactory.Repository;
import org.jack.common.core.ClassLoaderFactory.RepositoryType;
import org.jack.common.util.ClassScaner;
import org.jack.common.util.Utils;
import org.jack.common.util.net.DBConnectionPair;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

public class MapperHleperTest extends DBTest {
	private File tongcProjectDir=new File("D:\\Projects\\com\\tongc-soft");
	protected Project bms=loadMavenProject(tongcProjectDir,"bms-trade");
	protected Project cfs=loadMavenProject(tongcProjectDir,"CFS");
	protected Project bds=loadMavenProject(tongcProjectDir,"BDS");
	protected Project rule=loadMavenProject(tongcProjectDir,"rule_gate");
	@Test
	public void testScan() {
		ClassScaner classScaner=new ClassScaner();
		try {
			classScaner.setClassLoader(getTongcProjectClassLoader(bms));
			Set<Class<?>> classes=classScaner.doScan("com.ymkj.bms.biz.entity");
			for(Class<?> clazz:classes){
				log(clazz);
			}
			compare(classes, getConnection(getDatabase("DEV_BMS")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void compare(Set<Class<?>> classes, Connection connection) throws SQLException {
		List<String> tableNames=getAllTables(connection);
		Map<Class<?>,String> unMatch=new HashMap<Class<?>,String>();
		for(Class<?> clazz:classes){
			if(!clazz.getSimpleName().endsWith("Entity")){
				log("ignore "+clazz);
				continue;
			}
			try{
				clazz.newInstance();
			}catch(Exception e){
				log("clazz instance fail:"+clazz);
				continue;
			}
			
			String tableName=match(clazz, tableNames);
			if(tableName!=null){
				log("compareEntity:"+clazz+"|"+tableName);
				compareEntity(clazz, tableName, connection);
			}else{
				unMatch.put(clazz, tableName);
			}
		}
		log("unMatch:"+unMatch);
	}
	private String match(Class<?> clazz,List<String> tableNames){
		Table table=clazz.getAnnotation(Table.class);
		if(table!=null){
			return table.name();
		}
		String simpleName=clazz.getSimpleName();
		float same=-1;
		String useTableName=null;
		for(String tableName: tableNames){
			if(simpleName.equalsIgnoreCase(tableName)){
				return tableName;
			}
			float temp=Utils.Levenshtein(simpleName,tableName);
			if(temp>same){
				same=temp;
				useTableName=tableName;
			}
		}
		return useTableName;
	}
	@Test
	public void testsame() {
//		log(Utils.Levenshtein("bms_loan_base", "BMSLoanBaseEntity"));
//		log(Utils.Levenshtein("bms_loan_base", "BMSAppPersonInfoEntity"));
		log(Utils.Levenshtein("bms_loan_audit", "AFirstTrialHangEntity"));
		log(Utils.Levenshtein("bms_loan_audit", "BMSLoanAuditEntity"));
		log(Utils.Levenshtein("bms_loan_audit", "BMSLoatAuditEntity"));
	}
	
	@Test
	public void testClass(){
		String className="com.ymkj.bms.biz.entity.sign.BMSLoanChannelLockTargetEntity";
//		className="com.ymkj.bms.biz.entity.master.PettyLoanCustomerEntity";
		Class<?> clazz=tongcProjectClass(bms,className);
		Table table=clazz.getAnnotation(Table.class);
		log(table.name());
	}
	protected Class<?> tongcProjectClass(Project project,String className) {
		try {
			ClassLoader classLoader=getTongcProjectClassLoader(project);
			Class<?> clazz=classLoader.loadClass(className);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	protected List<Class<?>> tongcProjectClass(Project project,List<String> classNames) {
		
		List<Class<?>> classes=new ArrayList<Class<?>>();
		try {
			ClassLoader classLoader=getTongcProjectClassLoader(project);
			for(String className:classNames){
				Class<?> clazz=classLoader.loadClass(className);
				classes.add(clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return classes;
	}
	protected ClassLoader getTongcProjectClassLoader(Project project) throws Exception {
		List<File> list=new ArrayList<File>();
		File pfile=new File(tongcProjectDir, project.getName());
		for(String module:project.getModules()){
			list.add(new File(pfile,module+"/target/classes"));
		}
		String location="E:/maven/repository/com/ymkj/base-core-biz/0.0.1-SNAPSHOT/base-core-biz-0.0.1-SNAPSHOT.jar";
		Repository repository=new Repository(location,RepositoryType.JAR);
		location="E:/maven/repository/com/ymkj/base-core-common/0.0.1-SNAPSHOT/base-core-common-0.0.1-SNAPSHOT.jar";
		Repository repository2=new Repository(location,RepositoryType.JAR);
		location="E:/maven/repository/com/ymkj/base-core-biz-api/0.0.1-SNAPSHOT/base-core-biz-api-0.0.1-SNAPSHOT.jar";
		Repository repository3=new Repository(location,RepositoryType.JAR);
		return ClassLoaderFactory.createClassLoader(list.toArray(new File[list.size()]), new Repository[]{repository,repository2,repository3}, null);
	}
	@Test
	public void testProject() {
		log(JSON.toJSONString(rule));
	}
	@Test
	public void testInsertCondition() {
		String cls="IF_LOCAL_REGISTER,VERSION,IS_DELETE";
		insertCondition(getDatabase("DEV_BMS"), "bms_tm_app_salary_loan_info",cls.split(","));
	}
	private void insertCondition(DBConnectionPair dbConnectionPair,String tableName,String...columns) {
		StringBuilder keysb=new StringBuilder();
		StringBuilder valuesb=new StringBuilder();
		List<String> types=new ArrayList<String>();
		List<String> typeCheck=new ArrayList<String>();
		try {
			Map<String, ColumnInfo> columnInfos=getTableColumnInfos(tableName,getConnection(dbConnectionPair));
			if(columns==null||columns.length==0){
				for(Map.Entry<String, ColumnInfo> entry:columnInfos.entrySet()){
					ColumnInfo columnInfo=entry.getValue();
					buildInsertCondition(columnInfo,keysb,valuesb);
					String property=Utils.columnToProperty(columnInfo.getColumnName(), new StringBuilder());
					types.add(property);
					if(!"java.lang.String".equals(columnInfo.getColumnClassName())){
						typeCheck.add(property);
					}
				}
			}else{
				for(String column:columns){
					ColumnInfo columnInfo=columnInfos.get(column.toUpperCase());
					buildInsertCondition(columnInfo,keysb,valuesb);
					String property=Utils.columnToProperty(columnInfo.getColumnName(), new StringBuilder());
					types.add(property);
					if(!"java.lang.String".equals(columnInfo.getColumnClassName())){
						typeCheck.add(property);
					}
				}
			}
			log(keysb.toString());
			log("-----------------------------");
			log(valuesb.toString());
			log("-----------------------------");
			log(types);
			log(typeCheck);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	private void buildInsertCondition(ColumnInfo columnInfo,StringBuilder keysb,StringBuilder valuesb){
		StringBuilder sbc=new StringBuilder();
		String columnName=columnInfo.getColumnName();
		String property=Utils.columnToProperty(columnName, sbc);
		String conditionIf=null;
		if("java.lang.String".equals(columnInfo.getColumnClassName())){
			conditionIf=String.format("<if test=\"%s !=null and %s !=''\">\n", property,property);
			
		}else{
			conditionIf=String.format("<if test=\"%s !=null\">\n",property);
		}
		keysb.append(conditionIf);
		valuesb.append(conditionIf);
		keysb.append("\t,"+columnName+"\n");
		valuesb.append("\t,#{"+property+"}\n");
		keysb.append("</if>\n");
		valuesb.append("</if>\n");
	}
	private Project loadMavenProject(File workspace,String projectDirName){
		Project project=new Project();
		project.setName(projectDirName);
		List<String> modules=new ArrayList<String>();
		project.setModules(modules);
		File projectDir=new File(workspace,projectDirName);
		File[] list=projectDir.listFiles();
		for(File file:list){
			if(file.isDirectory()&&(new File(file,"pom.xml")).exists()){
				String name=file.getName();
				if(!name.startsWith(".")){
					modules.add(name);
				}
			}
		}
		return project;
	}
}
