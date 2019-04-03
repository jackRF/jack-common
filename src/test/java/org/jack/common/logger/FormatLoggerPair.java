package org.jack.common.logger;

import java.util.ArrayList;
import java.util.List;

public class FormatLoggerPair {
	private LoggerPair logger;
	private String format;
	public LoggerPair getLogger() {
		return logger;
	}
	public void setLogger(LoggerPair logger) {
		this.logger = logger;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public List<String> loggerFileNames(String name) {
		List<String> dateParts=logger.getDate().dateParts();
		if(dateParts!=null&&!dateParts.isEmpty()){
			List<String> fileNames=new ArrayList<String>();
			for(String datePart:dateParts){
				fileNames.add(String.format(format, name,logger.getLevel(),datePart));
			}
			return fileNames;
		}
		return null;
	}
}
