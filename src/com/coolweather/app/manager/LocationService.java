package com.coolweather.app.manager;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.Province;

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
		
		List<String> provinceNameList = new ArrayList<String>();
		for(int i=0;i<provinceList.size();i++){
			Province province = provinceList.get(i);
			subLocationList.add(province.getProvinceName());
		}
	}

}
