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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ManageLocationActivity extends Activity {
	
	private static final int REQUEST_ADD_LOCATION = 1;
	
	private static final int REQUEST_DELETE_LOCATION = 2;
	
	private SharedPreferences pref;
	
	private Button backButton;
	
	private ListView locationListView;
	
	private ArrayAdapter<String> listViewAdapter;
	
	private Button addLocationButton;
	
	private Button changeDeleteCountyButton;
	
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
		locationListView = 
				(ListView)findViewById(R.id.location_list_view);

		addLocationButton = (Button)findViewById(R.id.add_location_button);
		changeDeleteCountyButton = 
				(Button) findViewById(R.id.change_delete_county_button);
		
		pref = getSharedPreferences(Constant.CONFIG_LOCATION_FILE, Context.MODE_PRIVATE);
		
		listViewAdapter = new ArrayAdapter<String>(this
				, android.R.layout.simple_list_item_1
				, countyNameList);
		locationListView.setAdapter(listViewAdapter);
				
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

				setResult(Activity.RESULT_OK);
				finish();
			}
		});
		
		locationListView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				County county = countyList.get(position);
				LocationService.setDefaultCountyId(pref, county.getCountyId());
				
				ManageLocationActivity.this.setResult(Activity.RESULT_OK);
				ManageLocationActivity.this.finish();
			}
		});
		
		changeDeleteCountyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if(countyIdList.size() == 0){
					Toast.makeText(ManageLocationActivity.this
							, R.string.toast_empty_city, Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(ManageLocationActivity.this
						,DeleteLocationActivity.class);
				startActivityForResult(intent, REQUEST_DELETE_LOCATION);
			}
		});

		checkIsDefaultCountyIdSet();
		loadLocationData();
	}

	private void checkIsDefaultCountyIdSet(){

		isDefaultCountyIdSet = LocationService.isDefaultCountyIdSet(pref);
		
		if(isDefaultCountyIdSet){

			int defaultCountyId = LocationService.getDefaultCountyId(pref);
			defaultCounty = 
					CoolWeatherDB.getInstance(this).loadCountyById(defaultCountyId);
		}
	}

	private void loadLocationData() {

		countyIdList = LocationService.getCountyIdListFromPref(pref);
		countyNameList.clear();
		
		if(countyIdList.size()>0){
			countyList = 
					CoolWeatherDB.getInstance(this).getCountyListByCountyIdList(countyIdList);
			
			for(int i=0;i<countyList.size();i++){
				County county = countyList.get(i);
				if(defaultCounty != null && 
						county.getCountyId() == defaultCounty.getCountyId()){
					countyNameList.add(county.getCountyName()+" ¡ª¡ª Ä¬ÈÏ³ÇÊÐ");
				}else{
					countyNameList.add(county.getCountyName());				
				}
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
				if(countyIdList.contains(selectedCountyId)){
					return;
				}
				
				countyIdList.add(selectedCountyId);
				
				if(countyIdList.size() == 1){
					LocationService.setDefaultCountyId(pref, selectedCountyId);					
				}
				LocationService.saveCountyIdListInPref(pref, countyIdList);
				
				checkIsDefaultCountyIdSet();
				loadLocationData();
			}
			break;
			
		case REQUEST_DELETE_LOCATION:

			if(resultCode == Activity.RESULT_OK){
				
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
		setResult(Activity.RESULT_OK);
	}

}
