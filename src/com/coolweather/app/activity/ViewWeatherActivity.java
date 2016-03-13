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
import com.coolweather.app.service.WeatherAutoUpdateService;
import com.coolweather.app.util.Constant;
import com.coolweather.app.util.DateUtil;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.NetworkUtil;

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
	
	private static final int MESSAGE_CONNECT_EXCEPTION = 2;
	
	private static final int REQUEST_MANAGE_LOCATION = 11;
	
	private WeatherDayInfoManager weatherDayInfoManager;
	
	private Button manageLocationButton;
	
	private Button refreshDataButton;
	
	private TextView locationNameView;
	
	private TextView todayRealTimeTemperatureView,todayTemperatureView
					,todayWeatherInfoView,todayWeatherDateView,todayWeekView;
	
	private TextView weatherDayInfoUpdateTimeView,currentDateView;
	
	private GridView weatherDayInfoGridView;
	
	private WeatherDayInfoAdapter weatherDayInfoAdapter;
	
	private List<WeatherDayInfo> weatherDayInfoList = new ArrayList<WeatherDayInfo>();
	
	private SharedPreferences pref;
	
	private boolean isDefaultCountyIdSet;
	
	private County defaultCounty;
	
	private List<Integer> countyIdList;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case MESSAGE_UPDATE_UI:
				loadWeatherData();
				break;
			
			case MESSAGE_CONNECT_EXCEPTION:
				Toast.makeText(ViewWeatherActivity.this
						, R.string.toast_connection_error
						, Toast.LENGTH_SHORT).show();
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
		
		weatherDayInfoUpdateTimeView = 
				(TextView) findViewById(R.id.weather_day_info_update_time_view);
		currentDateView = 
				(TextView) findViewById(R.id.current_date_view);
		
		weatherDayInfoGridView = 
				(GridView) findViewById(R.id.weather_day_info_grid_view);

		pref = getSharedPreferences(
				Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
		
		manageLocationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ViewWeatherActivity.this
						, ManageLocationActivity.class);
				startActivityForResult(intent, REQUEST_MANAGE_LOCATION);
			}
		});
		
		refreshDataButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				checkIsDefaultCountyIdSet();
				if(isDefaultCountyIdSet){
					downloadCountyWeatherInfo();
				}
			}
		});
		
		weatherDayInfoAdapter = 
				new WeatherDayInfoAdapter(this, R.layout.weather_day_info_item_layout
						, weatherDayInfoList);
		weatherDayInfoGridView.setAdapter(weatherDayInfoAdapter);
		
		checkIsDefaultCountyIdSet();
		loadWeatherData();			
		if(isDefaultCountyIdSet){
			downloadCountyWeatherInfo();
		}
		
		Intent serviceIntent = new Intent(ViewWeatherActivity.this, WeatherAutoUpdateService.class);
		startService(serviceIntent);
	}
	
	
	@Override
	protected void onStart() {
		
		super.onStart();
		
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_MANAGE_LOCATION:
			if(resultCode == Activity.RESULT_OK){
				checkIsDefaultCountyIdSet();
				loadWeatherData();			
				if(isDefaultCountyIdSet){	
					downloadCountyWeatherInfo();
				}
			}
			break;

		default:
			break;
		}
	}


	private void loadWeatherData(){

		weatherDayInfoList.clear();
		if(isDefaultCountyIdSet){
			
			List<WeatherDayInfo> tempWeatherDayInfoList = 
					weatherDayInfoManager.getWeatherDayInfoListByCountyId(this
							,defaultCounty.getCountyId());
			
			
			Date currentTime = new Date(System.currentTimeMillis());
			String todayString = DateUtil.dateFormat.format(currentTime);
			
			for(int i=0;i<tempWeatherDayInfoList.size();i++){
				WeatherDayInfo tempWeatherDayInfo = tempWeatherDayInfoList.get(i);
				String tempWeatherDateString = tempWeatherDayInfo.getWeatherDateString();
				if(tempWeatherDateString.compareTo(todayString)>=0){
					weatherDayInfoList.add(tempWeatherDayInfo);
				}
			}
		}
		
		updateUI();
	}

	private void updateUI() {

		if(isDefaultCountyIdSet){
			
			locationNameView.setText(defaultCounty.getCountyName());
			
			//如果有可以显示的天气数据，就进行显示
			if(weatherDayInfoList.size()>0){
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
				String formatWeatherDateString = DateUtil.shortDateFormat.format(weatherDate);
				
				todayWeatherDateView.setText(formatWeatherDateString);
				todayWeekView.setText(todayWeatherDayInfo.getWeek());
				
				weatherDayInfoUpdateTimeView.setText(todayWeatherDayInfo.getUpdateTimeString());
			}else{
				//如果没有数据，就对界面进行重置
				todayRealTimeTemperatureView.setText("");
				todayTemperatureView.setText("");
				todayWeatherInfoView.setText("");
				todayWeatherDateView.setText("");
				todayWeekView.setText("");
				
				weatherDayInfoUpdateTimeView.setText("");
			
			}
		}else{
			
			locationNameView.setText("");
			//如果没有数据，就对界面进行重置
			todayRealTimeTemperatureView.setText("");
			todayTemperatureView.setText("");
			todayWeatherInfoView.setText("");
			todayWeatherDateView.setText("");
			todayWeekView.setText("");
			
			weatherDayInfoUpdateTimeView.setText("");
		}	
		
		Date currentTime = new Date(System.currentTimeMillis());
		String todayString = DateUtil.fullDateFormat.format(currentTime);
		currentDateView.setText(todayString);
		
		weatherDayInfoAdapter.notifyDataSetChanged();
		weatherDayInfoGridView.setSelection(0);
		
	}
	
	private void checkIsDefaultCountyIdSet(){

		isDefaultCountyIdSet = LocationService.isDefaultCountyIdSet(pref);
		
		if(!isDefaultCountyIdSet){
			Toast.makeText(this, R.string.toast_no_city_is_set, Toast.LENGTH_LONG)
				.show();
			return;
		}else{

			int defaultCountyId = LocationService.getDefaultCountyId(pref);
			defaultCounty = 
					CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
		}
	}


	private void downloadCountyWeatherInfo() {

		if(!isDefaultCountyIdSet){
			return;
		}
		
		if(!NetworkUtil.isNetworkAvailable(this)){

			Toast.makeText(ViewWeatherActivity.this
					, R.string.toast_no_network_connection
					, Toast.LENGTH_SHORT).show();
			return;
		}
				
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
				Log.i(TAG, "onFinish");
				weatherDayInfoManager.parseWeatherInfoResponse(ViewWeatherActivity.this
						,defaultCounty.getCountyId(),response);
				
				Message message = new Message();
				message.what = MESSAGE_UPDATE_UI;
				handler.sendMessage(message);
			}
			
			@Override
			public void onError(Exception ex) {
				Log.i(TAG, "onError");

				Message message = new Message();
				message.what = MESSAGE_CONNECT_EXCEPTION;
				handler.sendMessage(message);
				return;
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
	
	@Override
	protected void onRestart() {
		super.onRestart();
		Log.i(TAG,"onRestart");
	}

}
