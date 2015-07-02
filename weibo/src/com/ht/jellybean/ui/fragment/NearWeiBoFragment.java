package com.ht.jellybean.ui.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.ui.activity.MainActivity;
import com.ht.jellybean.ui.activity.StatusActivity;
import com.ht.jellybean.ui.adapter.StatusContentListAdapter;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.PlaceAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/30.
 */
public class NearWeiBoFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable, BDLocationListener {
    private int currentPage = 1;
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private ArrayList<Status> statuses;
    private StatusContentListAdapter statusContentListAdapter;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    //记录当前的ListView的位置
    private int CURRENT_LISTVIEW_ITEM_POSITION = 0;
    private LocationClient mLocationClient;
    private PlaceAPI placeAPI;
    private double latitude;
    private double lontitude;
    private long endtime;
    private long starttime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearweibo, container, false);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());

        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);
        Thread thread = new Thread(this);
        thread.start();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.nearweibo_listview);
        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);
        //定位的方法
        getLoc();

        placeAPI = new PlaceAPI(getActivity(), Constants.APP_KEY, token);


        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int position1 = position - pullToRefreshListView.getRefreshableView().getHeaderViewsCount();
                Intent intent = new Intent(getActivity(), StatusActivity.class);
                Log.d("111111111111111111111", "11111111111111");
                //准备传递给StatusActivity的数据
                Status status = statuses.get(position1);
                if (status != null) {
                    intent.putExtra("status", status);
                }
                startActivity(intent);
            }
        });

        return view;
    }

    private void getLoc() {
        //在这里得到用户的地理位置信息，这样可以传递给和本fragment相关的其他的三个fragment使用
        //定位
        mLocationClient = new LocationClient(getActivity());
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);//设置定位模式
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        option.setScanSpan(60 * 60 * 1000);//设置发起定位请求的间隔时间为5000ms
        option.setIsNeedAddress(true);//返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向

        mLocationClient.setLocOption(option);
        mLocationClient.start();
        if (mLocationClient != null && mLocationClient.isStarted())
            mLocationClient.requestLocation();
        //注册监听函数
        mLocationClient.registerLocationListener(this);
    }


    //获取定位返回的结果
    @Override
    public void onReceiveLocation(BDLocation location) {

        //************************************查找个人的位置并显示***********************
        if (location == null)
            return;
        //定位的纬度、精度
        latitude = location.getLatitude();
        lontitude = location.getLongitude();

        //给activity赋值，这样可以保证在nearpeoplefragment中的到该数据
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setLatitude(latitude);
        mainActivity.setLontitude(lontitude);

        //得到结束时间，当前系统时间
        endtime = System.currentTimeMillis();
        //得到开始时间(取的是最近两天的周边微博)
        long chazhi = 2 * 24 * 60 * 60 * 1000;
        starttime = endtime - chazhi;

        placeAPI.nearbyTimeline(String.valueOf(latitude), String.valueOf(lontitude), 2000, starttime, endtime, 0, 10, 1, false, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //取到值
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    StatusList statusList = StatusList.parse(response);
                    if (statusList != null) {
                        statuses = statusList.statusList;
                        if (statuses != null) {
                            statusContentListAdapter = new StatusContentListAdapter(getActivity(), statuses);
                            pullToRefreshListView.setAdapter(statusContentListAdapter);
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });
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
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        placeAPI.nearbyTimeline(String.valueOf(latitude), String.valueOf(lontitude), 2000, starttime, endtime, 0, 10, currentPage, false, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    StatusList statusList = StatusList.parse(response);
                    if (statusList != null) {
                        ArrayList<Status> statusList1 = statusList.statusList;
                        if (statusList1 != null) {
                            statuses.addAll(statusList1);
                            statusContentListAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        CURRENT_LISTVIEW_ITEM_POSITION = pullToRefreshListView.getRefreshableView().getFirstVisiblePosition();//得到当前ListView的第一个
        Log.d("onPause记住当前位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");
    }



    @Override
    public void onStop() {
        super.onStop();
        mLocationClient.stop();
    }

    @Override
    public void onResume() {
        super.onResume();

        //如果是从其他页面返回的话
        //滚动到listivew的上一次的位置
        pullToRefreshListView.getRefreshableView().setSelection(CURRENT_LISTVIEW_ITEM_POSITION);//回到原来的位置
        Log.d("滚动到原来的位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");

    }
}