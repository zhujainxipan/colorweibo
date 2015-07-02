package com.ht.jellybean.ui.fragment;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.astuetz.PagerSlidingTabStrip;
import com.ht.jellybean.R;

/**
 * Created by annuo on 2015/6/3.
 */
public class MessageFragment extends Fragment {
    private MsgCommentFragment msgCommentFragment;
    private MsgMyFragment msgMyFragment;
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private DisplayMetrics dm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dm = getResources().getDisplayMetrics();
        pager = (ViewPager) getView().findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip) getView().findViewById(R.id.tabs);
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

        private final String[] titles = { "发出的评论", "收到的评论"};

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
                    if (msgMyFragment == null) {
                        msgMyFragment = new MsgMyFragment();
                    }
                    return msgMyFragment;
                case 1:
                    if (msgCommentFragment == null) {
                        msgCommentFragment = new MsgCommentFragment();
                    }
                    return msgCommentFragment;
                default:
                    return null;
            }
        }

    }
}