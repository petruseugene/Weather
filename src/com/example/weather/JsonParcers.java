package com.example.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParcers {
	
	public static CityObject parceJsonToCityObject(String json){
		CityObject parcedCity = null;
		try{
			JSONObject rootJson = new JSONObject(json);
			String countryName = new JSONObject(rootJson.getString("sys")).getString("country");
			
			parcedCity = new CityObject(rootJson.getInt("id"),
										rootJson.getString("name"),
										countryName,
										false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return parcedCity;
	}
	
	public static CityObject[] parceSearchJsonToCityObjects(String json){
		CityObject cityArray[] = null;
		try{
			JSONObject jsonObj = new JSONObject(json);
			JSONArray jsonArr = jsonObj.getJSONArray("list");
			cityArray = new CityObject[jsonArr.length()];
			for (int i = 0;i<cityArray.length;i++) {
				JSONObject currentObject = jsonArr.getJSONObject(i);
				cityArray[i] = new CityObject(currentObject.getInt("id"), currentObject.getString("name"), (currentObject.getJSONObject("sys")).getString("country"), false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return cityArray;
	}
	
	public static WeatherObject[] parceJsonToWeatherArray(String json){
		WeatherObject weather[] = null;
		try{
		JSONObject rootJson = new JSONObject(json);
		JSONObject jsonCity = new JSONObject(rootJson.getString("city"));
		JSONArray jsonListArr = rootJson.getJSONArray("list");
		int cityId = jsonCity.getInt("id");
		weather = new WeatherObject[jsonListArr.length()];
		for(int i = 0 ; i < jsonListArr.length(); i++ ){
			JSONObject listItem = jsonListArr.getJSONObject(i);
			weather[i] = new WeatherObject(	cityId,
											listItem.getJSONObject("temp").getString("max")+"°C",
											listItem.getJSONArray("weather").getJSONObject(0).getString("description"),
											listItem.getLong("dt")*1000,
											"img" + listItem.getJSONArray("weather").getJSONObject(0).getString("icon").substring(0, 2) );
		}
		
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		return weather;
	}
	
}
