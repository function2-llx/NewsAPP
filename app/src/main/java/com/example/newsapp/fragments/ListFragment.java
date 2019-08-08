package com.example.newsapp.fragments;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import androidx.fragment.app.Fragment

import com.aspsine.irecyclerview.IRecyclerView;
import com.aspsine.irecyclerview.OnLoadMoreListener;
import com.aspsine.irecyclerview.OnRefreshListener;
import com.aspsine.irecyclerview.widget.LoadMoreFooterView;

import com.example.newsapp.R;
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
public class ListFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {
    @Bind(R.id.irc)
    IRecyclerView irc;
    @Bind(R.id.loadedTip)
    LoadingTip loadedTip;
    private NewListAdapter newListAdapter;
    private List<NewsSummary> datas = new ArrayList<>();

    private String mNewsId;
    private String mNewsType;
    private int mStartPage=0;

    // 标志位，标志已经初始化完成。
    private boolean isPrepared;
    private boolean isVisible;

    public ListFragment() {}

    public static ListFragment newInstance(String channel) {
        ListFragment fragment = new ListFragment();
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
        newListAdapter.getPageBean().setRefresh(true);
        mStartPage = 0;
        //发起请求
        irc.setRefreshing(true);
//        mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
    }

    @Override
    public void onLoadMore(View loadMoreView) {
        newListAdapter.getPageBean().setRefresh(false);
        //发起请求
        irc.setLoadMoreStatus(LoadMoreFooterView.Status.LOADING);
//        mPresenter.getNewsListDataRequest(mNewsType, mNewsId, mStartPage);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        irc.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }

}
