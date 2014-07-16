package com.example.weather.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.data.DBworker;
import com.example.weather.data.WeatherContentProvider;
import com.example.weather.data.WeatherDB;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;
import com.example.weather.util.WeatherAdapter;

import java.util.List;

public class ForecastActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{
	
	public static final String EXTRA_CITY_ID = "EXTRA_CITY_ID";
    private final int CITY_LOADER = 0;
    private final int WEATHER_LOADER = 1;

    private int cityId;
    private CityObject currentCity;
    private List<WeatherObject> weatherList;
	private TextView cityName;
    private ListView weatherListView;
	
	//final private static String LOG_TAG = ForecastActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		findAllViews();
        Intent intent = getIntent();
        cityId = intent.getIntExtra(EXTRA_CITY_ID, 0);
        startLoader(CITY_LOADER);
        startLoader(WEATHER_LOADER);

        getContentResolver().registerContentObserver(WeatherContentProvider.WEATHER_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                restartLoader(WEATHER_LOADER);
            }
        });
        getContentResolver().registerContentObserver(WeatherContentProvider.CITY_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                restartLoader(CITY_LOADER);
            }
        });
	}

    private void startLoader(int loaderId){
        getSupportLoaderManager().initLoader(loaderId, null, this);
    }

    private void restartLoader(int loaderId){
        getSupportLoaderManager().restartLoader(loaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case WEATHER_LOADER:{
                return new CursorLoader(this, WeatherContentProvider.WEATHER_CONTENT_URI, null, WeatherDB.Weather.WEATHER_CITY_ID + " = " + cityId, null, null);
            }
            case CITY_LOADER:{
                return new CursorLoader(this, WeatherContentProvider.CITY_CONTENT_URI, null, WeatherDB.Cities.SERVER_CITY_ID + " = " + cityId, null, null);
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
                currentCity = DBworker.getCityObject(cursor);
            } break;
        }
        updateMainUI();
    }

    private void updateMainUI() {
        if(currentCity != null) {
            initActionBar(currentCity);
            cityName.setText(currentCity.getCityNameCountry());
            if(weatherList != null) {
                WeatherAdapter weatherAdapter = new WeatherAdapter(this, weatherList);
                weatherListView.setAdapter(weatherAdapter);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case WEATHER_LOADER:{
                weatherList = null;
            } break;
            case CITY_LOADER:{
                currentCity = null;
            } break;
        }
        getSupportLoaderManager().getLoader(loaderId).forceLoad();
    }
	
	private void initActionBar(CityObject currentCity) {
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
		if(currentCity != null){
			ab.setTitle(String.format(getString(R.string.format_two_strings_space), currentCity.getCityNameCountry(), getString(R.string.title_activity_forecast)));
		}
	}
	
	private void findAllViews() {
		cityName = (TextView) findViewById(R.id.cityName);
        weatherListView = (ListView) findViewById(R.id.weatherListView);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	this.finish();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }
}
