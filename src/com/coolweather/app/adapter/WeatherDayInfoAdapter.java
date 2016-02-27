package com.coolweather.app.adapter;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.WeatherDayInfo;
import com.coolweather.app.util.DateUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WeatherDayInfoAdapter extends ArrayAdapter<WeatherDayInfo> {

	private int resourceId;
	
	public WeatherDayInfoAdapter(Context context, int textViewResourceId,
			List<WeatherDayInfo> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		WeatherDayInfo weatherDayInfo = getItem(position);
		View view ;
		ViewHolder viewHolder;
		if(convertView == null){
			view = LayoutInflater.from(getContext()).inflate(resourceId, null);
			
			viewHolder = new ViewHolder();
			viewHolder.weekView = (TextView) view.findViewById(R.id.week_view);
			viewHolder.weatherDateView = 
					(TextView) view.findViewById(R.id.weather_date_view);
			viewHolder.weatherInfoView = 
					(TextView) view.findViewById(R.id.weather_info_view);
			viewHolder.temperatureView = 
					(TextView) view.findViewById(R.id.temperature_view);
			
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder) view.getTag();
		}
		
		viewHolder.weekView.setText(weatherDayInfo.getWeek());
		
		Date weatherDate = null;
		try {
			weatherDate = DateUtil.dateFormat.parse(weatherDayInfo.getWeatherDateString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String formatWeatherDateString = DateUtil.weatherDateFormat.format(weatherDate);
		
		viewHolder.weatherDateView.setText(formatWeatherDateString);
		
		viewHolder.weatherInfoView.setText(weatherDayInfo.getWeatherInfo());
		viewHolder.temperatureView.setText(weatherDayInfo.getTemperature());
		
		return view;
	}
	
	class ViewHolder{
		
		TextView weekView;
		
		TextView weatherDateView;
		
		TextView weatherInfoView;
		
		TextView temperatureView;
		
	}

}
