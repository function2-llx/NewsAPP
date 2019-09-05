package com.java.luolingxiao.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.java.luolingxiao.api.UserApi;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.Objects;

public class FavoriteNewsListFragment extends SimpleNewsListFragment {
    public FavoriteNewsListFragment() {}

    public static FavoriteNewsListFragment newInstance(boolean local) {
        FavoriteNewsListFragment fragment = new FavoriteNewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("local", local);
        fragment.setArguments(bundle);
        return fragment;
    }

    private boolean isLocal() { return Objects.requireNonNull(getArguments()).getBoolean("local"); }
    private boolean isUser() { return !isLocal(); }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        updateNewsList(true);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    private void updateNewsList(boolean refresh) {
        setNewsList(isLocal() ? getDataRepository().getLocalFavoriteNewsSync() : UserApi.getInstance().getFavoriteSync(), refresh, false);
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
