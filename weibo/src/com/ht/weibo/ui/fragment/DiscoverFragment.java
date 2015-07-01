package com.ht.weibo.ui.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.ht.weibo.R;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.BDNotifyListener;//假如用到位置提醒功能，需要import该类
//如果使用地理围栏功能，需要import如下类
import com.baidu.location.BDGeofence;
import com.baidu.location.BDLocationStatusCodes;
import com.baidu.location.GeofenceClient;
import com.baidu.location.GeofenceClient.OnAddBDGeofencesResultListener;
import com.baidu.location.GeofenceClient.OnGeofenceTriggerListener;
import com.baidu.location.GeofenceClient.OnRemoveBDGeofencesResultListener;

/**
 * Created by annuo on 2015/6/3.
 */
public class DiscoverFragment extends Fragment {
    private NearWeiBoFragment nearWeiBoFragment;
    private NearPeopleFragment nearPeopleFragment;
    private RecommendPeopleFragment recommendPeopleFragment;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private DisplayMetrics dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) getView().findViewById(R.id.pager_discover);
        tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs_discover);
        pager.setAdapter(new MyPagerAdapter(getActivity().getSupportFragmentManager()));
        tabs.setViewPager(pager);
        setTabsValue();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    /**
     * 对PagerSlidingTabStrip的各项属性进行赋值。
     */
    private void setTabsValue() {
        // 设置Tab是自动填充满屏幕的
        tabs.setShouldExpand(true);
        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 4, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#009DFF"));
        // 设置选中Tab文字的颜色 (这是我自定义的一个方法)
        tabs.setSelectedTextColor(Color.parseColor("#009DFF"));
        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        private final String[] titles = {"周边微博", "周边人", "推荐关注"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if (nearWeiBoFragment == null) {
                        nearWeiBoFragment = new NearWeiBoFragment();
                    }
                    return nearWeiBoFragment;
                case 1:
                    if (nearPeopleFragment == null) {
                        nearPeopleFragment = new NearPeopleFragment();
                    }
                    return nearPeopleFragment;
                case 2:
                    if (recommendPeopleFragment == null) {
                        recommendPeopleFragment = new RecommendPeopleFragment();
                    }
                    return recommendPeopleFragment;

                default:
                    return null;
            }
        }

    }

}