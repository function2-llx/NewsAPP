package com.java.luolingxiao.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.java.luolingxiao.bean.ChannelBean;
import com.java.luolingxiao.fragment.NormalNewsListFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private List<ChannelBean> channels;
    private List<NormalNewsListFragment> fragments;
    private String words = "";

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        fragments = new ArrayList<>();
        channels = new ArrayList<>();
    }

    private void updateFragments() {
        fragments.clear();
        for (ChannelBean channel: channels) {
            fragments.add(NormalNewsListFragment.newInstance(channel.getName(), words));
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
//        return NormalNewsListFragment.newInstance(channels.get(position).getName(), words);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return channels.get(position).getName();
    }

    @Override
    public int getCount() { return channels.size(); }
}
