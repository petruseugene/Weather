package com.example.weather.activities;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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

import com.example.weather.R;
import com.example.weather.data.DBworker;
import com.example.weather.data.WeatherContentProvider;
import com.example.weather.objects.CityObject;
import com.example.weather.objects.JsonParsers;
import com.example.weather.update.GetWeatherService;
import com.example.weather.update.WeatherRestClient;
import com.example.weather.util.LoadingDialog;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AddNewCityActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //private final String LOG_TAG = AddNewCityActivity.class.getSimpleName();
	//Extra data names constants
	final public static String NEW_CITY_ID 			= "NEW_CITY_ID";
	final public static String NEW_CITY_NAME 		= "NEW_CITY_NAME";
	final public static String NEW_CITY_COUNTRY 	= "NEW_CITY_COUNTRY";
    //Dialog constant
    final public static String LOADING_DIALOG 	    = "LOADING_DIALOG";
    private final int CITY_LOADER = 0;
	//RequestParams string constants.
	final private static String SEARCH_URI 			= "find";
	final private static String SEARCH_PARAM 		= "q";
	final private static String SEARCH_MODE 		= "mode";
	final private static String SEARCH_MODE_JSON	= "json";
	final private static String SEARCH_UNITS 		= "units";
	final private static String SEARCH_UNITS_METRIC = "metric";
	final private static String SEARCH_TYPE 		= "type";
	final private static String SEARCH_TYPE_LIKE 	= "like";
    private static List<CityObject> cityArrayList = null;
	//GUI elements
	private TextView cityListLabel;
	private ListView cityList;
	private Button searchButton;
	private EditText editSearch;
	private DialogFragment progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_new_city);
		findAllViews();
		initProgressWindow();
		initActionBar();

        startLoader(CITY_LOADER);
        getContentResolver().registerContentObserver(WeatherContentProvider.CITY_CONTENT_URI, true, new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                restartLoader(CITY_LOADER);
            }
        });
	    editSearch.setOnEditorActionListener(new OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
	            	startSearch();
	            }
	            return false;
	        }
	    });
		
		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startSearch();
			}
		});
	}

    private void startLoader(int loaderId){
        getSupportLoaderManager().initLoader(loaderId, null, this);
    }

    private void restartLoader(int loaderId){
        getSupportLoaderManager().restartLoader(loaderId, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        switch (id){
            case CITY_LOADER:{
                return new CursorLoader(this, WeatherContentProvider.CITY_CONTENT_URI,null,null,null,null);
            }
            default:{
                return null;
            }
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case CITY_LOADER:{
                cityArrayList = DBworker.getCityList(cursor);
            } break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        int loaderId = cursorLoader.getId();
        switch (loaderId){
            case CITY_LOADER:{
                cityArrayList = null;
            } break;
        }
        getSupportLoaderManager().getLoader(loaderId).forceLoad();
    }
	/* 
	 * Creating RequestParams and sending search  request.
	 */
	private void startSearch(){
        showDialogLoading();
		try {
            RequestParams params = new RequestParams();
            params.put(SEARCH_PARAM, editSearch.getText()+"");
            params.put(SEARCH_MODE, SEARCH_MODE_JSON);
            params.put(SEARCH_UNITS, SEARCH_UNITS_METRIC);
            params.put(SEARCH_TYPE, SEARCH_TYPE_LIKE);
            searchRequest(SEARCH_URI, params);
    	} catch (JSONException e) {
    		e.printStackTrace();
    	} catch (NullPointerException e){
            e.printStackTrace();
        }
	}
	/* 
	 * Sending search request and result handlers.
	 */
	private void searchRequest(String urlAddition, RequestParams params) throws JSONException {
		WeatherRestClient.get(urlAddition, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(JSONObject result) {
                updateSearchResultActivity(result);
            }

            @Override
            public void onFailure(Throwable e, JSONObject errorResponse) {
                hideDialogLoading();
                super.onFailure(e, errorResponse);
            }
        });
    }

    private void showDialogLoading(){
        progressDialog.show(getSupportFragmentManager(), LOADING_DIALOG);
    }

    private void hideDialogLoading(){
        progressDialog.dismiss();
    }
	
	private void findAllViews() {
		editSearch		= (EditText) findViewById(R.id.edit_search_query);
		cityListLabel 	= (TextView) findViewById(R.id.city_list_label);
		cityList 		= (ListView) findViewById(R.id.list_of_search);
		searchButton 	= (Button) findViewById(R.id.search_button);
	}
	
	private void initActionBar() {
		ActionBar ab = getSupportActionBar();
		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);
	}

	private void initProgressWindow() {
		progressDialog = new LoadingDialog();
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

	/* 
	 * Updating SearchActivity UI and hiding loading dialog.
	 * And sending result to MainActivity.
	 */
	public void updateSearchResultActivity(JSONObject result){
		final CityObject[] cityArray = JsonParsers.parseSearchJsonToCityObjects(result);
		if(cityArray != null){
			String[] listArray = new String[cityArray.length];
			int i =0;
			for (CityObject cityObject : cityArray) {
				listArray[i] = cityObject.getCityNameCountry(); i++;
			}
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
			cityList.setAdapter(adapter);
			cityListLabel.setText(getString(R.string.search_result));
			cityListLabel.setVisibility(View.VISIBLE);
			cityList.setVisibility(View.VISIBLE);
			hideDialogLoading();
			
			cityList.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View v,	int position, long id) {
					if( !isCityExist(cityArray[position]) ) {
						Intent intent = new Intent();
					    intent.putExtra(NEW_CITY_ID, cityArray[position].getServerCityId());
					    intent.putExtra(NEW_CITY_NAME, cityArray[position].getName());
					    intent.putExtra(NEW_CITY_COUNTRY, cityArray[position].getCountry());
					    setResult(RESULT_OK, intent);
                        startServiceToUpdateData();
					    finish();
				    } else {
				    	Toast.makeText(getBaseContext(), getString(R.string.has_such_city_message), Toast.LENGTH_LONG).show();
				    }
				}
			});
		} else {
			cityListLabel.setText(getString(R.string.search_result_fail));
			cityListLabel.setVisibility(View.VISIBLE);
			cityList.setVisibility(View.INVISIBLE);
			hideDialogLoading();
		}
	}
    private void startServiceToUpdateData() {
        Intent serviceIntent = new Intent(this, GetWeatherService.class);
        startService(serviceIntent);
    }

    private boolean isCityExist(CityObject cityToCheck){
        boolean isExist = false;
        for(CityObject city : cityArrayList ){
            if(city.getServerCityId().equals(cityToCheck.getServerCityId())) isExist = true;
        }
        return isExist;
    }
		
}
