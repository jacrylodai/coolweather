package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

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
	
	public void saveProvince(Province province){
		
		db.execSQL(
				"insert into Province (province_name,province_code) values (?,?)"
				, new Object[]{province.getProvinceName(),province.getProvinceCode()}
				);
		
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
}
