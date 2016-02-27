package com.coolweather.app.dao;

import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.WeatherDayInfo;
import com.coolweather.app.util.DateUtil;

public class WeatherDayInfoDaoImpl implements WeatherDayInfoDao{

	@Override
	public void saveWeatherDayInfo(SQLiteDatabase db,
			WeatherDayInfo weatherDayInfo) {

		String sql = 
				"insert into t_weather_day_info" +
				" (county_id,week,weather_date,day_picture_url,night_picture_url" +
				",weather_info,wind,temperature,realtime_temperature,update_time)" +
				" values (?,?,?,?,?,?,?,?,?,?)";
		
		db.execSQL(sql
				, new Object[]{
				weatherDayInfo.getCountyId()
				,weatherDayInfo.getWeek()
				,weatherDayInfo.getWeatherDateString()
				,weatherDayInfo.getDayPictureUrl()
				,weatherDayInfo.getNightPictureUrl()
				,weatherDayInfo.getWeatherInfo()
				,weatherDayInfo.getWind()
				,weatherDayInfo.getTemperature()
				,weatherDayInfo.getRealTimeTemperature()
				,weatherDayInfo.getUpdateTimeString()
				}
		);
		
	}

	@Override
	public void deleteWeatherDayInfoByCountyId(SQLiteDatabase db,
			Integer countyId) {

		db.execSQL("delete from t_weather_day_info where county_id=?"
				, new String[]{countyId.toString()}
				);
		
	}

}
