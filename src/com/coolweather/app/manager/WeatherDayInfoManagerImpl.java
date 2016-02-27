package com.coolweather.app.manager;

import java.io.StringReader;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.dao.WeatherDayInfoDao;
import com.coolweather.app.dao.WeatherDayInfoDaoImpl;
import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.WeatherDayInfo;
import com.coolweather.app.util.XMLUtil;

public class WeatherDayInfoManagerImpl implements WeatherDayInfoManager{

	private WeatherDayInfoDao weatherDayInfoDao = new WeatherDayInfoDaoImpl();
	
	private static WeatherDayInfoManagerImpl instance = new WeatherDayInfoManagerImpl();
	
	private WeatherDayInfoManagerImpl(){
		
	}
	
	public static WeatherDayInfoManagerImpl getInstance(){
		return instance;
	}
	
	@Override
	public void saveWeatherDayInfoList(Context context,
			List<WeatherDayInfo> weatherDayInfoList) {

		SQLiteDatabase db = 
				CoolWeatherDB.getInstance(context).getDbHelper().getWritableDatabase();
		
		db.beginTransaction();
		try{
			
			for(int i=0;i<weatherDayInfoList.size();i++){
				WeatherDayInfo weatherDayInfo = weatherDayInfoList.get(i);
				weatherDayInfoDao.saveWeatherDayInfo(db, weatherDayInfo);
			}
			
			db.setTransactionSuccessful();
		} catch (Exception ex){
			ex.printStackTrace();
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	@Override
	public void parseWeatherInfoResponse(Context context, County defaultCounty,
			String response) {

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(new StringReader(response));
		} catch (DocumentException e) {
			e.printStackTrace();
		} 

		Element statusE = 
				(Element) document.selectObject("/CityWeatherResponse/status");
		String status = statusE.getStringValue();
		if(!status.equals("success")){
			return;
		}
		deleteWeatherDayInfoByCountyId(context, defaultCounty.getCountyId());
		List<WeatherDayInfo> weatherDayInfoList =
				XMLUtil.getWeatherDayInfoListFromXML(document,defaultCounty);
		saveWeatherDayInfoList(context, weatherDayInfoList);
	}
	
	public void deleteWeatherDayInfoByCountyId(Context context, Integer countyId){

		SQLiteDatabase db = 
				CoolWeatherDB.getInstance(context).getDbHelper().getWritableDatabase();
		
		weatherDayInfoDao.deleteWeatherDayInfoByCountyId(db,countyId);
		
		db.close();
	}

	@Override
	public List<WeatherDayInfo> getWeatherDayInfoListByCountyId(
			Context context, Integer countyId) {

		SQLiteDatabase db = 
				CoolWeatherDB.getInstance(context).getDbHelper().getReadableDatabase();
		
		List<WeatherDayInfo> weatherDayInfoList = 
				weatherDayInfoDao.getWeatherDayInfoListByCountyId(db,countyId);
		
		db.close();
		return weatherDayInfoList;
	}

}
