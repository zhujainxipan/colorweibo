package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.ui.adapter.FriFolListAdapter;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.ht.jellybean.model.weibo.FriFol;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.FriendshipsAPI;
import com.sina.weibo.sdk.openapi.models.User;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.HashSet;
import java.util.List;

/**
 * Created by annuo on 2015/6/7.
 */
public class FriendsActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable {
    private int currentCursor = 0;
    private HashSet<String> cursorSet;
    private Oauth2AccessToken token;
    private Long uid;
    private PullToRefreshListView pullToRefreshListView;
    private List<User> users;
    private FriFolListAdapter friFolListAdapter;
    private FriendshipsAPI friendshipsAPI;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();


        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        pullToRefreshListView = (PullToRefreshListView)findViewById(R.id.friends_listview);

        View myHead = LayoutInflater.from(this).inflate(R.layout.friends_head, null);
        pullToRefreshListView.getRefreshableView().addHeaderView(myHead, null, false);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);
        uid = Long.parseLong(token.getUid());

        //得到该用户的朋友列表
        friendshipsAPI = new FriendshipsAPI(this, Constants.APP_KEY, token);
        friendshipsAPI.friends(uid, 50, currentCursor, true, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    FriFol friFol = new FriFol();
                    friFol.pareJSON(response);
                    Log.d("friends", response);
                    Log.d("friendscuroser", friFol.getNext_cursor() + "");
                    if (friFol != null) {
                        users = friFol.getUsers();
                        if (users != null) {
                                friFolListAdapter = new FriFolListAdapter(FriendsActivity.this, users);
                                pullToRefreshListView.setAdapter(friFolListAdapter);
                                currentCursor = friFol.getNext_cursor();
                            cursorSet = new HashSet<String>();
                            cursorSet.add(currentCursor + "");
                            } else
                                Toast.makeText(FriendsActivity.this, "没有更多的数据了", Toast.LENGTH_LONG).show();

                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });


    }

    /**
     * 下拉刷新的方法
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //根据curentid来书写下拉刷新的方法


    }

    /**
     * 上拉刷新的方法
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        friendshipsAPI.friends(uid, 50, currentCursor, true, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    FriFol friFol = new FriFol();
                    friFol.pareJSON(response);
                    Log.d("friendsre", response);
                    Log.d("friendscuroserre", friFol.getNext_cursor() + "");
                    if (friFol != null) {
                        List<User> users1 = friFol.getUsers();
                        if (users1 != null) {
                            if (!cursorSet.contains(friFol.getNext_cursor() + "")) {
                                users.addAll(users1);
                                friFolListAdapter.notifyDataSetChanged();
                                currentCursor = friFol.getNext_cursor();
                                cursorSet.add(currentCursor + "");
                            } else
                                Toast.makeText(FriendsActivity.this, "没有更多的数据了", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(FriendsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onContextItemSelected(item);
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



}

