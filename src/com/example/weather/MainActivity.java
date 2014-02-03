package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	
	
	private static int currentCityId = 5128638;
	private Cursor cur;
	
	TextView cityName;
	TextView cityWeather;
	TextView cityTemp;
	TextView cityDate;
	ListView cityList;
	
	ActionBar ab ;
	
	public SlidingMenu menu;
	
	private String names[];
	private int city_id[]; 
	
	public int ADD_CITY_ACT_ID = 1;
	
	
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
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		menu.setBehindWidth((int) (metrics.widthPixels * 0.85));
		menu.setMenu(R.layout.main_menu);
		
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		
		
		getWeatherData();
			
		
		cityName = (TextView) findViewById(R.id.city_weather_name);
		cityWeather = (TextView) findViewById(R.id.city_weather_condition);
		cityTemp = (TextView) findViewById(R.id.city_temperature);
		cityDate = (TextView) findViewById(R.id.city_date);

		Button button_get_weather = (Button) findViewById(R.id.button_get_weather);
		Button menu_button = (Button) findViewById(R.id.menu_button);
		Button add_city_button = (Button) findViewById(R.id.add_city_button);
		Button button_make_favourite = (Button) findViewById(R.id.button_make_favourite);
		button_get_weather.setOnClickListener(this);
		menu_button.setOnClickListener(this);
		add_city_button.setOnClickListener(this);
		button_make_favourite.setOnClickListener(this);
		
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
		cur = getContentResolver().query(WeatherContentProvider.WEATHER_CONTENT_URI, null,  WeatherDB.Cities.FAVOURITE_CITY+" = 'true'" , null, null);
    	if(cur.moveToFirst()){
    		currentCityId = cur.getInt(WeatherDB.Cities.CITY_ID_KEY);
    	}
		getWeatherData();
		
	}
	
	
	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
		    case R.id.button_get_weather:{
		    	getWeatherData();
		    }break;
		    case R.id.menu_button:{
		    	menu.toggle();
		    }break;
		    case R.id.add_city_button:{
		    	Intent intent = new Intent(this, AddNewCity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
		    }break;
		    case R.id.button_make_favourite:{
		    	ContentValues cv = new ContentValues();
		    	int res;
		    		cv.put(WeatherDB.Cities.FAVOURITE_CITY, "false");
		    		res = getContentResolver().update(WeatherContentProvider.WEATHER_CONTENT_URI, cv, WeatherDB.Cities.FAVOURITE_CITY+" = 'true'" ,null);
		    	
		    	Log.d(LOG_TAG,"inserted false in -");
		    	
		    	ContentValues cv2 = new ContentValues();
		    	cv2.put(WeatherDB.Cities.FAVOURITE_CITY, "true");
		    	
		    	int res1 = getContentResolver().update(WeatherContentProvider.WEATHER_CONTENT_URI, cv2 ,
		    			WeatherDB.Cities.CITY_ID+" = " + currentCityId, null);
		    }break;
	    }
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	public void updateCityForecast(){
		
		cur = getContentResolver().query(WeatherContentProvider.WEATHER_CONTENT_URI, null, null, null, null);
		names = new String[cur.getCount()];
		city_id = new int[cur.getCount()];
		int i = 0;
		if(cur.moveToFirst()){
			do{
				names[i] = cur.getString(WeatherDB.Cities.CITY_NAME_KEY) + " " +cur.getString(WeatherDB.Cities.COUNTRY_KEY)+" " + cur.getString(WeatherDB.Cities.TEMPERATURE_KEY); 
				city_id[i] = cur.getInt(WeatherDB.Cities.CITY_ID_KEY);
				i++;
			} while( cur.moveToNext() );
		}
		
		// находим список
		cityList = (ListView) findViewById(R.id.left_menu_listView);
		// создаем адаптер
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		// присваиваем адаптер списку
		cityList.setAdapter(adapter);
		
		cityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
				currentCityId = city_id[position];
				updateCityForecast();
				menu.toggle();
			}
		});
		
		if(currentCityId>0){
			cur = getContentResolver().query(WeatherContentProvider.WEATHER_CONTENT_URI,
											 null,
											 WeatherDB.Cities.CITY_ID + " = " + currentCityId , null, null);
			if(cur.moveToFirst()){
				do{
					cityName.setText(cur.getString(WeatherDB.Cities.CITY_NAME_KEY)+", "+cur.getString(WeatherDB.Cities.COUNTRY_KEY));
					cityTemp.setText(cur.getString(WeatherDB.Cities.TEMPERATURE_KEY));
					cityWeather.setText(cur.getString(WeatherDB.Cities.WEATHER_KEY));
					cityDate.setText(cur.getString(WeatherDB.Cities.TIME_KEY));
				} while( cur.moveToNext() );
			}
		}
		
		Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_LONG).show();
		cur.close();
	}
	
	public void getWeatherData(){
		startService(new Intent(this, GetWeatherService.class).putExtra("cityId", currentCityId));
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	menu.toggle();
		        return true;
		    case R.id.add_new_city:
		    	Intent intent = new Intent(this, AddNewCity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
		        return true;
		    case R.id.update_data:
		    	getWeatherData();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
	
	
	 @Override
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (resultCode == RESULT_OK) {
	    	currentCityId = data.getIntExtra("newCityId", currentCityId);
	    	getWeatherData();
	    }
	 }
	
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			boolean isUpdated = intent.getBooleanExtra("update", false);
			if(isUpdated){
				updateCityForecast();
			} else {
				Toast.makeText(getBaseContext(), "NOT Updated", Toast.LENGTH_LONG).show();
			}
			
			//Log.d("receiver", "Got message: " + message);
		}
	};
		
	
}
