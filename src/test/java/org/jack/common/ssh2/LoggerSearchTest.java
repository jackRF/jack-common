package org.jack.common.ssh2;


import java.io.File;
import java.io.IOException;

import org.jack.common.logger.task.BmsTask;
import org.jack.common.logger.task.RuleTask;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.jack.common.util.net.ConnectionPair;
import org.junit.Test;


public class LoggerSearchTest extends SSH2Test {
	private IServerConfig serverConfig=new ServerConfig();
	
	@Test
	public void testTailRule() {
		tailLogger(serverConfig.getServer("RULE", "DEV")
				,"tail -f  /home/rule/rule-gate-biz/logs/stdout.log"
				,new RuleTask(new File("D:\\tmp\\rule"),true,true));
	}
	@Test
	public void testTailBms() {
		tailLogger(serverConfig.getServer("BMS", "DEV")
				,"tail -f  /home/bms/bms_biz/bms-biz/logs/stdout.log"
				,new BmsTask(new File("D:\\tmp\\bms"),true));
	}
	@Test
	public void testTailCfs() {
		tailLogger(serverConfig.getServer("CFS", "DEV"),"tail -f /home/cfs/cfs_logs/cfs.log");
	}
	@Test
	public void testTailBds() {
		tailLogger(serverConfig.getServer("BDS", "DEV"),"tail -f /home/bds-biz/logs/stdout.log");
	}
	@Test
	public void testRuleTest() {
		try {
			IOUtils.processText(new File("D:\\tmp\\stdout.log"), new RuleTask(new File("D:\\tmp\\rule"),false,true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void tailLogger(ConnectionPair connectionPair,String cmd) {
		tailLogger(connectionPair,cmd,new Task<String>() {
			@Override
			public void toDo(String t) {
				log(t);
			}
		});
	}
	private void tailLogger(ConnectionPair connectionPair,String cmd,Task<String> task) {
		execute(connectionPair,cmd,task);
	}
}
