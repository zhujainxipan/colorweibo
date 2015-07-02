package com.ht.jellybean.ui.adapter;

import android.widget.BaseAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ht.jellybean.R;
import com.ht.jellybean.model.weibo.Trend;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.adapters
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */
public class HotTopicListAdapter extends BaseAdapter {
    private Context context;
    private List<Trend> list;
    int[] colors = {0x88512DA7, 0x88FF5723, 0x88D81A60, 0x885D4037, 0x88C27270};


    public HotTopicListAdapter(Context context, List<Trend> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_hottopic, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.hottopic_item_name);
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();
        //设置item的背景颜色,根据position来随机选择颜色
        int colorChoose = i % colors.length;
        viewHolder.name.setBackgroundColor(colors[colorChoose]);

        Trend trend = list.get(i);
        if (trend != null) {
            viewHolder.name.setText("#   "+trend.getName()+"   #");
        }
        return view;
    }

    private class ViewHolder {
        private TextView name;
    }



}
