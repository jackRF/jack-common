package org.jack.common;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.jack.common.util.IOUtils;
import org.junit.Test;

public class IOTest extends BaseTest {
	/**
	 * 比较配置文件的差异
	 * @throws IOException
	 */
	@Test
	public void testPropertiesDiff() throws IOException {
		Properties properties=IOUtils.loadProperties(new File("D:\\data\\conf\\bms-biz\\bms_biz配置 9.29(1).16"));
		Properties properties2=IOUtils.loadProperties(new File("D:\\data\\conf\\bms-biz\\bms_biz 2配置.txt"));
		
		Enumeration<Object> enumerationKey=properties.keys();
		while(enumerationKey.hasMoreElements()){
			Object key=enumerationKey.nextElement();
			if(!properties2.containsKey(key)){
				log(key);
			}
		}
	}
}
