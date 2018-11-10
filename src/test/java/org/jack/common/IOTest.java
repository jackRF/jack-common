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
//		File file=new File("D:\\data\\conf\\bms-biz\\1D535670-4998-44c4-A3D1-694B2A2BB70A.txt");
		File file=new File("D:\\data\\compare\\bms_biz 3.txt");
		final Set<String> keys=new HashSet<String>();
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
		File parent=new File("D:\\data\\compare");
		Properties properties=IOUtils.loadProperties(new File(parent,"12E78C0A-C63C-42fd-A209-E59B1CDDC4F0111.txt"));
		Properties properties2=IOUtils.loadProperties(new File(parent,"2C1ECD6B-2DFE-4afd-9596-81FA7CE52F3E333.txt"));
		
		Enumeration<Object> enumerationKey=properties.keys();
		while(enumerationKey.hasMoreElements()){
			Object key=enumerationKey.nextElement();
			if(!properties2.containsKey(key)){
				log(key+"="+properties.getProperty((String)key));
			}
			
		}
	}
	
}
