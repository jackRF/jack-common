package org.jack.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * 日期工具类
 * @author YM10177
 *
 */
public class DateUtils {
	public static final String DATE_FORMAT_YYYY_MM_DD="yyyy-MM-dd";
	public static final String DATE_FORMAT_DATE=DATE_FORMAT_YYYY_MM_DD;
	public static final String DATE_FORMAT_DATETIME="yyyy-MM-dd HH:mm:ss";
	public static String formatDate(Date date,String format){
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.format(date);
	}
	public static Date parseDate(String strDate,String format) throws ParseException{
		SimpleDateFormat sdf=new SimpleDateFormat(format);
		return sdf.parse(strDate);
	}
	/**
	 * 这一天结束
	 * @param date
	 * @return
	 */
	public static Date dayEnd(Date date){
		Date dateEnd=date;
		if(dateEnd != null){
			dateEnd = setMilliseconds(dateEnd,999);
			dateEnd = DateUtils.setMinutes(dateEnd, 59);
			dateEnd = DateUtils.setSeconds(dateEnd, 59);
			return DateUtils.setHours(date, 23);
		}
		return dateEnd;
	}
	/**
	 * 这一天开始
	 * @param date
	 * @return
	 */
	public static Date dayStart(Date date){
		Date dateStart=date;
		if(dateStart != null){
			dateStart = setMilliseconds(dateStart,0);
			dateStart = DateUtils.setMinutes(dateStart, 0);
			dateStart = DateUtils.setSeconds(dateStart, 0);
			return DateUtils.setHours(dateStart, 0);
		}
		return dateStart;
	}
	public static Date setMilliseconds(Date date, int amount) {
        return set(date, Calendar.MILLISECOND, amount);
    } 
	public static Date setMinutes(Date date, int amount) {
        return set(date, Calendar.MINUTE, amount);
    }
	public static Date setSeconds(Date date, int amount) {
        return set(date, Calendar.SECOND, amount);
    }
	public static Date setHours(Date date, int amount) {
        return set(date, Calendar.HOUR_OF_DAY, amount);
    }
	private static Date set(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(calendarField, amount);
        return c.getTime();
    }
}
