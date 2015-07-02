package com.ht.jellybean.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.adapters
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */
public class RecomListAdapter extends BaseAdapter {
    private Context context;
    private List<String> list;
    private final UsersAPI usersAPI;
    private User user;

    public RecomListAdapter(Context context, List<String> list, Oauth2AccessToken token) {
        this.context = context;
        this.list = list;
        usersAPI = new UsersAPI(context, Constants.APP_KEY, token);

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_friends, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.item_frfe_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.item_frfe_name);
            viewHolder.loc = (TextView) view.findViewById(R.id.item_frfe_loc);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        //在用户信息设置之前，清楚复用倒置的信息错乱
        viewHolder.icon.setImageResource(R.drawable.ht);
        //清空user中的数据
        user = null;


        String uid = list.get(i);
        usersAPI.show(Long.valueOf(uid), new RequestListener() {
            @Override
            public void onComplete(String s) {
                if (!TextUtils.isEmpty(s)) {
                    user = User.parse(s);
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

        if (user != null) {
            viewHolder.name.setText(user.screen_name);
            viewHolder.loc.setText(user.location);
            //设置用户的头像，需要开启异步任务
            String avataUrl = user.avatar_large;
            //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
            //通过tag传递给task，进行检查
            viewHolder.icon.setTag(avataUrl);
            if (avataUrl != null) {
                //todo 加载图片，并设置到imageview
                CacheImageAsyncTask task = new CacheImageAsyncTask(viewHolder.icon, "userico");
                task.execute(avataUrl);
            }
        }


        return view;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView loc;
    }
}
