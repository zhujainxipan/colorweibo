package com.ht.weibo.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.ui.adapter.StatusContentListAdapter;
import com.ht.weibo.ui.widget.RoundProgressBar;
import com.ht.weibo.util.AccessTokenKeeper;
import com.ht.weibo.model.weibo.HotFavList;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.SuggestionsAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/6.
 */
public class HotFavActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable {
    private int currentPage = 1;
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private StatusContentListAdapter statusContentListAdapter;
    private SuggestionsAPI suggestionsAPI;
    private ArrayList<Status> list;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    //记录当前的ListView的位置
    private int CURRENT_LISTVIEW_ITEM_POSITION = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotfav);

        roundProgressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();


        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        View myHead = LayoutInflater.from(this).inflate(R.layout.hotfav_head, null);
        pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.hotfav_listview);
        pullToRefreshListView.getRefreshableView().addHeaderView(myHead, null, false);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);

        //获取用户收藏的微博列表
        suggestionsAPI = new SuggestionsAPI(this, Constants.APP_KEY, token);
        suggestionsAPI.favoritesHot(10, currentPage, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    HotFavList hotFavList = new HotFavList();
                    hotFavList.parseJSON(response);
                    Log.d("zz", hotFavList.getStatuses().get(0).text);
                    if (hotFavList != null) {
                        list = hotFavList.getStatuses();
                        if (list != null) {
                            statusContentListAdapter = new StatusContentListAdapter(HotFavActivity.this, list);
                            pullToRefreshListView.setAdapter(statusContentListAdapter);
                        } else
                            Toast.makeText(HotFavActivity.this, "没有更多的微博了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(HotFavActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - pullToRefreshListView.getRefreshableView().getHeaderViewsCount();
                Intent intent = new Intent(HotFavActivity.this, StatusActivity.class);
                Log.d("111111111111111111111", "11111111111111");
                //准备传递给StatusActivity的数据
                Status status = list.get(position);
                if (status != null) {
                    intent.putExtra("status", status);
                }
                startActivity(intent);
            }
        });


    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        suggestionsAPI.favoritesHot(10, currentPage, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();

                //需要对代码进行重构和判空操作，另外有好提示
                if (!TextUtils.isEmpty(response)) {
                    HotFavList hotFavList = new HotFavList();
                    hotFavList.parseJSON(response);
                    if (hotFavList != null) {
                        ArrayList<Status> statuses = hotFavList.getStatuses();
                        if (statuses != null) {
                            list.addAll(statuses);
                            statusContentListAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(HotFavActivity.this, "没有更多微博了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(HotFavActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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


    @Override
    public void onResume() {
        super.onResume();

        //如果是从其他页面返回的话
        //滚动到listivew的上一次的位置
        pullToRefreshListView.getRefreshableView().setSelection(CURRENT_LISTVIEW_ITEM_POSITION);//回到原来的位置
        Log.d("滚动到原来的位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");

    }

    @Override
    public void onPause() {
        super.onPause();
        CURRENT_LISTVIEW_ITEM_POSITION = pullToRefreshListView.getRefreshableView().getFirstVisiblePosition();//得到当前ListView的第一个
        Log.d("onPause记住当前位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");
    }



}