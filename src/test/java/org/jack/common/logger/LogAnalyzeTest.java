package org.jack.common.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.logger.task.BmsTask;
import org.jack.common.logger.task.Filter;
import org.jack.common.logger.task.RuleTask;
import org.jack.common.util.DateUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

public class LogAnalyzeTest extends TongcLoggerCollectTest {
	@Test
	public void collectAnalyzeTime() throws IOException {
//		List<File> files=collectBms("2019-05-30 03:27:50", "2019-05-30 04:27:50",true);
//		List<File> files=collectBms("2019-07-11 18:19:23", "2019-07-11 18:19:23",true);
		List<File> files=collectBms(null,null,true);
//		List<File> files=collectCfs("2019-06-15", "2019-06-15",true);
//		collectRule("2019-05-18", "2019-05-18",true);
		for(File logFile:files){
//			final Map<String,Stack<LineInfo>> threadMap=new HashMap<String,Stack<LineInfo>>();
//			analyzeLogFile(logFile, threadMap,new DefaultRule(3000));
//			final Map<String,LocalInfo> detailThreadMap=new HashMap<String,LocalInfo>();
//			analyzeLogFileDetail(logFile, detailThreadMap);
			destSearchLoggerBms(logFile,"dest071809");
		}
	}
	@Test
	public void testSearchLoggerBms() {
//		List<File> files=collectBms(null, null,false);
//		List<File> files=collectBms("2019-06-13 12:27:50", "2019-06-13 12:27:50",false);
		List<File> files=collectBms("2019-06-13 11:27:50", "2019-06-13 11:27:50",false);
		for(File file:files){
			destSearchLoggerBms(file);
		}
	}
	@Test
	public void testSearchLogger() {
//		File dir=new File("D:\\data\\test\\dev");
		File dir=new File("D:\\data\\logs\\dev");
		File dest=new File(dir,"dest1");
		if(!dest.exists()){
			dest.mkdir();
		}
		loggerSearch(new File(dir,"rule-gate.log"), new RuleTask(dest,false,true));
	}
	private void destSearchLoggerBms(File file) {
		destSearchLoggerBms(file,"dest");
	}
	private void destSearchLoggerBms(File file,String dest) {
		Filter<InvokeInfo<LoggerInfo>> filter=new Filter<InvokeInfo<LoggerInfo>>() {
			
			@Override
			public boolean filter(InvokeInfo<LoggerInfo> e) {
				LoggerInfo start=e.getStart();
				if(start==null){
					return false;
				}
				String clazz="";
				String method="";
//				clazz="com.ymkj.bms.biz.api.service.app.IAPPExecuter";
//				clazz="com.ymkj.bms.biz.api.service.apply.IApplyEnterExecuter";
				clazz="com.ymkj.bms.biz.api.service.job.IBMSLoanJobExecuter";
//				clazz="com.ymkj.bms.biz.api.service.apply.IApplyValidateExecuter";
//				clazz="com.ymkj.bms.biz.api.service.apply.IEntryAuditExecuter";
//				method=	"queryApply";
//				method="saveOrUpdate";
//				method="zhongAnHistory";
//				method="prepareSuanHuaInLoanRuleData";
				
				if(!filterMethod(clazz, "processPettyLoanAPPPush", e)){
					return false;
				}
				return true;
//				return filterContains("120221198608241512", e);//||filterContains("2019070305ECFD", e)||filterContains("20190704384EA9", e);
//				try {
//					Date time1 = DateUtils.parseDate("2019-06-15 07:41:46", DateUtils.DATE_FORMAT_DATETIME);
//					Date time2=DateUtils.parseDate("2019-06-15 07:51:52", DateUtils.DATE_FORMAT_DATETIME);
//					if(filterTime(time1, time2.getTime()-time1.getTime(),1, e)){
//						String[] loanNos={"23626094", "20170508170000611317", "88317568", "15567829", "20180329326714", "20170104180000451431", "20170531160000662139", "20170930160000917424", "48307925", "16931574"};
//						filterContains(e, loanNos);
////						return filterMethod(clazz, method, e)&&(filterContains("20190613BB6E22", e)||filterContains("3856184", e)||filterContains("510129198407050013", e));
//					  }
//				} catch (ParseException e2) {
//				}
//			  return false;
//				return filterValidateNameIdNo(e);
			}
			private boolean filterContains(InvokeInfo<LoggerInfo> e,String...contents){
				for(String content:contents){
					if(filterContains(content, e)){
						return true;
					}
				}
				return false;
			}
			private boolean filterPreparePettyLoanRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				return filterMethodContains("preparePettyLoanRuleData", idNoStr, e);
			}
			private boolean filterPrepareCoreRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("prepareCoreRuleData", idNoStr, e);
			}
			private boolean filterPrepareSuanHuaInLoanRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("prepareSuanHuaInLoanRuleData", idNoStr, e);
			}
			private boolean filterPrepareZDQQRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("prepareZDQQRuleData", idNoStr, e);
			}
			private boolean filterPrepareZDQQRuleBackendData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("prepareZDQQRuleBackendData", idNoStr, e);
			}
			private boolean filterPrepareSignRuleData(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("prepareSignRuleData", idNoStr, e);
			}
			private boolean filterQueryApplyDataIsYBR(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("queryApplyDataIsYBR", idNoStr, e);
			}
			private boolean filterValidateNameIdNo(InvokeInfo<LoggerInfo> e){
				String idNoStr="\"idNo\":\"622923199111138769\"";
				String loanNoStr="\"loanNo\":\"20171011B3CEA6\"";
				return filterMethodContains("validateNameIdNo", idNoStr, e);
			}
			private boolean filterMethod(String clazz,String method,InvokeInfo<LoggerInfo> e){
				return method.equals(e.getMethod())&&clazz.equals(e.getClazz());
			}
			private boolean filterContains(String contains,InvokeInfo<LoggerInfo> e){
				return e.getStart().getContent().contains(contains);
			}
			private boolean filterTime(Date time,long precision,int type,InvokeInfo<LoggerInfo> e){
				long basems=time.getTime();
				long ms=e.getStart().getTime().getTime();
				long diff=ms-basems;
				if(precision==0){
					if(type==0){
						return diff==0;
					}else if(type==1){
						return diff>=0;
					}else if(type==-1){
						return diff<=0;
					}
					return false;
				}
				if(type==0){
					return -precision<=diff&&diff<=precision;
				}else if(type==1){
					return 0<=diff&&diff<=precision;
				}else if(type==-1){
					return -precision<=diff&&diff<=0;
				}
				return false;
				
			}
			private boolean filterMethodContains(String method,String contains,InvokeInfo<LoggerInfo> e){
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
				return true;
			}
		};
		File dir=file.getParentFile();
		File destDir=new File(dir,dest);
		try {
			if(!destDir.exists()){
				destDir.mkdir();
			}
			IOUtils.processText(file,new BmsTask(destDir,false,filter));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	private void loggerSearch(File file,Task<String> task){
		try {
			IOUtils.processText(file,task);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	@Test
	public void testAnalyzeTime() throws IOException {
		
		final Map<String,Stack<LineInfo>> threadMap=new HashMap<String,Stack<LineInfo>>();
		File logPath=new File("D:\\data\\online\\bms");
		String fileName="bms-api-info1108_2.log";
		fileName="bms-api-info.log_2018-11-08-11.log.txt";
		fileName="bms-api-info1109.log";
		fileName="bms-api-info1112.log";
		fileName="bms-api-info1112.2.log";
		fileName="bms-api-info1112-13.2.log";
		fileName="bms-api-info.log_2018-11-13-09.log.txt";
		fileName="bms-api-info.log_2019-04-01-03.log";
		String destFileName="1"+"-"+fileName;
		destFileName="part2-bms-api-info.log_2019-05-17-21.log";
//		fileName="bms-api-info.log.txt";
//		fileName="bms-api-info2.log";
		File logFile=new File(logPath,destFileName);
		analyzeLogFile(logFile, threadMap,new DefaultRule(3000));
	}
	private class DefaultRule implements Rule{
		final List<LocalInfo> dest=new ArrayList<LocalInfo>();
		final long duration;
		public DefaultRule(long duration) {
			this.duration=duration;
		}
		
		@Override
		public void process(Stack<LineInfo> stack) {
			LineInfo start=stack.firstElement();
			LineInfo last=stack.lastElement();
			Date time2= last.getTime();
			Date time1= start.getTime();
			long duration=time2.getTime()-time1.getTime();
			if(duration>this.duration){
				LocalInfo d=new LocalInfo();
				d.setLastLogTime(start.getTime());
				d.setMethod(start.getMethod());
				d.setThreadId(start.getThreadId());
				d.setLastLoglineIndex(start.getLineIndex());
				d.lineIndex=last.getLineIndex();
				d.duration=duration;
				dest.add(d);
			}
		}
		@Override
		public void complete(File logFile) {
			Collections.sort(dest, new Comparator<LocalInfo>() {
				@Override
				public int compare(LocalInfo o1, LocalInfo o2) {
					Date time1= o1.getLastLogTime();
					Date time2= o2.getLastLogTime();
					return time1.compareTo(time2);
				}
			});
			File outFile=new File(logFile.getParentFile(),logFile.getName()+".analyze");
			PrintWriter pw;
			try {
				pw = new PrintWriter(outFile);
				for(LocalInfo localInfo:dest){
					pw.println(String.format("%s method:%s 线程%s在%d-%d行耗时%ds ",DateUtils.formatDate(localInfo.getLastLogTime(), DateUtils.DATE_FORMAT_DATETIME)
							,localInfo.getMethod()
							,localInfo.getThreadId()
							, localInfo.getLastLoglineIndex()
							,localInfo.getLineIndex(),localInfo.getDuration()/1000					
							));
				}
				pw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	private void analyzeLogFile(File logFile,final Map<String,Stack<LineInfo>> threadMap,final Rule rule) throws IOException {
		IOUtils.processText(logFile, new Task<String>(){
			private int i=0;
			private LocalInfo lastlocalInfo;
			@Override
			public void toDo(String line) {
				i++;
				LineInfo lineInfo=null;
				try{
					lineInfo=parseLine(line,i,lastlocalInfo);
					if(lineInfo==null){
						return;
					}
				}catch(Exception e){
					e.printStackTrace();
					return;
				}
				Stack<LineInfo> stackLocalInfo=threadMap.get(lineInfo.getThreadId());
				if(stackLocalInfo==null){
					stackLocalInfo=new Stack<LineInfo>();
					threadMap.put(lineInfo.getThreadId(), stackLocalInfo);
				}
				if(lineInfo.isMethodEnd()){
					rule.process(stackLocalInfo);
					threadMap.remove(lineInfo.getThreadId());
					return;
				}
				if(lineInfo.isMethodStart()){
					stackLocalInfo.removeAllElements();
				}
				stackLocalInfo.add(lineInfo);
				LocalInfo localInfo=new LocalInfo();
				localInfo.setThreadId(lineInfo.getThreadId());
				localInfo.setLastLoglineIndex(lineInfo.getLineIndex());
				localInfo.setLastLogTime(lineInfo.getTime());
				if(StringUtils.hasText(lineInfo.getBizId())){
					localInfo.setBizId(lineInfo.getBizId());
				}
				localInfo.setMethodStart(lineInfo.isMethodStart());
				localInfo.setMethodEnd(lineInfo.isMethodEnd());
				localInfo.setMethod(lineInfo.getMethod());
				localInfo.setLastLine(line);
				lastlocalInfo=localInfo;
			}
		});
		rule.complete(logFile);
	}
	private void analyzeLogFileDetail(File logFile,final Map<String,LocalInfo> threadMap) throws IOException {
		final List<LocalInfo> dest=new ArrayList<LocalInfo>();
		IOUtils.processText(logFile, new Task<String>(){
			private int i=0;
			private LocalInfo lastlocalInfo;
			@Override
			public void toDo(String line) {
				i++;
				LineInfo lineInfo=null;
				try{
					lineInfo=parseLine(line,i,lastlocalInfo);
					
					if(lineInfo==null){
						return;
					}
				}catch(Exception e){
					return;
				}
				LocalInfo localInfo=threadMap.get(lineInfo.getThreadId());
				if(localInfo==null){
					localInfo=new LocalInfo();
					localInfo.setThreadId(lineInfo.getThreadId());
					threadMap.put(lineInfo.getThreadId(), localInfo);
				}else{
					applyRule(localInfo,lineInfo,dest);
				}
				localInfo.setLastLoglineIndex(lineInfo.getLineIndex());
				localInfo.setLastLogTime(lineInfo.getTime());
				if(StringUtils.hasText(lineInfo.getBizId())){
					localInfo.setBizId(lineInfo.getBizId());
				}
				localInfo.setMethodEnd(lineInfo.isMethodEnd());
				localInfo.setMethod(lineInfo.getMethod());
				localInfo.setLastLine(line);
				lastlocalInfo=localInfo;
			}

			
			
		});
		Collections.sort(dest, new Comparator<LocalInfo>() {

			@Override
			public int compare(LocalInfo o1, LocalInfo o2) {
				Date time1= o1.getLastLogTime();
				Date time2= o2.getLastLogTime();
				return time1.compareTo(time2);
			}
		});
		File outFile=new File(logFile.getParentFile(),logFile.getName()+".analyze.detail");
		PrintWriter pw=new PrintWriter(outFile);
		for(LocalInfo localInfo:dest){
			pw.println(String.format("%s method:%s 线程%s在%d-%d行耗时%ds ",DateUtils.formatDate(localInfo.getLastLogTime(), DateUtils.DATE_FORMAT_DATETIME)
					,localInfo.getMethod()
					,localInfo.getThreadId()
					, localInfo.getLastLoglineIndex()
					,localInfo.getLineIndex(),localInfo.getDuration()/1000					
					));
		}
		pw.close();

	}
	private void applyRule(LocalInfo localInfo, LineInfo lineInfo,List<LocalInfo> dest) {
		Date time1= localInfo.getLastLogTime();
		Date time2= lineInfo.getTime();
		if(time1==null||time2==null){
			return;
		}
		long duration=time2.getTime()-time1.getTime();
		if(duration>1000){
			if(localInfo.isMethodEnd()||localInfo.getLastLine().contains("response:")){
				return;
			}
			LocalInfo d=new LocalInfo();
			d.copy(localInfo);
			d.lineIndex=lineInfo.getLineIndex();
			d.duration=duration;
			dest.add(d);			
		}
	}
	@Test
	public void testParseLine() {
		LineInfo lineInfo=parseLine("2018-10-24 16:00:00[INFO][com.ymkj.base.core.biz.filter.BizLoggerFilter.invoke()][DubboServerHandler-10.100.40.113:20880-thread-487]:",1,null);
		log(JSON.toJSONString(lineInfo));
	}
	private Pattern threadPattern=Pattern.compile(":(\\d+-thread-\\d+)]:$");
	private Pattern methodPattern=Pattern.compile("\\]\\[([\\w\\$\\.]+)\\(\\)\\]\\[");
	private LineInfo parseLine(String line, int lineIndex, LocalInfo lastlocalInfo){
		if(!StringUtils.hasText(line)){
			return null;
		}
		Matcher matcher=threadPattern.matcher(line);
		LineInfo lineInfo=null;
		if(matcher.find()){
			String threadId=matcher.group(1);
			lineInfo=new LineInfo();
			lineInfo.setThreadId(threadId);
			Date time;
			try {
				time = DateUtils.parseDate(line.substring(0, 19), "yyyy-MM-dd HH:mm:ss");
				lineInfo.setTime(time);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Matcher matcherMethod=methodPattern.matcher(line);
			if(matcherMethod.find()){
				String method=matcherMethod.group(1);
				lineInfo.setMethod(method);
			}
		}else if(lastlocalInfo!=null){
			lineInfo=new LineInfo();
			lineInfo.setThreadId(lastlocalInfo.getThreadId());
			lineInfo.setTime(lastlocalInfo.getLastLogTime());
			lineInfo.setMethod(lastlocalInfo.getMethod());
		}else{
			return null;
		}
		lineInfo.setLineIndex(lineIndex);
		lineInfo.setLine(line);
		lineInfo.setMethodStart(line.startsWith("==========开始调用服务接口"));
		lineInfo.setMethodEnd(line.startsWith("==========结束调用服务接口"));
		return lineInfo;
	}
	static interface Rule{
		void process(Stack<LineInfo> stack);
		void complete(File logFile);
	}
	static class LineInfo{
		private Date time;
		private String threadId;
		private String bizId;
		private int lineIndex;
		private String method;
		private Map<String,Object> props;
		private String line;
		private boolean isMethodEnd;
		public boolean methodStart;
		
		public String getLine() {
			return line;
		}
		public void setLine(String line) {
			this.line = line;
		}
		public boolean isMethodStart() {
			return methodStart;
		}
		public void setMethodStart(boolean methodStart) {
			this.methodStart = methodStart;
		}
		public boolean isMethodEnd() {
			return isMethodEnd;
		}
		public void setMethodEnd(boolean isMethodEnd) {
			this.isMethodEnd = isMethodEnd;
		}
		public int getLineIndex() {
			return lineIndex;
		}
		public void setLineIndex(int lineIndex) {
			this.lineIndex = lineIndex;
		}
		public Date getTime() {
			return time;
		}
		public void setTime(Date time) {
			this.time = time;
		}
		public String getThreadId() {
			return threadId;
		}
		public void setThreadId(String threadId) {
			this.threadId = threadId;
		}
		public String getBizId() {
			return bizId;
		}
		public void setBizId(String bizId) {
			this.bizId = bizId;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public Map<String, Object> getProps() {
			return props;
		}
		public void setProps(Map<String, Object> props) {
			this.props = props;
		}
	}
	static class LocalInfo{
		private String threadId;
		private Date lastLogTime;
		private int lastLoglineIndex;
		private String bizId;
		private boolean isMethodEnd;
		private boolean methodStart;
		private int lineIndex;
		private long duration;
		private String method;
		private String lastLine;
		
		public void copy(LocalInfo src){
			threadId=src.threadId;
			lastLogTime=src.lastLogTime;
			lastLoglineIndex=src.lastLoglineIndex;
			bizId=src.bizId;
			isMethodEnd=src.isMethodEnd;
			methodStart=src.methodStart;
			lineIndex=src.lineIndex;
			method=src.method;
			lastLine=src.lastLine;
		}
		public boolean isMethodStart() {
			return methodStart;
		}

		public void setMethodStart(boolean methodStart) {
			this.methodStart = methodStart;
		}

		public String getLastLine() {
			return lastLine;
		}
		public void setLastLine(String lastLine) {
			this.lastLine = lastLine;
		}

		public String getThreadId() {
			return threadId;
		}
		public void setThreadId(String threadId) {
			this.threadId = threadId;
		}
		public Date getLastLogTime() {
			return lastLogTime;
		}
		public void setLastLogTime(Date lastLogTime) {
			this.lastLogTime = lastLogTime;
		}
		public int getLastLoglineIndex() {
			return lastLoglineIndex;
		}
		public void setLastLoglineIndex(int lastLoglineIndex) {
			this.lastLoglineIndex = lastLoglineIndex;
		}
		public String getBizId() {
			return bizId;
		}
		public void setBizId(String bizId) {
			this.bizId = bizId;
		}
		public boolean isMethodEnd() {
			return isMethodEnd;
		}
		public void setMethodEnd(boolean isMethodEnd) {
			this.isMethodEnd = isMethodEnd;
		}
		public int getLineIndex() {
			return lineIndex;
		}
		public void setLineIndex(int lineIndex) {
			this.lineIndex = lineIndex;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
	}
}
