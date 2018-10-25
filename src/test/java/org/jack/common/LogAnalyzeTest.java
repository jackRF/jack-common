package org.jack.common;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.util.DateUtils;
import org.jack.common.util.IOUtils;
import org.jack.common.util.Task;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class LogAnalyzeTest extends BaseTest {
	private int i=0;
	@Test
	public void testAnalyze() throws IOException {
		
		Map<String,LocalInfo> threadMap=new HashMap<String,LocalInfo>();
		File logPath=new File("D:\\data\\online");
		File logFile=new File(logPath,"bms-api-info.log_2018-10-24-16.log.txt");
		
		IOUtils.processText(logFile, new Task<String>(){

			@Override
			public void toDo(String line) {
				i++;
				LineInfo lineInfo=null;
				try{
					lineInfo=parseLine(line);
					
					if(lineInfo==null){
						return;
					}
					lineInfo.setLineIndex(i);
				}catch(Exception e){
					return;
				}
				LocalInfo localInfo=threadMap.get(lineInfo.getThreadId());
				if(localInfo==null){
					localInfo=new LocalInfo();
					threadMap.put(lineInfo.getThreadId(), localInfo);
				}else{
					applyRule(localInfo,lineInfo);
				}
				localInfo.setLastLoglineIndex(lineInfo.getLineIndex());
				localInfo.setLastLogTime(lineInfo.getTime());
				if(StringUtils.hasText(lineInfo.getBizId())){
					localInfo.setBizId(lineInfo.getBizId());
				}
			}

			
			
		});
	}
	private void applyRule(LocalInfo localInfo, LineInfo lineInfo) {
		Date time1= localInfo.getLastLogTime();
		Date time2= lineInfo.getTime();
		if(time1==null||time2==null){
			return;
		}
		long duration=time2.getTime()-time1.getTime();
		if(duration>2000){
			log(String.format("线程%s在%d-%d行耗时%ds",lineInfo.getThreadId(), localInfo.getLastLoglineIndex(),lineInfo.getLineIndex(),duration/1000));
		}
	}
	@Test
	public void testParseLine() {
		parseLine("2018-10-24 16:00:00[INFO][com.ymkj.base.core.biz.filter.BizLoggerFilter.invoke()][DubboServerHandler-10.100.40.113:20880-thread-487]:");

	}
	private Pattern threadPattern=Pattern.compile(":(\\d+-thread-\\d+)]:$");
	private LineInfo parseLine(String line){
		if(!StringUtils.hasText(line)){
			return null;
		}
		Matcher matcher=threadPattern.matcher(line);
		if(!matcher.find()){
			return null;
		}
		String threadId=matcher.group(1);
		LineInfo lineInfo=new LineInfo();
		lineInfo.setThreadId(threadId);
		Date time;
		try {
			time = DateUtils.parseDate(line.substring(0, 19), "yyyy-MM-dd HH:mm:ss");
			lineInfo.setTime(time);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lineInfo;
	}
	static class LineInfo{
		private Date time;
		private String threadId;
		private String bizId;
		private int lineIndex;
		private Map<String,Object> props;
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
		public Map<String, Object> getProps() {
			return props;
		}
		public void setProps(Map<String, Object> props) {
			this.props = props;
		}
	}
	static class LocalInfo{
		private Date lastLogTime;
		private int lastLoglineIndex;
		private String bizId;
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
		
	}
}
