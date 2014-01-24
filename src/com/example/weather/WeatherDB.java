package com.example.weather;

public class WeatherDB {
	
	public static final String NAME = "weathrdb";
	public static final int VERSION = 1;
	
	static final String AUTHORITY = "com.example.weather.provider";
	
	public static class Cities{
		
	  // Константы для БД

	  // Таблица
	  public static final String TABLE = "weather";

	  // Поля
	  public static final String ID = "_id";
	  public static final String CITY_ID = "city_id";
	  public static final String CITY_NAME = "city_name";
	  public static final String COUNTRY = "country";
	  public static final String TEMPERATURE = "temperature";
	  public static final String WEATHER = "weather_condition";
	  public static final String TIME = "date_time";
	  public static final String ICON = "icon";

	}
	
}
