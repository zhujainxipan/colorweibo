package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.ui.adapter.StatusContentListAdapter;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.StatusList;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/6.
 */
public class WBSquareActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable {
    private int currentPage = 1;
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private ArrayList<Status> statuses;
    private StatusContentListAdapter statusContentListAdapter;
    private StatusesAPI statusesAPI;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;
    //记录当前的ListView的位置
    private int CURRENT_LISTVIEW_ITEM_POSITION = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wbsquare);

        roundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();


        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        View myHead = LayoutInflater.from(this).inflate(R.layout.wbsquare_head, null);
        pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.wbsquare_listview);
        pullToRefreshListView.getRefreshableView().addHeaderView(myHead, null, false);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);
        //获取精选的微博列表
        statusesAPI = new StatusesAPI(this, Constants.APP_KEY, token);
        statusesAPI.publicTimeline(10, currentPage, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    StatusList statusList = StatusList.parse(response);
                    if (statusList != null) {
                        statuses = statusList.statusList;
                        if (statuses != null) {
                            statusContentListAdapter = new StatusContentListAdapter(WBSquareActivity.this, statuses);
                            pullToRefreshListView.setAdapter(statusContentListAdapter);

                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });

        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int position1 = position - pullToRefreshListView.getRefreshableView().getHeaderViewsCount();
                Intent intent = new Intent(WBSquareActivity.this, StatusActivity.class);
                Log.d("111111111111111111111", "11111111111111");
                //准备传递给StatusActivity的数据
                if (statuses != null) {
                    Status status = statuses.get(position1);
                    if (status != null) {
                        intent.putExtra("status", status);
                    }
                    startActivity(intent);
                }
            }
        });


    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        statusesAPI.publicTimeline(10, currentPage, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    StatusList statusList1 = StatusList.parse(response);
                    if (statusList1 != null) {
                        ArrayList<Status> statuses1 = statusList1.statusList;
                        if (statuses1 != null) {
                            statuses.addAll(statusList1.statusList);
                            statusContentListAdapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(WBSquareActivity.this, "没有更多数据了", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

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
    public void onPause() {
        super.onPause();
        CURRENT_LISTVIEW_ITEM_POSITION = pullToRefreshListView.getRefreshableView().getFirstVisiblePosition();//得到当前ListView的第一个
        Log.d("onPause记住当前位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");
    }


    @Override
    public void onResume() {
        super.onResume();

        //如果是从其他页面返回的话
        //滚动到listivew的上一次的位置
        //listView.setAdapter(statusContentListAdapter);
        pullToRefreshListView.getRefreshableView().setSelection(CURRENT_LISTVIEW_ITEM_POSITION);//回到原来的位置
        Log.d("滚动到原来的位置：", CURRENT_LISTVIEW_ITEM_POSITION + "");

    }

}