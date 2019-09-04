package com.java.luolingxiao.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.luolingxiao.DataRepository;
import com.java.luolingxiao.R;
import com.java.luolingxiao.api.NetworkChecker;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.yanzhenjie.nohttp.error.NetworkError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class NormalNewsListFragment extends SimpleNewsListFragment {
//    private ArrayList<NewsBean> data = new ArrayList<>();
    private ArrayList<Boolean> data_read = new ArrayList<>();
    private String mNewsId;
    private String mNewsType;
    private int mStartPage = 0;
    private boolean offline = false;
    private boolean isPrepared;
    private boolean isVisible;
    private int offset;

    private NewsDateTime lastDate;

    public NormalNewsListFragment() {}

    public static NormalNewsListFragment newInstance(String channel, String words) {
        NormalNewsListFragment fragment = new NormalNewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel", channel);
        bundle.putString("words", words);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String getChannel() {
        assert this.getArguments() != null;
        return this.getArguments().getString("channel");
    }

    private String getWords() {
        assert this.getArguments() != null;
        return getArguments().getString("words");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        lastDate = new NewsDateTime();
        getNewsListDataRequest("", 1, lastDate, false, false);
        return view;
    }

    @Override
    public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(() -> {
            getNewsListDataRequest("", 6, lastDate, true, false);
            refreshLayout.resetNoMoreData();//setNoMoreData(false);//恢复上拉状态
        }, 100);
    }

    @Override
    public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
        refreshLayout.getLayout().postDelayed(() -> {
            if (noMore) {
                Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
            } else {
                getNewsListDataRequest("", 6, lastDate, false, true);
            }
        }, 100);
    }

    public void getNewsListDataRequest(String type, int size, NewsDateTime endDate, boolean isOnRefresh, boolean isOnLoadMore) {
        if (endDate != null) {
            endDate = endDate.minusSeconds(1);
        }

        if (NetworkChecker.isNetworkConnected(getContext())) {
            if (offline) Toast.makeText(getContext(), "进入在线模式", Toast.LENGTH_SHORT).show();
            offline = false;
            NewsApi.requestNews(new NewsApi.SearchParams()
                            .setSize(size)
                            .setWords(getWords())
                            .setCategory(getChannel().equals(getString(R.string.channel_default)) ? "" : getChannel())
//                        .setStartDate()
                            .setEndDate(endDate),
                    new NewsApi.NewsCallback() {
                        @Override
                        public void onReceived(List<NewsBean> newsBeanList) {
                            // 可能已经莫得了
                            if (getActivity() == null) return;
                            if (newsBeanList.size() > 0) {
                                lastDate = newsBeanList.get(newsBeanList.size() - 1).getPublishTime();
                            } else {
                                noMore = true;
                            }
                            getDataRepository().insertNews(newsBeanList);
                            setNewsList(newsBeanList, isOnRefresh, isOnLoadMore);

                            refreshLayout.finishLoadMore();
                            refreshLayout.finishRefresh();
//                        returnNewsListData(newsBeanList);
//                        if (newsBeanList.isEmpty()) {
//                            Toast.makeText(MainActivity.this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
//                        }
                        }

                        @Override
                        public void onException(Exception e) {
                            e.printStackTrace();
                            if (e instanceof NetworkError) {
                                Toast.makeText(getContext(), "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
                            }
                            refreshLayout.finishLoadMore();
                            refreshLayout.finishRefresh();
                        }
                    }
            );
        } else {
//            if (")getCategory().equals("首页)
//            if (getUserVisibleHint())
            if (!offline) Toast.makeText(getContext(), "离线模式", Toast.LENGTH_SHORT).show();
            offline = true;
            getDataRepository().getSavedNewsByCategory(getChannel(), size, offset, new DataRepository.OnReceiveSavedNewsCallback() {
                @Override
                public void onReceive(List<NewsBean> savedNews) {
                    offset += savedNews.size();
                    setNewsList(savedNews, isOnRefresh, isOnLoadMore);
                    refreshLayout.finishLoadMore();
                    refreshLayout.finishRefresh();
                }
            });
        }

    }

    public void setNewsList(List<NewsBean> newsBeanList, boolean isOnRefresh, boolean isOnLoadMore) {
        ArrayList<Model> newsList = new ArrayList<>();
        if (isOnRefresh) {
            data.clear();
            data_read.clear();
        }
        for (int i = 0; i < newsBeanList.size(); ++i) {
            int finalI = i;
            List<String> images = newsBeanList.get(finalI).getImageUrls();
            data.add(newsBeanList.get(finalI));
            data_read.add(false);
            newsList.add(new Model() {{

                this.name = newsBeanList.get(finalI).getTitle();
                this.nickname = newsBeanList.get(finalI).getContent();
                for (int i = 0; i < this.nickname.length() - 1; ++i) {
                    if (this.nickname.charAt(i) != '\n' && this.nickname.charAt(i) != '　' && this.nickname.charAt(i) != ' ') {
                        this.nickname = this.nickname.substring(i);
                        newsBeanList.get(finalI).setContent(this.nickname);
                        break;
                    }
                }
                if (images.size() > 0) this.imageUrl = images.get(0);
                this.position = data.size() - 1;
            }});
        }
        if (isOnRefresh) {
            mAdapter.refresh(newsList);
        } else mAdapter.loadMore(newsList);
    }

    private Collection<Model> loadModels() {
        return Arrays.asList(
                new Model() {{
                    this.name = "但家香酥鸭";
                    this.nickname = "爱过那张脸";
//                    this.imageId = R.mipmap.image_practice_repast_1;
                }}, new Model() {{
                    this.name = "香菇蒸鸟蛋";
                    this.nickname = "淑女算个鸟";
//                    this.imageId = R.mipmap.image_practice_repast_2;
                }}, new Model() {{
                    this.name = "花溪牛肉粉";
                    this.nickname = "性感妩媚";
//                    this.imageId = R.mipmap.image_practice_repast_3;
                }}, new Model() {{
                    this.name = "破酥包";
                    this.nickname = "一丝丝纯真";
//                    this.imageId = R.mipmap.image_practice_repast_4;
                }}, new Model() {{
                    this.name = "盐菜饭";
                    this.nickname = "等着你回来";
//                    this.imageId = R.mipmap.image_practice_repast_5;
                }}, new Model() {{
                    this.name = "米豆腐";
                    this.nickname = "宝宝树人";
//                    this.imageId = R.mipmap.image_practice_repast_6;
                }});
    }
//    public void scrolltoTop() {
//        irc.smoothScrollToPosition(0);
//    }
}
