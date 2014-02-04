package com.example.weather;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends ActionBarActivity implements OnClickListener{

	
	
	private static int currentCityId = 0;
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
		
		initSliderMenu();
		
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
			
		
		cityName = (TextView) findViewById(R.id.city_weather_name);
		cityWeather = (TextView) findViewById(R.id.city_weather_condition);
		cityTemp = (TextView) findViewById(R.id.city_temperature);
		cityDate = (TextView) findViewById(R.id.city_date);

		
		Button add_city_button = (Button) findViewById(R.id.add_city_button);
		add_city_button.setOnClickListener(this);
		
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));
		cur = getContentResolver().query(WeatherContentProvider.WEATHER_CONTENT_URI, null,  WeatherDB.Cities.FAVOURITE_CITY+" = 'true'" , null, null);
    	if(cur.moveToFirst()){
    		currentCityId = cur.getInt(WeatherDB.Cities.CITY_ID_KEY);
    	}
		getWeatherRequest();
		
		updateMainUI();
		
		cityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
				currentCityId = city_id[position];
				updateMainUI();
				menu.toggle();
			}
		});
		
	}
	
	
	private void initSliderMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidth(20);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setFadeDegree(0.0f);
		menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		menu.setBehindWidth((int) (metrics.widthPixels * 0.80));
		menu.setMenu(R.layout.main_menu);
	}


	@Override
	public void onClick(View v) {
	    switch (v.getId()) {
		    case R.id.add_city_button:{
		    	Intent intent = new Intent(this, AddNewCity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
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
	
	public void updateMainUI(){
		ContentResolver cr = getContentResolver();
		cur = cr.query(WeatherContentProvider.WEATHER_CONTENT_URI, null, null, null, null);
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
		
		cityList = (ListView) findViewById(R.id.left_menu_listView);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
		cityList.setAdapter(adapter);
		
		if(currentCityId>0){
			cur = cr.query(WeatherContentProvider.WEATHER_CONTENT_URI,
											 null,
											 WeatherDB.Cities.CITY_ID + " = " + currentCityId,
											 null,
											 null);
			if(cur.moveToFirst()){
				do{	
					ImageView img = (ImageView) findViewById(R.id.weather_icon);
					img.setImageResource(R.drawable.ic_launcher);
					cityName.setText(cur.getString(WeatherDB.Cities.CITY_NAME_KEY) + ", " + cur.getString(WeatherDB.Cities.COUNTRY_KEY));
					cityTemp.setText(cur.getString(WeatherDB.Cities.TEMPERATURE_KEY));
					cityWeather.setText(cur.getString(WeatherDB.Cities.WEATHER_KEY));
					cityDate.setText(cur.getString(WeatherDB.Cities.TIME_KEY));
				} while( cur.moveToNext() );
			}
		}
		cur.close();
	}
	
	public void getWeatherRequest(){
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
		    	getWeatherRequest();
		    	return true;
		    case R.id.make_favourite:
		    	makeCurrantCityFavourite();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
	
	private void makeCurrantCityFavourite(){
		ContentValues cv = new ContentValues();
		ContentResolver contRes = getContentResolver();
    	cv.put(WeatherDB.Cities.FAVOURITE_CITY, "false");
    	int res = contRes.update(WeatherContentProvider.WEATHER_CONTENT_URI, 
    							 cv,
    							 WeatherDB.Cities.FAVOURITE_CITY + " = 'true'",
    							 null);
    	cv.clear();
    	cv.put(WeatherDB.Cities.FAVOURITE_CITY, "true");
    	res = contRes.update(WeatherContentProvider.WEATHER_CONTENT_URI, 
    						 cv,
    						 WeatherDB.Cities.CITY_ID + " = " + currentCityId,
    						 null);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (resultCode == RESULT_OK) {
		   currentCityId = data.getIntExtra("newCityId", currentCityId);
		   getWeatherRequest();
	   }
	}
	
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isUpdated = intent.getBooleanExtra("update", false);
			if(isUpdated){
				updateMainUI();
				Toast.makeText(getBaseContext(), getString(R.string.updated_message_pos), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(getBaseContext(), getString(R.string.updated_message_neg), Toast.LENGTH_LONG).show();
			}
		}
	};
		
	
}
