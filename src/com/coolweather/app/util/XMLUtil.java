package com.coolweather.app.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import android.os.SystemClock;

import com.coolweather.app.model.County;
import com.coolweather.app.model.WeatherDayInfo;

public class XMLUtil {
	
	private static final String ITEM_DATE_PATH = 
			"/CityWeatherResponse/results/weather_data/date";

	private static final String ITEM_DAY_PICTURE_URL_PATH = 
			"/CityWeatherResponse/results/weather_data/dayPictureUrl";

	private static final String ITEM_NIGHT_PICTURE_URL_PATH = 
			"/CityWeatherResponse/results/weather_data/nightPictureUrl";

	private static final String ITEM_WEATHER_INFO_PATH = 
			"/CityWeatherResponse/results/weather_data/weather";

	private static final String ITEM_WIND_PATH = 
			"/CityWeatherResponse/results/weather_data/wind";

	private static final String ITEM_TEMPERATURE_PATH = 
			"/CityWeatherResponse/results/weather_data/temperature";

	public static List<WeatherDayInfo> getWeatherDayInfoListFromXML(
			Document document, Integer countyId) {
		
		List<WeatherDayInfo> weatherDayInfoList = 
				new ArrayList<WeatherDayInfo>();
		
		Element dateFirstElt = (Element) document.selectObject("/CityWeatherResponse/date");
		String dateFirst = dateFirstElt.getStringValue();
		
		Date updateTime = new Date(System.currentTimeMillis());
		String updateTimeString = DateUtil.timeFormat.format(updateTime);
		
		List<Element> dateEltList = 
				(List<Element>) document.selectObject(ITEM_DATE_PATH);
		List<Element> dayPicUrlEltList = 
				(List<Element>)document.selectObject(ITEM_DAY_PICTURE_URL_PATH);
		List<Element> nightPicUrlEltList = 
				(List<Element>)document.selectObject(ITEM_NIGHT_PICTURE_URL_PATH);
		List<Element> weatherInfoEltList = 
				(List<Element>) document.selectObject(ITEM_WEATHER_INFO_PATH);
		List<Element> windEltList = 
				(List<Element>) document.selectObject(ITEM_WIND_PATH);
		List<Element> temperatureEltList = 
				(List<Element>) document.selectObject(ITEM_TEMPERATURE_PATH);
		
		int totalDays = dateEltList.size();
		for(int i=0;i<totalDays;i++){
			Element dateElt = dateEltList.get(i);
			Element dayPicUrlElt = dayPicUrlEltList.get(i);
			Element nightPicUrlElt = nightPicUrlEltList.get(i);
			Element weatherInfoElt = weatherInfoEltList.get(i);
			Element windElt = windEltList.get(i);
			Element temperatureElt = temperatureEltList.get(i);
			
			String weatherDateStr = DateUtil.getSpecifiedDayAfterDays(dateFirst, i);
			
			WeatherDayInfo weatherDayInfo = new WeatherDayInfo();
			weatherDayInfo.setCountyId(countyId);
			weatherDayInfo.setWeatherDateString(weatherDateStr);
			weatherDayInfo.setDayPictureUrl(dayPicUrlElt.getStringValue());
			weatherDayInfo.setNightPictureUrl(nightPicUrlElt.getStringValue());
			weatherDayInfo.setWeatherInfo(weatherInfoElt.getStringValue());
			weatherDayInfo.setWind(windElt.getStringValue());
			
			String tempTemperature = temperatureElt.getStringValue();
			String formatTemperature = tempTemperature.replace(" ~ ", "/");
			weatherDayInfo.setTemperature(formatTemperature);
			
			weatherDayInfo.setUpdateTimeString(updateTimeString);
			if(0 == i){
				String infoString = dateElt.getStringValue();
				String[] infoPartArray = infoString.split("\\s");
				
				String week = infoPartArray[0];
				weatherDayInfo.setWeek(week);
				
				String tempRealTimeTemp = infoPartArray[2];
				int index = tempRealTimeTemp.indexOf('£º');
				String realTimeTemp = 
						tempRealTimeTemp.substring(index+1, tempRealTimeTemp.length()-1);
				weatherDayInfo.setRealTimeTemperature(realTimeTemp);
			}else{
				String week = dateElt.getStringValue();
				weatherDayInfo.setWeek(week);
				weatherDayInfo.setRealTimeTemperature("");
			}
			
			weatherDayInfoList.add(weatherDayInfo);
		}
		return weatherDayInfoList;
	}

}
