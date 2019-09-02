package com.java.luolingxiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.luolingxiao.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchFragment extends DefaultFragment {

    private ViewPager viewPager;
    private FragmentPagerAdapter adapter;
    private List<NewsListFragment> fragments;

    public SearchFragment() {}

    public static SearchFragment newInstance(ArrayList<String> channels, String word) {
        SearchFragment fragment = new SearchFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("channels", channels);
        bundle.putString("word", word);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout_search);
        viewPager = view.findViewById(R.id.view_pager_search);
        List<String> channels = Objects.requireNonNull(getArguments()).getStringArrayList("channels");
        String word = Objects.requireNonNull(getArguments()).getString("word");
        fragments = new ArrayList<>();
        for (String channel: Objects.requireNonNull(channels)) {
            fragments.add(NewsListFragment.newInstance(channel, word));
        }
        adapter = new FragmentPagerAdapter(Objects.requireNonNull(getFragmentManager())) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return Objects.requireNonNull(channels).size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return Objects.requireNonNull(channels).get(position);
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(100);
        tabLayout.setupWithViewPager(viewPager);
        adapter.startUpdate(viewPager);
        return view;
    }

    private void removeFragments() {
        FragmentTransaction transaction = Objects.requireNonNull(getFragmentManager()).beginTransaction();
        for (Fragment fragment: fragments) {
            transaction.remove(fragment);
        }
        transaction.commit();
    }

    @Override
    public void onStop() {
        super.onStop();
        removeFragments();
    }
}
