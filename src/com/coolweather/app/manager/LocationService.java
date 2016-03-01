package com.coolweather.app.manager;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.Constant;

public class LocationService {

	public static final String URL_PROVINCE = 
			"http://www.weather.com.cn/data/list3/city.xml";
	
	public static final String URL_LOCATION_PREFIX = 
			"http://www.weather.com.cn/data/list3/city";

	public static final String URL_LOCATION_SUFIX = 
			".xml";
			
	public static List<Province> parseResponseToProvinceList(String response) {

		List<Province> provinceList = new ArrayList<Province>();
		
		String[] provinceArray = response.split("\\,");
		for(int i=0;i<provinceArray.length;i++){
			String provinceStr = provinceArray[i];
			String[] provincePartArray = provinceStr.split("\\|");
			String provinceCode = provincePartArray[0];
			String provinceName = provincePartArray[1];
			
			Province province = new Province();
			province.setProvinceCode(provinceCode);
			province.setProvinceName(provinceName);
			
			provinceList.add(province);
		}
		return provinceList;
	}
	
	public static void putProvinceNameInList(List<Province> provinceList
			,List<String> subLocationList){
		
		for(int i=0;i<provinceList.size();i++){
			Province province = provinceList.get(i);
			subLocationList.add(province.getProvinceName());
		}
	}

	public static List<City> parseResponseToCityList(String response) {

		List<City> cityList = new ArrayList<City>();
		
		String[] cityArray = response.split("\\,");
		for(int i=0;i<cityArray.length;i++){
			String cityStr = cityArray[i];
			String[] cityPartArray = cityStr.split("\\|");
			String cityCode = cityPartArray[0];
			String cityName = cityPartArray[1];
			
			City city = new City();
			city.setCityName(cityName);
			city.setCityCode(cityCode);
			cityList.add(city);
		}
		return cityList;
	}
	
	public static void putCityNameInList(List<City> cityList,
			List<String> subLocationList){
		
		for(int i=0;i<cityList.size();i++){
			City city = cityList.get(i);
			subLocationList.add(city.getCityName());
		}
	}
	
	public static List<County> parseResponseToCountyList(String response){
		
		List<County> countyList = new ArrayList<County>();
		
		String[] countyArray = response.split("\\,");
		for(int i=0;i<countyArray.length;i++){
			String countyStr = countyArray[i];
			String[] countyPartArray = countyStr.split("\\|");
			String countyCode = countyPartArray[0];
			String countyName = countyPartArray[1];
			
			County county = new County();
			county.setCountyName(countyName);
			county.setCountyCode(countyCode);
			
			countyList.add(county);
		}
		return countyList;
	}
	
	public static void putCountyNameInList(List<County> countyList
			,List<String> subLocationList){
		
		for(int i=0;i<countyList.size();i++){
			County county = countyList.get(i);
			subLocationList.add(county.getCountyName());
		}
	}
	
	public static void setDefaultCountyId(SharedPreferences pref
			,Integer defaultCountyId){
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID, defaultCountyId);
		editor.commit();
	}
	
	public static List<Integer> getCountyIdListFromPref(SharedPreferences pref){
		
		List<Integer> countyIdList = new ArrayList<Integer>();
		String countyIdListStr = pref.getString(Constant.CONFIG_ITEM_COUNTY_ID_LIST, "");
		if(countyIdListStr.length() > 0){
			
			String [] countyIdArray = countyIdListStr.split("\\,");
			for(int i=0;i<countyIdArray.length;i++){
				String countyIdStr = countyIdArray[i];
				Integer countyId = Integer.parseInt(countyIdStr);
				countyIdList.add(countyId);
			}
		}
		return countyIdList;		
	}
	
	public static void saveCountyIdListInPref(SharedPreferences pref,
			List<Integer> countyIdList){
		
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<countyIdList.size();i++){
			Integer countyId = countyIdList.get(i);
			
			if(0 == i){
				sb.append(countyId.toString());
			}else{
				sb.append(','+countyId.toString());
			}
		}
		
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(Constant.CONFIG_ITEM_COUNTY_ID_LIST, sb.toString());
		editor.commit();
	}

	public static void removeDefaultCountyId(SharedPreferences pref) {

		SharedPreferences.Editor editor = pref.edit();
		editor.remove(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
		editor.commit();
		
	}
	
	public static boolean isDefaultCountyIdSet(SharedPreferences pref){
		
		return pref.contains(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID);
	}

	public static int getDefaultCountyId(SharedPreferences pref) {
		
		if(isDefaultCountyIdSet(pref)){
			return pref.getInt(Constant.CONFIG_ITEM_DEFAULT_COUNTY_ID, -1);
		}else{
			throw new RuntimeException("没有设置城市");
		}
	}
	
}
