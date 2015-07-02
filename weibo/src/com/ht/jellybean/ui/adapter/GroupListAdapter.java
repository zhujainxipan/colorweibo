package com.ht.jellybean.ui.adapter;

import android.widget.BaseAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ht.jellybean.R;
import com.sina.weibo.sdk.openapi.models.Group;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.adapters
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */
public class GroupListAdapter extends BaseAdapter {
    private Context context;
    private List<Group> list;

    public GroupListAdapter(Context context, List<Group> list) {
        this.context = context;
        this.list = list;
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
            view = LayoutInflater.from(context).inflate(R.layout.group_item, null);
            viewHolder = new ViewHolder();
            viewHolder.groupTitle = (TextView) view.findViewById(R.id.group_item_title);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        Group group = list.get(i);
        if (group != null) {
            viewHolder.groupTitle.setText(group.name);
        }
        return view;
    }

    private class ViewHolder {
        private TextView groupTitle;
    }
}
