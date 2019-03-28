package org.jack.common.ssh2;

import org.jack.common.util.net.ConnectionPair;

public class WebDeploy {
	private final ConnectionPair connectionPair;
	private String artifactId;
	private String version;
	private String sourcePath;
	private String remotePath;
	private String containerPath;
	public WebDeploy(ConnectionPair connectionPair) {
		this.connectionPair=connectionPair;
	}
	public ConnectionPair getConnectionPair() {
		return connectionPair;
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
	public String getContainerPath() {
		return containerPath;
	}
	public void setContainerPath(String containerPath) {
		this.containerPath = containerPath;
	}
	public String getDeployTarget() {
		return artifactId+".war";
	}
	public boolean useRemotePath() {
		return remotePath!=null&&!remotePath.isEmpty();
	}
}
