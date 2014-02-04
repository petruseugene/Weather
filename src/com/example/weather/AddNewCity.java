package com.example.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class AddNewCity extends Activity {
	
	private String search_query = "";
	int city_id[];
	String res[];
	
	final String LOG_TAG = "WeatherSerchLogs";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_city);
		
		
		Button search_button = (Button) findViewById(R.id.search_button);
		search_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText) findViewById(R.id.edit_search_query);
				search_query = et.getText().toString();
				Log.d(LOG_TAG, search_query);
				new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/find?q=" + search_query + "&mode=json&units=metric");
			}
		});
		
	}
	
	

	public String GET(String url) {
		InputStream inputStream = null;
		String result = "";
		if(isConnected()){
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
				inputStream = httpResponse.getEntity().getContent();
				if (inputStream != null) {
					result = convertInputStreamToString(inputStream);
				} else {
					result = "Cant get data.";
				}
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}
		}
		return result;
	}

	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) result += line;
		inputStream.close();
		return result;
	}

	
	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
	
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			updateSearchResult(result);
		}
	}
	
	
	public void updateSearchResult(String result){
		try {
			JSONObject json = new JSONObject(result);
			JSONArray jsonArr = json.getJSONArray("list");
			res = new String[jsonArr.length()];
			city_id = new int[jsonArr.length()];
			for(int i = 0;i<jsonArr.length();i++){
				res[i] = jsonArr.getJSONObject(i).getString("name") + ", "  ;//+jsonArr.getJSONObject(i).getJSONObject("sys").getString("country");
				city_id[i] = jsonArr.getJSONObject(i).getInt("id");
			}
			
			ListView cityList = (ListView) findViewById(R.id.list_of_search);
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, res);
			cityList.setAdapter(adapter);
			
			cityList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
					Intent intent = new Intent();
				    intent.putExtra("newCityId", city_id[position]);
				    setResult(RESULT_OK, intent);
				    finish();
				}
			});
				
			
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
		
}
