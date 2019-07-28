package com.example.newsapp.models;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.newsapp.fragments.MainFragment;

import java.util.List;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new MainFragment(ChannelsManager.getInstance().getChannel(position));
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return ChannelsManager.getInstance().getChannel(position);
    }

    @Override
    public int getCount() {
        return ChannelsManager.getInstance().getCount();
    }
}
