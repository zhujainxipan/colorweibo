package com.ht.jellybean.thread;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import com.ht.jellybean.util.FastJsonTools;
import com.ht.jellybean.util.HttpUtils;
import com.ht.jellybean.model.weather.CityWeather;
import com.ht.jellybean.model.weather.DayData;
import com.ht.jellybean.model.weather.Weathers;

import java.io.InputStream;
import java.util.List;

/**
 * Created by annuo on 2015/6/5.
 */

/**
 * 获取天气的加载器
 */
public class WeatherTask extends AsyncTask<String, Void, String> {
    private TextView tempeTextView;

    public WeatherTask(TextView tempeTextView) {
        this.tempeTextView = tempeTextView;
    }

    @Override
    protected String doInBackground(String... strings) {
        String url = strings[0];
        Log.d("url", url);
        InputStream stream = HttpUtils.getInputStream(url);
        String jsonString = HttpUtils.changeToString(stream, "utf-8");
        return jsonString;
    }


    @Override
    protected void onPostExecute(String result) {
        Weathers weathers = FastJsonTools.getObject(result, Weathers.class);
        Log.d("jsonStr", result);
        List<CityWeather> weathersResults = weathers.getResults();
        if (weathersResults != null) {
            CityWeather cityWeather = weathersResults.get(0);
            if (cityWeather != null) {
                List<DayData> weatherData = cityWeather.getWeather_data();
                if (weatherData != null) {
                    DayData dayData = weatherData.get(0);
                    if (dayData != null) {
                        String weather = dayData.getWeather();
                        String temperature = dayData.getTemperature();
                        tempeTextView.setText(weather + " " + temperature);
                    }
                }
            }
        }

    }

}
