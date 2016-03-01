package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class SetLocationActivity extends Activity {
	
	private static final int LEVEL_PROVINCE = 1;
	
	private static final int LEVEL_CITY = 2;
	
	private static final int LEVEL_COUNTY = 3;
	
	private static final int MESSAGE_UPDATE_UI = 11;
	
	private Button backButton;
	
	private TextView mainLocationView;
	
	private ListView subLocationListView;
	
	private ArrayAdapter<String> listViewAdapter;
	
	private String mainLocation;
	
	private List<String> subLocationList = new ArrayList<String>();
	
	private int currentLevel;
	
	private List<Province> provinceList;
	
	private Province selectedProvince;
	
	private List<City> cityList;
	
	private City selectedCity;
	
	private List<County> countyList;
	
	private County selectedCounty;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case MESSAGE_UPDATE_UI:
				loadLocationData();
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
		setContentView(R.layout.set_location_layout);
		
		backButton = (Button)findViewById(R.id.back_button);
		mainLocationView = (TextView)findViewById(R.id.main_location_view);
		subLocationListView = (ListView)findViewById(R.id.sub_location_list_view);

		listViewAdapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
						, subLocationList);
		subLocationListView.setAdapter(listViewAdapter);
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				SetLocationActivity.this.onBackPressed();
			}
		});
		
		subLocationListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						switch(currentLevel){
						case LEVEL_PROVINCE:							
							selectedProvince = provinceList.get(position);
							currentLevel = LEVEL_CITY;
							loadLocationData();
							break;
							
						case LEVEL_CITY:
							selectedCity = cityList.get(position);
							currentLevel = LEVEL_COUNTY;
							loadLocationData();
							break;
							
						case LEVEL_COUNTY:
							selectedCounty = countyList.get(position);
							
							Intent intent = new Intent();
							intent.putExtra("selectedCountyId", selectedCounty.getCountyId());
							SetLocationActivity.this.setResult(Activity.RESULT_OK,intent);
							SetLocationActivity.this.finish();
						}
					}
		});
		
		currentLevel = LEVEL_PROVINCE;
		
		loadLocationData();
	}

	private void loadLocationData() {
		
		switch(currentLevel){
		case LEVEL_PROVINCE:
			provinceList = 
					CoolWeatherDB.getInstance(this).getProvinceList();
			if(provinceList.size() == 0){			
				downloadProvinceList();
			}else{			
				mainLocation = "全国";
				subLocationList.clear();
				LocationService.putProvinceNameInList(provinceList,subLocationList);
				refreshUI();
			}
			break;
			
		case LEVEL_CITY:
			cityList = CoolWeatherDB.getInstance(this)
				.getCityListByProvinceId(selectedProvince.getProvinceId());
			if(cityList.size() == 0){
				downloadCityList();
			}else{
				mainLocation = "省-"+selectedProvince.getProvinceName();
				subLocationList.clear();
				LocationService.putCityNameInList(cityList, subLocationList);
				refreshUI();
			}
			break;
			
		case LEVEL_COUNTY:
			countyList = CoolWeatherDB.getInstance(SetLocationActivity.this)
				.getCountyListByCityId(selectedCity.getCityId());
			if(countyList.size() == 0){
				downloadCountyList();
			}else{
				mainLocation = "市-"+selectedCity.getCityName();
				subLocationList.clear();
				LocationService.putCountyNameInList(countyList, subLocationList);
				refreshUI();
			}
		}
	}		
	
	private void refreshUI() {

		mainLocationView.setText(mainLocation);
		listViewAdapter.notifyDataSetChanged();
		subLocationListView.setSelection(0);
	}

	private void downloadProvinceList(){

		HttpUtil.sendHttpRequest(LocationService.URL_PROVINCE
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				List<Province> tempProvinceList = 
						LocationService.parseResponseToProvinceList(response);
				CoolWeatherDB.getInstance(SetLocationActivity.this)
					.saveProvinceList(tempProvinceList);
				
				Message message = new Message();
				message.what = MESSAGE_UPDATE_UI;
				handler.sendMessage(message);
			}
			@Override
			public void onError(Exception ex) {
				
			}
		});
	}

	private void downloadCityList(){

		String webUrl = 
				LocationService.URL_LOCATION_PREFIX 
				+ selectedProvince.getProvinceCode() 
				+ LocationService.URL_LOCATION_SUFIX;
		
		HttpUtil.sendHttpRequest(webUrl
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				List<City> tempCityList = 
						LocationService.parseResponseToCityList(response);
				
				int provinceId = selectedProvince.getProvinceId();
				for(int i=0;i<tempCityList.size();i++){
					City tempCity = tempCityList.get(i);
					tempCity.setProvinceId(provinceId);
				}
				
				CoolWeatherDB.getInstance(SetLocationActivity.this).saveCityList(
						tempCityList);
				Message message = new Message();
				message.what = MESSAGE_UPDATE_UI;
				handler.sendMessage(message);
			}
			@Override
			public void onError(Exception ex) {
				
			}
		});
	}	

	private void downloadCountyList(){

		String webUrl = 
				LocationService.URL_LOCATION_PREFIX 
				+ selectedCity.getCityCode()
				+ LocationService.URL_LOCATION_SUFIX;
		
		HttpUtil.sendHttpRequest(webUrl
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				List<County> tempCountyList =  LocationService.parseResponseToCountyList(
						response);
				
				int cityId = selectedCity.getCityId();
				for(int i=0;i<tempCountyList.size();i++){
					County tempCounty = tempCountyList.get(i);
					tempCounty.setCityId(cityId);
				}
				
				CoolWeatherDB.getInstance(SetLocationActivity.this).saveCountyList(
						tempCountyList);
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
		
		switch (currentLevel) {
		case LEVEL_COUNTY:
			currentLevel = LEVEL_CITY;
			loadLocationData();
			break;
			
		case LEVEL_CITY:
			currentLevel = LEVEL_PROVINCE;
			loadLocationData();
			break;
			
		case LEVEL_PROVINCE:
			finish();
			break;

		default:
			break;
		}
	}
	

}
