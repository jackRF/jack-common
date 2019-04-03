package org.jack.common.logger;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jack.common.util.DateUtils;
import org.junit.Test;
import org.springframework.util.StringUtils;

public class TongcLoggerCollectTest extends AbstractLoggerCollectTest{
	private File dir=new File("D:/data/online");
	protected ProjectLoggerPair bmsLogger;
	protected ProjectLoggerPair ruleLogger;
	protected ProjectLoggerPair bdsLogger;
	protected ProjectLoggerPair cfsLogger;
	{
		{
			ProjectLoggerPair temp=new ProjectLoggerPair();
			temp.setName("bms-api");
			FormatLoggerPair formatLogger=new FormatLoggerPair();
			formatLogger.setFormat("%s-%s.log_%s.log");
			LoggerPair logger=new LoggerPair();
			logger.setLevel("info");
			formatLogger.setLogger(logger);
			temp.setFormatLogger(formatLogger);
			bmsLogger=temp;
		}{
			ProjectLoggerPair temp=new ProjectLoggerPair();
			temp.setName("rule-gate");
			FormatLoggerPair formatLogger=new FormatLoggerPair();
			formatLogger.setFormat("%s%s.log.%s");
			LoggerPair logger=new LoggerPair();
			logger.setLevel("");
			formatLogger.setLogger(logger);
			temp.setFormatLogger(formatLogger);
			ruleLogger=temp;
		}{
			ProjectLoggerPair temp=new ProjectLoggerPair();
			temp.setName("bds-biz");
			FormatLoggerPair formatLogger=new FormatLoggerPair();
			formatLogger.setFormat("%s%s.log.%s");
			LoggerPair logger=new LoggerPair();
			logger.setLevel("");
			formatLogger.setLogger(logger);
			temp.setFormatLogger(formatLogger);
			bdsLogger=temp;
		}{
			ProjectLoggerPair temp=new ProjectLoggerPair();
			temp.setName("cfs");
			FormatLoggerPair formatLogger=new FormatLoggerPair();
			formatLogger.setFormat("%s%s.log.%s");
			LoggerPair logger=new LoggerPair();
			logger.setLevel("");
			formatLogger.setLogger(logger);
			temp.setFormatLogger(formatLogger);
			cfsLogger=temp;
		}
	}
	@Test
	public void collectLogger() {
//		collectBms("2019-04-01 04:00:00", "2019-04-01 08:00:00");
//		collectBms(null, null);
//		collectRule("2019-04-01", "2019-04-02");
//		collectRule(null, null);
		collectBds("2019-04-01", "2019-04-02");
//		collectBds(null, null);
//		collectCfs("2019-04-01", "2019-04-02");
//		collectCfs(null, null);
	}
	protected List<File> collectCfs(String startDate,String endDate) {
		FormatLoggerPair formatLogger=cfsLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/ludan1/cfs_logs/","/ludan2/cfs_logs/"};
		return collectTongcProject(uris, new File(dir,"cfs"), cfsLogger);
	}
	protected List<File> collectBds(String startDate,String endDate){
		FormatLoggerPair formatLogger=bdsLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/xinweiku1/","/xinweiku2/"};
		return collectTongcProject(uris, new File(dir,"bds-biz"), bdsLogger);
	}
	protected List<File> collectRule(String startDate,String endDate) {
		FormatLoggerPair formatLogger=ruleLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/guize-api1/","/guize-api2/"};
		return collectTongcProject(uris, new File(dir,"rule-gate"), ruleLogger);
	}
	protected List<File> collectBms(String startTime,String endTime) {
		FormatLoggerPair formatLogger=bmsLogger.getFormatLogger();
		processRangeHour(formatLogger,startTime,endTime);
		String[] uris={"/jiekuan_api1/","/jiekuan_api2/"};
		return collectTongcProject(uris, new File(dir,"bms"), bmsLogger);
	}
	private void processRangeDay(FormatLoggerPair formatLogger,
			String startDate, String endDate) {
		if(StringUtils.hasText(startDate)){
			Date start=null;
			Date end=null;
			try {
				start = DateUtils.parseDate(startDate, DateUtils.DATE_FORMAT_YYYY_MM_DD);
				DatePair date=null;
				if(StringUtils.hasText(endDate)){
					end=DateUtils.parseDate(endDate, DateUtils.DATE_FORMAT_YYYY_MM_DD);
					date=new RangeDayDatePair(start, end);
				}else{
					date=new RangeDayDatePair(start);
				}
				formatLogger.setFormat("%s%s.log.%s");
				formatLogger.getLogger().setDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return;
		}
		formatLogger.setFormat("%s%s.log");
		formatLogger.getLogger().setDate(DatePair.EMPTY);
		
		
	}
	private void processRangeHour(FormatLoggerPair formatLogger,String startTime,String endTime){
		if(StringUtils.hasText(startTime)){
			Date start=null;
			Date end=null;
			try {
				start = DateUtils.parseDate(startTime, DateUtils.DATE_FORMAT_DATETIME);
				DatePair date=null;
				if(StringUtils.hasText(endTime)){
					end=DateUtils.parseDate(endTime, DateUtils.DATE_FORMAT_DATETIME);
					date=new RangeHourDatePair(start, end);
				}else{
					date=new RangeHourDatePair(start);
				}
				formatLogger.setFormat("%s-%s.log_%s.log");
				formatLogger.getLogger().setDate(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return;
		}
		formatLogger.setFormat("%s-%s.log");
		formatLogger.getLogger().setDate(DatePair.EMPTY);
	}
	
	private List<File> collectTongcProject(String[] uris,File loggerDir,ProjectLoggerPair projectLogger){
		List<String> fileNames=projectLogger.loggerFileNames();
		log(fileNames);
		List<File> list=new ArrayList<File>();
		for(String fileName:fileNames){
			int i=0;
			for(String uri:uris){
				String destFileName="part"+(++i)+"-"+fileName;
				File file=new File(loggerDir,destFileName);
				collectTongcLogger(uri+fileName,file);
				list.add(file);
			}
		}
		return list;
	}
	private void collectTongcLogger(String uri,File file){
		collect("http://10.100.3.71:10000"+uri,file);
	}
	
}
