package com.example.weather.update;

import com.loopj.android.http.*;

public class WeatherRestClient {
	
	private final static String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}
	
	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
	
}
