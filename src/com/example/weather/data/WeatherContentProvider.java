package com.example.weather.data;

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

    //final String LOG_TAG = WeatherContentProvider.class.getSimpleName();

    static final String DB_WEAtHER_TABLE_CREATE = "create table " + WeatherDB.Cities.TABLE_NAME + "("
            + WeatherDB.Cities.CITY_ID + " integer primary key autoincrement, "
            + WeatherDB.Cities.SERVER_CITY_ID + " integer, "
            + WeatherDB.Cities.CITY_NAME + " text ,"
            + WeatherDB.Cities.CITY_COUNTRY + " text ,"
            + WeatherDB.Cities.CITY_FAVOURITE + " text );";

    static final String DB_FORECAST_TABLE_CREATE = "create table " + WeatherDB.Weather.TABLE_NAME + "("
            + WeatherDB.Weather.WEATHER_ID + " integer primary key autoincrement, "
            + WeatherDB.Weather.WEATHER_CITY_ID + " integer, "
            + WeatherDB.Weather.WEATHER_TEMPERATURE + " text ,"
            + WeatherDB.Weather.WEATHER_CONDITION + " text ,"
            + WeatherDB.Weather.WEATHER_IMAGE + " text ,"
            + WeatherDB.Weather.WEATHER_DATE + " numeric " + ");";

    // path
    static final String CITIES_PATH = WeatherDB.Cities.TABLE_NAME;
    public static final Uri CITY_CONTENT_URI = Uri.parse("content://" + WeatherDB.AUTHORITY + "/" + CITIES_PATH);
    static final String CITY_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + WeatherDB.AUTHORITY + "." + CITIES_PATH;
    static final String CITY_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + WeatherDB.AUTHORITY + "." + CITIES_PATH;

    static final String WEATHER_PATH = WeatherDB.Weather.TABLE_NAME;
    public static final Uri WEATHER_CONTENT_URI = Uri.parse("content://" + WeatherDB.AUTHORITY + "/" + WEATHER_PATH);
    static final String WEATHER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + WeatherDB.AUTHORITY + "." + WEATHER_PATH;
    static final String WEATHER_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + WeatherDB.AUTHORITY + "." + WEATHER_PATH;

    static final int URI_ALL_ROWS_CITIES = 1;
    static final int URI_SINGLE_CITIES = 2;
    static final int URI_ALL_ROWS_WEATHER = 3;
    static final int URI_SINGLE_WEATHER = 4;

    private static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(WeatherDB.AUTHORITY, CITIES_PATH, URI_ALL_ROWS_CITIES);
        uriMatcher.addURI(WeatherDB.AUTHORITY, CITIES_PATH + "/#", URI_SINGLE_CITIES);
        uriMatcher.addURI(WeatherDB.AUTHORITY, WEATHER_PATH, URI_ALL_ROWS_WEATHER);
        uriMatcher.addURI(WeatherDB.AUTHORITY, WEATHER_PATH + "/#", URI_SINGLE_WEATHER);
    }

    DBHelper dbHelper;
    SQLiteDatabase db;

    public boolean onCreate() {
        dbHelper = new DBHelper(getContext());
        dbHelper.onUpgrade(db, 7, WeatherDB.VERSION);
        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        db = dbHelper.getWritableDatabase();
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES: {
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WeatherDB.Cities.CITY_FAVOURITE + " ASC";
                }
                cursor = db.query(WeatherDB.Cities.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case URI_SINGLE_CITIES: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                cursor = db.query(WeatherDB.Cities.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case URI_ALL_ROWS_WEATHER: {
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WeatherDB.Weather.WEATHER_DATE + " ASC";
                }
                cursor = db.query(WeatherDB.Weather.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            case URI_SINGLE_WEATHER: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                cursor = db.query(WeatherDB.Weather.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            }
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values) {
        db = dbHelper.getWritableDatabase();
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES: {
                long rowID = db.insert(WeatherDB.Cities.TABLE_NAME, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
            case URI_ALL_ROWS_WEATHER: {
                long rowID = db.insert(WeatherDB.Weather.TABLE_NAME, null, values);
                resultUri = ContentUris.withAppendedId(uri, rowID);
                getContext().getContentResolver().notifyChange(resultUri, null);
                return resultUri;
            }
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }
    }

    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int cnt;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES: {
                if (TextUtils.isEmpty(selection)) {
                    cnt = 0;
                } else {
                    cnt = db.delete(WeatherDB.Cities.TABLE_NAME, selection, selectionArgs);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            }
            case URI_SINGLE_CITIES: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                cnt = db.delete(WeatherDB.Cities.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            }
            case URI_ALL_ROWS_WEATHER: {
                if (TextUtils.isEmpty(selection)) {
                    cnt = 0;
                } else {
                    cnt = db.delete(WeatherDB.Weather.TABLE_NAME, selection, selectionArgs);
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            }
            case URI_SINGLE_WEATHER: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                cnt = db.delete(WeatherDB.Weather.TABLE_NAME, selection, selectionArgs);
                getContext().getContentResolver().notifyChange(uri, null);
                return cnt;
            }
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id;
        int cnt;
        db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES: {
                cnt = db.update(WeatherDB.Cities.TABLE_NAME, values, selection, selectionArgs);
                if( cnt > 0 ){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return cnt;
            }
            case URI_SINGLE_CITIES: {
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                cnt = db.update(WeatherDB.Cities.TABLE_NAME, values, selection, selectionArgs);
                if( cnt > 0 ){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return cnt;
            }
            case URI_ALL_ROWS_WEATHER: {
                cnt = db.update(WeatherDB.Weather.TABLE_NAME, values, selection, selectionArgs);
                if( cnt > 0 ){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return cnt;
            }
            case URI_SINGLE_WEATHER: {
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                cnt = db.update(WeatherDB.Weather.TABLE_NAME, values, selection, selectionArgs);
                if( cnt > 0 ){
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return cnt;
            }
            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }
    }

    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES:
                return CITY_CONTENT_TYPE;
            case URI_SINGLE_CITIES:
                return CITY_CONTENT_ITEM_TYPE;
            case URI_ALL_ROWS_WEATHER:
                return WEATHER_CONTENT_TYPE;
            case URI_SINGLE_WEATHER:
                return WEATHER_CONTENT_ITEM_TYPE;
        }
        return null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, WeatherDB.NAME, null, WeatherDB.VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_WEAtHER_TABLE_CREATE);
            db.execSQL(DB_FORECAST_TABLE_CREATE);
            ContentValues cv = new ContentValues();
            cv.put(WeatherDB.Cities.SERVER_CITY_ID, 5128581);
            cv.put(WeatherDB.Cities.CITY_NAME, "New York");
            cv.put(WeatherDB.Cities.CITY_COUNTRY, "US");
            cv.put(WeatherDB.Cities.CITY_FAVOURITE, "true");
            db.insert(WeatherDB.Cities.TABLE_NAME, null, cv);
            cv.clear();
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion != newVersion) {
                db = dbHelper.getWritableDatabase();
                db.execSQL("DROP TABLE IF EXISTS " + WeatherDB.Cities.TABLE_NAME);
                db.execSQL("DROP TABLE IF EXISTS " + WeatherDB.Weather.TABLE_NAME);
                onCreate(db);
            }
        }
    }

}
