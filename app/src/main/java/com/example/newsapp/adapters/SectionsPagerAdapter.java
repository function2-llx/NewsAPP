package com.example.newsapp.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.newsapp.bean.ChannelBean;
import com.example.newsapp.fragments.NewsListFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<ChannelBean> channels;
    private List<NewsListFragment> fragments;
    private String words = "";

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        channels = new ArrayList<>();
    }

    private void updateFragments() {
        fragments.clear();
        for (ChannelBean channel: channels) {
            fragments.add(NewsListFragment.newInstance(channel.getName(), words));
        }
    }

    public void updateWords(String words) {
        this.words = words;
        updateFragments();
    }

    public void updateChannels(List<ChannelBean> newChannels) {
//        Collections.copy(channels, newChannels);
        channels.clear();
        channels.addAll(newChannels);
        updateFragments();
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public Fragment getItem(int position) {
        return fragments.get(position);
//        return NewsListFragment.newInstance(channels.get(position).getName(), words);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getName();
    }

    @Override
    public int getCount() { return channels.size(); }
}
