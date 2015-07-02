package com.ht.jellybean.model.weibo;

import com.sina.weibo.sdk.openapi.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by annuo on 2015/6/7.
 */
public class FriFol implements Serializable {
    private List<User> users;
    private int next_cursor;
    private int previous_cursor;
    private int total_number;


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public int getNext_cursor() {
        return next_cursor;
    }

    public void setNext_cursor(int next_cursor) {
        this.next_cursor = next_cursor;
    }

    public int getPrevious_cursor() {
        return previous_cursor;
    }

    public void setPrevious_cursor(int previous_cursor) {
        this.previous_cursor = previous_cursor;
    }

    public int getTotal_number() {
        return total_number;
    }

    public void setTotal_number(int total_number) {
        this.total_number = total_number;
    }

    public void pareJSON(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            next_cursor = jsonObject.optInt("next_cursor");
            previous_cursor = jsonObject.optInt("previous_cursor");
            total_number = jsonObject.getInt("total_number");
            JSONArray jsonArray = jsonObject.getJSONArray("users");
            users = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                User user = User.parse((JSONObject) jsonArray.get(i));
                users.add(user);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
