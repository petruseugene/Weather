package com.example.weather;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.Log;

public class WeatherObject {

	public int cityId;
	public String temperature;
	public String condition;
	public long date;
	public String icon;
	
	public WeatherObject(){
		cityId = 0;
		temperature	 = "No data";
		condition	 = "No data";
		date 		 = 0;
		icon		 = "No data";
	}
	
	public WeatherObject(
			int cityId,
			String temperature,
			String condition,
			long date,
			String icon){
		this.cityId = cityId;
		this.temperature	= temperature;
		this.condition		= condition;
		this.date			= date;
		this.icon			= icon;
	}
	
	public String getDate(){
		Date forecastDate = new Date(this.date);
		String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(forecastDate);
		return formattedDate;
	}
	
	public int getImageResourceId(Context context){
		return context.getResources().getIdentifier(this.icon, "drawable", context.getPackageName());
	}
	
	
	
}
