package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoolWeatherDB {

	private static final String DB_NAME = "coolWeatherDB.db";
	
	private static final int DB_VERSION = 1;
	
	private static CoolWeatherDB instance = null;
	
	private CoolWeatherOpenHelper dbHelper;
	
	private SQLiteDatabase db;
	
	private CoolWeatherDB(Context context){
		
		dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, DB_VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	public synchronized static CoolWeatherDB getInstance(Context context){
		if(instance == null){
			instance = new CoolWeatherDB(context);
		}
		return instance;
	}	
	
	public SQLiteDatabase getDB(){
		return db;
	}
	
	public void closeDB(){
		db.close();
	}

	public void saveProvinceList(List<Province> provinceList){

		db.beginTransaction();
		try{
			for(int i=0;i<provinceList.size();i++){
				Province province = provinceList.get(i);
				db.execSQL(
						"insert into Province (province_name,province_code) values (?,?)"
						, new Object[]{province.getProvinceName(),province.getProvinceCode()}
						);
			}
			db.setTransactionSuccessful();
		} catch(Exception ex){
			ex.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	
	public Province loadProvinceByName(String provinceName){
		
		Province province = null;
		Cursor cursor = db.rawQuery(
				"select * from Province province where province.provinceName=?"
				, new String[]{ provinceName });
		
		if(cursor.moveToNext()){
			int provinceId = 
					cursor.getInt(cursor.getColumnIndex("province_id"));
			String proName = 
					cursor.getString(cursor.getColumnIndex("province_name"));
			String provinceCode =
					cursor.getString(cursor.getColumnIndex("province_code"));
			province = new Province();
			province.setProvinceId(provinceId);
			province.setProvinceName(proName);
			province.setProvinceCode(provinceCode);
			
			if(cursor.moveToNext()){
				throw new RuntimeException("数据库中有多余的省记录");
			}
			cursor.close();
		}
		
		return province;	
	}
	
	public List<Province> getProvinceList(){

		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.rawQuery(
				"select * from Province province",null);
		
		while(cursor.moveToNext()){
			
			int provinceId = 
					cursor.getInt(cursor.getColumnIndex("province_id"));
			String proName = 
					cursor.getString(cursor.getColumnIndex("province_name"));
			String provinceCode =
					cursor.getString(cursor.getColumnIndex("province_code"));
			
			Province province = new Province();
			province.setProvinceId(provinceId);
			province.setProvinceName(proName);
			province.setProvinceCode(provinceCode);
			
			provinceList.add(province);
		}
		cursor.close();
		return provinceList;		
	}
	
	public void saveCityList(List<City> cityList){

		db.beginTransaction();
		try{
			
			for(int i=0;i<cityList.size();i++){
				City city = cityList.get(i);
				db.execSQL(
						"insert into City (city_name,city_code,province_id) values (?,?,?)"
						,new Object[]{city.getCityName(),city.getCityCode()
								,city.getProvinceId()} 
						);
			}
			
			db.setTransactionSuccessful();
		} catch (Exception ex){
			ex.printStackTrace();
		} finally{
			db.endTransaction();
		}
	}
	
	public List<City> getCityListByProvinceId(Integer provinceId){

		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.rawQuery(
				"select * from City city where city.province_id=?"
				, new String[]{provinceId.toString()}
				);
		while(cursor.moveToNext()){
			
			int cityId = cursor.getInt(cursor.getColumnIndex("city_id"));
			String cityName = cursor.getString(cursor.getColumnIndex("city_name"));
			String cityCode = cursor.getString(cursor.getColumnIndex("city_code"));
			int tempProvinceId = cursor.getInt(cursor.getColumnIndex("province_id"));
			
			City city = new City();
			city.setCityId(cityId);
			city.setCityName(cityName);
			city.setCityCode(cityCode);
			city.setProvinceId(tempProvinceId);
			
			cityList.add(city);
		}
		cursor.close();
		return cityList;
	}
	
	public void saveCountyList(List<County> countyList){

		db.beginTransaction();
		try{
			
			for(int i=0;i<countyList.size();i++){
				County county = countyList.get(i);
				db.execSQL(
						"insert into County (county_name,county_code,city_id) values (?,?,?)"
						,new Object[]{county.getCountyName(),county.getCountyCode()
								,county.getCityId()} 
						);
			}
			
			db.setTransactionSuccessful();
		} catch (Exception ex){
			ex.printStackTrace();
		} finally{
			db.endTransaction();
		}
	}
	
	public List<County> getCountyListByCityId(Integer cityId){

		List<County> countyList = new ArrayList<County>();
		Cursor cursor = 
				db.rawQuery("select * from County county where county.city_id=?"
						, new String[]{cityId.toString()}
						);
		while(cursor.moveToNext()){
			
			int countyId = cursor.getInt(cursor.getColumnIndex("county_id"));
			String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
			String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
			int tempCityId = cursor.getInt(cursor.getColumnIndex("city_id"));
			
			County county = new County();
			county.setCountyId(countyId);
			county.setCountyName(countyName);
			county.setCountyCode(countyCode);
			county.setCityId(tempCityId);
			
			countyList.add(county);
		}
		cursor.close();
		return countyList;
	}
	
	public County loadCountyById(Integer countyId){

		County county = null;
		Cursor cursor = 
				db.rawQuery("select * from County county where county.county_id=?"
						, new String[]{countyId.toString()}
						);
		if(cursor.moveToNext()){
			
			int tempCountyId = cursor.getInt(cursor.getColumnIndex("county_id"));
			String countyName = cursor.getString(cursor.getColumnIndex("county_name"));
			String countyCode = cursor.getString(cursor.getColumnIndex("county_code"));
			int tempCityId = cursor.getInt(cursor.getColumnIndex("city_id"));
			
			county = new County();
			county.setCountyId(tempCountyId);
			county.setCountyName(countyName);
			county.setCountyCode(countyCode);
			county.setCityId(tempCityId);
		}
		cursor.close();
		return county;
	}

	public List<County> getCountyListByCountyIdList(List<Integer> countyIdList) {

		List<County> countyList = new ArrayList<County>();
		
		for(int i=0;i<countyIdList.size();i++){
			Integer countyId = countyIdList.get(i);
			County county = loadCountyById(countyId);
			countyList.add(county);
		}
		return countyList;
	}
	
}
