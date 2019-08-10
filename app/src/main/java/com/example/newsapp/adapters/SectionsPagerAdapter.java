package com.example.newsapp.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.newsapp.fragments.NewsListFragment;
import com.example.newsapp.bean.ChannelBean;
import com.example.newsapp.fragments.SearchPageFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static final int SEARCH_PAGE_POS = -1;
    private List<ChannelBean> channels;

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        channels = new ArrayList<>();
    }

    public void updateChannels(List<ChannelBean> newChannels) {
        channels.clear();
        channels.addAll(newChannels);
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        switch (position) {
//            case SEARCH_PAGE_POS:
//                return SearchPageFragment.newInstance();

            default:
                return NewsListFragment.newInstance(channels.get(position).getName());
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
//            case SEARCH_PAGE_POS:
//                return "搜索";
            default:
                return channels.get(position).getName();
        }
    }

    @Override
    public int getCount() { return channels.size(); }
}
