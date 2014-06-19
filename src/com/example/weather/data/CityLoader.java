package com.example.weather.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.example.weather.objects.CityObject;

import java.util.List;

public class CityLoader extends AsyncTaskLoader<List<CityObject>> {

    private List<CityObject> cityList;

    public CityLoader(Context context) {
        super(context);
    }

    @Override
    public List<CityObject> loadInBackground() {
        DBworker db = new DBworker(getContext().getContentResolver());
        cityList = db.getCityList();
        return cityList;
    }

}
