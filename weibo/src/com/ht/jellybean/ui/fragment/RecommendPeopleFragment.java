package com.ht.jellybean.ui.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.ui.adapter.RecomListAdapter;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.SuggestionsAPI;
import com.sina.weibo.sdk.openapi.models.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annuo on 2015/6/30.
 */
public class RecommendPeopleFragment extends Fragment implements Runnable, PullToRefreshBase.OnRefreshListener2<ListView> {
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private RecomListAdapter recomListAdapter;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    private int currentPage;
    private SuggestionsAPI suggestionsAPI;
    private List<String> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommendpeople, container, false);

        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.recommendpeople_listview);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());

        //得到系统推荐的用户
        suggestionsAPI = new SuggestionsAPI(getActivity(), Constants.APP_KEY, token);

        suggestionsAPI.mayInterested(10, 1, new RequestListener() {
            @Override
            public void onComplete(String response) {
                Log.d("333333333", response);
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        list = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = (JSONObject) jsonArray.get(i);
                            String uid = object.getString("uid");
                            Log.d("44444444444", uid);
                            list.add(uid);
                        }
                        if (list != null) {
                            Log.d("55555555", list.size() + "");
                            recomListAdapter = new RecomListAdapter(getActivity(), list, token);
                            pullToRefreshListView.setAdapter(recomListAdapter);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
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
        suggestionsAPI.mayInterested(10, currentPage, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    //关闭刷新的操作
                    pullToRefreshListView.onRefreshComplete();
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List list1 = new ArrayList<User>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = (JSONObject) jsonArray.get(i);
                            String uid = object.getString("uid");
                            list1.add(uid);
                        }
                        if (list1 != null) {
                            list.addAll(list1);
                            recomListAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(getActivity(), "没有更多的数据了", Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
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