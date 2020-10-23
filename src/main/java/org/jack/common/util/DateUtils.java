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
	public static final String DATE_FORMAT_TIMESTAMP="yyyy-MM-dd HH:mm:ss,SSS";
	public static int getAge(Date birthDay){
        Calendar cal = Calendar.getInstance();
        if (cal.before(birthDay)) {
            throw new IllegalArgumentException("The birthDay is before Now.It's unbelievable!");
        }
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthDay);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow<monthBirth||dayOfMonthNow < dayOfMonthBirth) {
                age--;
            }
        }
        return age;
    }
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
	public static Date weekDay(Date date,int firstDayOfWeek){
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0){
			day_of_week = 7;
		}
		c.add(Calendar.DATE, -day_of_week + firstDayOfWeek);
		return c.getTime();
	}
	public static Date addMonth(Date date, int month) {
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}
	public static Date addDay(Date date, int day) {
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, day);
		return c.getTime();
	}
	public static Date addHour(Date date, int hour) {
		Calendar c = Calendar.getInstance();
		c.setLenient(false);
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, hour);
		return c.getTime();
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
