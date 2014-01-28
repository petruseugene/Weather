package com.example.weather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends Activity {

	public static String[] names = { "Иван", "Марья", "Петр", "Антон", "Даша", "Борис",
			"Костя", "Игорь", "Анна", "Денис", "Андрей" };
	
	private static int currentCityId = 5128638; 
	private Cursor cur;
	
	TextView cityName;
	TextView cityWeather;
	TextView cityTemp;
	TextView cityDate;
	
	public SlidingMenu menu;
	
	
	final String LOG_TAG = "WeatherLogs";

	final Uri CONTACT_URI = Uri.parse("content://ru.startandroid.providers.AdressBook/contacts");
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidth(20);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setFadeDegree(0.0f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		menu.setBehindWidth((int) (metrics.widthPixels * 0.85));
		menu.setMenu(R.layout.main_menu);

		// находим список
		ListView lvMain = (ListView) findViewById(R.id.left_menu_listView);

		// создаем адаптер
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);

		// присваиваем адаптер списку
		lvMain.setAdapter(adapter);

		
			getWeatherData();
			
		
		cityName = (TextView) findViewById(R.id.city_weather_name);
		cityWeather = (TextView) findViewById(R.id.city_weather_condition);
		cityTemp = (TextView) findViewById(R.id.city_temperature);
		cityDate = (TextView) findViewById(R.id.city_date);

		Button button = (Button) findViewById(R.id.button_get_weather);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					getWeatherData();
			}
		});
		
		Button menu_button = (Button) findViewById(R.id.menu_button);
		menu_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				menu.toggle();
			}
		});
		
		updateCityForecast();
		
	}
	
	public void updateCityForecast(){
		
		if(currentCityId>0){
			cur = getContentResolver().query(WeatherContentProvider.WEATHER_CONTENT_URI,
											 null,
											 WeatherDB.Cities.CITY_ID + " = " + currentCityId , null, null);
			if(cur.moveToFirst()){
				do{
					cityName.setText(cur.getString(WeatherDB.Cities.CITY_NAME_KEY));
					cityTemp.setText(cur.getString(WeatherDB.Cities.TEMPERATURE_KEY));
					cityWeather.setText(cur.getString(WeatherDB.Cities.WEATHER_KEY));
					cityDate.setText(cur.getString(WeatherDB.Cities.TIME_KEY));
				} while( cur.moveToNext() );
			}
		} else {
			
		}
		cur.close();
	}
	
	public void getWeatherData(){
		startService(new Intent(this, GetWeatherService.class).putExtra("cityId", currentCityId));
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
}
