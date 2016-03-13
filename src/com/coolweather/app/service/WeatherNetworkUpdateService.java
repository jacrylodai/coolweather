package com.coolweather.app.service;

import java.net.URLEncoder;
import java.util.List;

import com.coolweather.app.activity.ViewWeatherActivity;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.manager.WeatherDayInfoManager;
import com.coolweather.app.manager.WeatherDayInfoManagerImpl;
import com.coolweather.app.manager.WeatherService;
import com.coolweather.app.model.County;
import com.coolweather.app.receiver.WeatherAutoUpdateReceiver;
import com.coolweather.app.util.Constant;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.NetworkUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

public class WeatherNetworkUpdateService extends Service{
	
	private static final String TAG = "WeatherUpdateService";
	
	private WeatherDayInfoManager weatherDayInfoManager;
	
	private SharedPreferences pref;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		
		super.onCreate();
		Log.i(TAG, "onStart");
		weatherDayInfoManager = WeatherDayInfoManagerImpl.getInstance();	
		pref = getSharedPreferences(Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "onStartCommand");
		List<Integer> countyIdList = LocationService.getCountyIdListFromPref(pref);
		
		if(countyIdList.size()>0 && NetworkUtil.isNetworkAvailable(this)){

			List<County> countyList = 
					CoolWeatherDB.getInstance(this).getCountyListByCountyIdList(countyIdList);
			for(int i=0;i<countyList.size();i++){
				County county = countyList.get(i);
				WeatherService.downloadCountyWeatherInfo(weatherDayInfoManager, this, county);
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy");
	}

}
