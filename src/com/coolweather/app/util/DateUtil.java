package com.coolweather.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat shortDateFormat = 
			new SimpleDateFormat("MM月dd日");
	
	public static final SimpleDateFormat fullDateFormat = 
			new SimpleDateFormat("yyyy年MM月dd日");
	
	public static final SimpleDateFormat timeFormat = 
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public static String getSpecifiedDayBeforeDays(String specifiedDay,int days){

		if(days == 0){
			return specifiedDay;
		}
		
		Calendar c = Calendar.getInstance();
		Date date=null;
		try {
			date = dateFormat.parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day=c.get(Calendar.DATE);
		c.set(Calendar.DATE,day-days);

		String dayBefore=dateFormat.format(c.getTime());
		return dayBefore;
	}

	/**
	 * 得出指定的日期向后指定天数的日期
	 * @param specifiedDay
	 * @param days 后面几天，1就是明天，2就是后天
	 * @return
	 */
	public static String getSpecifiedDayAfterDays(String specifiedDay,int days){
		
		if(days == 0){
			return specifiedDay;
		}
		
		Calendar c = Calendar.getInstance();
		Date date=null;
		try {
			date = dateFormat.parse(specifiedDay);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		c.setTime(date);
		int day=c.get(Calendar.DATE);
		c.set(Calendar.DATE,day+days);

		String dayAfter = dateFormat.format(c.getTime());
		return dayAfter;
	}
	
}
