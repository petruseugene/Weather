package com.example.weather;

import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.LEFT;
import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.SLIDING_WINDOW;
import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.TOUCHMODE_FULLSCREEN;

import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	
	//Activity objects
	private static CityObject currentCity = null;
	private static WeatherObject[] defaultWeather = null;
	private static ContentResolver contentResolver = null;
	private ActionBar ab;
	private SlidingMenu menu;
	private static List<CityObject> cityArrayList = null;
	private static ArrayAdapter<String> adapter = null;
	//Activity constants
	public final int ADD_CITY_ACT_ID = 1;
	private final static String LOG_TAG = "WeatherMainLogs";
	//GUI elements
	private TextView cityName;
	private TextView cityWeather;
	private TextView cityTemp;
	private TextView cityDate;
	private ListView cityList;
	private ImageView weatherImage;
	private Button add_city_button;
	private Button show_forecast_button;
	private Button open_settings;
	//SlidingMenu constants
	private final static int shadowWidth = 20;
	private final static float fadeDegree = 0.0f;
	private final static double menuWidthInPercents = 0.80;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new GetCityAndWeatherFromDB().execute(null,null,null);
		contentResolver = getContentResolver();
		initSliderMenu();
		initActionBar();
		findAllViews();
		
		add_city_button.setOnClickListener(this);
		show_forecast_button.setOnClickListener(this);
		open_settings.setOnClickListener(this);
		
		cityList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
				if(currentCity.serverId != cityArrayList.get(position).serverId){
					currentCity = cityArrayList.get(position);
					new UpdateCityOnUI().execute(null, null, null);
				}
				menu.toggle();
			}
		});
		
	}

	private void initActionBar() {
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setIcon(R.drawable.menu_icon);
	}
	
	private void findAllViews() {
		cityName 			 = (TextView) findViewById(R.id.city_weather_name);
		cityWeather 		 = (TextView) findViewById(R.id.city_weather_condition);
		cityTemp 			 = (TextView) findViewById(R.id.city_temperature);
		cityDate 			 = (TextView) findViewById(R.id.city_date);
		weatherImage 		 = (ImageView) findViewById(R.id.weather_image);
		cityList 			 = (ListView) findViewById(R.id.left_menu_listView);
		add_city_button 	 = (Button) findViewById(R.id.add_city_button);
		show_forecast_button = (Button) findViewById(R.id.show_forecast_button);
		open_settings 		 = (Button) findViewById(R.id.open_settings);
	}

	private void initSliderMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(LEFT);
		menu.setBackgroundColor(getResources().getColor(R.color.back_blue_color));
		menu.setTouchModeAbove(TOUCHMODE_FULLSCREEN);
		menu.setShadowWidth(shadowWidth);
		menu.setShadowDrawable(R.drawable.sliding_menu_shadow);
		menu.setFadeDegree(fadeDegree);
		menu.attachToActivity(this, SLIDING_WINDOW);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		menu.setBehindWidth((int) (metrics.widthPixels * menuWidthInPercents));
		menu.setMenu(R.layout.main_menu);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
	    switch (v.getId()) {
		    case R.id.add_city_button:{
		    	intent = new Intent(this, AddNewCity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
		    }break;
		    case R.id.show_forecast_button:{
		    	intent = new Intent(this, ForecastActivity.class);
		    	intent.putExtra(ForecastActivity.EXTRA_CITY_ID, currentCity.serverId);
			    startActivity(intent);
		    }break;
		    case R.id.open_settings:{
		    	intent = new Intent(this, SettingsActivity.class);
		        startActivity(intent);
		    }break;
	    }
	}
	
	@Override
	protected void onStart() {
		LocalBroadcastManager.getInstance(this).registerReceiver(serviceReciever, new IntentFilter(GetWeatherService.ACTION_NEW_CITY));
		LocalBroadcastManager.getInstance(this).registerReceiver(serviceReciever, new IntentFilter(GetWeatherService.ACTION_UPDATE_WEATHER));
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReciever);
		super.onStop();
	}
	
	private void updateMainUI(){
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		adapter.clear();
		for (CityObject city : cityArrayList) {
			adapter.add(city.getCityNameCountry());
		}
		cityList.setAdapter(adapter);
		cityName.setText(currentCity.getCityNameCountry());
		
		if( defaultWeather != null ){
			WeatherObject todaysWeather = defaultWeather[0];
			cityTemp.setText(todaysWeather.temperature);
			cityWeather.setText(todaysWeather.condition);
			cityDate.setText(todaysWeather.getDate());
			weatherImage.setImageResource(todaysWeather.getImageResourceId(getApplicationContext()));
		}
	}
	
	private void startServiceToAddNewCity(){
		Intent intent = new Intent(this, GetWeatherService.class);
		intent.putExtra(GetWeatherService.EXTRA_CITY_SERVER_ID, currentCity.serverId);
		intent.setAction(GetWeatherService.ACTION_NEW_CITY);
		startService(intent);
	}
	
	private void startServiceToUpdateData(){
		Intent serviceIntent = new Intent(this, GetWeatherService.class);
	    serviceIntent.setAction(GetWeatherService.ACTION_UPDATE_WEATHER);
	    startService(serviceIntent);
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
		    	startServiceToUpdateData();
		    	return true;
		    case R.id.make_favourite:
		    	makeCurrantCityFavourite();
		    	return true;
		    case R.id.delete_city_item:
		    	deleteCurrentCity();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
	
	private void deleteCurrentCity() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.delete_dialog_title));
		builder.setMessage(getString(R.string.delete_dialog_text) + currentCity.getCityNameCountry() + " ?");
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	new DeleteCurrentCity().execute(null, null, null);
		        dialog.dismiss();
		    }
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	private void makeCurrantCityFavourite(){
		DBworker db = new DBworker(getContentResolver());
		if(db.makeCityFavourite(currentCity)){
			new GetCityAndWeatherFromDB().execute(null, null, null);
		} else {
			showToast(getString(R.string.toast_cant_make_favourite));
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (requestCode == ADD_CITY_ACT_ID && resultCode == RESULT_OK) {
		   currentCity = new CityObject(	data.getIntExtra(AddNewCity.NEW_CITY_ID, 0),
				   							data.getStringExtra(AddNewCity.NEW_CITY_NAME),
				   							data.getStringExtra(AddNewCity.NEW_CITY_COUNTRY),
				   							false);
		   startServiceToAddNewCity();
	   }
	}
	
	private BroadcastReceiver serviceReciever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean isUpdated = intent.getBooleanExtra(GetWeatherService.EXTRA_RESULT_BOOL, false);
			String mess = intent.getStringExtra(GetWeatherService.EXTRA_RESULT_MESSAGE);
			if(isUpdated){
				new UpdateCityOnUI().execute(null, null, null);
			}
			showToast(mess);
		}
	};
	
	private void showToast(String text){
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
	}
	
	private class GetCityAndWeatherFromDB extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			DBworker db = new DBworker(contentResolver);
	    	currentCity = db.getDefaultCity();
	    	cityArrayList = db.getCityList();
	    	defaultWeather = db.getWeatherObjects(currentCity.serverId);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			updateMainUI();
		}
		
	}
	
	private class UpdateCityOnUI extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			DBworker db = new DBworker(contentResolver);
			cityArrayList = db.getCityList();
			defaultWeather = db.getWeatherObjects(currentCity.serverId);
			return null;
		}
		
		@Override
		protected void onPostExecute(Void param) {
			updateMainUI();
		}
		
	}
	
	private class DeleteCurrentCity extends AsyncTask<Void, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			DBworker db = new DBworker(contentResolver);
			return db.deleteCity(currentCity);
		}
		
		@Override
		protected void onPostExecute(Boolean param) {
			if(param){
				new GetCityAndWeatherFromDB().execute(null, null, null);
			} else {
				showToast(getString(R.string.toast_cant_delete));
			}
		}
		
	}
	
}
