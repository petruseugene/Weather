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
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AddNewCity extends ActionBarActivity {
	
	private final String LOG_TAG = "WeatherSerchLogs";
	//Extra data names constants
	final public static String NEW_CITY_ID = "NEW_CITY_ID";
	final public static String NEW_CITY_NAME = "NEW_CITY_NAME";
	final public static String NEW_CITY_COUNTRY = "NEW_CITY_COUNTRY";
	//GUI elements
	private TextView city_list_label;
	private ListView cityList;
	private Button search_button;
	private EditText editSearch;
	private ProgressDialog progress;
	private ActionBar ab;
	//Activity objects
	private static CityObject[] cityArray;
	private static String[] listArray;
	private static String search_query = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_city);
		findAllViews();
		initProgressWindow();
		initActionBar();
		
	    editSearch.setOnEditorActionListener(new OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	                searchRequest();
	            }
	            return false;
	        }
	    });
		
		search_button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				searchRequest();
			}
		});
	}

	private void findAllViews() {
		editSearch		= (EditText) findViewById(R.id.edit_search_query);
		city_list_label = (TextView)findViewById(R.id.city_list_label);
		cityList 		= (ListView) findViewById(R.id.list_of_search);
		search_button 	= (Button) findViewById(R.id.search_button);
	}
	
	private void initActionBar() {
		ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	private void initProgressWindow() {
		progress = new ProgressDialog(this);
		progress.setMessage(getString(R.string.search_dialog_text));
	    progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	    progress.setIndeterminate(true);
	}
	
	protected void searchRequest() {
		if(isConnected()){
			search_query = editSearch.getText().toString();
			new HttpAsyncTask().execute("http://api.openweathermap.org/data/2.5/find?q=" + search_query + "&mode=json&units=metric&type=like");
		    progress.show();
		}
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		    case android.R.id.home:
		    	this.finish();
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
    }

	@Override
	protected void onDestroy() {
		progress.cancel();
		super.onDestroy();
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
				Log.e("InputStream", e.getLocalizedMessage());
			}
		}
		return result;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) throws IOException {
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
		String line = "", result = "";
		while ((line = bufferedReader.readLine()) != null) result += line;
		inputStream.close();
		return result;
	}
	
	public boolean isConnected() {
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return GET(urls[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			updateSearchResultActivity(result);
		}
	}
	
	public void updateSearchResultActivity(String result){
		cityArray = JsonParcers.parceSearchJsonToCityObjects(result);
		if(cityArray != null){
			listArray = new String[cityArray.length];
			int i =0;
			for (CityObject cityObject : cityArray) {
				listArray[i] = cityObject.getCityNameCountry(); i++;
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
			cityList.setAdapter(adapter);
			city_list_label.setText(getString(R.string.search_result));
			city_list_label.setVisibility(View.VISIBLE);
			cityList.setVisibility(View.VISIBLE);
			progress.hide();
			
			cityList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
					DBworker db = new DBworker(getContentResolver());
					if( !db.isCityExist(cityArray[position].serverId) ) {
						Intent intent = new Intent();
					    intent.putExtra(NEW_CITY_ID, cityArray[position].serverId);
					    intent.putExtra(NEW_CITY_NAME, cityArray[position].name);
					    intent.putExtra(NEW_CITY_COUNTRY, cityArray[position].country);
					    setResult(RESULT_OK, intent);
					    progress.cancel();
					    finish();
				    } else {
				    	Toast.makeText(getBaseContext(), getString(R.string.has_such_city_message), Toast.LENGTH_LONG).show();
				    }
				}
			});
		} else {
			city_list_label.setText(getString(R.string.search_result_fail));
			city_list_label.setVisibility(View.VISIBLE);
			cityList.setVisibility(View.INVISIBLE);
			progress.hide();
		}
	}
		
}
