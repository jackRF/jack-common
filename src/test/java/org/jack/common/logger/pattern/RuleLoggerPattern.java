package org.jack.common.logger.pattern;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.StackLogger;
import org.jack.common.util.DateUtils;

public class RuleLoggerPattern implements  ILoggerPattern<LoggerInfo,String>{

	private static final String threadGroupReg="\\[([\\w\\d\\.-]+:[\\w\\d-]+)\\]";
	private static final String levelReg="\\[(\\w+)\\s*\\]";
	private static final String timeReg= "\\[([\\d\\s:,-]+)\\]";
	
	private static final Pattern regex=Pattern.compile("^"+threadGroupReg+levelReg+timeReg);
	@Override
	public LoggerInfo parseLine(String line,int index, LoggerInfo last) {
		LoggerInfo info=index>0?new LoggerInfo(line,index):new LoggerInfo(line);
		Matcher matcher=regex.matcher(line);
		if(matcher.find()){
			String threadGroup=matcher.group(1);
			String[] ss=threadGroup.split(":");
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
			info.copy(last);
			info.setContent(line);
			return info;
		}
		return null;
	}

	@Override
	public boolean matcherStart(LoggerInfo logger,StackLogger<LoggerInfo,String> stackLogger) {
		Matcher matcher=logger.getMatcher();
		if(matcher!=null){
			String str=logger.getContent().substring(matcher.end());
			return str.startsWith(" >>>>>规则计算传入参数:");
		}
		return false;
	}

	@Override
	public boolean matcherEnd(LoggerInfo loggerInfo,StackLogger<LoggerInfo,String> stackLogger) {
		Matcher matcher=loggerInfo.getMatcher();
		if(matcher!=null){
			String str=loggerInfo.getContent().substring(matcher.end());
			return str.startsWith(" <<<<<规则计算传出参数:");
		}
		return false;
	}

	@Override
	public void onLogger(LoggerInfo loggerInfo,StackLogger<LoggerInfo,String> stackLogger) {
		Matcher matcher=loggerInfo.getMatcher();
		if(matcher!=null){
			String str=loggerInfo.getContent().substring(matcher.end());
			String temp=" "+loggerInfo.getThreadGroup()+":"+loggerInfo.getThreadId()+":Entering strategy :";
			if(str.startsWith(temp)){
				str=str.substring(temp.length());
				Matcher am=Pattern.compile("\\s*(\\w+)\\s*:").matcher(str);
				if(am.find()){
					String alias= am.group(1);
					stackLogger.setS(alias);
				}
			}
		}
		
	}
}
