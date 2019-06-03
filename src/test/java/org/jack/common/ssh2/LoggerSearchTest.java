package org.jack.common.ssh2;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
		tailLoggerDetail(serverConfig.getServer("RULE", "DEV"),"tail -f  /home/rule/rule-gate-biz/logs/stdout.log");
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
			IOUtils.processText(new File("D:\\tmp\\stdout.log"), new RuleDetailTask(new File("D:\\tmp\\rule detail")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static class RuleDetailTask implements Task<String>{
		private File destLoggerDir;
		public RuleDetailTask(File destLoggerDir) {
			this.destLoggerDir=destLoggerDir;
		}
		public RuleDetailTask(File destLoggerDir,boolean logger) {
			this.destLoggerDir=destLoggerDir;
			this.logger=logger;
		}
		private Map<String,StackBizInfo> threadStack=new HashMap<String,StackBizInfo>();
		private LoggerInfo last;
		private ILoggerRule loggerRule=new RuleRule();
		private boolean logger;
		private int lineIndex=0;
		@Override
		public void toDo(String line) {
			if(logger){
				log(line);
			}
			lineIndex++;
			LoggerInfo current=loggerRule.parseLine(line, last);
			if(current==null){
				return;
			}
			current.setLineIndex(lineIndex);
			last=current;
			StackBizInfo stackBizInfo=threadStack.get(current.getThreadId());
			if(stackBizInfo==null){
				stackBizInfo=new StackBizInfo();
				threadStack.put(current.getThreadId(), stackBizInfo);
			}
			stackBizInfo.getStack().add(current);
			if(loggerRule.ruleStart(current, stackBizInfo)){
				
			}else if(loggerRule.ruleEnd(current, stackBizInfo)){
				if(StringUtils.hasText(stackBizInfo.getRuleAlias())){
					String fileName=stackBizInfo.getRuleAlias()+"_"+DateUtils.formatDate(current.getTime(), "yyyy-MM-dd HH_mm_ss_SSS")+".txt";
					loggerStack(stackBizInfo.getStack(),new File(destLoggerDir,fileName));
				}
				threadStack.remove(current.getThreadId());
			}else{
				loggerRule.onRuleData(current, stackBizInfo);
			}
		}
	}
	private static void loggerStack(Stack<LoggerInfo> stack,File file){
		try {
			PrintWriter pw= new PrintWriter(file);
			for(LoggerInfo  loggerInfo:stack){
				pw.println(loggerInfo.getContent());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testa() {
		String text="[DubboServerHandler-172.16.230.50:20811-thread-16][INFO ][2019-06-03 10:05:45,995] DubboServerHandler-172.16.230.50:20811-thread-16:IN : 数据准备 (Process Flow Object)";
		Matcher matcher=RuleRule.regex.matcher(text);
		if(matcher.find()){
			log(matcher.group(0));
			log(matcher.group(1));
			log(matcher.group(2));
			log(matcher.group(3));
			log(matcher.end());
		}
	}
	private static class RuleRule implements ILoggerRule{
		private static final String threadGroupReg="\\[([\\w\\d\\.-]+:[\\w\\d-]+)\\]";
		private static final String levelReg="\\[(\\w+)\\s*\\]";
		private static final String timeReg= "\\[([\\d\\s:,-]+)\\]";
		
		private static final Pattern regex=Pattern.compile("^"+threadGroupReg+levelReg+timeReg);
		@Override
		public LoggerInfo parseLine(String line, LoggerInfo last) {
			Matcher matcher=regex.matcher(line);
			if(matcher.find()){
				String threadGroup=matcher.group(1);
				String[] ss=threadGroup.split(":");
				LoggerInfo info=new LoggerInfo();
				info.setThreadGroup(ss[0]);
				info.setThreadId(ss[1]);
				info.setContent(line);
				info.setMatcher(matcher);
				info.setLevel(matcher.group(2));
				try {
					info.setTime(DateUtils.parseDate(matcher.group(3), DateUtils.DATE_FORMAT_TIMESTAMP));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return info;
			}else if(last!=null){
				LoggerInfo info=new LoggerInfo();
				info.copy(last);
				info.setContent(line);
				return info;
			}
			return null;
		}

		@Override
		public boolean ruleStart(LoggerInfo loggerInfo,StackBizInfo stackBizInfo) {
			Matcher matcher=loggerInfo.getMatcher();
			if(matcher!=null){
				String str=loggerInfo.getContent().substring(matcher.end());
				boolean start=str.startsWith(" >>>>>规则计算传入参数:");
				if(start){
					stackBizInfo.getStack().clear();
					stackBizInfo.getStack().add(loggerInfo);
				}
			}
			return false;
		}

		@Override
		public boolean ruleEnd(LoggerInfo loggerInfo,StackBizInfo stackBizInfo) {
			Matcher matcher=loggerInfo.getMatcher();
			if(matcher!=null){
				String str=loggerInfo.getContent().substring(matcher.end());
				return str.startsWith(" <<<<<规则计算传出参数:");
			}
			return false;
		}

		@Override
		public void onRuleData(LoggerInfo loggerInfo,StackBizInfo stackBizInfo) {
			Matcher matcher=loggerInfo.getMatcher();
			if(matcher!=null){
				String str=loggerInfo.getContent().substring(matcher.end());
				String temp=" "+loggerInfo.getThreadGroup()+":"+loggerInfo.getThreadId()+":Entering strategy :";
				if(str.startsWith(temp)){
					str=str.substring(temp.length());
					Matcher am=Pattern.compile("\\s*(\\w+)\\s*:").matcher(str);
					if(am.find()){
						String alias= am.group(1);
						stackBizInfo.ruleAlias=alias;
					}
				}
			}
			
		}
	}
	private static interface ILoggerRule{
		LoggerInfo parseLine(String line,LoggerInfo last);
		boolean ruleStart(LoggerInfo loggerInfo,StackBizInfo stackBizInfo);
		void onRuleData(LoggerInfo loggerInfo,StackBizInfo stackBizInfo);
		boolean ruleEnd(LoggerInfo loggerInfo,StackBizInfo stackBizInfo);
	}
	private static class StackBizInfo{
		private Stack<LoggerInfo> stack=new Stack<LoggerInfo>();
		private String ruleAlias;
		public Stack<LoggerInfo> getStack() {
			return stack;
		}
		public void setStack(Stack<LoggerInfo> stack) {
			this.stack = stack;
		}
		public String getRuleAlias() {
			return ruleAlias;
		}
		public void setRuleAlias(String ruleAlias) {
			this.ruleAlias = ruleAlias;
		}
	}
	private static class LoggerInfo{
		private String threadGroup;
		private String threadId;
		private String level;
		private Date time;
		private Map<String,Object> properties;
		private String content;
		private int lineIndex;
		private Matcher matcher;
		public void copy(LoggerInfo other){
			this.level=other.level;
			this.time=other.time;
			this.threadGroup=other.threadGroup;
			this.threadId=other.threadId;
		}
		
		public Matcher getMatcher() {
			return matcher;
		}

		public void setMatcher(Matcher matcher) {
			this.matcher = matcher;
		}

		public String getThreadGroup() {
			return threadGroup;
		}
		public void setThreadGroup(String threadGroup) {
			this.threadGroup = threadGroup;
		}
		public String getThreadId() {
			return threadId;
		}
		public void setThreadId(String threadId) {
			this.threadId = threadId;
		}
		public String getLevel() {
			return level;
		}
		public void setLevel(String level) {
			this.level = level;
		}
		public Date getTime() {
			return time;
		}
		public void setTime(Date time) {
			this.time = time;
		}
		public Map<String, Object> getProperties() {
			return properties;
		}
		public void setProperties(Map<String, Object> properties) {
			this.properties = properties;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public int getLineIndex() {
			return lineIndex;
		}
		public void setLineIndex(int lineIndex) {
			this.lineIndex = lineIndex;
		}
	} 
	private void tailLoggerDetail(ConnectionPair connectionPair,String cmd){
		execute(connectionPair,cmd,new RuleDetailTask(new File("D:\\tmp\\rule detail"),true));
	}
	
	private void tailLogger(ConnectionPair connectionPair,String cmd) {
		execute(connectionPair,cmd,new Task<String>() {
			@Override
			public void toDo(String t) {
				log(t);
			}
		});
	}
}
