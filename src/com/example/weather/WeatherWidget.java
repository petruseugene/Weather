package com.example.weather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.widget.RemoteViews;

import com.example.weather.data.DBworker;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;

public class WeatherWidget extends AppWidgetProvider {

	//private final String LOG_TAG = WeatherWidget.class.getSimpleName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		RemoteViews views = getAndUpdateRemoteViews(context);
		appWidgetManager.updateAppWidget(appWidgetIds, views);
	}
	
	private static RemoteViews getAndUpdateRemoteViews(Context context) {
		DBworker db = new DBworker(context.getContentResolver());
		CityObject defaultCity = db.getDefaultCity();
		WeatherObject defaultWeather = db.getWeatherObjects(defaultCity.getServerCityId()).get(0);
		RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
		views.setTextViewText(R.id.widget_city_name, defaultCity.getCityNameCountry());
		views.setTextViewText(R.id.widget_weather_temperature, defaultWeather.getTemperature());
		views.setTextViewText(R.id.widget_weather_condition, defaultWeather.getCondition());
		views.setImageViewResource(R.id.weather_image, defaultWeather.getImageResourceId(context));
		return views;
	}
	
	public static void updateWidget(Context context) {
		RemoteViews views = getAndUpdateRemoteViews(context);
		AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		mgr.updateAppWidget(new ComponentName(context.getPackageName(), WeatherWidget.class.getName()), views);
	}

}
