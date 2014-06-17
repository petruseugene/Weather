package com.example.weather.objects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParcers {
	
	private static final String JSON_ID 		= "id";
	private static final String JSON_SYS 		= "sys";
	private static final String JSON_COUNTRY 	= "country";
	private static final String JSON_NAME 		= "name";
	private static final String JSON_CITY 		= "city";
	private static final String JSON_LIST 		= "list";
	
	private static final String JSON_TEMP 			= "temp";
	private static final String JSON_MAX 			= "max";
	private static final String JSON_WEATHER 		= "weather";
	private static final String JSON_ICON 			= "icon";
	private static final String JSON_DESCRIPTION 	= "description";
	private static final String JSON_DT 			= "dt";
	private static final String JSON_ICON_PREFIX 	= "img";
	private static final String TEMP_METRIC 		= "\u00B0C";
	
	public static CityObject[] parceSearchJsonToCityObjects(JSONObject jsonObj) {
		CityObject cityArray[] = null;
		try{
			JSONArray jsonArr = jsonObj.getJSONArray(JSON_LIST);
			if(jsonArr.length() > 0) {
				cityArray = new CityObject[jsonArr.length()];
				for (int i = 0;i<cityArray.length;i++) {
					JSONObject currentObject = jsonArr.getJSONObject(i);
					cityArray[i] = new CityObject(currentObject.getInt(JSON_ID), currentObject.getString(JSON_NAME), (currentObject.getJSONObject(JSON_SYS)).getString(JSON_COUNTRY), false);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return cityArray;
	}
	
	public static WeatherObject[] parceJsonToWeatherArray(JSONObject rootJson) {
		WeatherObject weather[] = null;
		try{
			JSONObject jsonCity = new JSONObject(rootJson.getString(JSON_CITY));
			JSONArray jsonListArr = rootJson.getJSONArray(JSON_LIST);
			int cityId = jsonCity.getInt(JSON_ID);
			weather = new WeatherObject[jsonListArr.length()];
			for(int i = 0 ; i < jsonListArr.length(); i++ ) {
				JSONObject listItem = jsonListArr.getJSONObject(i);
				weather[i] = new WeatherObject(	cityId,
												listItem.getJSONObject(JSON_TEMP).getString(JSON_MAX)+TEMP_METRIC,
												listItem.getJSONArray(JSON_WEATHER).getJSONObject(0).getString(JSON_DESCRIPTION),
												listItem.getLong(JSON_DT)*1000, // FIXME MAGIC?
												JSON_ICON_PREFIX + listItem.getJSONArray(JSON_WEATHER).getJSONObject(0).getString(JSON_ICON).substring(0, 2) );
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return weather;
	}
	
}
