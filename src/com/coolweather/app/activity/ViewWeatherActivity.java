package com.coolweather.app.activity;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.adapter.WeatherDayInfoAdapter;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.manager.WeatherDayInfoManager;
import com.coolweather.app.manager.WeatherDayInfoManagerImpl;
import com.coolweather.app.manager.WeatherService;
import com.coolweather.app.model.County;
import com.coolweather.app.model.WeatherDayInfo;
import com.coolweather.app.util.Constant;
import com.coolweather.app.util.DateUtil;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewWeatherActivity extends Activity {
	
	private static final String TAG = "ViewWeatherActivity";
	
	private static final int MESSAGE_UPDATE_UI = 1;
	
	private WeatherDayInfoManager weatherDayInfoManager;
	
	private Button manageLocationButton;
	
	private Button refreshDataButton;
	
	private TextView locationNameView;
	
	private TextView todayRealTimeTemperatureView,todayTemperatureView
					,todayWeatherInfoView,todayWeatherDateView,todayWeekView;
	
	private GridView weatherDayInfoGridView;
	
	private WeatherDayInfoAdapter weatherDayInfoAdapter;
	
	private List<WeatherDayInfo> weatherDayInfoList = new ArrayList<WeatherDayInfo>();
	
	private SharedPreferences pref;
	
	private boolean isDefaultCountyIdSet;
	
	private County defaultCounty;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case MESSAGE_UPDATE_UI:
				loadWeatherData();
				break;
			default:
				break;
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_weather_layout);
		
		weatherDayInfoManager = WeatherDayInfoManagerImpl.getInstance();
		
		manageLocationButton = (Button)findViewById(R.id.manage_location_button);
		refreshDataButton = (Button)findViewById(R.id.refresh_data_button);
		
		locationNameView = (TextView)findViewById(R.id.location_name_view);
		
		todayRealTimeTemperatureView = 
				(TextView) findViewById(R.id.today_real_time_temperature_view);
		todayTemperatureView = 
				(TextView) findViewById(R.id.today_temperature_view);
		todayWeatherInfoView = 
				(TextView) findViewById(R.id.today_weather_info_view);
		todayWeatherDateView = 
				(TextView) findViewById(R.id.today_weather_date_view);
		todayWeekView = 
				(TextView) findViewById(R.id.today_week_view);
		
		weatherDayInfoGridView = 
				(GridView) findViewById(R.id.weather_day_info_grid_view);

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
		
		weatherDayInfoAdapter = 
				new WeatherDayInfoAdapter(this, R.layout.weather_day_info_item_layout
						, weatherDayInfoList);
		weatherDayInfoGridView.setAdapter(weatherDayInfoAdapter);

	}
	
	
	@Override
	protected void onStart() {
		super.onStart();

		isDefaultCountyIdSet = 
				pref.contains(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
		if(isDefaultCountyIdSet){

			int defaultCountyId = 
				pref.getInt(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID, -1);
			defaultCounty = 
					CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
			
			downloadCountyWeatherInfo();
		}else{
			Toast.makeText(this, "未设置城市，请进入-城市管理 里进行设置", Toast.LENGTH_LONG)
				.show();
		}
	}
	
	private void loadWeatherData(){
		
		List<WeatherDayInfo> tempWeatherDayInfoList = 
				weatherDayInfoManager.getWeatherDayInfoListByCountyId(this
						,defaultCounty.getCountyId());
		if(tempWeatherDayInfoList.size() == 0){
			Toast.makeText(this, "数据库中没有天气数据，检查是否开启手机网络"
					, Toast.LENGTH_LONG).show();
			return;
		}
		
		weatherDayInfoList.clear();
		for(int i=0;i<tempWeatherDayInfoList.size();i++){
			WeatherDayInfo tempWeatherDayInfo = tempWeatherDayInfoList.get(i);
			weatherDayInfoList.add(tempWeatherDayInfo);
		}
		updateUI();
	}

	private void updateUI() {

		locationNameView.setText(defaultCounty.getCountyName());
		
		WeatherDayInfo todayWeatherDayInfo = weatherDayInfoList.get(0);
		todayRealTimeTemperatureView.setText(todayWeatherDayInfo.getRealTimeTemperature());
		todayTemperatureView.setText(todayWeatherDayInfo.getTemperature());
		todayWeatherInfoView.setText(todayWeatherDayInfo.getWeatherInfo());

		Date weatherDate = null;
		try {
			weatherDate = DateUtil.dateFormat.parse(todayWeatherDayInfo.getWeatherDateString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String formatWeatherDateString = DateUtil.weatherDateFormat.format(weatherDate);
		
		todayWeatherDateView.setText(formatWeatherDateString);
		todayWeekView.setText(todayWeatherDayInfo.getWeek());
		
		weatherDayInfoAdapter.notifyDataSetChanged();
		weatherDayInfoGridView.setSelection(0);
		
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
				
				Message message = new Message();
				message.what = MESSAGE_UPDATE_UI;
				handler.sendMessage(message);
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
