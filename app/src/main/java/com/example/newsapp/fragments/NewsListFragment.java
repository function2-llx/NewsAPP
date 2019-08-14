package com.example.newsapp.fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.animation.ScaleInAnimation;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;

import com.example.newsapp.MainActivity;
import com.example.newsapp.R;
import com.example.newsapp.adapters.NewsListAdapter;
import com.example.newsapp.api.Api;
import com.example.newsapp.api.ApiConstants;
import com.example.newsapp.api.HostType;
import com.example.newsapp.api.NewsApi;
import com.example.newsapp.bean.AppConstant;
import com.example.newsapp.bean.NewsBean;
import com.example.newsapp.bean.NewsDateTime;
import com.example.newsapp.bean.NewsSummary;
import com.example.newsapp.adapters.NewListAdapter;

import com.jaydenxiao.common.baserx.RxSchedulers;
import com.jaydenxiao.common.commonutils.TimeUtil;
import com.jaydenxiao.common.commonwidget.LoadingTip;
import com.yanzhenjie.nohttp.error.NetworkError;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * des:新闻fragment
 * Created by xsf
 * on 2016.09.17:30
 */
public class NewsListFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
//    @Bind(R.id.irc)
    private IRecyclerView irc;
    @Bind(R.id.loadedTip)
    LoadingTip loadedTip;
    private NewsListAdapter newsListAdapter;
    private List<NewsBean> datas = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage=0;
//    Context a;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;

    public NewsListFragment() {}

    public static NewsListFragment newInstance(String channel) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("channel", channel);
        fragment.setArguments(bundle);
        return fragment;
    }

    private String getChannel() {
        assert this.getArguments() != null;
        return this.getArguments().getString("channel");
    }



    @Override
    public void onRefresh() {
        newsListAdapter.getPageBean().setRefresh(true);
        mStartPage = 0;
        //发起请求
        irc.setRefreshing(true);
        getNewsListDataRequest(mNewsType, mNewsId, mStartPage, true, false);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        newsListAdapter.getPageBean().setRefresh(false);
        //发起请求
        irc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
        getNewsListDataRequest(mNewsType, mNewsId, mStartPage, false, true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
//            mNewsId = getArguments().getString(AppConstant.NEWS_ID);
//            mNewsType = getArguments().getString(AppConstant.NEWS_TYPE);
        }
//        Context a = getContext();
//        System.out.println(getContext());

//        //数据为空才重新发起请求
//        if(newListAdapter.getSize()<=0) {
//            mStartPage = 0;
//            mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        irc = view.findViewById(R.id.news_irc);
        irc.setLayoutManager(new LinearLayoutManager(getContext()));
        datas.clear();
        newsListAdapter = new NewsListAdapter(getContext(), datas);
        newsListAdapter.openLoadAnimation(new ScaleInAnimation());
        irc.setAdapter(newsListAdapter);
        irc.setOnRefreshListener(this);
        irc.setOnLoadMoreListener(this);
        //数据为空才重新发起请求
        if(newsListAdapter.getSize()<=0) {
            mStartPage = 0;
            getNewsListDataRequest(mNewsType, mNewsId, mStartPage, false, false);
        }
        return view;
    }

    public void getNewsListDataRequest(String type, String id, int startPage, boolean isOnRefresh, boolean isOnLoadMore) {
        NewsApi.requestNews(new NewsApi.SearchParams()
                        .setSize(20)
                        .setWords("")
                        .setCategory(getChannel().equals("首页")? "" : getChannel())
                        .setStartDate(new NewsDateTime(2019, 7, 1))
                        .setEndDate(new NewsDateTime(2019, 7, 3)),
                new NewsApi.Callback() {
                    @Override
                    public void onNewsReceived(List<NewsBean> newsBeanList) {
                        returnNewsListData(newsBeanList);
//                        if (newsBeanList.isEmpty()) {
//                            Toast.makeText(MainActivity.this, "莫得新闻了，等哈再来哈", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(MainActivity.this, newsBeanList.get(0).getTitle(), Toast.LENGTH_SHORT).show();
//                        }
                    }

                    @Override
                    public void onHandleException(Exception e) {
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

    public void returnNewsListData(List<NewsBean> newsSummaries) {
        if (newsSummaries != null) {
            mStartPage += 20;
            if (newsListAdapter.getPageBean().isRefresh()) {
                irc.setRefreshing(false);
                newsListAdapter.replaceAll(newsSummaries);
            } else {
                if (newsSummaries.size() > 0) {
                    irc.setLoadMoreStatus(LoadMoreFooterView.Status.GONE);
                    newsListAdapter.addAll(newsSummaries);
                } else {
                    irc.setLoadMoreStatus(LoadMoreFooterView.Status.THE_END);
                }
            }
        }
    }

    public void scrolltoTop() {
        irc.smoothScrollToPosition(0);
    }
}
