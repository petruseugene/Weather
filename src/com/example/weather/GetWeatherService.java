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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GetWeatherService extends Service {
	
	final String LOG_TAG = "GetWeatherServiceLogs";
	
	public void onCreate() {
	    super.onCreate();
	    //Log.d(LOG_TAG, "onCreate");
	}
	  
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //Log.d(LOG_TAG, "onStartCommand");
	    //Get weather Thread
	    someTask();
	    return super.onStartCommand(intent, flags, startId);
	}

	
	public void onDestroy() {
	    super.onDestroy();
	    Log.d(LOG_TAG, "onDestroy");
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	private void someTask() {
		new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/weather?id=5128638&units=metric");
	}
	
	
	public static String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			// convert inputstream to string
			if (inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Did not work!";
			}
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;
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

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getBaseContext(), "Updated!", Toast.LENGTH_LONG).show();
			try {
				JSONObject json = new JSONObject(result);
				JSONArray jsonArr = json.getJSONArray("weather");
				
				//cityName.setText(json.getString("name"));
				//cityWeather.setText(jsonArr.getJSONObject(0).getString("description"));
				JSONObject jsonMain = new JSONObject(json.getString("main"));
				//cityTemp.setText(jsonMain.getString("temp"));
				//Date forecastDate = new Date(Long.parseLong(json.getString("dt"))*1000);
				//cityDate.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm").format(forecastDate));
				Toast.makeText(getBaseContext(), json.getString("name"), Toast.LENGTH_LONG).show();
				
				Date forecastDate = new Date(Long.parseLong(json.getString("dt"))*1000);
				
				ContentResolver cr = getContentResolver();
				ContentValues newValues = new ContentValues();
				
				newValues.put(WeatherDB.Cities.CITY_ID, "5128638");
				newValues.put(WeatherDB.Cities.CITY_NAME, json.getString("name"));
				newValues.put(WeatherDB.Cities.COUNTRY, "US");
				newValues.put(WeatherDB.Cities.TEMPERATURE, jsonMain.getString("temp"));
				newValues.put(WeatherDB.Cities.WEATHER, jsonArr.getJSONObject(0).getString("description"));
				newValues.put(WeatherDB.Cities.TIME, new SimpleDateFormat("dd-MM-yyyy HH:mm").format(forecastDate));
				newValues.put(WeatherDB.Cities.ICON, "ico90.png");
				
				Uri myRowUri = cr.insert(WeatherContentProvider.WEATHER_CONTENT_URI, newValues);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("error", e.toString());
			}
		}
	}

}
