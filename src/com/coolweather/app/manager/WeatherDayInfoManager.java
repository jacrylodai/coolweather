package com.coolweather.app.manager;

import java.util.List;

import com.coolweather.app.model.County;
import com.coolweather.app.model.WeatherDayInfo;

import android.content.Context;

public interface WeatherDayInfoManager {

	public void parseWeatherInfoResponse(
			Context context, Integer countyId ,String response);
	
	public List<WeatherDayInfo> getWeatherDayInfoListByCountyId(
			Context context, Integer countyId);
	
}
