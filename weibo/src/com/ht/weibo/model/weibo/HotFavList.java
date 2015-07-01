package com.ht.weibo.model.weibo;

import com.sina.weibo.sdk.openapi.models.Status;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/6.
 */
public class HotFavList implements Serializable {
    private ArrayList<Status> statuses;

    public ArrayList<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(ArrayList<Status> statuses) {
        this.statuses = statuses;
    }

    public void parseJSON(String json) {
        if (json != null) {
            try {
                JSONArray array = new JSONArray(json);
                statuses = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    Status status = Status.parse(jsonObject);
                    statuses.add(status);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
