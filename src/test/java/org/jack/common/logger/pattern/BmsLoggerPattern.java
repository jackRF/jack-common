package org.jack.common.logger.pattern;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jack.common.logger.InvokeInfo;
import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.StackLogger;
import org.jack.common.util.DateUtils;

public class BmsLoggerPattern implements ILoggerPattern<LoggerInfo, InvokeInfo<LoggerInfo>>{
	private static final String threadGroup="[\\w\\d\\.-]+:[\\w\\d-]+";
	private static final String level="\\[(\\w+)\\s*\\]";
	private static final String day= "[\\d]{4}-[0-1][0-9]-[0-3][0-9]";
	private static final String time= "[0-2][0-9]:[0-5][0-9]:[0-5][0-9]";
	private static final String method="\\w+(?:\\.\\w+)+\\(\\)";
	private static final Pattern pattern=Pattern.compile(String.format("^(%s)%s\\[(%s)\\]\\[(%s)\\]", day+" "+time,level,method,threadGroup));
	public static void main(String[] args) {
		String line="2019-06-25 15:18:07[INFO][com.ymkj.bms.biz.service.adapter.CreditZXService.baseRequest()][DubboServerHandler-172.16.235.9:20880-thread-99]:";
		BmsLoggerPattern p=new BmsLoggerPattern();
		System.out.println(p.parseLine(line, 1, null));
	}
	@Override
	public LoggerInfo parseLine(String line, int index, LoggerInfo last) {
		LoggerInfo logger=index>0?new LoggerInfo(line, index):new LoggerInfo(line);
		Matcher matcher=pattern.matcher(line);
		if(matcher.find()){
			String methodString=matcher.group(3);
			int lastDotIndex=methodString.lastIndexOf(".");
			String threadGroup=matcher.group(4);
			String[] ss=threadGroup.split(":");
			logger.setThreadGroup(ss[0]);
			logger.setThreadId(ss[1]);
			logger.setMatcher(matcher);
			logger.setLevel(matcher.group(2));
			logger.setClazz(methodString.substring(0,lastDotIndex));
			logger.setMethod(methodString.substring(lastDotIndex+1,methodString.length()-2));
			try {
				logger.setTime(DateUtils.parseDate(matcher.group(1), DateUtils.DATE_FORMAT_DATETIME));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return logger;
			
		}else if(last!=null){
			logger.copy(last);
			return logger;
		}
		return null;
	}

	@Override
	public boolean matcherStart(LoggerInfo logger,
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
		if("com.ymkj.base.core.biz.filter.BizLoggerFilter.invoke".equals(logger.getClazz()+"."+logger.getMethod())){
			if(logger.getContent().startsWith("==========开始调用服务接口:")){
				return true;
			}
		}
		return false;
	}

	@Override
	public void onLogger(LoggerInfo logger,
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
		InvokeInfo<LoggerInfo> ii=stackLogger.getS();
		if(ii==null){
			ii=new InvokeInfo<LoggerInfo>();
			stackLogger.setS(ii);
			ii.setStart(logger);
		}
		String content=logger.getContent();
		int s=content.indexOf("[");
		int e=content.indexOf("]");
		if(s>=0&&e>0){
			s+="interface ".length()+1;
			if(s<e){
				String method=content.substring(s, e);
				int amlDotIndex=method.lastIndexOf(".");
				ii.setClazz(method.substring(0, amlDotIndex));
				ii.setMethod(method.substring(amlDotIndex+1));
			}
		}
	}

	@Override
	public boolean matcherEnd(LoggerInfo logger,
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
		if("com.ymkj.base.core.biz.filter.BizLoggerFilter.invoke".equals(logger.getClazz()+"."+logger.getMethod())){
			if(logger.getContent().startsWith("==========结束调用服务接口:")){
				return true;
			}
		}
		return false;
	}

}
