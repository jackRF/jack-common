package org.jack.common.logger.task;

import java.io.File;

import org.jack.common.logger.InvokeInfo;
import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.StackLogger;
import org.jack.common.logger.pattern.BmsLoggerPattern;
import org.springframework.util.StringUtils;

public class BmsTask extends AbstractTask<LoggerInfo,InvokeInfo> {
	public BmsTask(File destLoggerDir) {
		this(destLoggerDir,false);
	}
	public BmsTask(File destLoggerDir,boolean logger) {
		super(destLoggerDir,new BmsLoggerPattern(),logger);
	}
	@Override
	protected void onStackStart(LoggerInfo current,
			StackLogger<LoggerInfo, InvokeInfo> stackLogger) {
		stackLogger.clear();
		LoggerInfo prev=current.getPrev();
		if(prev!=null){
			stackLogger.add(prev);
		}
		stackLogger.add(current);
		super.onLogger(current, stackLogger);
	}
	@Override
	protected void onStackEnd(LoggerInfo current,
			StackLogger<LoggerInfo, InvokeInfo> stackLogger) {
		InvokeInfo invokeInfo=stackLogger.getS();
		if(invokeInfo==null){
			return;
		}
		String clazz=invokeInfo.getClazz();
		String method=invokeInfo.getMethod();
		if(StringUtils.hasText(clazz)&&StringUtils.hasText(method)){
			export(stackLogger, clazz,method,current.getLocation()+".txt");
		}
	}
	@Override
	protected void onLogger(LoggerInfo current,
			StackLogger<LoggerInfo, InvokeInfo> stackLogger) {
		
	}
}
