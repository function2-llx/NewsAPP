package com.java.luolingxiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.luolingxiao.bean.NewsBean;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.List;

public abstract class DisplayNewsListFragment extends SimpleNewsListFragment {
    public DisplayNewsListFragment() {}

    protected abstract List<NewsBean> getNews();

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateNewsList(true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    private void updateNewsList(boolean refresh) {
        setNewsList(getNews(), refresh, false);
        if (refresh) refreshLayout.finishRefresh();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        refreshLayout.setEnableLoadMore(false);
        updateNewsList(false);
        return view;
    }
}
