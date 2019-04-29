package org.jack.common.ssh2;

import org.jack.common.util.PathPair;
import org.jack.common.util.net.ConnectionPair;

public abstract class AbstractDeploy {
	protected final ConnectionPair connectionPair;
	protected String projectPath;
	protected String artifactId;
	protected String version;
	protected boolean restart;
	protected abstract PathPair getPathPair();
	protected abstract String getDeployTarget();
	protected abstract String useStartCommand();
	
	public boolean isRestart() {
		return restart;
	}
	public void setRestart(boolean restart) {
		this.restart = restart;
	}
	public String getPsCommand(){
		return "ps -ef|grep java";
	}
	public abstract String useGrep();
	public AbstractDeploy(ConnectionPair connectionPair) {
		this.connectionPair=connectionPair;
	}
	public ConnectionPair getConnectionPair() {
		return connectionPair;
	}
	public String getProjectPath() {
		return projectPath;
	}
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
}
