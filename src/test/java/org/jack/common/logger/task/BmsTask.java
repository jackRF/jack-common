package org.jack.common.logger.task;

import java.io.File;

import org.jack.common.logger.InvokeInfo;
import org.jack.common.logger.LoggerInfo;
import org.jack.common.logger.StackLogger;
import org.jack.common.logger.pattern.BmsLoggerPattern;
import org.springframework.util.StringUtils;

public class BmsTask extends AbstractTask<LoggerInfo,InvokeInfo<LoggerInfo>> {
	private Filter<InvokeInfo<LoggerInfo>> filter;
	public BmsTask(File destLoggerDir) {
		this(destLoggerDir,false);
	}
	public BmsTask(File destLoggerDir,boolean logger) {
		this(destLoggerDir,logger,null);
	}
	public BmsTask(File destLoggerDir,boolean logger,Filter<InvokeInfo<LoggerInfo>> filter) {
		super(destLoggerDir,new BmsLoggerPattern(),logger);
		this.filter=filter;
	}
	@Override
	protected void onStackStart(LoggerInfo current,
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
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
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
		InvokeInfo<LoggerInfo> invokeInfo=stackLogger.getS();
		if(invokeInfo==null){
			return;
		}
		invokeInfo.setEnd(current);
		String clazz=invokeInfo.getClazz();
		String method=invokeInfo.getMethod();		
		if(StringUtils.hasText(clazz)&&StringUtils.hasText(method)){
			if(filter!=null){
				if(filter.filter(invokeInfo)){
					if(filter.export()){
						export(stackLogger, clazz,method,current.getLocation()+".txt");
					}else{
						log(stackLogger);
					}
				}
			}else{
				export(stackLogger, clazz,method,current.getLocation()+".txt");
			}
		}
	}
	@Override
	protected void onLogger(LoggerInfo current,
			StackLogger<LoggerInfo, InvokeInfo<LoggerInfo>> stackLogger) {
		
	}
}
