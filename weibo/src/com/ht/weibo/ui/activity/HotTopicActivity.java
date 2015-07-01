package com.ht.weibo.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.ui.adapter.HotTopicListAdapter;
import com.ht.weibo.ui.widget.RoundProgressBar;
import com.ht.weibo.util.AccessTokenKeeper;
import com.ht.weibo.model.weibo.Trend;
import com.ht.weibo.model.weibo.Trends;
import com.ht.weibo.model.weibo.WBTrends;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.TrendsAPI;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

import java.util.List;

/**
 * Created by annuo on 2015/6/6.
 */
public class HotTopicActivity extends SwipeBackActivity implements Runnable{
    private Oauth2AccessToken token;
    private ListView listView;
    private HotTopicListAdapter hotTopicListAdapter;
    private List<Trend> nowTrends;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hottopic);

        roundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);
        Thread thread = new Thread(this);
        thread.start();


        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        View myHead = LayoutInflater.from(this).inflate(R.layout.hottopic_head, null);
        listView = (ListView) this.findViewById(R.id.hottopic_listview);
        listView.addHeaderView(myHead, null, false);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);

        //todo 文字数据的缓存
        //判断有没有该url地址对应的json字符串存不存在，存在就使用之

        //得到近一周的热门话题
        TrendsAPI trendsAPI = new TrendsAPI(this, Constants.APP_KEY, token);
        trendsAPI.weekly(true, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    WBTrends wbTrends = new WBTrends();
                    wbTrends.parseJSON(response);
                    if (wbTrends != null) {
                        Trends trends = wbTrends.getTrends();
                        if (trends != null) {
                            nowTrends = trends.getNowTrends();
                            if (nowTrends != null) {
                                hotTopicListAdapter = new HotTopicListAdapter(HotTopicActivity.this, nowTrends);
                                listView.setAdapter(hotTopicListAdapter);
                            } else
                                Toast.makeText(HotTopicActivity.this, "没有更多的热门话题了", Toast.LENGTH_LONG).show();
                        }
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
}