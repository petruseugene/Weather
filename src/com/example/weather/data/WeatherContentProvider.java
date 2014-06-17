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

    final String LOG_TAG = "WeatherContentProvider LOG";

    static final String DB_WETAHER_TABLE_CREATE = "create table " + WeatherDB.Cities.TABLE_NAME + "("
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

    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {

        String table_name = "";

        switch (uriMatcher.match(uri)) {

            case URI_ALL_ROWS_CITIES: {
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WeatherDB.Cities.CITY_NAME + " ASC";
                }
                table_name = WeatherDB.Cities.TABLE_NAME;
            }
            break; // FIXME FORMAT!

            case URI_SINGLE_CITIES: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                table_name = WeatherDB.Cities.TABLE_NAME;
            }
            break;

            case URI_ALL_ROWS_WEATHER: {
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = WeatherDB.Weather.WEATHER_DATE + " ASC";
                }
                table_name = WeatherDB.Weather.TABLE_NAME;
            }
            break;

            case URI_SINGLE_WEATHER: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                table_name = WeatherDB.Weather.TABLE_NAME;
            }
            break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(table_name, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), CITY_CONTENT_URI);
        return cursor;
    }


    public Uri insert(Uri uri, ContentValues values) {
        db = dbHelper.getWritableDatabase();
        Uri resultUri;
        switch (uriMatcher.match(uri)) {
            case URI_ALL_ROWS_CITIES: {
                long rowID = db.insert(WeatherDB.Cities.TABLE_NAME, null, values);
                resultUri = ContentUris.withAppendedId(CITY_CONTENT_URI, rowID);
            }
            break;
            case URI_ALL_ROWS_WEATHER: {
                long rowID = db.insert(WeatherDB.Weather.TABLE_NAME, null, values);
                resultUri = ContentUris.withAppendedId(WEATHER_CONTENT_URI, rowID);
            }
            break;
            default: {
                throw new IllegalArgumentException("Wrong URI: " + uri);
            }
        }

        getContext().getContentResolver().notifyChange(resultUri, null);

        return resultUri;
    }


    public int delete(Uri uri, String selection, String[] selectionArgs) {
        db = dbHelper.getWritableDatabase();
        int cnt = 0;
        switch (uriMatcher.match(uri)) {

            case URI_ALL_ROWS_CITIES: {
                if (TextUtils.isEmpty(selection)) {
                    cnt = 0;
                } else {
                    cnt = db.delete(WeatherDB.Cities.TABLE_NAME, selection, selectionArgs);
                }
            }
            break;

            case URI_SINGLE_CITIES: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                cnt = db.delete(WeatherDB.Cities.TABLE_NAME, selection, selectionArgs);
            }
            break;

            case URI_ALL_ROWS_WEATHER: {
                if (TextUtils.isEmpty(selection)) {
                    cnt = 0;
                } else {
                    cnt = db.delete(WeatherDB.Weather.TABLE_NAME, selection, selectionArgs);
                }
            }
            break;

            case URI_SINGLE_WEATHER: {
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                cnt = db.delete(WeatherDB.Weather.TABLE_NAME, selection, selectionArgs);
            }
            break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }


    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String id = "";
        db = dbHelper.getWritableDatabase();
        int cnt = 0;
        switch (uriMatcher.match(uri)) {

            case URI_ALL_ROWS_CITIES:
                cnt = db.update(WeatherDB.Cities.TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_SINGLE_CITIES:
                id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Cities.CITY_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Cities.CITY_ID + " = " + id;
                }
                cnt = db.update(WeatherDB.Cities.TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_ALL_ROWS_WEATHER:
                cnt = db.update(WeatherDB.Cities.TABLE_NAME, values, selection, selectionArgs);
                break;

            case URI_SINGLE_WEATHER:
                id = uri.getLastPathSegment();

                if (TextUtils.isEmpty(selection)) {
                    selection = WeatherDB.Weather.WEATHER_ID + " = " + id;
                } else {
                    selection = selection + " AND " + WeatherDB.Weather.WEATHER_ID + " = " + id;
                }
                cnt = db.update(WeatherDB.Weather.TABLE_NAME, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Wrong URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return cnt;
    }

    public String getType(Uri uri) {
        Log.d(LOG_TAG, "getType, " + uri.toString());
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
            db.execSQL(DB_WETAHER_TABLE_CREATE);
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
