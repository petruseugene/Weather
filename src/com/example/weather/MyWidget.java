package com.example.weather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;

public class MyWidget extends AppWidgetProvider {

	private final String LOG_TAG = "Weatehr widget";
	private CityObject defaultCity = null;
	private WeatherObject defaultWeather[] = null;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews views = getAndUpdateRemoteViews(context);
		Log.d(LOG_TAG, "updating widget!!!!!!");
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	
	private RemoteViews getAndUpdateRemoteViews(Context context){
		DBworker db = new DBworker(context.getContentResolver());
		defaultCity = db.getDefaultCity();
		defaultWeather = db.getWeatherObjects(defaultCity.serverId);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.widget_city_name, defaultCity.getCityNameCountry());
		views.setTextViewText(R.id.widget_weather_temperature, defaultWeather[0].temperature);
		views.setTextViewText(R.id.widget_weather_condition, defaultWeather[0].condition);
		views.setImageViewResource(R.id.weather_image, defaultWeather[0].getImageResourceId(context));
		return views;
	}

}
