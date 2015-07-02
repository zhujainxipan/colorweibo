package com.ht.jellybean.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.*;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.ht.jellybean.Constants;
import com.ht.jellybean.R;
import com.ht.jellybean.ui.fragment.DiscoverFragment;
import com.ht.jellybean.ui.fragment.HomeFragment;
import com.ht.jellybean.ui.fragment.MessageFragment;
import com.ht.jellybean.ui.fragment.MyFragment;
import com.ht.jellybean.ui.widget.DrawerArrowDrawable;
import com.ht.jellybean.util.AccessTokenKeeper;
import com.ht.jellybean.thread.CacheImageAsyncTask;
import com.ht.jellybean.util.MyApplication;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.UsersAPI;
import com.sina.weibo.sdk.openapi.models.User;
import static android.view.Gravity.START;

/**
 * Created by annuo on 2015/6/3.
 */
public class MainActivity extends FragmentActivity {

    private Fragment currentFragment = null;
    private FragmentManager manager;
    private ActionBar actionBar;
    private DrawerLayout drawerLayout;
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private long exitTime = 0;
    private String extra;
    private double latitude;
    private double lontitude;


    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLontitude() {
        return lontitude;
    }

    public void setLontitude(double lontitude) {
        this.lontitude = lontitude;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        actionBar = getActionBar();
        //只有这样，actionbar的图标才可以被点击
        actionBar.setDisplayHomeAsUpEnabled(true);


        //得到从splashactivity中传递过来的参数
        Intent intent = getIntent();
        extra = intent.getStringExtra("response");


        //自定义的发布微博的按钮

        //属于其中之一
        FloatingActionButton actionA = new FloatingActionButton(getBaseContext());
        actionA.setImageResource(R.drawable.ab_handwriting);
        actionA.setPadding(5, 3, 5, 5);
        //actionA.setTitle("发布文字微博");
        actionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddWeiBoActivity.class);
                intent.putExtra("type", "txt");
                startActivity(intent);

            }
        });

        //属于其中之一
        FloatingActionButton actionB = new FloatingActionButton(getBaseContext());
        //actionB.setTitle("发布图片微博");
        actionB.setPadding(5, 0, 5, 5);
        actionB.setImageResource(R.drawable.ab_attach_picture);
        actionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddWeiBoActivity.class);
                intent.putExtra("type", "img");
                startActivity(intent);
            }
        });


        FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        floatingActionsMenu.addButton(actionA);
        floatingActionsMenu.addButton(actionB);



        //默认显示首页界面
        if (currentFragment == null) {
            HomeFragment homeFragment = new HomeFragment();
            Bundle bundle = new Bundle();
            bundle.putString("response", extra);
            homeFragment.setArguments(bundle);
            manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.main_fragment_container, homeFragment, "tag1");
            transaction.addToBackStack("tag1");
            transaction.commit();
            currentFragment = homeFragment;
        }

        //radiogroup的选中事件
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.main_tab_bar);
        //默认首页选中
        radioGroup.check(R.id.tab_home);

        //为解决fragment+radiogroup卡顿的问题，使用一个参数记录当前选中的fragment的id
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.tab_home:
                        actionBar.setTitle("  首页");
                        Fragment fragment1 = manager.findFragmentByTag("tag1");
                        if (fragment1 == null) {
                            HomeFragment homeFragment = new HomeFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("response", extra);
                            homeFragment.setArguments(bundle);
                            //将新Fragment add并将当前显示的Fragment hide
                            manager.beginTransaction().hide(currentFragment).add(R.id.main_fragment_container, homeFragment, "tag1").addToBackStack("tag1").commit();
                            currentFragment = homeFragment;
                        } else if (fragment1.isHidden()) {
                            manager.beginTransaction().hide(currentFragment).show(fragment1).commit();
                            currentFragment = fragment1;
                        }
                        break;
                    case R.id.tab_discover:
                        actionBar.setTitle("  发现");
                        Fragment fragment2 = manager.findFragmentByTag("tag2");
                        if (fragment2 == null) {
                            DiscoverFragment discoverFragment = new DiscoverFragment();
                            //将新Fragment add并将当前显示的Fragment hide
                            manager.beginTransaction().hide(currentFragment).add(R.id.main_fragment_container, discoverFragment, "tag2").addToBackStack("tag2").commit();
                            currentFragment = discoverFragment;
                        } else if (fragment2.isHidden()) {
                            manager.beginTransaction().hide(currentFragment).show(fragment2).commit();
                            currentFragment = fragment2;
                        }
                        break;
                    case R.id.tab_message:
                        actionBar.setTitle("  消息");
                        Fragment fragment3 = manager.findFragmentByTag("tag3");
                        if (fragment3 == null) {
                            MessageFragment messageFragment = new MessageFragment();
                            //将新Fragment add并将当前显示的Fragment hide
                            manager.beginTransaction().hide(currentFragment).add(R.id.main_fragment_container, messageFragment, "tag3").addToBackStack("tag3").commit();
                            currentFragment = messageFragment;
                        } else if (fragment3.isHidden()) {
                            manager.beginTransaction().hide(currentFragment).show(fragment3).commit();
                            currentFragment = fragment3;
                        }
                        break;
                    case R.id.tab_my:
                        actionBar.setTitle("  个人");
                        Fragment fragment4 = manager.findFragmentByTag("tag4");
                        if (fragment4 == null) {
                            MyFragment myFragment = new MyFragment();
                            //将新Fragment add并将当前显示的Fragment hide
                            manager.beginTransaction().hide(currentFragment).add(R.id.main_fragment_container, myFragment, "tag4").addToBackStack("tag4").commit();
                            currentFragment = myFragment;
                        } else if (fragment4.isHidden()) {
                            manager.beginTransaction().hide(currentFragment).show(fragment4).commit();
                            currentFragment = fragment4;
                        }
                        break;
                }
            }
        });

        //activity的actionbar图标动态改变（跟随侧拉的出现和展开）
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        final Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.white));
        actionBar.setLogo(drawerArrowDrawable);

        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }
                drawerArrowDrawable.setParameter(offset);
            }
        });


        //为侧拉菜单添加监听事件
        ImageView imageView = (ImageView) findViewById(R.id.leftmenu_usericon);

        //**************************设置头像
        Oauth2AccessToken token = AccessTokenKeeper.readAccessToken(this);
        //使用usersapi获得用户的信息
        UsersAPI muserAPI = new UsersAPI(this, Constants.APP_KEY, token);
        long uid = Long.parseLong(token.getUid());
        muserAPI.show(uid, new RequestListener() {
            @Override
            public void onComplete(String response) {
                if (!TextUtils.isEmpty(response)) {
                    User user = User.parse(response);
                    if (user != null) {
                        //显示用户的头像
                        imageView.setTag(user.avatar_large);
                        CacheImageAsyncTask myTask = new CacheImageAsyncTask(imageView, "userico");
                        myTask.execute(user.avatar_large);
                    }
                }
            }

            @Override
            public void onWeiboException(WeiboException e) {
            }
        });


        //百度地图定位写在这里吧

    }


    /**
     * 我的收藏的点击处理事件
     *
     * @param view
     */
    public void favOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, FavActivity.class);
        startActivity(intent);
    }


    /**
     * 热门收藏点击处理事件
     *
     * @param view
     */
    public void hotFavOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, HotFavActivity.class);
        startActivity(intent);
    }

    /**
     * 微博精选的点击处理事件
     *
     * @param view
     */
    public void jingxuanOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, WBSquareActivity.class);
        startActivity(intent);
    }

    /**
     * 热门话题点击处理事件
     *
     * @param view
     */
    public void hothuatiOnClick(View view) {
        Intent intent = new Intent(MainActivity.this, HotTopicActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case android.R.id.home:
                if (drawerLayout.isDrawerVisible(START)) {
                    drawerLayout.closeDrawer(START);
                } else {
                    drawerLayout.openDrawer(START);
                }
                break;
            case R.id.action_more:
                //显示一个pupupwindow
                popUpMyOverflow(MainActivity.this);
                break;
        }

        return super.

                onOptionsItemSelected(item);

    }


    public void popUpMyOverflow(Activity context) {
        /**
         * 定位PopupWindow，让它恰好显示在Action Bar的下方。 通过设置Gravity，确定PopupWindow的大致位置。
         * 首先获得状态栏的高度，再获取Action bar的高度，这两者相加设置y方向的offset样PopupWindow就显示在action
         * bar的下方了。 通过dp计算出px，就可以在不同密度屏幕统一X方向的offset.但是要注意不要让背景阴影大于所设置的offset，
         * 否则阴影的宽度为offset.
         */
        // 获取状态栏高度
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        //状态栏高度：frame.top
        int xOffset = frame.top + getActionBar().getHeight();//减去阴影宽度，适配UI.
        int yOffset = Dp2Px(this, 5f); //设置x方向offset为5dp
        View parentView = getLayoutInflater().inflate(R.layout.activity_main,
                null);
        View popView = getLayoutInflater().inflate(
                R.layout.popupwin_more, null);

        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        PopupWindow popWind = new PopupWindow(popView,
                w / 2, ViewGroup.LayoutParams.WRAP_CONTENT, true);//popView即popupWindow的布局，ture设置focusAble.
        //必须设置BackgroundDrawable后setOutsideTouchable(true)才会有效。这里在XML中定义背景，所以这里设置为null;
        popWind.setBackgroundDrawable(new BitmapDrawable(getResources(),
                (Bitmap) null));
        popWind.setOutsideTouchable(true); //点击外部关闭。
        popWind.setAnimationStyle(android.R.style.Animation_Dialog);    //设置一个动画。
        //设置Gravity，让它显示在右上角。
        popWind.showAtLocation(parentView, Gravity.RIGHT | Gravity.TOP,
                yOffset, xOffset);
    }


    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    /**
     * 切换模式的点击事件
     * @param view
     */
    public void switchOnClick(View view) {
        Toast.makeText(this, "该功能正在开发中....", Toast.LENGTH_SHORT).show();
    }

    /**
     * 关于事件的点击事件
     * @param view
     */
    public void aboutOnClick(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    /**
     * 反馈的事件监听
     * @param view
     */
    public void feedbackOnClick(View view) {
        Intent intent = new Intent(this, FeedBackActivity.class);
        startActivity(intent);
    }


    ////实现连续点击两次弹出是否退出的提示框
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            exitTime = System.currentTimeMillis();
        } else {
            //弹出是否退出应用程序的对话框
            SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            sweetAlertDialog.setTitleText("你确定要退出吗?");
            sweetAlertDialog.setContentText("还有很多精彩你没有去发现呢....");
            sweetAlertDialog.setCancelText("取消");
            sweetAlertDialog.setConfirmText("确定");
            sweetAlertDialog.showCancelButton(true);
            sweetAlertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                //取消的监听事件
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    sweetAlertDialog.cancel();
                }
            });
            sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                //确定的监听事件
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    MainActivity.this.finish();
                    MyApplication application = (MyApplication) getApplication();
                    application.exit();
                }
            });
            sweetAlertDialog.show();
        }
    }
}
