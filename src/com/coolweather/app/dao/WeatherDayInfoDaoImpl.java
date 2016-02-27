package com.coolweather.app.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
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
				",weather_info,wind,temperature,real_time_temperature,update_time)" +
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

	@Override
	public List<WeatherDayInfo> getWeatherDayInfoListByCountyId(
			SQLiteDatabase db, Integer countyId) {

		List<WeatherDayInfo> weatherDayInfoList = new ArrayList<WeatherDayInfo>();
		
		Cursor cursor = db.rawQuery("select * from t_weather_day_info where county_id=?"
				, new String[]{ countyId.toString() }
				);
		while(cursor.moveToNext()){
			int weatherDayInfoId = cursor.getInt(cursor.getColumnIndex("weather_day_info_id"));
			int tempCountyId = cursor.getInt(cursor.getColumnIndex("county_id"));
			String week = cursor.getString(cursor.getColumnIndex("week"));
			String weatherDateString = cursor.getString(cursor.getColumnIndex("weather_date"));
			String dayPictureUrl = cursor.getString(cursor.getColumnIndex("day_picture_url"));
			String nightPictureUrl = cursor.getString(cursor.getColumnIndex("night_picture_url"));
			String weatherInfo = cursor.getString(cursor.getColumnIndex("weather_info"));
			String wind = cursor.getString(cursor.getColumnIndex("wind"));
			String temperature = cursor.getString(cursor.getColumnIndex("temperature"));
			String realTimeTemperature = cursor.getString(cursor.getColumnIndex("real_time_temperature"));
			String updateTimeString = cursor.getString(cursor.getColumnIndex("update_time"));
			
			WeatherDayInfo weatherDayInfo = new WeatherDayInfo();
			weatherDayInfo.setWeatherDayInfoId(weatherDayInfoId);
			weatherDayInfo.setCountyId(tempCountyId);
			weatherDayInfo.setWeek(week);
			weatherDayInfo.setWeatherDateString(weatherDateString);
			weatherDayInfo.setDayPictureUrl(dayPictureUrl);
			weatherDayInfo.setNightPictureUrl(nightPictureUrl);
			weatherDayInfo.setWeatherInfo(weatherInfo);
			weatherDayInfo.setWind(wind);
			weatherDayInfo.setTemperature(temperature);
			weatherDayInfo.setRealTimeTemperature(realTimeTemperature);
			weatherDayInfo.setUpdateTimeString(updateTimeString);
			
			weatherDayInfoList.add(weatherDayInfo);
		}
		cursor.close();
		
		return weatherDayInfoList;
	}

}
