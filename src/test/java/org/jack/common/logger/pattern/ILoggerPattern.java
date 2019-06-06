package org.jack.common.logger.pattern;

import org.jack.common.logger.Ilogger;
import org.jack.common.logger.StackLogger;

/**
 * 日志匹配模式
 * @author YM10177
 *
 */
public interface ILoggerPattern<T extends Ilogger,S> {
	/**
	 * 
	 * @param line  一行日志内容
	 * @param index  行号
	 * @param last
	 * @return
	 */
	T parseLine(String line,int index,T last);
	/**
	 * 匹配开始
	 * @param logger
	 * @param stackLogger
	 * @return
	 */
	boolean matcherStart(T logger,StackLogger<T,S> stackLogger);
	/**
	 * logger事件
	 * @param logger
	 * @param stackLogger
	 */
	void onLogger(T logger,StackLogger<T,S> stackLogger);
	/**
	 * 匹配结束
	 * @param logger
	 * @param stackLogger
	 * @return
	 */
	boolean matcherEnd(T logger,StackLogger<T,S> stackLogger);
}
