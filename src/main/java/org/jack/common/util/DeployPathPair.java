package org.jack.common.util;

public class DeployPathPair extends PathPair {
	private boolean version;
	public DeployPathPair(boolean version) {
		this.version=version;
	}
	@Override
	public String getDestFilePath(String fileName) {
		if(version&&!fileName.toLowerCase().endsWith(".zip")){
			int i1=fileName.lastIndexOf(".");
			int i2=fileName.lastIndexOf("-");
			if("SNAPSHOT".equals(fileName.substring(i2+1, i1).toUpperCase())){
				String useFileName=fileName.substring(0, i2);
				i2=useFileName.lastIndexOf("-");
				fileName=useFileName.substring(0, i2)+fileName.substring(i1);
			}else{
				fileName=fileName.substring(0, i2)+fileName.substring(i1);
			}
		}
		return super.getDestFilePath(fileName);
	}
}
