package org.jack.common.logger;

import java.util.ArrayList;
import java.util.List;

public class LoggerPair {
	private String level;
	private DatePair date;
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public DatePair getDate() {
		return date;
	}
	public void setDate(DatePair date) {
		this.date = date;
	}
	public List<String[]> loggerFileNames(){
		List<String> dateParts=date.dateParts();
		if(dateParts!=null&&!dateParts.isEmpty()){
			List<String> fileNames=new ArrayList<String>();
			for(String datePart:dateParts){
				fileNames.add(level+datePart);
			}
		}
		return null;
	}
}
