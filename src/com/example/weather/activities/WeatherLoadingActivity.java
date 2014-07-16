package com.example.weather.activities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import com.example.weather.R;
import com.example.weather.update.AlarmManagerBroadcastReceiver;
import com.example.weather.update.BootReceiver;
import com.example.weather.update.GetWeatherService;

public class WeatherLoadingActivity extends Activity {
	
	public static final int SERVICE_ALARM_DELAY = 1000 * 3600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading_weather);
		startAlarmWeather();
	}
	
	private void startAlarmWeather() {
		if (BootReceiver.am == null ){
			AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            Intent recieverIntent = new Intent(this, AlarmManagerBroadcastReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(this, 0, recieverIntent, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), SERVICE_ALARM_DELAY, pIntent);
		}
	}
	
	@Override
	protected void onStart() {
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(GetWeatherService.BROADCAST_NAME));
		super.onStart();
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onStop();
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			startActivity(new Intent(WeatherLoadingActivity.this, MainActivity.class));
			WeatherLoadingActivity.this.finish();
		}
	};

}
