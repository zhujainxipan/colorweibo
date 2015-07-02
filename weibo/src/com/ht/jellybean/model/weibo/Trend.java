package com.ht.jellybean.model.weibo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by annuo on 2015/6/7.
 */
public class Trend implements Serializable {
    private String name;
    private String query;
    private String amount;
    private String delta;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDelta() {
        return delta;
    }

    public void setDelta(String delta) {
        this.delta = delta;
    }


    public void pareJSON(JSONObject json) {
        try {
            name = json.getString("name");
            query = json.getString("query");
            amount = json.getString("amount");
            delta = json.getString("delta");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
