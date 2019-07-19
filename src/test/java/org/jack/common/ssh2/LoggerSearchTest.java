package org.jack.common.ssh2;


import java.io.File;
import java.io.IOException;

import org.jack.common.logger.InvokeInfo;
import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.task.BmsTask;
import org.jack.common.logger.task.Filter;
import org.jack.common.logger.task.RuleTask;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.jack.common.util.net.ConnectionPair;
import org.junit.Test;


public class LoggerSearchTest extends SSH2Test {
	private IServerConfig serverConfig=new ServerConfig();
	private String env="UAT";
	@Test
	public void testTailRule() {
		tailLogger(serverConfig.getServer("RULE", env)
//				,"tail -1000f  /home/rule/rule-gate-biz/logs/stdout.log"
//				,"tail -f /home/rule/rule-gate-biz/logs/stdout.log"
				,"tail -1000f /data/logs/dubbo/rule-gate.log"
				,new RuleTask(new File("D:\\data\\test\\"+env.toLowerCase()+"\\rule"),true,true));
	}
	@Test
	public void testTailBms() {
		tailLogger(serverConfig.getServer("BMS", env)
//				,"tail -f  /home/bms/bms_biz/bms-biz/logs/stdout.log"
				,"tail -1000f  /data/logs/bms-api-debug.log"
				,new BmsTask(new File("D:\\data\\test\\"+env.toLowerCase()+"\\bms"),true,null));
	}
	private Filter<InvokeInfo<LoggerInfo>> getFilter(){
		return new Filter<InvokeInfo<LoggerInfo>>() {
			
			@Override
			public boolean filter(InvokeInfo<LoggerInfo> e) {
				LoggerInfo start=e.getStart();
				if(start==null){
					return false;
				}
				if(!"com.ymkj.bms.biz.api.service.apply.IApplyValidateExecuter".equals(e.getClazz())){
					return false;
				}
				return filterValidateNameIdNo(e);
			}
			private boolean filterPreparePettyLoanRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				return filterMethod("preparePettyLoanRuleData", idNoStr, e);
			}
			private boolean filterPrepareCoreRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("prepareCoreRuleData", idNoStr, e);
			}
			private boolean filterPrepareSuanHuaInLoanRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("prepareSuanHuaInLoanRuleData", idNoStr, e);
			}
			private boolean filterPrepareZDQQRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("prepareZDQQRuleData", idNoStr, e);
			}
			private boolean filterPrepareZDQQRuleBackendData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("prepareZDQQRuleBackendData", idNoStr, e);
			}
			private boolean filterPrepareSignRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("prepareSignRuleData", idNoStr, e);
			}
			private boolean filterQueryApplyDataIsYBR(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("queryApplyDataIsYBR", idNoStr, e);
			}
			private boolean filterValidateNameIdNo(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethod("validateNameIdNo", idNoStr, e);
			}
			private boolean filterMethod(String method,String contains,InvokeInfo<LoggerInfo> e){
				if(method.equals(e.getMethod())){
					if(contains==null){
						return true;
					}
					return e.getStart().getContent().contains(contains);
				}
				return false;
			}
			private boolean determineDuration(InvokeInfo<LoggerInfo> e,long ms){
				return e.getEnd().getTime().getTime()-e.getStart().getTime().getTime()>=ms;
			}
			
			@Override
			public boolean export() {
				return false;
			}
		};
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
