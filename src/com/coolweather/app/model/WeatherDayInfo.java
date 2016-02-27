package com.coolweather.app.model;

import java.util.Date;

public class WeatherDayInfo {

	private Integer weatherDayInfoId;
	
	private Integer countyId;
	
	private String week;
	
	private String weatherDateString;
	
	private String dayPictureUrl;
	
	private String nightPictureUrl;
	
	private String weatherInfo;
	
	private String wind;
	
	private String temperature;
	
	private String realTimeTemperature;
	
	private String updateTimeString;

	public Integer getWeatherDayInfoId() {
		return weatherDayInfoId;
	}

	public void setWeatherDayInfoId(Integer weatherDayInfoId) {
		this.weatherDayInfoId = weatherDayInfoId;
	}

	public Integer getCountyId() {
		return countyId;
	}

	public void setCountyId(Integer countyId) {
		this.countyId = countyId;
	}

	public void setCountyId(int countyId) {
		this.countyId = countyId;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getDayPictureUrl() {
		return dayPictureUrl;
	}

	public void setDayPictureUrl(String dayPictureUrl) {
		this.dayPictureUrl = dayPictureUrl;
	}

	public String getNightPictureUrl() {
		return nightPictureUrl;
	}

	public void setNightPictureUrl(String nightPictureUrl) {
		this.nightPictureUrl = nightPictureUrl;
	}

	public String getWeatherInfo() {
		return weatherInfo;
	}

	public void setWeatherInfo(String weatherInfo) {
		this.weatherInfo = weatherInfo;
	}

	public String getWind() {
		return wind;
	}

	public void setWind(String wind) {
		this.wind = wind;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getRealTimeTemperature() {
		return realTimeTemperature;
	}

	public void setRealTimeTemperature(String realTimeTemperature) {
		this.realTimeTemperature = realTimeTemperature;
	}

	public String getWeatherDateString() {
		return weatherDateString;
	}

	public void setWeatherDateString(String weatherDateString) {
		this.weatherDateString = weatherDateString;
	}

	public String getUpdateTimeString() {
		return updateTimeString;
	}

	public void setUpdateTimeString(String updateTimeString) {
		this.updateTimeString = updateTimeString;
	}
	
}
