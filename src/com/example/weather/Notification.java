package com.example.weather;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.DisplayMetrics;

import com.example.weather.activities.MainActivity;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;

public class Notification {
	
	private static final float DP = 64f;
	private static final float IMAGE_PADDING = 0.5f;
	
	public static void notification(Context context, CityObject city, WeatherObject weather){
		BitmapDrawable weatherPicDrawable = (BitmapDrawable) context.getResources().getDrawable(weather.getImageResourceId(context));
		Bitmap weatherPic = weatherPicDrawable != null ? weatherPicDrawable.getBitmap() : null;

		Resources res = context.getResources();
		DisplayMetrics metrics = res.getDisplayMetrics();
		int pixels = (int) (metrics.density * DP + IMAGE_PADDING);
		weatherPic = Bitmap.createScaledBitmap(weatherPic, pixels, pixels, false); 
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
		        .setSmallIcon(weather.getImageResourceId(context))
		        .setContentTitle(String.format(context.getString(R.string.format_two_strings_tire), weather.getTemperature(), city.getCityNameCountry()))
		        .setContentText(weather.getCondition())
		        .setOngoing(true);
		notificationBuilder.setLargeIcon(weatherPic);
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, notificationBuilder.build());
		
	}
}
