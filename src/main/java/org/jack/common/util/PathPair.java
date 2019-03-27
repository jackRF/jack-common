package org.jack.common.util;

public class PathPair {
	private String source;
	private String dest;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDest() {
		return dest;
	}
	public void setDest(String dest) {
		this.dest = dest;
	}
	public String getSourceFilePath(String fileName){
		return getChildFilePath(source, fileName);
	}
	public String getDestFilePath(String fileName){
		return getChildFilePath(dest, fileName);
	}
	public static String getChildFilePath(String dir,String fileName) {
		if(dir==null||dir.isEmpty()){
			return fileName;
		}
		int i=dir.length()-1;
		while(i>=0){
			if(dir.charAt(i)=='/'||dir.charAt(i)=='\\'){
				i--;
				continue;
			}
			break;
		}
		String use=dir.substring(0,i+1);
		if(fileName.startsWith("/")){
			return use+fileName;
		}else{
			return use+"/"+fileName;
		}
	}
}
