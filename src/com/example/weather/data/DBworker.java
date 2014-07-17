package com.example.weather.data;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;

import com.example.weather.objects.CityObject;
import com.example.weather.objects.WeatherObject;

import java.util.ArrayList;
import java.util.List;

public class DBworker {

	//private static final String LOG_TAG = DBworker.class.getSimpleName(); 
	
	private ContentResolver contentResolver;
	
	public DBworker(ContentResolver contextResolver) {
		this.contentResolver = contextResolver;
	}
	
	public CityObject getDefaultCity() {
		Cursor cur = null;
		CityObject city = null;
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, null,  WeatherDB.Cities.CITY_FAVOURITE+" = 'true'", null, null);
			city = new CityObject();
			while(cur.moveToNext()) {
				city = new CityObject(
						cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
						cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
						Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE))));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return city;
	}
	
	public int[] getCityServerIdArray() {
		Cursor cur = null;
		int citiesId[] = null;
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, new String[]{WeatherDB.Cities.SERVER_CITY_ID}, null, null, null);
			citiesId = new int[cur.getCount()];
			for(int i = 0;cur.moveToNext();i++) {
				citiesId[i] = cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID));
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return citiesId;
	}
	
	public List<CityObject> getCityList() {
		Cursor cur = null;
        List<CityObject> citiesList = new ArrayList<CityObject>();
		try{
			cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, null, null, null, null);
			while(cur.moveToNext()){
                citiesList.add(new CityObject( cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
                                               cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
                                               cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
                                               Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE)))));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }	
		}
		return citiesList;
	}

    public static List<CityObject> getCityList(Cursor cur) {
        List<CityObject> citiesList = new ArrayList<CityObject>();
        try{
            while(cur.moveToNext()){
                citiesList.add(new CityObject( cur.getInt(cur.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
                        Boolean.parseBoolean(cur.getString(cur.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE)))));
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (cur != null) { cur.close(); }
        }
        return citiesList;
    }

    public static CityObject getCityObject(Cursor cursor) {
        CityObject city = null;
        try{
            if(cursor.moveToFirst()) {
                city = new CityObject(
                        cursor.getInt(cursor.getColumnIndex(WeatherDB.Cities.SERVER_CITY_ID)),
                        cursor.getString(cursor.getColumnIndex(WeatherDB.Cities.CITY_NAME)),
                        cursor.getString(cursor.getColumnIndex(WeatherDB.Cities.CITY_COUNTRY)),
                        Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(WeatherDB.Cities.CITY_FAVOURITE))));
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null) { cursor.close(); }
        }
        return city;
    }
	
	public List<WeatherObject> getWeatherObjects(int cityServerId) {
		Cursor cur = null;
        List<WeatherObject> weatherList = new ArrayList<WeatherObject>();
		try{
			cur = contentResolver.query(WeatherContentProvider.WEATHER_CONTENT_URI, null, WeatherDB.Weather.WEATHER_CITY_ID + " = " + cityServerId, null, null);
			while(cur.moveToNext()) {
                weatherList.add(new WeatherObject(
                        cur.getInt(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CITY_ID)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_TEMPERATURE)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CONDITION)),
                        cur.getLong(cur.getColumnIndex(WeatherDB.Weather.WEATHER_DATE)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_IMAGE))));
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return weatherList;
	}

    public static List<WeatherObject> getWeatherObjects(Cursor cur) {
        List<WeatherObject> weatherList = new ArrayList<WeatherObject>();
        try{
            while(cur.moveToNext()) {
                weatherList.add(new WeatherObject(
                        cur.getInt(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CITY_ID)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_TEMPERATURE)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_CONDITION)),
                        cur.getLong(cur.getColumnIndex(WeatherDB.Weather.WEATHER_DATE)),
                        cur.getString(cur.getColumnIndex(WeatherDB.Weather.WEATHER_IMAGE)) ));
            }
        } catch(Exception e){
            e.printStackTrace();
        } finally {
            if (cur != null) { cur.close(); }
        }
        return weatherList;
    }
	
	public boolean writeCityObject(CityObject... city) {
		boolean result = true;
		ContentValues cv = new ContentValues();
		Cursor cur = null;
		for (CityObject cityObject : city) {
			try{
				cur = contentResolver.query(WeatherContentProvider.CITY_CONTENT_URI, null, 
											WeatherDB.Cities.SERVER_CITY_ID + " = " + cityObject.getServerCityId(), null, null);
				if(!cur.moveToFirst()){
					cv.put(WeatherDB.Cities.SERVER_CITY_ID, cityObject.getServerCityId());
					cv.put(WeatherDB.Cities.CITY_NAME, cityObject.getName());
					cv.put(WeatherDB.Cities.CITY_COUNTRY, cityObject.getCountry());
					cv.put(WeatherDB.Cities.CITY_FAVOURITE, cityObject.isFavourite());
					contentResolver.insert(WeatherContentProvider.CITY_CONTENT_URI, cv);
				}
			} catch(Exception e){
				result = false;
			} finally {
				if (cur != null) { cur.close(); }
				cv.clear();
			}
		}
		return result;
	}
	
	public boolean writeWeatherObjects(WeatherObject... weatherArray) {
		try{
			ContentValues cv = new ContentValues();
			for (WeatherObject weather : weatherArray) {
				cv.put(WeatherDB.Weather.WEATHER_CITY_ID, weather.getServerCityId());
				cv.put(WeatherDB.Weather.WEATHER_TEMPERATURE, weather.getTemperature());
				cv.put(WeatherDB.Weather.WEATHER_CONDITION, weather.getCondition());
				cv.put(WeatherDB.Weather.WEATHER_DATE, weather.getDate());
				cv.put(WeatherDB.Weather.WEATHER_IMAGE, weather.getIcon());
				contentResolver.insert(WeatherContentProvider.WEATHER_CONTENT_URI, cv);
				cv.clear();
			}
		} catch(Exception e){
			return false;
		}
		return true;
	}
	
	public boolean updateWeather(WeatherObject... weatherArray) {
		if(weatherArray != null) {
			this.deleteWeather(weatherArray[0].getServerCityId());
			writeWeatherObjects(weatherArray);
			return true;
		} else {
			return false;
		}
	}
	
	public boolean makeCityFavourite(CityObject city) {
		if( city != null && !city.isFavourite() ) {
			ContentValues cv = new ContentValues();
	    	cv.put(WeatherDB.Cities.CITY_FAVOURITE, "false");
	    	contentResolver.update(WeatherContentProvider.CITY_CONTENT_URI, cv, WeatherDB.Cities.CITY_FAVOURITE + " = 'true'", null);
	    	cv.clear();
	    	cv.put(WeatherDB.Cities.CITY_FAVOURITE, "true");
	    	int res = contentResolver.update(	WeatherContentProvider.CITY_CONTENT_URI,
	    						 			    cv,
	    						 			    WeatherDB.Cities.SERVER_CITY_ID + " = " + city.getServerCityId(),
	    						 			    null);
	    	if(res > 0){
                city.setFavourite(true);
                return true;
            }
		}
    	return false;
	}
	
	public boolean deleteCity(CityObject cityObject) {
		boolean result = false;
    	if(isCityExist(cityObject.getServerCityId()) && this.getCityList().size() > 1){
    		this.deleteWeather(cityObject.getServerCityId());
    		this.deleteCity(cityObject.getServerCityId());
	    	if(cityObject.isFavourite()) {
	    		this.makeCityFavourite(this.getCityList().get(0));
	    	}
	    	result = true;
    	}
    	return result;
	}

	public boolean deleteWeather(int... cityserverId) {
		int countDelete = 0;
		for (int serverId : cityserverId) {
			countDelete = contentResolver.delete( WeatherContentProvider.WEATHER_CONTENT_URI, 
					WeatherDB.Weather.WEATHER_CITY_ID + " = " + serverId,
					null);
		}
		return countDelete > 0;		
	}
	
	public boolean deleteCity(int... cityserverId) {
		int countDelete = 0;
		for (int serverId : cityserverId) {
			countDelete = contentResolver.delete( WeatherContentProvider.CITY_CONTENT_URI, WeatherDB.Cities.SERVER_CITY_ID + " = " + serverId, null);
		}
		return countDelete > 0;		
	}
	
	public boolean isCityExist(int cityServerId) {
		Boolean result = false;
		Cursor cur = null;
		try {
            cur = contentResolver.query( WeatherContentProvider.CITY_CONTENT_URI,
                                         new String[]{WeatherDB.Cities.SERVER_CITY_ID},
                                         WeatherDB.Cities.SERVER_CITY_ID + " = " + cityServerId, null, null);
            result = cur.getCount() > 0;
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if (cur != null) { cur.close(); }
		}
		return result;
	}

}
