package com.ht.weibo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.ui.adapter.FromMyCommentListAdapter;
import com.ht.weibo.ui.widget.RoundProgressBar;
import com.ht.weibo.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.CommentsAPI;
import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.CommentList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by annuo on 2015/6/3.
 */
public class MsgMyFragment extends Fragment implements PullToRefreshBase.OnRefreshListener2<ListView>, Runnable {
    private int currentPage = 1;
    //记录下当前屏幕最上面的记录的时间
    private long currentId;
    private Oauth2AccessToken token;
    private Long uid;
    private PullToRefreshListView pullToRefreshListView;
    private FromMyCommentListAdapter fromMyCommentListAdapter;
    private CommentsAPI commentsAPI;
    private ArrayList<Comment> comments;
    private RoundProgressBar roundProgressBar;
    private boolean isRoundProgressBarShown = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messagemy, container, false);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        roundProgressBar = (RoundProgressBar)getView().findViewById(R.id.roundProgressBar);
        roundProgressBar.setMax(100);
        Thread thread = new Thread(this);
        thread.start();


        pullToRefreshListView = (PullToRefreshListView) getView().findViewById(R.id.msgmy_listview);

        //下拉刷新和上拉加载
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setOnRefreshListener(this);

        //从sharepreference中得到用户id
        token = AccessTokenKeeper.readAccessToken(getActivity());
        uid = Long.parseLong(token.getUid());

        //得到登录用户收到的的评论
        commentsAPI = new CommentsAPI(getActivity(), Constants.APP_KEY, token);
        commentsAPI.byME(0, 0, 10, currentPage, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    isRoundProgressBarShown = false;
                    roundProgressBar.setVisibility(View.GONE);
                    CommentList commentList = CommentList.parse(response);
                    comments = commentList.commentList;
                    if (comments != null) {
                        Comment comment = comments.get(0);
                        if (comment != null) {
                            currentId = Long.parseLong(comment.id);
                            fromMyCommentListAdapter = new FromMyCommentListAdapter(getActivity(), comments);
                            pullToRefreshListView.setAdapter(fromMyCommentListAdapter);
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

    /**
     * 下拉刷新的方法
     *
     * @param refreshView
     */
    @Override
    public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
        //根据curentid来书写下拉刷新的方法
        commentsAPI.byME(currentId, 0, 10, 1, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        List<Comment> list = commentList.commentList;
                        if (list != null) {
                            Comment comment = list.get(0);
                            if (comment != null) {
                                currentId = Long.parseLong(comment.id);
                                comments.addAll(0, list);
                                fromMyCommentListAdapter.notifyDataSetChanged();
                            } else
                                Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {

            }
        });


    }

    /**
     * 上拉刷新的方法
     *
     * @param refreshView
     */
    @Override
    public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
        currentPage = currentPage + 1;
        commentsAPI.byME(0, 0, 10, currentPage, 0, new RequestListener() {
            @Override
            public void onComplete(String response) {
                //关闭刷新的操作
                pullToRefreshListView.onRefreshComplete();
                if (!TextUtils.isEmpty(response)) {
                    CommentList commentList = CommentList.parse(response);
                    if (commentList != null) {
                        List<Comment> list = commentList.commentList;
                        if (list != null) {
                            comments.addAll(1, list);
                            fromMyCommentListAdapter.notifyDataSetChanged();
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