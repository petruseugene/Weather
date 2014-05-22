package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RemoteViews;

public class GetWeatherService extends Service {
	//Service constants
	private final String LOG_TAG = "GetWeatherServiceLogs";
	private final static String weatherUri = "http://api.openweathermap.org/data/2.5/weather";
	private final static String forecastUri = "http://api.openweathermap.org/data/2.5/forecast/daily";
	//Service Actions
	public final static String ACTION_NEW_CITY = "AddNewCity";
	public final static String ACTION_UPDATE_WEATHER = "UpdateAll";
	//Service extra data names
	public final static String EXTRA_CITY_SERVER_ID = "EXTRA_CITY_SERVER_ID";
	public final static String EXTRA_RESULT_BOOL = "EXTRA_RESULT_BOOL";
	public final static String EXTRA_RESULT_MESSAGE = "EXTRA_RESULT_DATA";
	//Service var
	private static int forecastLength = 3;
	private static boolean showNotifications = true;
	private static String currentAction = ACTION_UPDATE_WEATHER;
	//Service objects
	private static DBworker db = null;
	private static ContentResolver contentResolver = null;
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		if( intent!=null ){
			contentResolver = getContentResolver();
			getPreferences();
			db = new DBworker(contentResolver);
			String action = intent.getAction();
			if(isConnected()){
				if (action.equals(ACTION_UPDATE_WEATHER)) {
					currentAction = ACTION_UPDATE_WEATHER;
					
					int citiesServerId[] = db.getCityServerIdArray();
					URI urisForecast[] = new URI[citiesServerId.length];
					
					for (int i = 0; i < citiesServerId.length ; i++) {
						urisForecast[i] = URI.create(forecastUri + "?id=" + citiesServerId[i] + "&units=metric&cnt=" + forecastLength);
					}
					
					new GetHttpWeather().execute(urisForecast);
					
			    } else if (action.equals(ACTION_NEW_CITY)) {
			    	currentAction = ACTION_NEW_CITY;
			    	int cityId = intent.getIntExtra(GetWeatherService.EXTRA_CITY_SERVER_ID, 0);
			    	new GetHttpCityWeather().execute(URI.create(weatherUri + "?id=" + cityId + "&units=metric"));
					new GetHttpWeather().execute(URI.create(forecastUri + "?id=" + cityId + "&units=metric&cnt=" + forecastLength));
			    }
			} else {
				sendBroadcastService(false, getString(R.string.toast_no_connection));
				this.stopSelf();
			}
		} 
	    return super.onStartCommand(intent, flags, startId);
	}
	
	private void getPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		forecastLength = Integer.parseInt(prefs.getString(SettingsActivity.PREF_FORECAST_LENGTH, String.valueOf(forecastLength)));
		showNotifications = prefs.getBoolean(SettingsActivity.PREF_SHOW_NOTIFICATIONS, true);
	}

	private String GET(URI url) {
		InputStream inputStream = null;
		String result = "";
		try {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url.toString()));
			inputStream = httpResponse.getEntity().getContent();
			if (inputStream != null) {
				result = convertInputStreamToString(inputStream);
			} else {
				result = "Cant get data.";
			}
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	private static String convertInputStreamToString(InputStream inputStream) {
		String line = "", result = "";
		try {
			BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
			while ((line = bufferedReader.readLine()) != null) result += line;
			inputStream.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		return result;
	}

	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	private class GetHttpCityWeather extends AsyncTask<URI, Void, String[]> {
		
		@Override
		protected String[] doInBackground(URI... querysURI) {
			String res[] = new String[querysURI.length];
		     for (int i = 0; i < querysURI.length; i++) {
	        	 res[i] = GET(querysURI[i]);
	         }
			return res;
		}
		
		@Override
		protected void onPostExecute(String result[]) {
			super.onPostExecute(result);
			updateCityWeatherData(result);
			String message = "";
			if(currentAction.equals(ACTION_UPDATE_WEATHER)){
				message = getString(R.string.service_updated_successfully);
			} else {
				message = getString(R.string.service_added_new_city);
			}
			sendBroadcastService(true, message);
		}
		
		private void updateCityWeatherData(String result[]){
			for (String res : result) {
				CityObject city = JsonParcers.parceJsonToCityObject(res);
				db.writeCityObject(city);
			}
		}
	}
	
	private class GetHttpWeather extends AsyncTask<URI, Void, String[]> {
		
		@Override
		protected String[] doInBackground(URI... querysURI) {
			String res[] = new String[querysURI.length];
		     for (int i = 0; i < querysURI.length; i++) {
	        	 res[i] = GET(querysURI[i]);
	         }
			return res;
		}
		
		@Override
		protected void onPostExecute(String result[]) {
			super.onPostExecute(result);
			updateForecastResult(result);
			String message = "";
			if(currentAction.equals(ACTION_UPDATE_WEATHER)){
				message = getString(R.string.toast_weather_updated);
			} else {
				message = getString(R.string.toast_added_new_city);
			}
			sendBroadcastService(true, message);
		}
		
		public void updateForecastResult(String result[]){
			for (String res : result) {
				WeatherObject weather[] = JsonParcers.parceJsonToWeatherArray(res);
				db.updateWeather(weather);
			}
			updateWidget();
			if(showNotifications){
				CityObject defCity = db.getDefaultCity();
				notificate(defCity, db.getWeatherObjects(defCity.serverId)[0]);
			}
		}
	}	
	
	public void sendBroadcastService(boolean serviceResult, String message) {
		Intent intent = new Intent(currentAction);
		intent.putExtra(GetWeatherService.EXTRA_RESULT_BOOL, serviceResult);
		intent.putExtra(GetWeatherService.EXTRA_RESULT_MESSAGE, message);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	private void notificate(CityObject city,WeatherObject weather){
		BitmapDrawable contactPicDrawable = (BitmapDrawable) getResources().getDrawable(weather.getImageResourceId(getApplicationContext()));
		Bitmap contactPic = contactPicDrawable.getBitmap();

		Resources res = getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		float dp = 64f;
		int pixels = (int) (metrics.density * dp + 0.5f);
		contactPic = Bitmap.createScaledBitmap(contactPic, pixels, pixels, false); 
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(this)
		        .setSmallIcon(weather.getImageResourceId(getApplicationContext()))
		        .setContentTitle(weather.temperature + " - " + city.getCityNameCountry())
		        .setContentText(weather.condition)
		        .setOngoing(true);
		mBuilder.setLargeIcon(contactPic);
		Intent resultIntent = new Intent(this, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}
	
	private void updateWidget() {
		DBworker db = new DBworker(getContentResolver());
		CityObject defaultCity = db.getDefaultCity();
		WeatherObject defaultWeather[] = db.getWeatherObjects(defaultCity.serverId);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.widget_city_name, defaultCity.getCityNameCountry());
		views.setTextViewText(R.id.widget_weather_temperature, defaultWeather[0].temperature);
		views.setTextViewText(R.id.widget_weather_condition, defaultWeather[0].condition);
		views.setImageViewResource(R.id.weather_image, defaultWeather[0].getImageResourceId(getApplicationContext()));
		AppWidgetManager mgr=AppWidgetManager.getInstance(this);
		mgr.updateAppWidget(new ComponentName(getPackageName(), MyWidget.class.getName()), views);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
