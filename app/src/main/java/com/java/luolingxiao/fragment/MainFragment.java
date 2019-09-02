package com.java.luolingxiao.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.luolingxiao.DeFaultActivity;
import com.java.luolingxiao.MainActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.SearchActivity;
import com.java.luolingxiao.adapter.SectionsPagerAdapter;
import com.java.luolingxiao.bean.ChannelBean;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.trs.channellib.channel.channel.helper.ChannelDataHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainFragment extends DefaultFragment
    implements ChannelDataHelper.ChannelDataRefreshListenter {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChannelDataHelper<ChannelBean> channelDataHelper;
    private int selectedChannelPosition = -1;
    private SectionsPagerAdapter pagerAdapter;
    private Toolbar toolbar;

    //static 代码段可以防止内存泄露
    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.colorPrimary, android.R.color.white);//全局设置主题颜色
                return new ClassicsHeader(context);//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setDrawableSize(20);
            }
        });
    }

//    private List<String> getShowChannels() {
////        return channelDataHelper.getShowChannels((()));
//    }

    private void configureMenu() {
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    ((MainActivity)getActivity()).startSettings();
                break;

                case R.id.action_search: {
                    Intent intent = new Intent(getContext(), SearchActivity.class);
//                    ArrayList<String> unfixedNames = new ArrayList<>();
                    ArrayList<String> channels = new ArrayList<>();
                    for (ChannelBean bean: getShowChannels()) channels.add(bean.getName());
                    intent.putStringArrayListExtra("channels", channels);
                    startActivity(intent);
                }
                break;

            }
            return true;
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        toolbar = view.findViewById(R.id.toolbar_main);
        configureMenu();

        initTabs(view);
        return view;
    }

    public MainFragment() {}

    public static MainFragment newInstance() {
        MainFragment ret = new MainFragment();
        return ret;
    }


    @Override
    public void updateData() {
        refreshTabs();
    }

    @Override
    public void onChannelSelected(boolean update, int position) {
        if (!update) {
            viewPager.setCurrentItem(position);
        } else {
            selectedChannelPosition = position;
        }
    }

    private void refreshTabs() {
        new Thread(() -> {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                List<ChannelBean> channels = getShowChannels();
                pagerAdapter.updateChannels(channels);
                if (selectedChannelPosition != -1) {
                    viewPager.setCurrentItem(selectedChannelPosition);
                    selectedChannelPosition = -1;
                }
            });
        }).start();
    }

    private List<ChannelBean> getShowChannels() {
        return channelDataHelper.getShowChannels(((DeFaultActivity) Objects.requireNonNull(getActivity())).getAllChannels());
    }

    private void initTabs(View view) {
        viewPager = view.findViewById(R.id.view_pager_main);
        pagerAdapter = new SectionsPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        channelDataHelper = new ChannelDataHelper<>(getActivity(), this, view.findViewById(R.id.container_main));
        channelDataHelper.setSwitchView(view.findViewById(R.id.subscribe));
        viewPager.setOffscreenPageLimit(100);
        refreshTabs();
    }
}
