package com.example.newsapp.models;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.newsapp.fragments.MainFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
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
    public Fragment getItem(int position) { return MainFragment.newInstance(channels.get(position).getName()); }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) { return channels.get(position).getName(); }

    @Override
    public int getCount() { return channels.size(); }
}