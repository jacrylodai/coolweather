package com.coolweather.app.dao;

import java.util.List;

import com.coolweather.app.model.WeatherDayInfo;

import android.database.sqlite.SQLiteDatabase;

public interface WeatherDayInfoDao {

	public void saveWeatherDayInfo(SQLiteDatabase db,WeatherDayInfo weatherDayInfo);

	public void deleteWeatherDayInfoByCountyId(SQLiteDatabase db,
			Integer countyId);

	public List<WeatherDayInfo> getWeatherDayInfoListByCountyId(
			SQLiteDatabase db, Integer countyId);
	
}
