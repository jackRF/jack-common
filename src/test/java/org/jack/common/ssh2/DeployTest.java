package org.jack.common.ssh2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jack.common.util.PathPair;
import org.jack.common.util.net.AuthenticatePair;
import org.jack.common.util.net.ConnectionPair;
import org.jack.common.util.net.NetAddressPair;
import org.junit.Test;

import ch.ethz.ssh2.Connection;

public class DeployTest extends SSH2Test {
	
	private IServerConfig serverConfig=new ServerConfig();
	
	@Test
	public void testDeployJob() {
		WebDeploy deploy=new WebDeploy(serverConfig.getServer("BMS", "DEV"));
		deploy.setProjectPath("D:/Projects/com/tongc-soft/preCreditJob");
		deploy.setContainerPath("/usr/local/apache-tomcat-8.5.38");
		deploy.setArtifactId("job-admin");
		deploy.setVersion("2.0.2-SNAPSHOT");
//		if(!compile(deploy.getProjectPath())){
//			return;
//		}
		deploy(deploy);
		deploy.setArtifactId("job-executor");
		deploy.setVersion("2.0.2-SNAPSHOT");
//		if(!compile(deploy.getProjectPath())){
//			return;
//		}
		deploy(deploy);
	}
	@Test
	public void testDeployBms() {
		DubboDeploy dubboDeploy=new DubboDeploy(serverConfig.getServer("BMS", "DEV"));
		dubboDeploy.setArtifactId("bms-biz");
		dubboDeploy.setRemotePath("/home/bms/bms_biz");
		dubboDeploy.setSourcePath("D:/Projects/com/tongc-soft/bms-trade/bms-biz/target");
		deploy(dubboDeploy);
	}
	@Test	
	public void testDeployCfs() {
		WebDeploy webDeploy=new WebDeploy(serverConfig.getServer("CFS", "DEV"));
		webDeploy.setArtifactId("cfs-web-boss");
		webDeploy.setContainerPath("/home/cfs/apache-tomcat-7.0.69");
		webDeploy.setSourcePath("D:/Projects/com/tongc-soft/CFS/cfs-web-boss/target");
		deploy(webDeploy);
		
	}
	
	@Test
	public void testDeployRule() {
		DubboDeploy dubboDeploy=new DubboDeploy(serverConfig.getServer("RULE", "DEV"));
		dubboDeploy.setArtifactId("rule-gate-biz");
		dubboDeploy.setSourcePath("D:/Projects/com/tongc-soft/rule_gate/rule-gate-biz/target");
//		dubboDeploy.restart=true;
		deploy(dubboDeploy);
	}
	@Test
	public void testDeployRulePack() {
		Connection connection=RemoteCommandUtils.login(serverConfig.getServer("RULE", "DEV"));
		PathPair pathPair=new PathPair();
		pathPair.setSource("D:/data/rule");
		pathPair.setDest("rule_conf");
		compareUpdateDir(connection, pathPair);
		connection.close();
		testDeployRule();
	}
	@Test
	public void testDeployBds() {
		DubboDeploy dubboDeploy=new DubboDeploy(serverConfig.getServer("BDS", "DEV"));
		dubboDeploy.setArtifactId("bds-biz");
		dubboDeploy.setRemotePath("/home");
		dubboDeploy.setSourcePath("D:/Projects/com/tongc-soft/BDS/bds-biz/target");
		deploy(dubboDeploy);
	}
	private void deploy(AbstractDeploy deploy){
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		ProcessInfo processInfo=grepParse(execute(conn, deploy.getPsCommand()), deploy.useGrep());
		if(processInfo!=null){
			execute(conn, "kill -9 "+processInfo.getPid());
		}
		if(!deploy.isRestart()){
			RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),deploy.getPathPair(), conn);
		}
		log("----------restart--------");
		execute(conn,deploy.useStartCommand());
		conn.close();
	}
	protected void update(AbstractDeploy deploy){
		Connection conn=RemoteCommandUtils.login(deploy.getConnectionPair());
		RemoteCommandUtils.uploadFile(deploy.getDeployTarget(),deploy.getPathPair(), conn);
	}
	protected boolean compile(String pomPath) {
		String command="D:/soft/apache-maven-3.2.5/bin/mvn.bat clean install -Dmaven.test.skip=true -f "+pomPath+"/pom.xml";
		return exec(command);
	}
	protected void execCmd(String command) {
		exec("cmd /k start "+command);
	}
	protected boolean exec(String command) {
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
			BufferedReader errReader = new BufferedReader(new InputStreamReader(p.getErrorStream(),"UTF-8"));
			String line=null;
			while((line=reader.readLine())!=null){
				log(line);
			}
			while((line=errReader.readLine())!=null){
				log(line);
			}
			reader.close();
			errReader.close();
			if(p.exitValue()==0){
				log("执行成功");
				return true;
			}else{
				log(p.exitValue());
				log("执行失败");
				return false;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
