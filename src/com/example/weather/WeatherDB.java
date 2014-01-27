package com.example.weather;

public class WeatherDB {
	
	public static final String NAME = "weathr.db";
	public static final int VERSION = 5;
	
	static final String AUTHORITY = "com.example.weather.provider";
	
	public static class Cities{
		
	  // DB Constants

	  // table
	  public static final String TABLE = "forecast";

	  // DB fields
	  public static final String ID = "_id";
	  public static final String CITY_ID = "city_id";
	  public static final String CITY_NAME = "city_name";
	  public static final String COUNTRY = "country";
	  public static final String FAVOURITE_CITY = "favourite";
	  public static final String TEMPERATURE = "temperature";
	  public static final String WEATHER = "weather_condition";
	  public static final String TIME = "date_time";
	  public static final String ICON = "icon";
	  
	  // DB fields KEYS
	  public static final int ID_KEY 				= 0;
	  public static final int CITY_ID_KEY 			= 1;
	  public static final int CITY_NAME_KEY 		= 2;
	  public static final int COUNTRY_KEY 			= 3;
	  public static final int FAVOURITE_CITY_key 	= 4;
	  public static final int TEMPERATURE_KEY 		= 5;
	  public static final int WEATHER_KEY 			= 6;
	  public static final int TIME_KEY 				= 7;
	  public static final int ICON_KEY 				= 8;

	}
	
}
