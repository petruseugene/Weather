package com.example.weather.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weather.R;
import com.example.weather.objects.WeatherObject;

import java.util.List;

/**
 * Created by Johnny on 14.07.14.
 */
public class WeatherAdapter extends BaseAdapter{

    private List<WeatherObject> weatherList;
    private LayoutInflater layoutInflater;

    public WeatherAdapter( Context context, List<WeatherObject> weatherList) {
        this.weatherList = weatherList;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return weatherList.size();
    }

    @Override
    public Object getItem(int position) {
        return weatherList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = layoutInflater.inflate(R.layout.forecast_item, parent, false);
        }
        WeatherObject weather = getWeather(position);
        TextView tvWeather = (TextView) view.findViewById(R.id.forecast_city_weather);
        TextView tvForecastDate = (TextView) view.findViewById(R.id.forecast_date);
        TextView tvTemp = (TextView) view.findViewById(R.id.forecast_city_temperature);
        ImageView weatherImage = (ImageView) view.findViewById(R.id.forecast_weather_image);

        weatherImage.setImageResource(weather.getImageResourceId(parent.getContext()));
        tvWeather.setText(weather.getCondition());
        tvTemp.setText(weather.getTemperature());
        tvForecastDate.setText(weather.getFormattedDate());

        return view;
    }

    private WeatherObject getWeather (int position){
        return (WeatherObject) getItem(position);
    }
}
