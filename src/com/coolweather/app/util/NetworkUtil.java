package com.coolweather.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

	public static boolean isNetworkAvailable(Context context){
		
		ConnectivityManager connectionManager = 
				(ConnectivityManager) context.getSystemService(
						Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isAvailable()){
			return true;
		}else{
			return false;
		}
	}
}
