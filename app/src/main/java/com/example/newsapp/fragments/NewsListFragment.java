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


import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.animation.ScaleInAnimation;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;

import com.example.newsapp.R;
import com.example.newsapp.adapters.NewsListAdapter;
import com.example.newsapp.bean.AppConstant;
import com.example.newsapp.bean.NewsSummary;
import com.example.newsapp.adapters.NewListAdapter;

import com.jaydenxiao.common.commonwidget.LoadingTip;


import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

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
    private List<NewsSummary> datas = new ArrayList<>();

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
//        mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        newsListAdapter.getPageBean().setRefresh(false);
        //发起请求
        irc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
//        mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
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
        irc.setOnLoadMoreListener(this);        return view;
    }

    public void returnNewsListData(List<NewsSummary> newsSummaries) {
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
}