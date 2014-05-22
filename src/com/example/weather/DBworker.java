package com.example.weather;

import java.util.Arrays;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class DBworker {
	
	private static final String LOG_TAG = "DBworker"; 
	
	private ContentResolver contentResolver;
	
	public DBworker(ContentResolver contextResolver){
		this.contentResolver = contextResolver;
	}
	
	public CityObject getDefaultCity(){
		Cursor cur = null;
		CityObject city = null;
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, null,  WeatherDB.Cities.CITY_FAVOURITE+" = 'true'", null, null);
			city = new CityObject();
			while(cur.moveToNext()){
				city = new CityObject(
						cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
						Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME))));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return city;
	}
	
	public int[] getCityServerIdArray(){
		Cursor cur = null;
		int citiesId[] = null;
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, new String[]{WeatherDB.Cities.SERVER_CITY_ID}, null, null, null);
			citiesId = new int[cur.getCount()];
			for(int i = 0;cur.moveToNext();i++){
				citiesId[i] = cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return citiesId;
	}
	
	public List<CityObject> getCityList(){
		Cursor cur = null;
		CityObject cities[] = null;
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, null, null, null, null);
			cities = new CityObject[cur.getCount()];
			for(int i = 0;cur.moveToNext();i++){
				cities[i] = new CityObject(
						cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
						Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE))));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }	
		}
		return Arrays.asList(cities);
	}
	
	public CityObject getCityObject(int cityServerId){
		Cursor cur = null;
		CityObject city = null;
		try{
			cur = contentResolver.query(	WeatherContentProvider.CITY_CONTENT_URI,
											null,
											WeatherDB.Cities.SERVER_CITY_ID + " = " + cityServerId,
											null,
											null);
			city = new CityObject();
			while(cur.moveToNext()){
				city = new CityObject(
						cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
						Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE))));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return city;
	}
	
	public WeatherObject[] getWeatherObjects(int cityServerId){
		Cursor cur = null;
		WeatherObject weather[] = null;
		try{
			cur = contentResolver.query(		WeatherContentProvider.WEATHER_CONTENT_URI, 
												null, 
												WeatherDB.Weather.WEATHER_CITY_ID + " = " + cityServerId,
												null,
												null
												);
			weather = new WeatherObject[cur.getCount()];
			int i =0;
			while(cur.moveToNext()){
				weather[i] = new WeatherObject(
						cur.getInt(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CITY_ID)),
						cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_TEMPERATURE)),
						cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CONDITION)),
						cur.getLong(cur.getColumnIndex(WeatherDB.Weather.WEATHER_DATE)),
						cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_IMAGE)) );
				i++;
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return weather;
	}
	
	public boolean writeCityObject(CityObject... city){
		ContentValues cv = new ContentValues();
		Cursor cur = null;
		for (CityObject cityObject : city) {
			try{
				cur = contentResolver.query(	WeatherContentProvider.CITY_CONTENT_URI,
													null, 
													WeatherDB.Cities.SERVER_CITY_ID + " = " + cityObject.serverId,
													null,
													null);
				if(!cur.moveToFirst()){
					cv.put(WeatherDB.Cities.SERVER_CITY_ID, cityObject.serverId);
					cv.put(WeatherDB.Cities.CITY_NAME, cityObject.name);
					cv.put(WeatherDB.Cities.CITY_COUNTRY, cityObject.country);
					cv.put(WeatherDB.Cities.CITY_FAVOURITE, cityObject.favourite);
					contentResolver.insert(WeatherContentProvider.CITY_CONTENT_URI, cv);
				}
			} catch(Exception e){
				e.printStackTrace();
			} finally {
				if (cur != null) { cur.close(); }
				cv.clear();
			}
		}
		return true;
	}
	
	public boolean writeWeatherObjects(WeatherObject... weatherArray){
		ContentValues cv = new ContentValues();
		for (WeatherObject weather : weatherArray) {
			cv.put(WeatherDB.Weather.WEATHER_CITY_ID, weather.cityId);
			cv.put(WeatherDB.Weather.WEATHER_TEMPERATURE, weather.temperature);
			cv.put(WeatherDB.Weather.WEATHER_CONDITION, weather.condition);
			cv.put(WeatherDB.Weather.WEATHER_DATE, weather.date);
			cv.put(WeatherDB.Weather.WEATHER_IMAGE, weather.icon);
			contentResolver.insert(WeatherContentProvider.WEATHER_CONTENT_URI, cv);
			cv.clear();
		}
		return true;
	}
	
	public boolean updateWeather(WeatherObject... weatherArray){
		if(weatherArray != null){
			this.deleteWeather(weatherArray[0].cityId);
			writeWeatherObjects(weatherArray);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean makeCityFavourite(CityObject city){
		if( city != null && !city.favourite ){
			ContentValues cv = new ContentValues();
	    	cv.put(WeatherDB.Cities.CITY_FAVOURITE, "false");
	    	int res = contentResolver.update(	WeatherContentProvider.CITY_CONTENT_URI, 
	    							 			cv,
	    							 			WeatherDB.Cities.CITY_FAVOURITE + " = 'true'",
	    							 			null);
	    	cv.clear();
	    	cv.put(WeatherDB.Cities.CITY_FAVOURITE, "true");
	    	res = contentResolver.update(	WeatherContentProvider.CITY_CONTENT_URI, 
	    						 			cv,
	    						 			WeatherDB.Cities.SERVER_CITY_ID + " = " + city.serverId,
	    						 			null);
	    	if(res > 0) return true;
		}
    	return false;
	}
	
	public boolean deleteCity(CityObject cityObject){
		boolean success = false;
    	if(isCityExist(cityObject.serverId)){
    		this.deleteWeather(cityObject.serverId);
    		this.deleteCity(cityObject.serverId);
	    	if(cityObject.favourite) {
	    		this.makeCityFavourite(this.getCityList().get(0));
	    	}
	    	success = true;
    	}
    	return success;
	}
	
	public boolean deleteWeather(int... cityserverId){
		int countDelete = 0;
		for (int serverId : cityserverId) {
			countDelete = contentResolver.delete(	WeatherContentProvider.WEATHER_CONTENT_URI, 
					WeatherDB.Weather.WEATHER_CITY_ID + " = " + serverId,
					null);
		}
		return countDelete > 0;		
	}
	
	public boolean deleteCity(int... cityserverId){
		int countDelete = 0;
		for (int serverId : cityserverId) {
			countDelete = contentResolver.delete(	WeatherContentProvider.CITY_CONTENT_URI, 
					WeatherDB.Cities.SERVER_CITY_ID + " = " + serverId,
					null);
		}
		return countDelete > 0;		
	}
	
	public boolean isCityExist(int cityServerId){
		Boolean result = false;
		Cursor cur = null;
		try{
		cur = contentResolver.query( WeatherContentProvider.CITY_CONTENT_URI,
									 new String[]{WeatherDB.Cities.SERVER_CITY_ID},
									 WeatherDB.Cities.SERVER_CITY_ID + " = " + cityServerId,
									 null,
									 null);
		result = cur.getCount() > 0;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return result;
	}

}
