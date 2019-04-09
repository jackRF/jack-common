package orm;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jack.common.util.Utils;
import org.jack.common.util.net.DBConnectionPair;
import org.junit.Test;

public class MapperHleperTest extends DBTest {
	@Test
	public void testInsertCondition() {
		String cls="IF_LOCAL_REGISTER,VERSION,IS_DELETE";
		insertCondition(DEV_BMS, "bms_tm_app_salary_loan_info",cls.split(","));
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
}
