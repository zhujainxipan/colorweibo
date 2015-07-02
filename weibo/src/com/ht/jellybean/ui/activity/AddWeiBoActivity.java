package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;
import com.sina.weibo.sdk.openapi.models.Status;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by annuo on 2015/6/24.
 */
public class AddWeiBoActivity extends SwipeBackActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addweibo);

        //actionbar图标可以点击
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
        switch (type) {
            case "txt":
                break;
            case "img":
                ImageButton imageButton = (ImageButton) findViewById(R.id.activity_addweibo_img);
                imageButton.setVisibility(View.VISIBLE);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(AddWeiBoActivity.this, "支持发送图片的功能正在开发中....", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addweibo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_send:
                //发送一条微博
                Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
                StatusesAPI statusesAPI = new StatusesAPI(this, Constants.APP_KEY, token);
                EditText editText = (EditText) findViewById(R.id.addweibo_content);
                String string = editText.getText().toString();
                if (string != null) {
                    statusesAPI.update(string, 0.0 + "", 0.0 + "", new RequestListener() {
                        @Override
                        public void onComplete(String s) {
                            if (!TextUtils.isEmpty(s)) {
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    Status status = Status.parse(jsonObject);
                                    if (status != null) {
                                        //吐司一下，发布成功
                                        Toast.makeText(AddWeiBoActivity.this, "发布成功", Toast.LENGTH_LONG).show();
                                        editText.setText("");
                                        AddWeiBoActivity.this.finish();
                                    } else
                                        Toast.makeText(AddWeiBoActivity.this, "发布失败", Toast.LENGTH_LONG).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onWeiboException(WeiboException e) {

                        }
                    });
                }

                break;

        }
        return super.onContextItemSelected(item);
    }


    public void toOnClick(View view) {
        Toast.makeText(this, "@xx的功能正在开发中....", Toast.LENGTH_SHORT).show();
    }

}