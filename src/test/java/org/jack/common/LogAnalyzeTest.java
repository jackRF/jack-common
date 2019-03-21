package org.jack.common;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.util.DateUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.junit.Test;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

public class LogAnalyzeTest extends BaseTest {
	private int i=0;
	private LocalInfo lastlocalInfo;

	@Test
	public void testAnalyze() throws IOException {
		
		final Map<String,LocalInfo> threadMap=new HashMap<String,LocalInfo>();
		File logPath=new File("D:\\data\\online");
		String fileName="bms-api-info1108_2.log";
		fileName="bms-api-info.log_2018-11-08-11.log.txt";
		fileName="bms-api-info1109.log";
		fileName="bms-api-info1112.log";
		fileName="bms-api-info1112.2.log";
		fileName="bms-api-info1112-13.2.log";
		fileName="bms-api-info.log_2018-11-13-09.log.txt";
		fileName="bms-api-info.log.txt";
//		fileName="bms-api-info2.log";
		File logFile=new File(logPath,fileName);
		analyzeLogFile(logFile, threadMap);
	}
	private void analyzeLogFile(File logFile,final Map<String,LocalInfo> threadMap) throws IOException {
		final List<LocalInfo> dest=new ArrayList<LocalInfo>();
		IOUtils.processText(logFile, new Task<String>(){

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
		File outFile=new File(logFile.getParentFile(),logFile.getName()+".analyze");
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
		lineInfo.setMethodEnd(line.startsWith("==========结束调用服务接口"));
		return lineInfo;
	}
	static class LineInfo{
		private Date time;
		private String threadId;
		private String bizId;
		private int lineIndex;
		private String method;
		private Map<String,Object> props;
		private boolean isMethodEnd;
		
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
			lineIndex=src.lineIndex;
			method=src.method;
			lastLine=src.lastLine;
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
