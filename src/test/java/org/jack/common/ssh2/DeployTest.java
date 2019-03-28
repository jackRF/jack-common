package org.jack.common.ssh2;


import org.jack.common.util.PathPair;
import org.jack.common.util.net.AuthenticatePair;
import org.jack.common.util.net.ConnectionPair;
import org.jack.common.util.net.NetAddressPair;
import org.junit.Test;

import ch.ethz.ssh2.Connection;

public class DeployTest extends SSH2Test {
	private  static final ConnectionPair DEV_BMS;
	private  static final ConnectionPair DEV_RULE;
	private  static final ConnectionPair DEV_CFS;
	private  static final ConnectionPair DEV_BDS;
	static{
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.235.9"));
			connectionPair.setAuthenticate(new AuthenticatePair("root", "zd,123"));
			DEV_BMS=connectionPair;
		}
		{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.50"));
			connectionPair.setAuthenticate(new AuthenticatePair("rule", "zd,123"));
			DEV_RULE=connectionPair;
		}{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.235.172"));
			connectionPair.setAuthenticate(new AuthenticatePair("cfs", "zd,123"));
			DEV_CFS=connectionPair;
		}{
			ConnectionPair connectionPair=new ConnectionPair();
			connectionPair.setNetAddress(new NetAddressPair("172.16.230.49"));
			connectionPair.setAuthenticate(new AuthenticatePair("root", "zd,123"));
			DEV_BDS=connectionPair;
		}
	}
	@Test
	public void testDeployBms() {
		DubboDeploy dubboDeploy=new DubboDeploy(DEV_BMS);
		dubboDeploy.setArtifactId("bms-biz");
		dubboDeploy.setRemotePath("/home/bms/bms_biz");
		dubboDeploy.setSourcePath("D:/Projects/com/tongc-soft/bms-trade/bms-biz/target");
		deploy(dubboDeploy);
	}
	@Test	
	public void testDeployCfs() {
		WebDeploy webDeploy=new WebDeploy(DEV_CFS);
		webDeploy.setArtifactId("cfs-web-boss");
		webDeploy.setContainerPath("/home/cfs/apache-tomcat-7.0.69");
		webDeploy.setSourcePath("D:/Projects/com/tongc-soft/CFS/cfs-web-boss/target");
		deploy(webDeploy);
		
	}
	
	@Test
	public void testDeployRule() {
		DubboDeploy dubboDeploy=new DubboDeploy(DEV_RULE);
		dubboDeploy.setArtifactId("rule-gate-biz");
		dubboDeploy.setSourcePath("E:/Term/rule_gate/rule-gate-biz/target");
		deploy(dubboDeploy);
	}
	@Test
	public void testDeployRulePack() {
		Connection connection=RemoteCommandUtils.login(DEV_RULE);
		PathPair pathPair=new PathPair();
		pathPair.setSource("D:/data/rule");
		pathPair.setDest("rule_conf");
		compareUpdateDir(connection, pathPair);
		connection.close();
		testDeployRule();
	}
	@Test
	public void testDeployBds() {
		DubboDeploy dubboDeploy=new DubboDeploy(DEV_BDS);
		dubboDeploy.setArtifactId("bds-biz");
		dubboDeploy.setRemotePath("/home");
		dubboDeploy.setSourcePath("D:/Projects/com/tongc-soft/BDS/bds-biz/target");
		deploy(dubboDeploy);
	}
	private void deploy(WebDeploy deploy) {
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		ProcessInfo processInfo=grepParse(execute(conn, " ps -ef|grep java"), deploy.getContainerPath());
		if(processInfo!=null){
			execute(conn, "kill -9 "+processInfo.getPid());
		}
		PathPair pathPair=new PathPair();
		pathPair.setSource(deploy.getSourcePath());
		pathPair.setDest(deploy.getRemotePath());
		RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),pathPair, conn);
		log("----------restart--------");
		if(deploy.useRemotePath()){
			execute(conn, "cd "+deploy.getRemotePath()+" && sh auto_deploy.sh");
		}else{
			execute(conn, "sh auto_deploy.sh");
		}
		conn.close();
	}
	private void deploy(DubboDeploy deploy){
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		ProcessInfo processInfo=grepParse(execute(conn, "ps -ef|grep dubbo"), deploy.getArtifactId());
		if(processInfo!=null){
			execute(conn, "kill -9 "+processInfo.getPid());
		}
		PathPair pathPair=new PathPair();
		pathPair.setSource(deploy.getSourcePath());
		pathPair.setDest(deploy.getRemotePath());
		RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),pathPair, conn);
		log("----------restart--------");
		if(deploy.useRemotePath()){
			execute(conn, "cd "+deploy.getRemotePath()+" && sh auto_deploy.sh");
		}else{
			execute(conn, "sh auto_deploy.sh");
		}
		conn.close();
	}
	protected void update(DubboDeploy deploy){
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		PathPair pathPair=new PathPair();
		pathPair.setSource(deploy.getSourcePath());
		pathPair.setDest(deploy.getRemotePath());
		RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),pathPair, conn);
	}
	protected void update(WebDeploy deploy){
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		PathPair pathPair=new PathPair();
		pathPair.setSource(deploy.getSourcePath());
		pathPair.setDest(deploy.getRemotePath());
		RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),pathPair, conn);
	}
}
