package com.coolweather.app.manager;

import java.util.HashMap;
import java.util.Map;

import com.coolweather.app.R;

public class WeatherService {

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
	
}
