package org.jack.common.logger;

import java.util.List;

public class ProjectLoggerPair {
	private String name;
	private FormatLoggerPair formatLogger;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public FormatLoggerPair getFormatLogger() {
		return formatLogger;
	}
	public void setFormatLogger(FormatLoggerPair formatLogger) {
		this.formatLogger = formatLogger;
	}
	public List<String> loggerFileNames(){
		return formatLogger.loggerFileNames(name);
	}
}
