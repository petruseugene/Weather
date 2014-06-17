package com.example.weather.data;


public class WeatherDB {
	
	public static final String NAME = "weather.db";
	public static final int VERSION = 7;
	
	static final String AUTHORITY = "com.example.weather.provider";
	
	public static class Cities{
		
		// table
		public static final String TABLE_NAME = "cities";
		// DB fields
		public static final String CITY_ID = "city_id";
		public static final String SERVER_CITY_ID = "server_city_id";
		public static final String CITY_NAME = "city_name";
		public static final String CITY_COUNTRY = "country";
		public static final String CITY_FAVOURITE = "favourite";

	}
	
	public static class Weather{
		
		// table
		public static final String TABLE_NAME = "weather";
		// DB fields
		public static final String WEATHER_ID = "weather_id";
		public static final String WEATHER_CITY_ID = "weather_city_id";
		public static final String WEATHER_TEMPERATURE = "weather_temperature";
		public static final String WEATHER_CONDITION = "weather_condition";
		public static final String WEATHER_IMAGE = "weather_image";
		public static final String WEATHER_DATE = "weather_date";

	}
	
}
 