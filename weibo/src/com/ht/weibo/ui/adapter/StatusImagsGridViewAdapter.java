package com.ht.weibo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.ht.weibo.R;
import com.ht.weibo.thread.CacheImageAsyncTask;

import java.util.List;

public class StatusImagsGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<String> urls;
    private LayoutInflater inflater;

    public StatusImagsGridViewAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        int ret = 0;
        if (urls != null) {
            ret = urls.size();
        }
        return ret;
    }

    @Override
    public Object getItem(int position) {
        Object obj = null;
        if (urls != null) {
            obj = urls.get(position);
        }
        return obj;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;
        if (context != null) {
            inflater = LayoutInflater.from(context);
        }

        if (convertView != null) {
            ret = convertView;
        } else {
            ret = inflater.inflate(R.layout.item_image_status_imgs, parent, false);
        }

        ViewHolder viewHolder = (ViewHolder) ret.getTag();
        if (viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.img = (ImageView) ret.findViewById(R.id.item_image_status_img);
            ret.setTag(viewHolder);
        }

        viewHolder.img.setImageResource(R.drawable.defaultimg);

        String url = urls.get(position);

        if (url != null) {
            //设置图片
            //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
            //通过tag传递给task，进行检查
            viewHolder.img.setTag(url);
            //todo 加载图片，并设置到imageview
            CacheImageAsyncTask task = new CacheImageAsyncTask(viewHolder.img);
            task.execute(url);
        }

        return ret;
    }

    private static class ViewHolder {
        ImageView img;
    }
}
