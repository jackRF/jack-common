package org.jack.common.logger.task;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.StackLogger;
import org.jack.common.logger.pattern.RuleLoggerPattern;
import org.springframework.util.StringUtils;

public class RuleTask extends AbstractTask<LoggerInfo,String>{
	private boolean simple;
	public RuleTask(File destLoggerDir) {
		this(destLoggerDir,false);
	}
	public RuleTask(File destLoggerDir,boolean logger) {
		this(destLoggerDir,logger,false);
	}
	public RuleTask(File destLoggerDir,boolean logger,boolean simple) {
		this.destLoggerDir=destLoggerDir;
		this.logger=logger;
		this.simple=simple;
		this.loggerPattern=new RuleLoggerPattern();
	}
	@Override
	protected void completeStackLogger(LoggerInfo current,
			StackLogger<LoggerInfo, String> stackLogger) {
		boolean useSimple=false;
		if(StringUtils.hasText(stackLogger.getS())){
			String fileName=stackLogger.getS()+"_detail_"+current.getLocation()+".txt";
			export(stackLogger,fileName);
		}else{
			useSimple=true;
		}
		if(useSimple||simple){
			LoggerInfo start=stackLogger.firstElement();
			if(start!=null&&start!=current&&loggerPattern.matcherStart(start, stackLogger)){
				if(useSimple){
					String s=decideRuleAlias(start, current);
					String fileName=s+"_"+current.getLocation()+".txt";
					export(stackLogger,fileName);
				}else{
					String fileName=stackLogger.getS()+"_"+current.getLocation()+".txt";
					List<LoggerInfo> list=new ArrayList<LoggerInfo>();
					list.add(start);
					list.add(current);
					export(list,fileName);
				}
			}
		}
	}
	private String decideRuleAlias(LoggerInfo start,LoggerInfo end){
		if(start.getContent().contains("\"VALUE IN JAVA XS\"")){
			return "XS01";
		}
		if(start.getContent().contains("\"VALUE IN JAVA ZDQQRGCZ\"")){
			return "ZDQQRGCZ";
		}
		if(start.getContent().contains("\"VALUE IN JAVA QYRGCZ\"")){
			return "QYRGCZ";
		}
		if(start.getContent().contains("\"VALUE IN JAVA KFLDRGCZ\"")){
			return "KFRGCZ";
		}
		if(start.getContent().contains("\"VALUE OUT JAVA ZHHKDJ\"")){
			return "ZHHKDJ";
		}
		if(start.getContent().contains("\"VALUE OUT JAVA ZHHKDJ\"")){
			return "ZHHKDJ";
		}
		if(start.getContent().contains("\"VALUE OUT JAVA ZDSCB\"")){
			return "ZDSCB";
		}
		if(start.getContent().contains("\"VALUE OUT JAVA ZDSCC\"")){
			return "ZDSCC";
		}
		if(start.getContent().contains("\"VALUE IN JAVA APPRZPP\"")){
			return "APPRZPP";
		}
		if(start.getContent().contains("\"VALUE IN JAVA APPRGCZ\"")){
			return "APPRGCZ";
		}
		if(start.getContent().contains("\"grantCount\"")){
			return "XEDSX";
		}
		if(start.getContent().contains("\"previousMonthIntegralGrant\"")){
			return "ZYJF";
		}
		return "rulexxx";
	}
}
