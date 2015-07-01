package com.ht.weibo.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.ui.activity.StatusActivity;
import com.ht.weibo.ui.adapter.StatusContentListAdapter;
import com.ht.weibo.ui.activity.FollowersActivity;
import com.ht.weibo.ui.activity.FriendsActivity;
import com.ht.weibo.ui.activity.MyAllWeiBoActivity;
import com.ht.weibo.ui.widget.RoundProgressBar;
import com.ht.weibo.util.AccessTokenKeeper;
import com.ht.weibo.thread.CacheImageAsyncTask;
import com.ht.weibo.util.UserInfoKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/3.
 */
public class MyFragment extends Fragment implements Runnable{

    private long uid;
    private Oauth2AccessToken token;
    private ListView listView;
    private ArrayList<Status> list;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    //记录当前的ListView的位置
    private int CURRENT_LISTVIEW_ITEM_POSITION = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        roundProgressBar = (RoundProgressBar) getView().findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);
        Thread thread = new Thread(this);
        thread.start();
        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());
        uid = Long.parseLong(token.getUid());

        View myHead = LayoutInflater.from(getActivity()).inflate(R.layout.user_info_head, null);
        listView = (ListView) getView().findViewById(R.id.my_listview);
        listView.addHeaderView(myHead, null, false);

        //点击微博数跳转所有的微博列表
//        TextView weiboCountTextView = (TextView) myHead.findViewById(R.id.user_info_head_weibo_count);
//        weiboCountTextView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getActivity(), MyAllWeiBoActivity.class);
//                startActivity(intent);
//            }
//        });


        //点击关注数跳转所有关注
        TextView friendsCountTextView = (TextView) myHead.findViewById(R.id.user_info_head_guanzhu_count);
        friendsCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
            }
        });

        //点击关注数跳转所有粉丝列表
        TextView fellowersCountTextView = (TextView) myHead.findViewById(R.id.user_info_head_fensi_count);
        fellowersCountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FollowersActivity.class);
                startActivity(intent);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                Intent intent = new Intent(getActivity(), StatusActivity.class);
                Log.d("111111111111111111111", "11111111111111");
                //准备传递给StatusActivity的数据
                Status status = list.get(position);
                if (status != null) {
                    intent.putExtra("status", status);
                }
                getActivity().startActivity(intent);
            }
        });


        //使用usersapi获得用户的信息
        UsersAPI muserAPI = new UsersAPI(getActivity(), Constants.APP_KEY, token);


        //显示用户的个人信息
        User user = UserInfoKeeper.readUserInfo(getActivity());
        if (user != null) {
            //显示用户的昵称
            TextView screenName = (TextView) getActivity().findViewById(R.id.user_info_head_name);
            screenName.setText(user.screen_name);
            //显示用户的性别
            TextView sexTextView = (TextView) getActivity().findViewById(R.id.user_info_head_sex);
            String sex = null;
            switch (user.gender) {
                case "m":
                    sex = "男";
                    break;
                case "f":
                    sex = "女";
                    break;
                case "n":
                    sex = "未知";
                    break;
            }
            sexTextView.setText(sex);
            //显示用户所在的城市
            TextView addressTextView = (TextView) getActivity().findViewById(R.id.user_info_head_adress);
            addressTextView.setText(user.location);
            //显示用户所发表的微博数量
            TextView weiboCountTextView = (TextView) getActivity().findViewById(R.id.user_info_head_weibo_count);
            weiboCountTextView.setText(user.statuses_count + "\n微博");
            //显示用户的粉丝数量
            TextView fensiCountTextView = (TextView) getActivity().findViewById(R.id.user_info_head_fensi_count);
            fensiCountTextView.setText(user.followers_count + "\n粉丝");
            //显示用户的关注数量
            TextView guanzhuCountTextView = (TextView) getActivity().findViewById(R.id.user_info_head_guanzhu_count);
            guanzhuCountTextView.setText(user.friends_count + "\n关注");
            //显示用户的头像
            ImageView imageView = (ImageView) getActivity().findViewById(R.id.user_info_head_avatar);
            imageView.setTag(user.avatar_large);
            CacheImageAsyncTask myTask = new CacheImageAsyncTask(imageView, "userico");
            myTask.execute(user.avatar_large);
        }

        //得到自己发布的微博的最新的微博的条目，第三方应用只返回5条最近的微博
        StatusesAPI mstatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, token);
        mstatusesAPI.userTimeline(uid, 0, 0, 5, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                isRoundProgressBarShown = false;
                roundProgressBar.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(response)) {
                    StatusList statusList = StatusList.parse(response);
                    if (statusList != null) {
                        list = statusList.statusList;
                        if (list != null) {
                            StatusContentListAdapter statusContentListAdapter = new StatusContentListAdapter(getActivity(), list);
                            listView.setAdapter(statusContentListAdapter);
                        } else
                            Toast.makeText(getActivity(), "没有更多微博了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void run() {
        boolean running = true;
        int i=1;
        while(running){
            i+=5;
            if(i > 100){
                i=1;
            }
            roundProgressBar.setProgress(i);
            if(!isRoundProgressBarShown){
                running = isRoundProgressBarShown;
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CURRENT_LISTVIEW_ITEM_POSITION = listView.getFirstVisiblePosition();//得到当前ListView的第一个
        Log.d("onPause记住当前位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");
    }


    @Override
    public void onResume() {
        super.onResume();

        //如果是从其他页面返回的话
        //滚动到listivew的上一次的位置
        listView.setSelection(CURRENT_LISTVIEW_ITEM_POSITION);//回到原来的位置
        Log.d("滚动到原来的位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");

    }
}