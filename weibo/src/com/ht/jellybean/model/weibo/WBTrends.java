package com.ht.jellybean.model.weibo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by annuo on 2015/6/7.
 */
public class WBTrends implements Serializable {
    private int as_of;
    private Trends trends;

    public int getAs_of() {
        return as_of;
    }

    public void setAs_of(int as_of) {
        this.as_of = as_of;
    }

    public Trends getTrends() {
        return trends;
    }

    public void setTrends(Trends trends) {
        this.trends = trends;
    }

    public void parseJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            as_of = jsonObject.getInt("as_of");
            JSONObject jsonObject1 = jsonObject.getJSONObject("trends");
            trends = new Trends();
            trends.pareJSON(jsonObject1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
