package com.ht.jellybean.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.ht.jellybean.R;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.ht.jellybean.util.FormateTimeUtil;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 * Created by IntelliJ IDEA
 * Project: com.ht.mynote.adapters
 * Author: 安诺爱成长
 * Email: 1399487511@qq.com
 * Date: 2015/5/2
 */
public class MsgCommentListAdapter extends BaseAdapter {
    private Context context;
    private List<Comment> list;

    public MsgCommentListAdapter(Context context, List<Comment> list) {
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
            view = LayoutInflater.from(context).inflate(R.layout.item_msgcomment, null);
            viewHolder = new ViewHolder();
            viewHolder.icon = (ImageView) view.findViewById(R.id.item_msgcomment_icon);
            viewHolder.name = (TextView) view.findViewById(R.id.item_msgcomment_name);
            viewHolder.time = (TextView) view.findViewById(R.id.item_msgcomment_time);
            viewHolder.commentContent = (TextView) view.findViewById(R.id.item_msgcomment_commentcontent);
            viewHolder.content = (TextView) view.findViewById(R.id.item_msgcomment_content);
            viewHolder.reply = (ImageView) view.findViewById(R.id.item_msgcomment_send_reply);
            viewHolder.reply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "此功能正在开发中...", Toast.LENGTH_SHORT).show();
                }
            });
            view.setTag(viewHolder);
        } else
            viewHolder = (ViewHolder) view.getTag();

        //在用户信息设置之前，清楚复用倒置的信息错乱
        viewHolder.icon.setImageResource(R.drawable.ht);



        Comment comment = list.get(i);
        if (comment != null) {
            //viewHolder.groupTitle.setText(group.name);
            User user = comment.user;
            if (user != null) {
                viewHolder.name.setText(user.screen_name);
                viewHolder.commentContent.setText(comment.text);

                //对时间进行格式化的处理
                String[] strings = FormateTimeUtil.getFormatTime(comment.created_at);
                viewHolder.time.setText(strings[1] + strings[2] + "日 " + strings[3]);

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


                Status status = comment.status;
                if (status != null) {
                    viewHolder.content.setText("@" + status.user.screen_name + " : " + status.text);
                }

            }
        }
        return view;
    }

    private class ViewHolder {
        private ImageView icon;
        private TextView name;
        private TextView time;
        private TextView commentContent;
        private TextView content;
        private ImageView reply;
    }
}
