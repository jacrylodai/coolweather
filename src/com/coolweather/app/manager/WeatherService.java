package com.coolweather.app.manager;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.coolweather.app.R;
import com.coolweather.app.model.County;
import com.coolweather.app.service.WeatherAutoUpdateService;
import com.coolweather.app.util.Constant;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;

public class WeatherService {
	
	private static final String TAG = "WeatherService";

	public static final String URL_BAIDU_WEATHER_API = 
			"http://api.map.baidu.com/telematics/v3/weather";
	
	public static final Map<String, Integer> dayPictureMap = 
			new HashMap<String, Integer>();
	
	static {
		dayPictureMap.put("baoxue", R.drawable.baoxue);
		dayPictureMap.put("baoyu",R.drawable.baoyu);
		dayPictureMap.put("baoyuzhuandabaoyu",R.drawable.baoyuzhuandabaoyu);
		dayPictureMap.put("dabaoyu",R.drawable.dabaoyu);
		dayPictureMap.put("dabaoyuzhuantedabaoyu",R.drawable.dabaoyuzhuantedabaoyu);
		dayPictureMap.put("daxue",R.drawable.daxue);
		dayPictureMap.put("daxuezhuanbaoxue",R.drawable.daxuezhuanbaoxue);
		dayPictureMap.put("dayu",R.drawable.dayu);
		dayPictureMap.put("dayuzhuanbaoyu",R.drawable.dayuzhuanbaoyu);
		dayPictureMap.put("dongyu",R.drawable.dongyu);
		dayPictureMap.put("duoyun",R.drawable.duoyun);
		dayPictureMap.put("fuchen",R.drawable.fuchen);
		dayPictureMap.put("leizhenyu",R.drawable.leizhenyu);
		dayPictureMap.put("leizhenyubanyoubingbao",R.drawable.leizhenyubanyoubingbao);
		dayPictureMap.put("mai",R.drawable.mai);
		dayPictureMap.put("qiangshachenbao",R.drawable.qiangshachenbao);
		dayPictureMap.put("qing",R.drawable.qing);
		dayPictureMap.put("shachenbao",R.drawable.shachenbao);
		dayPictureMap.put("tedabaoyu",R.drawable.tedabaoyu);
		dayPictureMap.put("wu",R.drawable.wu);
		dayPictureMap.put("xiaoxue",R.drawable.xiaoxue);
		dayPictureMap.put("xiaoxuezhuanzhongxue",R.drawable.xiaoxuezhuanzhongxue);
		dayPictureMap.put("xiaoyu",R.drawable.xiaoyu);
		dayPictureMap.put("xiaoyuzhuanzhongyu",R.drawable.xiaoyuzhuanzhongyu);
		dayPictureMap.put("yangsha",R.drawable.yangsha);
		dayPictureMap.put("yin",R.drawable.yin);
		dayPictureMap.put("yujiaxue",R.drawable.yujiaxue);
		dayPictureMap.put("zhenxue",R.drawable.zhenxue);
		dayPictureMap.put("zhenyu",R.drawable.zhenyu);
		dayPictureMap.put("zhongxue",R.drawable.zhongxue);
		dayPictureMap.put("zhongxuezhuandaxue",R.drawable.zhongxuezhuandaxue);
		dayPictureMap.put("zhongyu",R.drawable.zhongyu);
		dayPictureMap.put("zhongyuzhuandayu",R.drawable.zhongyuzhuandayu);
	}
	
	public static void downloadCountyWeatherInfo(
			final WeatherDayInfoManager weatherDayInfoManager
			,final Context context
			,final County county) {
	
		StringBuffer sb = new StringBuffer();
		sb.append(WeatherService.URL_BAIDU_WEATHER_API);
		sb.append('?');
		sb.append("location="+
				URLEncoder.encode(county.getCountyName()));
		sb.append("&output=xml");
		sb.append("&ak="+Constant.BAIDU_APP_KEY);		
		sb.append("&mcode="+Constant.APP_MCODE);
		
		String webUrl = sb.toString();
		Log.i(TAG, webUrl);
		HttpUtil.sendHttpRequest(webUrl, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				Log.i(TAG, "onFinish");
				weatherDayInfoManager.parseWeatherInfoResponse(
						context,county.getCountyId(),response);				
			}
			
			@Override
			public void onError(Exception ex) {
				Log.i(TAG, "onError");
			}
		});
	}
	
}
