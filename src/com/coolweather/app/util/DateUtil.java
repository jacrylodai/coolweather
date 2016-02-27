package com.coolweather.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final SimpleDateFormat dateFormat = 
			new SimpleDateFormat("yyyy-MM-dd");

	public static final SimpleDateFormat shortDateFormat = 
			new SimpleDateFormat("MM��dd��");
	
	public static final SimpleDateFormat fullDateFormat = 
			new SimpleDateFormat("yyyy��MM��dd��");
	
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
	 * �ó�ָ�����������ָ������������
	 * @param specifiedDay
	 * @param days ���漸�죬1�������죬2���Ǻ���
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
