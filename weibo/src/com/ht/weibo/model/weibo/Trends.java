package com.ht.weibo.model.weibo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by annuo on 2015/6/7.
 */
public class Trends implements Serializable {
    private List<Trend> nowTrends;

    public List<Trend> getNowTrends() {
        return nowTrends;
    }

    public void setNowTrends(List<Trend> nowTrends) {
        this.nowTrends = nowTrends;
    }

    public void pareJSON(JSONObject json) {
        Iterator<String> keys = json.keys();
        //因为只有一个对象，所以不必做更深入的迭代
        String key = keys.next();
        try {
            nowTrends = new ArrayList<>();
            JSONArray jsonArray = json.getJSONArray(key);
            for (int i = 0; i < jsonArray.length(); i++) {
                Trend trend = new Trend();
                trend.pareJSON((JSONObject) jsonArray.get(i));
                nowTrends.add(trend);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
