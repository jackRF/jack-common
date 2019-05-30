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
//		collectBms("2019-04-17 11:12:34", "2019-04-17 11:12:34",true);
		collectBms("2019-05-17 22:27:50", "2019-05-18 04:27:50",true);
		collectBms(null, null,true);
//		collectRule("2019-04-01", "2019-04-02",true);
//		collectRule(null, null,true);
//		collectBds("2019-04-01", "2019-04-02",true);
//		collectBds(null, null,true);
//		collectCfs("2019-05-17", "2019-05-17",true);
//		collectCfs(null, null,true);
	}
	protected List<File> collectCfs(String startDate,String endDate,boolean collect) {
		FormatLoggerPair formatLogger=cfsLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/ludan1/cfs_logs/","/ludan2/cfs_logs/"};
		if(collect){
			return collectTongcProject(uris, new File(dir,"cfs"), cfsLogger);
		}
		return collectTongcProject(uris, new File(dir,"cfs"), cfsLogger,null);
	}
	protected List<File> collectBds(String startDate,String endDate,boolean collect){
		FormatLoggerPair formatLogger=bdsLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/xinweiku1/","/xinweiku2/"};
		if(collect){
			return collectTongcProject(uris, new File(dir,"bds-biz"), bdsLogger);
		}
		return collectTongcProject(uris, new File(dir,"bds-biz"), bdsLogger,null);
	}
	protected List<File> collectRule(String startDate,String endDate,boolean collect) {
		FormatLoggerPair formatLogger=ruleLogger.getFormatLogger();
		processRangeDay(formatLogger,startDate,endDate);
		String[] uris={"/guize-api1/","/guize-api2/"};
		if(collect){
			return collectTongcProject(uris, new File(dir,"rule-gate"), ruleLogger);
		}
		return collectTongcProject(uris, new File(dir,"rule-gate"), ruleLogger,null);
	}
	protected List<File> collectBms(String startTime,String endTime,boolean collect) {
		FormatLoggerPair formatLogger=bmsLogger.getFormatLogger();
		processRangeHour(formatLogger,startTime,endTime);
		String[] uris={"/jiekuan_api1/","/jiekuan_api2/"};
		if(collect){
			return collectTongcProject(uris, new File(dir,"bms"), bmsLogger);
		}
		return collectTongcProject(uris, new File(dir,"bms"), bmsLogger,null);
	}
	
	private List<File> collectTongcProject(String[] uris,File loggerDir,ProjectLoggerPair projectLogger){
		return collectTongcProject(uris, loggerDir, projectLogger, new CollectTask() {
			@Override
			public void collect(String uri, String fileName, File file) {
				collectTongcLogger(uri+fileName, file);
			}
		});
	}
	private List<File> collectTongcProject(String[] uris,File loggerDir,ProjectLoggerPair projectLogger,CollectTask collectTask){
		List<String> fileNames=projectLogger.loggerFileNames();
		log(fileNames);
		List<File> list=new ArrayList<File>();
		for(String fileName:fileNames){
			int i=0;
			for(String uri:uris){
				String destFileName="part"+(++i)+"-"+fileName;
				File file=new File(loggerDir,destFileName);
				if(collectTask!=null){
					collectTask.collect(uri, fileName, file);
				}
				list.add(file);
			}
		}
		return list;
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
	interface CollectTask{
		void collect(String uri,String fileName,File file);
	}
	private void collectTongcLogger(String uri,File file){
		collect("http://10.100.3.71:10000"+uri,file);
	}
	
}
