package com.ht.jellybean.ui.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/4.
 */

/**
 * 这是新浪微博的高级接口，需要申请statuses/timeline_batch
 */
public class MyAllWeiBoActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener {
    private int currentPage;
    private Oauth2AccessToken token;
    private Long uid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myallweibo);

        PullToRefreshListView pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.allweibo_listview);
        //上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);


        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);

        uid = Long.parseLong(token.getUid());

        //得到自己发布的所有的微博的id

        StatusesAPI mstatusesAPI = new StatusesAPI(this, Constants.APP_KEY, token);
        mstatusesAPI.userTimelineIds(uid, 0, 0, 10, 1, false, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    ArrayList<String> sIds = new ArrayList();
                }

            }

            @Override
            public void onWeiboException(WeiboException e) {
                Log.d("取所有微博", e.getMessage());
            }
        });


    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

        //实现上拉刷新的功能
        //得到自己发布的微博的条目
        currentPage = currentPage - 1;
        Log.d("cuurent", currentPage + "");

    }
}