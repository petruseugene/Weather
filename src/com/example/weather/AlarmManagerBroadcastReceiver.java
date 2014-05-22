package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";
	final private static String LOG_TAG = "WeatherUpdateAlarm";
	 
	 @Override
	 public void onReceive(Context context, Intent intent) {
		 	 PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	         PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
	         wl.acquire();
	         Intent serviceIntent = new Intent(context, GetWeatherService.class);
	         serviceIntent.setAction(GetWeatherService.ACTION_UPDATE_WEATHER);
	         context.startService(serviceIntent);
	         wl.release();
	 }

}
