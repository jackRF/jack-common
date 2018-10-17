package org.jack.common;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class IOTest extends BaseTest {
	@Test
	public void testRepetitive() throws IOException {
		File file=new File("D:\\data\\conf\\bms-biz\\1D535670-4998-44c4-A3D1-694B2A2BB70A.txt");
		Set<String> keys=new HashSet<String>();
		IOUtils.processText(file, new Task<String>(){

			@Override
			public void toDo(String line) {
				line=StringUtils.trimWhitespace(line);
				if(!StringUtils.hasText(line)||line.startsWith("#")){
					return;
				}
				String key=StringUtils.trimWhitespace(line.split("=")[0]);
				if(keys.contains(key)){
					log(key);
				}else{
					keys.add(key);
				}
				
			}});
	}
	/**
	 * 比较配置文件的差异
	 * @throws IOException
	 */
	@Test
	public void testPropertiesDiff() throws IOException {
		Properties properties=IOUtils.loadProperties(new File("D:\\data\\conf\\bms-biz\\bms_biz dev配置.txt"));
		Properties properties2=IOUtils.loadProperties(new File("D:\\data\\conf\\bms-biz\\6AF5DE18-2127-4ad0-A353-A55337DCAF2F.txt"));
		
		Enumeration<Object> enumerationKey=properties.keys();
		while(enumerationKey.hasMoreElements()){
			Object key=enumerationKey.nextElement();
			if(!properties2.containsKey(key)){
				log(key);
			}
		}
	}
	
}
