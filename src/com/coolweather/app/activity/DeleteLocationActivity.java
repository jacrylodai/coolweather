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
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeleteLocationActivity extends Activity {
	
	private SharedPreferences pref;
	
	private Button backButton;
	
	private ListView locationListView;
	
	private ArrayAdapter<String> listViewAdapter;
	
	private Button deleteCountyButton;
	
	private List<String> countyNameList = new ArrayList<String>();
	
	private List<County> countyList;
	
	private List<Integer> countyIdList;
	
	private boolean isDefaultCountyIdSet;
	
	private County defaultCounty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.delete_location_layout);
		
		backButton = (Button)findViewById(R.id.back_button);
		locationListView = 
				(ListView)findViewById(R.id.location_list_view);
		
		deleteCountyButton = 
				(Button) findViewById(R.id.delete_county_button);
		
		pref = getSharedPreferences(Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
		
		listViewAdapter = new ArrayAdapter<String>(this
				, android.R.layout.simple_list_item_multiple_choice
				, countyNameList);
		locationListView.setAdapter(listViewAdapter);
		locationListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
					
		backButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				finish();
			}
		});
		
		deleteCountyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				SparseBooleanArray boolArray = locationListView.getCheckedItemPositions();
				List<Integer> newIdList = new ArrayList<Integer>();
				
				for(int i=0;i<countyIdList.size();i++){
					Integer countyId = countyIdList.get(i);
					boolean isChecked = boolArray.get(i);
					if(!isChecked){
						newIdList.add(countyId);
					}
				}
				if(newIdList.size() == countyIdList.size()){
					Toast.makeText(DeleteLocationActivity.this, "你没有选择城市"
							, Toast.LENGTH_LONG).show();
				}else{
					LocationService.saveCountyIdListInPref(pref, newIdList);
										
					if(newIdList.size() == 0){
						LocationService.removeDefaultCountyId(pref);
					}else{
						if(!newIdList.contains(defaultCounty.getCountyId())){
							int newDefaultId = newIdList.get(0);
							LocationService.setDefaultCountyId(pref, newDefaultId);
						}
					}
					
					DeleteLocationActivity.this.setResult(Activity.RESULT_OK);
					DeleteLocationActivity.this.finish();
				}
				
				
			}
		});

		checkIsDefaultCountyIdSet();
		loadLocationData();
	}

	private void checkIsDefaultCountyIdSet(){

		isDefaultCountyIdSet = LocationService.isDefaultCountyIdSet(pref);
		
		if(!isDefaultCountyIdSet){
			Toast.makeText(this, "未设置城市，请进入-城市管理 里进行设置", Toast.LENGTH_LONG)
				.show();
			return;
		}else{

			int defaultCountyId = LocationService.getDefaultCountyId(pref);
			defaultCounty = 
					CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
		}
	}

	private void loadLocationData() {

		countyNameList.clear();
		
		countyIdList = LocationService.getCountyIdListFromPref(pref);
		if(countyIdList.size()>0){
			countyList = 
					CoolWeatherDB.getInstance(this).getCountyListByCountyIdList(countyIdList);
			
			for(int i=0;i<countyList.size();i++){
				County county = countyList.get(i);
				countyNameList.add(county.getCountyName());				
			}
		}
		
		updateUI();
	}

	private void updateUI() {

		listViewAdapter.notifyDataSetChanged();
		locationListView.setSelection(0);
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
