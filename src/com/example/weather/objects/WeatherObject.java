package com.example.weather.objects;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;

public class WeatherObject {
	
	private static final String DATE_FORMAT = "dd-MM-yyyy";
	private static final String IMAGE_RESOURCE_TYPE = "drawable";
	
	private Integer serverCityId;
	private String temperature;
	private String condition;
	private Long date;
	private String icon;
	
	public WeatherObject(){ // FIXME ?
		serverCityId = null;
		temperature	 = "temperature";
		condition	 = "condition";
		date 		 = null;
		icon		 = "icon";
	}
	
	public WeatherObject(Integer serverCityId, String temperature, String condition, Long date, String icon){
		this.serverCityId 	= serverCityId;
		this.temperature	= temperature;
		this.condition		= condition;
		this.date			= date;
		this.icon			= icon;
	}
	
	public Integer getServerCityId(){
		return this.serverCityId;
	}
	
	public String getTemperature(){
		return this.temperature;
	}
	
	public String getCondition(){
		return this.condition;
	}
	
	public Long getDate(){
		return this.date;
	}
	
	public String getFormattedDate(){
		Date forecastDate = new Date(this.date);
		String formattedDate = new SimpleDateFormat(DATE_FORMAT, Locale.US).format(forecastDate);
		return formattedDate;
	}
	
	public String getIcon(){
		return this.icon;
	}
	
	public int getImageResourceId(Context context){
		return context.getResources().getIdentifier(this.icon, IMAGE_RESOURCE_TYPE, context.getPackageName());
	}
	
	
}
