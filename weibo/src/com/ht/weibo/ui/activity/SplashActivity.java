package com.ht.weibo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.util.AccessTokenKeeper;
import com.ht.weibo.util.UserInfoKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.User;


/**
 * Created by annuo on 2015/6/29.
 */
public class SplashActivity extends Activity {

    private String homeResponse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this, R.layout.activity_splash, null);
        setContentView(view);

        //渐变展示启动屏
        AlphaAnimation aa = new AlphaAnimation(0.3f,1.0f);
        aa.setDuration(2000);
        view.startAnimation(aa);
        aa.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation arg0) {
                redirectTo();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }

        });

    }


    private void redirectTo() {
        //todo 建议把homefragment的数据获取加到这里来获取

        //////////////////////保存下来用户的信息，减少以后的访问
        //从sharepreference中得到用户id
        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        //使用usersapi获得用户的信息
        UsersAPI muserAPI = new UsersAPI(this, Constants.APP_KEY, token);
        long uid = Long.parseLong(token.getUid());
        muserAPI.show(uid, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    User user = User.parse(response);
                    //把用户信息保存下来，减少接口的调用次数
                    if (user != null) {
                        UserInfoKeeper.writeUserInfo(SplashActivity.this, user);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(SplashActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


        ///////////////////////取到最近微博的json数据
        //得到该用户和该用户关注的人发布的最新微博
        StatusesAPI mstatusesAPI = new StatusesAPI(this, Constants.APP_KEY, token);
        mstatusesAPI.friendsTimeline(0, 0, 10, 1, false, 0, false, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    //可以启动activity了，并且可以把数据发给mainactivity了
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    intent.putExtra("response", response);
                    startActivity(intent);
                    finish();
                }

            }

            @Override
            public void onWeiboException(WeiboException e) {
                Toast.makeText(SplashActivity.this, "请确保网络连接畅通", Toast.LENGTH_LONG).show();
            }
        });
    }
}