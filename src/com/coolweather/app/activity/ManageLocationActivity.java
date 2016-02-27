package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.manager.LocationService;
import com.coolweather.app.model.County;
import com.coolweather.app.util.Constant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ManageLocationActivity extends Activity {
	
	private static final int REQUEST_ADD_LOCATION = 11;
	
	private SharedPreferences pref;
	
	private Button backButton;
	
	private Button addLocationButton;
	
	private ListView locationListView;
	
	private ArrayAdapter<String> listViewAdapter;
	
	private Button setDefaultCountyButton;
	
	private List<String> countyNameList = new ArrayList<String>();
	
	private List<County> countyList;
	
	private List<Integer> countyIdList;
	
	private boolean isDefaultCountyIdSet;
	
	private County defaultCounty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.manage_location_layout);
		
		backButton = (Button)findViewById(R.id.back_button);
		addLocationButton = (Button)findViewById(R.id.add_location_button);
		locationListView = 
				(ListView)findViewById(R.id.location_list_view);
		
		setDefaultCountyButton = 
				(Button) findViewById(R.id.set_default_county_button);
		
		pref = getSharedPreferences(Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
		
		listViewAdapter = new ArrayAdapter<String>(this
				, android.R.layout.simple_list_item_single_choice
				, countyNameList);
		locationListView.setAdapter(listViewAdapter);
		locationListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
				
		addLocationButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(ManageLocationActivity.this
						,SetLocationActivity.class);
				startActivityForResult(intent, REQUEST_ADD_LOCATION);				
			}
		});
		
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		setDefaultCountyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				int position = locationListView.getCheckedItemPosition();
				if(position != AbsListView.INVALID_POSITION){
					int defaultCountyId = countyIdList.get(position);
					LocationService.setDefaultCountyId(pref, defaultCountyId);
					
					checkIsDefaultCountyIdSet();
					loadLocationData();
				}else{
					Toast.makeText(ManageLocationActivity.this
							, "你没有选择城市", Toast.LENGTH_LONG).show();
				}
			}
		});

		checkIsDefaultCountyIdSet();
		loadLocationData();
	}

	private void checkIsDefaultCountyIdSet(){

		isDefaultCountyIdSet = 
				pref.contains(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
		if(isDefaultCountyIdSet){

			int defaultCountyId = 
				pref.getInt(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID, -1);
			defaultCounty = 
					CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
		}
	}

	private void loadLocationData() {

		countyIdList = LocationService.getCountyIdListFromPref(pref);
		countyList = 
				CoolWeatherDB.getInstance(this).getCountyListByCountyIdList(countyIdList);
		countyNameList.clear();

		for(int i=0;i<countyList.size();i++){
			County county = countyList.get(i);
			if(defaultCounty != null && 
					county.getCountyId() == defaultCounty.getCountyId()){
				countyNameList.add(county.getCountyName()+" —— 默认城市");
			}else{
				countyNameList.add(county.getCountyName());				
			}
		}		
		
		refreshUI();
	}

	private void refreshUI() {

		listViewAdapter.notifyDataSetChanged();
		locationListView.setSelection(0);
	}
		

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch(requestCode){
		
		case REQUEST_ADD_LOCATION:
			
			if(resultCode == Activity.RESULT_OK){
				Integer selectedCountyId = data.getIntExtra("selectedCountyId", -1);
				countyIdList.add(selectedCountyId);
				
				if(countyIdList.size() == 1){
					LocationService.setDefaultCountyId(pref, selectedCountyId);					
				}
				LocationService.saveCountyIdListInPref(pref, countyIdList);
				
				checkIsDefaultCountyIdSet();
				loadLocationData();
			}
			break;
		
		default:
			break;
		}
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
