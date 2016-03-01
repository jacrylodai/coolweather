package com.coolweather.app.receiver;

import com.coolweather.app.service.WeatherAutoUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherAutoUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent updateIntent = new Intent(context,WeatherAutoUpdateService.class);
		context.startService(updateIntent);
	}

}
