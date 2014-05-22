package com.example.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ForecastActivity extends ActionBarActivity {
	
	private static ActionBar ab;
	public static final String EXTRA_CITY_ID = "cityID";
	private static CityObject currentCity = null;
	private static WeatherObject currentWeather[] = null;
	
	private static TextView cityName;
	
	final private static String LOG_TAG = "ForecastActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		initActionBar();
		findAllViews();
		
		Intent intent = getIntent();
		int cityId = intent.getIntExtra(EXTRA_CITY_ID, 0);
		DBworker db = new DBworker(getContentResolver());
		currentCity = db.getCityObject(cityId);
		
		if(currentCity != null){
			cityName.setText(currentCity.name + ", " + currentCity.country);
			currentWeather = db.getWeatherObjects(currentCity.serverId);
			if(currentWeather != null){
				LinearLayout linLayout = (LinearLayout) findViewById(R.id.forecast_list);
			    LayoutInflater ltInflater = getLayoutInflater();
				for (WeatherObject weather : currentWeather) {
					View item = ltInflater.inflate(R.layout.forecast_item, linLayout, false);
				    TextView tvWeather = (TextView) item.findViewById(R.id.forecast_city_weather);
				    TextView tvForecastDate = (TextView) item.findViewById(R.id.forecast_date);
				    TextView tvTemp = (TextView) item.findViewById(R.id.forecast_city_temperature);
				    ImageView weatherImage = (ImageView) item.findViewById(R.id.forecast_weather_image);
				      
				    weatherImage.setImageResource(weather.getImageResourceId(getApplicationContext()));
				    tvWeather.setText(weather.condition);
				    tvTemp.setText(weather.temperature);
				    tvForecastDate.setText(weather.getDate());
				    linLayout.addView(item);
				}
			}
		}
	}
	
	private void initActionBar() {
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
	}
	
	private void findAllViews() {
		cityName = (TextView) findViewById(R.id.cityName);
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
