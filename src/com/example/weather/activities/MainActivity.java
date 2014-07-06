package com.example.weather.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weather.Notification;
import com.example.weather.R;
import com.example.weather.WeatherWidget;
import com.example.weather.data.DBworker;
import com.example.weather.data.WeatherContentProvider;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;
import com.example.weather.update.GetWeatherService;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.List;

import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.LEFT;
import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.SLIDING_WINDOW;
import static com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.TOUCHMODE_FULLSCREEN;

public class MainActivity extends ActionBarActivity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	//Activity objects
	private static CityObject currentCity = null;
	private static List<WeatherObject> weatherList = null;
	private static List<CityObject> cityArrayList = null;
	//Activity constants
	public final int ADD_CITY_ACT_ID = 1;
	private final String LOG_TAG = MainActivity.class.getSimpleName();
    private final int CITY_LOADER = 0;
    private final int WEATHER_LOADER = 1;
    //GUI elements
	private TextView cityName;
	private TextView cityWeather;
	private TextView cityTemp;
	private TextView cityDate;
	private ListView cityList;
	private ImageView weatherImage;
	private Button addCityButton;
	private Button showForecastButton;
	private Button openSettingsButton;
	private MenuItem makeFavourite;
	private SlidingMenu menu;
	//Preferences fields
	private static boolean showNotifications = true;
	//SlidingMenu constants
	private final static int SHADOW_WIDTH = 20;
	private final static float FADE_DEGREE = 0.0f;
	private final static double MENU_WIDTH_IN_PERCENTS = 0.80;
	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSliderMenu();
		initActionBar();
		findAllViews();
		getPreferences();
		
		addCityButton.setOnClickListener(this);
		showForecastButton.setOnClickListener(this);
		openSettingsButton.setOnClickListener(this);
		
		cityList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
                if (!currentCity.getServerCityId().equals(cityArrayList.get(position).getServerCityId())) {
                    currentCity = cityArrayList.get(position);
                    updateMainUI();
                }
                menu.toggle();
            }
        });
        startLoader();
        getContentResolver().registerContentObserver(WeatherContentProvider.WEATHER_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                restartLoader();
            }
        });
        getContentResolver().registerContentObserver(WeatherContentProvider.CITY_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                restartLoader();
            }
        });
	}

    private void startLoader(){
        getSupportLoaderManager().initLoader(CITY_LOADER, null, this);
        getSupportLoaderManager().initLoader(WEATHER_LOADER, null, this);
    }

    private void restartLoader(){
        getSupportLoaderManager().restartLoader(WEATHER_LOADER, null, this);
        getSupportLoaderManager().restartLoader(CITY_LOADER, null, this);
    }

	private void initActionBar() {
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setIcon(R.drawable.menu_icon);
	}
	
	private void getPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		showNotifications = prefs.getBoolean(SettingsActivity.PREF_SHOW_NOTIFICATIONS, true);
	}
	
	private void findAllViews() {
		cityName 			 	 = (TextView) findViewById(R.id.city_weather_name);
		cityWeather 			 = (TextView) findViewById(R.id.city_weather_condition);
		cityTemp 			 	 = (TextView) findViewById(R.id.city_temperature);
		cityDate 			 	 = (TextView) findViewById(R.id.city_date);
		weatherImage 		 	 = (ImageView) findViewById(R.id.weather_image);
		cityList 			 	 = (ListView) findViewById(R.id.left_menu_listView);
		addCityButton 	 		 = (Button) findViewById(R.id.add_city_button);
		showForecastButton 		 = (Button) findViewById(R.id.show_forecast_button);
		openSettingsButton 		 = (Button) findViewById(R.id.open_settings);
		makeFavourite 			 = (MenuItem) findViewById(R.id.make_favourite);
	}

	private void initSliderMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(LEFT);
		menu.setBackgroundColor(getResources().getColor(R.color.back_blue_color));
		menu.setTouchModeAbove(TOUCHMODE_FULLSCREEN);
		menu.setShadowWidth(SHADOW_WIDTH);
		menu.setShadowDrawable(R.drawable.sliding_menu_shadow);
		menu.setFadeDegree(FADE_DEGREE);
		menu.attachToActivity(this, SLIDING_WINDOW);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		menu.setBehindWidth((int) (metrics.widthPixels * MENU_WIDTH_IN_PERCENTS));
		menu.setMenu(R.layout.main_menu);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
	    switch (v.getId()) {
		    case R.id.add_city_button:{
		    	intent = new Intent(this, AddNewCityActivity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
		    }break;
		    case R.id.show_forecast_button:{
		    	intent = new Intent(this, ForecastActivity.class);
		    	intent.putExtra(ForecastActivity.EXTRA_CITY_ID, currentCity.getServerCityId());
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
		LocalBroadcastManager.getInstance(this).registerReceiver(serviceReceiver, new IntentFilter(GetWeatherService.BROADCAST_NAME));
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceReceiver);
		super.onStop();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_actions, menu);
		makeFavourite = menu.findItem(R.id.make_favourite);
        makeFavourite.setIcon(R.drawable.ic_menu_star_on);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	menu.toggle();
		        return true;
		    case R.id.add_new_city:
		    	Intent intent = new Intent(this, AddNewCityActivity.class);
			    startActivityForResult(intent, ADD_CITY_ACT_ID);
		        return true;
		    case R.id.update_data:
		    	startServiceToUpdateData();
		    	return true;
		    case R.id.make_favourite:
		    	makeCurrantCityFavorite();
		    	return true;
		    case R.id.delete_city_item:
		    	deleteCurrentCity();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
	/* 
	 * Updating main Activity UI.
	 */
	private void updateMainUI(){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        if(cityArrayList != null){
            for (CityObject city : cityArrayList) {
                adapter.add(city.getCityNameCountry());
            }
            adapter.notifyDataSetChanged();
            cityList.setAdapter(adapter);
        }
        if(currentCity != null){
            cityName.setText(currentCity.getCityNameCountry());
            WeatherObject todayWeather = getWeatherToday(currentCity.getServerCityId());
            if( todayWeather != null){
                cityTemp.setText(todayWeather.getTemperature());
                cityWeather.setText(todayWeather.getCondition());
                cityDate.setText(todayWeather.getFormattedDate());
                weatherImage.setImageResource(todayWeather.getImageResourceId(getApplicationContext()));
            }
            if(makeFavourite != null){
                if(currentCity.isFavourite()){
                    makeFavourite.setIcon(R.drawable.ic_menu_star_on);
                } else {
                    makeFavourite.setIcon(R.drawable.ic_menu_star_off);
                }
            }
        }

	}
	/* 
	 * Starts service to update data and update UI.
	 */
	private void startServiceToUpdateData() {
		Intent serviceIntent = new Intent(this, GetWeatherService.class);
	    startService(serviceIntent);
	}
	/* 
	 * Delete current city (Building dialog "Delete city?") and updating UI.
	 */
	private void deleteCurrentCity(){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getString(R.string.delete_dialog_title));
		dialogBuilder.setMessage(String.format(getString(R.string.format_delete_question), getString(R.string.delete_dialog_text), currentCity.getCityNameCountry()));
		dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();
		    }
		});
		dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	new DeleteCurrentCity().execute(null, null, null);
		        dialog.dismiss();
		    }
		});
		AlertDialog dialog = dialogBuilder.create();
		dialog.show();
	}
	/* 
	 * Making city favorite, updating UI, notification and widget. 
	 */
	private void makeCurrantCityFavorite() {
        new MakeCurrentCityFavourite().execute(this, null, null);
	}
	/* 
	 * Receiving new city data from AddNewCity Activity.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	   if (requestCode == ADD_CITY_ACT_ID && resultCode == RESULT_OK) {
		   currentCity = new CityObject(	data.getIntExtra(AddNewCityActivity.NEW_CITY_ID, 0),
				   							data.getStringExtra(AddNewCityActivity.NEW_CITY_NAME),
				   							data.getStringExtra(AddNewCityActivity.NEW_CITY_COUNTRY),
				   							false);
		   new AddNewCity().execute(null, null, null);
	   }
	}
	/* 
	 * AsyncTask classes for Getting data from content provider, 
	 * updating data and for delete current city.
	 */
	private BroadcastReceiver serviceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String mess = intent.getStringExtra(GetWeatherService.EXTRA_RESULT_MESSAGE);
			showToast(mess);
		}
	};

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case WEATHER_LOADER:{
                return new CursorLoader(this, WeatherContentProvider.WEATHER_CONTENT_URI,null, null, null, null);
            }
            case CITY_LOADER:{
                return new CursorLoader(this, WeatherContentProvider.CITY_CONTENT_URI,null,null,null,null);
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case WEATHER_LOADER:{
                 weatherList = DBworker.getWeatherObjects(cursor);
            } break;
            case CITY_LOADER:{
                for (CityObject city : cityArrayList = DBworker.getCityList(cursor)) {
                    if(city.isFavourite() && currentCity == null){
                        currentCity = city;
                    }
                }
            } break;
        }
        updateMainUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case WEATHER_LOADER:{
                weatherList = null;
            } break;
            case CITY_LOADER:{
                cityArrayList = null;
            } break;
        }
        getSupportLoaderManager().getLoader(loaderId).forceLoad();
    }

    private WeatherObject getWeatherToday(int cityServerId){
        if(weatherList != null){
            for(WeatherObject weather : weatherList){
                if(weather.getServerCityId().equals(cityServerId)){
                    return weather;
                }
            }
            return null;
        } else {
            return null;
        }
    }
    /*
	 * AsyncTask to add new city and update UI.
	 */
    private class AddNewCity extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            DBworker db = new DBworker(getContentResolver());
            db.writeCityObject(currentCity);
            return null;
        }

        @Override
        protected void onPostExecute(Void param) {
            startServiceToUpdateData();
        }

    }
    /*
	 * AsyncTask to make current city favourite and update UI.
	 */
    private class MakeCurrentCityFavourite extends AsyncTask<Context, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Context... contexts) {
            Context context = contexts[0];
            Boolean madeFavourite = false;
            if(currentCity != null && context != null && weatherList != null){
                DBworker db = new DBworker(context.getContentResolver());
                if(madeFavourite = db.makeCityFavourite(currentCity)){
                    WeatherWidget.updateWidget(context);
                    if(showNotifications){
                        Notification.notification(context, currentCity, weatherList.get(0));
                    }
                }
            }
            return madeFavourite;
        }

        @Override
        protected void onPostExecute(Boolean isAdded) {
            if(!isAdded){
                showToast(getString(R.string.toast_cant_make_favourite));
            }
        }

    }
	/* 
	 * AsyncTask to delete current city and update UI.
	 */
	private class DeleteCurrentCity extends AsyncTask<Void, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
            DBworker db = new DBworker(getContentResolver());
            return db.deleteCity(currentCity);
		}
		
		@Override
		protected void onPostExecute(Boolean param) {
			if(param){
				currentCity = null;
			} else {
				showToast(getString(R.string.toast_cant_delete));
			}
		}
		
	}
	/* 
	 * UI toast method.
	 */
	private void showToast(String text) {
		Toast.makeText(getBaseContext(), text, Toast.LENGTH_LONG).show();
	}
	
}