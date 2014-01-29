package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class GetWeatherService extends Service {
	
	
	final String LOG_TAG = "GetWeatherServiceLogs";
	private static int cityId = 0;
	private static int action_type = 0;
	private String search_query = "";
	
	
	public void onCreate() {
	    super.onCreate();
	}
	  
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		cityId = intent.getIntExtra("cityId", 5128638);
		action_type = intent.getIntExtra("action", 0);
	    getWeather();
	    return super.onStartCommand(intent, flags, startId);
	}

	
	public void onDestroy() {
	    super.onDestroy();
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	
	private void getWeather() {
		switch(action_type){
			case 0:{
				new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/weather?id=" + cityId + "&units=metric");
			}break;
			case 1:{
				new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/find?q=" + search_query + "&mode=json");
			}break;
		}
	}
	
	
	public String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		if(isConnected()){
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null) {
					result = convertInputStreamToString(inputStream);
				} else {
					result = "Did not work!";
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
		return result;
	}

	
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) result += line;
		inputStream.close();
		return result;

	}

	
	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
	
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			switch(action_type){
				case 0:{
					updateCityForecast(result);
				}break;
				case 1:{
					new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/find?q=" + search_query + "&mode=json");
				}break;
				default:{
					
				}
			}
			
		}
	}
	
	
	public void updateCityForecast(String result){
		try {
			JSONObject json = new JSONObject(result);
			JSONArray jsonArr = json.getJSONArray("weather");
			
			JSONObject jsonMain = new JSONObject(json.getString("main"));
			
			Date forecastDate = new Date(Long.parseLong(json.getString("dt"))*1000);
			
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(WeatherContentProvider.WEATHER_CONTENT_URI,
					 null,
					 WeatherDB.Cities.CITY_ID + " = " + cityId,
					 null,
					 null);
			if(c.getCount()>0){
				ContentValues newValues = new ContentValues();
				
				newValues.put(WeatherDB.Cities.CITY_ID, "5128638");
				newValues.put(WeatherDB.Cities.CITY_NAME, json.getString("name"));
				newValues.put(WeatherDB.Cities.COUNTRY, "US");
				newValues.put(WeatherDB.Cities.FAVOURITE_CITY, "false");
				newValues.put(WeatherDB.Cities.TEMPERATURE, jsonMain.getString("temp"));
				newValues.put(WeatherDB.Cities.WEATHER, jsonArr.getJSONObject(0).getString("description"));
				newValues.put(WeatherDB.Cities.TIME, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(forecastDate));
				newValues.put(WeatherDB.Cities.ICON, "ico90.png");
				
				int myRowUri = cr.update(WeatherContentProvider.WEATHER_CONTENT_URI, 
										 newValues,
										 WeatherDB.Cities.CITY_ID + " = " + cityId,
										 null);
				Log.d(LOG_TAG, "myRowUri = " + myRowUri);
				sendBroadcastService(true);
			} else {
				sendBroadcastService(false);
			}
			
			this.finalize();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public void searchQuery(String result){
		try {
			JSONObject json = new JSONObject(result);
			JSONArray jsonArr = json.getJSONArray("weather");
			
			JSONObject jsonMain = new JSONObject(json.getString("main"));
			
			Date forecastDate = new Date(Long.parseLong(json.getString("dt"))*1000);
			
			ContentResolver cr = getContentResolver();
			Cursor c = cr.query(WeatherContentProvider.WEATHER_CONTENT_URI,
					 null,
					 WeatherDB.Cities.CITY_ID + " = " + cityId,
					 null,
					 null);
			if(c.getCount()>0){
				ContentValues newValues = new ContentValues();
				
				newValues.put(WeatherDB.Cities.CITY_ID, "5128638");
				newValues.put(WeatherDB.Cities.CITY_NAME, json.getString("name"));
				newValues.put(WeatherDB.Cities.COUNTRY, "US");
				newValues.put(WeatherDB.Cities.FAVOURITE_CITY, "false");
				newValues.put(WeatherDB.Cities.TEMPERATURE, jsonMain.getString("temp"));
				newValues.put(WeatherDB.Cities.WEATHER, jsonArr.getJSONObject(0).getString("description"));
				newValues.put(WeatherDB.Cities.TIME, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(forecastDate));
				newValues.put(WeatherDB.Cities.ICON, "ico90.png");
				
				int myRowUri = cr.update(WeatherContentProvider.WEATHER_CONTENT_URI, 
										 newValues,
										 WeatherDB.Cities.CITY_ID + " = " + cityId,
										 null);
				Log.d(LOG_TAG, "myRowUri = " + myRowUri);
				sendBroadcastService(true);
			} else {
				sendBroadcastService(false);
			}
			
			this.finalize();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendBroadcastService(boolean update) {  
		Intent intent = new Intent("custom-event-name");
		intent.putExtra("update", true);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	    Log.d(LOG_TAG, "update = " + update);
	}  

}
