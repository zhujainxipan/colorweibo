package com.ht.jellybean.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ht.jellybean.R;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.ht.jellybean.util.FormateTimeUtil;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.adapters
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */
public class CommentListAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> list;

    public CommentListAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (list != null) {
            ret = list.size();
        }
        return ret;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_comment_status, null);
            viewHolder = new ViewHolder();
            viewHolder.iconImg = (ImageView) view.findViewById(R.id.item_commment_icon);
            viewHolder.nameTxt = (TextView) view.findViewById(R.id.item_commment_name);
            viewHolder.timeTxt = (TextView) view.findViewById(R.id.item_commment_time);
            viewHolder.contentTxt = (TextView) view.findViewById(R.id.item_commment_content);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        Comment comment = list.get(i);
        //在用户信息设置之前，清楚复用倒置的信息错乱
        viewHolder.iconImg.setImageResource(R.drawable.ht);
        if (comment != null) {
            User user = comment.user;
            if (user != null) {
                viewHolder.nameTxt.setText(user.name);
                //设置用户的头像，需要开启异步任务
                String avataUrl = user.avatar_large;
                //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
                //通过tag传递给task，进行检查
                viewHolder.iconImg.setTag(avataUrl);
                if (avataUrl != null) {
                    //todo 加载图片，并设置到imageview
                    CacheImageAsyncTask task = new CacheImageAsyncTask(viewHolder.iconImg, "userico");
                    task.execute(avataUrl);
                }
            }
            String[] strings = FormateTimeUtil.getFormatTime(comment.created_at);
            viewHolder.timeTxt.setText(strings[1] + strings[2] + "日 " + strings[3]);
            viewHolder.contentTxt.setText(comment.text);
        }


        return view;
    }

    private class ViewHolder {
        private TextView nameTxt;
        private ImageView iconImg;
        private TextView timeTxt;
        private TextView contentTxt;
    }
}
