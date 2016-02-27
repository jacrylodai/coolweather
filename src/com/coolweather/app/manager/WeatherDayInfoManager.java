package com.coolweather.app.manager;

import java.util.List;

import com.coolweather.app.model.County;
import com.coolweather.app.model.WeatherDayInfo;

import android.content.Context;

public interface WeatherDayInfoManager {

	public void saveWeatherDayInfoList(Context context,
			List<WeatherDayInfo> weatherDayInfoList);

	public void parseWeatherInfoResponse(
			Context context, County defaultCounty,String response);
	
	public void deleteWeatherDayInfoByCountyId(Context context, Integer countyId);
	
}
