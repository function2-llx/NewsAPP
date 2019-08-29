package com.java.luolingxiao.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.java.luolingxiao.fragment.MainFragment;
import com.java.luolingxiao.fragment.MyFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
    private String[] titles = new String[] {"首页", "我的"};
    public MainPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MainFragment.newInstance();
            case 1:
                return MyFragment.newInstance();
            default:
                return MainFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
