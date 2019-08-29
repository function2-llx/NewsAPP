package com.java.luolingxiao.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.luolingxiao.DeFaultActivity;
import com.java.luolingxiao.MainActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.adapters.SectionsPagerAdapter;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.ChannelBean;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.trs.channellib.channel.channel.helper.ChannelDataHelper;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;

public class MainFragment extends DefaultFragment
    implements ChannelDataHelper.ChannelDataRefreshListenter {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ChannelDataHelper<ChannelBean> channelDataHelper;
    private int selectedChannelPosition = -1;
    private SectionsPagerAdapter pagerAdapter;

    private MaterialSearchBar searchBar;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        initSearchBar(view);
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
                List<ChannelBean> channels = channelDataHelper.getShowChannels(((DeFaultActivity)getActivity()).getAllChannels());
                pagerAdapter.updateChannels(channels);
                if (selectedChannelPosition != -1) {
                    viewPager.setCurrentItem(selectedChannelPosition);
                    selectedChannelPosition = -1;
                }
            });
        }).start();
    }

    private void initTabs(View view) {
        viewPager = view.findViewById(R.id.view_pager);
        pagerAdapter = new SectionsPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        channelDataHelper = new ChannelDataHelper<ChannelBean>(getActivity(), this, view.findViewById(R.id.container_main));
        channelDataHelper.setSwitchView(view.findViewById(R.id.subscribe));
        viewPager.setOffscreenPageLimit(100);
        refreshTabs();
    }

    void initSearchBar(View view) {
        searchBar = view.findViewById(R.id.search_view);
        searchBar.inflateMenu(R.menu.menu_main);

        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (enabled) {
                    searchBar.setHint(searchBar.getPlaceHolderText());
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                if (text != null) {
                    if (text.length() > 0) getDataRepository().insertSearchRecords(text.toString());
                    pagerAdapter.updateWords(text.toString());
                    searchBar.setPlaceHolder(text);
                    searchBar.disableSearch();
                } else {
                    // back to default mode
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        searchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                searchBar.onItemClick(position, v);
            }

            @Override
            public void onItemDelete(int position, View v) {
                searchBar.onItemDelete(position, v);
                if (v.getTag() instanceof String) {
                    getDataRepository().deleteSearchRecords(v.getTag().toString());
                }
            }
        });
        getDataRepository().getAllSearchRecords(records -> searchBar.setLastSuggestions(records));

        searchBar.getMenu().setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_settings:
                    ((MainActivity)getActivity()).startSettings();
                    break;

                case R.id.share_test:
                    OnekeyShare oks = new OnekeyShare();
                    oks.disableSSOWhenAuthorize();
                    oks.setTitle("分享测试");
                    oks.setText("咕鸽快来写代码");
                    oks.show(getActivity());
                    break;

                case R.id.news_test:
                    NewsApi.requestNews(new NewsApi.SearchParams()
                                    .setSize(20)
                                    .setWords("咕咕")
                                    .setCategory("科技")
                                    .setStartDate(new NewsDateTime(2019, 7, 1))
                                    .setEndDate(new NewsDateTime(2019, 7, 3)),
                            new NewsApi.NewsCallback() {
                                @Override
                                public void onReceived(List<NewsBean> newsBeanList) {
                                    if (newsBeanList.isEmpty()) {
                                        Toast.makeText(getActivity(), "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onException(Exception e) {
                                    if (e instanceof NetworkError) {
                                        Toast.makeText(getActivity(), "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                    );
                    break;

                case R.id.login:
                    Platform platform = ShareSDK.getPlatform(QQ.NAME);
                    if (!platform.isAuthValid()) {
                        Toast.makeText(getActivity(), "登陆过了", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(getActivity(), "userid: " + platform.getDb().getUserId(), Toast.LENGTH_SHORT).show();
//                    System.err.print();
//                    platform.SSOSetting(false);
                    ShareSDK.setActivity(getActivity());
                    platform.setPlatformActionListener(new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                            getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), "登录成功啦" + hashMap.toString(), Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            Toast.makeText(getActivity(), "登录失败", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            Toast.makeText(getActivity(), "取消登录", Toast.LENGTH_SHORT).show();
                        }
                    });
//                    platform.authorize();
                    platform.showUser(null);
                    break;
            }
            return true;
        });
    }
}
