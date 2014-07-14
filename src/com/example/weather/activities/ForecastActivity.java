package com.example.weather.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.data.DBworker;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;
import com.example.weather.util.WeatherAdapter;

import java.util.Arrays;

public class ForecastActivity extends ActionBarActivity {
	
	public static final String EXTRA_CITY_ID = "EXTRA_CITY_ID";
	
	private TextView cityName;
    private ListView weatherListView;
	
	//final private static String LOG_TAG = ForecastActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forecast);
		findAllViews();
		Intent intent = getIntent();
		int cityId = intent.getIntExtra(EXTRA_CITY_ID, 0);
		DBworker db = new DBworker(getContentResolver());
		CityObject currentCity = db.getCityObject(cityId); // FIXME why in UI thread?
		initActionBar(currentCity);

		if(currentCity != null) {
			cityName.setText(currentCity.getCityNameCountry());
			WeatherObject currentWeather[] = db.getWeatherObjects(currentCity.getServerCityId()); // FIXME SAME?
			if(currentWeather != null) {
                WeatherAdapter weatherAdapter = new WeatherAdapter(this, Arrays.asList(currentWeather));
                weatherListView.setAdapter(weatherAdapter);
//				LinearLayout linLayout = (LinearLayout) findViewById(R.id.forecast_list);
//				LayoutInflater ltInflater = getLayoutInflater();
//				for (WeatherObject weather : currentWeather) {
//					View item = ltInflater.inflate(R.layout.forecast_item, linLayout, false); // FIXME use ListView + ListAdapter
//					TextView tvWeather = (TextView) item.findViewById(R.id.forecast_city_weather);
//					TextView tvForecastDate = (TextView) item.findViewById(R.id.forecast_date);
//					TextView tvTemp = (TextView) item.findViewById(R.id.forecast_city_temperature);
//					ImageView weatherImage = (ImageView) item.findViewById(R.id.forecast_weather_image);
//
//					weatherImage.setImageResource(weather.getImageResourceId(getApplicationContext()));
//					tvWeather.setText(weather.getCondition());
//					tvTemp.setText(weather.getTemperature());
//					tvForecastDate.setText(weather.getFormattedDate());
//					linLayout.addView(item);
//				}
			}
		}
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
