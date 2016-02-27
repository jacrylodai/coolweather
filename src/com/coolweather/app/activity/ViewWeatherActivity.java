package com.coolweather.app.activity;

import java.net.URLEncoder;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.manager.WeatherDayInfoManager;
import com.coolweather.app.manager.WeatherDayInfoManagerImpl;
import com.coolweather.app.manager.WeatherService;
import com.coolweather.app.model.County;
import com.coolweather.app.util.Constant;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ViewWeatherActivity extends Activity {
	
	private static final String TAG = "ViewWeatherActivity";
	
	private WeatherDayInfoManager weatherDayInfoManager;
	
	private Button manageLocationButton;
	
	private Button refreshDataButton;
	
	private TextView locationNameView,currentDateView,weatherDescView,tempView;
	
	private SharedPreferences pref;
	
	private boolean isDefaultCountyIdSet;
	
	private County defaultCounty;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_weather_layout);
		
		weatherDayInfoManager = WeatherDayInfoManagerImpl.getInstance();
		
		manageLocationButton = (Button)findViewById(R.id.manage_location_button);
		refreshDataButton = (Button)findViewById(R.id.refresh_data_button);
		
		locationNameView = (TextView)findViewById(R.id.location_name_view);
		currentDateView = (TextView)findViewById(R.id.current_date_view);
		weatherDescView = (TextView)findViewById(R.id.weather_desc_view);
		tempView = (TextView)findViewById(R.id.temp_view);

		pref = getSharedPreferences(
				Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
		
		manageLocationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewWeatherActivity.this
						, ManageLocationActivity.class);
				startActivity(intent);
			}
		});

		isDefaultCountyIdSet = 
				pref.contains(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
		if(isDefaultCountyIdSet == false){
			Intent intent = new Intent(ViewWeatherActivity.this
					, ManageLocationActivity.class);
			startActivity(intent);
		}
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();

		isDefaultCountyIdSet = 
				pref.contains(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
		if(isDefaultCountyIdSet){
			loadWeatherData();
		}
	}


	private void loadWeatherData(){

		int defaultCountyId = 
			pref.getInt(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID, -1);
		defaultCounty = 
				CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
		downloadCountyWeatherInfo();
	}

	private void downloadCountyWeatherInfo() {

		StringBuffer sb = new StringBuffer();
		sb.append(WeatherService.URL_BAIDU_WEATHER_API);
		sb.append('?');
		sb.append("location="+
				URLEncoder.encode(defaultCounty.getCountyName()));
		sb.append("&output=xml");
		sb.append("&ak="+Constant.BAIDU_APP_KEY);		
		sb.append("&mcode="+Constant.APP_MCODE);
		
		String webUrl = sb.toString();
		Log.i(TAG, webUrl);
		HttpUtil.sendHttpRequest(webUrl, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.i(TAG, "onFinished");
				weatherDayInfoManager.parseWeatherInfoResponse(ViewWeatherActivity.this
						,defaultCounty,response);
			}
			
			@Override
			public void onError(Exception ex) {
				
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

}
