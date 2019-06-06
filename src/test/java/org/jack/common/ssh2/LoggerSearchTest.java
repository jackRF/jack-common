package org.jack.common.ssh2;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.task.RuleTask;
import org.jack.common.util.DateUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.jack.common.util.net.ConnectionPair;
import org.junit.Test;
import org.springframework.util.StringUtils;


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
		tailLogger(serverConfig.getServer("BMS", "DEV"),"tail -f  /home/bms/bms_biz/bms-biz/logs/stdout.log");
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
