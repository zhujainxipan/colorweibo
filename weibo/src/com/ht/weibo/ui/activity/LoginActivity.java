package com.ht.weibo.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.ht.weibo.Constants;
import com.ht.weibo.R;
import com.ht.weibo.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

public class LoginActivity extends Activity {

    private SsoHandler ssoHandler;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //创建授权认证信息
        AuthInfo authInfo = new AuthInfo(LoginActivity.this, Constants.APP_KEY, Constants.REDIRECT_URL
                , Constants.SCOPE);
        //创建SsoHandler对象
        ssoHandler = new SsoHandler(LoginActivity.this, authInfo);


        //sso登录方式的监听器
        Button ssoButton = (Button) findViewById(R.id.clientLoginButton);
        ssoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用SsoHandler的authorizeClientSso方法
                ssoHandler.authorizeClientSso(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        if (accessToken != null && accessToken.isSessionValid()) {
                            AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this,
                                R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //web登录方式的监听器
        Button webButton = (Button) findViewById(R.id.webLoginButton);
        webButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //调用SsoHandler的authorizeWeb方法
                ssoHandler.authorizeWeb(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        Oauth2AccessToken accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        if (accessToken != null && accessToken.isSessionValid()) {
                            AccessTokenKeeper.writeAccessToken(getApplicationContext(), accessToken);
                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this,
                                R.string.weibosdk_demo_toast_auth_canceled, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }


    /**
     * 当sso授权activity退出时，该函数被调用
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        //仅使用sso方式登录的时候
        if (ssoHandler != null) {
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
            startActivity(intent);
            finish();
        }
    }

}
