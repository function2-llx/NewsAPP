package com.java.luolingxiao.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.luolingxiao.R;

public class SearchPageFragment extends Fragment {
    public SearchPageFragment() {}

    public static SearchPageFragment newInstance() {
        SearchPageFragment ret = new SearchPageFragment();
        return ret;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }
}
