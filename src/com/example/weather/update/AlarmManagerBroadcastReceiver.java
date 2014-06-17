package com.example.weather.update;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	private final static String LOG_TAG = AlarmManagerBroadcastReceiver.class.getSimpleName();
	 
	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOG_TAG);
		wl.acquire();
		Intent serviceIntent = new Intent(context, GetWeatherService.class);
		context.startService(serviceIntent);
		wl.release();
	}

}
