package com.example.weather.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.text.TextUtils;

import com.example.weather.R;
import com.example.weather.update.GetWeatherService;

public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	final public static String PREF_FORECAST_LENGTH = "pref_forecast_length";
	final public static String PREF_SHOW_NOTIFICATIONS = "pref_show_notifications";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_activity);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if(!TextUtils.isEmpty(key) && key.equals(PREF_FORECAST_LENGTH)){
			startGetWeatherService();
		} 
		else if(!TextUtils.isEmpty(key) && key.equals(PREF_SHOW_NOTIFICATIONS)){
			if(!sharedPreferences.getBoolean(PREF_SHOW_NOTIFICATIONS, true)){
				NotificationManager mNotificationManager =
					    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.cancel(0);
			} else {
				startGetWeatherService();
			}
		}
	}
	
	private void startGetWeatherService(){
		Intent serviceIntent = new Intent(this, GetWeatherService.class);
	    startService(serviceIntent);
	}
}
