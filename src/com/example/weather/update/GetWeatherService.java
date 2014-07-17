package com.example.weather.update;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.weather.Notification;
import com.example.weather.R;
import com.example.weather.WeatherWidget;
import com.example.weather.activities.SettingsActivity;
import com.example.weather.data.DBworker;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.JsonParsers;
import com.example.weather.objects.WeatherObject;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;


public class GetWeatherService extends Service {
	
	//private final String LOG_TAG = GetWeatherService.class.getSimpleName();
	//Service constants
	//Service extra data names
	public final static String BROADCAST_NAME 		= "SERVICE_BROADCAST";
	public final static String EXTRA_RESULT_BOOL 	= "EXTRA_RESULT_BOOL";
	public final static String EXTRA_RESULT_MESSAGE = "EXTRA_RESULT_DATA";
	//Service get request parameters names
	private final static String ID 		= "id";
	private final static String UNITS 	= "units";
	private final static String COUNT 	= "cnt";
	private final static String DAILY 	= "forecast/daily";
	private final static String METRIC	= "metric";
	//Service variable
	private int numOfRequests = 0;
	private int forecastLength = 5;
	private boolean showNotifications = true;
	private DBworker db = null;
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		db = new DBworker(getContentResolver());
		if( intent!=null ) {
			getPreferences();
			try {
				int citiesServerId[] = db.getCityServerIdArray();
				numOfRequests = citiesServerId.length;
				for (int i = 0; i < citiesServerId.length ; i++) {
					RequestParams params = new RequestParams();
					params.put(ID, String.valueOf(citiesServerId[i]));
					params.put(UNITS, METRIC);
					params.put(COUNT, String.valueOf(forecastLength));
					getWeather(DAILY, params);
				}
			} catch (JSONException e) {
				return super.onStartCommand(intent, flags, startId);
			}
		}
	    return super.onStartCommand(intent, flags, startId);
	}
	
	private void getWeather(String urlAddition, RequestParams params) throws JSONException {
        WeatherRestClient.get(urlAddition, params, new JsonHttpResponseHandler() {
            
        	@Override
            public void onSuccess(JSONObject result) {
    			WeatherObject weather[] = JsonParsers.parseJsonToWeatherArray(result);
    			db.updateWeather(weather);
        		updateWidgetAndNotification();
        		sendBroadcastService(true, getString(R.string.toast_weather_updated));
            }
            
			@Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
				sendBroadcastService(false, getString(R.string.service_not_updated));
            	super.onFailure(e, errorResponse);
            }
        });
    }
	
	private void updateWidgetAndNotification() {
		WeatherWidget.updateWidget(this);
		if(showNotifications) {
			CityObject defCity = db.getDefaultCity();
			Notification.notification(this, defCity, db.getWeatherObjects(defCity.getServerCityId())[0]);
		}
	}
	
	private void getPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		forecastLength = prefs.getInt(SettingsActivity.PREF_FORECAST_LENGTH, forecastLength);
		showNotifications = prefs.getBoolean(SettingsActivity.PREF_SHOW_NOTIFICATIONS, true);
	}
	
	public void sendBroadcastService(boolean serviceResult, String message) {
		if (!(--numOfRequests > 0)) {
			Intent intent = new Intent(BROADCAST_NAME);
			intent.putExtra(GetWeatherService.EXTRA_RESULT_BOOL, serviceResult);
			intent.putExtra(GetWeatherService.EXTRA_RESULT_MESSAGE, message);
			Log.e("service", "sending broadcast");
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
