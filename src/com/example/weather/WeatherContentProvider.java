package com.example.weather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class WeatherContentProvider extends ContentProvider {
	
	final String LOG_TAG = "WeatherContentProvider LOG";
	
	  // Скрипт создания таблицы
	  static final String DB_CREATE = "create table " + WeatherDB.Cities.TABLE + "("
	      + WeatherDB.Cities.ID + " integer primary key autoincrement, "
	      + WeatherDB.Cities.CITY_ID + " integer, " 
	      + WeatherDB.Cities.CITY_NAME + " text ,"
	      + WeatherDB.Cities.COUNTRY + " text ,"
	      + WeatherDB.Cities.FAVOURITE_CITY + " text ,"
	      + WeatherDB.Cities.TEMPERATURE + " text ,"
	      + WeatherDB.Cities.WEATHER + " text ,"
	      + WeatherDB.Cities.TIME + " text ,"
	      + WeatherDB.Cities.ICON + " text " + ");";

	  // // Uri
	  // authority
	  
	  // path
	  static final String WEATHER_PATH = "forecasts";

	  // Общий Uri
	  public static final Uri WEATHER_CONTENT_URI = Uri.parse("content://" + WeatherDB.AUTHORITY + "/" + WEATHER_PATH);

	  // Типы данных
	  // набор строк
	  static final String WEATHER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + WeatherDB.AUTHORITY + "." + WEATHER_PATH;

	  // одна строка
	  static final String WEATHER_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + WeatherDB.AUTHORITY + "." + WEATHER_PATH;

	  //// UriMatcher
	  // общий Uri
	  static final int URI_ALL_ROWS = 1;

	  // Uri с указанным ID
	  static final int URI_SINGLE = 2;

	  // описание и создание UriMatcher
	  private static final UriMatcher uriMatcher;
	  static {
	    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	    uriMatcher.addURI(WeatherDB.AUTHORITY, WEATHER_PATH, URI_ALL_ROWS);
	    uriMatcher.addURI(WeatherDB.AUTHORITY, WEATHER_PATH + "/#", URI_SINGLE);
	  }

	  DBHelper dbHelper;
	  SQLiteDatabase db;
	  
	  
	  public boolean onCreate() {
		    Log.d(LOG_TAG, "onCreate");
		    dbHelper = new DBHelper(getContext());
		    dbHelper.onUpgrade(db, 1, WeatherDB.VERSION);
		    return true;
		  }

		  // чтение
		  public Cursor query(	
					Uri uri, 
	  			  	String[] projection,
	  			  	String selection,
	  			  	String[] selectionArgs,
	  			  	String sortOrder) {
			  
		    Log.d(LOG_TAG, "query, " + uri.toString());
		    
		    switch (uriMatcher.match(uri)) {
		    
		    case URI_ALL_ROWS:{
		      if (TextUtils.isEmpty(sortOrder)) {
		    	  sortOrder = WeatherDB.Cities.CITY_NAME + " ASC";
		      }
		    }break;
		    
		    case URI_SINGLE:{
		      String id = uri.getLastPathSegment();
		      Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
		      if (TextUtils.isEmpty(selection)) {
		    	  selection = WeatherDB.Cities.ID + " = " + id;
		      } else {
		    	  selection = selection + " AND " + WeatherDB.Cities.ID + " = " + id;
		      }
		    }break;
		    
		    default:
		    	throw new IllegalArgumentException("Wrong URI: " + uri);
		    	
		    }
		    
		    db = dbHelper.getWritableDatabase();
		    
		    Cursor cursor = db.query(WeatherDB.Cities.TABLE, projection, selection, selectionArgs, null, null, sortOrder);
		    cursor.setNotificationUri(getContext().getContentResolver(), WEATHER_CONTENT_URI);
		    
		    return cursor;
		  }

		  public Uri insert(Uri uri, ContentValues values) {
		    Log.d(LOG_TAG, "insert, " + uri.toString());
		    
		    if (uriMatcher.match(uri) != URI_ALL_ROWS){
		    	throw new IllegalArgumentException("Wrong URI: " + uri);
		    }

		    db = dbHelper.getWritableDatabase();
		    long rowID = db.insert(WeatherDB.Cities.TABLE, null, values);
		    
		    Uri resultUri = ContentUris.withAppendedId(WEATHER_CONTENT_URI, rowID);
		    getContext().getContentResolver().notifyChange(resultUri, null);
		    
		    return resultUri;
		  }

		  public int delete(Uri uri, String selection, String[] selectionArgs) {
		    Log.d(LOG_TAG, "delete, " + uri.toString());
		    switch (uriMatcher.match(uri)) {
		    
		    case URI_ALL_ROWS:{
		      Log.d(LOG_TAG, "URI_CONTACTS");
		    }break;
		      
		    case URI_SINGLE:{
		      String id = uri.getLastPathSegment();
		      Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
		      if (TextUtils.isEmpty(selection)) {
		        selection = WeatherDB.Cities.ID + " = " + id;
		      } else {
		        selection = selection + " AND " + WeatherDB.Cities.ID + " = " + id;
		      }
		    }break;
		      
		    default:
		      throw new IllegalArgumentException("Wrong URI: " + uri);
		      
		    }
		    db = dbHelper.getWritableDatabase();
		    int cnt = db.delete(WeatherDB.Cities.TABLE, selection, selectionArgs);
		    getContext().getContentResolver().notifyChange(uri, null);
		    return cnt;
		  }

		  public int update(Uri uri, ContentValues values, String selection,
		      String[] selectionArgs) {
		    Log.d(LOG_TAG, "update, " + uri.toString());
		    switch (uriMatcher.match(uri)) {
		    
		    case URI_ALL_ROWS:
		      
		    	Log.d(LOG_TAG, "URI_CONTACTS");

		    break;
		      
		    case URI_SINGLE:
		    	
		      String id = uri.getLastPathSegment();
		      Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
		      
		      if (TextUtils.isEmpty(selection)) {
		    	  selection = WeatherDB.Cities.ID + " = " + id;
		      } else {
		    	  selection = selection + " AND " + WeatherDB.Cities.ID + " = " + id;
		      }
		      
		      break;
		      
		    default:
		    	
		      throw new IllegalArgumentException("Wrong URI: " + uri);
		    
		    }
		    db = dbHelper.getWritableDatabase();
		    int cnt = db.update(WeatherDB.Cities.TABLE, values, selection, selectionArgs);
		    getContext().getContentResolver().notifyChange(uri, null);
		    return cnt;
		  }

		  public String getType(Uri uri) {
		    Log.d(LOG_TAG, "getType, " + uri.toString());
		    switch (uriMatcher.match(uri)) {
		    case URI_ALL_ROWS:
		      return WEATHER_CONTENT_TYPE;
		    case URI_SINGLE:
		      return WEATHER_CONTENT_ITEM_TYPE;
		    }
		    return null;
		  }
	
	 private class DBHelper extends SQLiteOpenHelper {

	    public DBHelper(Context context) {
	      super(context, WeatherDB.NAME, null, WeatherDB.VERSION);
	    }

	    public void onCreate(SQLiteDatabase db) {
	      db.execSQL(DB_CREATE);
	      ContentValues cv = new ContentValues();
	      for (int i = 1; i <= 3; i++) {
	        cv.put(WeatherDB.Cities.CITY_ID, i*100+"");
	        cv.put(WeatherDB.Cities.CITY_NAME, "NYrk");
	        cv.put(WeatherDB.Cities.COUNTRY, "US");
	        if(i==1)cv.put(WeatherDB.Cities.FAVOURITE_CITY, "false");
	        cv.put(WeatherDB.Cities.TEMPERATURE, "-12");
	        cv.put(WeatherDB.Cities.WEATHER, "sadsads sa d");
	        cv.put(WeatherDB.Cities.TIME, "123213213");
	        cv.put(WeatherDB.Cities.ICON, "ico90.png");
	        db.insert(WeatherDB.Cities.TABLE, null, cv);
	      }
	    }

	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	
	    }
	  }

}
