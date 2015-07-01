package com.ht.weibo.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.openapi.models.User;

/**
 * Created by annuo on 2015/6/5.
 */
public class UserInfoKeeper {

    /**
     *
     *
     * @param context 应用程序上下文环境
     */
    public static void writeUserInfo(Context context, User user) {
        if (null == context || null == user) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("weibo_user_info", Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("loction", user.location);
        editor.putString("id", user.id);
        editor.putString("screenname", user.screen_name);
        editor.putString("gender", user.gender);
        editor.putInt("statusescount", user.statuses_count);
        editor.putInt("followerscount", user.followers_count);
        editor.putInt("friendscount", user.friends_count);
        editor.putString("avatarlarge", user.avatar_large);
        editor.commit();
    }

    /**
     *
     *
     * @param context 应用程序上下文环境
     *
     */
    public static User readUserInfo(Context context) {
        if (null == context) {
            return null;
        }

        User user = new User();
        SharedPreferences pref = context.getSharedPreferences("weibo_user_info", Context.MODE_APPEND);
        user.setId(pref.getString("id", ""));
        user.setLocation(pref.getString("loction", ""));
        user.setScreen_name(pref.getString("screenname", ""));
        user.setGender(pref.getString("gender", ""));
        user.setStatuses_count(pref.getInt("statusescount", 0));
        user.setFollowers_count(pref.getInt("followerscount", 0));
        user.setFriends_count(pref.getInt("friendscount", 0));
        user.setAvatar_large(pref.getString("avatarlarge", ""));
        return user;
    }

    /**
     * 清空 SharedPreferences 中 Token信息。
     *
     * @param context 应用程序上下文环境
     */
    public static void clear(Context context) {
        if (null == context) {
            return;
        }

        SharedPreferences pref = context.getSharedPreferences("weibo_user_info", Context.MODE_APPEND);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
