package org.jack.common.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.BaseTest;
import org.jack.common.algorithm.HashDigest;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.junit.Test;

public class MavenTest extends BaseTest{
	@Test
	public void cleanCheckFile() throws IOException {
		// TODO Auto-generated method stub
		
		File file=new File("D:\\tmp\\tem2.txt");
		IOUtils.processText(file, new Task<String>() {
			
			@Override
			public void toDo(String t) {
				File f=new File(t);
				if(f.exists()){
					f.delete();
				}
				f=new File(t+".sha1");
				if(f.exists()){
					f.delete();
				}
				f=new File(t+".md5");
				if(f.exists()){
					f.delete();
				}
			}
		});
	}
	@Test
	public void checkRepository() {
		File repositoryDir=new File("E:\\maven\\repository");
		repositoryDir=new File(repositoryDir,"org\\apache\\maven");
		final List<File> failFiles=new ArrayList<File>();
		final List<File> catchFiles=new ArrayList<File>();
		checkDir(repositoryDir,new Processor() {
			
			@Override
			public void onFile(File file) {
				try {
					if(!checkFile(file)){
						failFiles.add(file);
					}
				} catch (IOException e) {
					e.printStackTrace();
					catchFiles.add(file);
				}
			}
			
			@Override
			public boolean onDir(File file) {
				return true;
			}
		});
		for(File file:failFiles){
			log(file);
		}
	}
	
	private boolean checkFile(File file) throws IOException{
		String name=file.getName();
		int lindex=name.lastIndexOf(".");
		if(lindex>0){
			String ext=name.substring(lindex);
			if(!(ext.equalsIgnoreCase(".jar")||ext.equalsIgnoreCase(".pom"))){
//				if(!(name.endsWith(".sha1")||name.endsWith(".md5")||name.endsWith(".repositories"))){
//					return false;
//				}
				return true;
			}
		}else{
			return true;
		}
		File sha1File=new File(file.getCanonicalPath()+".sha1");
		File md5File=new File(file.getCanonicalPath()+".md5");
		if(sha1File.exists()||md5File.exists()){
			if(sha1File.exists()){
				String sha1=HashDigest.getFileSHA1(file);
				String sha1dest=IOUtils.readText(sha1File);
				if(!sha1.equals(sha1dest.trim())){
					return false;
				}
			}else{
				String md5=HashDigest.getFileMD5(file);
				String md5dest=IOUtils.readText(md5File);
				if(!md5.equals(md5dest.trim())){
					return false;
				}
			}
			
		}else{
			return false;
		}
		
		return true;
	}
	private void checkDir(File dir,Processor processor) {
		File[] children=dir.listFiles();
		for(File file:children){
			if(file.isDirectory()){
				if(processor.onDir(file)){
					checkDir(file,processor);
				}
				continue;
			}
			processor.onFile(file);
		}
	}
	public static  interface Processor{
		void onFile(File file);
		boolean onDir(File file);
	}
}
