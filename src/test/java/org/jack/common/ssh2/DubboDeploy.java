package org.jack.common.ssh2;

import org.jack.common.util.PathPair;
import org.jack.common.util.net.ConnectionPair;


public class DubboDeploy extends AbstractDeploy {
	private String sourcePath;
	private String remotePath;
	public DubboDeploy(ConnectionPair connectionPair) {
		super(connectionPair);
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	public String getRemotePath() {
		return remotePath;
	}

	public void setRemotePath(String remotePath) {
		this.remotePath = remotePath;
	}
	@Override
	public String getDeployTarget() {
		if(version==null||version.isEmpty()){
			return artifactId+"-deployment.zip";
		}
		return artifactId+"-"+version+".zip";
	}

	@Override
	protected PathPair getPathPair() {
		PathPair pathPair=new PathPair();
		pathPair.setSource(PathPair.getChildFilePath(projectPath, sourcePath));
		pathPair.setDest(remotePath);
		return pathPair;
	}

	@Override
	protected String useStartCommand() {
		if(remotePath!=null&&!remotePath.isEmpty()){
			return "cd "+remotePath+" && sh auto_deploy.sh";
		}
		return "sh auto_deploy.sh";
	}

	@Override
	public String useGrep() {
		return artifactId;
	}
}
