package com.example.weather;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    Context mContext;
    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";
    
    public static AlarmManager am;
    
    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String action = intent.getAction();
        if (action.equalsIgnoreCase(BOOT_ACTION)) {
        	 am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
             Intent recieverIntent = new Intent(context, AlarmManagerBroadcastReceiver.class);
             PendingIntent pi = PendingIntent.getBroadcast(context, 0, recieverIntent, 0);
             am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 3600 , pi);
        }
    }
}
