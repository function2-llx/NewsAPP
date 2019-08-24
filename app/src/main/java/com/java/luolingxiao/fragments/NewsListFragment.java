package com.java.luolingxiao.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.aspsine.irecyclerview.IRecyclerView;
//import com.aspsine.irecyclerview.OnLoadMoreListener;
//import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.animation.ScaleInAnimation;
//import com.aspsine.irecyclerview.widget.LoadMoreFooterView;
import com.java.luolingxiao.DeFaultActivity;
import com.java.luolingxiao.NewsActivity;
import com.java.luolingxiao.R;
import com.java.luolingxiao.adapters.BaseRecyclerAdapter;
import com.java.luolingxiao.adapters.NewsListAdapter;
import com.java.luolingxiao.adapters.SmartViewHolder;
import com.java.luolingxiao.api.NewsApi;
import com.java.luolingxiao.bean.NewsBean;
import com.java.luolingxiao.bean.NewsDateTime;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.scwang.smartrefresh.layout.util.SmartUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;


public class NewsListFragment extends Fragment {
    private NewsListAdapter newsListAdapter;
    private ArrayList<NewsBean> data = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage=0;
    private boolean noMore = false;
//    Context a;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;
    private NewsDateTime lastDate;
    private class Model {
        int imageId;
        int avatarId;
        int position;
        String name;
        String nickname;
        String imageUrl;
    }
    private BaseRecyclerAdapter<Model> mAdapter;

    public NewsListFragment() {}

    public static NewsListFragment newInstance(String channel, String words) {
        NewsListFragment fragment = new NewsListFragment();
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
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        RefreshLayout refreshLayout = view.findViewById(R.id.refreshLayout);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        lastDate = new NewsDateTime();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter = new BaseRecyclerAdapter<Model>(R.layout.item_practice_repast) {
            @Override
            protected void onBindViewHolder(SmartViewHolder holder, Model model, int position) {
                holder.text(R.id.name, model.name);
                holder.text(R.id.nickname, model.nickname);
                holder.image_init(R.id.image, R.mipmap.ic_care_normal);
                holder.image(R.id.image, model.imageUrl);
                holder.setPosition(model.position);
//                holder.image(R.id.image, model.imageId);
            }
        });

        mAdapter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsActivity.startAction(getContext(), data.get(position));

            }
        });

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefresh();
                        refreshLayout.resetNoMoreData();//setNoMoreData(false);//恢复上拉状态
                    }
                }, 2000);
            }
            @Override
            public void onLoadMore(@NonNull final RefreshLayout refreshLayout) {
                refreshLayout.getLayout().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        if (mAdapter.getCount() > 100)
                        if (noMore) {
                            Toast.makeText(getContext(), "数据全部加载完毕", Toast.LENGTH_SHORT).show();
                            refreshLayout.finishLoadMoreWithNoMoreData();//设置之后，将不会再触发加载事件
                        } else {
                            getNewsListDataRequest("", 20, lastDate, false, false);
//                            mAdapter.loadMore(loadModels());
                            refreshLayout.finishLoadMore();
                        }
                    }
                }, 1000);
            }
        });

//        refreshLayout.getLayout().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                refreshLayout.setHeaderInsetStart(SmartUtil.px2dp(toolbar.getHeight()));
//            }
//        }, 500);
        getNewsListDataRequest("", 1, lastDate, false, false);
        return view;
    }

    public void getNewsListDataRequest(String type, int size, NewsDateTime endDate, boolean isOnRefresh, boolean isOnLoadMore) {
        NewsApi.requestNews(new NewsApi.SearchParams()
                        .setSize(size)
                        .setWords(getWords())
                        .setCategory(getChannel().equals("首页")? "" : getChannel())
//                        .setStartDate()
                        .setEndDate(endDate),
                !((DeFaultActivity)getActivity()).isSaveTrafficMode(),
                new NewsApi.NewsCallback() {
                    @Override
                    public void onReceived(List<NewsBean> newsBeanList) {
                        if (newsBeanList.size() > 0) {
                            lastDate = newsBeanList.get(newsBeanList.size() - 1).getPublishTime();
                        } else {
                            noMore = true;
                        }
                        setNewsList(newsBeanList);

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
//                        if (e instanceof NetworkError) {
//                            Toast.makeText(MainActivity.this, "莫得网络啦，等下再来吧", Toast.LENGTH_SHORT).show();
//                        }
                    }
                }
        );
//        List<NewsSummary> newsSummaries = new ArrayList<NewsSummary>();
//        for (int i = 0; i < 20; ++i) {
//            NewsSummary a = new NewsSummary();
//            a.setPostid("" + i);
//            String t = "";
//            if (isOnRefresh) {
//                t = " fresh";
//            }
//            if (isOnLoadMore) {
//                t = " loadMore";
//            }
//            a.setTitle("Title" + i + t);
//            newsSummaries.add(a);
//        }
//        returnNewsListData(newsSummaries);
    }

    public void setNewsList(List<NewsBean> newsBeanList) {
        ArrayList<Model> newsList = new ArrayList<>();
        for (int i = 0; i < newsBeanList.size(); ++i) {
            int finalI = i;
            List<String> images = newsBeanList.get(finalI).getImageUrls();
            data.add(newsBeanList.get(finalI));
            newsList.add(new Model() {{
                this.name = newsBeanList.get(finalI).getTitle();
                this.nickname = newsBeanList.get(finalI).getContent();
                if (this.nickname.length() > 20) {
                    this.nickname = this.nickname.substring(0, 10) + "...";
                }
                if (images.size() > 0) this.imageUrl = images.get(0);
                this.position = data.size() - 1;
            }});
        }
        mAdapter.loadMore(newsList);
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
