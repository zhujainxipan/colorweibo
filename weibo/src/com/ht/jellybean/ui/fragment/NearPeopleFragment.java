package com.ht.jellybean.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.model.weibo.FriFol;
import com.ht.jellybean.ui.activity.MainActivity;
import com.ht.jellybean.ui.adapter.FriFolListAdapter;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.PlaceAPI;
import com.sina.weibo.sdk.openapi.models.User;

import java.util.List;

/**
 * Created by annuo on 2015/6/30.
 */
public class NearPeopleFragment extends Fragment implements Runnable, PullToRefreshBase.OnRefreshListener2<ListView> {
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private List<User> users;
    private FriFolListAdapter friFolListAdapter;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    private PlaceAPI placeAPI;
    private int currentPage;
    private long endtime;
    private long starttime;
    private double latitude;
    private double lontitude;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nearpeople, container, false);

        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();

        //得到mainactivity中的经纬度
        MainActivity mainActivity = (MainActivity) getActivity();
        latitude = mainActivity.getLatitude();
        lontitude = mainActivity.getLontitude();


        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.nearpeople_listview);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());


        //得到周围发布
        placeAPI = new PlaceAPI(getActivity(), Constants.APP_KEY, token);

        //得到结束时间，当前系统时间
        endtime = System.currentTimeMillis();
        //得到开始时间(取的是最近两天的周边微博)
        long chazhi = 2 * 24 * 60 * 60 * 1000;
        starttime = endtime - chazhi;

        placeAPI.nearbyUsers(String.valueOf(latitude), String.valueOf(lontitude), 2000, endtime, starttime, 0, 10, 1, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    FriFol friFol = new FriFol();
                    friFol.pareJSON(response);
                    if (friFol != null) {
                        users = friFol.getUsers();
                        if (users != null) {
                            friFolListAdapter = new FriFolListAdapter(getActivity(), users);
                            pullToRefreshListView.setAdapter(friFolListAdapter);
                        } else
                            Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

        return view;
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
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        placeAPI.nearbyUsers(String.valueOf(latitude), String.valueOf(lontitude), 2000, endtime, starttime, 0, 10, currentPage, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    FriFol friFol = new FriFol();
                    friFol.pareJSON(response);
                    if (friFol != null) {
                        List<User> users1 = friFol.getUsers();
                        if (users1 != null) {
                                users.addAll(users1);
                                friFolListAdapter.notifyDataSetChanged();
                            } else
                                Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_LONG).show();
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