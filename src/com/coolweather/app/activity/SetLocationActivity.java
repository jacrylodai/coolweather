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
import com.coolweather.app.util.NetworkUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SetLocationActivity extends Activity {
	
	private static final String TAG = "SetLocationActivity";
	
	private static final int LEVEL_PROVINCE = 1;
	
	private static final int LEVEL_CITY = 2;
	
	private static final int LEVEL_COUNTY = 3;
	
	private static final int MESSAGE_UPDATE_UI = 1;
	
	private static final int MESSAGE_CONNECT_EXCEPTION = 2;
	
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
	
	private ProgressDialog dialog;
	
	private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what){
			case MESSAGE_UPDATE_UI:
				loadLocationData();
				break;
				
			case MESSAGE_CONNECT_EXCEPTION:
				if(currentLevel>LEVEL_PROVINCE){
					currentLevel --;
					loadLocationData();
				}else{
					loadEmptyData();
				}
				Toast.makeText(SetLocationActivity.this, R.string.toast_connection_error
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
		setContentView(R.layout.set_location_layout);
		
		backButton = (Button)findViewById(R.id.back_button);
		mainLocationView = (TextView)findViewById(R.id.main_location_view);
		subLocationListView = (ListView)findViewById(R.id.sub_location_list_view);
		
		dialog = new ProgressDialog(SetLocationActivity.this);
		dialog.setTitle(R.string.dialog_loading_city_data);
		dialog.setMessage(getString(R.string.dialog_is_loading));
		dialog.setCancelable(false);

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

						dialog.show();
						
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
		
		dialog.show();
		
		currentLevel = LEVEL_PROVINCE;
		
		loadLocationData();
	}
	
	private void loadEmptyData(){
		
		subLocationList.clear();

		mainLocationView.setText("");
		listViewAdapter.notifyDataSetChanged();
		subLocationListView.setSelection(0);
		
		dialog.dismiss();
	}

	private void loadLocationData() {
		
		if(!NetworkUtil.isNetworkAvailable(this)){

			Toast.makeText(this
					, R.string.toast_no_network_connection
					, Toast.LENGTH_SHORT).show();
			loadEmptyData();
			return;
		}
		
		switch(currentLevel){
		case LEVEL_PROVINCE:
			provinceList = 
					CoolWeatherDB.getInstance(this).getProvinceList();
			if(provinceList.size() == 0){			
				downloadProvinceList();
			}else{			
				mainLocation = getString(R.string.view_country);
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
				mainLocation = getString(R.string.view_province)
						+"-"+selectedProvince.getProvinceName();
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
				mainLocation = getString(R.string.view_county)
						+"-"+selectedCity.getCityName();
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
		
		dialog.dismiss();
	}

	private void downloadProvinceList(){
		
		HttpUtil.sendHttpRequest(LocationService.URL_PROVINCE
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				Log.i(TAG,"onFinish");
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

				Log.i(TAG, "onError");
				handler.sendEmptyMessage(MESSAGE_CONNECT_EXCEPTION);
			}
		});
	}

	private void downloadCityList(){

		if(!NetworkUtil.isNetworkAvailable(this)){

			Toast.makeText(this
					, R.string.toast_no_network_connection
					, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String webUrl = 
				LocationService.URL_LOCATION_PREFIX 
				+ selectedProvince.getProvinceCode() 
				+ LocationService.URL_LOCATION_SUFIX;
		
		HttpUtil.sendHttpRequest(webUrl
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				Log.i(TAG,"onFinish");
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

				Log.i(TAG, "onError");
				handler.sendEmptyMessage(MESSAGE_CONNECT_EXCEPTION);
			}
		});
	}	

	private void downloadCountyList(){

		if(!NetworkUtil.isNetworkAvailable(this)){

			Toast.makeText(this
					, R.string.toast_no_network_connection
					, Toast.LENGTH_SHORT).show();
			return;
		}
		
		String webUrl = 
				LocationService.URL_LOCATION_PREFIX 
				+ selectedCity.getCityCode()
				+ LocationService.URL_LOCATION_SUFIX;
		
		HttpUtil.sendHttpRequest(webUrl
				, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {

				Log.i(TAG, "onFinish");
				List<County> tempCountyList =  LocationService.parseResponseToCountyList(
						response);
				
				int cityId = selectedCity.getCityId();
				for(int i=0;i<tempCountyList.size();i++){
					County tempCounty = tempCountyList.get(i);
					tempCounty.setCityId(cityId);
				}
				
				CoolWeatherDB.getInstance(SetLocationActivity.this).saveCountyList(
						tempCountyList);
				handler.sendEmptyMessage(MESSAGE_UPDATE_UI);
			}
			@Override
			public void onError(Exception ex) {
				
				Log.i(TAG, "onError");
				handler.sendEmptyMessage(MESSAGE_CONNECT_EXCEPTION);
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
