package com.ht.weibo.ui.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.ui.activity.StatusActivity;
import com.ht.weibo.ui.adapter.StatusContentListAdapter;
import com.ht.weibo.thread.WeatherTask;
import com.ht.weibo.ui.widget.RoundProgressBar;
import com.ht.weibo.util.AccessTokenKeeper;
import com.ht.weibo.util.UserInfoKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import com.sina.weibo.sdk.openapi.models.User;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by annuo on 2015/6/3.
 */
public class HomeFragment extends Fragment implements Runnable {
    private int currentPage = 1;
    //记录下当前屏幕最上面的记录的时间
    private long currentId;
    private Oauth2AccessToken token;
    private Long uid;
    private ListView listView;
    private ArrayList<Status> statuses;
    private StatusContentListAdapter statusContentListAdapter;
    private StatusesAPI mstatusesAPI;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isBottom;//是否到底了
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    //记录点击进入详情页的时候的listview的位置
    //记录当前的ListView的位置
    private int CURRENT_LISTVIEW_ITEM_POSITION = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);//
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());
        mstatusesAPI = new StatusesAPI(getActivity(), Constants.APP_KEY, token);

        roundProgressBar = (RoundProgressBar) getView().findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);
        Thread thread = new Thread(this);
        thread.start();


        View myHead = LayoutInflater.from(getActivity()).inflate(R.layout.home_head, null);
        listView = (ListView) getView().findViewById(R.id.home_listview);
        listView.addHeaderView(myHead, null, false);

        //设置显示时间
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年 MM月 dd日");
        String time = sDateFormat.format(System.currentTimeMillis());

        TextView timeTextView = (TextView) myHead.findViewById(R.id.home_head_time);
        timeTextView.setText(time);

        //设置显示的地点



        //读取用户信息
        User user = UserInfoKeeper.readUserInfo(getActivity());
        if (user != null) {
            TextView locTextView = (TextView) myHead.findViewById(R.id.home_head_loc);
            locTextView.setText(user.location);
            String weatherLoc = user.location;
            //设置显示的温度，和天气情况
            if (weatherLoc.contains(" ")) {
                String[] strings = weatherLoc.split(" ");
                weatherLoc = strings[strings.length - 1];
            }
            String path = null;
            try {
                path = "http://api.map.baidu.com/telematics/v3/weather?location=" + URLEncoder.encode(weatherLoc, "UTF-8") + "&output=json&ak=" + Constants.BAIDU_MAP_AK + "&mcode=" + Constants.BAIDU_MAP_CODE;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            TextView tempeTextView = (TextView) myHead.findViewById(R.id.home_head_tempe);
            new WeatherTask(tempeTextView).execute(path);
        }


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - listView.getHeaderViewsCount();
                Intent intent = new Intent(getActivity(), StatusActivity.class);
                //准备传递给StatusActivity的数据
                Status status = statuses.get(position);
                if (status != null) {
                    intent.putExtra("status", status);
                }

                getActivity().startActivity(intent);
            }
        });


        //使用google自带的下拉刷新组件来刷新
        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeLayout);
        //设置刷新时动画的颜色，可以设置4个
        //setColorScheme()已经弃用，使用setColorSchemeResources()来设置颜色。
        //setColorSchemeResources(int… colorResIds):设置进度动画的颜色。
        swipeRefreshLayout.setColorSchemeResources(R.color.swipe_color_1,
                R.color.swipe_color_2,
                R.color.swipe_color_3,
                R.color.swipe_color_4);

        //setSize(int size):设置进度圈的大小，只有两个值：DEFAULT、LARGE
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);

        //setProgressBackgroundColor(int colorRes):设置进度圈的背景色
        swipeRefreshLayout.setProgressBackgroundColor(R.color.swipe_background_color);

        //swipeRefreshLayout.setPadding(20, 20, 20, 20);
        //设置背景圈在屏幕上出现的位置
        swipeRefreshLayout.setProgressViewOffset(true, 40, 100);
        //swipeRefreshLayout.setDistanceToTriggerSync(50);
        //swipeRefreshLayout.setProgressViewEndTarget(true, 100);

        //setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener listener):设置手势滑动监听器
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //根据curentid来书写下拉刷新的方法
                mstatusesAPI.friendsTimeline(currentId, 0, 10, 1, false, 0, false, new RequestListener() {
                    @Override
                    public void onComplete(String response) {
                        //关闭刷新的操作
                        //setRefreshing(Boolean refreshing):设置组件的刷洗状态。用来停止刷新
                        swipeRefreshLayout.setRefreshing(false);
                        if (!TextUtils.isEmpty(response)) {
                            StatusList statusList = StatusList.parse(response);
                            if (statusList != null) {
                                List<Status> statuses3 = statusList.statusList;
                                if (statuses3 != null) {
                                    Status status = statuses3.get(0);
                                    if (status != null) {
                                        currentId = Long.parseLong(status.id);
                                        statuses.addAll(0, statuses3);
                                        statusContentListAdapter.notifyDataSetChanged();
                                    }
                                } else
                                    Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });


        //重写lsitview的onscrollstatechanged方法，来实现上拉加载更多的效果
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * SCROLL_STATE_IDLE:松手状态
             * SCROLL_STATE_TOUCH_SCROLL:用手滑动屏幕的状态，但没有松手
             * SCROLL_STATE_FLING:屏幕的惯性滑动
             * 只要以上三种状态之间发生切换就会触发以下的方法
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                if (isBottom && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    currentPage = currentPage + 1;
                    mstatusesAPI.friendsTimeline(0, 0, 10, currentPage, false, 0, false, new RequestListener() {
                        @Override
                        public void onComplete(String response) {
                            if (!TextUtils.isEmpty(response)) {
                                StatusList statusList = StatusList.parse(response);
                                if (statusList != null) {
                                    List<Status> statuses2 = statusList.statusList;
                                    if (statuses2 != null) {
                                        statuses.addAll(statuses2);
                                        statusContentListAdapter.notifyDataSetChanged();
                                    } else
                                        Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_LONG).show();
                                }
                            }
                        }

                        @Override
                        public void onWeiboException(WeiboException e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            /**
             *
             * @param view listiview
             * @param firstVisibleItem 已经划出屏幕的
             * @param visibleItemCount 可见的，没有划出屏幕的
             * @param totalItemCount 一共多少条数据
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //判断是否滑动到底部，已经划出屏幕的item数加上可见的item数和总的item数相等时
                //说明已经滑到所有数据的底部
                isBottom = (firstVisibleItem + visibleItemCount) == totalItemCount;
            }
        });



        //得到该用户和该用户关注的人发布的最新微博
        Bundle bundle = getArguments();
        String response = bundle.getString("response");
        if (!TextUtils.isEmpty(response)) {

            isRoundProgressBarShown = false;
            roundProgressBar.setVisibility(View.GONE);

            StatusList statusList = StatusList.parse(response);
            if (statusList != null) {
                statuses = statusList.statusList;
                //记录下最顶部的微博的id
                if (statuses != null) {
                    Status status = statuses.get(0);
                    if (status != null) {
                        currentId = Long.parseLong(status.id);
                        statusContentListAdapter = new StatusContentListAdapter(getActivity(), statuses);
                        listView.setAdapter(statusContentListAdapter);
                    }
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //如果是从其他页面返回的话
        //滚动到listivew的上一次的位置
        //listView.setAdapter(statusContentListAdapter);
        listView.setSelection(CURRENT_LISTVIEW_ITEM_POSITION);//回到原来的位置
        Log.d("滚动到原来的位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");

    }


    @Override
    public void run() {
        boolean running = true;
        int i = 1;
        while (running) {
            i += 5;
            if (i > 100) {
                i = 1;
            }
            roundProgressBar.setProgress(i);
            if (!isRoundProgressBarShown) {
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

}