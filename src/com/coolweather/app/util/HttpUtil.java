package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpUtil {

	public static void sendHttpRequest(final String webUrl
			,final HttpCallbackListener listener){
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				HttpURLConnection connection = null;
				try {
					URL url = new URL(webUrl);
					connection = 
							(HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(10000);
					connection.setConnectTimeout(10000);
					
					connection.setDoInput(true);
					connection.connect();
					
					InputStream in = connection.getInputStream();
					BufferedReader reader = 
							new BufferedReader(new InputStreamReader(in,"UTF-8"));
					String line = null;
					StringBuffer sb = new StringBuffer();
					
					while( (line = reader.readLine()) != null){
						sb.append(line);
			            sb.append("\r\n");						
					}
					String response = sb.toString();
					
					if(listener != null){
						listener.onFinish(response);
					}
					
				} catch (MalformedURLException e) {
					e.printStackTrace();
					if(listener != null){
						listener.onError(e);
					}
				} catch (IOException e) {
					e.printStackTrace();
					if(listener != null){
						listener.onError(e);
					}					
				} finally{
					
					if(connection!=null){
						connection.disconnect();
					}
				}
				
			}
		}).start();
		
	}
}
