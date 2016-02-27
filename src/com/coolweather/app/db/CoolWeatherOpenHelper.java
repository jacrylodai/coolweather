package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
	
	private static final String CREATE_PROVINCE = 
			"create table Province("+
				"province_id integer primary key autoincrement,"+
				"province_name text,"+
				"province_code text)";
	
	private static final String CREATE_CITY = 
			"create table City("+
				"city_id integer primary key autoincrement,"+
				"city_name text,"+
				"city_code text,"+
				"province_id integer)";
	
	private static final String CREATE_COUNTY = 
			"create table County("+
			"county_id integer primary key autoincrement,"+
			"county_name text,"+
			"county_code text,"+
			"city_id integer)";
	
	private static final String CREATE_WEATHER_DAY_INFO = 
			"create table t_weather_day_info( "+
			   "weather_day_info_id  integer primary key autoincrement, "+
			   "county_id            integer, "+
			   "week                 text, "+
			   "weather_date         text, "+
			   "day_picture_url      text, "+
			   "night_picture_url    text, "+
			   "weather_info         text, "+
			   "wind                 text, "+
			   "temperature          text, "+
			   "real_time_temperature text, "+
			   "update_time          text"+
			")";
			

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTY);
		db.execSQL(CREATE_WEATHER_DAY_INFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
