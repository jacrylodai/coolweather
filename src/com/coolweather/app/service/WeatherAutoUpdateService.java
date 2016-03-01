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

public class WeatherAutoUpdateService extends Service{
	
	private static final String TAG = "WeatherAutoUpdateService";
	
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
		List<County> countyList = 
				CoolWeatherDB.getInstance(this).getCountyListByCountyIdList(countyIdList);
		for(int i=0;i<countyList.size();i++){
			County county = countyList.get(i);
			downloadCountyWeatherInfo(county);
		}
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		long waitTime = 8*60*60*1000;
		long triggerTime = SystemClock.elapsedRealtime() + waitTime;
		
		Intent updateReceiverIntent = new Intent(this,WeatherAutoUpdateReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, 0, updateReceiverIntent, 0);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pi);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void downloadCountyWeatherInfo(final County county) {
	
		StringBuffer sb = new StringBuffer();
		sb.append(WeatherService.URL_BAIDU_WEATHER_API);
		sb.append('?');
		sb.append("location="+
				URLEncoder.encode(county.getCountyName()));
		sb.append("&output=xml");
		sb.append("&ak="+Constant.BAIDU_APP_KEY);		
		sb.append("&mcode="+Constant.APP_MCODE);
		
		String webUrl = sb.toString();
		Log.i(TAG, webUrl);
		HttpUtil.sendHttpRequest(webUrl, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.i(TAG, "onFinish");
				weatherDayInfoManager.parseWeatherInfoResponse(
						WeatherAutoUpdateService.this
						,county.getCountyId(),response);				
			}
			
			@Override
			public void onError(Exception ex) {
				Log.i(TAG, "onError");
			}
		});
	}

}
