package org.jack.common;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class FileTest extends BaseTest {
	
	@Test
	public void testMavenInvalidFile() {
		File dir=new File("E:\\maven\\repository");
		List<File> destFiles=new ArrayList<File>();
		processFile(dir, new MavenInvalidFileFilter(), destFiles);
	}
	private void processFile(File dir,FileFilter filter, Collection<File> destFiles){
		File[] files=dir.listFiles();
		for(File file:files){
			if(file.isDirectory()){
				processFile(file,filter,destFiles);
			}else if(file.isFile()){
				if(filter.accept(file)){
					log(file);
					destFiles.add(file);
				}
			}
		}
	}
	private static class MavenInvalidFileFilter implements FileFilter{
		private Set<String> extType=new HashSet<String>();
		public MavenInvalidFileFilter() {
			extType.add("md5");
			extType.add("sha1");
			extType.add("jar");
			extType.add("pom");
			extType.add("xml");
			extType.add("zip");
			extType.add("war");
			extType.add("repositories");
			extType.add("properties");
		}
		@Override
		public boolean accept(File file) {
			
			String name=file.getName();
//			if(name.contains("lastUpdated")||name.contains("-in-progress")){
//				return true;
//			}else{
//				return false;
//			}
			int i=name.lastIndexOf(".");
			if(i<0){
				return true;
			}else if(!extType.contains(name.substring(i+1).toLowerCase())){
				return true;
			}else if(name.contains("lastUpdated")||name.contains("-in-progress")){
				return true;
			}
			return false;
		}
	}
}
