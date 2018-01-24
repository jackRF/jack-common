package org.jack.common;

import java.sql.SQLException;

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
	public void testMySql() {
		try {
			DBUtils.getConnection(DEV_BMS);
			log("Connection success");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testOracel() {
		try {
			DBUtils.getConnection(DEV_CREDIT_ZX);
			log("Connection success");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
