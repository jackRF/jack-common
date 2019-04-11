package org.jack.common.ssh2;

import org.jack.common.util.DeployPathPair;
import org.jack.common.util.PathPair;
import org.jack.common.util.net.ConnectionPair;

public class WebDeploy extends AbstractDeploy{
	private String sourcePath;
	private String containerPath;
	public WebDeploy(ConnectionPair connectionPair) {
		super(connectionPair);
	}
	public String getSourcePath() {
		if(sourcePath==null||sourcePath.isEmpty()){
			return getArtifactId()+"/target";
		};
		return sourcePath;
	}
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	public String getContainerPath() {
		return containerPath;
	}
	public void setContainerPath(String containerPath) {
		this.containerPath = containerPath;
	}
	public String getDeployTarget() {
		if(version!=null&&!version.isEmpty()){
			return String.format("%s-%s.war", artifactId,version);
		}
		return artifactId+".war";
	}
	@Override
	protected PathPair getPathPair() {
		PathPair pathPair=new DeployPathPair(version!=null&&!version.isEmpty());
		pathPair.setSource(PathPair.getChildFilePath(projectPath, getSourcePath()));
		pathPair.setDest(PathPair.getChildFilePath(containerPath, "webapps"));
		return pathPair;
	}
	@Override
	protected String useStartCommand() {
		return String.format("cd %s && sh bin/startup.sh", containerPath);
	}
	@Override
	public String useGrep() {
		return containerPath;
	}
}
