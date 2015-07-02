package com.ht.jellybean.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ht.jellybean.R;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.ht.jellybean.ui.widget.MyGridView;
import com.ht.jellybean.util.FormateTimeUtil;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annuo on 2015/5/26.
 */

/**
 *
 */
public class StatusContentListAdapter extends BaseAdapter {
    private Context context;
    private List<Status> statusList;
    private LayoutInflater inflater;
    int[] colors = {0x88512DA7, 0x88FF5723, 0x88D81A60, 0x885D4037, 0x88C27270};


    public StatusContentListAdapter(Context context, List<Status> statusList) {
        this.context = context;
        this.statusList = statusList;
        if (context != null) {
            inflater = LayoutInflater.from(context);
        }

    }

    @Override
    public int getCount() {
        int ret = 0;
        if (statusList != null) {
            ret = statusList.size();
        }
        return ret;
    }

    @Override
    public int getViewTypeCount() {
        //无图微博、有图微博、转发的微博
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        //根据当前的数据，判断类型，并且返回
        Status status = statusList.get(position);
        int ret = 0;
        //下面的判断要和这里写的一致
        if (status.pic_urls != null) {
            ret = 1;//说明是图片微博
        } else if (status.retweeted_status != null) {
            ret = 2;//说明是转发的微博
        } else
            ret = 0;//说明就是文本微博
        return ret;
    }

    @Override
    public Object getItem(int i) {
        Object ret = null;
        if (statusList != null) {
            ret = statusList.get(i);
        }
        return ret;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * @param position
     * @param convertView
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        //因为adapter重写了getViewTypeCount以及getItemViewCount
        //并且getViewTypeCount返回值大于1，代表需要创建多布局内容
        int index = getItemViewType(position);
        switch (index) {
            case 1:
                //应该显示图片
                convertView = bindImage(position, convertView);
                break;
            case 0:
                //应该显示文本的内容
                convertView = bindText(position, convertView);
                break;
            case 2:
                //应该显示转发微博的内容
                //1、视图的复用，convertView转化
                if (convertView == null) {
                    convertView = LayoutInflater.from(context)
                            .inflate(R.layout.item_zhuanfa_status, viewGroup, false);
                }

                //2、处理viewholder减少findviewbyid
                ZhuanFaViewHolder zhuanFaViewHolder = (ZhuanFaViewHolder) convertView.getTag();
                //初始化viewholder
                if (zhuanFaViewHolder == null) {
                    zhuanFaViewHolder = new ZhuanFaViewHolder();
                    zhuanFaViewHolder.txtcommentCount = (TextView) convertView.findViewById(R.id.item_status_comment_count);
                    zhuanFaViewHolder.txtForwordCount = (TextView) convertView.findViewById(R.id.item_status_forword_count);
                    zhuanFaViewHolder.txtContent = (TextView) convertView.findViewById(R.id.item_status_content);
                    zhuanFaViewHolder.txtName = (TextView) convertView.findViewById(R.id.item_status_name);
                    zhuanFaViewHolder.imgUserIcon = (ImageView) convertView.findViewById(R.id.item_status_user_icon);
                    zhuanFaViewHolder.txtTime = (TextView) convertView.findViewById(R.id.item_status_time);
                    zhuanFaViewHolder.txtLikeCount = (TextView) convertView.findViewById(R.id.item_status_like_count);
                    zhuanFaViewHolder.item = (LinearLayout) convertView.findViewById(R.id.item_status_bg);
                    zhuanFaViewHolder.zhanFatxtContent = (TextView) convertView.findViewById(R.id.item_zhuanfa_status_content);
                    //zhuanFaViewHolder.zhanFatxtName = (TextView) convertView.findViewById(R.id.item_zhuanfa_status_name);
                    zhuanFaViewHolder.zhanFamyGridView = (MyGridView) convertView.findViewById(R.id.item_zhuanfa_status_imgs);
                    convertView.setTag(zhuanFaViewHolder);
                }

                //3、显示内容
                //在用户信息设置之前，清楚复用倒置的信息错乱
                zhuanFaViewHolder.imgUserIcon.setImageResource(R.drawable.ht);

                //设置item的背景颜色,根据position来随机选择颜色
                int colorChoose = position % colors.length;
                zhuanFaViewHolder.item.setBackgroundColor(colors[colorChoose]);

                Status status = statusList.get(position);
                //记住选取的颜色
                status.bgColor = colors[colorChoose];
                if (status != null) {
                    User user = status.user;
                    if (user != null) {
                        zhuanFaViewHolder.txtName.setText(user.screen_name);
                        zhuanFaViewHolder.txtContent.setText(status.text);
                        zhuanFaViewHolder.txtForwordCount.setText(status.reposts_count + "");
                        zhuanFaViewHolder.txtcommentCount.setText(status.comments_count + "");

                        String[] strings = FormateTimeUtil.getFormatTime(status.created_at);
                        zhuanFaViewHolder.txtTime.setText(strings[1] + strings[2] + "日 " + strings[3]);

                        zhuanFaViewHolder.txtLikeCount.setText(status.attitudes_count + "");
                        //设置用户的头像，需要开启异步任务
                        String avataUrl = user.avatar_large;
                        //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
                        //通过tag传递给task，进行检查
                        zhuanFaViewHolder.imgUserIcon.setTag(avataUrl);
                        if (avataUrl != null) {
                            //todo 加载图片，并设置到imageview
                            CacheImageAsyncTask task = new CacheImageAsyncTask(zhuanFaViewHolder.imgUserIcon, "userico");
                            task.execute(avataUrl);
                        }
                    }

                    //显示转发的微博信息内容
                    //显示转发微博的原作者
                    Status retweetedStatus = status.retweeted_status;
                    if (retweetedStatus != null) {
                        User retweetedUser = retweetedStatus.user;
                        if (retweetedUser != null) {
                            String name = retweetedUser.name;
                            String text = retweetedStatus.text;
                            if (text != null) {
                                //显示转发微博的内容
                                zhuanFaViewHolder.zhanFatxtContent.setText("@" + name + ":" + text);
                            }
                            ArrayList<String> picUrls = retweetedStatus.pic_urls;
                            if (picUrls != null) {
                                //显示转发微博的图片信息
                                StatusImagsGridViewAdapter imagsGridViewAdapter = new StatusImagsGridViewAdapter(context, picUrls);
                                zhuanFaViewHolder.zhanFamyGridView.setAdapter(imagsGridViewAdapter);
                            }
                        }
                    }
                }
                break;
        }

        return convertView;
    }


    private View bindImage(int position, View convertView) {
        ImageViewHolder imageViewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_image_status, null);
            imageViewHolder = new ImageViewHolder();
            imageViewHolder.txtcommentCount = (TextView) convertView.findViewById(R.id.item_status_comment_count);
            imageViewHolder.txtForwordCount = (TextView) convertView.findViewById(R.id.item_status_forword_count);
            imageViewHolder.txtContent = (TextView) convertView.findViewById(R.id.item_status_content);
            imageViewHolder.txtName = (TextView) convertView.findViewById(R.id.item_status_name);
            imageViewHolder.imgUserIcon = (ImageView) convertView.findViewById(R.id.item_status_user_icon);
            imageViewHolder.myGridView = (MyGridView) convertView.findViewById(R.id.item_image_status_imgs);
            imageViewHolder.txtTime = (TextView) convertView.findViewById(R.id.item_status_time);
            imageViewHolder.txtLikeCount = (TextView) convertView.findViewById(R.id.item_status_like_count);
            imageViewHolder.item = (LinearLayout) convertView.findViewById(R.id.item_status_bg);
            convertView.setTag(imageViewHolder);
        } else {
            imageViewHolder = (ImageViewHolder) convertView.getTag();
        }
        //在用户信息设置之前，清楚复用倒置的信息错乱
        imageViewHolder.imgUserIcon.setImageResource(R.drawable.ht);

        //设置item的背景颜色,根据position来随机选择颜色
        int colorChoose = position % colors.length;
        imageViewHolder.item.setBackgroundColor(colors[colorChoose]);

        Status status = statusList.get(position);
        //记住选取的颜色
        status.bgColor = colors[colorChoose];

        if (status != null) {
            User user = status.user;
            if (user != null) {
                imageViewHolder.txtName.setText(user.screen_name);
                imageViewHolder.txtContent.setText(status.text);
                imageViewHolder.txtForwordCount.setText(status.reposts_count + "");
                imageViewHolder.txtcommentCount.setText(status.comments_count + "");

                //gradleview
                ArrayList<String> picUrls = status.pic_urls;
                Log.d("111111111111", picUrls.get(0));
                StatusImagsGridViewAdapter imagsGridViewAdapter = new StatusImagsGridViewAdapter(context, picUrls);
                imageViewHolder.myGridView.setAdapter(imagsGridViewAdapter);


                //对时间进行格式化的处理
                String[] strings = FormateTimeUtil.getFormatTime(status.created_at);
                imageViewHolder.txtTime.setText(strings[1] + strings[2] + "日 " + strings[3]);
                imageViewHolder.txtLikeCount.setText(status.attitudes_count + "");
                //设置用户的头像，需要开启异步任务
                String avataUrl = user.avatar_large;
                //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
                //通过tag传递给task，进行检查
                imageViewHolder.imgUserIcon.setTag(avataUrl);
                if (avataUrl != null) {
                    //todo 加载图片，并设置到imageview
                    CacheImageAsyncTask task = new CacheImageAsyncTask(imageViewHolder.imgUserIcon, "userico");
                    task.execute(avataUrl);
                }

            }
        }

        return convertView;
    }


    /**
     * 用来加载和显示文本段子
     *
     * @param position
     * @param convertView
     * @return
     */
    private View bindText(int position, View convertView) {
        TextViewHolder textViewHolder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_text_status, null);
            textViewHolder = new TextViewHolder();
            textViewHolder.txtcommentCount = (TextView) convertView.findViewById(R.id.item_status_comment_count);
            textViewHolder.txtForwordCount = (TextView) convertView.findViewById(R.id.item_status_forword_count);
            textViewHolder.txtContent = (TextView) convertView.findViewById(R.id.item_status_content);
            textViewHolder.txtName = (TextView) convertView.findViewById(R.id.item_status_name);
            textViewHolder.imgUserIcon = (ImageView) convertView.findViewById(R.id.item_status_user_icon);
            textViewHolder.txtTime = (TextView) convertView.findViewById(R.id.item_status_time);
            textViewHolder.txtLikeCount = (TextView) convertView.findViewById(R.id.item_status_like_count);
            textViewHolder.item = (LinearLayout) convertView.findViewById(R.id.item_status_bg);
            convertView.setTag(textViewHolder);
        } else {
            textViewHolder = (TextViewHolder) convertView.getTag();
        }

        //在用户信息设置之前，清楚复用倒置的信息错乱
        textViewHolder.imgUserIcon.setImageResource(R.drawable.ht);

        //设置item的背景颜色,根据position来随机选择颜色
        int colorChoose = position % colors.length;
        textViewHolder.item.setBackgroundColor(colors[colorChoose]);

        Status status = statusList.get(position);
        //记住选取的颜色
        status.bgColor = colors[colorChoose];

        if (status != null) {
            User user = status.user;
            if (user != null) {
                textViewHolder.txtName.setText(user.screen_name);
                textViewHolder.txtContent.setText(status.text);
                textViewHolder.txtForwordCount.setText(status.reposts_count + "");
                textViewHolder.txtcommentCount.setText(status.comments_count + "");

                String[] strings = FormateTimeUtil.getFormatTime(status.created_at);
                textViewHolder.txtTime.setText(strings[1] + strings[2] + "日 " + strings[3]);

                textViewHolder.txtLikeCount.setText(status.attitudes_count + "");
                //设置用户的头像，需要开启异步任务
                String avataUrl = user.avatar_large;
                //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
                //通过tag传递给task，进行检查
                textViewHolder.imgUserIcon.setTag(avataUrl);
                if (avataUrl != null) {
                    //todo 加载图片，并设置到imageview
                    CacheImageAsyncTask task = new CacheImageAsyncTask(textViewHolder.imgUserIcon, "userico");
                    task.execute(avataUrl);
                }

            }
        }

        return convertView;
    }


    private class TextViewHolder {
        public TextView txtName;
        public TextView txtContent;
        public TextView txtForwordCount;
        public TextView txtcommentCount;
        public TextView txtLikeCount;
        //用户头像
        public ImageView imgUserIcon;
        //微博发表的时间
        public TextView txtTime;
        //段子的整个Linearlayout
        public LinearLayout item;

    }

    /**
     * 图片的holder
     */
    private class ImageViewHolder extends TextViewHolder {
        public MyGridView myGridView;
        //段子的整个Reltivelayout
        public RelativeLayout reItem;

    }

    /**
     * 转发微博的holder
     */

    private class ZhuanFaViewHolder extends TextViewHolder {
        //转发的微博的内容
        public TextView zhanFatxtContent;
        //转发微博的图片
        public MyGridView zhanFamyGridView;
        //转发的微博的原作者
        public TextView zhanFatxtName;
    }


}
