package org.jack.common.logger.task;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jack.common.logger.Ilogger;
import org.jack.common.logger.StackLogger;
import org.jack.common.logger.pattern.ILoggerPattern;
import org.jack.common.util.Task;

public abstract class AbstractTask<T extends Ilogger,S> implements Task<String>{
	protected File destLoggerDir;
	protected Map<String,StackLogger<T,S>> threadStack=new HashMap<String,StackLogger<T,S>>();
	protected T last;
	protected ILoggerPattern<T,S> loggerPattern;
	protected boolean logger;
	protected int lineIndex=0;
	public AbstractTask(File destLoggerDir,ILoggerPattern<T,S> loggerPattern,boolean logger) {
		this.destLoggerDir=destLoggerDir;
		this.loggerPattern=loggerPattern;
		this.logger=logger;
	}
	@Override
	public void toDo(String line) {

		if(logger){
			log(line);
		}
		lineIndex++;
		T current=loggerPattern.parseLine(line,lineIndex, last);
		if(current==null){
			return;
		}
		last=current;
		String threadId=current.getThreadId();
		StackLogger<T, S> stackLogger=threadStack.get(threadId);
		if(stackLogger==null){
			stackLogger=new StackLogger<T, S>();
			threadStack.put(threadId, stackLogger);
		}
		stackLogger.add(current);
		if(loggerPattern.matcherStart(current, stackLogger)){
			onStackStart(current, stackLogger);
		}else if(loggerPattern.matcherEnd(current, stackLogger)){
			onStackEnd(current, stackLogger);
			threadStack.remove(threadId);
		}else{
			onLogger(current, stackLogger);
		}
	}
	protected void onLogger(T current,StackLogger<T, S> stackLogger) {
		loggerPattern.onLogger(current, stackLogger);
	}
	protected void onStackStart(T current,StackLogger<T, S> stackLogger) {
		stackLogger.clear();
		stackLogger.add(current);
	}
	protected abstract void onStackEnd(T current,StackLogger<T, S> stackLogger);
	protected void export(Collection<T> loggers,String...paths) {
		int ln=paths.length;
		File file=destLoggerDir;
		for(int i=0;i<ln;i++){
			file=new File(file,paths[i]);
			if(i!=(ln-1)){
				if(!file.exists()){
					file.mkdir();
				}
			}
		}
		try {
			PrintWriter pw= new PrintWriter(file);
			for(T  logger:loggers){
				pw.println(logger.getContent());
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	protected void log(Collection<T> loggers){
		for(T  logger:loggers){
			log(logger.getContent());
		}
	}
	protected void log(Object message){
		System.out.println(message);
	}
}
