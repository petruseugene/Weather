package com.example.weather;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class WeatherLoading extends Activity {
	
	public static final int serviceAlarmDuration = 1000 * 3600;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_weather);
		startAlarmWeather(this);
	}
	
	private void startAlarmWeather(Context context) {
		if( BootReceiver.am == null ){
			AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent recieverIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, recieverIntent, 0);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), serviceAlarmDuration, pIntent);
		}
	}
	
	@Override
	protected void onStart() {
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(GetWeatherService.ACTION_UPDATE_WEATHER));
		super.onStart();
	}
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			startActivity(new Intent(WeatherLoading.this, MainActivity.class));
			WeatherLoading.this.finish();
		}
	};

}
