package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SetLocationActivity extends Activity {
	
	private static final int LEVEL_PROVINCE = 1;
	
	private static final int LEVEL_CITY = 2;
	
	private static final int LEVEL_COUNTY = 3;
	
	private static final int MESSAGE_UPDATE_UI = 11;
	
	private TextView mainLocationView;
	
	private ListView subLocationListView;
	
	private ArrayAdapter<String> listViewAdapter;
	
	private String mainLocation;
	
	private List<String> subLocationList = new ArrayList<String>();
	
	private int currentLevel;
	
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
		
		mainLocationView = (TextView)findViewById(R.id.main_location_view);
		subLocationListView = (ListView)findViewById(R.id.sub_location_list_view);

		listViewAdapter = 
				new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1
						, subLocationList);
		subLocationListView.setAdapter(listViewAdapter);
		
		currentLevel = LEVEL_PROVINCE;
		
		loadLocationData();
	}

	private void loadLocationData() {
		
		List<Province> provinceList = 
				CoolWeatherDB.getInstance(this).getProvinceList();
		if(provinceList.size() == 0){			
			downloadProvinceList();
		}else{			
			mainLocation = "È«¹ú";
			subLocationList.clear();
			LocationService.putProvinceNameInList(provinceList,subLocationList);
			refreshUI();
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

				List<Province> provinceList = 
						LocationService.parseResponseToProvinceList(response);
				CoolWeatherDB.getInstance(SetLocationActivity.this)
					.saveProvinceList(provinceList);
				
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

}
