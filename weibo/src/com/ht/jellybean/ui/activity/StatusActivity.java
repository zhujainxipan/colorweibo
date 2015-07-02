package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.ht.jellybean.ui.adapter.CommentListAdapter;
import com.ht.jellybean.ui.adapter.StatusImagsGridViewAdapter;
import com.ht.jellybean.ui.widget.MyGridView;
import com.ht.jellybean.ui.widget.RoundProgressBar;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.ht.jellybean.util.FormateTimeUtil;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by annuo on 2015/6/23.
 */
public class StatusActivity extends SwipeBackActivity implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable {
    private int currentPage = 1;
    private Oauth2AccessToken token;
    private PullToRefreshListView pullToRefreshListView;
    private ArrayList<Comment> comments;
    private CommentListAdapter adapter;
    private String id;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        roundProgressBar = (RoundProgressBar)findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);

        Thread thread = new Thread(this);
        thread.start();

        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //取到传递过来的数据
        Intent intent = getIntent();
        Status status = (Status) intent.getSerializableExtra("status");

        pullToRefreshListView = (PullToRefreshListView) this.findViewById(R.id.status_listview);
        View myHead = setHeadView(status);

        pullToRefreshListView.getRefreshableView().addHeaderView(myHead, null, false);
        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        pullToRefreshListView.setOnRefreshListener(this);

        pullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position - pullToRefreshListView.getRefreshableView().getHeaderViewsCount();
                Toast.makeText(StatusActivity.this, "对评论进行回复的功能正在开发中.....", Toast.LENGTH_SHORT).show();
            }
        });


        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(this);
        //获取该微博的所有的评论的数据
        CommentsAPI commentsAPI = new CommentsAPI(this, Constants.APP_KEY, token);
        id = status.id;
        commentsAPI.show(Long.parseLong(id), 0, 0, 10, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);

                    Log.d("123", response);
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        comments = commentList.commentList;
                        adapter = new CommentListAdapter(StatusActivity.this, comments);
                        pullToRefreshListView.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(StatusActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private View setHeadView(Status status) {
        View myHead = LayoutInflater.from(this).inflate(R.layout.status_head, null);
        ImageView iconImg = (ImageView) myHead.findViewById(R.id.item_status_user_icon);
        TextView nameTxt = (TextView) myHead.findViewById(R.id.item_status_name);
        TextView timeTxt = (TextView) myHead.findViewById(R.id.item_status_time);
        TextView contentTxt = (TextView) myHead.findViewById(R.id.item_status_content);
        MyGridView imgs = (MyGridView) myHead.findViewById(R.id.item__status_imgs);
        TextView zhuanFaContentTxt = (TextView) myHead.findViewById(R.id.item_zhuanfa_status_content);
        MyGridView zhuanFaImgs = (MyGridView) myHead.findViewById(R.id.item_zhuanfa_status_imgs);
        TextView txtLikeCount = (TextView) myHead.findViewById(R.id.item_status_like_count);
        TextView txtForwordCount = (TextView) myHead.findViewById(R.id.item_status_forword_count);
        TextView txtcommentCount = (TextView) myHead.findViewById(R.id.item_status_comment_count);
        LinearLayout linearLayout = (LinearLayout) myHead.findViewById(R.id.item_status_bg);
        if (status != null) {
            User user = status.user;
            if (user != null) {
                nameTxt.setText(user.screen_name);
                contentTxt.setText(status.text);
                txtLikeCount.setText(status.attitudes_count + "");
                txtForwordCount.setText(status.reposts_count + "");
                txtcommentCount.setText(status.comments_count + "");
                String[] strings = FormateTimeUtil.getFormatTime(status.created_at);
                timeTxt.setText(strings[1] + strings[2] + "日 " + strings[3]);
                if (status.bgColor != 0) {
                    linearLayout.setBackgroundColor(status.bgColor);
                }
                //设置用户的头像，需要开启异步任务
                String avataUrl = user.avatar_large;
                //为了避免图片错乱的问题，需要给每一次加载图片时，imageview设置加载网址的tag
                //通过tag传递给task，进行检查
                iconImg.setTag(avataUrl);
                if (avataUrl != null) {
                    //todo 加载图片，并设置到imageview
                    CacheImageAsyncTask task = new CacheImageAsyncTask(iconImg, "userico");
                    task.execute(avataUrl);
                }
                ArrayList<String> picUrls = status.pic_urls;
                if (picUrls != null) {
                    //显示微博的图片信息
                    StatusImagsGridViewAdapter imagsGridViewAdapter = new StatusImagsGridViewAdapter(this, picUrls);
                    imgs.setAdapter(imagsGridViewAdapter);
                }
            }

            //显示转发的微博信息内容
            //显示转发微博的原作者
            Status retweetedStatus = status.retweeted_status;
            if (retweetedStatus != null) {
                User retweetedUser = retweetedStatus.user;
                String name = retweetedUser.name;
                String text = retweetedStatus.text;
                if (text != null) {
                    //显示转发微博的内容
                    zhuanFaContentTxt.setText("@" + name + ":" + text);
                }
                ArrayList<String> picUrls = retweetedStatus.pic_urls;
                if (picUrls != null) {
                    //显示转发微博的图片信息
                    StatusImagsGridViewAdapter imagsGridViewAdapter = new StatusImagsGridViewAdapter(this, picUrls);
                    zhuanFaImgs.setAdapter(imagsGridViewAdapter);
                }
            }
        }
        return myHead;
    }


    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {

    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        CommentsAPI commentsAPI = new CommentsAPI(this, Constants.APP_KEY, token);
        commentsAPI.show(Long.parseLong(id), 0, 0, 10, currentPage, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();

                if (!TextUtils.isEmpty(response)) {
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        ArrayList<Comment> comments1 = commentList.commentList;
                        if (comments1 != null) {
                            comments.addAll(comments1);
                            adapter.notifyDataSetChanged();
                        } else
                            Toast.makeText(StatusActivity.this, "没有更多的数据了", Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(StatusActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    /**
     * 回复评论的监听事件
     *
     * @param view
     */
    public void replyOnClick(View view) {
        //1、联网把回复的评论发送给服务器
        TextView inputTxt = (TextView) findViewById(R.id.input);
        String text = inputTxt.getText().toString();
        CommentsAPI commentsAPI = new CommentsAPI(this, Constants.APP_KEY, token);
        if (text != null) {
            commentsAPI.create(text, Long.parseLong(id), true, new RequestListener() {
                @Override
                public void onComplete(String s) {
                    //这里应该返回的是成功还是失败了
                    //需要对数据进行解析，看返回值来判断是否发布成功
                    if (!TextUtils.isEmpty(s)) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Comment comment = Comment.parse(jsonObject);
                            if (comment != null) {
                                Toast.makeText(StatusActivity.this, "评论成功", Toast.LENGTH_LONG).show();
                                //清空输入框
                                inputTxt.setText("");
                                //2、更新评论列表的展示
                                commentsAPI.show(Long.parseLong(id), 0, 0, 10, 1, 0, new RequestListener() {
                                    @Override
                                    public void onComplete(String response) {
                                        if (!TextUtils.isEmpty(response)) {
                                            CommentList commentList = CommentList.parse(response);
                                            if (commentList != null) {
                                                ArrayList<Comment> comments1 = commentList.commentList;
                                                if (comments1 != null) {
                                                    if (comments != null) {
                                                        comments.clear();
                                                        comments.addAll(0, comments1);
                                                        adapter.notifyDataSetChanged();
                                                    } else {//空指针，给他new一个
                                                        adapter = new CommentListAdapter(StatusActivity.this, comments1);
                                                        pullToRefreshListView.setAdapter(adapter);
                                                    }

                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onWeiboException(WeiboException e) {
                                        Toast.makeText(StatusActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onWeiboException(WeiboException e) {
                    Toast.makeText(StatusActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_fav:
                Toast.makeText(StatusActivity.this, "此功能正在开发中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_zhuanfa:
                Toast.makeText(StatusActivity.this, "此功能正在开发中...", Toast.LENGTH_SHORT).show();
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