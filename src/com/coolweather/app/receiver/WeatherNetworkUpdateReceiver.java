package com.coolweather.app.receiver;

import com.coolweather.app.service.WeatherNetworkUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WeatherNetworkUpdateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		Intent updateIntent = new Intent(context,WeatherNetworkUpdateService.class);
		context.startService(updateIntent);
	}

}
