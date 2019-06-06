package org.jack.common.logger;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;

import org.jack.common.util.DateUtils;

public class LoggerInfo implements Ilogger {
	private String threadGroup;
	private String threadId;
	private String level;
	private Date time;
	private Map<String,Object> properties;
	private String content;
	private int lineIndex;
	private Matcher matcher;
	public LoggerInfo(String content) {
		this.content=content;
	}
	public LoggerInfo(String content,int lineIndex){
		this.content=content;
		this.lineIndex=lineIndex;
	}
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
	@Override
	public String getLocation() {
		return DateUtils.formatDate(this.getTime(), "yyyy-MM-dd HH_mm_ss_SSS");
	}
}
